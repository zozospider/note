
# Document & Code

* [../Zookeeper-book](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-book.md)

---

# 一 典型应用场景及实现

## 1.1 数据发布/订阅

发布/订阅系统一般有两种模式:
- 推（Push）模式: 服务端主动将数据更新发送给所有订阅的客户端。
- 拉（Pull）模式: 客户端主动请求服务端获取最新数据，通常采用定时轮询拉取方式。

ZooKeeper 采用推（Push）拉（Pull）结合。客户端向服务端注册关注的节点，一旦该节点变化，服务端会向注册客户端发送 Watcher 事件通知（推），客户端收到通知后，需要主动到服务端获取最新数据（拉）。

数据一般为机器列表，运行时的开关配置，数据库配置等全局配置。具有以下特性:
- 数据量较小
- 数据在运行时会发生变化
- 集群中配置一致，各机器共享

### 1.1.1 ZooKeeper 应用示例

该示例为 `数据库切换` 场景，客户端从 ZooKeeper 获取数据库配置，在 ZooKeeper 的数据库配置发生变化的时候，客户端需要作出相应更新。

> __配置存储__

一般存储在 ZooKeeper 的 `/configer/app1/database_config` 节点，内容如下:
```properties
#DBCP
dbcp.driverClassName=com.mysql.jdbc.Driver
dbcp.dbJDBCUrl=jdbc:mysql://1.1.1.1:3306/app1
dbcp.characterEncording=utf-8
dbcp.username=xiaoming
dbcp.password=123456
dbcp.maxActive=30
dbcp.maxIdle=10
dbcp.maxWait=10000
```

> __配置获取__

集群中每台机器在启动阶段，首先从 ZooKeeper 读取数据库配置，同时，在该节点上注册一个数据变更的 Watcher 监听。

> __配置变更__

数据库配置变更后，对 ZooKeeper 配置节点内容进行更新。此时 ZooKeeper 会将变更通知发送到注册的客户端，客户端接收到通知后，重新获取最新数据。

## 1.2 负载均衡

用来对多个计算机、网络连接、CPU、磁盘驱动或其他资源进行分配负载，以达到优化资源使用、最大化吞吐率、最小化响应时间和避免过载的目的。分为硬件负载均衡和软件负载均衡。

### 1.2.1 ZooKeeper 应用示例

该示例为 `自动化 DNS 服务` 场景。

> __域名注册__

每个服务在启动时，会把自己的域名信息注册到 Register 服务中。例如 A 机器提供 serviceA.xxx.com 服务，它就通过一个 Rest 请求向 Register 发送一个请求: "serviceA.xxx.com -> 192.168.0.1:8080"。

Register 服务获取到数据后，将其写入到 ZooKeeper 对应节点上。

> __域名解析__

客户端在使用该域名时，会向 Dispatcher 发出域名解析请求。

Dispatcher 服务收到请求后，会从 ZooKeeper 指定节点上获取对应 IP:PORT 数据列表，通过一定策略选取其中一个返回给前端应用。

## 1.3 命名服务

被命名的实体可以是集群中的机器、提供的服务地址或远程对象等，统称为名字（Name）。比如 RPC 中的服务地址列表，通过使用命名服务，客户端能够根据名字获取资源的实体、服务地址和提供者信息。

### 1.3.1 ZooKeeper 应用实例

该示例为 `全局唯一 ID` 场景。

以下为使用 ZooKeeper 生成唯一 ID 的步骤:
- a. 所有客户端都会根据自己的任务类型，通过 create() 创建一个顺序节点，例如 `job-` 节点。
- b. 因为每个数据节点都可以维护一份子节点的顺序序列，自动以后缀的形式添加一个序号。所以，创建完毕后，create() 接口会返回一个完整的节点名，如 `job-0000000003`。
- c. 客户端拿到返回值后，拼接上 type 类型，如 `type2-job-0000000003`，就可以作为全局唯一 ID 了。

## 1.4 分布式协调/通知

基于 ZooKeeper 实现分布式协调和通知功能，通常的做法是不同的客户端都对 ZooKeeper 上同一个数据节点进行 Watcher 注册，监听数据变化，如果本身或子节点发生变化，则做出相应处理。

### 1.4.1 ZooKeeper 应用实例

