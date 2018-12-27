
# Document & Code

* [../Zookeeper-book](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-book.md)

---

# 一. 初识 ZooKeeper

## ZooKeeper 介绍

ZooKeeper 是一个开源的分布式协调服务。提供统一命名服务、配置管理、分布式锁。通过 ZAB（ZooKeeper Atomic Broadcast） 协议解决分布式数据一致性问题。

目标是将复杂的分布式一致性服务封装成一系列简单易用的接口服务。

Google Chubby 的开源实现，雅虎创建，2010年成为 Apache 顶级项目。

### ZooKeeper 是什么

应用程序可基于 ZooKeeper 实现数据发布/订阅、负载均衡、命名服务、分布式协调/通知、集群管理、Master 选举、分布式锁、分布式队列等。

ZooKeeper 可保证如下一致性特征：
* a. 顺序一致性（一个客户端发起的事务请求，最终会顺序执行）
* b. 原子性（事务请求结果，在所有机器上情况一致，要么都应用，要么都不应用）
* c. 单一试图（客户端连接的任何服务端数据模型都是一致的）
* d. 可靠性（一旦事务成功，状态会一直保留）
* e. 实时性（保证在事务执行后的一段时间内，客户端最终能从服务端读取到最新的数据）

### ZooKeeper 的设计目标

提供一个高性能（吞吐量大的分布式系统）、高可用（解决单点问题）、严格顺序访问控制能力（客户端可基于 ZooKeeper 实现复杂的同步原语）。

以下为 ZooKeeper 的四个设计目标：
* 1. 简单的数据模型（一系列 ZNode 数据节点组成一个共享的树形结构，全部数据存储在内存中）
* 2. 可以构建集群（一般 3~5 台机器，每台机器在内存中维护服务器状态。超过一半机器正常，整个集群就可正常服务。客户端选择任意一台机器创建连接，连接断开后可自动重连）
* 3. 顺序访问（客户端每个更新请求，Zookeeper 会分配唯一递增编号）
* 4. 高性能（全量数据存储在内存中，适合高性能读取）

## ZooKeeper 的基本概念

### 集群角色

Master/Slave 模式（主备模式）：Master 负责写，Slave 通过异步复制获取最新数据，提供读服务。

ZooKeeper 采用 Leader、Follower、Observer：
* Leader: 选举产生，为客户端提供读写服务。
* Follower: 提供读，参与选举，参与写操作的 "过半写成功" 策略。
* Observer: 提供读。提高读性能。

### 会话（Session）

会话指客户端和服务端的一个 TCP 长连接。默认端口 2181.

建立连接后，通过心跳检测保持有效。可向 ZooKeeper 服务器发送请求，接受响应。可接受 ZooKeeper 服务器的 Watch 事件。

客户端和服务端连接断开时，只要在 sessionTimeout 超时时间内，能够重连任意一台服务器，那么会话仍然有效。

### 数据节点（ZNode）

数据模型为一棵树（ZNode Tree），每个 ZNode 会保存自己的数据内容和属性信息。

ZNode 分为持久节点和临时节点：
* 持久节点：一旦 ZNode 创建，除非主动移除，将一直保持。
* 临时节点：生命周期和客户端会话绑定，会话失效后被移除。

### 版本

ZooKeeper 为每个 ZNode 维护一个 Stat 数据结构，记录了如下三个数据版本：
* version: 当前 ZNode 的版本。
* cversion: 当前 ZNode 子节点的版本。
* aversion: 当前 ZNode 的 ACL 版本。

### 事件监听器（Watcher）

ZooKeeper 允许用户在指定节点上注册一些 Watcher，在事件触发时，会通知到客户端。

### ACL（Access Control Lists）

ZooKeeper 采用 ACL 策略进行权限控制。

以下为 ZooKeeper 的 5 种权限（其中，CREATE 和 DELETE 为针对子节点的权限）：
* CREATE: 创建子节点。
* READ: 获取节点数据和子节点列表。
* WRITE: 更新节点数据。
* DELETE: 删除子节点数据。
* ADMIN: 设置节点 ACL 的权限。

# 二. ZooKeeper 的 ZAB 协议

参考 [ZooKeeper ZAB协议](https://www.jianshu.com/p/3fec1f8bfc5f)


## ZAB 协议



## 协议介绍



## 深入 ZAB 协议



## ZAB 与 Paxos 算法的联系与区别



