


---

# 一 概述

实现 HA (High Available) 高可用最关键的策略是消除单点故障. 在 Hadoop 2.0 之前, 存在 NameNode 的 SPOF (single point of failure) 单点故障.

HDFS HA 功能通过配置 Active / Standby 两个 NameNodes 实现在集群中对 NameNode 的热备来解决上述问题, 如果出现故障 (或机器需要升级维护), NameNode 可以切换到另一台机器.

参考:
- [HDFS High Availability Using the Quorum Journal Manager](https://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html)

---

# 二 手动故障转移 - 原理

参考:
- [HDFS High Availability Using the Quorum Journal Manager - Deployment](https://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html#Deployment)

---

# 三 手动故障转移 - 测试

## 3.1 集群规划

| 模块 / 节点 | __vm017__ | __vm06__ | __vm03__ |
| :--- | :--- | :--- | :--- |
| __HDFS__ | DataNode | DataNode | DataNode |
|  | __NameNode__ | __NameNode__ |  |
|  | JournalNode | JournalNode | JournalNode |
| __YARN__ | NodeManager | NodeManager | NodeManager |
|  |  |  | __ResourceManager__ |

| 节点 / 模块 | __HDFS__ | __YARN__ |  __HA__ |
| :--- | :--- | :--- |
| __vm017__ | DataNode | NodeManager |  |
|  | __NameNode__ |  |  |
|  |  |  | JournalNode |
| __vm06__ | DataNode | NodeManager |  |
|  | __NameNode__ |  |  |
|  |  |  | JournalNode |
| __vm03__ | DataNode | NodeManager |  |
|  |  | __ResourceManager__ |  |
|  |  |  | JournalNode |

---


# 四 自动故障转移 - 原理

参考:
- [HDFS High Availability Using the Quorum Journal Manager - Automatic_Failover](https://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html#Automatic_Failover)


---

# 五 自动故障转移 - 测试

## 5.1 集群规划

| 模块 / 节点 | __vm017__ | __vm06__ | __vm03__ |
| :--- | :--- | :--- | :--- |
| __HDFS__ | DataNode | DataNode | DataNode |
|  | __NameNode__ | __NameNode__ |  |
|  | JournalNode | JournalNode | JournalNode |
|  | ZooKeeper | ZooKeeper | ZooKeeper |
| __YARN__ | NodeManager | NodeManager | NodeManager |
|  | __ResourceManager__ |  | __ResourceManager__ |

| 节点 / 模块 | __HDFS__ | __YARN__ | __HA__ |
| :--- | :--- | :--- |
| __vm017__ | DataNode | NodeManager |
|  | __NameNode__ |  |  |
|  |  |  | JournalNode |
|  |  |  | ZooKeeper |
| __vm06__ | DataNode | NodeManager |
|  | __NameNode__ | __ResourceManager__ |  |
|  |  |  | JournalNode |
|  |  |  | ZooKeeper |
| __vm03__ | DataNode | NodeManager |
|  |  | __ResourceManager__ |  |
|  |  |  | JournalNode |
|  |  |  | ZooKeeper |


---
