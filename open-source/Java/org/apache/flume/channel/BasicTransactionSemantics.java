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

import com.google.common.base.Preconditions;

/**
 * <p>
 * An implementation of basic {@link Transaction} semantics designed
 * to work in concert with {@link BasicChannelSemantics} to simplify
 * creation of robust {@link Channel} implementations.  This class
 * ensures that each transaction implementation method is called only
 * while the transaction is in the correct state for that method, and
 * only by the thread that created the transaction.  Nested calls to
 * <code>begin()</code> and <code>close()</code> are supported as long
 * as they are balanced.
 * </p>
 * <p>
 * 基本 {@link Transaction} 语义的实现, 旨在与 {@link BasicChannelSemantics} 协同工作, 以简化强健的 {@link Channel} 实现类的创建.
 * 此类确保仅在此 Transaction 处于该方法的正确状态时调用此 Transaction 的每个实现方法, 并且仅由创建此 Transaction 的线程调用.
 * 只要它们是平衡的, 就支持对 <code>begin()</code> 和 <code>close()</code> 的嵌套调用.
 * </p>
 * <p>
 * Subclasses need only implement <code>doPut</code>,
 * <code>doTake</code>, <code>doCommit</code>, and
 * <code>doRollback</code>, and the developer can rest assured that
 * those methods are called only after transaction state preconditions
 * have been properly met.  <code>doBegin</code> and
 * <code>doClose</code> may also be implemented if there is work to be
 * done at those points.
 * </p>
 * <p>
 * 子类只需要实现 <code>doPut</code>, <code>doTake</code>, <code>doCommit</code> 和 <code>doRollback</code> 这些方法, 开发人员可以放心, 那些方法只有在正确满足 Transaction 状态前提条件后才会被调用.
 * 如果 <code>doBegin</code> 和 <code>doClose</code> 有工作要做, 也可以实现.
 * </p>
 * <p>
 * All InterruptedException exceptions thrown from the implementations
 * of the <code>doXXX</code> methods are automatically wrapped to
 * become ChannelExceptions, but only after restoring the interrupted
 * status of the thread so that any subsequent blocking method calls
 * will themselves throw InterruptedException rather than blocking.
 * The exception to this rule is <code>doTake</code>, which simply
 * returns null instead of wrapping and propagating the
 * InterruptedException, though it still first restores the
 * interrupted status of the thread.
 * </p>
 * <p>
 * 从 <code>doXXX</code> 方法的实现抛出的所有 InterruptedException 异常都会自动包装成为 ChannelExceptions, 但仅在恢复线程的中断状态之后, 以便任何后续的阻塞方法调用本身都会抛出 InterruptedException 而不是阻塞.
 * 此规则的例外是 <code>doTake</code>, 它只返回 null 而不是包装和传播 InterruptedException, 尽管它仍然首先恢复线程的中断状态.
 * </p>
 */
public abstract class BasicTransactionSemantics implements Transaction {

  // 当前 Transaction 的状态
  private State state;
  // 当前 Transaction 初始化时的线程 ID, 用于判断后续调用方法的线程是否为初始化时的线程.
  private long initialThreadId;

  // 子类实现具体的 <code>doBegin</code> (可选), <code>doPut</code>, <code>doTake</code>, <code>doCommit</code>, <code>doRollback</code>, <code>doClose</code> (可选) 方法.
  protected void doBegin() throws InterruptedException {}
  protected abstract void doPut(Event event) throws InterruptedException;
  protected abstract Event doTake() throws InterruptedException;
  protected abstract void doCommit() throws InterruptedException;
  protected abstract void doRollback() throws InterruptedException;
  protected void doClose() {}

  /**
   * 构造方法
   */
  protected BasicTransactionSemantics() {
    // 设置当前状态为 State.NEW
    state = State.NEW;
    // 记录当前 Transaction 初始化时的线程 ID
    initialThreadId = Thread.currentThread().getId();
  }

  /**
   * <p>
   * The method to which {@link BasicChannelSemantics} delegates calls
   * to <code>put</code>.
   * </p>
   * <p>
   * {@link BasicChannelSemantics} 委托调用 <code>put</code> 的方法.
   * </p>
   */
  protected void put(Event event) {
    // 调用此方法的线程必须为初始化时的线程
    Preconditions.checkState(Thread.currentThread().getId() == initialThreadId,
        "put() called from different thread than getTransaction()!");
    // 当前状态必须为 State.OPEN
    Preconditions.checkState(state.equals(State.OPEN),
        "put() called when transaction is %s!", state);
    Preconditions.checkArgument(event != null,
        "put() called with null event!");

    try {
      // 调用子类的具体实现
      doPut(event);
    } catch (InterruptedException e) {
      // 设置当前线程为中断状态, 然后包装成 ChannelException 抛出
      Thread.currentThread().interrupt();
      throw new ChannelException(e.toString(), e);
    }
  }

