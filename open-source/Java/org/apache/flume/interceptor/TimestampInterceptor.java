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

import static org.apache.flume.interceptor.TimestampInterceptor.Constants.*;

/**
 * Simple Interceptor class that sets the current system timestamp on all events
 * that are intercepted.
 * 简单的 Interceptor 类: 用于设置所有被拦截的 events 的当前系统时间戳.
 * By convention, this timestamp header is named "timestamp" by default and its format
 * is a "stringified" long timestamp in milliseconds since the UNIX epoch.
 * The name of the header can be changed through the configuration using the
 * config key "header".
 * 按照惯例, 此时间戳 header 默认名为 "timestamp", 其格式为自 UNIX 纪元以来的 "字符串化" 长时间戳 (以毫秒为单位).
 * 可以通过配置, 使用配置键 "header" 更改 header 的名称.
 */
public class TimestampInterceptor implements Interceptor {

  private final boolean preserveExisting;
  private final String header;

  /**
   * Only {@link TimestampInterceptor.Builder} can build me
   * 只有 {@link HostInterceptor.Builder} 能构建我
   */
  private TimestampInterceptor(boolean preserveExisting, String header) {
    this.preserveExisting = preserveExisting;
    this.header = header;
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
    // 如果 preserveExisting 为 True (保留 timestamp 的现有值) 且 event 的 headers 中包含设置的 header, 则不做任何处理.
    if (preserveExisting && headers.containsKey(header)) {
      // we must preserve the existing timestamp
      // 我们必须保留现有的 timestamp
    } else {
      long now = System.currentTimeMillis();
      headers.put(header, Long.toString(now));
    }
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
   * Builder which builds new instances of the TimestampInterceptor.
   * 生成 TimestampInterceptor 新实例的 Builder.
   */
  public static class Builder implements Interceptor.Builder {

    // preserveExisting: If the timestamp already exists, should it be preserved - true or false
    // preserveExisting: 如果 timestamp 已存在, 是否应该保留 - true / false
    private boolean preserveExisting = DEFAULT_PRESERVE;
    // headerName: The name of the header in which to place the generated timestamp.
    // headerName: 用于放置生成的时间戳的 header 的名称
    private String header = DEFAULT_HEADER_NAME;

    @Override
    public Interceptor build() {
      return new TimestampInterceptor(preserveExisting, header);
    }

    @Override
    public void configure(Context context) {
      preserveExisting = context.getBoolean(CONFIG_PRESERVE, DEFAULT_PRESERVE);
      header = context.getString(CONFIG_HEADER_NAME, DEFAULT_HEADER_NAME);
    }

  }

  public static class Constants {
    public static final String CONFIG_PRESERVE = "preserveExisting";
    public static final boolean DEFAULT_PRESERVE = false;
    public static final String CONFIG_HEADER_NAME = "headerName";
    public static final String DEFAULT_HEADER_NAME = "timestamp";
  }

}
