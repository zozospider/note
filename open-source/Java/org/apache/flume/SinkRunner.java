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
  private PollingRunner runner;
  private Thread runnerThread;
  private LifecycleState lifecycleState;

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

  @Override
  public void start() {
    SinkProcessor policy = getPolicy();

    policy.start();

    runner = new PollingRunner();

    runner.policy = policy;
    runner.counterGroup = counterGroup;
    runner.shouldStop = new AtomicBoolean();

    runnerThread = new Thread(runner);
    runnerThread.setName("SinkRunner-PollingRunner-" +
        policy.getClass().getSimpleName());
    runnerThread.start();

    lifecycleState = LifecycleState.START;
  }

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

  @Override
  public LifecycleState getLifecycleState() {
    return lifecycleState;
  }

  /**
   * {@link Runnable} that {@linkplain SinkProcessor#process() polls} a
   * {@link SinkProcessor} and manages event delivery notification,
   * {@link Sink.Status BACKOFF} delay handling, etc.
   * {@link Runnable}: {@linkplain SinkProcessor#process() polls} 1 个 {@link SinkProcessor} 并管理 event 传递通知, {@link Sink.Status BACKOFF} 延迟处理等.
   */
  public static class PollingRunner implements Runnable {

    private SinkProcessor policy;
    private AtomicBoolean shouldStop;
    private CounterGroup counterGroup;

    @Override
    public void run() {
      logger.debug("Polling sink runner starting");

      while (!shouldStop.get()) {
        try {
          if (policy.process().equals(Sink.Status.BACKOFF)) {
            counterGroup.incrementAndGet("runner.backoffs");

            Thread.sleep(Math.min(
                counterGroup.incrementAndGet("runner.backoffs.consecutive")
                * backoffSleepIncrement, maxBackoffSleep));
          } else {
            counterGroup.set("runner.backoffs.consecutive", 0L);
          }
        } catch (InterruptedException e) {
          logger.debug("Interrupted while processing an event. Exiting.");
          counterGroup.incrementAndGet("runner.interruptions");
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
