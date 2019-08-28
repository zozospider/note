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
package org.apache.flume.sink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.flume.Sink;
import org.apache.flume.SinkProcessor;
import org.apache.flume.lifecycle.LifecycleState;

/**
 * A convenience base class for sink processors.
 * sink processors 的便利的基础类.
 */
public abstract class AbstractSinkProcessor implements SinkProcessor {

  // 生命周期状态 enum
  private LifecycleState state;

  // List of sinks as specified
  // 指定的 sinks 列表
  private List<Sink> sinkList;

  /**
   * 实现 LifecycleAware 接口的 start() 方法. 状态为: START.
   * 遍历 sinkList, 调用所有 sink 的 start() 方法.
   */
  @Override
  public void start() {
    for (Sink s : sinkList) {
      s.start();
    }

    state = LifecycleState.START;
  }

  /**
   * 实现 LifecycleAware 接口的 stop() 方法. 状态为: STOP.
   * 遍历 sinkList, 调用所有 sink 的 stop() 方法.
   */
  @Override
  public void stop() {
    for (Sink s : sinkList) {
      s.stop();
    }
    state = LifecycleState.STOP;
  }

  /**
   * 实现 LifecycleAware 接口的 getLifecycleState() 方法.
   * 返回 SinkProcessor 的当前状态.
   */
  @Override
  public LifecycleState getLifecycleState() {
    return state;
  }

  /**
   * 实现 SinkProcessor 接口的 setSinks(ss) 方法.
   * 将传入的 sinks 参数赋值到当前 SinkProcessor 的成员变量 sinkList.
   */
  @Override
  public void setSinks(List<Sink> sinks) {
    List<Sink> list = new ArrayList<Sink>();
    list.addAll(sinks);
    sinkList = Collections.unmodifiableList(list);
  }

  /**
   * 获取 sinkList
   */
  protected List<Sink> getSinks() {
    return sinkList;
  }
}
