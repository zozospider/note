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

  private class FailedSink implements Comparable<FailedSink> {
    private Long refresh;
    private Integer priority;
    private Sink sink;
    private Integer sequentialFailures;

    public FailedSink(Integer priority, Sink sink, int seqFailures) {
      this.sink = sink;
      this.priority = priority;
      this.sequentialFailures = seqFailures;
      adjustRefresh();
    }

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

    public void incFails() {
      sequentialFailures++;
      adjustRefresh();
      logger.debug("Sink {} failed again, new refresh is at {}, current time {}",
                   new Object[] { sink.getName(), refresh, System.currentTimeMillis() });
    }

    private void adjustRefresh() {
      refresh = System.currentTimeMillis()
          + Math.min(maxPenalty, (1 << sequentialFailures) * FAILURE_PENALTY);
    }
  }

  private static final Logger logger = LoggerFactory.getLogger(FailoverSinkProcessor.class);

  private static final String PRIORITY_PREFIX = "priority.";
  private static final String MAX_PENALTY_PREFIX = "maxpenalty";
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
    for (Entry<String, Sink> entry : sinks.entrySet()) {
      String priStr = PRIORITY_PREFIX + entry.getKey();
      Integer priority;
      try {
        priority =  Integer.parseInt(context.getString(priStr));
      } catch (Exception e) {
        priority = --nextPrio;
      }
      if (!liveSinks.containsKey(priority)) {
        liveSinks.put(priority, sinks.get(entry.getKey()));
      } else {
        logger.warn("Sink {} not added to FailverSinkProcessor as priority" +
            "duplicates that of sink {}", entry.getKey(),
            liveSinks.get(priority));
      }
    }
    activeSink = liveSinks.get(liveSinks.lastKey());
  }

  @Override
  public Status process() throws EventDeliveryException {
    // Retry any failed sinks that have gone through their "cooldown" period
    // 重试已经过 "cooldown" 期的任何失败的 sinks
    Long now = System.currentTimeMillis();
    while (!failedSinks.isEmpty() && failedSinks.peek().getRefresh() < now) {
      FailedSink cur = failedSinks.poll();
      Status s;
      try {
        s = cur.getSink().process();
        if (s  == Status.READY) {
          liveSinks.put(cur.getPriority(), cur.getSink());
          activeSink = liveSinks.get(liveSinks.lastKey());
          logger.debug("Sink {} was recovered from the fail list",
                  cur.getSink().getName());
        } else {
          // if it's a backoff it needn't be penalized.
          // 如果它是 backoff, 它不需要受到惩罚.  ???
          failedSinks.add(cur);
        }
        return s;
      } catch (Exception e) {
        cur.incFails();
        failedSinks.add(cur);
      }
    }

    Status ret = null;
    while (activeSink != null) {
      try {
        ret = activeSink.process();
        return ret;
      } catch (Exception e) {
        logger.warn("Sink {} failed and has been sent to failover list",
                activeSink.getName(), e);
        activeSink = moveActiveToDeadAndGetNext();
      }
    }

    throw new EventDeliveryException("All sinks failed to process, " +
        "nothing left to failover to");
  }

  private Sink moveActiveToDeadAndGetNext() {
    Integer key = liveSinks.lastKey();
    failedSinks.add(new FailedSink(key, activeSink, 1));
    liveSinks.remove(key);
    if (liveSinks.isEmpty()) return null;
    if (liveSinks.lastKey() != null) {
      return liveSinks.get(liveSinks.lastKey());
    } else {
      return null;
    }
  }

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
