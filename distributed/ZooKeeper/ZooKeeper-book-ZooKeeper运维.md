
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
| dataLogDir | 该参数有默认值 `dataDir`, 可以不配置, 不支持系统属性方式配置. |
|  | dataLogDir 用于配置 ZooKeeper 服务器存储事务日志文件的目录. 默认情况下, ZooKeeper 会将事务日志文件和快照数据文件存储在同一个目录, 应该尽量将他们分开. |
|  | 事务日志记录对磁盘的性能非常高, 为了保证数据一致性, ZooKeeper 在返回客户端事务请求响应之前, 必须将本次请求对应的事务日志写入到磁盘上. 因此, 事务日志写入性能直接决定了 ZooKeeper 在处理事务请求时的吞吐. 由于针对同一块磁盘的其他并发读写操作 (如 ZooKeeper 运行时的数据快照操作, 日志输出, 操作系统自身读写等) 会极大的影响事务日志的写性能, 因此如果条件允许, 建议将事务日志的存储配置一个单独的磁盘或挂载点, 以提升 ZooKeeper 的性能. |
| initLimit | 该参数有默认值 `10`, 即 `10 * tickTime`, 必须配置一个正整数, 不支持系统属性方式配置. |
|  | initLimit 用于配置 Leader 等待 Follower 启动, 并完成数据同步的时间. Follower 在启动过程中, 会与 Leader 建立连接并完成对数据的同步, 从而确定自己对外提供服务的起始状态, Leader 允许 Follower 在 initLimit 时间内完成这个工作. |
|  | 通常情况下, 运维人员使用该参数的默认值即可. 但是如果随着 ZooKeeper 集群管理的数据量增大, Follower 在启动时, 从 Leader 上进行同步数据的时间也会变长. 因此, 在这种情况下, 有必要适当调大该参数. |
| syncLimit | 该参数有默认值 `5`, 即 `5 * tickTime`, 必须配置一个正整数, 不支持系统属性方式配置. |
|  | syncLimit 用于配置 Leader 和 Follower 之间进行心跳检测的最大延长时间. 在 ZooKeeper 集群运行过程中, Leader 会与所有 Follower 进行心跳检测来确定该服务器是否存活. 如果 Leader 在 syncLimit 时间内无法获取到 Follower 的心跳检测响应, 那么就认为该 Follower 已经脱离了和自己的同步. |
|  | 通常情况下, 运维人员使用该参数的默认值即可. 但是如果部署 ZooKeeper 集群的网络环境质量较低 (如网络延时大, 丢包严重等), 那么可以适当调大这个参数.  |
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
