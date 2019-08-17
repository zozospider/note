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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.apache.flume.ChannelException;
import org.apache.flume.ChannelFullException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.annotations.InterfaceAudience;
import org.apache.flume.annotations.InterfaceStability;
import org.apache.flume.annotations.Recyclable;
import org.apache.flume.conf.TransactionCapacitySupported;
import org.apache.flume.instrumentation.ChannelCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.GuardedBy;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * MemoryChannel is the recommended channel to use when speeds which
 * writing to disk is impractical is required or durability of data is not
 * required.
 * </p>
 * <p>
 * 当写磁盘的速度不行或不需要数据持久化时, 建议使用 MemoryChannel.
 * </p>
 * <p>
 * Additionally, MemoryChannel should be used when a channel is required for
 * unit testing purposes.
 * </p>
 * <p>
 * 此外, 当单元测试需要 Channel 时, 应使用 MemoryChannel.
 * </p>
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
@Recyclable
public class MemoryChannel extends BasicChannelSemantics implements TransactionCapacitySupported {
  private static Logger LOGGER = LoggerFactory.getLogger(MemoryChannel.class);
  // capacity 默认值
  private static final Integer defaultCapacity = 100;
  // TransCapacity 默认值
  private static final Integer defaultTransCapacity = 100;
  private static final double byteCapacitySlotSize = 100;
  // ByteCapacity 默认值
  private static final Long defaultByteCapacity = (long)(Runtime.getRuntime().maxMemory() * .80);
  // ByteCapacityBufferPercentage 默认值
  private static final Integer defaultByteCapacityBufferPercentage = 20;

  // KeepAlive 默认值
  private static final Integer defaultKeepAlive = 3;

  private class MemoryTransaction extends BasicTransactionSemantics {
    private LinkedBlockingDeque<Event> takeList;
    private LinkedBlockingDeque<Event> putList;
    private final ChannelCounter channelCounter;
    private int putByteCounter = 0;
    private int takeByteCounter = 0;

    public MemoryTransaction(int transCapacity, ChannelCounter counter) {
      putList = new LinkedBlockingDeque<Event>(transCapacity);
      takeList = new LinkedBlockingDeque<Event>(transCapacity);

      channelCounter = counter;
    }

    @Override
    protected void doPut(Event event) throws InterruptedException {
      channelCounter.incrementEventPutAttemptCount();
      int eventByteSize = (int) Math.ceil(estimateEventSize(event) / byteCapacitySlotSize);

      if (!putList.offer(event)) {
        throw new ChannelException(
            "Put queue for MemoryTransaction of capacity " +
            putList.size() + " full, consider committing more frequently, " +
            "increasing capacity or increasing thread count");
      }
      putByteCounter += eventByteSize;
    }

    @Override
    protected Event doTake() throws InterruptedException {
      channelCounter.incrementEventTakeAttemptCount();
      if (takeList.remainingCapacity() == 0) {
        throw new ChannelException("Take list for MemoryTransaction, capacity " +
            takeList.size() + " full, consider committing more frequently, " +
            "increasing capacity, or increasing thread count");
      }
      if (!queueStored.tryAcquire(keepAlive, TimeUnit.SECONDS)) {
        return null;
      }
      Event event;
      // 调整大小期间, 通过 queueLock 锁定保护 queue
      synchronized (queueLock) {
        event = queue.poll();
      }
      Preconditions.checkNotNull(event, "Queue.poll returned NULL despite semaphore " +
          "signalling existence of entry");
      takeList.put(event);

      int eventByteSize = (int) Math.ceil(estimateEventSize(event) / byteCapacitySlotSize);
      takeByteCounter += eventByteSize;

      return event;
    }

