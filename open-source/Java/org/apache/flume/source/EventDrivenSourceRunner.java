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

package org.apache.flume.source;

import org.apache.flume.Source;
import org.apache.flume.SourceRunner;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.lifecycle.LifecycleState;

/**
 * Starts, stops, and manages
 * {@linkplain EventDrivenSource event-driven sources}.
 * 启动, 停止和管理 {@linkplain EventDrivenSource event-driven sources}.
 */
public class EventDrivenSourceRunner extends SourceRunner {

  // 生命周期状态 enum
  private LifecycleState lifecycleState;

  /**
   * 构造方法, 设置状态为: IDLE.
   */
  public EventDrivenSourceRunner() {
    lifecycleState = LifecycleState.IDLE;
  }

  /**
   * 实现 LifecycleAware 接口的 start() 方法, 设置状态为: START.
   * SourceRunner start 时, 执行对应 Source 的 ChannelProcessor 的初始化逻辑, 然后启动 Source.
   */
  @Override
  public void start() {
    // 调用 getSource() 方法获取当前 SourceRunner 处理的 Source 变量
    Source source = getSource();
    // 获取 Source 对应的 ChannelProcessor
    ChannelProcessor cp = source.getChannelProcessor();
    // 调用 ChannelProcessor 的 initialize() 方法, 初始化 ChannelProcessor
    cp.initialize();
    // 调用 Source 的 start() 方法, 启动 Source
    source.start();
    lifecycleState = LifecycleState.START;
  }

  /**
   * 实现 LifecycleAware 接口的 stop() 方法, 设置状态为: STOP.
   * SourceRunner stop 时, 停止对应 Source, 然后执行 Source 的 ChannelProcessor 的关闭逻辑.
   */
  @Override
  public void stop() {
    Source source = getSource();
    source.stop();
    ChannelProcessor cp = source.getChannelProcessor();
    cp.close();
    lifecycleState = LifecycleState.STOP;
  }

  @Override
  public String toString() {
    return "EventDrivenSourceRunner: { source:" + getSource() + " }";
  }

  /**
   * 实现 LifecycleAware 接口的 getLifecycleState() 方法.
   * 返回 SourceRunner 的当前状态.
   */
  @Override
  public LifecycleState getLifecycleState() {
    return lifecycleState;
  }

}
