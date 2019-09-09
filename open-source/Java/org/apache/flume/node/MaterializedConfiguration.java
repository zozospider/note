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

package org.apache.flume.node;

import org.apache.flume.Channel;
import org.apache.flume.SinkRunner;
import org.apache.flume.SourceRunner;

import com.google.common.collect.ImmutableMap;

/**
 * MaterializedConfiguration represents the materialization of a Flume
 * properties file. That is it's the actual Source, Sink, and Channels
 * represented in the configuration file.
 * MaterializedConfiguration 表示 Flume 配置文件的具体化. 这就是配置文件中表示的实际 Source, Sink 和 Channels.
 * 此接口定义的方法包括: 添加单个 Channel, SourceRunner, SinkRunner, 获取所有 Channels, SourceRunners, SinkRunners.
 */
public interface MaterializedConfiguration {

  /**
   * 添加 1 个 SourceRunner
   */
  public void addSourceRunner(String name, SourceRunner sourceRunner);

  /**
   * 添加 1 个 SinkRunner
   */
  public void addSinkRunner(String name, SinkRunner sinkRunner);

  /**
   * 添加 1 个 Channel
   */
  public void addChannel(String name, Channel channel);

  /**
   * 获取所有 SourceRunners
   */
  public ImmutableMap<String, SourceRunner> getSourceRunners();

  /**
   * 获取所有 SinkRunners
   */
  public ImmutableMap<String, SinkRunner> getSinkRunners();

  /**
   * 获取所有 Channels
   */
  public ImmutableMap<String, Channel> getChannels();

}
