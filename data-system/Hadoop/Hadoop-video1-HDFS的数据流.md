
- [一. HDFS 写数据流程](#一-hdfs-写数据流程)
- [二. 节点距离计算](#二-节点距离计算)
- [三. 副本选择](#三-副本选择)
- [四. HDFS 读数据流程](#四-hdfs-读数据流程)

---

# 一. HDFS 写数据流程

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-HDFS%E7%9A%84%E6%95%B0%E6%8D%AE%E6%B5%81/HDFS%E5%86%99%E6%95%B0%E6%8D%AE%E6%B5%81%E7%A8%8B.png?raw=true)

---

# 二. 节点距离计算

节点距离计算: 两个节点到达最近的共同祖先的距离总和.

如下, 数据中心 `d1` 机架 `r1` 中的节点 `n1` 可以表示为: `/d1/r1/n1`. 以下为四种距离计算的结果:

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-HDFS%E7%9A%84%E6%95%B0%E6%8D%AE%E6%B5%81/HDFS%E8%8A%82%E7%82%B9%E8%B7%9D%E7%A6%BB%E8%AE%A1%E7%AE%97.png?raw=true)

---

# 三. 副本选择

官方说明:

- [HDFS: Replica_Placement: The First Baby Steps](https://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsDesign.html#Replica_Placement:_The_First_Baby_Steps)

```
For the common case, when the replication factor is three, HDFS’s placement policy is to put one replica on one node in the local rack, another on a different node in the local rack, and the last on a different node in a different rack. This policy cuts the inter-rack write traffic which generally improves write performance. The chance of rack failure is far less than that of node failure; this policy does not impact data reliability and availability guarantees. However, it does reduce the aggregate network bandwidth used when reading data since a block is placed in only two unique racks rather than three. With this policy, the replicas of a file do not evenly distribute across the racks. One third of replicas are on one node, two thirds of replicas are on one rack, and the other third are evenly distributed across the remaining racks. This policy improves write performance without compromising data reliability or read performance.
```

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-HDFS%E7%9A%84%E6%95%B0%E6%8D%AE%E6%B5%81/HDFS%E5%89%AF%E6%9C%AC%E9%80%89%E6%8B%A9.png?raw=true)

---

# 四. HDFS 读数据流程

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-HDFS%E7%9A%84%E6%95%B0%E6%8D%AE%E6%B5%81/HDFS%E7%9A%84%E8%AF%BB%E6%95%B0%E6%8D%AE%E6%B5%81%E7%A8%8B.png?raw=true)

---
