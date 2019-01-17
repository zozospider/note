
- [Document & Code](#document--code)
- [一 配置详解](#一-配置详解)
    - [1.1 基本配置](#11-基本配置)
    - [1.2 高级配置](#12-高级配置)
- [二 四字命令](#二-四字命令)
- [三 JMX](#三-jmx)
    - [3.1 开启远程 JMX](#31-开启远程-jmx)
    - [3.2 通过 JConsole 连接 ZooKeeper](#32-通过-jconsole-连接-zookeeper)
- [四 监控](#四-监控)
    - [4.1 实时监控](#41-实时监控)
    - [4.2 数据统计](#42-数据统计)
- [五 构建一个高可用的集群](#五-构建一个高可用的集群)
    - [5.1 集群组成](#51-集群组成)
    - [5.2 容灾](#52-容灾)
    - [5.3 扩容与缩容](#53-扩容与缩容)
- [六 日常运维](#六-日常运维)
    - [6.1 数据与日志管理](#61-数据与日志管理)
    - [6.2 Too many connections](#62-too-many-connections)
    - [6.3 磁盘管理](#63-磁盘管理)

---

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
| tickTime | 该参数有默认值 `3000` (单位: ms), 可以不配置, 不支持系统属性方式配置. |
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
|  | 通常情况下, 运维人员使用该参数的默认值即可. 但是如果部署 ZooKeeper 集群的网络环境质量较低 (如网络延时大, 丢包严重等), 那么可以适当调大这个参数. |
| snapCount | 该参数有默认值 `100000`, 可以不配置, 仅支持系统属性方式配置: `zookeeper.snapCount`. |
|  | snapCount 用于配置相邻两次数据快照之间的事务操作次数, 即 ZooKeeper 会在 snapCount 次事务操作之后进行一次数据快照. |
| preAllocSize | 该参数有默认值 `65536` (单位: KB), 即 `64MB`, 可以不配置, 仅支持系统属性方式配置: `zookeeper.preAllocSize`. |
|  | preAllocSize 用于配置 ZooKeeper 事务日志文件预分配的磁盘空间大小. |
|  | 通常情况下, 运维人员使用该参数的默认值即可. 但是如果将参数 snapCount 设置得比默认值更小或更大, 那么 preAllocSize 也要随之做出变更. 例如: snapCount 设置为 500, 预估每次事务操作的数据量大小最多 20KB, 那么 preAllocSize 设置为 10000 就足够了. |
| minSessionTimeout / maxSessionTimeout | 这两个参数有默认值, 分别是 `2` 和 `20` (单位: ms), 即默认的会话超时时间在 `2 * tickTime` ~ `20 * tickTime` 范围内, 可以不配置, 不支持系统属性方式配置. |
|  | 这两个参数用于服务端对客户端会话的超时时间进行限制, 如果客户端设置的超时时间不在该范围内, 那么会被服务端强制设置为 minSessionTimeout 或 maxSessionTimeout 超时时间. |
| maxClientCnxns | 该参数有默认值 `60`, 可以不配置, 不支持系统属性方式配置. |
|  | maxClientCnxns 从 Socket 层面限制单个客户端与单台服务器之间的连接并发数, 即以 IP 地址粒度来进行连接数的限制. 如果该参数设置为 0, 则表示连接数不作任何限制. |
|  | 需要注意该连接数限制选项的适用范围, 其仅仅是对单台客户端机器与单台 ZooKeeper 服务器之间的连接数限制, 并不控制所有客户端的连接数总和. |
| jute.maxbuffer | 该参数有默认值 `1048575` (单位: byte), 可以不配置, 仅支持系统属性方式配置: `jute.maxbuffer`. |
|  | jute.maxbuffer 用于配置单个 ZNode (数据节点) 上可以存储的最大数据量大小. |
|  | 通常情况下, 运维人员使用该参数的默认值即可. 同时考虑到 ZooKeeper 上不宜存储太多的数据, 往往还需要调小该参数. 另外, 在变更该参数的时候, 需要在 ZooKeeper 集群的所有机器以及所有的客户端上均设置才能生效. |
| clientPortAddress | 该参数没有默认值, 可以不配置, 不支持系统属性方式配置. |
|  | 针对那些多网卡的机器, 该参数允许为每个 IP 地址指定不同的监听端口 |
| server.id=host:port:port | 该参数没有默认值, 在单机模式下可以不配置, 不支持系统属性方式配置. |
|  | server.id=host:port:port 用于配置组成 ZooKeeper 集群的机器列表, 其中 id 即为 ServerID, 与每台服务器 myid 文件中的数字相对应. 同时, 在该参数中, 会配置两个端口: 第一个端口, 第一个端口用于指定 Follower 与 Leader 进行运行时通信和数据同步时所使用的端口, 第二个端口用于进行 Leader 选举过程的投票通信. |
|  | 在 ZooKeeper 服务器启动时, 会根据 myid 文件中配置的 ServerID 来确定自己是哪台服务器, 并使用对应配置的度单口来进行启动. |
|  | 如果在同一台服务器上部署多个 ZooKeeper 来搭建伪分布式集群, 这些端口要不同, 如下: |
|  | server.1=192.168.0.1:2777:3777 |
|  | server.1=192.168.0.1:2888:3888 |
|  | server.1=192.168.0.1:2999:3999 |
| autopurge.snapRetainCount | 该参数有默认值 `3`, 可以不配置, 不支持系统属性方式配置. |
|  | autopurge.snapRetainCount 用于配置 ZooKeeper 在自动清理历史事务日志和快照数据的时候需要保留的快照数据文件数量和对应的事务日志文件. |
|  | 需要注意, 并不是磁盘上的所有事务日志和快照数据文件都可以被清理掉 (那样将无法恢复数据), 因此, 参数 autopurge.snapRetainCount 的最小值是 `3`, 如果配置的值比 3 小的话, 就会被调整成 3, 即至少需要保留 3 个快照数据文件和对应的事务日志文件. |
| autopurge.purgeInterval | 该参数有默认值 `0` (单位: 小时), 可以不配置, 不支持系统属性方式配置. |
|  | 参数 autopurge.purgeInterval 和参数 autopurge.snapRetainCount 配套使用, 适用于配置 ZooKeeper 进行历史文件自动清理的频率. |
|  | 如果配置该值为 `0` 或负数, 那么就表示不需要开启定时清理功能. ZooKeeper 默认不开启这个功能. |
| fsync.warningthresholdms | 该参数有默认值 `1000` (单位: ms), 可以不配置, 仅支持系统属性方式配置: `fsync.warningthresholdms`. |
|  | fsync.warningthresholdms 用于配置 ZooKeeper 进行事务日志 fsync 操作时消耗时间的报警阈值. 一旦进行一个 fsync 操作小号的时间大于 fsync.warningthresholdms 值, 就会在日志中打印报警日志. |
| forceSync | 该参数有默认值 `yes`, 可以不配置, 可选配置项为 `yes` 和 `no`, 仅支持系统属性方式配置: `zookeeper.forceSync`. |
|  | forceSync 用于配置 ZooKeeper 服务器是否在事务提交的时候, 将日志写入操作强制刷入磁盘 (即调用 `java.nio.channels.FileChannel.force(boolean metaData)` 接口), 默认是 yes, 即每次事务日志写入操作都会实时刷入磁盘. 如果设置为 no, 则能一定程度提高 ZooKeeper 的写性能, 但是会存在类似于机器断电的安全风险. |
| globalOutstandingLimit | 该参数有默认值 `1000`, 可以不配置, 仅支持系统属性方式配置: `zookeeper.globalOutstandingLimit`. |
|  | globalOutstandingLimit 用于配置 ZooKeeper 服务器最大请求堆积数量. 在 ZooKeeper 服务器运行过程中, 客户端会源源不断的将请求发送到服务端, 为了防止服务端资源 (如 CPU, 内存, 网络等) 耗尽, 服务端必须限制同时处理的请求数, 即最大请求堆积数量. |
| leaderServes | 该参数有默认值 `yes`, 可以不配置, 可选配置项为 `yes` 和 `no`, 仅支持系统属性方式配置: `zookeeper.leaderServes`. |
|  | leaderServes 用于配置 Leader 是否能够接受客户端的连接, 即是否允许 Leader 向服务器提供服务, 默认情况下, Leader 能够接受并处理客户端的所有读写请求. 在 ZooKeeper 设计中, Leader 主要用来进行对事务更新请求的协调和集群本身的运行时协调. 因此, 可以设置让 Leader 不接受客户端的连接, 以使其专注于分布式协调. |
| SkipAcl | 该参数有默认值 `no`, 可以不配置, 可选配置项为 `yes` 和 `no`, 仅支持系统属性方式配置: `zookeeper.SkipAcl`. |
|  | SkipAcl 用于配置 ZooKeeper 服务器是否跳过 ACL 权限检查, 默认为 no, 即会对每一个客户端请求进行权限检查. 如果将其设置为 yes, 则能一定程度的提高 ZooKeeper 的读写性能, 但同时也将向所有客户端开放权限, 包括之前设置过的 ACL 权限的数据节点都将不再接受权限控制. |
| cnxTimeout | 该参数有默认值 `5000` (单位: ms), 可以不配置, 仅支持系统属性方式配置: `zookeeper.cnxTimeout`. |
|  | cnxTimeout 用于配置在 Leader 选举过程中, 各服务器之间进行 TCP 连接创建的超时时间. |

---

# 二 四字命令

四字命令通常用来发送请求到 ZooKeeper 服务器用于获取信息.

请参考 [ZooKeeper 四字命令 Four Letter Words](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-video-ZK%E5%9F%BA%E6%9C%AC%E7%89%B9%E6%80%A7%E4%B8%8E%E5%9F%BA%E4%BA%8ELinux%E7%9A%84ZK%E5%AE%A2%E6%88%B7%E7%AB%AF%E5%91%BD%E4%BB%A4%E8%A1%8C%E5%AD%A6%E4%B9%A0.md#zookeeper-%E5%9B%9B%E5%AD%97%E5%91%BD%E4%BB%A4-four-letter-words)

---

# 三 JMX

JMX (Java Management Extensions, Java 管理拓展), 是一个为应用程序, 设备, 系统等植入管理功能的框架, 能够非常方便地让 Java 系统对外提供运行时数据信息获取和系统管控的接口.

ZooKeeper 使用了标准的 JMX 方式来对外提供运行时数据信息和便捷的管控接口.

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
