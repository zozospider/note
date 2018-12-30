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

每个数据节点都具有以下三类版本信息:
- `version`: 当前数据节点数据内容的版本号
- `cversion`: 当前数据节点子节点的版本号
- `aversion`: 当前数据节点 ACL 变更版本号

比如一个数据节点 `/zk-book` 创建完毕后，节点的 version 值是 0，表示 "当前节点创建以后，被更新过 0 次"，如果进行更新操作，那么 version 将变成 1。注意，version 表示的变更次数，即使更新为相同的值，version 依然会增加。

version 的主要应用场景为分布式锁。在分布式系统运行过程中，需要依靠锁来保证在高并发情况下数据更新的准确性。锁可以分为悲观锁和乐观锁，以下为说明:

> __悲观锁__

悲观锁称为悲观并发控制(Pessimistic Concurrency Control, PCC), 具有强烈的独占和排他性，能够有效避免不同事务对同一个资源并发更新而造成的数据一致性问题。悲观锁假定不同事务之间的处理一定会出现相互干扰，从而需要在一个事务从头到尾的过程中都对数据进行加锁。悲观锁适合解决那些对于数据更新竞争十分激烈的场景。

如果一个事务正在对数据进行处理，那么在整个处理过程中，都会将数据出于锁定状态，在这期间，其他事务将无法对这个数据进行更新操作，直到事务完成对该数据的处理并释放锁后，才能重新竞争锁。

> __乐观锁__

乐观锁称为乐观并发控制(Optimistic Concurrency Control, OCC), 具有宽松和友好性。乐观锁假定不同事务之间处理过程不会相互影响，因此在事务处理的绝大部分时间里不需要进行加锁处理，只在更新请求提交前，检查当前事务在读取数据后的这段时间内，是否由其他事务对该数据进行了修改，如果没有修改，则提交，如果有修改则回滚。乐观锁适合使用在数据并发竞争不大、事务冲突较少的应用场景中。

乐观锁事务分为三个阶段: 数据读取, 写入校验, 数据写入。ZooKeeper 中的 version 就是用来实现乐观锁中的写入校验。以下为 PrepRequestProcessor 处理类中对数据更新请求的版本校验逻辑:
```java
version = setDataRequest.getVersion();
int currentVersion = nodeRecord.stat.getVersion();
if (version != -1 && version != currentVersion) {
    throw new KeeperException.BadVersionException(path);
}
version = currentVersion + 1;
```

以上代码可以看出，在进行 setDataRequest 请求处理时，首先进行版本校验，ZooKeeper 从 setDataRequest 请求中获取当前请求的版本 version，同时从数据记录 nodeRecord 中获取当前服务器上该数据的最新版本 currentVersion，如果 version 为 "-1"，说明客户端不要求使用乐观锁，忽略版本比对，否则，就对比 version 和 currentVersion，如果不匹配，抛出 `BadVersionException` 异常。

## 1.4 Watcher - 数据变更的通知

ZooKeeper 提供了分布式数据的发布/订阅功能，能够让多个订阅者同时监听某一个主题对象。ZooKeeper 引入 Watcher 机制实现通知功能，客户端通过向服务端注册一个 Watcher 监听，当服务端指定事件触发一个 Watcher，就会向客户端发送一个事件通知。

### Watcher 接口

接口类 Watcher 用于表示一个标准的事件处理器，其中包含 KeeperState（通知状态） 和 EventType（事件类型）.

以下为常见的 KeeperState（通知状态） 和 EventType（事件类型）:

| KeeperState | EventType | 触发条件 |
| :--- | :--- | :--- |
| SyncConnected(3): 此时客户端和服务器处于连接状态 | None(-1) | 客户端与服务器成功建立会话 |
|  | NodeCreated(1) | Watcher 监听的对应数据节点被创建 |
|  | NodeDeleted(2) | Watcher 监听的对应数据节点被删除 |
|  | NodeDataChanged(3) | Watcher 监听的对应数据节点的数据内容发生变化 |
|  | NodeChildrenChanged(4) | Watcher 监听的对应数据节点的子节点列表发生变化 |
| DisConnected(0): 此时客户端和服务器处于断开连接状态 | None(-1) | 客户端与 ZooKeeper 服务端断开连接 |
| Expired(-112): 此时客户端会话失效，通常同时也会收到 SessionExpiredException 异常 | None(-1) | 会话超时 |
| AuthFailed(4): 授权失败，通常也会收到 AuthFailedException 异常 | None(-1) | 使用错误的 scheme 进行权限检查 或 SASL 权限检查失败 |

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
