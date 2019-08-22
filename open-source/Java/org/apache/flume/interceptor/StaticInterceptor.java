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

package org.apache.flume.interceptor;

import java.util.List;
import java.util.Map;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interceptor class that appends a static, pre-configured header to all events.
 * Interceptor 类: 为所有 events 添加静态的预配置 header.
 *
 * Properties:<p>
 *
 *   key: Key to use in static header insertion.
 *        (default is "key")<p>
 *   key: 用于静态 header 插入的 Key.
 *        (默认为 "key")<p>
 *
 *   value: Value to use in static header insertion.
 *        (default is "value")<p>
 *   value: 用于静态 header 插入的 Value.
 *        (默认为 "value")<p>
 *
 *   preserveExisting: Whether to preserve an existing value for 'key'
 *                     (default is true)<p>
 *   preserveExisting: 是否保留 'key' 的现有值
 *                     (默认为 true)<p>
 *
 * Sample config:<p>
 * 示例配置<p>
 *
 * <code>
 *   agent.sources.r1.channels = c1<p>
 *   agent.sources.r1.type = SEQ<p>
 *   agent.sources.r1.interceptors = i1<p>
 *   agent.sources.r1.interceptors.i1.type = static<p>
 *   agent.sources.r1.interceptors.i1.preserveExisting = false<p>
 *   agent.sources.r1.interceptors.i1.key = datacenter<p>
 *   agent.sources.r1.interceptors.i1.value= NYC_01<p>
 * </code>
 *
 */
public class StaticInterceptor implements Interceptor {

  private static final Logger logger = LoggerFactory.getLogger(StaticInterceptor.class);

  private final boolean preserveExisting;
  private final String key;
  private final String value;

  /**
   * Only {@link HostInterceptor.Builder} can build me
   * 只有 {@link HostInterceptor.Builder} 能构建我
   */
  private StaticInterceptor(boolean preserveExisting, String key,
      String value) {
    this.preserveExisting = preserveExisting;
    this.key = key;
    this.value = value;
  }

  @Override
  public void initialize() {
    // no-op
  }

  /**
   * Modifies events in-place.
   * 就地修改 events.
   */
  @Override
  public Event intercept(Event event) {
    Map<String, String> headers = event.getHeaders();

    // 如果 preserveExisting 为 true (保留 key 的现有值) 且 headers 中包含设置的 key, 则不做任何处理.
    if (preserveExisting && headers.containsKey(key)) {
      return event;
    }

    headers.put(key, value);
    return event;
  }

  /**
   * Delegates to {@link #intercept(Event)} in a loop.
   * @param events
   * @return
   */
  @Override
  public List<Event> intercept(List<Event> events) {
    for (Event event : events) {
      intercept(event);
    }
    return events;
  }

  @Override
  public void close() {
    // no-op
  }

  /**
   * Builder which builds new instance of the StaticInterceptor.
   * 生成 StaticInterceptor 新实例的 Builder.
   */
  public static class Builder implements Interceptor.Builder {

    // preserveExisting: If configured header already exists, should it be preserved - true or false
    // preserveExisting: 如果已配置的 header 已存在, 是否应该保留 - true / false
    private boolean preserveExisting;
    // key: Name of header that should be created
    // key: 应创建的 header 的名称
    private String key;
    private String value;

    @Override
    public void configure(Context context) {
      preserveExisting = context.getBoolean(Constants.PRESERVE, Constants.PRESERVE_DEFAULT);
      key = context.getString(Constants.KEY, Constants.KEY_DEFAULT);
      value = context.getString(Constants.VALUE, Constants.VALUE_DEFAULT);
    }

    @Override
    public Interceptor build() {
      logger.info(String.format(
          "Creating StaticInterceptor: preserveExisting=%s,key=%s,value=%s",
          preserveExisting, key, value));
      return new StaticInterceptor(preserveExisting, key, value);
    }

  }

  public static class Constants {
    public static final String KEY = "key";
    public static final String KEY_DEFAULT = "key";

    public static final String VALUE = "value";
    public static final String VALUE_DEFAULT = "value";

    public static final String PRESERVE = "preserveExisting";
    public static final boolean PRESERVE_DEFAULT = true;
  }
}
