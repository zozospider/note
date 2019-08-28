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

import java.util.Iterator;
import java.util.List;

import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.apache.flume.Sink;
import org.apache.flume.Sink.Status;
import org.apache.flume.conf.Configurable;
import org.apache.flume.lifecycle.LifecycleAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import org.apache.flume.util.OrderSelector;
import org.apache.flume.util.RandomOrderSelector;
import org.apache.flume.util.RoundRobinOrderSelector;

/**
 * <p>Provides the ability to load-balance flow over multiple sinks.</p>
 * <p>提供在多个 sinks 上进行负载均衡流量的功能.</p>
 *
 * <p>The <tt>LoadBalancingSinkProcessor</tt> maintains an indexed list of
 * active sinks on which the load must be distributed. This implementation
 * supports distributing load using either via <tt>ROUND_ROBIN</tt> or via
 * <tt>RANDOM</tt> selection mechanism. The choice of selection mechanism
 * defaults to <tt>ROUND_ROBIN</tt> type, but can be overridden via
 * configuration.</p>
 * <p><tt>LoadBalancingSinkProcessor</tt> 维护一个必须分配负载的 active sinks 的索引列表.
 * 此实现支持使用 <tt>ROUND_ROBIN</tt> 或 <tt>RANDOM</tt> 选择机制分配负载.
 * 选择机制的选择默认为 <tt>ROUND_ROBIN</tt> 类型, 但可以通过配置覆盖.</p>
 *
 * <p>When invoked, this selector picks the next sink using its configured
 * selection mechanism and invokes it. In case the selected sink fails with
 * an exception, the processor picks the next available sink via its configured
 * selection mechanism. This implementation does not blacklist the failing
 * sink and instead continues to optimistically attempt every available sink.
 * If all sinks invocations result in failure, the selector propagates the
 * failure to the sink runner.</p>
 * <p>调用时, 此 Selector 使用其配置的选择机制选择下一个 Sink 并调用它. 如果所选 Sink 因异常而失败, 则 Processor 通过其配置的选择机制选择下一个可用 Sink.
 * 此实现不会将失败的 sink 列入黑名单, 而是继续乐观地尝试每个可用的 Sink. 如果所有 sinks 调用都导致失败, 则 Selector 将故障传播到 Sink Runner.</p>
 *
 * <p>
 * Sample configuration:
 * <pre>
 * {@code
 * host1.sinkgroups.group1.sinks = sink1 sink2
 * host1.sinkgroups.group1.processor.type = load_balance
 * host1.sinkgroups.group1.processor.selector = <selector type>
 * host1.sinkgroups.group1.processor.selector.selector_property = <value>
 * }
 * </pre>
 *
 * The value of processor.selector could be either <tt>round_robin</tt> for
 * round-robin scheme of load-balancing or <tt>random</tt> for random
 * selection. Alternatively you can specify your own implementation of the
 * selection algorithm by implementing the <tt>LoadBalancingSelector</tt>
 * interface. If no selector mechanism is specified, the round-robin selector
 * is used by default.
 * processor.selector 的值可以是 <tt>round_robin</tt> (load-balancing 的 round-robin 方案), 也可以是 <tt>random</tt> (随机选择).
 * 或者, 您可以通过实现 <tt>LoadBalancingSelector</tt> 接口来指定自己的 selector 算法实现. 如果未指定选择机制, 则默认使用 round-robin Selector.
 * </p>
 * <p>
 * This implementation is not thread safe at this time
 * </p>
 * <p>
 * 此实现目前不是线程安全的
 * </p>
 *
 * @see FailoverSinkProcessor
 * @see LoadBalancingSinkProcessor.SinkSelector
 */
public class LoadBalancingSinkProcessor extends AbstractSinkProcessor {
  public static final String CONFIG_SELECTOR = "selector";
  public static final String CONFIG_SELECTOR_PREFIX = CONFIG_SELECTOR + ".";
  public static final String CONFIG_BACKOFF = "backoff";

