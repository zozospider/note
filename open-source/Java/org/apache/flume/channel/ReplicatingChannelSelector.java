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
import java.util.List;
import java.util.Map;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;

/**
 * Replicating channel selector. This selector allows the event to be placed
 * in all the channels that the source is configured with.
 * 复制 Channel Selector. 此 Selector 允许将 event 放置在配置 Source 的所有 channels 中.
 */
public class ReplicatingChannelSelector extends AbstractChannelSelector {

  /**
   * Configuration to set a subset of the channels as optional.
   * 配置以将 channels 的子集设置为可选.
   */
  public static final String CONFIG_OPTIONAL = "optional";
  List<Channel> requiredChannels = null;
  List<Channel> optionalChannels = new ArrayList<Channel>();

  /**
   * 获取必须 channels 的列表, 当 requiredChannels 为空时 (即没有调用 configure(c) 方法), 返回所有 channels.
   */
  @Override
  public List<Channel> getRequiredChannels(Event event) {
    /*
     * Seems like there are lot of components within flume that do not call
     * configure method. It is conceiveable that custom component tests too
     * do that. So in that case, revert to old behavior.
     * 好像 flume 中有很多组件没有调用 configure 方法.
     * 可以想象, 定制组件测试也这样做. 因此在那种情况下, 恢复为旧的行为.
     */
    if (requiredChannels == null) {
      return getAllChannels();
    }
    return requiredChannels;
  }

  @Override
  public List<Channel> getOptionalChannels(Event event) {
    return optionalChannels;
  }

  /**
   * 通过 optional channels 配置, 将可选的 channels 列表和必须的 channels 列表分别设置到 requiredChannels 和 optionalChannels 变量中.
   */
  @Override
  public void configure(Context context) {
    String optionalList = context.getString(CONFIG_OPTIONAL);
    requiredChannels = new ArrayList<Channel>(getAllChannels());
    Map<String, Channel> channelNameMap = getChannelNameMap();
    if (optionalList != null && !optionalList.isEmpty()) {
      for (String optional : optionalList.split("\\s+")) {
        Channel optionalChannel = channelNameMap.get(optional);
        requiredChannels.remove(optionalChannel);
        if (!optionalChannels.contains(optionalChannel)) {
          optionalChannels.add(optionalChannel);
        }
      }
    }
  }
}
