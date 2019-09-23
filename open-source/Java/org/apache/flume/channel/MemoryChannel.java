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
    // 当 Channel 调用 1 次或多次 take 方法时, 每次从 Channel 的 queue 中取出 1 个 event 加入到 takeList 作为缓存 (此时数据已从 Channel 取出), 并返回该 event 给调用者.
    // 当 Channel 调用 1 次 commit 方法时 (表示 sink take 1 个或多个 events 逻辑无异常), 会将 takeList 清空 (下次 Transaction 重新缓存).
    // 当 Channel 调用 1 次 rollback 方法时 (表示 sink take 1 个或多个 events 逻辑有异常), 将 takeList 中的 1 个或多个 events 放回到 Channel 的 queue (此时数据已放回到 Channel), 然后清空 takeList (下次 Transaction 重新缓存).
    private LinkedBlockingDeque<Event> takeList;
    // 当 Channel 调用 1 次或多次 put 方法时, 每次将 1 个 event 加入到 putList 作为缓存 (此时数据未加入 Channel).
    // 当 Channel 调用 1 次 commit 方法时 (表示 Channel put 1 个或多个 events 逻辑无异常), 会将 putList 中的 1 个或多个 events 加入到 Channel 的 queue (此时数据已加入 Channel), 然后清空 putList (下次 Transaction 重新缓存).
    // 当 Channel 调用 1 次 rollback 方法时 (表示 Channel put 1 个或多个 events 逻辑有异常), 会将 putList 清空 (下次 Transaction 重新缓存).
    private LinkedBlockingDeque<Event> putList;
    private final ChannelCounter channelCounter;
    // 距离上次 Transaction 提交或回滚到现在, 累计放入 Channel 的 events 的字节数
    private int putByteCounter = 0;
    // 距离上次 Transaction 提交或回滚到现在, 累计从 Channel 中拿走的 events 的字节数
    private int takeByteCounter = 0;

    public MemoryTransaction(int transCapacity, ChannelCounter counter) {
      putList = new LinkedBlockingDeque<Event>(transCapacity);
      takeList = new LinkedBlockingDeque<Event>(transCapacity);

      channelCounter = counter;
    }

    @Override
    protected void doPut(Event event) throws InterruptedException {
      channelCounter.incrementEventPutAttemptCount();
      // 计算当前 event 字节数
      int eventByteSize = (int) Math.ceil(estimateEventSize(event) / byteCapacitySlotSize);

      // 将 1 个 event 放入到 putList 的尾部 (非阻塞), 若成功返回 true, 若队列已满返回 false.
      if (!putList.offer(event)) {
        throw new ChannelException(
            "Put queue for MemoryTransaction of capacity " +
            putList.size() + " full, consider committing more frequently, " +
            "increasing capacity or increasing thread count");
      }
      // putByteCounter 累加
      putByteCounter += eventByteSize;
    }

    @Override
    protected Event doTake() throws InterruptedException {
      channelCounter.incrementEventTakeAttemptCount();
      // 若 takeList 无剩余空间, 则无法缓存更多数据, 抛出异常
      if (takeList.remainingCapacity() == 0) {
        throw new ChannelException("Take list for MemoryTransaction, capacity " +
            takeList.size() + " full, consider committing more frequently, " +
            "increasing capacity, or increasing thread count");
      }
      // 在 keepAlive 时间内尝试从 queueStored (控制 queue 中已存储的 events 个数的信号量) 中获取 1 个许可
      // 如果在 keepAlive 时间内获取成功, 返回 true, 否则返回 false (不会一直阻塞)
      // 如果获取成功, queueStored (控制 queue 中已存储的 events 个数的信号量) 的容量会减少 1 个 (即 queue 中已存储的 events 个数减少), 即距离上次 Transaction 提交或回滚到现在, Channel 有 put 至少 1 个数据并 commit (在 commit 中调用 queueStored.release() 方法).
      // 如果获取失败, 代表 queueStored 没有容量可用 (即 queue 中已存储的 events 个数小于 1), 即距离上次 Transaction 提交或回滚到现在, Channel 没有 put 任何数据并 commit (在 commit 中调用 queueStored.release() 方法).
      if (!queueStored.tryAcquire(keepAlive, TimeUnit.SECONDS)) {
        return null;
      }
      Event event;
      // 调整大小期间, 通过 queueLock 锁定保护 queue
      synchronized (queueLock) {
        // 移除并返问 queue 头部的一个 event (非阻塞) (此时数据已从 Channel 取出), 没有则返回 null
        event = queue.poll();
      }
      Preconditions.checkNotNull(event, "Queue.poll returned NULL despite semaphore " +
          "signalling existence of entry");
      // 加入到 takeList 作为缓存
      takeList.put(event);

      // 计算当前 event 字节数
      int eventByteSize = (int) Math.ceil(estimateEventSize(event) / byteCapacitySlotSize);
      // takeByteCounter 累加
      takeByteCounter += eventByteSize;

      // 返回该 event 给调用者
      return event;
    }

    @Override
    protected void doCommit() throws InterruptedException {
      // 临时存储中的 takeList 缓存和 putList 缓存差额
      int remainingChange = takeList.size() - putList.size();
      // takeList < putList, 表示距离上次 Transaction 提交或回滚到现在, 已从 Channel 的 queue 中拿出的 events 个数 < 放入 putList 的 events 的个数 (即将放入 Channel 的 queue 中), 即本次 commit 即将造成 Channel 的 queue 容量增加
      if (remainingChange < 0) {
        // 在 keepAlive 时间内尝试从 bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量) 中获取 putByteCounter 个许可
        // 如果在 keepAlive 时间内获取成功, 返回 true, 否则返回 false (不会一直阻塞)
        // 如果获取成功, bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量) 的容量会减少 putByteCounter 个 (即 queue 中剩余可用的 events 字节数减少).
        // 如果获取失败, 抛出异常
        if (!bytesRemaining.tryAcquire(putByteCounter, keepAlive, TimeUnit.SECONDS)) {
          throw new ChannelException("Cannot commit transaction. Byte capacity " +
              "allocated to store event body " + byteCapacity * byteCapacitySlotSize +
              "reached. Please increase heap space/byte capacity allocated to " +
              "the channel as the sinks may not be keeping up with the sources");
        }
        // 如果获取成功, 继续下面逻辑
        // 在 keepAlive 时间内尝试从 queueRemaining (控制 queue 中剩余可用的 events 个数的信号量) 中获取 -remainingChange 个许可
        // 如果在 keepAlive 时间内获取成功, 返回 true, 否则返回 false (不会一直阻塞)
        // 如果获取成功, queueRemaining (控制 queue 中剩余可用的 events 个数的信号量) 的容量会减少 |-remainingChange| 个 (即 queue 中剩余可用的 events 个数减少).
        // 如果获取失败, 则回退之前逻辑 (释放恢复 bytesRemaining 容量)
        if (!queueRemaining.tryAcquire(-remainingChange, keepAlive, TimeUnit.SECONDS)) {
          // 回退之前逻辑 (释放恢复 bytesRemaining 容量)
          bytesRemaining.release(putByteCounter);
          throw new ChannelFullException("Space for commit to queue couldn't be acquired." +
              " Sinks are likely not keeping up with sources, or the buffer size is too tight");
        }
      }
      int puts = putList.size();
      int takes = takeList.size();
      // 调整大小期间, 通过 queueLock 锁定保护 queue
      synchronized (queueLock) {
        // 距离上次 Transaction 提交或回滚到现在, 存在 put 行为
        if (puts > 0) {
          // 循环将 putList 中的每个元素取出, 放入到 queue 的尾部 (非阻塞), 如果有至少 1 个加入失败, 则抛出异常
          while (!putList.isEmpty()) {
            if (!queue.offer(putList.removeFirst())) {
              throw new RuntimeException("Queue add failed, this shouldn't be able to happen");
            }
          }
        }
        // 将 putList 和 takeList 清空 (下次 Transaction 重新缓存)
        putList.clear();
        takeList.clear();
      }
      // 从 bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量) 中释放 takeByteCounter 个许可
      // bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量) 的容量会增加 takeByteCounter 个 (即 queue 中剩余可用的 events 字节数增加).
      bytesRemaining.release(takeByteCounter);
      // 将 takeByteCounter 和 putByteCounter 清空 (下次 Transaction 重新缓存)
      takeByteCounter = 0;
      putByteCounter = 0;

      // 从 queueStored (控制 queue 中已存储的 events 个数的信号量) 中释放 putList.size() 个许可 (一个线程调用 release() 之前并不要求一定要调用了 acquire).
      // queueStored (控制 queue 中已存储的 events 个数的信号量) 容量会增加 putList.size() 个 (即 queue 中已存储的 events 个数增加) (此后 Channel 调用 take 方法时, 可成功从 queueStored 中获取到许可).
      queueStored.release(puts);
      // takeList > putList, 表示距离上次 Transaction 提交或回滚到现在, 已从 Channel 的 queue 中拿出的 events 个数 > 放入 putList 的 events 的个数 (即将放入 Channel 的 queue 中), 即本次 commit 即将造成 Channel 的 queue 容量减少
      if (remainingChange > 0) {
        // 从 queueRemaining (控制 queue 中剩余可用的 events 个数的信号量) 中释放 remainingChange 个许可
        // queueRemaining (控制 queue 中剩余可用的 events 个数的信号量) 的容量会增加 remainingChange 个 (即 queue 中剩余可用的 events 个数增加).
        queueRemaining.release(remainingChange);
      }
      // 距离上次 Transaction 提交或回滚到现在, 存在 put 行为
      if (puts > 0) {
        channelCounter.addToEventPutSuccessCount(puts);
      }
      // 距离上次 Transaction 提交或回滚到现在, 存在 take 行为
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
        // 循环将 takeList 中的每个元素取出, 放入到 queue 的尾部 (即回退调用 take 方法时从 queue 中取出的 events), 当 queue 满时候, 会抛出 IllegalStateException("Queue full") 异常
        while (!takeList.isEmpty()) {
          queue.addFirst(takeList.removeLast());
        }
        // 将 putList 清空 (下次 Transaction 重新缓存)
        putList.clear();
      }
      // 将 putByteCounter 和 takeByteCounter 清空 (下次 Transaction 重新缓存)
      putByteCounter = 0;
      takeByteCounter = 0;

      // 从 queueStored (控制 queue 中已存储的 events 个数的信号量) 中释放 takeList.size() 个许可 (即回退调用 take 方法时从 queueStored 中存储的个数).
      // queueStored (控制 queue 中已存储的 events 个数的信号量) 的容量会增加 takeList.size() 个 (即 queue 中已存储的 events 个数增加).
      queueStored.release(takes);
      channelCounter.setChannelSize(queue.size());
    }

  }

  // lock to guard queue, mainly needed to keep it locked down during resizes
  // it should never be held through a blocking operation
  // 锁定以保护 queue, 主要是需要在调整大小期间将其锁定, 它永远不应该通过阻塞操作来保持.
  private Object queueLock = new Object();

  // MemoryChannel 通过此 queue (FIFO) 保存 events 在内存中.
  // ps: 详情见 LinkedBlockingDeque 类的使用.
  @GuardedBy(value = "queueLock")
  private LinkedBlockingDeque<Event> queue;

  // invariant that tracks the amount of space remaining in the queue(with all uncommitted takeLists deducted)
  // we maintain the remaining permits = queue.remaining - takeList.size()
  // this allows local threads waiting for space in the queue to commit without denying access to the
  // shared lock to threads that would make more space on the queue
  // 固定跟踪 queue 中剩余的空间量 (扣除所有未提交的 takeList)
  // 我们维护了 剩余的许可 = queue.remaining - takeList.size()
  // 这允许本地线程等待 queue 上的空间去提交, 而不需要访问对线程的共享锁定 (会在 queue 上占用更多空间)
  // ps: 控制 queue 中剩余可用的 events 个数的信号量 (Channel 调用 put 并 commit 会导致容量减少, 调用 take 并 commit 会导致容量增加)
  // ps: 详情见 Semaphore 类的使用
  private Semaphore queueRemaining;

  // used to make "reservations" to grab data from the queue.
  // by using this we can block for a while to get data without locking all other threads out
  // like we would if we tried to use a blocking call on queue
  // 用于进行 "保留" 以从队列中获取数据.
  // 通过使用这个变量, 我们可以在获取数据的时候阻塞一段时间, 但是不需要锁定所有其他线程 (就像我们尝试在 queue 上使用阻塞调用时一样, ps: LinkedBlockingDeque 会阻塞所有线程调用)
  // queueStored 在 Channel 调用 1 次 commit 方法时, 会释放 putList.size() 个许可 (表示有 putList.size() 的容量)
  // queueStored 在 Channel 调用 1 次 take 方法时, 会请求获取 1 个许可 (表示已有容量减少 1 个, 如果获取失败, 表示没有容量, 即距离上次 Transaction 提交或回滚到现在, Channel 没有 put 任何数据并 commit)
  // ps: 控制 queue 中已存储的 events 个数的信号量 (Channel 调用 put 并 commit 会导致容量增加, 调用 take 会导致容量减少)
  // ps: 详情见 Semaphore 类的使用
  private Semaphore queueStored;

  // maximum items in a transaction queue
  // 一个 Transaction 队列中的最大 item 个数
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
  // 定义 byteCapacity 与 Channel 中所有 events 的估计总大小之间的缓冲区百分比, 以计算 headers 中的数据. 见上文 byteCapacity 说明.
  private volatile int byteCapacityBufferPercentage;
  // ps: 控制 queue 中剩余可用的 events 字节数的信号量 (Channel 调用 put 并 commit 会导致容量减少, 调用 take 并 commit 会导致容量增加)
  // ps: 详情见 Semaphore 类的使用
  private Semaphore bytesRemaining;
  private ChannelCounter channelCounter;

  public MemoryChannel() {
    super();
  }

  /**
   * Read parameters from context
   * 从上下文中读取参数
   * <li>capacity = type long that defines the total number of events allowed at one time in the queue.
   * <li>capacity = 类型: long, 用于定义 queue 中一次允许的 events 总数.
   * <li>transactionCapacity = type long that defines the total number of events allowed in one transaction.
   * <li>transactionCapacity = 类型: long, 用于定义一个 transaction 中允许的 events 总数.
   * <li>byteCapacity = type long that defines the max number of bytes used for events in the queue.
   * <li>byteCapacity = 类型: long, 用于定义 queue 中 events 的最大字节数.
   * <li>byteCapacityBufferPercentage = type int that defines the percent of buffer between byteCapacity and the estimated event size.
   * <li>byteCapacityBufferPercentage = 类型: int, 用于定义 byteCapacity 与估计 event 大小之间的缓冲区百分比.
   * <li>keep-alive = type int that defines the number of second to wait for a queue permit
   * <li>keep-alive = 类型: int, 定义等待 queue 许可的秒数
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

    // 根据 queue 是否为空 (即是否重新启动了 Flume 或重新加载配置文件) 和新旧 capacity 对比, 来调整 queue 和 queueRemaining (控制 queue 中剩余可用的 events 个数的信号量)
    // 如果 queue 不为空
    if (queue != null) {
      try {
        // 重新调整 queue 和 queueRemaining (控制 queue 中剩余可用的 events 个数的信号量)
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
        // 新建一个容量为 capacity 的 Semaphore, 即 queueRemaining (控制 queue 中剩余可用的 events 个数的信号量)
        queueRemaining = new Semaphore(capacity);
        // 新建一个容量为 0 的 Semaphore, 即 queueStored (控制 queue 中已存储的 events 个数的信号量)
        queueStored = new Semaphore(0);
      }
    }

    // 根据 bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量) 是否为空 (即是否重新启动了 Flume 或重新加载配置文件) 和新旧 byteCapacity 对比, 来调整 byteCapacity 和 bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量)
    // ps: 具体逻辑与上文对 queueRemaining 的处理类似
    if (bytesRemaining == null) {
      // 重新建一个容量为 byteCapacity 的 bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量)
      bytesRemaining = new Semaphore(byteCapacity);
      // 记录最近的 lastByteCapacity
      lastByteCapacity = byteCapacity;
    } else {
      // 如果新 byteCapacity > 旧 byteCapacity
      // 需要释放 bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量) 的许可
      if (byteCapacity > lastByteCapacity) {
        // 从 bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量) 中释放 (byteCapacity - lastByteCapacity) 个许可
        // bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量) 的容量会增加 (byteCapacity - lastByteCapacity) 个 (即 queue 中剩余可用的 events 字节数增加).
        // 表示成功维护了新 byteCapacity 对应的 bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量) 的正确性.
        bytesRemaining.release(byteCapacity - lastByteCapacity);
        // 记录最近的 lastByteCapacity
        lastByteCapacity = byteCapacity;
      // 如果新 byteCapacity < 旧 byteCapacity
      } else {
        try {
          // 在 keepAlive 时间内尝试从 bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量) 中获取 (lastByteCapacity - byteCapacity) 个许可
          // 如果在 keepAlive 时间内获取成功, 返回 true, 否则返回 false (不会一直阻塞)
          // 如果获取失败, 不做任何处理
          if (!bytesRemaining.tryAcquire(lastByteCapacity - byteCapacity, keepAlive,
                                         TimeUnit.SECONDS)) {
            LOGGER.warn("Couldn't acquire permits to downsize the byte capacity, resizing has been aborted");
          // 如果获取成功, bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量) 的容量会减少 (lastByteCapacity - byteCapacity) 个 (即 queue 中剩余可用的 events 字节数减少).
          // 如果获取成功, 则表示成功维护了新 byteCapacity 对应的 bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量) 的正确性.
          // bytesRemaining (控制 queue 中剩余可用的 events 字节数的信号量) 获取成功后, 记录最近的 lastByteCapacity.
          } else {
            // 记录最近的 lastByteCapacity
            lastByteCapacity = byteCapacity;
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }

    // 创建计数器 ChannelCounter(n)
    if (channelCounter == null) {
      channelCounter = new ChannelCounter(getName());
    }
  }

  /**
   * 根据传入的新 capacity, 与旧 capacity 进行对比, 重新调整 queue 的容量, 并将 queueRemaining (控制 queue 中剩余可用的 events 个数的信号量) 调整到新 queue 对应的容量.
   */
  private void resizeQueue(int capacity) throws InterruptedException {
    // 旧 capacity
    int oldCapacity;
    // 调整大小期间, 通过 queueLock 锁定保护 queue
    synchronized (queueLock) {
      // oldCapacity = queue 已用容量 + 剩余容量 = count + capacity - count = capacity (即 queue 的 capacity)
      oldCapacity = queue.size() + queue.remainingCapacity();
    }

    // 如果旧 capacity = 新 capacity, 则不做任何调整
    if (oldCapacity == capacity) {
      return;
    // 如果旧 capacity > 新 capacity
    // 需要尝试获取 queueRemaining (控制 queue 中剩余可用的 events 个数的信号量) 的许可, 并将重建立一个新的 queue.
    } else if (oldCapacity > capacity) {
      // 在 keepAlive 时间内尝试从 queueRemaining (控制 queue 中剩余可用的 events 个数的信号量) 中获取 (oldCapacity - capacity) 个许可
      // 如果在 keepAlive 时间内获取成功, 返回 true, 否则返回 false (不会一直阻塞)
      // 如果获取失败, 不做任何处理
      if (!queueRemaining.tryAcquire(oldCapacity - capacity, keepAlive, TimeUnit.SECONDS)) {
        LOGGER.warn("Couldn't acquire permits to downsize the queue, resizing has been aborted");
      // 如果获取成功, queueRemaining (控制 queue 中剩余可用的 events 个数的信号量) 的容量会减少 (oldCapacity - capacity) 个 (即 queue 中剩余可用的 events 个数减少).
      // 如果获取成功, 则表示成功维护了新 capacity 对应的 queueRemaining (控制 queue 中剩余可用的 events 个数的信号量) 的正确性.
      // queueRemaining (控制 queue 中剩余可用的 events 个数的信号量) 获取成功后, 重新建立一个容量为 capacity 的 queue, 添加旧 queue 的数据并映射到原 queue 地址.
      } else {
        // 调整大小期间, 通过 queueLock 锁定保护 queue
        synchronized (queueLock) {
          // 重新建立一个容量为新 capacity 的 queue, 将旧 queue 中的数据添加到新 queue, 再映射到原地址.
          LinkedBlockingDeque<Event> newQueue = new LinkedBlockingDeque<Event>(capacity);
          newQueue.addAll(queue);
          queue = newQueue;
        }
      }
    // 如果旧 capacity < 新 capacity
    // 需要重建立一个新的 queue, 并释放 queueRemaining (控制 queue 中剩余可用的 events 个数的信号量) 的许可.
    } else {
      // 调整大小期间, 通过 queueLock 锁定保护 queue
      synchronized (queueLock) {
        // 重新建立一个容量为新 capacity 的 queue, 将旧 queue 中的数据添加到新 queue, 再映射到原地址.
        LinkedBlockingDeque<Event> newQueue = new LinkedBlockingDeque<Event>(capacity);
        newQueue.addAll(queue);
        queue = newQueue;
      }
      // 从 queueRemaining (控制 queue 中剩余可用的 events 个数的信号量) 中释放 (capacity - oldCapacity) 个许可
      // queueRemaining (控制 queue 中剩余可用的 events 个数的信号量) 的容量会增加 (capacity - oldCapacity) 个 (即 queue 中剩余可用的 events 个数增加).
      // 表示成功维护了新 capacity 对应的 queueRemaining (控制 queue 中剩余可用的 events 个数的信号量) 的正确性.
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
