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

import org.apache.flume.annotations.InterfaceAudience;
import org.apache.flume.annotations.InterfaceStability;
import org.apache.flume.lifecycle.LifecycleAware;

/**
 * <p>
 * A sink is connected to a <tt>Channel</tt> and consumes its contents,
 * sending them to a configured destination that may vary according to
 * the sink type.
 * </p>
 * <p>
 * Sink 连接到 Channel 并消费它的内容, 发送到一个配置了类型的目的地.
 * </p>
 * <p>
 * Sinks can be grouped together for various behaviors using <tt>SinkGroup</tt>
 * and <tt>SinkProcessor</tt>. They are polled periodically by a
 * <tt>SinkRunner</tt> via the processor</p>
 * <p>
 * Sink 可以被 SinkGroup 和 SinkProcessor 组合在一起以实现各种行为. 他们由 processor 通过 SinkRunner 定期轮询.
 * </p>
 *<p>
 * Sinks are associated with unique names that can be used for separating
 * configuration and working namespaces.
 * </p>
 * <p>
 * While the {@link Sink#process()} call is guaranteed to only be accessed
 * by a single thread, other calls may be concurrently accessed and should
 * thus be protected.
 * </p>
 *
 * @see org.apache.flume.Channel
 * @see org.apache.flume.SinkProcessor
 * @see org.apache.flume.SinkRunner
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public interface Sink extends LifecycleAware, NamedComponent {
  /**
   * <p>Sets the channel the sink will consume from</p>
   * <p>设置 Sink 将使用的 Channel<p>
   * @param channel The channel to be polled
   * @param channel 要轮询的 Channel
   */
  public void setChannel(Channel channel);

  /**
   * @return the channel associated with this sink
   * @return 与此 Sink 关联的 Channel
   */
  public Channel getChannel();

  /**
   * <p>Requests the sink to attempt to consume data from attached channel</p>
   * <p>请求 Sink 尝试消费所属 Channel 中的数据</p>
   * <p><strong>Note</strong>: This method should be consuming from the channel
   * within the bounds of a Transaction. On successful delivery, the transaction
   * should be committed, and on failure it should be rolled back.
   * <p><string>注意</strong>: 此方法应使用 Transaction 范围内的 Channel. 在成功传送时, Transaction 应该被提交, 并且在失败时应该被回滚.
   * @return READY if 1 or more Events were successfully delivered, BACKOFF if
   * no data could be retrieved from the channel feeding this sink
   * @return 如果成功传送了 1 个或多个 Events, 则 READY, 如果无法从提供此 Sink 的 Channel 中检索到数据, 则 BACKOFF
   * @throws EventDeliveryException In case of any kind of failure to
   * deliver data to the next hop destination.
   * @throws EventDeliveryException 在任何类型的无法将数据传递到下一个跃点目的地的情况下.
   */
  public Status process() throws EventDeliveryException;

  public static enum Status {
    READY, BACKOFF
  }
}
