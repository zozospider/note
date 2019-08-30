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
package org.apache.flume.sink;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Sink;
import org.apache.flume.Sink.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FailoverSinkProcessor maintains a prioritized list of sinks,
 * guarranteeing that so long as one is available events will be processed.
 * FailoverSinkProcessor 维护一个优先级的 sinks 列表, 保证只要有 1 个可用的 event 将被处理.
 *
 * The failover mechanism works by relegating failed sinks to a pool
 * where they are assigned a cooldown period, increasing with sequential
 * failures before they are retried. Once a sink successfully sends an
 * event it is restored to the live pool.
 * 故障转移机制的工作原理是将 failed sinks 降级到池, 在池中为它们分配一个冷却时间段, 在重试之前随顺序故障而增加. Sink 成功发送 event 后, 它将恢复到实时池.
 *
 * FailoverSinkProcessor is in no way thread safe and expects to be run via
 * SinkRunner Additionally, setSinks must be called before configure, and
 * additional sinks cannot be added while running
 * FailoverSinkProcessor 绝不是线程安全的, 并且期望通过 SinkRunner 运行. 此外, 必须在配置之前调用 setSinks, 并且在运行时不能添加其他 sinks
 *
 * To configure, set a sink groups processor to "failover" and set priorities
 * for individual sinks, all priorities must be unique. Furthermore, an
 * upper limit to failover time can be set(in miliseconds) using maxpenalty
 * 要进行配置, 请将 Sink Groups Processor 设置为 "failover" 并为各个 sinks 设置优先级, 所有优先级必须唯一.
 * 此外, 可以使用 maxpenalty 设置故障转移时间的上限 (以毫秒为单位)
 *
 * Ex)
 *
 * host1.sinkgroups = group1
 *
 * host1.sinkgroups.group1.sinks = sink1 sink2
 * host1.sinkgroups.group1.processor.type = failover
 * host1.sinkgroups.group1.processor.priority.sink1 = 5
 * host1.sinkgroups.group1.processor.priority.sink2 = 10
 * host1.sinkgroups.group1.processor.maxpenalty = 10000
 *
 */
public class FailoverSinkProcessor extends AbstractSinkProcessor {
  private static final int FAILURE_PENALTY = 1000;
  private static final int DEFAULT_MAX_PENALTY = 30000;

  /**
   * 在 activeSink 执行 process() 方法异常时, 调用 moveActiveToDeadAndGetNext() 方法将其转换为此对象. 并将此对象加入到 failedSinks (PriorityQueue) 中.
   * 此 FailedSink 实现了 Comparable<T> 接口, 其重写的 compareTo(T) 方法用于 failedSinks (PriorityQueue) 判断优先级.
   */
  private class FailedSink implements Comparable<FailedSink> {
    // 恢复时间 (小于 now 表示可用), 同时用于 failedSinks (PriorityQueue) 判断优先级 (小的优先级高)
    private Long refresh;
    // 用户配置的优先级, 作为 liveSinks (SortedMap) 的 key.
    private Integer priority;
    // 当前 Sink 对象
    private Sink sink;
    // 连续失败的次数
    private Integer sequentialFailures;

    /**
     * 构造方法, 传入的 seqFailures = 1
     */
    public FailedSink(Integer priority, Sink sink, int seqFailures) {
      this.sink = sink;
      this.priority = priority;
      // sequentialFailures = 1
      this.sequentialFailures = seqFailures;
      // 调整 refresh = now + 2000
      adjustRefresh();
    }

    /**
     * 重写 Comparable<T> 接口的 compareTo(T) 方法.
     * failedSinks (PriorityQueue) 通过此方法判断优先级.
     */
    @Override
    public int compareTo(FailedSink arg0) {
      return refresh.compareTo(arg0.refresh);
    }

    public Long getRefresh() {
      return refresh;
    }

    public Sink getSink() {
      return sink;
    }

    public Integer getPriority() {
      return priority;
    }

    /**
     * 失败通知逻辑
     */
    public void incFails() {
      // 增加 sequentialFailures (连续失败的次数)
      sequentialFailures++;
      // 调整 refresh
      adjustRefresh();
      logger.debug("Sink {} failed again, new refresh is at {}, current time {}",
                   new Object[] { sink.getName(), refresh, System.currentTimeMillis() });
    }

    /**
     * 计算出的 refresh (恢复时间, 小于 now 表示可用) 会持续增加直到距离 now 的增量达到最大值.
     * refresh = now + Math.min(30000, (1 << 1) * 1000) = now + Math.min(30000, 2000) = now + 2000
     * refresh = now + Math.min(30000, (1 << 2) * 1000) = now + Math.min(30000, 4000) = now + 4000
     * refresh = now + Math.min(30000, (1 << 3) * 1000) = now + Math.min(30000, 8000) = now + 8000
     * refresh = now + Math.min(30000, (1 << 4) * 1000) = now + Math.min(30000, 16000) = now + 16000
     * refresh = now + Math.min(30000, (1 << 5) * 1000) = now + Math.min(30000, 32000) = now + 30000
     * refresh = now + Math.min(30000, (1 << 6) * 1000) = now + Math.min(30000, 64000) = now + 30000
     */
    private void adjustRefresh() {
      refresh = System.currentTimeMillis()
          + Math.min(maxPenalty, (1 << sequentialFailures) * FAILURE_PENALTY);
    }
  }

