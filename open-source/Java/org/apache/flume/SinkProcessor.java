/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume;

import java.util.List;

import org.apache.flume.Sink.Status;
import org.apache.flume.conf.Configurable;
import org.apache.flume.lifecycle.LifecycleAware;

/**
 * <p>
 * Interface for a device that allows abstraction of the behavior of multiple
 * sinks, always assigned to a SinkRunner
 * </p>
 * <p>
 * 允许抽象多个 sinks 行为的设备接口, 始终分配给 SinkRunner
 * </p>
 * <p>
 * A sink processors {@link SinkProcessor#process()} method will only be
 * accessed by a single runner thread. However configuration methods
 * such as {@link Configurable#configure} may be concurrently accessed.
 * Sink Processors {@link SinkProcessor#process()} 方法只能由单个转轮线程访问.
 * 但是, 可以同时访问诸如 {@link Configurable#configure} 之类的配置方法.
 *
 * @see org.apache.flume.Sink
 * @see org.apache.flume.SinkRunner
 * @see org.apache.flume.sink.SinkGroup
 */
public interface SinkProcessor extends LifecycleAware, Configurable {
  /**
   * <p>Handle a request to poll the owned sinks.</p>
   * <p>处理轮询所拥有的 sinks 的请求.</p>
   *
   * <p>The processor is expected to call {@linkplain Sink#process()} on
   *  whatever sink(s) appropriate, handling failures as appropriate and
   *  throwing {@link EventDeliveryException} when there is a failure to
   *  deliver any events according to the delivery policy defined by the
   *  sink processor implementation. See specific implementations of this
   *  interface for delivery behavior and policies.</p>
   * <p>Processor 应该在适当的任何 sink(s) 上调用 {@linkplain Sink#process()},
   * 在适当的情况下处理失败, 并且当根据由 Sink Processor 实现类定义的 delivery 策略未能 deliver 任何 events 时, 抛出 {@link EventDeliveryException}.
   * 有关传递行为和策略, 请参阅此接口的特定实现.</p>
   *
   * @return Returns {@code READY} if events were successfully consumed,
   * or {@code BACKOFF} if no events were available in the channel to consume.
   * @return 如果 events 成功消费, 则返回 {@code READY}; 如果 Channel 中没有可用的 events 消费, 则返回 {@code BACKOFF}.
   * @throws EventDeliveryException if the behavior guaranteed by the processor
   * couldn't be carried out.
   * @throws EventDeliveryException 如果 Processor 保证的行为无法执行.
   */
  Status process() throws EventDeliveryException;

  /**
   * <p>Set all sinks to work with.</p>
   * <p>设置所有 sinks.</p>
   *
   * <p>Sink specific parameters are passed to the processor via configure</p>
   * <p>通过 configure 将 Sink 特定参数传递给 Processor</p>
   *
   * @param sinks A non-null, non-empty list of sinks to be chosen from by the
   * processor
   * @param sinks 要由 processor 选择的 non-null, no-empty 的 sinks 列表
   */
  void setSinks(List<Sink> sinks);
}