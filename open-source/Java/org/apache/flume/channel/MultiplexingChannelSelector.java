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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 多路复用 Channel Selector.
 */
public class MultiplexingChannelSelector extends AbstractChannelSelector {

  public static final String CONFIG_MULTIPLEX_HEADER_NAME = "header";
  public static final String DEFAULT_MULTIPLEX_HEADER =
      "flume.selector.header";
  public static final String CONFIG_PREFIX_MAPPING = "mapping.";
  public static final String CONFIG_DEFAULT_CHANNEL = "default";
  public static final String CONFIG_PREFIX_OPTIONAL = "optional";

  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(MultiplexingChannelSelector.class);

  private static final List<Channel> EMPTY_LIST =
      Collections.emptyList();

  // 配置的 header 值
  private String headerName;

  // 配置的 header - required channels 映射 map
  private Map<String, List<Channel>> channelMapping;
  // 配置的 header - optional channels 映射 map
  private Map<String, List<Channel>> optionalChannels;
  // 配置的 default channels
  private List<Channel> defaultChannels;

  /**
   * 获取 required channels 的列表
   */
  @Override
  public List<Channel> getRequiredChannels(Event event) {
    // 获取当前 event 配置的 header 值对应的 value
    String headerValue = event.getHeaders().get(headerName);
    // 如果不存在该 header, 则使用 default channels
    if (headerValue == null || headerValue.trim().length() == 0) {
      return defaultChannels;
    }

    // 获取 header 对应的 required channels
    List<Channel> channels = channelMapping.get(headerValue);

    //This header value does not point to anything
    //Return default channel(s) here.
    // 此 header 值不指向任何, 返回 default channels.
    if (channels == null) {
      channels = defaultChannels;
    }

    return channels;
  }

  /**
   * 获取 optional Channels 的列表
   */
  @Override
  public List<Channel> getOptionalChannels(Event event) {
    // 获取当前 event 配置的 header 值对应的 value
    String hdr = event.getHeaders().get(headerName);
    // 获取 header 对应的 optional Channels
    List<Channel> channels = optionalChannels.get(hdr);

    // 此 header 值不指向任何, 返回空列表.
    if (channels == null) {
      channels = EMPTY_LIST;
    }
    return channels;
  }

