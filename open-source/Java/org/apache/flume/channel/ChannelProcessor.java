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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.flume.Channel;
import org.apache.flume.ChannelException;
import org.apache.flume.ChannelSelector;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.interceptor.InterceptorChain;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.interceptor.InterceptorBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A channel processor exposes operations to put {@link Event}s into
 * {@link Channel}s. These operations will propagate a {@link ChannelException}
 * if any errors occur while attempting to write to {@code required} channels.
 * <p>
 * Channel Processor 公开操作以将 {@link Event} 放入 {@link Channel}.
 * 如果在尝试写入 {@code required} channels 时发生任何错误, 这些操作将传播 {@link ChannelException}.
 * <p>
 * Each channel processor instance is configured with a {@link ChannelSelector}
 * instance that specifies which channels are
 * {@linkplain ChannelSelector#getRequiredChannels(Event) required} and which
 * channels are
 * {@linkplain ChannelSelector#getOptionalChannels(Event) optional}.
 * 每个 Channel Processor 实例都配置有 {@link ChannelSelector} 实例,
 * 该实例指定哪些 channels 是 {@linkplain ChannelSelector＃getRequiredChannels（Event）required} 以及哪些 channels 是 {@linkplain ChannelSelector＃getOptionalChannels（Event）optional}.
 */
public class ChannelProcessor implements Configurable {

  private static final Logger LOG = LoggerFactory.getLogger(
      ChannelProcessor.class);

  // 如 ReplicatingChannelSelector / MultiplexingChannelSelector
  private final ChannelSelector selector;
  // 拦截器链, 包含配置的所有 interceptors 实例
  private final InterceptorChain interceptorChain;

  /**
   * 构造方法, selector 通过构造参数设置, interceptorChain 通过 configure(c) 和 initialize() 方法设置.
   */
  public ChannelProcessor(ChannelSelector selector) {
    this.selector = selector;
    this.interceptorChain = new InterceptorChain();
  }

  /**
   * 调用 interceptorChain 的 initialize() 方法, 进而调用所有 interceptors 的 initialize() 方法.
   */
  public void initialize() {
    interceptorChain.initialize();
  }

  /**
   * 调用 interceptorChain 的 close() 方法, 进而调用所有 interceptors 的 close() 方法.
   */
  public void close() {
    interceptorChain.close();
  }

  /**
   * The Context of the associated Source is passed.
   * 传递关联 Source 的上下文.
   *
   * @param context
   */
  @Override
  public void configure(Context context) {
    // 配置 interceptors
    configureInterceptors(context);
  }

  // WARNING: throws FlumeException (is that ok?)
  private void configureInterceptors(Context context) {

    List<Interceptor> interceptors = Lists.newLinkedList();

    // 获取所有 interceptor 配置名称
    String interceptorListStr = context.getString("interceptors", "");
    if (interceptorListStr.isEmpty()) {
      return;
    }
    String[] interceptorNames = interceptorListStr.split("\\s+");

    Context interceptorContexts =
        new Context(context.getSubProperties("interceptors."));

    // run through and instantiate all the interceptors specified in the Context
    // 运行并实例化 Context 中指定的所有拦截器
    InterceptorBuilderFactory factory = new InterceptorBuilderFactory();
    // 遍历所有 interceptor 配置名称
    for (String interceptorName : interceptorNames) {
      Context interceptorContext = new Context(
          interceptorContexts.getSubProperties(interceptorName + "."));
      // 获取 interceptor 类型
      String type = interceptorContext.getString("type");
      // 类型不能为空
      if (type == null) {
        LOG.error("Type not specified for interceptor " + interceptorName);
        throw new FlumeException("Interceptor.Type not specified for " +
            interceptorName);
      }
      try {
        Interceptor.Builder builder = factory.newInstance(type);
        // 调用 builder (当前 interceptor 的内部类) 的 configure(c) 方法
        builder.configure(interceptorContext);
        // 添加到 interceptors
        interceptors.add(builder.build());
      } catch (ClassNotFoundException e) {
        LOG.error("Builder class not found. Exception follows.", e);
        throw new FlumeException("Interceptor.Builder not found.", e);
      } catch (InstantiationException e) {
        LOG.error("Could not instantiate Builder. Exception follows.", e);
        throw new FlumeException("Interceptor.Builder not constructable.", e);
      } catch (IllegalAccessException e) {
        LOG.error("Unable to access Builder. Exception follows.", e);
        throw new FlumeException("Unable to access Interceptor.Builder.", e);
      }
    }

    // 将所有 interceptors 实例设置到拦截器链
    interceptorChain.setInterceptors(interceptors);
  }

  public ChannelSelector getSelector() {
    return selector;
  }

  /**
   * Attempts to {@linkplain Channel#put(Event) put} the given events into each
   * configured channel. If any {@code required} channel throws a
   * {@link ChannelException}, that exception will be propagated.
   * <p>
   * 尝试将给定 event {@linkplain Channel#put(Event) put} put 到每个配置的 Channel.
   * 如果任何 {@code required} channel 抛出 {@link ChannelException}, 则会传播该异常.
   * <p>
   * <p>Note that if multiple channels are configured, some {@link Transaction}s
   * may have already been committed while others may be rolled back in the
   * case of an exception.
   * 请注意, 如果配置了多个 channels, 则某些 {@link Transaction} 可能已经提交, 而其他 {@link Transaction} 可能会在异常的情况下回滚.
   *
   * @param events A list of events to put into the configured channels.
   * @param events 要放入已配置 channels 的 events 列表.
   * @throws ChannelException when a write to a required channel fails.
   * @throws ChannelException 当写入所需 channel 失败时.
   */
  public void processEventBatch(List<Event> events) {
    Preconditions.checkNotNull(events, "Event list must not be null");

    // 对传入的多个 events, 遍历 interceptors 列表, 调用每个 interceptor 的 intercept(es) 方法, 对多个 events 进行多次拦截
    events = interceptorChain.intercept(events);

    // key: 必须的 Channel
    // value: 当前必须的 Channel 需要处理的多个 events
    Map<Channel, List<Event>> reqChannelQueue =
        new LinkedHashMap<Channel, List<Event>>();

    // key: 可选的 Channel
    // value: 当前可选的 Channel 需要处理的多个 events
    Map<Channel, List<Event>> optChannelQueue =
        new LinkedHashMap<Channel, List<Event>>();

    // 遍历传入参数的 events 列表
    for (Event event : events) {
      // 通过 Selector 获取处理当前 event 必须的 channels
      List<Channel> reqChannels = selector.getRequiredChannels(event);

      // 将多个 key 和多个 value 设置到 reqChannelQueue
      // 多个 key: 当前 event 必须的 channels
      // 多个 value: 当前 event
      for (Channel ch : reqChannels) {
        List<Event> eventQueue = reqChannelQueue.get(ch);
        if (eventQueue == null) {
          eventQueue = new ArrayList<Event>();
          reqChannelQueue.put(ch, eventQueue);
        }
        eventQueue.add(event);
      }

      // 通过 Selector 获取处理当前 event 可选的 channels
      List<Channel> optChannels = selector.getOptionalChannels(event);

      // 将多个 key 和多个 value 设置到 optChannelQueue
      // 多个 key: 当前 event 可选的 channels
      // 多个 value: 当前 event
      for (Channel ch : optChannels) {
        List<Event> eventQueue = optChannelQueue.get(ch);
        if (eventQueue == null) {
          eventQueue = new ArrayList<Event>();
          optChannelQueue.put(ch, eventQueue);
        }

        eventQueue.add(event);
      }
    }

    // Process required channels
    // 处理必须的 channels
    // 遍历必须的 channels 的 key (必须的 Channel)
    for (Channel reqChannel : reqChannelQueue.keySet()) {
      // 获取当前 Channel 的 Transaction
      Transaction tx = reqChannel.getTransaction();
      Preconditions.checkNotNull(tx, "Transaction object must not be null");
      // 在此 Transaction 的周期内:
      // begin(), put(e)..., commit() / rollback(), close()
      // 遍历处理当前必须的 Channel 需要处理的多个 events.
      try {
        tx.begin();

        // 当前 Channel 的 value (当前必须的 Channel 需要处理的多个 events)
        List<Event> batch = reqChannelQueue.get(reqChannel);

        // 对于每个 event, 都调用当前 Channel 的 put(e) 方法, 即调用对应 Transaction 的 put(e) 方法.
        for (Event event : batch) {
          reqChannel.put(event);
        }

        tx.commit();
      } catch (Throwable t) {
        // 只要此 Transaction 周期内的某 1 个操作出现异常情况, 回滚, 并抛出异常
        tx.rollback();
        if (t instanceof Error) {
          LOG.error("Error while writing to required channel: " + reqChannel, t);
          throw (Error) t;
        } else if (t instanceof ChannelException) {
          throw (ChannelException) t;
        } else {
          throw new ChannelException("Unable to put batch on required " +
              "channel: " + reqChannel, t);
        }
      } finally {
        if (tx != null) {
          tx.close();
        }
      }
    }

    // Process optional channels
    // 处理可选的 channels
    // 遍历可选的 channels 的 key (可选的 Channel)
    for (Channel optChannel : optChannelQueue.keySet()) {
      // 获取当前 Channel 的 Transaction
      Transaction tx = optChannel.getTransaction();
      Preconditions.checkNotNull(tx, "Transaction object must not be null");
      // 在此 Transaction 的周期内:
      // begin(), put(e)..., commit() / rollback(), close()
      // 遍历处理当前可选的 Channel 需要处理的多个 events.
      try {
        tx.begin();

        // 当前 Channel 的 value (当前可选的 Channel 需要处理的多个 events)
        List<Event> batch = optChannelQueue.get(optChannel);

        // 对于每个 event, 都调用当前 Channel 的 put(e) 方法, 即调用对应 Transaction 的 put(e) 方法.
        for (Event event : batch) {
          optChannel.put(event);
        }

        tx.commit();
      } catch (Throwable t) {
        // 只要此 Transaction 周期内的某 1 个操作出现异常情况, 回滚, 但不抛出异常 (Error 除外)
        tx.rollback();
        LOG.error("Unable to put batch on optional channel: " + optChannel, t);
        if (t instanceof Error) {
          throw (Error) t;
        }
      } finally {
        if (tx != null) {
          tx.close();
        }
      }
    }
  }

  /**
   * Attempts to {@linkplain Channel#put(Event) put} the given event into each
   * configured channel. If any {@code required} channel throws a
   * {@link ChannelException}, that exception will be propagated.
   * <p>
   * 尝试将给定 event {@linkplain Channel#put(Event) put} put 到每个配置的 Channel.
   * 如果任何 {@code required} channel 抛出 {@link ChannelException}, 则会传播该异常.
   * <p>
   * <p>Note that if multiple channels are configured, some {@link Transaction}s
   * may have already been committed while others may be rolled back in the
   * case of an exception.
   * 请注意, 如果配置了多个 channels, 则某些 {@link Transaction} 可能已经提交, 而其他 {@link Transaction} 可能会在异常的情况下回滚.
   *
   * @param event The event to put into the configured channels.
   * @param event 要放入已配置 channels 的 event.
   * @throws ChannelException when a write to a required channel fails.
   * @throws ChannelException 当写入所需 channel 失败时.
   */
  public void processEvent(Event event) {

    // 对传入的 event, 遍历 interceptors 列表, 调用每个 interceptor 的 intercept(e) 方法, 对 event 进行多次拦截
    event = interceptorChain.intercept(event);
    // 如果 event 为 null, 表示该 event 已被拦截, 不做处理
    if (event == null) {
      return;
    }

    // Process required channels
    // 处理必须的 channels
    List<Channel> requiredChannels = selector.getRequiredChannels(event);
    // 遍历所有必须的 channels
    for (Channel reqChannel : requiredChannels) {
      // 获取当前 Channel 的 Transaction
      Transaction tx = reqChannel.getTransaction();
      Preconditions.checkNotNull(tx, "Transaction object must not be null");
      // 在此 Transaction 的周期内:
      // begin(), put(e), commit() / rollback(), close()
      // 处理当前必须的 Channel 需要处理的 event.
      try {
        tx.begin();

        // 调用当前 Channel 的 put(e) 方法, 即调用对应 Transaction 的 put(e) 方法.
        reqChannel.put(event);

        tx.commit();
      } catch (Throwable t) {
        // 异常情况下, 回滚, 并抛出异常
        tx.rollback();
        if (t instanceof Error) {
          LOG.error("Error while writing to required channel: " + reqChannel, t);
          throw (Error) t;
        } else if (t instanceof ChannelException) {
          throw (ChannelException) t;
        } else {
          throw new ChannelException("Unable to put event on required " +
              "channel: " + reqChannel, t);
        }
      } finally {
        if (tx != null) {
          tx.close();
        }
      }
    }

    // Process optional channels
    // 处理可选的 channels
    List<Channel> optionalChannels = selector.getOptionalChannels(event);
    // 遍历所有可选的 channels
    for (Channel optChannel : optionalChannels) {
      Transaction tx = null;
      // 在此 Transaction 的周期内:
      // begin(), put(e), commit() / rollback(), close()
      // 处理当前可选的 Channel 需要处理的 event.
      try {
        // 获取当前 Channel 的 Transaction
        tx = optChannel.getTransaction();
        tx.begin();

        // 调用当前 Channel 的 put(e) 方法, 即调用对应 Transaction 的 put(e) 方法.
        optChannel.put(event);

        tx.commit();
      } catch (Throwable t) {
        // 异常情况下, 回滚, 但不抛出异常 (Error 除外)
        tx.rollback();
        LOG.error("Unable to put event on optional channel: " + optChannel, t);
        if (t instanceof Error) {
          throw (Error) t;
        }
      } finally {
        if (tx != null) {
          tx.close();
        }
      }
    }
  }
}