  public static final String SELECTOR_NAME_ROUND_ROBIN = "ROUND_ROBIN";
  public static final String SELECTOR_NAME_RANDOM = "RANDOM";
  public static final String SELECTOR_NAME_ROUND_ROBIN_BACKOFF = "ROUND_ROBIN_BACKOFF";
  public static final String SELECTOR_NAME_RANDOM_BACKOFF = "RANDOM_BACKOFF";

  private static final Logger LOGGER = LoggerFactory
      .getLogger(LoadBalancingSinkProcessor.class);

  // 当前 SinkProcessor 使用的 SinkSelector, 如 RoundRobinSinkSelector / RandomOrderSinkSelector
  private SinkSelector selector;

  @Override
  public void configure(Context context) {
    Preconditions.checkState(getSinks().size() > 1,
        "The LoadBalancingSinkProcessor cannot be used for a single sink. "
        + "Please configure more than one sinks and try again.");

    String selectorTypeName = context.getString(CONFIG_SELECTOR,
        SELECTOR_NAME_ROUND_ROBIN);

    Boolean shouldBackOff = context.getBoolean(CONFIG_BACKOFF, false);

    // 根据配置的 selector 名称来决定创建 RoundRobinSinkSelector / RandomOrderSinkSelector / 自定义 SinkSelector
    selector = null;

    if (selectorTypeName.equalsIgnoreCase(SELECTOR_NAME_ROUND_ROBIN)) {
      selector = new RoundRobinSinkSelector(shouldBackOff);
    } else if (selectorTypeName.equalsIgnoreCase(SELECTOR_NAME_RANDOM)) {
      selector = new RandomOrderSinkSelector(shouldBackOff);
    } else {
      try {
        @SuppressWarnings("unchecked")
        Class<? extends SinkSelector> klass = (Class<? extends SinkSelector>)
            Class.forName(selectorTypeName);

        selector = klass.newInstance();
      } catch (Exception ex) {
        throw new FlumeException("Unable to instantiate sink selector: "
            + selectorTypeName, ex);
      }
    }

    // 设置当前 SinkSelector 的 sinks
    selector.setSinks(getSinks());
    // 调用 SinkSelector 实现的 Configurable 的 configure(c) 方法
    selector.configure(
        new Context(context.getSubProperties(CONFIG_SELECTOR_PREFIX)));

    LOGGER.debug("Sink selector: " + selector + " initialized");
  }

  /**
   * 实现 LifecycleAware 接口的 start() 方法.
   * 首先执行父类 AbstractSinkProcessor 的 start() 方法逻辑, 然后执行当前 SinkSelector 的 start() 方法逻辑.
   */
  @Override
  public void start() {
    // 调用父类 AbstractSinkProcessor 的 start() 方法.
    // 遍历 sinkList, 调用所有 sink 的 start() 方法, 并设置当前 SinkProcessor 状态为: START.
    super.start();

    // 调用当前 SinkSelector 实现的父类 AbstractSinkSelector 的 start() 方法.
    // 设置当前 SinkSelector 状态为: START.
    selector.start();
  }

  /**
   * 实现 LifecycleAware 接口的 stop() 方法.
   * 首先执行父类 AbstractSinkProcessor 的 stop() 方法逻辑, 然后执行当前 SinkSelector 的 stop() 方法逻辑.
   */
  @Override
  public void stop() {
    // 调用父类 AbstractSinkProcessor 的 stop() 方法.
    // 遍历 sinkList, 调用所有 sink 的 stop() 方法, 并设置当前 SinkProcessor 状态为: START.
    super.stop();

    // 调用当前 SinkSelector 实现的父类 AbstractSinkSelector 的 stop() 方法.
    // 设置当前 SinkSelector 状态为: STOP.
    selector.stop();
  }