    @Override
    protected void doCommit() throws InterruptedException {
      int remainingChange = takeList.size() - putList.size();
      if (remainingChange < 0) {
        if (!bytesRemaining.tryAcquire(putByteCounter, keepAlive, TimeUnit.SECONDS)) {
          throw new ChannelException("Cannot commit transaction. Byte capacity " +
              "allocated to store event body " + byteCapacity * byteCapacitySlotSize +
              "reached. Please increase heap space/byte capacity allocated to " +
              "the channel as the sinks may not be keeping up with the sources");
        }
        if (!queueRemaining.tryAcquire(-remainingChange, keepAlive, TimeUnit.SECONDS)) {
          bytesRemaining.release(putByteCounter);
          throw new ChannelFullException("Space for commit to queue couldn't be acquired." +
              " Sinks are likely not keeping up with sources, or the buffer size is too tight");
        }
      }
      int puts = putList.size();
      int takes = takeList.size();
      // 调整大小期间, 通过 queueLock 锁定保护 queue
      synchronized (queueLock) {
        if (puts > 0) {
          while (!putList.isEmpty()) {
            if (!queue.offer(putList.removeFirst())) {
              throw new RuntimeException("Queue add failed, this shouldn't be able to happen");
            }
          }
        }
        putList.clear();
        takeList.clear();
      }
      bytesRemaining.release(takeByteCounter);
      takeByteCounter = 0;
      putByteCounter = 0;

      queueStored.release(puts);
      if (remainingChange > 0) {
        queueRemaining.release(remainingChange);
      }
      if (puts > 0) {
        channelCounter.addToEventPutSuccessCount(puts);
      }
      if (takes > 0) {
        channelCounter.addToEventTakeSuccessCount(takes);
      }

      channelCounter.setChannelSize(queue.size());
    }

    @Override
    protected void doRollback() {
      int takes = takeList.size();
      // 调整大小期间, 通过 queueLock 锁定保护 queue
      synchronized (queueLock) {
        Preconditions.checkState(queue.remainingCapacity() >= takeList.size(),
            "Not enough space in memory channel " +
            "queue to rollback takes. This should never happen, please report");
        while (!takeList.isEmpty()) {
          queue.addFirst(takeList.removeLast());
        }
        putList.clear();
      }
      putByteCounter = 0;
      takeByteCounter = 0;

      queueStored.release(takes);
      channelCounter.setChannelSize(queue.size());
    }

  }

  // lock to guard queue, mainly needed to keep it locked down during resizes
  // it should never be held through a blocking operation
  // 锁定以保护 queue, 主要是需要在调整大小期间将其锁定, 它永远不应该通过阻塞操作来保持.
  private Object queueLock = new Object();

  // MemoryChannel 通过此 queue (FIFO) 保存 events 在内存中.
  @GuardedBy(value = "queueLock")
  private LinkedBlockingDeque<Event> queue;

  // invariant that tracks the amount of space remaining in the queue(with all uncommitted takeLists deducted)
  // we maintain the remaining permits = queue.remaining - takeList.size()
  // this allows local threads waiting for space in the queue to commit without denying access to the
  // shared lock to threads that would make more space on the queue
  private Semaphore queueRemaining;

  // used to make "reservations" to grab data from the queue.
  // by using this we can block for a while to get data without locking all other threads out
  // like we would if we tried to use a blocking call on queue
  private Semaphore queueStored;

  // maximum items in a transaction queue
  // 一个 Transaction 队列中的最大 item 数量
  // The maximum number of events the channel will take from a source or give to a sink per transaction
  // 每个事务中, Channel 从 Source 获取或提供给 Sink 的最大 events 数.
  private volatile Integer transCapacity;
  // Timeout in seconds for adding or removing an event
  // 添加或删除一个 event 的超时时间 (以秒为单位)
  private volatile int keepAlive;
  // Maximum total bytes of memory allowed as a sum of all events in this channel.
  // The implementation only counts the Event body, which is the reason for providing the byteCapacityBufferPercentage configuration parameter as well.
  // Defaults to a computed value equal to 80% of the maximum memory available to the JVM (i.e. 80% of the -Xmx value passed on the command line).
  // Note that if you have multiple memory channels on a single JVM, and they happen to hold the same physical events (i.e. if you are using a replicating channel selector from a single source) then those event sizes may be double-counted for channel byteCapacity purposes.
  // Setting this value to 0 will cause this value to fall back to a hard internal limit of about 200 GB.
  // 允许的最大内存总字节数, 作为此 Channel 中所有 events 的总和.
  // 该实现仅计算 Event body, 这也是提供 byteCapacityBufferPercentage 配置参数的原因.
  // 默认为等于 JVM 可用最大内存的 80% 的计算值 (即命令行传递的 -Xmx 值的 80％).
  // 请注意, 如果在单个 JVM 上有多个 memory channels, 并且它们碰巧保持相同的物理 events (即, 如果您使用来自单个 Source 的 replicating channel selector), 那么这些 events 大小可能会因为 channel byteCapacity 配置目的而被重复计算.
  // 将此值设置为 0 将导致此值回退到大约 200 GB 的内部硬限制.
  private volatile int byteCapacity;
  private volatile int lastByteCapacity;
  // Defines the percent of buffer between byteCapacity and the estimated total size of all events in the channel, to account for data in headers. See above.
  // 定义 byteCapacity 与 Channel 中所有 events 的估计总大小之间的缓冲区百分比, 以计算 headers 中的数据. 见上文.
  private volatile int byteCapacityBufferPercentage;
  private Semaphore bytesRemaining;
  private ChannelCounter channelCounter;

