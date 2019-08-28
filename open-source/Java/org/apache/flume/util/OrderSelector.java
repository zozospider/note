/*
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
package org.apache.flume.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A basic implementation of an order selector that implements a simple
 * exponential backoff algorithm. Subclasses can use the same algorithm for
 * backoff by simply overriding <tt>createIterator</tt> method to order the
 * list of active sinks returned by <tt>getIndexList</tt> method. Classes
 * instantiating subclasses of this class are expected to call <tt>informFailure</tt>
 * method when an object passed to this class should be marked as failed and backed off.
 * 实现简单指数级 backoff 算法的 Order Selector 的基本实现.
 * 通过简单地重写 <tt>createIterator</tt> 方法, 子类可以使用相同的算法进行 backoff, 以排序 <tt>getIndexList</tt> 方法返回的 active sinks 列表.
 * 实例化此类的子类的类应该在传递给此类的对象被标记为失败并退回时调用 <tt>informFailure</tt> 方法.
 *
 * When implementing a different backoff algorithm, a subclass should
 * minimally override <tt>informFailure</tt> and <tt>getIndexList</tt> methods.
 * 当实现不同的 backoff 算法时, 子类应该至少重写 <tt>informFailure</tt> 和 <tt>getIndexList</tt> 方法.
 *
 * ps: 排序选择器工具类
 *
 * @param <T> - The class on which ordering is to be done
 * @param <T> - 要进行排序的类 (要在哪个类上完成排序)
 */
public abstract class OrderSelector<T> {

  private static final int EXP_BACKOFF_COUNTER_LIMIT = 16;
  private static final long CONSIDER_SEQUENTIAL_RANGE = TimeUnit.HOURS.toMillis(1);
  private static final long MAX_TIMEOUT = 30000L;
  // key: 要进行排序的对象 T
  // value: 此对象对应的失败状态对象 FailureState
  // 要进行排序的对象 T 与其状态对象的 Map
  private final Map<T, FailureState> stateMap =
          new LinkedHashMap<T, FailureState>();
  private long maxTimeout = MAX_TIMEOUT;
  // 控制当前 OrderSelector 的 informFailure(t) 逻辑是否应该 backOff (不做任何处理)
  private final boolean shouldBackOff;

  // 构造方法, 传入参数: 控制当前 OrderSelector 的 informFailure(t) 逻辑是否应该 backOff (不做任何处理)
  protected OrderSelector(boolean shouldBackOff) {
    this.shouldBackOff = shouldBackOff;
  }

  /**
   * Set the list of objects which this class should return in order.
   * 设置此类应按顺序返回的对象列表.
   * @param objects
   */
  @SuppressWarnings("unchecked")
  public void setObjects(List<T> objects) {
    //Order is the same as the original order.
    // 排序与排序订单相同。

    // 遍历传入的对象 T 列表, 顺序将每个对象 T 和初始化的状态对象放入 stateMap (要进行排序的对象 T 与其状态对象的 Map)
    for (T sink : objects) {
      FailureState state = new FailureState();
      stateMap.put(sink, state);
    }
  }

  /**
   * Get the list of objects to be ordered. This list is in the same order
   * as originally passed in, not in the algorithmically reordered order.
   * 获取要排序的对象列表. 此列表的顺序与最初传入的顺序相同, 而不是按照算法重新排序的顺序排列.
   * @return - list of objects to be ordered.
   * @return - 要排序的对象列表.
   */
  public List<T> getObjects() {
    // 返回所有 key (要进行排序的对象 T), 顺序与 setObjects 的传入参数一致.
    return new ArrayList<T>(stateMap.keySet());
  }

  /**
   *
   * @return - list of algorithmically ordered active sinks
   * @return - 算法排序的 active sinks 列表
   */
  public abstract Iterator<T> createIterator();