  /**
   * 实现 SinkProcessor 接口的 process() 方法.
   * 该方法由 SinkRunner 启动的 PollingRunner 线程不断循环调用.
   */
  @Override
  public Status process() throws EventDeliveryException {
    Status status = null;

    /**
     * 调用 SinkSelector (RoundRobinSinkSelector / RandomOrderSinkSelector) 的 createSinkIterator() 方法.
     * RoundRobinSinkSelector 的 createSinkIterator() 方法调用对应的 RoundRobinOrderSelector 工具类的 createIterator() 方法.
     * RandomOrderSinkSelector 的 createSinkIterator() 方法调用对应的 RandomOrderSelector 工具类的 createIterator() 方法.
     *
     * 获取当前 Selector 对应的排序算法得出的 SpecificOrderIterator 迭代器 (封装了确认顺序的 active sinks 列表)
     */
    Iterator<Sink> sinkIterator = selector.createSinkIterator();
    /**
     * 不断循环 SpecificOrderIterator 迭代器, 直到该迭代器的 hasNext() 方法为返回 false (则表示已经遍历完该迭代器的所有元素)
     */
    while (sinkIterator.hasNext()) {
      // 调用该迭代器的 next() 方法, 从迭代器中取出一个 Sink
      Sink sink = sinkIterator.next();
      try {
        // 调用具体 Sink 实现的 process() 方法
        status = sink.process();
        // 在没有异常的情况下, 继续下一轮循环
        break;
      } catch (Exception ex) {
        /**
         * 异常情况下, 调用当前 SinkSelector 的 informSinkFailed(s) 方法.
         * 即调用对应的 OrderSelector 的 informFailure(s) 方法, 以便可以将当前 failedObject 的 backed off (退位).
         */
        selector.informSinkFailed(sink);
        LOGGER.warn("Sink failed to consume event. "
            + "Attempting next sink if available.", ex);
      }
    }

    // status 为 null, 说明所有配置的 sinks 都处理失败 (调用 sink.process() 方法异常).
    if (status == null) {
      throw new EventDeliveryException("All configured sinks have failed");
    }

    // 返回最后一次循环的 sink.process() 的返回值
    return status;
  }


  /**
   * <p>
   * An interface that allows the LoadBalancingSinkProcessor to use
   * a load-balancing strategy such as round-robin, random distribution etc.
   * Implementations of this class can be plugged into the system via
   * processor configuration and are used to select a sink on every invocation.
   * </p>
   * <p>
   * 允许 LoadBalancingSinkProcessor 使用 load-balancing 策略 (round-robin, random distribution 等) 的接口.
   * 此类的实现可以通过 Processor configuration 插入系统, 并用于在每次调用时选择 Sink.
   * </p>
   * <p>
   * An instance of the configured sink selector is create during the processor
   * configuration, its {@linkplain #setSinks(List)} method is invoked following
   * which it is configured via a subcontext. Once configured, the lifecycle of
   * this selector is tied to the lifecycle of the sink processor.
   * </p>
   * <p>
   * 在 Processor configuration 期间创建配置的 Sink Selector 的实例, 调用其 {@linkplain #setSinks(List)} 方法, 然后通过子 subcontext (上下文) 配置该方法.
   * 配置完成后, 此 Selector 的生命周期与 Sink Processor 的生命周期相关联.
   * </p>
   * <p>
   * At runtime, the processor invokes the {@link #createSinkIterator()}
   * method for every <tt>process</tt> call to create an iteration order over
   * the available sinks. The processor then loops through this iteration order
   * until one of the sinks succeeds in processing the event. If the iterator
   * is exhausted and none of the sinks succeed, the processor will raise
   * an <tt>EventDeliveryException</tt>.
   * </p>
   * <p>
   * 在运行时, Processor 为每个 <tt>process</tt> 调用调用 {@link #createSinkIterator()} 方法, 以在可用的 sinks 上创建迭代顺序.
   * 然后 Processor 循环遍历该迭代顺序, 直到其中一个 Sink 成功处理该 event。 如果迭代器耗尽且没有任何 sinks 成功, 则 Processor 将引发 <tt>EventDeliveryException</tt>.
   * </p>
   */
  public interface SinkSelector extends Configurable, LifecycleAware {

    // 设置当前 Sink Selector 的 sinks 列表
    void setSinks(List<Sink> sinks);

    // 获取 Sink 迭代器, 不同 Selector 会根据自身的选择策略返回当前所选择的 sinks 列表, sinks 列表 (包括顺序) 由 SpecificOrderIterator 对象封装
    Iterator<Sink> createSinkIterator();

    // 对异常的 failedSink 进行处理, 以便可以将其 backed off (退位).
    void informSinkFailed(Sink failedSink);
  }