该示例为 `MySQL 数据复制总线`，是一个实时数据复制框架，用于在不同 MySQL 数据实例之间进行异步数据复制和数据变化通知。该示例较为复杂，详情请参考 `《从 Paxos 到 ZooKeeper》 - 6.1.4 分布式协调/通知`。

### 1.4.2 分布式系统机器间通信

大部分分布式系统，机器间的通信包括心跳检测、工作进度汇报、系统调度。

通过使用 ZooKeeper 实现机器间通信，可省去底层网络通信工作，降低系统耦合性。

> __心跳检测__

分布式系统机器之间需要检测彼此是否存在。传统方式，是通过主机之间是否可 PING 通，或者彼此建立 TCP 连接。

通过 ZooKeeper，可以让不同机器在 ZooKeeper 同一个节点下创建临时节点，根据临时节点判断相互是否存活。这种方式也使得系统之间不需要直接相连，减少了耦合。

> __工作进度汇报__

对于任务分发，在 ZooKeeper 上选择一个节点，每个执行任务的客户端在该节点下创建子节点，并将自己的任务执行进度实时写到临时节点上。这样中心系统能够获取到各客户端的执行情况。

> __系统调度__

对于系统调度，管理人员通过控制台操作，对 ZooKeeper 上的节点数据进行操作，数据变更通知到订阅的客户端。


## 1.5 集群管理

集群管理包括集群监控（对集群运行时状态的收集）和集群控制（对集群进行操作和控制）。

管理过程经常进行如下操作:
- 想知道当前有多少机器在工作。
- 对每台机器的运行时状态进行数据收集。
- 对机器进行上下线操作。

传统方式，通过在每台机器上部署一个 Agent，该 Agent 主动向监控中心汇报。该方式有以下弊端:
- Agent 大规模升级困难。
- Agent 无法满足多样化需求，如涉及到应用内部的监控，如消息的消费情况，任务执行情况等。

ZooKeeper 通过对节点数据的监听，每台机器以临时节点的方式进行数据收集，可以对集群进行灵活的检测和处理。

### 1.5.1 ZooKeeper 应用示例: 分布式日志收集系统

日志收集系统分为日志源机器、收集器机器。
- 日志源机器: 就是日志产生的应用机器，每天都在变化（扩容、迁移、硬件问题、网络问题）。
- 收集器机器: 收集日志的机器，本身也在变更、扩容。

该系统需要关注一下几个问题。

> __注册收集器机器__

收集器机器启动时需要在 ZooKeeper 根节点 `/logs/collector` 下创建持久节点 `/logs/collector/[Hostname]`，之所以创建持久节点，是为了保证机器断开或挂掉后，保存的状态数据不丢。

> __任务分发__

系统将不同的日志源机器列表写入到收集器机器创建的节点上，使得每个收集器都对应日志源机器列表。

> __状态汇报__

收集器机器需要创建自己节点的子节点 `/logs/collector/host1/status`，定期写入状态信息（如日志收集进度）。

> __动态分配__

一旦系统检测到有收集器机器变更，就对任务进行重新分配，包括全局动态分配和局部动态分配:
- 全局动态分配: 根据最新的收集器机器列表，对所有日志源机器进行重新分配。该方式存在的问题是，少量机器变更会引起全部重新分配。
- 局部动态分配: 收集器机器会汇报自己的负载（当前收集器的综合评估），这样，在发生收集器机器变更时，系统会将任务分配到负载低的机器。

> __轮询监听__

对于节点状态的变更，采用系统主动轮询的方式（有延时），避免在大量节点情况下通知机制造成的大量消息。

### 1.5.2 ZooKeeper 应用示例: 在线云主机管理

虚拟主机提供商需要对集群进行监控。经常遇到如下需求:
- 想知道当前有多少机器在工作？
- 如何实施监控机器的运行状态？
- 如何获取机器的上下线情况？

传统方式，通过在每台机器上部署一个自定义 Agent，该 Agent 定期主动向监控中心汇报。该方式有以下弊端:
- 需要处理协议设计、网络通信、调度、容灾等，且 Agent 大规模升级困难。
- Agent 无法满足多样化需求，如涉及到应用内部的监控，如消息的消费情况，任务执行情况等。

