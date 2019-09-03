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

package org.apache.flume;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.flume.lifecycle.LifecycleAware;
import org.apache.flume.lifecycle.LifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A driver for {@linkplain Sink sinks} that polls them, attempting to
 * {@linkplain Sink#process() process} events if any are available in the
 * {@link Channel}.
 * </p>
 * <p>
 * {@linkplain Sink sinks} 的驱动程序, 用于轮询它们.
 * 如果 {@link Channel} 中有可用的 events, 尝试 {@linkplain Sink#process() process} events.
 * </p>
 *
 * <p>
 * Note that, unlike {@linkplain Source sources}, all sinks are polled.
 * </p>
 * <p>
 * 请注意, 与 {@linkplain Source sources} 不同, 所有 sinks 都会被轮询.
 * </p>
 *
 * @see org.apache.flume.Sink
 * @see org.apache.flume.SourceRunner
 */
public class SinkRunner implements LifecycleAware {

  private static final Logger logger = LoggerFactory
      .getLogger(SinkRunner.class);
  private static final long backoffSleepIncrement = 1000;
  private static final long maxBackoffSleep = 5000;

  private CounterGroup counterGroup;
  // 实现 Runnable 接口, 用于当前 SinkRunner 启动时通过线程执行本类的 run() 方法逻辑
  private PollingRunner runner;
  // 当前 SinkRunner 启动的线程对象
  private Thread runnerThread;
  // 生命周期状态 enum
  private LifecycleState lifecycleState;

  // 当前 SinkRunner 对应的 SinkProcessor 对象 (DefaultSinkProcessor / FailoverSinkProcessor / LoadBalancingSinkProcessor)
  private SinkProcessor policy;

  public SinkRunner() {
    counterGroup = new CounterGroup();
    lifecycleState = LifecycleState.IDLE;
  }

  public SinkRunner(SinkProcessor policy) {
    this();
    setSink(policy);
  }

  public SinkProcessor getPolicy() {
    return policy;
  }

  public void setSink(SinkProcessor policy) {
    this.policy = policy;
  }

  /**
   * 实现 LifecycleAware 接口的 start() 方法, 设置状态为: START.
   * SinkRunner start 时, 执行对应 SinkProcessor 的启动逻辑.
   * 然后再新建线程, 不断调用 SinkProcessor 的 process() 方法.
   */
  @Override
  public void start() {
    // 获取当前 SinkRunner 对应的 SinkProcessor 对象
    SinkProcessor policy = getPolicy();

    // 调用 SinkProcessor 的 start() 方法
    policy.start();

    // 新建 Runnable 对象, 设置成员变量
    runner = new PollingRunner();

    runner.policy = policy;
    runner.counterGroup = counterGroup;
    runner.shouldStop = new AtomicBoolean();

    // 新建 Thread 对象, 设置名称, 启动线程
    runnerThread = new Thread(runner);
    runnerThread.setName("SinkRunner-PollingRunner-" +
        policy.getClass().getSimpleName());
    runnerThread.start();

    lifecycleState = LifecycleState.START;
  }

  /**
   * 实现 LifecycleAware 接口的 stop() 方法, 设置状态为: STOP.
   * SinkRunner stop 时, 停止对应线程.
   * 然后再停止对应 SinkProcessor.
   */
  @Override
  public void stop() {

    if (runnerThread != null) {
      runner.shouldStop.set(true);
      runnerThread.interrupt();

      while (runnerThread.isAlive()) {
        try {
          logger.debug("Waiting for runner thread to exit");
          runnerThread.join(500);
        } catch (InterruptedException e) {
          logger.debug("Interrupted while waiting for runner thread to exit. Exception follows.",
                       e);
        }
      }
    }

    getPolicy().stop();
    lifecycleState = LifecycleState.STOP;
  }

  @Override
  public String toString() {
    return "SinkRunner: { policy:" + getPolicy() + " counterGroup:"
        + counterGroup + " }";
  }

  /**
   * 实现 LifecycleAware 接口的 getLifecycleState() 方法.
   * 返回 SinkRunner 的当前状态.
   */
  @Override
  public LifecycleState getLifecycleState() {
    return lifecycleState;
  }

  /**
   * {@link Runnable} that {@linkplain SinkProcessor#process() polls} a
   * {@link SinkProcessor} and manages event delivery notification,
   * {@link Sink.Status BACKOFF} delay handling, etc.
   * {@link Runnable}: {@linkplain SinkProcessor#process() polls} 1 个 {@link SinkProcessor} 并管理 event 传递通知, {@link Sink.Status BACKOFF} 延迟处理等.
   *
   * 实现 Runnable 接口, 用于当前 SinkRunner 启动时通过线程执行本类的 run() 方法逻辑.
   * 主要逻辑为: 在不停止的情况下, 不断循环调用对应 SinkProcessor 的 process() 方法, 并根据返回内容决定是否需要 sleep 一段时间.
   */
  public static class PollingRunner implements Runnable {

    // 当前 SinkRunner 对应的 SinkProcessor
    private SinkProcessor policy;
    // 控制当前 SinkRunner 的 run() 方法循环逻辑是否应该停止
    private AtomicBoolean shouldStop;
    private CounterGroup counterGroup;

    @Override
    public void run() {
      logger.debug("Polling sink runner starting");

      // 在不停止的情况下, 不断循环
      while (!shouldStop.get()) {
        try {
          // 调用对应 SinkProcessor 的 process() 方法
          // 如果返回状态为 BACKOFF, 则 sleep 一段时间
          if (policy.process().equals(Sink.Status.BACKOFF)) {
            counterGroup.incrementAndGet("runner.backoffs");

            // sleep 的时间和 runner.backoffs.consecutive 参数相关, 并随着连续 BACKOFF 次数不断递增, 直到达到最大值 (maxBackoffSleep 参数值).
            // 在返回状态变为 READY 时, sleep 的时间会重新开始递增.
            Thread.sleep(Math.min(
                counterGroup.incrementAndGet("runner.backoffs.consecutive")
                * backoffSleepIncrement, maxBackoffSleep));
          // 如果返回状态为 READY, 则继续下一轮循环
          } else {
            // 设置 runner.backoffs.consecutive 参数, 用于在返回 BACKOFF 状态时 sleep 的时间重新开始递增.
            counterGroup.set("runner.backoffs.consecutive", 0L);
          }
        } catch (InterruptedException e) {
          logger.debug("Interrupted while processing an event. Exiting.");
          counterGroup.incrementAndGet("runner.interruptions");
        // 其他未知异常情况下, sleep 一段时间 (maxBackoffSleep 参数值).
        } catch (Exception e) {
          logger.error("Unable to deliver event. Exception follows.", e);
          if (e instanceof EventDeliveryException) {
            counterGroup.incrementAndGet("runner.deliveryErrors");
          } else {
            counterGroup.incrementAndGet("runner.errors");
          }
          try {
            Thread.sleep(maxBackoffSleep);
          } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
          }
        }
      }
      logger.debug("Polling runner exiting. Metrics:{}", counterGroup);
    }

  }
}
