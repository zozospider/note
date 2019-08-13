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
package org.apache.flume;

import org.apache.flume.channel.BasicChannelSemantics;
import org.apache.flume.channel.BasicTransactionSemantics;

/**
 * <p>Provides the transaction boundary while accessing a channel.</p>
 * <p>在访问一个 Channel 时, 提供 Transaction 边界.</p>
 * <p>A <tt>Transaction</tt> instance is used to encompass channel access
 * via the following idiom:</p>
 * <p>一个 Transaction 实例用于通过以下习惯用法包含 Channel 访问</p>
 * <pre><code>
 * Channel ch = ...
 * Transaction tx = ch.getTransaction();
 * try {
 *   tx.begin();
 *   ...
 *   // ch.put(event) or ch.take()
 *   ...
 *   tx.commit();
 * } catch (ChannelException ex) {
 *   tx.rollback();
 *   ...
 * } finally {
 *   tx.close();
 * }
 * </code></pre>
 * <p>Depending upon the implementation of the channel, the transaction
 * semantics may be strong, or best-effort only.</p>
 * <p>根据 Channel 的实现, Transaction 语义可能很强, 也可能只是尽力而为.</p>
 *
 * <p>
 * Transactions must be thread safe. To provide  a guarantee of thread safe
 * access to Transactions, see {@link BasicChannelSemantics} and
 * {@link  BasicTransactionSemantics}.
 * Transactions 必须是线程安全的. 要保证线程安全访问 Transactions, 参阅 {@link BasicChannelSemantics} 和 {@link  BasicTransactionSemantics}.
 *
 * @see org.apache.flume.Channel
 */
public interface Transaction {

  enum TransactionState { Started, Committed, RolledBack, Closed }

  /**
   * <p>Starts a transaction boundary for the current channel operation. If a
   * transaction is already in progress, this method will join that transaction
   * using reference counting.</p>
   * <p><strong>Note</strong>: For every invocation of this method there must
   * be a corresponding invocation of {@linkplain #close()} method. Failure
   * to ensure this can lead to dangling transactions and unpredictable results.
   * </p>
   */
  void begin();

  /**
   * Indicates that the transaction can be successfully committed. It is
   * required that a transaction be in progress when this method is invoked.
   */
  void commit();

  /**
   * Indicates that the transaction can must be aborted. It is
   * required that a transaction be in progress when this method is invoked.
   */
  void rollback();

  /**
   * <p>Ends a transaction boundary for the current channel operation. If a
   * transaction is already in progress, this method will join that transaction
   * using reference counting. The transaction is completed only if there
   * are no more references left for this transaction.</p>
   * <p><strong>Note</strong>: For every invocation of this method there must
   * be a corresponding invocation of {@linkplain #begin()} method. Failure
   * to ensure this can lead to dangling transactions and unpredictable results.
   * </p>
   */
  void close();
}
