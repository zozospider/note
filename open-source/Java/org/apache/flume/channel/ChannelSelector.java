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

import java.util.List;

import org.apache.flume.conf.Configurable;

/**
 * <p>
 * Allows the selection of a subset of channels from the given set based on
 * its implementation policy. Different implementations of this interface
 * embody different policies that affect the choice of channels that a source
 * will push the incoming events to.
 * </p>
 * <p>
 * 允许根据其实现策略从给定集合中选择一个 channels 子集.
 * 此接口的不同实现包含不同的策略, 这些策略会影响 Source 将推送传入 events 的 channels 选择.
 * </p>
 */
public interface ChannelSelector extends NamedComponent, Configurable {

  /**
   * @param channels all channels the selector could select from.
   * @param channels Selector 可以选择的所有 channels.
   */
  public void setChannels(List<Channel> channels);

  /**
   * Returns a list of required channels. A failure in writing the event to
   * these channels must be communicated back to the source that received this
   * event.
   * 返回必须 channels 的列表. 将 event 写入这些 channels 的失败必须传回给收到此 event 的 Source.
   * @param event
   * @return the list of required channels that this selector has selected for
   * the given event.
   * @return 此 Selector 为给定 event 选择的必须 channels 的列表.
   */
  public List<Channel> getRequiredChannels(Event event);


  /**
   * Returns a list of optional channels. A failure in writing the event to
   * these channels must be ignored.
   * 返回可选 channels 的列表. 必须忽略将 event 写入这些 channels 的失败.
   * @param event
   * @return the list of optional channels that this selector has selected for
   * the given event.
   * 此 Selector 为给定 event 选择的可选 channels 的列表.
   */
  public List<Channel> getOptionalChannels(Event event);

  /**
   * @return the list of all channels that this selector is configured to work
   * with.
   * @return 此 Selector 配置为使用的所有 channels 的列表.
   */
  public List<Channel> getAllChannels();

}
