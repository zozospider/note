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

import org.apache.flume.Channel;
import org.apache.flume.ChannelException;
import org.apache.flume.Event;
import org.apache.flume.Transaction;
import org.apache.flume.annotations.InterfaceAudience;
import org.apache.flume.annotations.InterfaceStability;

import com.google.common.base.Preconditions;

/**
 * <p>
 * An implementation of basic {@link Channel} semantics, including the
 * implied thread-local semantics of the {@link Transaction} class,
 * which is required to extend {@link BasicTransactionSemantics}.
 * </p>
 * <p>
 * 基本的 {@link Channel} 语义实现, 包括 {@link Transaction} 类的隐含 ThreadLocal 语义, 这是扩展 {@link BasicTransactionSemantics} 所必需的.
 * </p>
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class BasicChannelSemantics extends AbstractChannel {

  // 通过 ThreadLocal 控制每个线程的当前 value (即 transaction)
  // ps: 具有统一管理, 线程内共享一些数据, 避免通过参数来传递, 降低耦合性等优点. 详情见 ThreadLocal 类的使用.
  private ThreadLocal<BasicTransactionSemantics> currentTransaction
      = new ThreadLocal<BasicTransactionSemantics>();

  // 用于初始化的控制变量, 调用 getTransaction() 时用到.
  private boolean initialized = false;

  /**
   * <p>
   * Called upon first getTransaction() request, while synchronized on
   * this {@link Channel} instance.  Use this method to delay the
   * initializization resources until just before the first
   * transaction begins.
   * </p>
   * <p>
   * 在 {@link Channel} 实例上同步时, 首先调用 getTransaction() 请求. 使用此方法将初始化资源延迟到第一个 Transaction 开始之前.
   * </p>
   */
  protected void initialize() {}

  /**
   * <p>
   * Called to create new {@link Transaction} objects, which must
   * extend {@link BasicTransactionSemantics}.  Each object is used
   * for only one transaction, but is stored in a thread-local and
   * retrieved by <code>getTransaction</code> for the duration of that
   * transaction.
   * </p>
   * <p>
   * 调用该方法以创建新的 {@link Transaction} 对象, 这些对象必须继承 {@link BasicTransactionSemantics}.
   * 每一个对象仅用于一个 Transaction, 但存储在 ThreadLocal 中, 并在该 Transaction 期间通过 <code>getTransaction</code> 检索.
   * </p>
   */
  protected abstract BasicTransactionSemantics createTransaction();

  /**
   * <p>
   * Ensures that a transaction exists for this thread and then
   * delegates the <code>put</code> to the thread's {@link
   * BasicTransactionSemantics} instance.
   * </p>
   * <p>
   * 确保此线程存在 Trasaction, 然后将 <code>put</code> 委托给此线程的 {@link BasicTransactionSemantics} 实例.
   * </p>
   */
  @Override
  public void put(Event event) throws ChannelException {
    // 通过 ThreadLocal 获取当前线程的 value (即 transaction). 且确保 value 不能为空.
    BasicTransactionSemantics transaction = currentTransaction.get();
    Preconditions.checkState(transaction != null,
        "No transaction exists for this thread");
    // 调用 transaction 的 <code>put</code> 实现方法.
    transaction.put(event);
  }

  /**
   * <p>
   * Ensures that a transaction exists for this thread and then
   * delegates the <code>take</code> to the thread's {@link
   * BasicTransactionSemantics} instance.
   * </p>
   * <p>
   * 确保此线程存在 Trasaction, 然后将 <code>take</code> 委托给此线程的 {@link BasicTransactionSemantics} 实例.
   * </p>
   */
  @Override
  public Event take() throws ChannelException {
    // 通过 ThreadLocal 获取当前线程的 value (即 transaction). 且确保 value 不能为空.
    BasicTransactionSemantics transaction = currentTransaction.get();
    Preconditions.checkState(transaction != null,
        "No transaction exists for this thread");
    // 调用 transaction 的 <code>take</code> 实现方法.
    return transaction.take();
  }

  /**
   * <p>
   * Initializes the channel if it is not already, then checks to see
   * if there is an open transaction for this thread, creating a new
   * one via <code>createTransaction</code> if not.
   * @return the current <code>Transaction</code> object for the
   *     calling thread
   * </p>
   * <p>
   * 如果尚未初始化 Channel, 则检查该线程是否存在打开的 Transaction, 如果没有, 则通过 <code>createTransaction</code> 创建一个新的 Transaction.
   * @return 调用线程的当前的 <code>Transaction</code> 对象.
   * </p>
   */
  @Override
  public Transaction getTransaction() {

    // 初始化
    if (!initialized) {
      synchronized (this) {
        if (!initialized) {
          initialize();
          initialized = true;
        }
      }
    }

    // 通过 ThreadLocal 获取当前线程的 value (即 transaction).
    // 然后判断当前线程的 value (transaction) 是否存在, 如果不存在或 transaction 已经关闭, 则创建一个新的 {@link Transaction} 对象, 并通过 ThreadLocal 设置到当前线程的 value.
    BasicTransactionSemantics transaction = currentTransaction.get();
    if (transaction == null || transaction.getState().equals(
            BasicTransactionSemantics.State.CLOSED)) {
      transaction = createTransaction();
      currentTransaction.set(transaction);
    }
    return transaction;
  }
}
