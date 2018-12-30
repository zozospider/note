# Document & Code

* [../Zookeeper-book](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-book.md)

---

# 一 系统模型

## 1.1 数据模型

ZooKeeper 使用 `数据节点`，称为 `ZNode`，是 ZooKeeper 中数据的最小单元，每个 ZNode 可以保存数据，也可以挂载子节点。

## 1.2 节点特性

ZooKeeper 的每个数据节点都是有生命周期的，节点类型可分为: 持久节点(`PERSISTENT`), 临时节点(`EPHEMERAL`), 顺序节点(`SEQUENTIAL`)。可生成以下四种组合类型:
- 持久节点(`PERSISTENT`): 是指数据节点被创建后，会一直存在 ZooKeeper 服务器上，直到有主动删除操作。
- 持久顺序节点(`PERSISTENT_SEQUENTIAL`): 在持久节点基础上增加顺序性。创建子节点的时候，可以设置一个顺序标记，在创建时，ZooKeeper 会自动为给定节点加上一个数字后缀（上限是整型的最大值），作为一个新的、完整的节点名。
- 临时节点(`EPHEMERAL`): 生命周期和客户端会话绑定，如果客户端会话失效，节点会自动清理。ZooKeeper 规定了临时节点只能作为叶子节点（不能基于临时节点来创建子节点）。
- 临时顺序节点(`EPHEMERAL_SEQUENTIAL`): 在临时节点基础上增加顺序性。

### 1.2.1 状态信息

每个数据节点除了存储数据内容外，还存储节点本身的状态信息（可通过 `get` 命令获取）。以下为 get 命令获取的结果:
- 第一行: 节点的数据内容。
- 后续内容: 节点 Stat 状态对象的格式化输出:
  - `czxid`: Created ZXID, 表示该数据节点被创建时的事务 ID。
  - `mzxid`: Modified ZXID, 表示该节点最后一次被更新时的事务 ID。
  - `ctime`: Created Time, 表示节点被创建的时间。
  - `mtime`: Modified Time, 表示该节点最后一次被更新的时间。
  - `version`: 数据节点的版本号。
  - `cversion`: 子节点的版本号。
  - `aversion`: 节点的 ACL 版本号。
  - `ephemeralOwner`: 创建该临时节点的会话 sessionID。如果是持久节点则为 0。
  - `dataLength`: 数据内容的长度。
  - `numChildren`: 当前节点的子节点个数。
  - `pzxid`: 该节点的子节点列表最后一次被修改时的事务 ID（只有子节点列表变更才会更新 pzxid，子节点内容变更不会影响 pzxid）。

## 1.3 版本 - 保证分布式数据原子性操作

## 1.4 Watcher - 数据变更的通知

## 1.5 ACL - 保障数据的安全

---

# 二 序列号与协议

## 2.1 Jute 介绍

## 2.2 使用 Jute 进行序列化

## 2.3 深入 Jute

## 2.4 通信协议

---

# 三 客户端

## 3.1 一次会话的创建过程

## 3.2 服务器地址列表

## 3.3 ClientCnxn: 网络 I/O

---

# 四 会话

## 4.1 会话状态

## 4.2 会话创建

## 4.3 会话管理

## 4.4 会话清理

## 4.5 重连

---

# 五 服务器启动

## 5.1 单机版服务器启动

## 5.2 集群版服务器启动

---

# 六 Leader 选举

## 6.1 Leader 选举概述

## 6.2 Leader 选举的算法分析

## 6.3 Leader 选举的实现细节

---

# 七 各服务器角色介绍

## 7.1 Leader

## 7.2 Follower

## 7.3 Observer

## 7.4 集群间消息通信

---

# 八 请求处理

## 8.1 会话创建请求

## 8.2 SetData 请求

## 8.3 事务请求转发

## 8.4 GetData 请求

---

# 九 数据与存储

## 9.1 内存数据

## 9.2 事物日志

## 9.3 snapshot - 数据快照

## 9.4 初始化

## 9.5 数据同步

---