  private static final Logger logger = LoggerFactory.getLogger(FailoverSinkProcessor.class);

  private static final String PRIORITY_PREFIX = "priority.";
  private static final String MAX_PENALTY_PREFIX = "maxpenalty";
  // 当前 SinkProcessor 处理的所有 sinks, 通过 setSinks(ss) 方法设置.
  private Map<String, Sink> sinks;
  // 当前活跃的 Sink, 取 liveSinks 中优先级最高的 Sink (调用 liveSinks.get(liveSinks.lastKey()) 获得).
  private Sink activeSink;
  // 记录 liveSinks 的 SortedMap, 默认包含所有配置的 sinks.
  // 在 activeSink 执行 process() 方法失败时, 从此 Map 中移除.
  // 在 failedSinks 循环尝试 process() 方法成功时, 重新加入此 Map.
  private SortedMap<Integer, Sink> liveSinks;
  // 记录 failedSinks 的 Queue, 在 activeSink 执行 process() 方法失败时, 加入到此列表中.
  // 该 Queue 的具体实现为 PriorityQueue<FailedSink> 优先级队列 (通过调用 FailedSink 的 compareTo(f) 方法确定优先级).
  private Queue<FailedSink> failedSinks;
  // The maximum backoff period for the failed Sink (in millis)
  // failed Sink 的最大 backed off 时间 (以毫秒为单位)
  private int maxPenalty;

  @Override
  public void configure(Context context) {
    liveSinks = new TreeMap<Integer, Sink>();
    // 记录 failedSinks 的 Queue, 初始化为 PriorityQueue<FailedSink> 优先级队列 (通过调用 FailedSink 的 compareTo(f) 方法确定优先级).
    failedSinks = new PriorityQueue<FailedSink>();
    Integer nextPrio = 0;
    String maxPenaltyStr = context.getString(MAX_PENALTY_PREFIX);
    if (maxPenaltyStr == null) {
      maxPenalty = DEFAULT_MAX_PENALTY;
    } else {
      try {
        maxPenalty = Integer.parseInt(maxPenaltyStr);
      } catch (NumberFormatException e) {
        logger.warn("{} is not a valid value for {}",
                    new Object[] { maxPenaltyStr, MAX_PENALTY_PREFIX });
        maxPenalty = DEFAULT_MAX_PENALTY;
      }
    }
    /**
     * 将 sinks 的所有元素按照配置的 priority 加入到 liveSinks 中.
     * 然后将 liveSinks 中优先级最高的 Sink 赋值给 activeSink.
     */
    for (Entry<String, Sink> entry : sinks.entrySet()) {
      // priStr = "priority." + "k1" = "priority.k1"
      String priStr = PRIORITY_PREFIX + entry.getKey();
      Integer priority;
      try {
        // 获取当前 Sink 的 priority 配置
        priority =  Integer.parseInt(context.getString(priStr));
      } catch (Exception e) {
        // 异常情况下, 为循环递减负数
        priority = --nextPrio;
      }
      // 加入到 liveSinks (按照 priority 排序的 SortedMap)
      if (!liveSinks.containsKey(priority)) {
        liveSinks.put(priority, sinks.get(entry.getKey()));
      } else {
        // 说明 liveSinks 已包含, 这种情况只在未重新启动 Flume, 但调用了此 configure(c) 方法时有可能发生 (即新加载配置文件而不重启)
        // 但是也不应该发生, 因为本方法上面的逻辑已经重新赋值 liveSinks = new TreeMap<Integer, Sink>();  ???
        logger.warn("Sink {} not added to FailverSinkProcessor as priority" +
            "duplicates that of sink {}", entry.getKey(),
            liveSinks.get(priority));
      }
    }
    // activeSink 取 liveSinks 中优先级最高的 Sink
    activeSink = liveSinks.get(liveSinks.lastKey());
  }

