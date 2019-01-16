
# Document & Code

* [../Zookeeper-book](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-book.md)

---

# 一 配置详解

## 1.1 基本配置

基本配置是指运行 ZooKeeper 必须的参数, 部分参数 ZooKeeper 会为其设置默认值.

以下为基本配置说明:

| 参数名 | 说明 |
| :--- | :--- |
| clientPort | 该参数无默认值, 必须配置, 不支持系统属性方式 (启动命令行加参数 -D) 配置. |
|  | clientPort 用于配置当前 ZooKeeper 服务器对外的服务端口, 客户端通过该端口和服务器创建连接, 一般设置为 `2181`. |
| dataDir | 该参数无默认值, 必须配置, 不支持系统属性方式配置. |
|  | dataDir 用于配置 ZooKeeper 服务器存储快照文件的目录, 默认情况下, 如果没有配置 dataLogDir, 那么事务日志也会存储在这个目录中. 考虑到事务日志的写性能直接影响 ZooKeeper 服务的整体性能, 建议同时设置参数 dataLogDir 来配置 ZooKeeper 事务日志的存储目录. |
| tickTime | 该参数有默认值 `3000 ms`, 可以不配置, 不支持系统属性方式配置. |
|  | tickTime 用于配置 ZooKeeper 中最小时间单元的长度, 很多运行时的时间间隔都是使用 tickTime 的倍数来表示的. 如 ZooKeeper 的会话最小超时时间默认是 2 * tickTime. |

## 1.2 高级配置

以下为高级配置说明:

| 参数名 | 说明 |
| :--- | :--- |
| dataLogDir | |
|  |  |
|  |  |
|  |  |
| initLimit |  |
|  |  |
|  |  |
|  |  |
| syncLimit |  |
|  |  |
|  |  |
|  |  |
| snapCount |  |
|  |  |
|  |  |
| preAllocSize |  |
|  |  |
|  |  |
|  |  |
| minSessionTimeout / maxSessionTimeout |  |
|  |  |
|  |  |
| maxClientCnxns |  |
|  |  |
|  |  |
|  |  |
| jute.maxbuffer |  |
|  |  |
|  |  |
|  |  |
| clientPortAddress |  |
|  |  |
|  |  |
|  |  |
| server.id=host:port:port |  |
|  |  |
|  |  |
|  |  |
| autopurge.snapRetainCount |  |
|  |  |
|  |  |
|  |  |
| autopurge.purgeInterval |  |
|  |  |
|  |  |
|  |  |
| fsync.warningthresholdms |  |
|  |  |
|  |  |
|  |  |
| forceSync |  |
|  |  |
|  |  |
|  |  |
| globalOutstandingLimit |  |
|  |  |
|  |  |
|  |  |
| leaderServes |  |
|  |  |
|  |  |
|  |  |
| SkipAcl |  |
|  |  |
|  |  |
|  |  |
| cnxTimeout |  |
|  |  |
|  |  |
|  |  |
| electionAlg |  |
|  |  |
|  |  |
|  |  |

---

# 二 四字命令

---

# 三 JMX

## 3.1 开启远程 JMX

## 3.2 通过 JConsole 连接 ZooKeeper

---

# 四 监控

## 4.1 实时监控

## 4.2 数据统计

---

# 五 构建一个高可用的集群

## 5.1 集群组成

## 5.2 容灾

## 5.3 扩容与缩容

---

# 六 日常运维

## 6.1 数据与日志管理

## 6.2 Too many connections

## 6.3 磁盘管理

---
