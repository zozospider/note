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
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.lifecycle.LifecycleAware;

/**
 * <p>
 * A source generates {@plainlink Event events} and calls methods on the
 * configured {@link ChannelProcessor} to persist those events into the
 * configured {@linkplain Channel channels}.
 * </p>
 * <p>
 * source 生成 events, 然后调用配置的 ChannelProcessor 的方法, 将这些 events 持久化到配置的 Channel.
 * </p>
 *
 * <p>
 * Sources are associated with unique {@linkplain NamedComponent names} that can
 * be used for separating configuration and working namespaces.
 * </p>
 *
 * <p>
 * No guarantees are given regarding thread safe access.
 * </p>
 * <p>
 * 不保证线程安全访问.
 * </p>
 *
 * @see org.apache.flume.Channel
 * @see org.apache.flume.Sink
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public interface Source extends LifecycleAware, NamedComponent {

  /**
   * Specifies which channel processor will handle this source's events.
   * 指定哪个 ChannelProcessor 来处理 Source 的 events.
   *
   * @param channelProcessor
   */
  public void setChannelProcessor(ChannelProcessor channelProcessor);

  /**
   * Returns the channel processor that will handle this source's events.
   * 返回将处理此 Source 的 events 的 ChannelProcessor
   */
  public ChannelProcessor getChannelProcessor();

}
