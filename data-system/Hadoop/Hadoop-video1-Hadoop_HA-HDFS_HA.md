
- [一 概述](#一-概述)
- [二 手动故障转移 - 原理](#二-手动故障转移---原理)
- [三 手动故障转移 - 测试](#三-手动故障转移---测试)
    - [3.1 集群规划](#31-集群规划)
- [四 自动故障转移 - 原理](#四-自动故障转移---原理)
- [五 自动故障转移 - 测试](#五-自动故障转移---测试)
    - [5.1 集群规划](#51-集群规划)

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
| :--- | :--- | :--- | :--- |
| __vm017__ | DataNode | NodeManager |  |
|  | __NameNode__ |  |  |
|  |  |  | JournalNode |
| __vm06__ | DataNode | NodeManager |  |
|  | __NameNode__ |  |  |
|  |  |  | JournalNode |
| __vm03__ | DataNode | NodeManager |  |
|  |  | __ResourceManager__ |  |
|  |  |  | JournalNode |

## 3.2 配置

### 3.2.1 配置 core-site.xml

```xml
<!-- 把两个 NameNode 的地址组装成一个集群 mycluster -->
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://mycluster</value>
  </property>

<!-- 指定 Hadoop 运行时产生文件的存储目录 -->
  <property>
    <name>hadoop.tmp.dir</name>
    <value>/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp</value>
  </property>

<!-- 指定 JournalNode 运行时产生文件的存储目录 -->
  <property>
    <name>dfs.journalnode.edits.dir</name>
    <value>/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/journal</value>
  </property>
```

### 3.2.2 配置 hdfs-site.xml

```xml
<!-- NameNode 集群名称 -->
  <property>
    <name>dfs.nameservices</name>
    <value>mycluster</value>
  </property>

<!-- 集群中 NameNode 节点都有哪些 -->
  <property>
    <name>dfs.ha.namenodes.mycluster</name>
    <value>nn1,nn2</value>
  </property>

<!-- nn1, nn2 的 RPC 通信地址 -->
  <property>
    <name>dfs.namenode.rpc-address.mycluster.nn1</name>
    <value>vm017:8020</value>
  </property>
  <property>
    <name>dfs.namenode.rpc-address.mycluster.nn2</name>
    <value>vm06:8020</value>
  </property>

<!-- nn1, nn2 的 HTTP 通信地址 -->
  <property>
    <name>dfs.namenode.http-address.mycluster.nn1</name>
    <value>vm017:50070</value>
  </property>
  <property>
    <name>dfs.namenode.http-address.mycluster.nn2</name>
    <value>vm06:50070</value>
  </property>

<!-- 指定 NameNode 元数据在 JournalNode 上的存放位置 -->
  <property>
    <name>dfs.namenode.shared.edits.dir</name>
    <value>qjournal://vm017:8485;vm06:8485;vm03:8485/mycluster</value>
  </property>

<!-- 访问代理类: client, mycluster, active 配置失败切换实现方式 -->
  <property>
    <name>dfs.client.failover.proxy.provider.mycluster</name>
    <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
  </property>

<!-- 配置隔离机制, 即同一时刻只能有 1 台服务器对外响应 -->
  <property>
    <name>dfs.ha.fencing.methods</name>
    <value>sshfence</value>
  </property>

<!-- 使用隔离机制时需要 ssh 无秘钥登录 -->
  <property>
    <name>dfs.ha.fencing.ssh.private-key-files</name>
    <value>/home/zozo/.ssh/id_rsa</value>
  </property>
```

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
| :--- | :--- | :--- | :--- |
| __vm017__ | DataNode | NodeManager |  |
|  | __NameNode__ |  |  |
|  |  |  | JournalNode |
|  |  |  | ZooKeeper |
| __vm06__ | DataNode | NodeManager |  |
|  | __NameNode__ | __ResourceManager__ |  |
|  |  |  | JournalNode |
|  |  |  | ZooKeeper |
| __vm03__ | DataNode | NodeManager |  |
|  |  | __ResourceManager__ |  |
|  |  |  | JournalNode |
|  |  |  | ZooKeeper |


---
