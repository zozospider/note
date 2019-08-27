/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.flume.source;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.flume.CounterGroup;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.Source;
import org.apache.flume.SourceRunner;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.lifecycle.LifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * An implementation of {@link SourceRunner} that can drive a
 * {@link PollableSource}.
 * </p>
 * <p>
 * {@link SourceRunner} 的实现, 可以驱动 {@link PollableSource}.
 * </p>
 * <p>
 * A {@link PollableSourceRunner} wraps a {@link PollableSource} in the required
 * run loop in order for it to operate. Internally, metrics and counters are
 * kept such that a source that returns a {@link PollableSource.Status} of
 * {@code BACKOFF} causes the run loop to do exactly that. There's a maximum
 * backoff period of 500ms. A source that returns {@code READY} is immediately
 * invoked. Note that {@code BACKOFF} is merely a hint to the runner; it need
 * not be strictly adhered to.
 * </p>
 * <p>
 * {@link PollableSourceRunner} 在所需的运行循环中包装 {@link PollableSource} 以使其运行.
 * 在内部, 保留 metrics 和 counters, 使得返回 {@code BACKOFF} 的 {@link PollableSource.Status} 的 Source 导致运行循环完全执行该操作.
 * 最大退避时间为 500 毫秒. 返回 {@code READY} 的 Source 会立即被调用.
 * 请注意, {@code BACKOFF} 仅仅是对 Runner 的提示; 它不需要严格遵守.
 * </p>
 */
public class PollableSourceRunner extends SourceRunner {

  private static final Logger logger = LoggerFactory.getLogger(PollableSourceRunner.class);

  // 控制当前 SourceRunner 启动的线程循环逻辑是否应该停止
  private AtomicBoolean shouldStop;

  private CounterGroup counterGroup;
  // 实现 Runnable 接口, 用于当前 SourceRunner 启动时通过线程执行本类的 run() 方法逻辑
  private PollingRunner runner;
  // 当前 SourceRunner 启动的线程对象
  private Thread runnerThread;
  // 生命周期状态 enum
  private LifecycleState lifecycleState;

  /**
   * 构造方法, 设置状态为: IDLE.
   */
  public PollableSourceRunner() {
    shouldStop = new AtomicBoolean();
    counterGroup = new CounterGroup();
    lifecycleState = LifecycleState.IDLE;
  }

  /**
   * 实现 LifecycleAware 接口的 start() 方法, 设置状态为: START.
   * SourceRunner start 时, 执行对应 Source 的 ChannelProcessor 的初始化逻辑, 然后启动 Source.
   * 然后再新建线程, 不断调用 Source 的 process() 方法.
   */
  @Override
  public void start() {
    // 调用 getSource() 方法获取当前 SourceRunner 处理的 PollableSource 变量
    PollableSource source = (PollableSource) getSource();
    // 获取 Source 对应的 ChannelProcessor
    ChannelProcessor cp = source.getChannelProcessor();
    // 调用 ChannelProcessor 的 initialize() 方法, 初始化 ChannelProcessor
    cp.initialize();
    // 调用 Source 的 start() 方法, 启动 Source
    source.start();

    // 新建 Runnable 对象, 设置成员变量
    runner = new PollingRunner();

    runner.source = source;
    runner.counterGroup = counterGroup;
    runner.shouldStop = shouldStop;

    // 新建 Thread 对象, 设置名称, 启动线程
    runnerThread = new Thread(runner);
    runnerThread.setName(getClass().getSimpleName() + "-" + 
        source.getClass().getSimpleName() + "-" + source.getName());
    runnerThread.start();

    lifecycleState = LifecycleState.START;
  }

  /**
   * 实现 LifecycleAware 接口的 stop() 方法, 设置状态为: STOP.
   * SourceRunner stop 时, 停止对应线程.
   * 然后再停止对应 Source, 再执行 Source 的 ChannelProcessor 的关闭逻辑.
   */
  @Override
  public void stop() {

    runner.shouldStop.set(true);

    try {
      runnerThread.interrupt();
      runnerThread.join();
    } catch (InterruptedException e) {
      logger.warn("Interrupted while waiting for polling runner to stop. Please report this.", e);
      Thread.currentThread().interrupt();
    }

    Source source = getSource();
    source.stop();
    ChannelProcessor cp = source.getChannelProcessor();
    cp.close();

    lifecycleState = LifecycleState.STOP;
  }

  @Override
  public String toString() {
    return "PollableSourceRunner: { source:" + getSource() + " counterGroup:"
        + counterGroup + " }";
  }

  /**
   * 实现 LifecycleAware 接口的 getLifecycleState() 方法.
   * 返回 SourceRunner 的当前状态.
   */
  @Override
  public LifecycleState getLifecycleState() {
    return lifecycleState;
  }

  /**
   * 实现 Runnable 接口, 用于当前 SourceRunner 启动时通过线程执行本类的 run() 方法逻辑.
   * 主要逻辑为: 在不停止的情况下, 不断循环调用对应 Source 的 process() 方法, 并根据返回内容决定是否需要 sleep 一段时间.
   */
  public static class PollingRunner implements Runnable {

    // 当前 SourceRunner 对应的 Source
    private PollableSource source;
    // 控制当前 SourceRunner 的 run() 方法循环逻辑是否应该停止
    private AtomicBoolean shouldStop;
    private CounterGroup counterGroup;

    @Override
    public void run() {
      logger.debug("Polling runner starting. Source:{}", source);

      // 在不停止的情况下, 不断循环
      while (!shouldStop.get()) {
        counterGroup.incrementAndGet("runner.polls");

        try {
          // 调用对应 Source 的 process() 方法
          // 如果返回状态为 BACKOFF, 则 sleep 一段时间
          if (source.process().equals(PollableSource.Status.BACKOFF)) {
            counterGroup.incrementAndGet("runner.backoffs");

            // sleep 的时间和 runner.backoffs.consecutive 参数相关, 并随着连续 BACKOFF 次数不断递增, 直到达到最大值 (MaxBackOffSleepInterval 参数值).
            // 在返回状态变为 READY 时, sleep 的时间会重新开始递增.
            Thread.sleep(Math.min(
                counterGroup.incrementAndGet("runner.backoffs.consecutive")
                * source.getBackOffSleepIncrement(), source.getMaxBackOffSleepInterval()));
          // 如果返回状态为 READY, 则继续下一轮循环
          } else {
            // 设置 runner.backoffs.consecutive 参数, 用于在返回 BACKOFF 状态时 sleep 的时间重新开始递增.
            counterGroup.set("runner.backoffs.consecutive", 0L);
          }
        } catch (InterruptedException e) {
          logger.info("Source runner interrupted. Exiting");
          counterGroup.incrementAndGet("runner.interruptions");
        } catch (EventDeliveryException e) {
          logger.error("Unable to deliver event. Exception follows.", e);
          counterGroup.incrementAndGet("runner.deliveryErrors");
        // 其他未知异常情况下, sleep 一段时间 (MaxBackOffSleepInterval 参数值).
        } catch (Exception e) {
          counterGroup.incrementAndGet("runner.errors");
          logger.error("Unhandled exception, logging and sleeping for " +
              source.getMaxBackOffSleepInterval() + "ms", e);
          try {
            Thread.sleep(source.getMaxBackOffSleepInterval());
          } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
          }
        }
      }

      logger.debug("Polling runner exiting. Metrics:{}", counterGroup);
    }

  }

}