  /**
   * Inform this class of the failure of an object so it can be backed off.
   * 将对象的失败通知此类, 以便可以将其 backed off (退位).
   * @param failedObject
   */
  public void informFailure(T failedObject) {
    //If there is no backoff this method is a no-op.
    // 如果没有 backoff, 这种方法就是无操作.
    if (!shouldBackOff) {
      return;
    }
    /**
     * 整体逻辑如下:
     *
     * a. 获取失败对象 T 对应的状态对象 FailureState, 计算上次失败时间和当前失败的时差.
     *
     * b1. 如果本次失败距离上次失败在累计失败期内 (即默认在 1 小时内), 那么会增加 sequentialFails (连续失败的次数).
     * c1. 计算出的 restoreTime (恢复时间, 小于 now 表示可用) 会持续增加直到距离 now 的增量达到最大值.
     * restoreTime = now + Math.min(30000L, 1000 * (1 << 2)) = now + Math.min(30000L, 4000) = now + 4000
     * restoreTime = now + Math.min(30000L, 1000 * (1 << 3)) = now + Math.min(30000L, 8000) = now + 8000
     * restoreTime = now + Math.min(30000L, 1000 * (1 << 4)) = now + Math.min(30000L, 16000) = now + 16000
     * restoreTime = now + Math.min(30000L, 1000 * (1 << 5)) = now + Math.min(30000L, 32000) = now + 30000
     * restoreTime = now + Math.min(30000L, 1000 * (1 << 6)) = now + Math.min(30000L, 64000) = now + 30000
     *
     * b2. 如果本次失败距离上次失败不在累计失败期内 (即超过默认 1 小时), 那么 sequentialFails (连续失败的次数) 重置为 1.
     * c2. 计算出的 restoreTime (恢复时间, 小于 now 表示可用) 会重置距离 now 的增量为固定值.
     * restoreTime = now + Math.min(30000L, 1000 * (1 << 1)) = now + Math.min(30000L, 2000) = now + 2000
     */
    FailureState state = stateMap.get(failedObject);
    long now = System.currentTimeMillis();
    long delta = now - state.lastFail;

    /*
     * When do we increase the backoff period?
     * We basically calculate the time difference between the last failure
     * and the current one. If this failure happened within one hour of the
     * last backoff period getting over, then we increase the timeout,
     * since the object did not recover yet. Else we assume this is a fresh
     * failure and reset the count.
     * 我们什么时候增加 backoff period (回退期)?
     * 我们基本上计算了上次失败和当前失败之间的时差.
     * 如果此失败发生在上次回退期结束后的 1 小时内, 那么我们会增加超时, 因为该对象尚未恢复.
     * 否则我们认为这是一个新的失败, 并重置计数.
     */
    long lastBackoffLength = Math.min(maxTimeout, 1000 * (1 << state.sequentialFails));
    long allowableDiff = lastBackoffLength + CONSIDER_SEQUENTIAL_RANGE;
    if (allowableDiff > delta) {
      if (state.sequentialFails < EXP_BACKOFF_COUNTER_LIMIT) {
        state.sequentialFails++;
      }
    } else {
      state.sequentialFails = 1;
    }
    state.lastFail = now;
    //Depending on the number of sequential failures this component had, delay
    //its restore time. Each time it fails, delay the restore by 1000 ms,
    //until the maxTimeOut is reached.
    // 根据此组件的连续故障数量, 延迟其还原时间. 每次失败时, 将还原延迟 1000 ms, 直到达到 maxTimeOut.
    state.restoreTime = now + Math.min(maxTimeout, 1000 * (1 << state.sequentialFails));
  }

  /**
   *
   * @return - List of indices currently active objects
   * @return - 目前 active objects (对象 T) 对应的索引列表
   */
  protected List<Integer> getIndexList() {
    // 获取当前时间
    long now = System.currentTimeMillis();

    // 记录 active objects (对象 T) 对应的索引列表
    List<Integer> indexList = new ArrayList<Integer>();

    /**
     * 遍历 stateMap (要进行排序的对象 T 与其状态对象的 Map)
     * 如果没有 backoff, 或当前对象 T 对应的 restoreTime (恢复时间, 小于 now 表示可用) < now (即表示当前对象 T 可用), 就将当前索引 i 加入到 active objects (对象 T) 的索引列表并返回
     */
    int i = 0;
    for (T obj : stateMap.keySet()) {
      if (!isShouldBackOff() || stateMap.get(obj).restoreTime < now) {
        indexList.add(i);
      }
      i++;
    }
    return indexList;
  }

  public boolean isShouldBackOff() {
    return shouldBackOff;
  }

  public void setMaxTimeOut(long timeout) {
    this.maxTimeout = timeout;
  }

  public long getMaxTimeOut() {
    return this.maxTimeout;
  }

  private static class FailureState {
    // 上次失败时间
    long lastFail = 0;
    // 恢复时间, 小于 now 表示可用
    long restoreTime = 0;
    // 连续失败的次数
    int sequentialFails = 0;
  }
}