  @Override
  public Status process() throws EventDeliveryException {
    // Retry any failed sinks that have gone through their "cooldown" period
    // 重试已经过 "cooldown" 期的任何失败的 sinks
    /**
     * 在调用 activeSink 逻辑之前, 首先尝试 failedSinks.
     * a. 如果 failedSinks 不为空 (说明之前有 sinks 处理失败, 并加入到 failedSinks 中), 则循环遍历 failedSinks.
     * b. 每次循环按照优先级查看 (不取出) 其中的 FailedSink, 如果当前 FailedSink 的 refresh (恢复时间 (小于 now 表示可用)) < now, 即表示已经过了 back off 期, 可以重新尝试 process().
     * c. 从 failedSinks 中取出当前 FailedSink, 并重新尝试 process().
     * d1. 如果当前 FailedSink 的 process() 方法返回 READY (说明当前 FailedSink 已经恢复, 可以成功处理 events 了), 那么将其加入到 liveSinks 中. 并从 liveSinks 中刷新最大优先级的 activeSink.
     * d2. 如果当前 FailedSink 的 process() 方法返回 BACKOFF (说明当前 FailedSink 已经恢复, 但是处理 events 失败了), 还不成为 liveSinks 中的一员, 重新放回到 failedSinks 中.
     * d3. 如果当前 FailedSink 的 process() 方法发生异常, 则调用 incFails() 方法执行失败通知逻辑, 并重新放回到 failedSinks 中.
     */
    Long now = System.currentTimeMillis();
    // a, b
    while (!failedSinks.isEmpty() && failedSinks.peek().getRefresh() < now) {
      // c
      FailedSink cur = failedSinks.poll();
      Status s;
      try {
        s = cur.getSink().process();
        // d1
        if (s  == Status.READY) {
          liveSinks.put(cur.getPriority(), cur.getSink());
          activeSink = liveSinks.get(liveSinks.lastKey());
          logger.debug("Sink {} was recovered from the fail list",
                  cur.getSink().getName());
        // d2
        } else {
          // if it's a backoff it needn't be penalized.
          // 如果它是 BACKOFF, 它不需要受到惩罚 (即不需要执行 incFails() 失败通知逻辑).
          failedSinks.add(cur);
        }
        return s;
      // d3
      } catch (Exception e) {
        cur.incFails();
        failedSinks.add(cur);
      }
    }

    /**
     * a. 至此, 说明上面尝试多个 failedSinks 均未成功, 则尝试 activeSink 的 process().
     *
     * b1. 如果当前 activeSink 的 process() 方法返回 READY / BACKOFF (说明当前 activeSink 可按照预期逻辑持续处理 events, 且未发生故障), 那么直接返回处理结果.
     * c1. 第一次循环已直接 return 结果, 结束本方法逻辑.
     * 
     * b2. 如果当前 activeSink 的 process() 方法发生异常, 则调用 moveActiveToDeadAndGetNext() 方法, 将当前 activeSink 添加到 failedSinks 中, 并从 liveSinks 中移除, 然后返回 liveSinks 中优先级最高的 Sink.
     * c21. 异常情况下, activeSink 被重新赋值, 如果此对象被赋值为 null (liveSinks 中没有可用的 Sink), 则结束循环, 抛出异常.
     * c22. 如果此对象不为 null, 则继续循环 (直到满足 c1 / c21 条件结束循环).
     */
    Status ret = null;
    // a c22
    while (activeSink != null) {
      try {
        // b1
        ret = activeSink.process();
        // c1
        return ret;
      } catch (Exception e) {
        logger.warn("Sink {} failed and has been sent to failover list",
                activeSink.getName(), e);
        // b2
        activeSink = moveActiveToDeadAndGetNext();
      }
    }

    // c21
    throw new EventDeliveryException("All sinks failed to process, " +
        "nothing left to failover to");
  }

  /**
   * activeSink 的 process() 方法发生异常时调用.
   * 将当前 activeSink 添加到 failedSinks 中, 并从 liveSinks 中移除, 然后返回 liveSinks 中优先级最高的 Sink.
   */
  private Sink moveActiveToDeadAndGetNext() {
    // 此时 liveSinks.lastKey() 即为当前 activeSink 的 priority
    Integer key = liveSinks.lastKey();
    // 将当前 activeSink 添加到 failedSinks 中
    // 添加后内部已按照 FailedSink 的 compareTo(T) 方法逻辑排序各元素 (即按照 key (priority) 优先级排列)
    // priority: key
    // sink: activeSink
    // sequentialFailures: 1
    failedSinks.add(new FailedSink(key, activeSink, 1));
    // 从 liveSinks 中移除
    liveSinks.remove(key);
    // 返回 liveSinks 中优先级最高的 Sink, 如果没有可用的 Sink 则返回 null
    if (liveSinks.isEmpty()) return null;
    if (liveSinks.lastKey() != null) {
      return liveSinks.get(liveSinks.lastKey());
    } else {
      return null;
    }
  }

  /**
   * 重写 SinkProcessor 接口的 setSinks(ss) 接口.
   * 将参数 sinks 的元素逐个 put 到本对象新建的 sinks 变量中, 而不直接引用传入参数.
   */
  @Override
  public void setSinks(List<Sink> sinks) {
    // needed to implement the start/stop functionality
    // 需要实现 start / stop 功能
    super.setSinks(sinks);

    this.sinks = new HashMap<String, Sink>();
    for (Sink sink : sinks) {
      this.sinks.put(sink.getName(), sink);
    }
  }

}
