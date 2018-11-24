# Document & Code

* [../Zookeeper-book](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-book.md)

---

# 一. Chubby

Google Chubby 为 GFS 和 BigTable 解决分布式协作，元数据存储，Master选举等一系列与分布式锁服务相关问题。

Chubby 的底层一致性实现就是以 Paxos 算法为基础的。

## 概述

一个分布式锁服务的目的是：允许它的客户端同步彼此的操作，并对当前环境的基本状态信息达成一致。

应用程序通过 Chubby 的提供的客户端接口，可以做到如下：
* a. 对 Chubby 服务器上的文件进行读写操作。
* b. 添加对文件节点的锁控制。
* c. 订阅 Chubby 服务端发出的文件变动事件通知。 

## 应用场景

Chubby 有如下应用场景：
* a. 为 GFS 和 BigTable 实现 Master 选举。
* b. Master 借助 Chubby，方便地感知所控制的节点。
* c. BigTable 的客户端可以方便定位当前集群的 Master。
* d. GFS 和 BigTable 使用 Chubby 进行元数据存储。

## 设计目标

Chubby 的设计目标包括以下：
* 对上层应用程序的侵入性更小（应用程序通过调用接口，更易于保持自身的程序接口和网络通信模式）
* 提供数据的发布与订阅（允许客户端在服务器进行小文件的读取，减少客户端依赖的外部服务）
* 提供基于锁的接口（为开发人员提供一套和单机锁机制类似的分布式锁服务）
* 更可靠的服务（"过半机制（Quorum 机制）"要求集群中过半机器达成一致，3 台以上机器可提供正常服务，一般为 5 台）
* 完整的、独立的分布式锁服务（）
* 提供细粒度的锁服务（客户端获得锁后进行长时间持有（小时/天），保持锁所有锁的持有状态）
* 提供对小文件的读写功能（Master 节点，不需要依赖其他服务，直接向文件写入 Master 信息，其他客户端就可读取）
* 高可用、高可靠（3 台以上机器可提供正常服务，必须支撑成百上千个客户端对同一个文件的监视和读取）
* 提供事件通知机制（客户端需要实时感知 Master 的变化。Chubby 可将服务端的数据变化通过事件通知订阅的客户端）

## Chubby 技术架构

### 系统结构

Chubby 集群，通过投票，过半投票的服务器作为 Master。Mster 通过不断续租延长 Master 期限。如果 Master 出现故障，其他服务器进行新的选举产生 Master，开始新的租期。

Chubby 集群中每个服务器使用 Paxos 协议从 Master 服务器同步数据。在实际运行中，只有 Master 服务器才能写数据库。

Chubby 客户端如何定位 Chubby Master 服务器：Chubby 客户端通过 DNS 获取 Chubby 服务器列表。逐个询问是否为 Chubby Master，非 Master 服务器会将 Master 标识反馈给客户端，这样客户端就可以快速定位 Chubby Master。

Chubby 客户端的读写：Chubby 客户端获取 Chubby Master 后，所有读写都在该 Chubby Master 上。写请求，Chubby Master 会采用一致性协议广播给所有服务器，过半服务器接受了写请求后，再响应给客户端。读请求，不需要广播，Chubby Master 单独处理。

Chubby Master 服务器崩溃怎么办：其他服务器在 Master 租期到期后，重新选举（几秒钟）。

非 Chubby Master 服务器崩溃怎么办：整个集群继续工作。崩溃的服务器在恢复后自动加入 Chubby 集群。同步最新数据以后，就可以加入正常的 Paxos 运作流程。

怎么更新或加入新机器：更新 DNS 列表，Chubby Master 会轮询 DNS 列表，然后将集群数据库中的地址列表更新，其他服务器通过复制获取到最新列表。

### 目录与文件

`/ls/foo/wombat/pouch`：
* ls: 所有 Chubby 节点共有的前缀，表示锁服务（Lock Service）。
* foo: Chubby 集群名字。
* wombat/pouch: 业务节点名字。

