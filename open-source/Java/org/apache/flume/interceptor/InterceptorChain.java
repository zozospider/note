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
package org.apache.flume.interceptor;

import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.List;

import org.apache.flume.Event;
import com.google.common.collect.Lists;

/**
 * Implementation of Interceptor that calls a list of other Interceptors
 * serially.
 * Interceptor 的实现: 串行调用其他 Interceptors 列表.
 */
public class InterceptorChain implements Interceptor {

  // list of interceptors that will be traversed, in order
  // 将按顺序遍历的 interceptors 列表
  private List<Interceptor> interceptors;

  // 构造方法, 默认新建一个空的 interceptors 列表
  public InterceptorChain() {
    interceptors = Lists.newLinkedList();
  }

  // 设置 interceptors 列表
  public void setInterceptors(List<Interceptor> interceptors) {
    this.interceptors = interceptors;
  }

  /**
   * 对传入的 event, 遍历 interceptors 列表, 调用每个 interceptor 的 intercept(e) 方法, 对 event 进行多次拦截
   */
  @Override
  public Event intercept(Event event) {
    for (Interceptor interceptor : interceptors) {
      // 如果 event 为 null, 则结束循环, 返回 null
      if (event == null) {
        return null;
      }
      // 调用当前 interceptor 的 intercept(e) 方法, 对 event 进行拦截
      event = interceptor.intercept(event);
    }
    // 返回多次拦截后的 event
    return event;
  }

  /**
   * 对传入的多个 events, 遍历 interceptors 列表, 调用每个 interceptor 的 intercept(es) 方法, 对多个 events 进行多次拦截
   */
  @Override
  public List<Event> intercept(List<Event> events) {
    for (Interceptor interceptor : interceptors) {
      // 如果 event 为空, 则结束循环, 返回 events
      if (events.isEmpty()) {
        return events;
      }
      // 调用当前 interceptor 的 intercept(es) 方法, 对多个 events 进行拦截
      events = interceptor.intercept(events);
      // 由于 Interceptor 接口定义的 intercept(es) 方法规定: 此方法不得返回 {@code null}. 如果所有 events 被删除, 则返回空 List.
      // 所以确保返回的 events 不为 null
      Preconditions.checkNotNull(events,
          "Event list returned null from interceptor %s", interceptor);
    }
    // 返回多次拦截后的多个 events
    return events;
  }

  /**
   * 遍历 interceptors 列表, 调用每个 interceptor 的 initialize() 方法
   */
  @Override
  public void initialize() {
    Iterator<Interceptor> iter = interceptors.iterator();
    while (iter.hasNext()) {
      Interceptor interceptor = iter.next();
      interceptor.initialize();
    }
  }

  /**
   * 遍历 interceptors 列表, 调用每个 interceptor 的 close() 方法
   */
  @Override
  public void close() {
    Iterator<Interceptor> iter = interceptors.iterator();
    while (iter.hasNext()) {
      Interceptor interceptor = iter.next();
      interceptor.close();
    }
  }

}
