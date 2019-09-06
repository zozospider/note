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
package org.apache.flume.instrumentation;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.ObjectName;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used for keeping track of internal metrics using atomic integers</p>
 * 用于使用 atomic integers 跟踪内部 metrics </p>
 *
 * This is used by a variety of component types such as Sources, Channels,
 * Sinks, SinkProcessors, ChannelProcessors, Interceptors and Serializers.
 * 它被各种 component 类型使用, 例如 Sources, Channels, Sinks, SinkProcessors, ChannelProcessors, Interceptor 和 Serializers.
 */
public abstract class MonitoredCounterGroup {

  private static final Logger logger =
      LoggerFactory.getLogger(MonitoredCounterGroup.class);

  // Key for component's start time in MonitoredCounterGroup.counterMap
  // MonitoredCounterGroup.counterMap 中 component 启动时间的 key
  private static final String COUNTER_GROUP_START_TIME = "start.time";

  // key for component's stop time in MonitoredCounterGroup.counterMap
  // MonitoredCounterGroup.counterMap 中 component 停止时间的 key
  private static final String COUNTER_GROUP_STOP_TIME = "stop.time";

  private final Type type;
  private final String name;
  private final Map<String, AtomicLong> counterMap;

  private AtomicLong startTime;
  private AtomicLong stopTime;
  private volatile boolean registered = false;

  protected MonitoredCounterGroup(Type type, String name, String... attrs) {
    this.type = type;
    this.name = name;

    Map<String, AtomicLong> counterInitMap = new HashMap<String, AtomicLong>();

    // Initialize the counters
    // 初始化 counters
    for (String attribute : attrs) {
      counterInitMap.put(attribute, new AtomicLong(0L));
    }

    counterMap = Collections.unmodifiableMap(counterInitMap);

    startTime = new AtomicLong(0L);
    stopTime = new AtomicLong(0L);

  }

  /**
   * Starts the component
   * 启动 component
   *
   * Initializes the values for the stop time as well as all the keys in the
   * internal map to zero and sets the start time to the current time in
   * milliseconds since midnight January 1, 1970 UTC
   * 将停止时间的值以及内部 map 中的所有 keys 初始化为零, 并将开始时间设置为自 1970 年 1 月 1 日午夜以来的当前时间 (以毫秒为单位)
   */
  public void start() {

    register();
    stopTime.set(0L);
    for (String counter : counterMap.keySet()) {
      counterMap.get(counter).set(0L);
    }
    startTime.set(System.currentTimeMillis());
    logger.info("Component type: " + type + ", name: " + name + " started");
  }

  /**
   * Registers the counter.
   * This method is exposed only for testing, and there should be no need for
   * any implementations to call this method directly.
   * 注册 counter.
   * 此方法仅用于测试, 并且不需要任何实现直接调用此方法.
   */
  @VisibleForTesting
  void register() {
    if (!registered) {
      try {
        ObjectName objName = new ObjectName("org.apache.flume."
                + type.name().toLowerCase(Locale.ENGLISH) + ":type=" + this.name);

        if (ManagementFactory.getPlatformMBeanServer().isRegistered(objName)) {
          logger.debug("Monitored counter group for type: " + type + ", name: "
              + name + ": Another MBean is already registered with this name. "
              + "Unregistering that pre-existing MBean now...");
          ManagementFactory.getPlatformMBeanServer().unregisterMBean(objName);
          logger.debug("Monitored counter group for type: " + type + ", name: "
              + name + ": Successfully unregistered pre-existing MBean.");
        }
        ManagementFactory.getPlatformMBeanServer().registerMBean(this, objName);
        logger.info("Monitored counter group for type: " + type + ", name: "
            + name + ": Successfully registered new MBean.");
        registered = true;
      } catch (Exception ex) {
        logger.error("Failed to register monitored counter group for type: "
                + type + ", name: " + name, ex);
      }
    }
  }