  public MemoryChannel() {
    super();
  }

  /**
   * Read parameters from context
   * 从上下文中读取参数
   * <li>capacity = type long that defines the total number of events allowed at one time in the queue.
   * <li>capacity = 类型: long, 用于定义队列中一次允许的 events 总数.
   * <li>transactionCapacity = type long that defines the total number of events allowed in one transaction.
   * <li>transactionCapacity = 类型: long, 用于定义一个 transaction 中允许的 events 总数.
   * <li>byteCapacity = type long that defines the max number of bytes used for events in the queue.
   * <li>byteCapacity = 类型: long, 用于定义队列中 events 的最大字节数.
   * <li>byteCapacityBufferPercentage = type int that defines the percent of buffer between byteCapacity and the estimated event size.
   * <li>byteCapacityBufferPercentage = 类型: int, 用于定义 byteCapacity 与估计 event 大小之间的缓冲区百分比.
   * <li>keep-alive = type int that defines the number of second to wait for a queue permit
   * <li>keep-alive = 类型: int, 定义等待队列许可的秒数
   */
  @Override
  public void configure(Context context) {
    Integer capacity = null;
    try {
      // The maximum number of events stored in the channel
      // Channel 中存储的最大 events 数
      capacity = context.getInteger("capacity", defaultCapacity);
    } catch (NumberFormatException e) {
      capacity = defaultCapacity;
      LOGGER.warn("Invalid capacity specified, initializing channel to "
          + "default capacity of {}", defaultCapacity);
    }

    if (capacity <= 0) {
      capacity = defaultCapacity;
      LOGGER.warn("Invalid capacity specified, initializing channel to "
          + "default capacity of {}", defaultCapacity);
    }
    try {
      transCapacity = context.getInteger("transactionCapacity", defaultTransCapacity);
    } catch (NumberFormatException e) {
      transCapacity = defaultTransCapacity;
      LOGGER.warn("Invalid transation capacity specified, initializing channel"
          + " to default capacity of {}", defaultTransCapacity);
    }

    if (transCapacity <= 0) {
      transCapacity = defaultTransCapacity;
      LOGGER.warn("Invalid transation capacity specified, initializing channel"
          + " to default capacity of {}", defaultTransCapacity);
    }
    // transCapacity 不能大于 capacity.
    Preconditions.checkState(transCapacity <= capacity,
        "Transaction Capacity of Memory Channel cannot be higher than " +
            "the capacity.");

    try {
      byteCapacityBufferPercentage = context.getInteger("byteCapacityBufferPercentage",
                                                        defaultByteCapacityBufferPercentage);
    } catch (NumberFormatException e) {
      byteCapacityBufferPercentage = defaultByteCapacityBufferPercentage;
    }

    try {
      // byteCapacity = (byteCapacity * (1 - byteCapacityBufferPercentage * .01)) / byteCapacitySlotSize
      // 假设当前运行内存为: 1908932608 B = 1864192.0 K = 1820.5 M, 取值如下:
      // byteCapacity = ((1908932608 * 0.80) * (1 - 20 * 0.01)) / 100 = 12217168 B = 11930.8 K = 11.7 M
      byteCapacity = (int) ((context.getLong("byteCapacity", defaultByteCapacity).longValue() *
          (1 - byteCapacityBufferPercentage * .01)) / byteCapacitySlotSize);
      // 如果计算结果不合法, 取值如下:
      // byteCapacity = Integer.MAX_VALUE = 2147483647 B = 2097152.0 K = 2048.0 M = 2.0 G
      if (byteCapacity < 1) {
        byteCapacity = Integer.MAX_VALUE;
      }
    } catch (NumberFormatException e) {
      // 异常情况下, 使用默认的 defaultByteCapacity 进行计算, 而非配置文件中的 byteCapacity.
      // 假设当前运行内存为: 1908932608 B = 1864192.0 K = 1820.5 M, 取值如下:
      // byteCapacity = ((1908932608 * 0.80) * (1 - 20 * 0.01)) / 100 = 12217168 B = 11930.8 K = 11.7 M
      byteCapacity = (int) ((defaultByteCapacity * (1 - byteCapacityBufferPercentage * .01)) /
          byteCapacitySlotSize);
    }

    try {
      keepAlive = context.getInteger("keep-alive", defaultKeepAlive);
    } catch (NumberFormatException e) {
      keepAlive = defaultKeepAlive;
    }

    // 如果 queue 不为空
    if (queue != null) {
      try {
        // 
        resizeQueue(capacity);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    } else {
      // 如果 queue 为空
      // 调整大小期间, 通过 queueLock 锁定保护 queue
      synchronized (queueLock) {
        // 则新建一个容量为 capacity 大小的 queue
        queue = new LinkedBlockingDeque<Event>(capacity);
        // 新建一个容量为 capacity 的 Semaphore 用于 queueRemaining (剩余空间 queue 控制)
        queueRemaining = new Semaphore(capacity);
        // 新建一个容量为 0 的 Semaphore 用于 queueStored (存储空间 queue 控制)
        queueStored = new Semaphore(0);
      }
    }

    if (bytesRemaining == null) {
      bytesRemaining = new Semaphore(byteCapacity);
      lastByteCapacity = byteCapacity;
    } else {
      if (byteCapacity > lastByteCapacity) {
        bytesRemaining.release(byteCapacity - lastByteCapacity);
        lastByteCapacity = byteCapacity;
      } else {
        try {
          if (!bytesRemaining.tryAcquire(lastByteCapacity - byteCapacity, keepAlive,
                                         TimeUnit.SECONDS)) {
            LOGGER.warn("Couldn't acquire permits to downsize the byte capacity, resizing has been aborted");
          } else {
            lastByteCapacity = byteCapacity;
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }

    if (channelCounter == null) {
      channelCounter = new ChannelCounter(getName());
    }
  }

  private void resizeQueue(int capacity) throws InterruptedException {
    // 旧的 capacity
    int oldCapacity;
    // 调整大小期间, 通过 queueLock 锁定保护 queue
    synchronized (queueLock) {
      // oldCapacity = queue 的初始化容量, 减去剩余容量.
      oldCapacity = queue.size() + queue.remainingCapacity();
    }

    if (oldCapacity == capacity) {
      return;
    } else if (oldCapacity > capacity) {
      if (!queueRemaining.tryAcquire(oldCapacity - capacity, keepAlive, TimeUnit.SECONDS)) {
        LOGGER.warn("Couldn't acquire permits to downsize the queue, resizing has been aborted");
      } else {
        // // 调整大小期间, 通过 queueLock 锁定保护 queue
        synchronized (queueLock) {
          LinkedBlockingDeque<Event> newQueue = new LinkedBlockingDeque<Event>(capacity);
          newQueue.addAll(queue);
          queue = newQueue;
        }
      }
    } else {
      // 调整大小期间, 通过 queueLock 锁定保护 queue
      synchronized (queueLock) {
        LinkedBlockingDeque<Event> newQueue = new LinkedBlockingDeque<Event>(capacity);
        newQueue.addAll(queue);
        queue = newQueue;
      }
      queueRemaining.release(capacity - oldCapacity);
    }
  }

  @Override
  public synchronized void start() {
    channelCounter.start();
    channelCounter.setChannelSize(queue.size());
    channelCounter.setChannelCapacity(Long.valueOf(
            queue.size() + queue.remainingCapacity()));
    super.start();
  }

  @Override
  public synchronized void stop() {
    channelCounter.setChannelSize(queue.size());
    channelCounter.stop();
    super.stop();
  }

  @Override
  protected BasicTransactionSemantics createTransaction() {
    return new MemoryTransaction(transCapacity, channelCounter);
  }

  private long estimateEventSize(Event event) {
    byte[] body = event.getBody();
    if (body != null && body.length != 0) {
      return body.length;
    }
    //Each event occupies at least 1 slot, so return 1.
    return 1;
  }

  @VisibleForTesting
  int getBytesRemainingValue() {
    return bytesRemaining.availablePermits();
  }

  public long getTransactionCapacity() {
    return transCapacity;
  }
}
