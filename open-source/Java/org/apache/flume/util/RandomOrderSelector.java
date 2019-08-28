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
import java.util.Random;

/**
 * An implementation of OrderSelector which returns objects in random order.
 * Also supports backoff.
 * OrderSelector 的实现, 它以随机顺序返回对象. 还支持 backoff.
 *
 * ps: Random 排序方式的选择器工具类
 */
public class RandomOrderSelector<T> extends OrderSelector<T> {

  private Random random = new Random(System.currentTimeMillis());

  public RandomOrderSelector(boolean shouldBackOff) {
    super(shouldBackOff);
  }

  /**
   * 实现 OrderSelector 抽象类的 createIterator() 方法.
   * SinkProcessor 的 process() 方法会调用此方法, 获取本次 process 处理的 SpecificOrderIterator 迭代器 (封装了确认顺序的 active objects 列表)
   */
  @Override
  public synchronized Iterator<T> createIterator() {
    // 获取目前 active objects (对象 T) 对应的索引列表
    List<Integer> indexList = getIndexList();

    int size = indexList.size();
    // 定义索引排序列表
    int[] indexOrder = new int[size];

    // 遍历索引列表, 并随机产生索引, 将排序列表插入, 直到索引列表所有索引被移除
    while (indexList.size() != 1) {
      int pick = random.nextInt(indexList.size());
      indexOrder[indexList.size() - 1] = indexList.remove(pick);
    }

    indexOrder[0] = indexList.get(0);

    // 将本次 process() 确定的 indexOrder (定义索引排序列表) 和 getObjects() (要排序的 objects 列表) 封装成 SpecificOrderIterator 迭代器, 并返回.
    return new SpecificOrderIterator<T>(indexOrder, getObjects());
  }
}
