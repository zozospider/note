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

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.annotations.InterfaceAudience;
import org.apache.flume.annotations.InterfaceStability;
import org.apache.flume.conf.Configurable;
import org.apache.flume.lifecycle.LifecycleAware;
import org.apache.flume.lifecycle.LifecycleState;

/**
 * Channel 接口抽象类, 实现最基本的功能, 具体的 Channel 实现类一般会继承该抽象类.
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class AbstractChannel
    implements Channel, LifecycleAware, Configurable {

  private String name;

  // 生命周期状态 enum
  private LifecycleState lifecycleState;

  /**
   * 构造方法, 状态为: IDLE.
   */
  public AbstractChannel() {
    lifecycleState = LifecycleState.IDLE;
  }

  @Override
  public synchronized void setName(String name) {
    this.name = name;
  }

  /**
   * 实现 LifecycleAware 接口的 start() 方法.
   * Channel start 时, 状态为: START.
   */
  @Override
  public synchronized void start() {
    lifecycleState = LifecycleState.START;
  }

  /**
   * 实现 LifecycleAware 接口的 start() 方法.
   * Channel stop 时, 状态为: STOP.
   */
  @Override
  public synchronized void stop() {
    lifecycleState = LifecycleState.STOP;
  }

  /**
   * 实现 LifecycleAware 接口的 getLifecycleState() 方法.
   * 返回 Channel 的当前状态.
   */
  @Override
  public synchronized LifecycleState getLifecycleState() {
    return lifecycleState;
  }

  /**
   * 实现 NamedComponent 接口的 getName() 方法.
   */
  @Override
  public synchronized String getName() {
    return name;
  }

  /**
   * 实现 Configurable 接口的 configure(c) 方法.
   */
  @Override
  public void configure(Context context) {}

  public String toString() {
    return this.getClass().getName() + "{name: " + name + "}";
  }

}
