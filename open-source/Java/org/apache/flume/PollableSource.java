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

import org.apache.flume.source.EventDrivenSourceRunner;

/**
 * A {@link Source} that requires an external driver to poll to determine
 * whether there are {@linkplain Event events} that are available to ingest
 * from the source.
 * 1 个{@link Source}: 需要外部驱动程序进行轮询, 以确定是否有可从 Source 获取的 {@linkplain Event events}.
 *
 * @see org.apache.flume.source.EventDrivenSourceRunner
 */
public interface PollableSource extends Source {
  /**
   * <p>
   * Attempt to pull an item from the source, sending it to the channel.
   * </p>
   * <p>
   * 尝试从 Source 中提取 item, 将其发送到 Channel.
   * </p>
   * <p>
   * When driven by an {@link EventDrivenSourceRunner} process is guaranteed
   * to be called only by a single thread at a time, with no concurrency.
   * Any other mechanism driving a pollable source must follow the same
   * semantics.
   * </p>
   * <p>
   * 当由 {@link EventDrivenSourceRunner} 进程驱动时, 保证 1 次只能由 1 个单线程调用, 没有并发.
   * 驱动可轮询 Source 的任何其他机制必须遵循相同的语义.
   * </p>
   * @return {@code READY} if one or more events were created from the source.
   * {@code BACKOFF} if no events could be created from the source.
   * @return {@code READY} 如果从 Source 创建了一个或多个 events.
   * {@code BACKOFF} 如果没有从 Source 创建 event.
   * @throws EventDeliveryException If there was a failure in delivering to
   * the attached channel, or if a failure occurred in acquiring data from
   * the source.
   * @throws EventDeliveryException 如果传递到附加 channel 失败, 或者从 Source 获取数据时发生故障.
   */
  public Status process() throws EventDeliveryException;

  public long getBackOffSleepIncrement();

  public long getMaxBackOffSleepInterval();

  public static enum Status {
    READY, BACKOFF
  }

}
