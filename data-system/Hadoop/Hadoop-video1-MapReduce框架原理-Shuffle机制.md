
- [一 概览](#一-概览)
- [二 Partitioner (分区) - 默认分区 HashPartitioner](#二-partitioner-分区---默认分区-hashpartitioner)
    - [2.1 代码测试](#21-代码测试)
- [三 Partitioner (分区) - 自定义分区 CustomPartitioner](#三-partitioner-分区---自定义分区-custompartitioner)
    - [3.1 代码测试](#31-代码测试)
    - [3.2 总结](#32-总结)
- [四 Comparable (排序)](#四-comparable-排序)
    - [4.1 概述](#41-概述)
    - [4.2 分类](#42-分类)
- [五 Comparable (排序) - 全排序](#五-comparable-排序---全排序)
    - [5.1 代码测试](#51-代码测试)
- [六 Comparable (排序) - 分区排序](#六-comparable-排序---分区排序)
    - [6.1 代码测试](#61-代码测试)
- [七 Combiner (Reducer)](#七-combiner-reducer)
    - [7.1 代码测试](#71-代码测试)
- [八 Grouping Comparator (分组)](#八-grouping-comparator-分组)
    - [8.1 代码测试](#81-代码测试)
- [九 设置 MapReduce 并行度](#九-设置-mapreduce-并行度)
    - [9.1 实验数据](#91-实验数据)
    - [9.2 注意事项](#92-注意事项)

---

- out
  - [Hadoop中WritableComparable 和 comparator](https://www.cnblogs.com/robert-blue/p/4159434.html)
  - [Hadoop Mapreduce分区、分组、二次排序过程详解(https://www.cnblogs.com/hadoop-dev/p/5910459.html)

---

# 一 概览

Mapper -> 分区 -> 排序 -> Combiner (Reducer) -> 分组 -> Reducer

Mapper -> Partitioner (分区) -> Comparable (排序) -> Combiner (Reducer) -> Grouping Comparator (分组) -> Reducer

Mapper -> Partitioner -> WritableComparable -> Reducer -> WritableComparator -> Reducer

Mapper.map() -> Partitioner.getPartition() -> WritableComparable.compareTo() -> Reducer.reduce() -> WritableComparator.compare() -> Reducer.reduce()

Mapper.map(KEYIN, VALUEIN, KEYOUT, VALUEOUT) -> Partitioner.getPartition(KEY, VALUE) -> WritableComparable.compareTo(o) -> Reducer.reduce(KEYIN, VALUEIN, KEYOUT, VALUEOUT) -> WritableComparator.compare(a, b) -> Reducer.reduce(KEYIN, VALUEIN, KEYOUT, VALUEOUT)

---

# 二 Partitioner (分区) - 默认分区 HashPartitioner

默认分区 `HashPartitioner` 是根据 key 的 hashCode 对 ReduceTasks 的个数取模得到. 在不进行其他设置的时候, 用户无法控制哪个 key 存储到哪个分区.

```java
public class HashPartitioner<K, V> extends Partitioner<K, V> {

  /** Use {@link Object#hashCode()} to partition. */
  public int getPartition(K key, V value,
                          int numReduceTasks) {
    return (key.hashCode() & Integer.MAX_VALUE) % numReduceTasks;
  }

}
```

## 2.1 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.partition.defaulted.DefaultDriver)](https://github.com/zozospider/note-hadoop-video1)

---

# 三 Partitioner (分区) - 自定义分区 CustomPartitioner

## 3.1 代码测试

需求: 要求将统计结果按照条件输出到不同文件中 (分区).

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.partition.custom.CustomDriver1, com.zozospider.hadoop.mapreduce.partition.custom.CustomDriver2, com.zozospider.hadoop.mapreduce.partition.custom.CustomDriver3, com.zozospider.hadoop.mapreduce.partition.custom.CustomDriver4)](https://github.com/zozospider/note-hadoop-video1)

## 3.2 总结

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-Shuffle%E6%9C%BA%E5%88%B6/Partitioner%E5%88%86%E5%8C%BA%E6%80%BB%E7%BB%93.png?raw=true)

---

# 四 Comparable (排序)

## 4.1 概述

MapTask 和 ReduceTask 均会默认对数据按照 key 进行排序 (不管是否需要).

默认排序是按照字典顺序排序, 时间方法是快速排序.

对于 MapTask, 它会将处理结果暂时放到环形缓冲区中, 当环形缓冲区使用率达到一定阈值后, 再对缓冲区中的数据进行一次快速排序, 并将这些有序数据溢写到磁盘上, 当数据处理完毕后, 它会对磁盘上的所有文件进行归并排序.

对于 ReduceTask, 它从每个 MapTask 上远程拷贝相应的数据文件, 如果文件大小超过一定阈值, 则溢写到磁盘上, 否则存储在内存中. 如果磁盘上的文件数目达到一定阈值, 则进行一次归并排序以生成一个更大的文件, 如果内存中文件大小或者数据超过一定阈值, 则进行一次合并后将数据溢写到磁盘上. 当所有数据拷贝完毕后, ReduceTask 统一对内存和磁盘上的所有数据进行一次归并排序.

## 4.2 分类

- 全排序: 最终输出结果只有 1 个文件, 且文件内部有序. 实现方式是只设置 1 个 ReduceTask. 但该方法在处理大型文件时效率极低, 丧失了 MapReduce 所提供的并行架构.
- 部分排序: MapReduce 根据输入记录的键值对数据集排序. 保证输出的每个文件内部有序.
- 辅助排序 (`GroupingComparator` 分组): 在 Reduce 端对 key 进行分组. 应用于: 在接受的 key 为 bean 对象时, 想让 1 个或几个字段相同 (全部字段比较不相同) 的 key 进入到同一个 reduce 方法时, 可以采用分组排序.
- 二次排序: 在自定义排序过程中, 如果 compareTo 中的判断条件为 2 个即为二次排序.

---

# 五 Comparable (排序) - 全排序

## 5.1 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.comparable.all.AllDriver)](https://github.com/zozospider/note-hadoop-video1)

---

# 六 Comparable (排序) - 分区排序

## 6.1 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.comparable.partitioned.PartitionedDriver)](https://github.com/zozospider/note-hadoop-video1)

---

# 七 Combiner (Reducer)

Combiner 是 MapReduce 程序中 Mapper 和 Reducer 之外的一种组件, Combiner 组件的父类就是 Reducer. Combiner 的意义就是对每一个 MapTask 的输出进行局部汇总, 以减少网络传输量.

Combiner 和 Reducer 的区别在于: Combiner 是在每一个 MapTask 所在的节点运行, Reducer 是接收全局所有 Mapper 的输出结果.

__注意__: Combiner 能够应用的前提是: 不管 Combiner Function 被调用多少次, 对应的 Reduce 输出结果都应该是一样的 (不能影响最终业务逻辑). 如求最大值可以使用 Combiner, 但是求平均值不能使用 Combiner.

## 7.1 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.combiner.CombinerDriver)](https://github.com/zozospider/note-hadoop-video1)

---

# 八 Grouping Comparator (分组)

## 8.1 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.groupingcomparator.GroupingComparatorDriver1, com.zozospider.hadoop.mapreduce.groupingcomparator.GroupingComparatorDriver2)](https://github.com/zozospider/note-hadoop-video1)

---

# 九 设置 MapReduce 并行度

ReduceTask 的并行度同样影响整个 job 的执行并发度和执行效率, 但与 MapTask 但并发数由切片数决定不同, ReduceTask 数量可以手动设置:

```java
// 默认是 1
job.setNumReduceTasks(1);
```

## 9.1 实验数据

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-Shuffle%E6%9C%BA%E5%88%B6/%E6%B5%8B%E8%AF%95ReduceTask%E4%B8%AA%E6%95%B0.png?raw=true)

## 9.2 注意事项

ReduceTask 个数默认 = 1, 所以输出文件个数为 1 个. 当 ReduceTask 个数 = 0 时, 表示没有 Reduce 阶段, 输出文件个数和 Map 个数一致.

如果分区数不是 1, 但是 ReduceTask 为 1, 那么就不会执行分区过程 (因为源码逻辑是, 执行分区前提是判断 ReduceNum 是否大于 1, 不大于则不执行分区).

ReduceTask 个数并不是任意设置, 还需要考虑集群性能和业务需求, 比如需要全局计算汇总结果, 就只能有 1 个 ReduceTask. 另外, 如果数据分布不均匀, 就有可能在 Reduce 阶段产生数据倾斜.

---
