
# Document & Code

* [../Zookeeper-book](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-book.md)

---

# 一. 典型应用场景及实现

## 数据发布/订阅

发布/订阅系统一般有两种模式：
* 推（Push）模式：服务端主动将数据更新发送给所有订阅的客户端。
* 拉（Pull）模式：客户端主动请求服务端获取最新数据，通常采用定时轮询拉取方式。

ZooKeeper 采用推（Push）拉（Pull）结合。客户端向服务端注册关注的节点，一旦该节点变化，服务端会向注册客户端发送 Watcher 事件通知（推），客户端收到通知后，需要主动到服务端获取最新数据（拉）。

数据一般为机器列表，运行时的开关配置，数据库配置等全局配置。具有以下特性：
* 数据量较小
* 数据在运行时会发生变化
* 集群中配置一致，各机器共享

### ZooKeeper 应用示例

该示例为 `数据库切换` 场景，客户端从 ZooKeeper 获取数据库配置，在 ZooKeeper 的数据库配置发生变化的时候，客户端需要作出相应更新。

1. 配置存储

一般存储在 ZooKeeper 的 `/configer/app1/database_config` 节点，内容如下：
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

2. 配置获取

集群中每台机器在启动阶段，首先从 ZooKeeper 读取数据库配置，同时，在该节点上注册一个数据变更的 Watcher 监听。

3. 配置变更

数据库配置变更后，对 ZooKeeper 配置节点内容进行更新。此时 ZooKeeper 会将变更通知发送到注册的客户端，客户端接收到通知后，重新获取最新数据。

## 负载均衡

用来对多个计算机、网络连接、CPU、磁盘驱动或其他资源进行分配负载，以达到优化资源使用、最大化吞吐率、最小化响应时间和避免过载的目的。分为硬件负载均衡和软件负载均衡。

### ZooKeeper 应用示例

该示例为 `自动化 DNS 服务` 场景。

1. 域名注册

每个服务在启动时，会把自己的域名信息注册到 Register 服务中。例如 A 机器提供 serviceA.xxx.com 服务，它就通过一个 Rest 请求向 Register 发送一个请求: "serviceA.xxx.com -> 192.168.0.1:8080"。

Register 服务获取到数据后，将其写入到 ZooKeeper 对应节点上。

2. 域名解析

客户端在使用该域名时，会向 Dispatcher 发出域名解析请求。

Dispatcher 服务收到请求后，会从 ZooKeeper 指定节点上获取对应 IP:PORT 数据列表，通过一定策略选取其中一个返回给前端应用。

## 命名服务

被命名的实体可以是集群中的机器、提供的服务地址或远程对象等，统称为名字（Name）。比如 RPC 中的服务地址列表，通过使用命名服务，客户端能够根据名字获取资源的实体、服务地址和提供者信息。

### ZooKeeper 应用实例

该示例为 `全局唯一 ID` 场景。

以下为使用 ZooKeeper 生成唯一 ID 的步骤：

1. 所有客户端都会根据自己的任务类型，通过 create() 创建一个顺序节点，例如 `job-` 节点。

2. 因为每个数据节点都可以维护一份子节点的顺序序列，自动以后缀的形式添加一个序号。所以，创建完毕后，create() 接口会返回一个完整的节点名，如 `job-0000000003`。

3. 客户端拿到返回值后，拼接上 type 类型，如 `type2-job-0000000003`，就可以作为全局唯一 ID 了。

## 分布式协调/通知

基于 ZooKeeper 实现分布式协调和通知功能，通常的做法是不同的客户端都对 ZooKeeper 上同一个数据节点进行 Watcher 注册，监听数据变化，如果本身或子节点发生变化，则做出相应处理。

### ZooKeeper 应用实例

该示例为 `MySQL 数据复制总线`，是一个实时数据复制框架，用于在不同 MySQL 数据实例之间进行异步数据复制和数据变化通知。该示例较为复杂，详情请参考 `《从 Paxos 到 ZooKeeper》 - 6.1.4 分布式协调/通知`。

### 分布式系统机器间通信

大部分分布式系统，机器间的通信包括心跳检测、工作进度汇报、系统调度。

通过使用 ZooKeeper 实现机器间通信，可省去底层网络通信工作，降低系统耦合性。

1. 心跳检测

分布式系统机器之间需要检测彼此是否存在。传统方式，是通过主机之间是否可 PING 通，或者彼此建立 TCP 连接。

通过 ZooKeeper，可以让不同机器在 ZooKeeper 同一个节点下创建临时节点，根据临时节点判断相互是否存活。这种方式也使得系统之间不需要直接相连，减少了耦合。

2. 工作进度汇报

对于任务分发，在 ZooKeeper 上选择一个节点，每个执行任务的客户端在该节点下创建子节点，并将自己的任务执行进度实时写到临时节点上。这样中心系统能够获取到各客户端的执行情况。