  /**
   * <p>A sink selector that implements the round-robin sink selection policy.
   * This implementation is not MT safe.</p>
   * <p>Sink Selector: 实现 round-robin sink 选择策略. 这种实现不是 MT 安全的.</p>
   *
   * <p>Unfortunately both implementations need to override the base implementation
   * in AbstractSinkSelector class, because any custom sink selectors
   * will break if this stuff is moved to that class.</p>
   * <p>遗憾的是, 两个实现都需要覆盖 AbstractSinkSelector 类中的基本实现, 因为如果将这些内容移动到该类, 任何自定义 sink selectors 都将中断.</p>
   */
  private static class RoundRobinSinkSelector extends AbstractSinkSelector {
    // 用于控制具体排序逻辑的 Selector 工具
    private OrderSelector<Sink> selector;

    /**
     * 构造方法, OrderSelector 初始化为 RoundRobinOrderSelector 对象实现
     */
    RoundRobinSinkSelector(boolean backoff) {
      selector = new RoundRobinOrderSelector<Sink>(backoff);
    }

    /**
     * 重写 AbstractSinkSelector 的 configure(c) 方法
     * 主要用于设置当前成员变量 OrderSelector 对应的 maxTimeOut 参数
     */
    @Override
    public void configure(Context context) {
      super.configure(context);
      if (maxTimeOut != 0) {
        selector.setMaxTimeOut(maxTimeOut);
      }
    }

    /**
     * 重写 OrderSelector 的 createSinkIterator() 方法
     * 具体执行 RoundRobinOrderSelector 的 createIterator() 方法逻辑
     */
    @Override
    public Iterator<Sink> createSinkIterator() {
      return selector.createIterator();
    }

    /**
     * 重写 SinkSelector 的 setSinks(ss) 方法
     * 具体执行 OrderSelector 的 setObjects(ss) 方法逻辑
     */
    @Override
    public void setSinks(List<Sink> sinks) {
      selector.setObjects(sinks);
    }

    /**
     * 重写 OrderSelector 的 informSinkFailed(f) 方法
     * 具体执行 RoundRobinOrderSelector 的 informSinkFailed(f) 方法逻辑
     */
    @Override
    public void informSinkFailed(Sink failedSink) {
      selector.informFailure(failedSink);
    }

  }

  /**
   * A sink selector that implements a random sink selection policy. This
   * implementation is not thread safe.
   * Sink Selector: 用于实现 random Sink 选择策略. 此实现不是线程安全的.
   */
  private static class RandomOrderSinkSelector extends AbstractSinkSelector {

    // 用于控制具体排序逻辑的 Selector 工具
    private OrderSelector<Sink> selector;

    /**
     * 构造方法, OrderSelector 初始化为 RandomOrderSelector 对象实现
     */
    RandomOrderSinkSelector(boolean backoff) {
      selector = new RandomOrderSelector<Sink>(backoff);
    }

    /**
     * 重写 AbstractSinkSelector 的 configure(c) 方法
     * 主要用于设置当前成员变量 OrderSelector 对应的 maxTimeOut 参数
     */
    @Override
    public void configure(Context context) {
      super.configure(context);
      if (maxTimeOut != 0) {
        selector.setMaxTimeOut(maxTimeOut);
      }
    }

    /**
     * 重写 SinkSelector 的 setSinks(ss) 方法
     * 具体执行 OrderSelector 的 setObjects(ss) 方法逻辑
     */
    @Override
    public void setSinks(List<Sink> sinks) {
      selector.setObjects(sinks);
    }

    /**
     * 重写 OrderSelector 的 createSinkIterator() 方法
     * 具体执行 RandomOrderSelector 的 createIterator() 方法逻辑
     */
    @Override
    public Iterator<Sink> createSinkIterator() {
      return selector.createIterator();
    }

    /**
     * 重写 OrderSelector 的 informSinkFailed(f) 方法
     * 具体执行 RandomOrderSelector 的 informSinkFailed(f) 方法逻辑
     */
    @Override
    public void informSinkFailed(Sink failedSink) {
      selector.informFailure(failedSink);
    }
  }
}
