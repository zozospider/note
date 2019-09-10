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
package org.apache.flume.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.flume.Channel;
import org.apache.flume.ChannelFactory;
import org.apache.flume.ChannelSelector;
import org.apache.flume.Context;
import org.apache.flume.FlumeException;
import org.apache.flume.Sink;
import org.apache.flume.SinkFactory;
import org.apache.flume.SinkProcessor;
import org.apache.flume.SinkRunner;
import org.apache.flume.Source;
import org.apache.flume.SourceFactory;
import org.apache.flume.SourceRunner;
import org.apache.flume.annotations.Disposable;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.channel.ChannelSelectorFactory;
import org.apache.flume.channel.DefaultChannelFactory;
import org.apache.flume.conf.BasicConfigurationConstants;
import org.apache.flume.conf.BatchSizeSupported;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.conf.Configurables;
import org.apache.flume.conf.FlumeConfiguration;
import org.apache.flume.conf.FlumeConfiguration.AgentConfiguration;
import org.apache.flume.conf.TransactionCapacitySupported;
import org.apache.flume.conf.channel.ChannelSelectorConfiguration;
import org.apache.flume.conf.sink.SinkConfiguration;
import org.apache.flume.conf.sink.SinkGroupConfiguration;
import org.apache.flume.conf.source.SourceConfiguration;
import org.apache.flume.sink.DefaultSinkFactory;
import org.apache.flume.sink.DefaultSinkProcessor;
import org.apache.flume.sink.SinkGroup;
import org.apache.flume.source.DefaultSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class AbstractConfigurationProvider implements ConfigurationProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfigurationProvider.class);

  private final String agentName;
  private final SourceFactory sourceFactory;
  private final SinkFactory sinkFactory;
  private final ChannelFactory channelFactory;

  private final Map<Class<? extends Channel>, Map<String, Channel>> channelCache;

  public AbstractConfigurationProvider(String agentName) {
    super();
    this.agentName = agentName;
    this.sourceFactory = new DefaultSourceFactory();
    this.sinkFactory = new DefaultSinkFactory();
    this.channelFactory = new DefaultChannelFactory();

    channelCache = new HashMap<Class<? extends Channel>, Map<String, Channel>>();
  }

  protected abstract FlumeConfiguration getFlumeConfiguration();

  /**
   * 实现 ConfigurationProvider 接口的 getConfiguration() 方法. 获取配置对象 MaterializedConfiguration (即 Flume 配置文件的具体化).
   * 主要逻辑为:
   * a. 获取配置文件对应的 FlumeConfiguration 对象和当前 agent 对应的 AgentConfiguration 对象
   * b. 构建所有配置的 channels, sources (ChannelSelector, ChannelProcessor, SourceRunner), sinks (SinkGroup, SinkProcessor, SinkRunner) 对象, 并调用它们的 configure(c) 方法.
   * c. 将所有 channels, sourceRunners, sinkRunners 设置到 MaterializedConfiguration 并返回.
   */
  public MaterializedConfiguration getConfiguration() {
    // 新建此方法需要返回的 MaterializedConfiguration 接口实现对象
    MaterializedConfiguration conf = new SimpleMaterializedConfiguration();
    // 调用子类 (PropertiesFileConfigurationProvider / MemoryConfigurationProvider / ...) 的 getFlumeConfiguration() 方法, 获取 FlumeConfiguration
    FlumeConfiguration fconfig = getFlumeConfiguration();
    // 从 FlumeConfiguration 对象中获取当前 agent 名称对应的 AgentConfiguration 对象 (即当前启动的 agent 的配置信息)
    AgentConfiguration agentConf = fconfig.getConfigurationFor(getAgentName());
    if (agentConf != null) {
      Map<String, ChannelComponent> channelComponentMap = Maps.newHashMap();
      Map<String, SourceRunner> sourceRunnerMap = Maps.newHashMap();
      Map<String, SinkRunner> sinkRunnerMap = Maps.newHashMap();
      try {
        // 通过 agentConf 参数构建所有 channels 对象, 调用他们的 configure(c) 方法, 并将 ChannelComponent 加入到 channelComponentMap 参数等.
        loadChannels(agentConf, channelComponentMap);
        // 通过 agentConf 参数构建所有 sources 对象, 获取 / 创建对应的 ChannelSelector, ChannelProcessor, SourceRunner 对象并设置它们之间的关系, 并将 SourceRunner 加入到 channelComponentMap 参数等.
        loadSources(agentConf, channelComponentMap, sourceRunnerMap);
        // 通过 agentConf 参数构建所有 sinks 对象, 获取并设置对应的 Channel. 然后获取 / 创建对应的 SinkGroup (可选), SinkProcessor, SinkRunner 对象并设置它们之间的关系, 并将 SinkRunner 加入到 sinkRunnerMap 参数等.
        loadSinks(agentConf, channelComponentMap, sinkRunnerMap);
        // 遍历所有 channel 名称, 将所有 Channel 加入到需要返回的 MaterializedConfiguration 对象中
        Set<String> channelNames = new HashSet<String>(channelComponentMap.keySet());
        for (String channelName : channelNames) {
          ChannelComponent channelComponent = channelComponentMap.get(channelName);
          // 如果当前 Channel 关联的 sources, sinks 名称集合为空, 则打印 WARN 日志并从 channelComponentMap 和 channelCache 中移除
          if (channelComponent.components.isEmpty()) {
            // Channel c1 has no components connected and has been removed.
            LOGGER.warn(String.format("Channel %s has no components connected" +
                " and has been removed.", channelName));
            channelComponentMap.remove(channelName);
            Map<String, Channel> nameChannelMap =
                channelCache.get(channelComponent.channel.getClass());
            if (nameChannelMap != null) {
              nameChannelMap.remove(channelName);
            }
          // 将当前 Channel 加入到需要返回的 MaterializedConfiguration 对象中
          } else {
            // Channel c1 connected to r1
            // Channel c1 connected to s1
            LOGGER.info(String.format("Channel %s connected to %s",
                channelName, channelComponent.components.toString()));
            conf.addChannel(channelName, channelComponent.channel);
          }
        }
        // 遍历 sourceRunnerMap, 将所有 SourceRunner 加入到需要返回的 MaterializedConfiguration 对象中
        for (Map.Entry<String, SourceRunner> entry : sourceRunnerMap.entrySet()) {
          conf.addSourceRunner(entry.getKey(), entry.getValue());
        }
        // 遍历 sourceRunnerMap, 将所有 SinkRunner 加入到需要返回的 MaterializedConfiguration 对象中
        for (Map.Entry<String, SinkRunner> entry : sinkRunnerMap.entrySet()) {
          conf.addSinkRunner(entry.getKey(), entry.getValue());
        }
      } catch (InstantiationException ex) {
        LOGGER.error("Failed to instantiate component", ex);
      } finally {
        channelComponentMap.clear();
        sourceRunnerMap.clear();
        sinkRunnerMap.clear();
      }
    } else {
      LOGGER.warn("No configuration found for this host:{}", getAgentName());
    }
    return conf;
  }

  public String getAgentName() {
    return agentName;
  }

  /**
   * 通过 agentConf 参数构建所有 channels 对象, 调用他们的 configure(c) 方法, 并将 ChannelComponent 加入到 channelComponentMap 参数.
   */
  private void loadChannels(AgentConfiguration agentConf,
      Map<String, ChannelComponent> channelComponentMap)
          throws InstantiationException {
    LOGGER.info("Creating channels");

    /*
     * Some channels will be reused across re-configurations. To handle this,
     * we store all the names of current channels, perform the reconfiguration,
     * and then if a channel was not used, we delete our reference to it.
     * This supports the scenario where you enable channel "ch0" then remove it
     * and add it back. Without this, channels like memory channel would cause
     * the first instances data to show up in the seconds.
     * 某些 channels 将在重新配置中重复使用. 为了处理这个问题, 我们存储当前 channels 的所有名称, 执行重新配置, 然后如果没有使用 channel, 我们删除对它的引用.
     * 这支持启用 channel "ch0" 然后将其删除并添加回来的情况. 如果没有这个, 像 memory channel 这样的 channels 会导致第一个实例数据在几秒钟内显示出来.
     */
    ListMultimap<Class<? extends Channel>, String> channelsNotReused =
        ArrayListMultimap.create();
    // assume all channels will not be re-used
    // 假设所有 channels 都不会被重复使用
    for (Map.Entry<Class<? extends Channel>, Map<String, Channel>> entry :
         channelCache.entrySet()) {
      Class<? extends Channel> channelKlass = entry.getKey();
      Set<String> channelNames = entry.getValue().keySet();
      channelsNotReused.get(channelKlass).addAll(channelNames);
    }

    // 获取所有 channels 名称集合
    Set<String> channelNames = agentConf.getChannelSet();
    /**
     * a. 获取 channels 中继承了 ComponentConfiguration 类的 map
     * b. 遍历所有 channels 名称
     * c. 通过当前 channel 名称对应的 ComponentConfiguration 子类构造 1 个 Channel 对象
     * d. 调用当前 Channel 的 configure(c) 方法
     * e. 加入到传入参数 channelComponentMap 中
     */
    // a
    Map<String, ComponentConfiguration> compMap = agentConf.getChannelConfigMap();
    /*
     * Components which have a ComponentConfiguration object
     * 具有 ComponentConfiguration 对象的 Components
     */
    // b
    for (String chName : channelNames) {
      // c
      ComponentConfiguration comp = compMap.get(chName);
      if (comp != null) {
        Channel channel = getOrCreateChannel(channelsNotReused,
            comp.getComponentName(), comp.getType());
        try {
          // d
          Configurables.configure(channel, comp);
          // e
          channelComponentMap.put(comp.getComponentName(),
              new ChannelComponent(channel));
          LOGGER.info("Created channel " + chName);
        } catch (Exception e) {
          String msg = String.format("Channel %s has been removed due to an " +
              "error during configuration", chName);
          LOGGER.error(msg, e);
        }
      }
    }
    /**
     * a. 遍历所有 channels 名称
     * b. 通过当前 channel 名称对应的上下文对象构造 1 个 Channel 对象
     * c. 调用当前 Channel 的 configure(c) 方法
     * d. 加入到传入参数 channelComponentMap 中
     */
    /*
     * Components which DO NOT have a ComponentConfiguration object
     * and use only Context
     * 没有 ComponentConfiguration 对象且仅使用 Context 的组件
     */
    // a
    for (String chName : channelNames) {
      // b
      Context context = agentConf.getChannelContext().get(chName);
      if (context != null) {
        Channel channel = getOrCreateChannel(channelsNotReused, chName,
            context.getString(BasicConfigurationConstants.CONFIG_TYPE));
        try {
          // c
          Configurables.configure(channel, context);
          // d
          channelComponentMap.put(chName, new ChannelComponent(channel));
          LOGGER.info("Created channel " + chName);
        } catch (Exception e) {
          String msg = String.format("Channel %s has been removed due to an " +
              "error during configuration", chName);
          LOGGER.error(msg, e);
        }
      }
    }
    /*
     * Any channel which was not re-used, will have it's reference removed
     * 任何未重复使用的 channel 都会删除它的引用
     */
    for (Class<? extends Channel> channelKlass : channelsNotReused.keySet()) {
      Map<String, Channel> channelMap = channelCache.get(channelKlass);
      if (channelMap != null) {
        for (String channelName : channelsNotReused.get(channelKlass)) {
          if (channelMap.remove(channelName) != null) {
            LOGGER.info("Removed {} of type {}", channelName, channelKlass);
          }
        }
        if (channelMap.isEmpty()) {
          channelCache.remove(channelKlass);
        }
      }
    }
  }

  /**
   * 直接构建 1 个新的或从内存缓存中返回 1 个已有的 Channel 对象
   */
  private Channel getOrCreateChannel(
      ListMultimap<Class<? extends Channel>, String> channelsNotReused,
      String name, String type)
      throws FlumeException {

    // 通过 channel type 获取对应的 channel class
    Class<? extends Channel> channelClass = channelFactory.getClass(type);
    /*
     * Channel has requested a new instance on each re-configuration
     * Channel 已在每次重新配置时请求新实例
     * 如果当前 channel class 标记为一次性使用的, 则不用到内存缓存, 直接返回 1 个新的 Channel 实例
     */
    if (channelClass.isAnnotationPresent(Disposable.class)) {
      Channel channel = channelFactory.create(name, type);
      channel.setName(name);
      return channel;
    }
    // 其他情况下, 先判断内存缓存是否存在, 不存在则新建 1 个 Channel 并加入内存缓存
    Map<String, Channel> channelMap = channelCache.get(channelClass);
    if (channelMap == null) {
      channelMap = new HashMap<String, Channel>();
      channelCache.put(channelClass, channelMap);
    }
    Channel channel = channelMap.get(name);
    if (channel == null) {
      channel = channelFactory.create(name, type);
      channel.setName(name);
      channelMap.put(name, channel);
    }
    channelsNotReused.get(channelClass).remove(name);
    return channel;
  }

  /**
   * 通过 agentConf 参数构建所有 sources 对象, 获取 / 创建对应的 ChannelSelector, ChannelProcessor, SourceRunner 对象并设置它们之间的关系, 并将 SourceRunner 加入到 channelComponentMap 参数等.
   */
  private void loadSources(AgentConfiguration agentConf,
      Map<String, ChannelComponent> channelComponentMap,
      Map<String, SourceRunner> sourceRunnerMap)
      throws InstantiationException {

    // 获取所有 sources 名称集合
    Set<String> sourceNames = agentConf.getSourceSet();
    /**
     * a. 获取 sources 中继承了 ComponentConfiguration 类的 map
     * b. 遍历所有 sources 名称
     * c. 通过当前 Source 名称对应的 ComponentConfiguration 子类构造 1 个 Source 对象
     * d. 调用当前 Source 的 configure(c) 方法
     * e. 获取当前 Source 对应的所有 channels (不能为空)
     * f. 获取当前 Source 对应的 ChannelSelectorConfiguration
     * g. 通过当前 Source 对应的所有 channels 和 ChannelSelectorConfiguration 构造 1 个 ChannelSelector 对象 (默认 selector.type = replicating)
     * h. 通过 ChannelSelector 构造 1 个 ChannelProcessor 对象
     * i. 调用当前 ChannelProcessor 的 configure(c) 方法
     * j. 将当前 ChannelProcessor 设置到 Source 中
     * k. 通过当前 Source 构造 1 个 SourceRunner 对象, 并加入到传入参数 sourceRunnerMap 中
     * l. 遍历当前 Source 对应的所有 channels, 将每个 Channel 对应的 channelComponentMap 参数中的 ChannelComponent 对象的 components 属性添加当前 Source 名称
     */
    // a
    Map<String, ComponentConfiguration> compMap =
        agentConf.getSourceConfigMap();
    /*
     * Components which have a ComponentConfiguration object
     * 具有 ComponentConfiguration 对象的 Components
     */
    // b
    for (String sourceName : sourceNames) {
      // c
      ComponentConfiguration comp = compMap.get(sourceName);
      if (comp != null) {
        SourceConfiguration config = (SourceConfiguration) comp;

        Source source = sourceFactory.create(comp.getComponentName(),
            comp.getType());
        try {
          // d
          Configurables.configure(source, config);
          // e
          Set<String> channelNames = config.getChannels();
          List<Channel> sourceChannels =
                  getSourceChannels(channelComponentMap, source, channelNames);
          if (sourceChannels.isEmpty()) {
            String msg = String.format("Source %s is not connected to a " +
                "channel",  sourceName);
            throw new IllegalStateException(msg);
          }
          // f
          ChannelSelectorConfiguration selectorConfig =
              config.getSelectorConfiguration();

          // g
          ChannelSelector selector = ChannelSelectorFactory.create(
              sourceChannels, selectorConfig);

          // h
          ChannelProcessor channelProcessor = new ChannelProcessor(selector);
          // i
          Configurables.configure(channelProcessor, config);

          // j
          source.setChannelProcessor(channelProcessor);
          // k
          sourceRunnerMap.put(comp.getComponentName(),
              SourceRunner.forSource(source));
          // l
          for (Channel channel : sourceChannels) {
            ChannelComponent channelComponent =
                Preconditions.checkNotNull(channelComponentMap.get(channel.getName()),
                                           String.format("Channel %s", channel.getName()));
            channelComponent.components.add(sourceName);
          }
        } catch (Exception e) {
          String msg = String.format("Source %s has been removed due to an " +
              "error during configuration", sourceName);
          LOGGER.error(msg, e);
        }
      }
    }
    /**
     * a. 获取 sources 中所有对应的上下文的 map
     * b. 遍历所有 sources 名称
     * c. 通过当前 Source 名称对应的上下文对象构造 1 个 Source 对象
     * d. 调用当前 Source 的 configure(c) 方法
     * e. 获取当前 Source 对应的所有 channels (不能为空)
     * f. 通过当前 Source 对应的上下文对象获取当前 Source 对应的 selectorConfig map
     * g. 通过当前 Source 对应的所有 channels 和 selectorConfig map 构造 1 个 ChannelSelector 对象
     * h. 通过 ChannelSelector 构造 1 个 ChannelProcessor 对象
     * i. 调用当前 ChannelProcessor 的 configure(c) 方法
     * j. 将当前 ChannelProcessor 设置到 Source 中
     * k. 通过当前 Source 构造 1 个 SourceRunner 对象, 并加入到传入参数 sourceRunnerMap 中
     * l. 遍历当前 Source 对应的所有 channels, 将每个 Channel 对应的 channelComponentMap 参数中的 ChannelComponent 对象的 components 属性添加当前 Source 名称
     */
    /*
     * Components which DO NOT have a ComponentConfiguration object
     * and use only Context
     * 没有 ComponentConfiguration 对象且仅使用 Context 的 Components
     */
    // a
    Map<String, Context> sourceContexts = agentConf.getSourceContext();
    // b
    for (String sourceName : sourceNames) {
      // c
      Context context = sourceContexts.get(sourceName);
      if (context != null) {
        Source source =
            sourceFactory.create(sourceName,
                                 context.getString(BasicConfigurationConstants.CONFIG_TYPE));
        try {
          // d
          Configurables.configure(source, context);
          // e
          String[] channelNames = context.getString(
              BasicConfigurationConstants.CONFIG_CHANNELS).split("\\s+");
          List<Channel> sourceChannels =
                  getSourceChannels(channelComponentMap, source, Arrays.asList(channelNames));
          if (sourceChannels.isEmpty()) {
            String msg = String.format("Source %s is not connected to a " +
                "channel",  sourceName);
            throw new IllegalStateException(msg);
          }
          // f
          Map<String, String> selectorConfig = context.getSubProperties(
              BasicConfigurationConstants.CONFIG_SOURCE_CHANNELSELECTOR_PREFIX);

          // g
          ChannelSelector selector = ChannelSelectorFactory.create(
              sourceChannels, selectorConfig);

          // h
          ChannelProcessor channelProcessor = new ChannelProcessor(selector);
          // i
          Configurables.configure(channelProcessor, context);
          // j
          source.setChannelProcessor(channelProcessor);
          // k
          sourceRunnerMap.put(sourceName,
              SourceRunner.forSource(source));
          // l
          for (Channel channel : sourceChannels) {
            ChannelComponent channelComponent =
                Preconditions.checkNotNull(channelComponentMap.get(channel.getName()),
                                           String.format("Channel %s", channel.getName()));
            channelComponent.components.add(sourceName);
          }
        } catch (Exception e) {
          String msg = String.format("Source %s has been removed due to an " +
              "error during configuration", sourceName);
          LOGGER.error(msg, e);
        }
      }
    }
  }

  /**
   * 获取当前 Source 对应的所有 channels
   * channelComponentMap: 当前 Agent 中的所有 Channel 名称和 ChannelComponent 映射的 map
   * source: 当前 Source
   * channelNames: 当前 Source 的所有 Channel 名称
   */
  private List<Channel> getSourceChannels(Map<String, ChannelComponent> channelComponentMap,
                  Source source, Collection<String> channelNames) throws InstantiationException {
    List<Channel> sourceChannels = new ArrayList<Channel>();
    for (String chName : channelNames) {
      ChannelComponent channelComponent = channelComponentMap.get(chName);
      if (channelComponent != null) {
        checkSourceChannelCompatibility(source, channelComponent.channel);
        sourceChannels.add(channelComponent.channel);
      }
    }
    return sourceChannels;
  }

  /**
   * 所配置的当前 Source 对应 Channel 的 transactionCapacity 必须大于或等于当前 Source 的 batchSize
   */
  private void checkSourceChannelCompatibility(Source source, Channel channel)
      throws InstantiationException {
    if (source instanceof BatchSizeSupported && channel instanceof TransactionCapacitySupported) {
      long transCap = ((TransactionCapacitySupported) channel).getTransactionCapacity();
      long batchSize = ((BatchSizeSupported) source).getBatchSize();
      if (transCap < batchSize) {
        String msg = String.format(
            "Incompatible source and channel settings defined. " +
                "source's batch size is greater than the channels transaction capacity. " +
                "Source: %s, batch size = %d, channel %s, transaction capacity = %d",
            source.getName(), batchSize,
            channel.getName(), transCap);
        throw new InstantiationException(msg);
      }
    }
  }

  /**
   * 所配置的当前 Sink 对应 Channel 的 transactionCapacity 必须大于或等于当前 Sink 的 batchSize
   */
  private void checkSinkChannelCompatibility(Sink sink, Channel channel)
      throws InstantiationException {
    if (sink instanceof BatchSizeSupported && channel instanceof TransactionCapacitySupported) {
      long transCap = ((TransactionCapacitySupported) channel).getTransactionCapacity();
      long batchSize = ((BatchSizeSupported) sink).getBatchSize();
      if (transCap < batchSize) {
        String msg = String.format(
            "Incompatible sink and channel settings defined. " +
                "sink's batch size is greater than the channels transaction capacity. " +
                "Sink: %s, batch size = %d, channel %s, transaction capacity = %d",
            sink.getName(), batchSize,
            channel.getName(), transCap);
        throw new InstantiationException(msg);
      }
    }
  }

  /**
   * 通过 agentConf 参数构建所有 sinks 对象, 获取并设置对应的 Channel. 然后获取 / 创建对应的 SinkGroup (可选), SinkProcessor, SinkRunner 对象并设置它们之间的关系, 并将 SinkRunner 加入到 sinkRunnerMap 参数等.
   */
  private void loadSinks(AgentConfiguration agentConf,
      Map<String, ChannelComponent> channelComponentMap, Map<String, SinkRunner> sinkRunnerMap)
      throws InstantiationException {
    // 获取所有 sinks 名称集合
    Set<String> sinkNames = agentConf.getSinkSet();
    /**
     * a. 获取 sinks 中继承了 ComponentConfiguration 类的 map
     * b. 初始化 sinks map, 用于存储所有 sinks
     * c. 遍历所有 sinks 名称
     * d. 通过当前 Sink 名称对应的 ComponentConfiguration 子类构造 1 个 Sink 对象
     * e. 调用当前 Sink 的 configure(c) 方法
     * f. 获取当前 Sink 对应的 Channel (不能为空, 所配置的当前 Sink 对应 Channel 的 transactionCapacity 必须大于或等于当前 Sink 的 batchSize)
     * g. 将当前 Channel 设置到 Sink 中
     * h. 将当前 Sink 加入到 sinks map 中
     * i. 将当前 Sink 名称到传入参数 channelComponentMap 中
     */
    // a
    Map<String, ComponentConfiguration> compMap =
        agentConf.getSinkConfigMap();
    // b
    Map<String, Sink> sinks = new HashMap<String, Sink>();
    /*
     * Components which have a ComponentConfiguration object
     * 具有 ComponentConfiguration 对象的 Components
     */
    // c
    for (String sinkName : sinkNames) {
      // d
      ComponentConfiguration comp = compMap.get(sinkName);
      if (comp != null) {
        SinkConfiguration config = (SinkConfiguration) comp;
        Sink sink = sinkFactory.create(comp.getComponentName(), comp.getType());
        try {
          // e
          Configurables.configure(sink, config);
          // f
          ChannelComponent channelComponent = channelComponentMap.get(config.getChannel());
          if (channelComponent == null) {
            String msg = String.format("Sink %s is not connected to a " +
                "channel",  sinkName);
            throw new IllegalStateException(msg);
          }
          checkSinkChannelCompatibility(sink, channelComponent.channel);
          // g
          sink.setChannel(channelComponent.channel);
          // h
          sinks.put(comp.getComponentName(), sink);
          // i
          channelComponent.components.add(sinkName);
        } catch (Exception e) {
          String msg = String.format("Sink %s has been removed due to an " +
              "error during configuration", sinkName);
          LOGGER.error(msg, e);
        }
      }
    }
    /**
     * a. 获取 sinks 中所有对应的上下文的 map
     * b. 遍历所有 sinks 名称
     * c. 通过当前 Sink 名称对应的上下文对象构造构造 1 个 Sink 对象
     * d. 调用当前 Sink 的 configure(c) 方法
     * e. 获取当前 Sink 对应的 Channel (不能为空, 所配置的当前 Sink 对应 Channel 的 transactionCapacity 必须大于或等于当前 Sink 的 batchSize)
     * f. 将当前 Channel 设置到 Sink 中
     * g. 将当前 Sink 加入到 sinks map 中
     * h. 将当前 Sink 名称到传入参数 channelComponentMap 中
     */
    /*
     * Components which DO NOT have a ComponentConfiguration object
     * and use only Context
     * 没有 ComponentConfiguration 对象且仅使用 Context 的 Components
     */
    // a
    Map<String, Context> sinkContexts = agentConf.getSinkContext();
    // b
    for (String sinkName : sinkNames) {
      // c
      Context context = sinkContexts.get(sinkName);
      if (context != null) {
        Sink sink = sinkFactory.create(sinkName, context.getString(
            BasicConfigurationConstants.CONFIG_TYPE));
        try {
          // d
          Configurables.configure(sink, context);
          // e
          ChannelComponent channelComponent =
              channelComponentMap.get(
                  context.getString(BasicConfigurationConstants.CONFIG_CHANNEL));
          if (channelComponent == null) {
            String msg = String.format("Sink %s is not connected to a " +
                "channel",  sinkName);
            throw new IllegalStateException(msg);
          }
          checkSinkChannelCompatibility(sink, channelComponent.channel);
          // f
          sink.setChannel(channelComponent.channel);
          // g
          sinks.put(sinkName, sink);
          // h
          channelComponent.components.add(sinkName);
        } catch (Exception e) {
          String msg = String.format("Sink %s has been removed due to an " +
              "error during configuration", sinkName);
          LOGGER.error(msg, e);
        }
      }
    }

    // 在已配置和未配置 sinkGroups 两种情况下, 分别获取 / 创建对应的 SinkGroup (可选), SinkProcessor, SinkRunner 对象并设置它们之间的关系, 并将 SinkRunner 加入到 sinkRunnerMap 参数.
    loadSinkGroups(agentConf, sinks, sinkRunnerMap);
  }

  /**
   * 在已配置和未配置 sinkGroups 两种情况下, 分别获取 / 创建对应的 SinkGroup (可选), SinkProcessor, SinkRunner 对象并设置它们之间的关系, 并将 SinkRunner 加入到 sinkRunnerMap 参数.
   */
  private void loadSinkGroups(AgentConfiguration agentConf,
      Map<String, Sink> sinks, Map<String, SinkRunner> sinkRunnerMap)
          throws InstantiationException {
    // 获取所有 sinkGroups 名称
    Set<String> sinkGroupNames = agentConf.getSinkgroupSet();
    // 获取 SinkGroups 中继承了 ComponentConfiguration 类的 map
    Map<String, ComponentConfiguration> compMap =
        agentConf.getSinkGroupConfigMap();
    // 记录被 sinkGroups 配置了的 sinks
    Map<String, String> usedSinks = new HashMap<String, String>();
    /**
     * a. 遍历所有 sinkGroups 名称
     * b. 遍历当前 sinkGroup 名称对应的 ComponentConfiguration 子类 (SinkGroupConfiguration) 的所有 sinks 名称
     * c. 从 sinks 参数中取出当前 sink 名称对应的 value Sink (不能为 null)
     * d. 将取出的 Sink 加入到当前 sinkGroup 对应的 groupSinks 集合中
     * e. 通过当前 sinkGroup 对应的 groupSinks 集合构建 1 个 SinkGroup 对象
     * f. 调用当前 SinkGroup 的 configure(c) 方法
     * g. 通过当前 SinkGroup 的 SinkProcessor 属性构造 1 个 SinkRunner 对象, 并加入到传入参数 sinkRunnerMap 中
     */
    // a
    for (String groupName: sinkGroupNames) {
      // b
      ComponentConfiguration comp = compMap.get(groupName);
      if (comp != null) {
        SinkGroupConfiguration groupConf = (SinkGroupConfiguration) comp;
        List<Sink> groupSinks = new ArrayList<Sink>();
        for (String sink : groupConf.getSinks()) {
          // c
          Sink s = sinks.remove(sink);
          if (s == null) {
            String sinkUser = usedSinks.get(sink);
            if (sinkUser != null) {
              throw new InstantiationException(String.format(
                  "Sink %s of group %s already " +
                      "in use by group %s", sink, groupName, sinkUser));
            } else {
              throw new InstantiationException(String.format(
                  "Sink %s of group %s does "
                      + "not exist or is not properly configured", sink,
                      groupName));
            }
          }
          // d
          groupSinks.add(s);
          usedSinks.put(sink, groupName);
        }
        try {
          // e
          SinkGroup group = new SinkGroup(groupSinks);
          // f
          Configurables.configure(group, groupConf);
          // g
          sinkRunnerMap.put(comp.getComponentName(),
              new SinkRunner(group.getProcessor()));
        } catch (Exception e) {
          String msg = String.format("SinkGroup %s has been removed due to " +
              "an error during configuration", groupName);
          LOGGER.error(msg, e);
        }
      }
    }
    // add any unassigned sinks to solo collectors
    // 将任何未分配的 sinks 添加到 solo collectors
    /**
     * 没有配置到 SinkGroup 的 sink 使用 DefaultSinkProcessor, 该 DefaultSinkProcessor 只包含 1 个 Sink
     * a. 筛选出没有配置到 SinkGroup 的 sink
     * b. 创建 DefaultSinkProcessor, 设置该 DefaultSinkProcessor 的 sinks 属性为当前的这 1 个 Sink
     * c. 调用当前 SinkProcessor 的 configure(c) 方法 (上下文参数通过默认的空构造方法创建)
     * d. 加入到传入参数 sinkRunnerMap 中
     */
    for (Entry<String, Sink> entry : sinks.entrySet()) {
      // a
      if (!usedSinks.containsValue(entry.getKey())) {
        try {
          // b
          SinkProcessor pr = new DefaultSinkProcessor();
          List<Sink> sinkMap = new ArrayList<Sink>();
          sinkMap.add(entry.getValue());
          pr.setSinks(sinkMap);
          // c
          Configurables.configure(pr, new Context());
          // d
          sinkRunnerMap.put(entry.getKey(), new SinkRunner(pr));
        } catch (Exception e) {
          String msg = String.format("SinkGroup %s has been removed due to " +
              "an error during configuration", entry.getKey());
          LOGGER.error(msg, e);
        }
      }
    }
  }
  /**
   * Channel Component 封装对象
   */
  private static class ChannelComponent {
    // 当前 Channel 对象
    final Channel channel;
    // 当前 Channel 关联的 sources, sinks 名称集合
    final List<String> components;

    ChannelComponent(Channel channel) {
      this.channel = channel;
      components = Lists.newArrayList();
    }
  }

  /**
   * 将 Properties 对象转换为 Map 对象.
   */
  protected Map<String, String> toMap(Properties properties) {
    Map<String, String> result = Maps.newHashMap();
    Enumeration<?> propertyNames = properties.propertyNames();
    while (propertyNames.hasMoreElements()) {
      String name = (String) propertyNames.nextElement();
      String value = properties.getProperty(name);
      result.put(name, value);
    }
    return result;
  }
}