3. 系统调度

对于系统调度，管理人员通过控制台操作，对 ZooKeeper 上的节点数据进行操作，数据变更通知到订阅的客户端。


## 集群管理

集群管理包括集群监控（对集群运行时状态的收集）和集群控制（对集群进行操作和控制）。

管理过程经常进行如下操作：
* 想知道当前有多少机器在工作。
* 对每台机器的运行时状态进行数据收集。
* 对机器进行上下线操作。

传统方式，通过在每台机器上部署一个 Agent，该 Agent 主动向监控中心汇报。该方式有以下弊端：
* Agent 大规模升级困难。
* Agent 无法满足多样化需求，如涉及到应用内部的监控，如消息的消费情况，任务执行情况等。

ZooKeeper 通过对节点数据的监听，每台机器以临时节点的方式进行数据收集，可以对集群进行灵活的检测和处理。

### ZooKeeper 应用示例: 分布式日志收集系统

日志收集系统分为日志源机器、收集器机器。
* 日志源机器: 就是日志产生的应用机器，每天都在变化（扩容、迁移、硬件问题、网络问题）。
* 收集器机器: 收集日志的机器，本身也在变更、扩容。

该系统需要关注一下几个问题。

1. 注册收集器机器

收集器机器启动时需要在 ZooKeeper 根节点 `/logs/collector` 下创建持久节点 `/logs/collector/[Hostname]`，之所以创建持久节点，是为了保证机器断开或挂掉后，保存的状态数据不丢。

2. 任务分发

系统将不同的日志源机器列表写入到收集器机器创建的节点上，使得每个收集器都对应日志源机器列表。

3. 状态汇报

收集器机器需要创建自己节点的子节点 `/logs/collector/host1/status`，定期写入状态信息（如日志收集进度）。

4. 动态分配

一旦系统检测到有收集器机器变更，就对任务进行重新分配，包括全局动态分配和局部动态分配：
* 全局动态分配: 根据最新的收集器机器列表，对所有日志源机器进行重新分配。该方式存在的问题是，少量机器变更会引起全部重新分配。
* 局部动态分配: 收集器机器会汇报自己的负载（当前收集器的综合评估），这样，在发生收集器机器变更时，系统会将任务分配到负载低的机器。

5. 轮询监听

对于节点状态的变更，采用系统主动轮询的方式（有延时），避免在大量节点情况下通知机制造成的大量消息。

### ZooKeeper 应用示例: 在线云主机管理

虚拟主机提供商需要对集群进行监控。经常遇到如下需求：
* 想知道当前有多少机器在工作？
* 如何实施监控机器的运行状态？
* 如何获取机器的上下线情况？

传统方式，通过在每台机器上部署一个自定义 Agent，该 Agent 定期主动向监控中心汇报。该方式有以下弊端：
* 需要处理协议设计、网络通信、调度、容灾等，且 Agent 大规模升级困难。
* Agent 无法满足多样化需求，如涉及到应用内部的监控，如消息的消费情况，任务执行情况等。

ZooKeeper 通过对节点数据的监听，每台机器以临时节点的方式进行数据收集，可以对集群进行灵活的检测和处理。

1. 机器上下线

每台机器上部署一个基于 ZooKeeper 实现的 Agent，该 Agent 部署后，首先向 ZooKeeper 指定节点注册，即创建 `/XAE/machine/[Hostname]`。监控 `/XAE/machine` 的系统即会收到上线通知。下线也如此。

2. 机器监控

运行过程中，Agent 定时将主机信息写入 ZooKeeper 指定节点，监控中心通过订阅这些节点获取信息。

## Master 选举