ZooKeeper 通过对节点数据的监听，每台机器以临时节点的方式进行数据收集，可以对集群进行灵活的检测和处理。

> __机器上下线__

每台机器上部署一个基于 ZooKeeper 实现的 Agent，该 Agent 部署后，首先向 ZooKeeper 指定节点注册，即创建 `/XAE/machine/[Hostname]`。监控 `/XAE/machine` 的系统即会收到上线通知。下线也如此。

> __机器监控__

运行过程中，Agent 定时将主机信息写入 ZooKeeper 指定节点，监控中心通过订阅这些节点获取信息。

## 1.6 Master 选举

可参考 [code: zozospider/note-distributed-zookeeper-video/zookeeper-curator/src/main/java/com/zozospider/zookeepercurator/leader](https://github.com/zozospider/note-distributed-zookeeper-video/tree/master/zookeeper-curator/src/main/java/com/zozospider/zookeepercurator/leader)

## 1.7 分布式锁

可参考 [code: zozospider/note-distributed-zookeeper-video/zookeeper-curator/src/main/java/com/zozospider/zookeepercurator/lock](https://github.com/zozospider/note-distributed-zookeeper-video/tree/master/zookeeper-curator/src/main/java/com/zozospider/zookeepercurator/lock)

对于分布式系统中多个任务操作一个共享资源，那么需要保证彼此之间不干扰。可分为排他锁和共享锁。

### 1.7.1 排他锁

排他锁称为写锁或独占锁。在同一时间，只允许一个事务 T1 对资源 O1 进行读取和更新。其他事务需要等到 T1 释放锁。

> __定义锁__

ZooKeeper 使用一个临时节点如 `/exclusive_lock/lock` 来定义一个锁。

> __获取锁__

客户端通过调用 create() 接口，试图创建临时节点 `/exclusive_lock/lock`。如果创建成功的客户端则获取到了锁。没有创建成功的客户端需要对 `/exclusive_lock` 注册子节点变更 Watcher 监听。

> __释放锁__

以下两种情况会释放锁:
* 获取到锁的客户端发生异常宕机，临时节点被 ZooKeeper 移除。
* 获取到锁的客户端完成业务逻辑后，主动删除临时节点。

临时节点 `/exclusive_lock/lock` 被移除后，ZooKeeper 会通知注册了监听并在等待的客户端，这些客户端会再次执行获取锁逻辑。

### 1.7.2 共享锁

共享锁称为读锁。不同事务可以同时对同一个数据对象进行读取，但是，只能在没有任何事务进行读写的情况下更新。

> __定义锁__

ZooKeeper 使用临时节点如 `/shared_lock/[Hostname]-R-xxxxx` 或 `/shared_lock/[Hostname]-W-xxxxx` 来定义读或写请求锁。

> __获取锁__

客户端创建临时节点 `/shared_lock/192.168.0.1-R-0000000001` 或 `/shared_lock/192.168.0.1-W-0000000001` 来定义读或写请求锁。

会经历如下步骤:
- a. 调用 `create()` 创建节点 `/shared_lock/192.168.0.1-R-0000000001` 或 `/shared_lock/192.168.0.1-W-0000000001`，并在 `/shared_lock` 注册子节点变更的 Watcher 监听。
- b. 调用 `getChildren()` 获取子节点列表，并确定自己在 `/shared_lock` 所有子节点中的顺序。
- c. 如果是读请求: 如果所有比自己小的子节点都是读请求，那么获取锁成功并执行。如果比自己小的子节点中有写请求，则需要等待。
- c. 如果是写请求: 自己必须是所有节点中最小的节点。
- d. 收到 Watcher 监听后，重复步骤。

> 释放锁

以下两种情况会释放锁:
- 获取到锁的客户端发生异常宕机，临时节点被 ZooKeeper 移除。
- 获取到锁的客户端完成业务逻辑后，主动删除临时节点。

临时节点 `/shared_lock/[Hostname]-R-xxxxx` 或 `/shared_lock/[Hostname]-W-xxxxx` 被移除后，ZooKeeper 会通知注册了监听并在等待的客户端，这些客户端会再次执行获取锁逻辑。

### 1.7.3 羊群效应

在大规模客户端情况下，一个客户端删除节点，会使得 ZooKeeper 短时间向大量注册监听的客户端发送通知，客户端也需要重新获取子节点列表。

改进后的版本如下:
- a. 调用 `create()` 创建节点 `/shared_lock/192.168.0.1-R-0000000001` 或 `/shared_lock/192.168.0.1-W-0000000001`，并在 `/shared_lock`（无需注册）。
- b. 调用 `getChildren()` 获取子节点列表，并确定自己在 `/shared_lock` 所有子节点中的顺序。
- c. 如果是读请求: 向比自己小的最后一个写请求节点注册 Watcher 监听。
- c. 如果是写请求: 向比自己小的最后一个节点注册 Watcher 监听。
- d. 收到 Watcher 监听后，重复步骤。

## 1.8 分布式队列

### 1.8.1 分布式屏障

可参考 [code: zozospider/note-distributed-zookeeper-video/zookeeper-curator/src/main/java/com/zozospider/zookeepercurator/barrier](https://github.com/zozospider/note-distributed-zookeeper-video/tree/master/zookeeper-curator/src/main/java/com/zozospider/zookeepercurator/barrier)

规定了一个队列的元素必须都聚集后才能统一进行安排，比如在大规模分布式并行计算时，最后的合并计算需要基于很多并行计算的子节点。

步骤如下:
- a. 定义一个 ZooKeeper `/queue_barrier` 节点，并设置数据为 `10`。
- b. 客户端在 ZooKeeper `/queue_barrier` 节点下创建临时节点，如 `/queue_barrier/192.168.0.1`。
- c. 调用 `getData()` 获取 `/queue_barrier` 节点数据内容: `10`。
- d. 调用 `getChildren` 获取 `/queue_barrier` 节点的所有子节点，并注册对子节点列表的 Watcher 监听。
- e. 统计子节点个数，如果到达 10 个，就往下执行业务逻辑。否则，就等待。
- f. 接受到 Watcher 通知后，重复步骤 4。

---

# 二. ZooKeeper在大型分布式系统中的应用

## 2.1 Hadoop

Hadoop 是 Apache 开源的一个大型分布式计算框架。包括 HDFS, MapReduce, YARN。

在 Hadoop 中，ZooKeeper 主要用于以下两个方面:
- 实现 Hadoop Common 的 HA(High Availability) 模块，HDFS 的 NameNode 和 YARN 的 ResourceManager 都是基于 Hadoop Common 来实现 HA 功能的。
- 为 YARN 存储应用的运行状态。

### 2.1.1 YARN 介绍

YARN 是 Hadoop 为了提高计算节点 Master(JT) 的拓展性，同时为了支持多计算模型和提供资源的细粒度调度而引入的分布式调度框架。可支持如 MapReduce, Tez, Spark, Storm, Imlala, Open MPI 等。

YARN 主要由以下四部分组成:
- ResourceManager(RM): 核心，作为全局的资源管理器，负责整个系统的资源管理和分配。
- NodeManager(NM)
- ApplicationMaster(AM)
- Container

### 2.1.2 ResourceManager(RM) 单点问题

为了解决 ResourceManager(RM) 单点问题，YARN 设计了一套 Active/Standby 模式的 ResourceManager HA 架构。

在运行期间，会有多个 ResourceManager 并存，其中只有一个是 Active 状态，另外为 Standby 状态，当 Active 节点无法工作时，Standby 节点会选举出新的 Active 节点。

以下为涉及到 ZooKeeper 的相关概念:

> __主备切换__

Hadoop Common 包中位于 org.apache.hadoop.ha 中的 ActiveStandbyElector 组件（封装了 ResourceManager 和 ZooKeeper 的通信交互），用来确定 ResourceManager 的状态为 Active 或 Standby（HDFS 的 NameNode 和 ResourceManager 模块也是使用该组件来实现各自的 HA）。

具体步骤如下:
- a. 创建锁节点: 所有 ResourceManager 节点启动时，竞争去写 ZooKeeper 临时节点 `yarn-leader-election/pseudo-yarn-rm-cluster/ActiveStandbyElectorLock`，最终创建成功的节点为 Active 状态，其他为 Standby 状态。
- b. 注册 Watcher 监听: 所有 Standby 节点会向 `yarn-leader-election/pseudo-yarn-rm-cluster/ActiveStandbyElectorLock` 注册一个节点变更的 Watcher 监听。
- c. 主备切换: 当 Active 节点重启或挂掉时，临时节点被删除，其他 Standby 节点收到 ZooKeeper 的变更通知，然后重复步骤 a。

> __Fencing（隔离）__

如果 ResourceManager1 为 Active 状态，但是由于负载过高（GC 占用时间长或 CPU 负载高）或网络闪断，无法对外提供服务，产生 `假死`，此时 ZooKeeper 认为 ResourceManager1 挂了，ResourceManager2 切换为 Active 状态。而 ResourceManager1 依然认为自己为 Active，产生 `脑裂`。如何解决呢？

YARN 引入 Fencing 机制，通过 ZooKeeper 的 ACL 权限控制，某个 RM 创建的节点必须携带 ZooKeeper 的 ACL 信息，以防止其他 RM 更新。

比如 RM1 为 Active 状态，出现假死后，ZooKeeper 将其移除，此时 RM2 创建节点，并切换为 Active。RM1 恢复后试图更新 ZooKeeper 数据，但是失败了，于是就将自己切换为 Standby 状态。

> __ResourceManager 状态存储__

`RMStateStore` 能够存储一些 ResourceManager 的内部状态信息（大部分不需要持久化）。提供以下三种存储方案:
* 基于内存，一般用于日常开发。
* 基于文件系统，如 HDFS。
* 基于 ZooKeeper。

因为存储量不大，官方建议基于 ZooKeeper 实现。存储在 `/rmstore` 根节点上。


## 2.2 HBase

全称 Hadoop Database，是 Google Bigtable 的开源实现，是一个基于 Hadoop 文件系统设计的面向海量数据的高可靠性、高性能、面向列、可伸缩的分布式存储系统。对数据的写入具有强一致性。

ZooKeeper 是 HBase 的核心组件。应用场景包括: 系统冗错、RootRegion 管理、Region 状态管理、分布式 SplitLog 任务管理、Replication 管理、HMaster 的 ActiveMaster 选举、BackupMaster 的实时接管、Table 的 enable/disable 状态记录、几乎所有的元数据存储等。

HBase 中所有对 ZooKeeper 的操作都封装在 org.apache.hadoop.hbase.zookeeper 包中。

### 2.2.1 系统冗错

HBase 启动时，每个 RegionServer 会到 ZooKeeper 的 `/hbase/rs` 下创建节点，如 `/hbase/rs/[Hostname]`。HMaster 监听该节点。当 RegionServer 挂掉（Session 失效）时，ZooKeeper 删除该临时节点，HMaster 接收到通知，则开始冗错工作（HMaster 将该 RegionServer 处理的数据分片（Region）重新路由到其他节点，并记录到 Meta 信息供客户端查询）。

HMaster 之所以本身不负责监控，是因为直接通过心跳机制，随着系统容量增加，HMaster 管理负担越来越重，HMaster 自身有挂掉的可能，因此数据需要持久化。

### 2.2.2 RootRegion 管理

RootRegion 负责数据存储位置的元数据分片。客户端发起请求，会去查 RootRegion 获取数据位置。

RootRegion 自身位置记录在 ZooKeeper 上（默认 `/hbase/root-region-server`）。当 RootRegion 变化，如手工移动或故障，能通过 ZooKeeper 感知，做出相应容灾措施。

### 2.2.3 Region 状态管理

Region 是数据的物理切片，每个 Region 记录了全局数据的一小部分。不同 Region 之间数据不重复。

由于系统故障、负载均衡、配置修改、Region 分裂合并等会引起 Region 经常变更。Region 移动会经历 Offline 和 online 过程。Offline 期间数据不能被访问，且 Region 的这个状态变更必须让全局知晓。高达 10 万级别的 Region 状态管理需要依靠 ZooKeeper 实现。

### 2.2.4 分布式 SplitLog 任务管理

当某个 RegionServer 挂掉时，部分数据还未持久化到 HFile 中。所以需要从 HLog 中恢复内存中的数据。

HMaster 遍历该 RegionServer 的 HLog，并按 Region 切分小块到新地址下，并进行数据的 Replay。

由于 RegionServer 数据量大，HLog 的任务分配给多个 RegionServer 处理，此时，HMaster 会在 ZooKeeper 上创建一个 splitlog 节点（默认 `/hbase/splitlog`），并将哪个 RegionServer 处理哪个 Region 等信息以列表的方式存放到该节点。然后各个 RegionServer 自行到该节点上领取任务并反馈结果。

### 2.2.5 Replication 管理

Replication 为 HBase 实现实时的主备同步功能，使 HBase 拥有了容灾和分流功能，加强了 HBase 的可用性。

实现方式是在 ZooKeeper 上记录 replication 节点（默认 `hbase/replication`），把不同的 RegionServer 服务器对应的 HLog 文件记录到相应节点上，HMaster 会将新增数据推送到 Slave 集群，并将推送信息记录到 ZooKeeper（`断点信息`）。

当服务器挂掉时，HMaster 根据 ZooKeeper 记录的断点信息来协调复制。

### 2.2.6 ZooKeeper 部署

`hbase-env.sh` 中可以选择自带的 ZooKeeper，还是外部的 ZooKeeper。一般建议后者，这样多个 HBase 集群可服用同一套 ZooKeeper 集群。需要注意，此时要为每个 HBase 集群指明对应的 ZooKeeper 根节点配置确保互不干扰。

HBase 客户端需要指明 ZooKeeper 集群地址和对应的 HBase 根节点配置。


## 2.3 Kafka

Kafka 是 LinkedIn 开源的分布式消息系统，目前为 Apache 顶级项目，由 Scala 开发。是一个吞吐量极高的消息系统，是发布与订阅模式系统，没有 "中心主节点" 概念，所有服务器都是对等的。

Kafka 使用 ZooKeeper 作为分布式协调框架，很好的将消息生产、消息存储、消息消费有机地结合起来。同时能够在生产者、Broker、消费者等组件无状态的情况下，建立起了生产者消费者订阅关系。并实现了生产者和消费者的负载均衡。

### 2.3.1 术语介绍

- `Producer（生产者）`: 消息产生的源头，负责生成消息并发送到 Kafka。
- `Consumer（消费者）`: 消息的使用方，负责消费 Kafka 的消息。
- `Topic（主题）`: 由用户定义在 Kafka，用于建立生产者和消费者之间的订阅关系（生产者发送消息到指定 Topic，消费者从该 Topic 消费消息）。
- `Partition（消息分区）`: 用于负载，一个 Topic 下有多个分区，如 `kafka-test` Topic 可以分 4 个分区，两台服务器分别提供 2 个分区，表示为: `0-1`, `0-2`, `1-1`, `1-2`。
- `Broker`: Kafka 服务器。
- `Group（消费者分组）`: 归组同类消费者，多个消费者可在同一个 Topic 下消费。
- `Offset（偏移量）`: 消费者消费消息的过程，消息在文件中的偏移量。

### 2.3.2 Broker 注册

ZooKeeper 负责管理所有 Broker。ZooKeeper 上有一个 `Broker 节点`（`/broker/ids`），在 Broker 启动时，会创建自己的临时节点（`/broker/ids/[0...n]`），其中的全局唯一数据称为 `Broker ID`，每个 Broker 会将自己的 IP 和 port 等信息写入该节点。

### 2.3.3 Topic 注册

同一个 Topic 消息会分成多个 Partition，这些对应关系，由 ZooKeeper 的 `/brokers/topics` 节点记录，该节点称为 `Topic 节点`。每一个 Topic 都以 `brokers/topics/[topic]` 的形式记录，如 `brokers/topics/login` 或 `brokers/topics/search`。

Broker 启动后，会在 Topic 节点下注册 Broker ID，并写入当前 Broker 存储该 Topic 的分区数。如 `brokers/topics/login/3->2` 表示为编号为 3 的 Broker ID 服务器，对名称为 login 的 Topic 提供了 2 个分区。该节点也为临时节点。

### 2.3.4 生产者负载均衡

同一个 Topic 的消息会被分区部署到不同 Broker 服务器上。所以生产者需要将消息负载发送到多个 Broker 上。

Kafka 支持以下两种生产者负载均衡方式:

> __四层负载均衡__

根据生产者的 IP 和 port 来确定关联的 Broker。有以下优缺点:
- 优势: 逻辑简单，只需维护和 Broker 单个 TCP 链接。
- 劣势: 不是真正的负载均衡，每个生产者消息量和每个 Broker 消息存储不一样，接收总数不均匀。另外，生产者也无法感知 Broker 的新增删除。

> __使用 ZooKeeper 进行负载均衡__

每个 Broker 启动时，完成注册，注册一些如 "有哪些可订阅的 Topic"。生产者通过该节点变化感知 Broker 服务器列表的变更。

生产者会对 ZooKeeper 上的如 "Broker 新增减少"、"Topic 新增减少"、"Broker 和 Topic 关系变化" 等事件注册 Watcher 监听，这样就可以实现动态负载均衡。

### 2.3.5 消费者负载均衡

多个消费者需要进行负载均衡，合理的从 Broker 服务器接收消息。

不同消费者分组消费特定 Topic 消息，互不干扰。每个消费者分组中包含多个消费者，每条消息只会发送给分组中的一个消费者。

> __消息分区与消费者关系__

每个消费者分组都有一个全局唯一 `Group ID`，每个消费者都有一个 `Consumer ID`，通常采用如 `Hostname:UUID` 形式。

每个消息分区只能有一个消费者消费，因此，ZooKeeper 需要记录分区与消费者的关系。每个消费者一旦确定对一个分区的消费权利，那么需要将 Consumer ID 写入对应消费的分区的临时节点上，如 `/consumers/[group_id]/owners/[topic]/[broker_id-partition_id]`，其中 `[broker_id-partition_id]` 表示某个 Broker 上的某个分区，节点数据内容则为消费该分区的 Consumer ID。

> __消息消费进度 Offset 记录__

消费者在指定分区进行消费时，需要定时将分区消息的消费进度（即 Offset）记录到 ZooKeeper 上。以便该消费者重启或其他消费者接管该分区后，能够获取之前进度。

ZooKeeper 记录 Offset 到节点路径为 `/consumers/[group_id]/offsets/[topic]/[broker_id-partition_id]`，节点数据内容则为 Offset 值。

> __消费者注册__

消费者服务器加入消费者分组过程如下:
- a. 注册到消费者分组: 消费者服务器启动时，创建临时节点如 `/consumers/[group_id]/ids/[consumer_id]`。然后将自己订阅的 Topic 信息写入该节点。
- b. 注册监听消费者分组中的消费者: 消费者需要对 `/consumers/[group_id]/ids` 注册子节点变化的 Watcher 监听，一旦同分组下有消费者变化，触发消费者负载均衡。
- c. 注册监听 Broker 服务器: 消费者需要对 `/broker/ids/[0...n]` 注册节点变化的 Watcher 监听，如果 Broker 服务器列表变化，根据情况决定是否进行消费者负载均衡。
- d. 进行消费者负载均衡: 让同一个 Topic 下不同分区的消息均衡的被多个消费者消费（通过一套特殊的消费者负载均衡算法）。

---

# 三. ZooKeeper在阿里巴巴的实践与应用

## 3.1 案例1 消息中间件: Metamorphosis

Matamorphosis 是阿里巴巴中间件团队开源的一个 Java 消息中间件，项目地址 [GitHub-killme2008-Metamorphosis](http:/github.com/killme2008/Metamorphosis)。详情请参考 `《从 Paxos 到 ZooKeeper》 - 6.3.1 案例1 消息中间件: Metamorphosis`。

## 3.2 案例2 RPC服务款姐: Dubbo

Dubbo 是阿里巴巴开源的一个由 Java 编写的分布式服务框架，项目地址 [GitHub-alibaba-dubbo](https://github.com/alibaba/dubbo)。

Dubbo 核心包括以下:
- 远程通信
- 集群容错
- 自动发现: 提供基于注册中心的目录服务，使服务消费方能够动态查找服务提供方。
- 其他: 其他还包括服务对象序列化 Serialize 组件，网络传输组件 Transport，协议层 Protocol，服务注册中心 Registry等。



## 3.3 案例3 基于MySQL Binlog的增量订阅和消费组件: Canal


## 3.4 案例4 分布式数据库同步系统: Otter


## 3.5 案例5 轻量级分布式通用搜索平台: 终搜


## 3.6 案例6 实时计算引擎