  /**
   * # channel selector configuration
   * agent_foo.sources.avro-AppSrv-source1.selector.type = multiplexing
   * agent_foo.sources.avro-AppSrv-source1.selector.header = State
   * agent_foo.sources.avro-AppSrv-source1.selector.mapping.CA = mem-channel-1
   * agent_foo.sources.avro-AppSrv-source1.selector.mapping.AZ = file-channel-2
   * agent_foo.sources.avro-AppSrv-source1.selector.mapping.NY = mem-channel-1 file-channel-2
   * agent_foo.sources.avro-AppSrv-source1.selector.optional.CA = mem-channel-1 file-channel-2
   * agent_foo.sources.avro-AppSrv-source1.selector.mapping.AZ = file-channel-2
   * agent_foo.sources.avro-AppSrv-source1.selector.default = mem-channel-1
   *
   * The selector will attempt to write to the required channels first and will fail the transaction if even one of these channels fails to consume the events. The transaction is reattempted on all of the channels.
   * Once all required channels have consumed the events, then the selector will attempt to write to the optional channels. A failure by any of the optional channels to consume the event is simply ignored and not retried.
   * selector 将首先尝试写入 required channels, 如果其中 1 个 channel 无法消费 events, 则会使 transaction 失败. 在所有 channels 上重新尝试 transaction.
   * 一旦所有 required channels 消耗了 events, 则 selector 将尝试写入 optional channels. 任何 optional channels 消费该 event 的失败都会被忽略而不会重试.
   *
   * If there is an overlap between the optional channels and required channels for a specific header, the channel is considered to be required, and a failure in the channel will cause the entire set of required channels to be retried.
   * For instance, in the above example, for the header “CA” mem-channel-1 is considered to be a required channel even though it is marked both as required and optional, and a failure to write to this channel will cause that event to be retried on all channels configured for the selector.
   * 如果 optional channels 与特定 header 的 required channels 之间存在重叠, 则认为该 channel 是 required, 并且 1 个 channel 中的失败将导致重试所有 required channels.
   * 例如, 在上面的示例中, 对于 header “CA” mem-channel-1 被认为是 required channel, 即使它被同时标记为 required and optional, 并且写入此 channel 的失败将导致该 event 在为 selector 配置的所有 channels 上重试.
   *
   * Note that if a header does not have any required channels, then the event will be written to the default channels and will be attempted to be written to the optional channels for that header.
   * Specifying optional channels will still cause the event to be written to the default channels, if no required channels are specified.
   * If no channels are designated as default and there are no required, the selector will attempt to write the events to the optional channels. Any failures are simply ignored in that case.
   * 请注意, 如果 1 个 header 没有任何 required channels, 则该 event 将被写入 default channels, 并将尝试写入该 header 的 optional channels.
   * 如果未指定 required channels, 则指定 optional channels 仍会将 event 写入 default channels.
   * 如果没有将 channels 指定为默认 default channels 且没有 required channels, 则 selector 将尝试将 events 写入 optional channels. 在这种情况下, 任何失败都会被忽略.
   */
  @Override
  public void configure(Context context) {
    // 获取配置的 header 值, 如 (state):
    // a1.sources = r1
    // a1.channels = c1 c2 c3 c4
    // a1.sources.r1.selector.type = multiplexing
    // a1.sources.r1.selector.header = state
    // a1.sources.r1.selector.mapping.CZ = c1
    // a1.sources.r1.selector.mapping.US = c2 c3
    // a1.sources.r1.selector.default = c4
    this.headerName = context.getString(CONFIG_MULTIPLEX_HEADER_NAME,
        DEFAULT_MULTIPLEX_HEADER);

    // 获取所有 名称, Channel 实例的映射 Map.
    Map<String, Channel> channelNameMap = getChannelNameMap();

    // 获取所有配置的以 `default.` 为前缀的 headernames key - values (对应的 Channel) 对, 如:
    // a1.sources.r1.selector.default = c4
    defaultChannels = getChannelListFromNames(
        context.getString(CONFIG_DEFAULT_CHANNEL), channelNameMap);

    // 获取所有配置的以 `mapping.` 为前缀的 headernames key - values 对, 如:
    // a1.sources.r1.selector.mapping.CZ = c1
    // a1.sources.r1.selector.mapping.US = c2 c3
    Map<String, String> mapConfig =
        context.getSubProperties(CONFIG_PREFIX_MAPPING);

    channelMapping = new HashMap<String, List<Channel>>();

    // 遍历配置的 headernames key - values 对
    for (String headerValue : mapConfig.keySet()) {
      // 获取每个 headernames 对应的 channels
      List<Channel> configuredChannels = getChannelListFromNames(
          mapConfig.get(headerValue),
          channelNameMap);

      //This should not go to default channel(s)
      //because this seems to be a bad way to configure.
      // 这不应该转到默认 channel(s), 因为这似乎是一种不好的配置方式.
      if (configuredChannels.size() == 0) {
        // No channel configured for when header value is: h1
        throw new FlumeException("No channel configured for when "
            + "header value is: " + headerValue);
      }

      if (channelMapping.put(headerValue, configuredChannels) != null) {
        throw new FlumeException("Selector channel configured twice");
      }
    }
    //If no mapping is configured, it is ok.
    //All events will go to the default channel(s).
    // 如果没有配置 mapping, 也可以. 所有 events 都将转到 default channel(s).
    // 获取所有配置的以 `optional.` 为前缀的 headernames key - values 对, 如:
    // agent_foo.sources.avro-AppSrv-source1.selector.optional.CA = mem-channel-1 file-channel-2
    Map<String, String> optionalChannelsMapping =
        context.getSubProperties(CONFIG_PREFIX_OPTIONAL + ".");

    optionalChannels = new HashMap<String, List<Channel>>();
    for (String hdr : optionalChannelsMapping.keySet()) {
      // 获取每个 headernames 对应的 channels
      List<Channel> confChannels = getChannelListFromNames(
              optionalChannelsMapping.get(hdr), channelNameMap);
      if (confChannels.isEmpty()) {
        confChannels = EMPTY_LIST;
      }
      //Remove channels from optional channels, which are already
      //configured to be required channels.
      // 从 optional channels 中删除 channels, 这些 channels 已配置为 required channels.

      List<Channel> reqdChannels = channelMapping.get(hdr);
      //Check if there are required channels, else defaults to default channels
      // 检查是否有 required channels, 否则默认为 default channels
      if (reqdChannels == null || reqdChannels.isEmpty()) {
        reqdChannels = defaultChannels;
      }
      // 将 required channels 从 optional channels 中删除
      for (Channel c : reqdChannels) {
        if (confChannels.contains(c)) {
          confChannels.remove(c);
        }
      }

      if (optionalChannels.put(hdr, confChannels) != null) {
        throw new FlumeException("Selector channel configured twice");
      }
    }

  }

}
