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

Chubby 客户端如何定位 Master 服务器：Chubby 客户端通过 DNS 获取 Chubby 服务器列表。逐个询问是否为 Chubby Master，非 Master 服务器会将 Master 标识反馈给客户端，这样客户端就可以快速定位 Chubby Master。

Chubby 客户端的读写：Chubby 客户端获取 Chubby Master 后，所有读写都在该 Chubby Master 上。写请求，Chubby Master 会采用一致性协议广播给所有服务器，过半服务器接受了写请求后，再响应给客户端。读请求，不需要广播，Chubby Master 单独处理。

Chubby Master 服务器崩溃怎么办：其他服务器在 Master 租期到期后，重新选举（几秒钟）。

非 Chubby Master 服务器崩溃怎么办：整个集群继续工作。崩溃的服务器在恢复后自动加入 Chubby 集群。同步最新数据以后，就可以加入正常的 Paxos 运作流程。

怎么更新或加入新机器：更新 DNS 列表，Chubby Master 会轮询 DNS 列表，然后将集群数据库中的地址列表更新，其他服务器通过复制获取到最新列表。

### 目录与文件

### 锁与锁序列器

### Chubby 中的事件通知机制

### Chubby 中的缓存

### 会话和会话激活

### KeepAlive 请求

### 会话超时

### Chubby Master 故障恢复


## Paxos 协议实现

---

# Hypertable

## 概述

## 算法实现