数据节点指 Chubby 的文件或目录，每个数据节点分为持久节点和临时节点。
* 持久节点: 需要显式调用接口 API 来删除。
* 临时节点: 生命周期和客户端会话绑定，客户端会话失效后自动删除，可用来进行客户端会话有效性的判断。

每个数据节点包含元数据信息，包括：
* a. 用于权限控制的访问控制列表（ACL）信息。
* b. 实例编号（标识 Chubby 创建该数据节点的顺序，创建时间晚的数据节点，编号一定大于之前创建的）
* c. 文件内容编号（只针对文件，用于标识文件内容的变化情况，内容被写入时增加）
* d. 锁编号（标识节点锁状态变更情况，在节点锁由自由状态（free）变为持有状态（held）时增加）
* e. ACL 编号（标识节点的 ACL 信息变更，在节点的 ACL 配置信息被写入时增加）

### 锁与锁序列器

分布式锁错乱

Chubby 的任何一个节点都可以作为读写锁，包括：
* 排他（写）锁：单个客户端持有。
* 共享（读）锁：任意数目客户端持有。

Chubby 通过锁延迟和锁序列号解决分布式锁问题：
* 锁延迟（lock-delay）：如果客户端主动释放锁，那么其他客户端可立即获得该锁。如果客户端异常，那么 Chubby 服务端会为该锁保留一段时间。该机制保护了消息延迟出现的数据不一致现象。
* 锁序列器：锁持有者可向 Chubby 请求一个锁序列器（锁名，排他/共享模式，锁序号）。当客户端需要锁操作时，可以将锁序列器发送给服务端。Chubby 会检测是否有效。

### Chubby 中的事件通知机制

客户端向 Chubby 服务端注册事件，当触发事件时，服务端会发送事件通知（异步）。

场景的 Chubby 事件如下：
* a. 文件内容变更
* b. 节点删除
* c. 子节点新增、删除
* d. Master 服务器转移

### Chubby 中的缓存

Chubby 会在客户端对文件内容和元数据提供缓存。并通过租期机制保证缓存一致性。

如何保证缓存强一致性：当文件内容或原数据要被修改时，Chubby 服务端先阻塞该修改操作，然后 Master 向缓存了该数据的客户端发送缓存过期信号，使其缓存失效，收到所有客户端应答后再进行修改。

### 会话和会话激活

客户端和服务端之间通过 TCP 进行网络通信（Session），他们通过心跳检测保持 Session 活性（KeepAlive / 会话激活），KeepAlive 将 Session 一致延续。

### KeepAlive 请求

Master 收到客户端发送的 KeepAlive 请求，会将该请求阻塞，等到租期即将过期，才续租，再响应该 KeepAlive 请求，并将最新租期反馈给客户端。客户端收到后，立即发起 KeepAlive 请求，Master 再次阻塞...

### 会话超时

客户端会在本地维持一个和 Master 端近似的会话租期，但是存在响应的网络传输时间和时钟不一致现象。

如果客户端的本地会话租期过期，但是尚未收到 Master 的 KeepAlive 响应，那么进入"危险状态"，此时客户端清空本地缓存，宽限期默认等待 45 秒，并通知上层应用一个 "jeopardy" 事件。在此期间如果成功 KeepAlive，客户端会再次开启本地缓存，并通知上层应用一个 "safe" 事件。否则，中止本次会话，并通知上层应用一个 "expired" 事件。

### Chubby Master 故障恢复

Chubby Master 有一个会话租期计时器，如果 Master 故障，计时器会停止，新的 Master 产生后，计时器继续。相当于延长了客户端的会话租期。

