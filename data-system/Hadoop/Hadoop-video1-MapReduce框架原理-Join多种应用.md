
- [一 Reduce Join](#一-reduce-join)
    - [1.1 代码测试](#11-代码测试)
- [二 Map Join](#二-map-join)
    - [2.1 代码测试](#21-代码测试)

---

# 一 Reduce Join

Map 端的主要工作: 为来自不同表或文件的 key / value 对打标签以区别不同来源的记录. 然后用连接字段作为 key, 其余部分和新加的标志作为 value, 最后进行输出.

Reduce 端的主要工作: 在 Reduce 端以连接字段作为 key 的分组已经完成, 我们只需要在每一个分组当中将那些来源于不同文件的记录 (在 Map 阶段已经打标签), 最后进行合并即可.

缺点: 这种方式中, 合并的操作是在 Reduce 阶段完成, Reduce 端的处理压力太大, Map 节点的运算负载则很低, 资源利用率不高, 且在 Reduce 阶段极易产生数据倾斜.

解决方案: Map 端实现数据合并.

## 1.1 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.join.reducejoin.ReduceJoinDriver)](https://github.com/zozospider/note-hadoop-video1)

---

# 二 Map Join

使用场景: Map Join 适用于 1 张表很小, 另 1 张表很大的场景.

优点: 在 Map 端缓存多张表, 提前处理业务逻辑, 这样增加 Map 端业务, 减少 Reduce 端数据的压力, 尽可能的减少数据倾斜.

方法: 采用 DistributedCache, 在驱动函数中加载缓存. 然后在 Mapper 的 setup 阶段, 将缓存文件读取到内存集合中.

## 2.1 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.join.mapjoin.MapJoinDriver)](https://github.com/zozospider/note-hadoop-video1)

---
