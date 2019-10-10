


---

# 一 Partitioner 分区 - 默认分区 HashPartitioner

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

## 1.1 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.partition.defaulted.DefaultDriver)](https://github.com/zozospider/note-hadoop-video1)

# 二 Partitioner 分区 - 自定义分区 CustomPartitioner

## 2.1 代码测试

需求: 要求将统计结果按照条件输出到不同文件中 (分区).

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.partition.custom.CustomDriver1, com.zozospider.hadoop.mapreduce.partition.custom.CustomDriver2, com.zozospider.hadoop.mapreduce.partition.custom.CustomDriver3, com.zozospider.hadoop.mapreduce.partition.custom.CustomDriver4)](https://github.com/zozospider/note-hadoop-video1)

## 2.2 总结

![image]()

---

# 二 

---

# 三 

---

# 四 

---