故障恢复执行流程：
* 1. Master 维持会话租期 "lease M1"，客户端维持会话租期 "lease C1"，客户端发送的 KeepAlive 被 Master 阻塞。
* 2. Master 反馈 KeepAlive 响应 2，开始会话租期 "lease M2"，客户端收到 KeepAlive 后，开始新的会话租期 "lease C2"，并发送新的 KeepAlive 请求 3。
* 3. Master 故障，无法响应客户端的 KeepAlive 请求 3，然后客户端的会话租期 "lease C2" 过期，清空本地缓存，进入宽限期等待，阻塞应用程序对它的调用请求，并通知上层应用一个 "jeopardy" 事件。
* 4. 选举出新 Master，为该客户端初始化新会话租期 "lease M3"，客户端发送 KeepAlive 请求 4，Master 检测到该客户端的 Master 周期号过期了，在客户端发送 KeepAlive 请求 5 时，返回最近的 Master 周期号。
* 5. 客户端携带最新的 Master 周期号发送 KeepAlive 请求 6，后续会话恢复正常。

新 Master 产生后的处理：
* 1. 确定 Master 周期，拒绝携带其他 Master 周期的客户端请求，并告知新 Master 周期。
* 2. 响应客户端的 Master 寻址。
* 3. 根据会话和锁信息构建服务器内存状态。处理客户端的 KeepAlive 请求。
* 4. 发送 "Master 故障切换" 事件给每一个会话，客户端收到后，清空本地缓存，警告上层应用。Master 等待直到每一个会话都应答了该切换事件。
* 5. 处理所有请求。

## Paxos 协议实现

Chubby 服务端架构分为三层：
* a. 容错日志系统（Fault-Tolerant Log）：保证集群所有机器的日志完全一致，并具有容错性。 
* b. 容错数据库（Fault-Tolerant DB）：Key-Value 数据库，通过容错日志系统保证一致性和容错性。
* c. 分布式锁服务和小文件存储服务

Paxos 算法的作用就在于保证集群内各个副本节点的日志能够保证一致。

### 算法实现

...

---

# Hypertable

Hypertable 是一个使用 C++ 开发的开源、高性能、可伸缩的数据库。

以 Google BigTable 论文为基础知道，和 HBase 分布式模型相似。目的是构建一个针对分布式海量数据的高并发数据库。

## 概述

只支持基本的增、删、改、查，不支持事务处理和关联查询。少量数据的查询性能和吞吐量不如传统数据库。

相比传统数据库的优势：
* a. 支持高并发。
* b. 支持海量数据管理。
* c. 可水平扩展。
* d. 可用性高，容错性好，节点失效不影响数据完整性。

Hypertable 核心组件包括 DFS Broker、RangeServer、Master、Hyperspace。

### DFS Broker

用于衔接 Hypertable 和底层分布式文件系统的抽象层。所有对文件系统的读写操作，都是通过 DFS Broker 完成。

可以接入的分布式文件系统包括：HDFS、MapR、Ceph、KFS 等。

### RangeServer

对外提供服务的组件，负责数据读写。

对每一张表按主键切分，形成多个 Range。每个 Range 由一个 RangeServer 管理（调用 DFS Broker 进行数据读写）。多个 RangeServer 由 Master 管理。

### Master

管理：创建表、删除表、表空间变更。

检测 RangeServer 工作状态。RangeServer 宕机后，重新分配 Range。

### Hyperspace

类似 Google Chubby，提供高效、可靠的在分布式锁服务，元数据管理，主机选举服务。保证数据一致性。

## 算法实现

Hyperspace 集群选举一个服务器作为 Active Server（真正对外服务），其余为 Standby Server。Active Server 和 Standby Server 进行数据和状态同步。

Active Server 选举过程：服务器事务日志更新时间最新的为 Active Server，其余 Standby Server 进行数据同步（即下文的 BDB 同步）。

Master 连上 Hyperspace 集群任意一台服务器后，如果为 Standby Server，会告知 Active Server 地址，Master 再重新连接 Active Server。

Hyperspace 集群底层通过 BDB 集群实现分布式数据一致性。对于 Master 发送给 Hyperspace 的事务请求，Hyperspace 会发送到 BDB 的主节点。（如： 5 台 Hyperspace 集群中的 Active Server 处理建表请求时，需要获得 3 台以上 BDB 服务器同意才能写入）

