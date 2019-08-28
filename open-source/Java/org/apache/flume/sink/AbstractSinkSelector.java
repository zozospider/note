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
package org.apache.flume.sink;

import java.util.ArrayList;
import java.util.List;

import org.apache.flume.Context;
import org.apache.flume.Sink;
import org.apache.flume.lifecycle.LifecycleState;
import org.apache.flume.sink.LoadBalancingSinkProcessor.SinkSelector;

/**
 * SinkSelector 接口抽象类, 实现最基本的功能, 具体的 SinkSelector 实现类一般会继承该抽象类.
 */
public abstract class AbstractSinkSelector implements SinkSelector {

  // 生命周期状态 enum
  private LifecycleState state;

  // List of sinks as specified
  // 指定的 sinks 列表
  private List<Sink> sinkList;

  // 用于设置到 SinkSelector 的成员变量 OrderSelector 的 maxTimeOut 参数, 默认为 30000L
  protected long maxTimeOut = 0;

  /**
   * 获取 maxTimeOut 配置, 赋值给成员变量
   */
  @Override
  public void configure(Context context) {
    Long timeOut = context.getLong("maxTimeOut");
    if (timeOut != null) {
      maxTimeOut = timeOut;
    }
  }

  /**
   * 实现 LifecycleAware 接口的 start() 方法. 状态为: START.
   */
  @Override
  public void start() {
    state = LifecycleState.START;
  }


  /**
   * 实现 LifecycleAware 接口的 stop() 方法. 状态为: STOP.
   */
  @Override
  public void stop() {
    state = LifecycleState.STOP;
  }

  /**
   * 实现 LifecycleAware 接口的 getLifecycleState() 方法.
   * 返回 SinkSelector 的当前状态.
   */
  @Override
  public LifecycleState getLifecycleState() {
    return state;
  }

  /**
   * 目前已实现的子类都重写了该方法
   */
  @Override
  public void setSinks(List<Sink> sinks) {
    sinkList = new ArrayList<Sink>();
    sinkList.addAll(sinks);
  }

  /**
   * 没用到
   */
  protected List<Sink> getSinks() {
    return sinkList;
  }

  @Override
  public void informSinkFailed(Sink failedSink) {
    // no-op
  }
}
