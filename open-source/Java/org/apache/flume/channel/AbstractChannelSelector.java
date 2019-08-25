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
package org.apache.flume.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flume.Channel;
import org.apache.flume.ChannelSelector;
import org.apache.flume.FlumeException;

/**
 * ChannelSelector 接口抽象类, 实现最基本的功能, 具体的 ChannelSelector 实现类一般会继承该抽象类.
 */
public abstract class AbstractChannelSelector implements ChannelSelector {

  private List<Channel> channels;
  private String name;

  /**
   * 获取 Selector 可以选择的所有 Channels.
   */
  @Override
  public List<Channel> getAllChannels() {
    return channels;
  }

  /**
   * 设置 Selector 可以选择的所有 channels.
   */
  @Override
  public void setChannels(List<Channel> channels) {
    this.channels = channels;
  }

  @Override
  public synchronized void setName(String name) {
    this.name = name;
  }

  @Override
  public synchronized String getName() {
    return name;
  }

  /**
   *
   * @return A map of name to channel instance.
   * @return 名称, Channel 实例的映射 Map.
   */

  protected Map<String, Channel> getChannelNameMap() {
    Map<String, Channel> channelNameMap = new HashMap<String, Channel>();
    for (Channel ch : getAllChannels()) {
      channelNameMap.put(ch.getName(), ch);
    }
    return channelNameMap;
  }

  /**
   * Given a list of channel names as space delimited string,
   * returns list of channels.
   * 给定一个以空格分隔的字符串的 channel 名称列表, 返回 channels 列表.
   * @return List of {@linkplain Channel}s represented by the names.
   * @return 由名称表示的 {@linkplain Channel} 列表.
   */
  protected List<Channel> getChannelListFromNames(String channels,
          Map<String, Channel> channelNameMap) {
    List<Channel> configuredChannels = new ArrayList<Channel>();
    if (channels == null || channels.isEmpty()) {
      return configuredChannels;
    }
    String[] chNames = channels.split(" ");
    for (String name : chNames) {
      Channel ch = channelNameMap.get(name);
      if (ch != null) {
        configuredChannels.add(ch);
      } else {
        throw new FlumeException("Selector channel not found: "
                + name);
      }
    }
    return configuredChannels;
  }

}
