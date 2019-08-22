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

import java.util.List;
import org.apache.flume.Event;
import org.apache.flume.annotations.InterfaceAudience;
import org.apache.flume.annotations.InterfaceStability;
import org.apache.flume.conf.Configurable;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface Interceptor {
  /**
   * Any initialization / startup needed by the Interceptor.
   * Interceptor 需要的任何 initialization / startup 动作.
   */
  public void initialize();

  /**
   * Interception of a single {@link Event}.
   * 拦截单个 {@link Event}.
   * @param event Event to be intercepted
   * @param event 被拦截的 Event
   * @return Original or modified event, or {@code null} if the Event
   * is to be dropped (i.e. filtered out).
   * @return 原有的或被修改的 event, 或 {@code null} (如果 Event 要被删除) (即过滤掉).
   */
  public Event intercept(Event event);

  /**
   * Interception of a batch of {@linkplain Event events}.
   * 拦截一批量的 {@linkplain Event events}.
   * @param events Input list of events
   * @param events events 的输入列表
   * @return Output list of events. The size of output list MUST NOT BE GREATER
   * than the size of the input list (i.e. transformation and removal ONLY).
   * Also, this method MUST NOT return {@code null}. If all events are dropped,
   * then an empty List is returned.
   * @return events 的输出列表. 输出列表的大小不得大于输入列表的大小 (即仅转换和删除).
   * 此外, 此方法不得返回 {@code null}. 如果所有 events 被删除, 则返回空 List.
   */
  public List<Event> intercept(List<Event> events);

  /**
   * Perform any closing / shutdown needed by the Interceptor.
   * 执行 Interceptor 需要的任何 closing / shutdown 动作.
   */
  public void close();

  /** Builder implementations MUST have a no-arg constructor */
  /** Builder 的实现类必须有一个 no-arg 构造方法 */
  public interface Builder extends Configurable {
    public Interceptor build();
  }
}
