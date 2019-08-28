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
 * @param <T> - The class on which ordering is to be done
 * @param <T> - 要进行排序的类 (要在哪个类上完成排序)
 */
public abstract class OrderSelector<T> {

  private static final int EXP_BACKOFF_COUNTER_LIMIT = 16;
  private static final long CONSIDER_SEQUENTIAL_RANGE = TimeUnit.HOURS.toMillis(1);
  private static final long MAX_TIMEOUT = 30000L;
  private final Map<T, FailureState> stateMap =
          new LinkedHashMap<T, FailureState>();
  private long maxTimeout = MAX_TIMEOUT;
  private final boolean shouldBackOff;

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
    if (!shouldBackOff) {
      return;
    }
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
   * @return - 当前活动对象的索引列表
   */
  protected List<Integer> getIndexList() {
    long now = System.currentTimeMillis();

    List<Integer> indexList = new ArrayList<Integer>();

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
    long lastFail = 0;
    long restoreTime = 0;
    int sequentialFails = 0;
  }
}
