


---

# 一 Partitioner 分区

## 1.1 默认分区 - HashPartitioner

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

需求: 要求将

---

# 二 

---

# 三 

---

# 四 

---