  /**
   * Shuts Down the Component
   * 关闭 Component
   *
   * Used to indicate that the component is shutting down.
   * 用于指示 component 正在关闭.
   *
   * Sets the stop time and then prints out the metrics from
   * the internal map of keys to values for the following components:
   * 设置停止时间, 然后将 metrics 从 keys 的内部 map 打印到以下 components 的值:
   *
   * - ChannelCounter
   * - ChannelProcessorCounter
   * - SinkCounter
   * - SinkProcessorCounter
   * - SourceCounter
   */
  public void stop() {

    // Sets the stopTime for the component as the current time in milliseconds
    // 将 component 的 stopTime 设置为当前时间 (以毫秒为单位)
    stopTime.set(System.currentTimeMillis());

    // Prints out a message indicating that this component has been stopped
    // 打印出一条消息, 指示此 component 已停止
    logger.info("Component type: " + type + ", name: " + name + " stopped");

    // Retrieve the type for this counter group
    // 检索此 counter 组的类型
    final String typePrefix = type.name().toLowerCase(Locale.ENGLISH);

    // Print out the startTime for this component
    // 打印出该 component 的 startTime
    logger.info("Shutdown Metric for type: " + type + ", "
        + "name: " + name + ". "
        + typePrefix + "." + COUNTER_GROUP_START_TIME
        + " == " + startTime);

    // Print out the stopTime for this component
    // 打印出该 component 的 stopTime
    logger.info("Shutdown Metric for type: " + type + ", "
        + "name: " + name + ". "
        + typePrefix + "." + COUNTER_GROUP_STOP_TIME
        + " == " + stopTime);

    // Retrieve and sort counter group map keys
    // 检索并排序 counter 组 map keys
    final List<String> mapKeys = new ArrayList<String>(counterMap.keySet());

    Collections.sort(mapKeys);

    // Cycle through and print out all the key value pairs in counterMap
    // 循环并打印出 counterMap 中的所有 key value 对
    for (final String counterMapKey : mapKeys) {

      // Retrieves the value from the original counterMap.
      // 从原始 counterMap 中检索 value.
      final long counterMapValue = get(counterMapKey);

      logger.info("Shutdown Metric for type: " + type + ", "
          + "name: " + name + ". "
          + counterMapKey + " == " + counterMapValue);
    }
  }

  /**
   * Returns when this component was first started
   * 首次启动此 component 时返回
   *
   * @return
   */
  public long getStartTime() {
    return startTime.get();
  }

  /**
   * Returns when this component was stopped
   * 此 component 停止时返回
   *
   * @return
   */
  public long getStopTime() {
    return stopTime.get();
  }

  @Override
  public final String toString() {
    StringBuilder sb = new StringBuilder(type.name()).append(":");
    sb.append(name).append("{");
    boolean first = true;
    Iterator<String> counterIterator = counterMap.keySet().iterator();
    while (counterIterator.hasNext()) {
      if (first) {
        first = false;
      } else {
        sb.append(", ");
      }
      String counterName = counterIterator.next();
      sb.append(counterName).append("=").append(get(counterName));
    }
    sb.append("}");

    return sb.toString();
  }


  /**
   * Retrieves the current value for this key
   * 检索此 key 的当前 value
   *
   * @param counter The key for this metric
   * @param counter 此 metric 的 key
   * @return The current value for this key
   * @return 此 key 的当前 value
   */
  protected long get(String counter) {
    return counterMap.get(counter).get();
  }

  /**
   * Sets the value for this key to the given value
   * 将此 key 的 value 设置为给定 value
   *
   * @param counter The key for this metric
   * @param counter 此 metric 的 key
   * @param value The new value for this key
   * @param value 此 key 的新 value
   */
  protected void set(String counter, long value) {
    counterMap.get(counter).set(value);
  }

  /**
   * Atomically adds the delta to the current value for this key
   * 以原子方式将 delta 添加到此 key 的当前 value
   *
   * @param counter The key for this metric
   * @param counter 此 metric 的 key
   * @param delta
   * @return The updated value for this key
   * @return 此 key 的更新后的 value
   */
  protected long addAndGet(String counter, long delta) {
    return counterMap.get(counter).addAndGet(delta);
  }

  /**
   * Atomically increments the current value for this key by one
   * 以原子方式将此 key 的当前 value 增加 1
   *
   * @param counter The key for this metric
   * @param counter 此 metric 的 key
   * @return The updated value for this key
   * @return 此 key 的更新后的 value
   */
  protected long increment(String counter) {
    return counterMap.get(counter).incrementAndGet();
  }

  /**
   * Component Enum Constants
   * Component 枚举常量
   *
   * Used by each component's constructor to distinguish which type the
   * component is.
   * 由每个 component 的构造函数用来区分 component 的类型.
   */
  public static enum Type {
    SOURCE,
    CHANNEL_PROCESSOR,
    CHANNEL,
    SINK_PROCESSOR,
    SINK,
    INTERCEPTOR,
    SERIALIZER,
    OTHER
  }

  public String getType() {
    return type.name();
  }
}