可参考 [code: zozospider/note-distributed-zookeeper-video/zookeeper-curator/src/main/java/com/zozospider/zookeepercurator/leader](https://github.com/zozospider/note-distributed-zookeeper-video/tree/master/zookeeper-curator/src/main/java/com/zozospider/zookeepercurator/leader)

## 分布式锁

可参考 [code: zozospider/note-distributed-zookeeper-video/zookeeper-curator/src/main/java/com/zozospider/zookeepercurator/lock](https://github.com/zozospider/note-distributed-zookeeper-video/tree/master/zookeeper-curator/src/main/java/com/zozospider/zookeepercurator/lock)

对于分布式系统中多个任务操作一个共享资源，那么需要保证彼此之间不干扰。可分为排他锁和共享锁。

### 排他锁

排他锁称为写锁或独占锁。在同一时间，只允许一个事务 T1 对资源 O1 进行读取和更新。其他事务需要等到 T1 释放锁。

1. 定义锁

ZooKeeper 使用一个临时节点如 `/exclusive_lock/lock` 来定义一个锁。

2. 获取锁

客户端通过调用 create() 接口，试图创建临时节点 `/exclusive_lock/lock`。如果创建成功的客户端则获取到了锁。没有创建成功的客户端需要对 `/exclusive_lock` 注册子节点变更 Watcher 监听。

3. 释放锁

以下两种情况会释放锁：
* 获取到锁的客户端发生异常宕机，临时节点被 ZooKeeper 移除。
* 获取到锁的客户端完成业务逻辑后，主动删除临时节点。

临时节点 `/exclusive_lock/lock` 被移除后，ZooKeeper 会通知注册了监听并在等待的客户端，这些客户端会再次执行获取锁逻辑。

### 共享锁

共享锁称为读锁。不同事务可以同时对同一个数据对象进行读取，但是，只能在没有任何事务进行读写的情况下更新。

1. 定义锁

ZooKeeper 使用临时节点如 `/shared_lock/[Hostname]-R-xxxxx` 或 `/shared_lock/[Hostname]-W-xxxxx` 来定义读或写请求锁。

2. 获取锁

客户端创建临时节点 `/shared_lock/192.168.0.1-R-0000000001` 或 `/shared_lock/192.168.0.1-W-0000000001` 来定义读或写请求锁。

会经历如下步骤：
* a. 调用 `create()` 创建节点 `/shared_lock/192.168.0.1-R-0000000001` 或 `/shared_lock/192.168.0.1-W-0000000001`，并在 `/shared_lock` 注册子节点变更的 Watcher 监听。
* b. 调用 `getChildren()` 获取子节点列表，并确定自己在 `/shared_lock` 所有子节点中的顺序。
* c. 如果是读请求: 如果所有比自己小的子节点都是读请求，那么获取锁成功并执行。如果比自己小的子节点中有写请求，则需要等待。
* c. 如果是写请求: 自己必须是所有节点中最小的节点。
* d. 收到 Watcher 监听后，重复步骤。

4. 释放锁

以下两种情况会释放锁：
* 获取到锁的客户端发生异常宕机，临时节点被 ZooKeeper 移除。
* 获取到锁的客户端完成业务逻辑后，主动删除临时节点。

临时节点 `/shared_lock/[Hostname]-R-xxxxx` 或 `/shared_lock/[Hostname]-W-xxxxx` 被移除后，ZooKeeper 会通知注册了监听并在等待的客户端，这些客户端会再次执行获取锁逻辑。

### 羊群效应

在大规模客户端情况下，一个客户端删除节点，会使得 ZooKeeper 短时间向大量注册监听的客户端发送通知，客户端也需要重新获取子节点列表。

改进后的版本如下:
* a. 调用 `create()` 创建节点 `/shared_lock/192.168.0.1-R-0000000001` 或 `/shared_lock/192.168.0.1-W-0000000001`，并在 `/shared_lock`（无需注册）。
* b. 调用 `getChildren()` 获取子节点列表，并确定自己在 `/shared_lock` 所有子节点中的顺序。
* c. 如果是读请求: 向比自己小的最后一个写请求节点注册 Watcher 监听。
* c. 如果是写请求: 向比自己小的最后一个节点注册 Watcher 监听。
* d. 收到 Watcher 监听后，重复步骤。

## 分布式队列

### 分布式屏障

可参考 [code: zozospider/note-distributed-zookeeper-video/zookeeper-curator/src/main/java/com/zozospider/zookeepercurator/barrier](https://github.com/zozospider/note-distributed-zookeeper-video/tree/master/zookeeper-curator/src/main/java/com/zozospider/zookeepercurator/barrier)

规定了一个队列的元素必须都聚集后才能统一进行安排，比如在大规模分布式并行计算时，最后的合并计算需要基于很多并行计算的子节点。

步骤如下:

1. 定义一个 ZooKeeper `/queue_barrier` 节点，并设置数据为 `10`。

2. 客户端在 ZooKeeper `/queue_barrier` 节点下创建临时节点，如 `/queue_barrier/192.168.0.1`。

3. 调用 `getData()` 获取 `/queue_barrier` 节点数据内容: `10`。

4. 调用 `getChildren` 获取 `/queue_barrier` 节点的所有子节点，并注册对子节点列表的 Watcher 监听。

5. 统计子节点个数，如果到达 10 个，就往下执行业务逻辑。否则，就等待。

6. 接受到 Watcher 通知后，重复步骤 4。

---

# 二. ZooKeeper在大型分布式系统中的应用

## Hadoop


## HBase


## Kafka


# 三. ZooKeeper在阿里巴巴的实践与应用

## 案例1 消息中间件：Metamorphosis


## 案例2 RPC服务款姐：Dubbo


## 案例3 基于MySQL Binlog的增量订阅和消费组件：Canal


## 案例4 分布式数据库同步系统：Otter


## 案例5 轻量级分布式通用搜索平台：终搜


## 案例6 实时计算引擎