  /**
   * <p>
   * The method to which {@link BasicChannelSemantics} delegates calls
   * to <code>take</code>.
   * </p>
   * <p>
   * {@link BasicChannelSemantics} 委托调用 <code>take</code> 的方法.
   * </p>
   */
  protected Event take() {
    // 调用此方法的线程必须为初始化时的线程
    Preconditions.checkState(Thread.currentThread().getId() == initialThreadId,
        "take() called from different thread than getTransaction()!");
    // 当前状态必须为 State.OPEN
    Preconditions.checkState(state.equals(State.OPEN),
        "take() called when transaction is %s!", state);

    try {
      // 调用子类的具体实现
      return doTake();
    } catch (InterruptedException e) {
      // 设置当前线程为中断状态
      Thread.currentThread().interrupt();
      return null;
    }
  }

  /**
   * @return the current state of the transaction
   * @return Transaction 的当前状态
   */
  protected State getState() {
    return state;
  }

  /**
   * 实现 Transaction 接口的 begin() 方法.
   */
  @Override
  public void begin() {
    // 调用此方法的线程必须为初始化时的线程
    Preconditions.checkState(Thread.currentThread().getId() == initialThreadId,
        "begin() called from different thread than getTransaction()!");
    // 当前状态必须为 State.NEW
    Preconditions.checkState(state.equals(State.NEW),
        "begin() called when transaction is " + state + "!");

    try {
      // 调用子类的具体实现 (若已实现)
      doBegin();
    } catch (InterruptedException e) {
      // 设置当前线程为中断状态, 然后包装成 ChannelException 抛出
      Thread.currentThread().interrupt();
      throw new ChannelException(e.toString(), e);
    }
    // 设置当前状态为 State.OPEN
    state = State.OPEN;
  }

  /**
   * 实现 Transaction 接口的 commit() 方法.
   */
  @Override
  public void commit() {
    // 调用此方法的线程必须为初始化时的线程
    Preconditions.checkState(Thread.currentThread().getId() == initialThreadId,
        "commit() called from different thread than getTransaction()!");
    // 当前状态必须为 State.OPEN
    Preconditions.checkState(state.equals(State.OPEN),
        "commit() called when transaction is %s!", state);

    try {
      // 调用子类的具体实现
      doCommit();
    } catch (InterruptedException e) {
      // 设置当前线程为中断状态, 然后包装成 ChannelException 抛出
      Thread.currentThread().interrupt();
      throw new ChannelException(e.toString(), e);
    }
    // 设置当前状态为 State.COMPLETED
    state = State.COMPLETED;
  }

  /**
   * 实现 Transaction 接口的 rollback() 方法.
   */
  @Override
  public void rollback() {
    // 调用此方法的线程必须为初始化时的线程
    Preconditions.checkState(Thread.currentThread().getId() == initialThreadId,
        "rollback() called from different thread than getTransaction()!");
    // 当前状态必须为 State.OPEN
    Preconditions.checkState(state.equals(State.OPEN),
        "rollback() called when transaction is %s!", state);

    state = State.COMPLETED;
    try {
      // 调用子类的具体实现
      doRollback();
    } catch (InterruptedException e) {
      // 设置当前线程为中断状态, 然后包装成 ChannelException 抛出
      Thread.currentThread().interrupt();
      throw new ChannelException(e.toString(), e);
    }
  }

  /**
   * 实现 Transaction 接口的 close() 方法.
   */
  @Override
  public void close() {
    // 调用此方法的线程必须为初始化时的线程
    Preconditions.checkState(Thread.currentThread().getId() == initialThreadId,
        "close() called from different thread than getTransaction()!");
    // 当前状态必须为 State.NEW 或 State.COMPLETED
    Preconditions.checkState(
            state.equals(State.NEW) || state.equals(State.COMPLETED),
            "close() called when transaction is %s"
            + " - you must either commit or rollback first", state);

    // 设置当前状态为 State.CLOSED
    state = State.CLOSED;
    // 调用子类的具体实现 (若已实现)
    doClose();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("BasicTransactionSemantics: {");
    builder.append(" state:").append(state);
    builder.append(" initialThreadId:").append(initialThreadId);
    builder.append(" }");
    return builder.toString();
  }

  /**
   * <p>
   * The state of the {@link Transaction} to which it belongs.
   * 所属的 {@link Transaction} 的状态.
   * </p>
   * <dl>
   * <dt>NEW</dt>
   * <dd>A newly created transaction that has not yet begun.</dd>
   * <dd>尚未开始的新创建的 Transaction.</dd>
   * <dt>OPEN</dt>
   * <dd>A transaction that is open. It is permissible to commit or rollback.
   * </dd>
   * <dd>已打开的 Transaction. 允许提交或回滚.</dd>
   * <dt>COMPLETED</dt>
   * <dd>This transaction has been committed or rolled back. It is illegal to
   * perform any further operations beyond closing it.</dd>
   * <dd>此 Transaction 已提交或回滚. 在关闭之后执行任何进一步的操作是违法的.</dd>
   * <dt>CLOSED</dt>
   * <dd>A closed transaction. No further operations are permitted.</dd>
   * <dd>已关闭的 Transaction. 不允许执行进一步操作.</dd>
   * </dl>
   */
  protected static enum State {
    NEW, OPEN, COMPLETED, CLOSED
  }
}
