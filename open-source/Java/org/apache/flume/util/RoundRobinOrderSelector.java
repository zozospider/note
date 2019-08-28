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

import java.util.Iterator;
import java.util.List;

/**
 * An implementation of OrderSelector which returns objects in round robin order.
 * Also supports backoff.
 * OrderSelector 的实现, 它以循环顺序返回对象. 还支持 backoff.
 *
 * ps: RoundRobin 排序方式的选择器工具类
 */

public class RoundRobinOrderSelector<T> extends OrderSelector<T> {

  // 记录下次从 activeIndices 中获取的起始位置
  private int nextHead = 0;

  public RoundRobinOrderSelector(boolean shouldBackOff) {
    super(shouldBackOff);
  }

  /**
   * 实现 OrderSelector 抽象类的 createIterator() 方法.
   * SinkProcessor 的 process() 方法会调用此方法, 获取本次 process 处理的 SpecificOrderIterator 迭代器 (封装了确认顺序的 active objects 列表)
   */
  @Override
  public Iterator<T> createIterator() {
    // 获取目前 active objects (对象 T) 对应的索引列表
    List<Integer> activeIndices = getIndexList();
    int size = activeIndices.size();
    // possible that the size has shrunk so gotta adjust nextHead for that
    // 可能是 size 变小所以必须调整 nextHead
    if (nextHead >= size) {
      nextHead = 0;
    }
    // begin: 从 activeIndices 中获取的起始位置递增
    int begin = nextHead++;
    if (nextHead == activeIndices.size()) {
      nextHead = 0;
    }

    int[] indexOrder = new int[size];

    // 遍历 activeIndices, 从 begin 开始, 将索引列表中的元素顺序放入 indexOrder 中
    for (int i = 0; i < size; i++) {
      indexOrder[i] = activeIndices.get((begin + i) % size);
    }

    // 将本次 process() 确定的 indexOrder (定义索引排序列表) 和 getObjects() (要排序的 objects 列表) 封装成 SpecificOrderIterator 迭代器, 并返回.
    return new SpecificOrderIterator<T>(indexOrder, getObjects());
  }
}
