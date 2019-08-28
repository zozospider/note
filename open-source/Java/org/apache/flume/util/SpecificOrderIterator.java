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
package org.apache.flume.util;

import java.util.Iterator;
import java.util.List;

/**
 * A utility class that iterates over the given ordered list of items via
 * the specified order array. The entries of the order array indicate the
 * index within the ordered list of items that needs to be picked over the
 * course of iteration.
 * 一个实用类, 它通过指定的顺序 array 迭代给定的有序 items list.
 * order array 的条目表示需要在迭代过程中选择的有序 items list 中的索引.
 */
public class SpecificOrderIterator<T> implements Iterator<T> {

  // 索引顺序 array
  private final int[] order;
  // 所有元素列表
  private final List<T> items;
  // 当前索引
  private int index = 0;

  /**
   * 构造方法
   */
  public SpecificOrderIterator(int[] orderArray, List<T> itemList) {
    order = orderArray;
    items = itemList;
  }

  /**
   * 重写 Iterator 接口的 hasNext() 方法: 判断该迭代器中是否还有下一个元素.
   * 如果 index (当前索引) >= order.length (索引顺序 array 的长度), 则表示已经遍历完该迭代器的所有元素.
   */
  @Override
  public boolean hasNext() {
    return index < order.length;
  }

  /**
   * 重写 Iterator 接口的 next() 方法: 从迭代器中取出一个元素 T.
   * 从 items (所有元素列表) 中, 取出由 order (索引顺序 array) 维护的一个递增索引.
   */
  @Override
  public T next() {
    return items.get(order[index++]);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
