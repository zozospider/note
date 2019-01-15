
- [Document & Code](#document--code)
- [一 系统模型](#一-系统模型)
    - [1.1 数据模型](#11-数据模型)
    - [1.2 节点特性](#12-节点特性)
        - [1.2.1 状态信息](#121-状态信息)
    - [1.3 版本 - 保证分布式数据原子性操作](#13-版本---保证分布式数据原子性操作)
    - [1.4 Watcher - 数据变更的通知](#14-watcher---数据变更的通知)
        - [Watcher 接口](#watcher-接口)
    - [1.5 ACL - 保障数据的安全](#15-acl---保障数据的安全)
        - [1.5.1 ACL 介绍](#151-acl-介绍)
        - [1.5.2 权限扩展体系](#152-权限扩展体系)
        - [1.5.3 ACL 管理](#153-acl-管理)
- [二 序列号与协议](#二-序列号与协议)
    - [2.1 Jute 介绍](#21-jute-介绍)
    - [2.2 使用 Jute 进行序列化](#22-使用-jute-进行序列化)
    - [2.3 深入 Jute](#23-深入-jute)
    - [2.4 通信协议](#24-通信协议)
        - [2.4.1 协议解析: 请求部分](#241-协议解析-请求部分)
        - [2.4.2 协议解析: 响应部分](#242-协议解析-响应部分)
- [三 客户端](#三-客户端)
    - [3.1 一次会话的创建过程](#31-一次会话的创建过程)
        - [3.1.1 初始化阶段](#311-初始化阶段)
        - [3.1.2 会话创建阶段](#312-会话创建阶段)
        - [3.1.3 响应处理阶段](#313-响应处理阶段)
    - [3.2 服务器地址列表](#32-服务器地址列表)
        - [3.2.1 Chroot: 客户端隔离命名空间](#321-chroot-客户端隔离命名空间)
        - [3.2.2 HostProvider: 地址列表管理器](#322-hostprovider-地址列表管理器)
    - [3.3 ClientCnxn: 网络 I/O](#33-clientcnxn-网络-io)
        - [3.3.1 Packet](#331-packet)
        - [3.3.2 outgoingQueue 和 pendingQueue](#332-outgoingqueue-和-pendingqueue)
        - [3.3.3 SendThread](#333-sendthread)
        - [3.3.4 EventThread](#334-eventthread)
        - [3.3.5 ClientCnxnSocket](#335-clientcnxnsocket)
- [四 会话](#四-会话)
    - [4.1 会话状态](#41-会话状态)
    - [4.2 会话创建](#42-会话创建)
        - [4.2.1 Session](#421-session)
        - [4.2.2 sessionID](#422-sessionid)
        - [4.2.3 SessionTracker](#423-sessiontracker)
        - [4.2.4 创建连接](#424-创建连接)
    - [4.3 会话管理](#43-会话管理)
        - [4.3.1 分桶策略](#431-分桶策略)
        - [4.3.2 会话激活](#432-会话激活)
    - [4.4 会话清理](#44-会话清理)
        - [4.4.1 会话超时检查](#441-会话超时检查)
        - [4.4.2 会话清理](#442-会话清理)
    - [4.5 重连](#45-重连)
        - [4.5.1 连接断开: CONNECTION_LOSS](#451-连接断开-connection_loss)
        - [4.5.2 会话失效: SESSION_EXPIRED](#452-会话失效-session_expired)
        - [4.5.3 会话转移: SESSION_MOVED](#453-会话转移-session_moved)
- [五 服务器启动](#五-服务器启动)
    - [5.1 单机版服务器启动](#51-单机版服务器启动)
        - [5.1.1 预启动](#511-预启动)
        - [5.1.2 初始化](#512-初始化)
    - [5.2 集群版服务器启动](#52-集群版服务器启动)
        - [5.2.1 预启动](#521-预启动)
        - [5.2.2 初始化](#522-初始化)
        - [5.2.3 Leader 选举](#523-leader-选举)
        - [5.2.4 Leader 和 Follower 启动期交互过程](#524-leader-和-follower-启动期交互过程)
        - [5.2.5 Leader 和 Follower 启动](#525-leader-和-follower-启动)
- [六 Leader 选举](#六-leader-选举)
    - [6.1 Leader 选举概述](#61-leader-选举概述)
        - [6.1.1 服务器启动时期的 Leader 选举](#611-服务器启动时期的-leader-选举)
        - [6.1.2 服务器运行期间的 Leader 选举](#612-服务器运行期间的-leader-选举)
    - [6.2 Leader 选举的算法分析](#62-leader-选举的算法分析)
        - [6.2.1 进入 Leader 选举](#621-进入-leader-选举)
        - [6.2.2 开始投票](#622-开始投票)
        - [6.2.3 变更投票](#623-变更投票)
        - [6.2.4 确定 Leader](#624-确定-leader)
        - [6.2.5 小结](#625-小结)
    - [6.3 Leader 选举的实现细节](#63-leader-选举的实现细节)
        - [6.3.1 QuorumCnxManager & FastLeaderElection](#631-quorumcnxmanager--fastleaderelection)
        - [6.3.2 QuorumCnxManager - 消息队列](#632-quorumcnxmanager---消息队列)
        - [6.3.3 QuorumCnxManager - 建立连接](#633-quorumcnxmanager---建立连接)
        - [6.3.4 QuorumCnxManager - 消息接收与发送](#634-quorumcnxmanager---消息接收与发送)
        - [6.3.5 FastLeaderElection - 选票管理](#635-fastleaderelection---选票管理)
        - [6.3.6 FastLeaderElection - 算法核心](#636-fastleaderelection---算法核心)
- [七 各服务器角色介绍](#七-各服务器角色介绍)
    - [7.1 Leader](#71-leader)
        - [7.1.1 请求处理链](#711-请求处理链)
        - [7.1.2 LearnerHandler](#712-learnerhandler)
    - [7.2 Follower](#72-follower)
        - [7.2.1 请求处理链](#721-请求处理链)
    - [7.3 Observer](#73-observer)
        - [7.3.1 请求处理链](#731-请求处理链)
    - [7.4 集群间消息通信](#74-集群间消息通信)
        - [7.4.1 数据同步型](#741-数据同步型)
        - [7.4.2 服务器初始化型](#742-服务器初始化型)
        - [7.4.3 请求处理型](#743-请求处理型)
        - [7.4.4 会话管理型](#744-会话管理型)
- [八 请求处理](#八-请求处理)
    - [8.1 事务请求转发](#81-事务请求转发)
    - [8.2 会话创建请求](#82-会话创建请求)
        - [8.2.1 请求接受](#821-请求接受)
        - [8.2.2 会话创建](#822-会话创建)
        - [8.1.3 预处理](#813-预处理)
        - [8.2.4 事务处理 - Sync 处理](#824-事务处理---sync-处理)
        - [8.2.5 事务处理 - Proposal 流程](#825-事务处理---proposal-流程)
        - [8.2.6 事务处理 - Commit 流程](#826-事务处理---commit-流程)
        - [8.2.7 事务应用](#827-事务应用)
        - [8.2.8 会话响应](#828-会话响应)
    - [8.3 SetData 请求](#83-setdata-请求)
        - [8.3.1 预处理](#831-预处理)
        - [8.3.2 事务处理](#832-事务处理)
        - [8.3.3 事务应用](#833-事务应用)
        - [8.3.4 请求响应](#834-请求响应)
    - [8.4 GetData 请求](#84-getdata-请求)
        - [8.4.1 预处理](#841-预处理)
        - [8.4.2 非事务处理](#842-非事务处理)
        - [8.4.3 请求响应](#843-请求响应)
- [九 数据与存储](#九-数据与存储)
    - [9.1 内存数据](#91-内存数据)
    - [9.2 事物日志](#92-事物日志)
    - [9.3 snapshot - 数据快照](#93-snapshot---数据快照)
    - [9.4 初始化](#94-初始化)
    - [9.5 数据同步](#95-数据同步)

---

# Document & Code

* [../Zookeeper-book](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-book.md)

---

# 一 系统模型

## 1.1 数据模型

ZooKeeper 使用 `数据节点`, 称为 `ZNode`, 是 ZooKeeper 中数据的最小单元, 每个 ZNode 可以保存数据, 也可以挂载子节点.

## 1.2 节点特性

ZooKeeper 的每个数据节点都是有生命周期的, 节点类型可分为: 持久节点 (`PERSISTENT`), 临时节点 (`EPHEMERAL`), 顺序节点 (`SEQUENTIAL`). 可生成以下四种组合类型:
- 持久节点 (`PERSISTENT`): 是指数据节点被创建后, 会一直存在 ZooKeeper 服务器上, 直到有主动删除操作.
- 持久顺序节点 (`PERSISTENT_SEQUENTIAL`): 在持久节点基础上增加顺序性. 创建子节点的时候, 可以设置一个顺序标记, 在创建时, ZooKeeper 会自动为给定节点加上一个数字后缀 (上限是整型的最大值), 作为一个新的, 完整的节点名.
- 临时节点 (`EPHEMERAL`): 生命周期和客户端会话绑定, 如果客户端会话失效, 节点会自动清理.ZooKeeper 规定了临时节点只能作为叶子节点 (不能基于临时节点来创建子节点).
- 临时顺序节点 (`EPHEMERAL_SEQUENTIAL`): 在临时节点基础上增加顺序性.

### 1.2.1 状态信息

每个数据节点除了存储数据内容外, 还存储节点本身的状态信息 (可通过 `get` 命令获取). 以下为 get 命令获取的结果:
- 第一行: 节点的数据内容.
- 后续内容: 节点 Stat 状态对象的格式化输出:
  - `czxid`: Created ZXID, 表示该数据节点被创建时的事务 ID.
  - `mzxid`: Modified ZXID, 表示该节点最后一次被更新时的事务 ID.
  - `ctime`: Created Time, 表示节点被创建的时间.
  - `mtime`: Modified Time, 表示该节点最后一次被更新的时间.
  - `version`: 数据节点的版本号.
  - `cversion`: 子节点的版本号.
  - `aversion`: 节点的 ACL 版本号.
  - `ephemeralOwner`: 创建该临时节点的会话 sessionID.如果是持久节点则为 0.
  - `dataLength`: 数据内容的长度.
  - `numChildren`: 当前节点的子节点个数.
  - `pzxid`: 该节点的子节点列表最后一次被修改时的事务 ID (只有子节点列表变更才会更新 pzxid, 子节点内容变更不会影响 pzxid).

## 1.3 版本 - 保证分布式数据原子性操作

每个数据节点都具有以下三类版本信息:
- `version`: 当前数据节点数据内容的版本号
- `cversion`: 当前数据节点子节点的版本号
- `aversion`: 当前数据节点 ACL 变更版本号

比如一个数据节点 `/zk-book` 创建完毕后, 节点的 version 值是 0, 表示 "当前节点创建以后, 被更新过 0 次", 如果进行更新操作, 那么 version 将变成 1. 注意, version 表示的变更次数, 即使更新为相同的值, version 依然会增加.

version 的主要应用场景为分布式锁. 在分布式系统运行过程中, 需要依靠锁来保证在高并发情况下数据更新的准确性. 锁可以分为悲观锁和乐观锁, 以下为说明:

> __悲观锁__

悲观锁称为悲观并发控制(Pessimistic Concurrency Control, PCC), 具有强烈的独占和排他性, 能够有效避免不同事务对同一个资源并发更新而造成的数据一致性问题. 悲观锁假定不同事务之间的处理一定会出现相互干扰, 从而需要在一个事务从头到尾的过程中都对数据进行加锁. 悲观锁适合解决那些对于数据更新竞争十分激烈的场景.

如果一个事务正在对数据进行处理, 那么在整个处理过程中, 都会将数据出于锁定状态, 在这期间, 其他事务将无法对这个数据进行更新操作, 直到事务完成对该数据的处理并释放锁后, 才能重新竞争锁.

> __乐观锁__

乐观锁称为乐观并发控制 (Optimistic Concurrency Control, OCC), 具有宽松和友好性. 乐观锁假定不同事务之间处理过程不会相互影响, 因此在事务处理的绝大部分时间里不需要进行加锁处理, 只在更新请求提交前, 检查当前事务在读取数据后的这段时间内, 是否由其他事务对该数据进行了修改, 如果没有修改, 则提交, 如果有修改则回滚. 乐观锁适合使用在数据并发竞争不大, 事务冲突较少的应用场景中.

乐观锁事务分为三个阶段: 数据读取, 写入校验, 数据写入. ZooKeeper 中的 version 就是用来实现乐观锁中的写入校验. 以下为 PrepRequestProcessor 处理类中对数据更新请求的版本校验逻辑:
```java
version = setDataRequest.getVersion();
int currentVersion = nodeRecord.stat.getVersion();
if (version != -1 && version != currentVersion) {
    throw new KeeperException.BadVersionException(path);
}
version = currentVersion + 1;
```

以上代码可以看出, 在进行 setDataRequest 请求处理时, 首先进行版本校验, ZooKeeper 从 setDataRequest 请求中获取当前请求的版本 version, 同时从数据记录 nodeRecord 中获取当前服务器上该数据的最新版本 currentVersion, 如果 version 为 "-1", 说明客户端不要求使用乐观锁, 忽略版本比对, 否则, 就对比 version 和 currentVersion, 如果不匹配, 抛出 `BadVersionException` 异常.

## 1.4 Watcher - 数据变更的通知

ZooKeeper 提供了分布式数据的发布/订阅功能, 能够让多个订阅者同时监听某一个主题对象.ZooKeeper 引入 Watcher 机制实现通知功能, 客户端通过向服务端注册一个 Watcher 监听, 当服务端指定事件触发一个 Watcher, 就会向客户端发送一个事件通知.

### Watcher 接口

接口类 Watcher 用于表示一个标准的事件处理器, 其中包含 KeeperState(通知状态) 和 EventType(事件类型).

以下为常见的 KeeperState(通知状态) 和 EventType(事件类型):

| KeeperState | EventType | 触发条件 |
| :--- | :--- | :--- |
| SyncConnected (3): 此时客户端和服务器处于连接状态 | None (-1) | 客户端与服务器成功建立会话 |
|  | NodeCreated (1) | Watcher 监听的对应数据节点被创建 |
|  | NodeDeleted (2) | Watcher 监听的对应数据节点被删除 |
|  | NodeDataChanged (3) | Watcher 监听的对应数据节点的数据内容发生变化 |
|  | NodeChildrenChanged (4) | Watcher 监听的对应数据节点的子节点列表发生变化 |
| DisConnected (0): 此时客户端和服务器处于断开连接状态 | None (-1) | 客户端与 ZooKeeper 服务端断开连接 |
| Expired (-112): 此时客户端会话失效, 通常同时也会收到 SessionExpiredException 异常 | None (-1) | 会话超时 |
| AuthFailed (4): 授权失败, 通常也会收到 AuthFailedException 异常 | None (-1) | 使用错误的 scheme 进行权限检查 或 SASL 权限检查失败 |

由于 Watcher 机制细节较为复杂, 详情请参考 `[从 Paxos 到 ZooKeeper] - 7.1.4 Watcher - 数据变更的通知`.

## 1.5 ACL - 保障数据的安全

`UGO (User, Group, Others)`: 应用最广泛的权限控制方式, 广泛应用于 Unix/Linux 系统中.

`ACL (Access Control List)`: 访问控制列表, 是一种更细粒度的权限管理方式, 可以针对任意用户和组进行细粒度的权限控制. 目前大部分 Unix 系统已经支持, Linux 也从 2.6 版本的内核开始支持.

### 1.5.1 ACL 介绍

ZooKeeper 的 ACL 权限控制和 Unix/Linux 操作系统的 ACL 有一些区别. ACL 具有以下三个概念:

> __权限模式 (Scheme)__

权限模式是权限校验使用的策略. 分为以下四种模式:
- `IP`: 通过 IP 地址细粒度控制. 如 `ip:192.168.0.110` 表示权限控制针对该 IP.`ip:192.168.0.1/24` 表示权限控制针对 "192.168.0.*" IP 段.
- `Digest`: 针对不同应用进行权限控制. 用 `username:password` 表示, 其中 ZooKeeper 会对 username:password 进行 SHA-1 算法加密和 BASE64 编码, 最后 username:password 被混淆为一个无法辨识的字符串.
- `World`: 对所有用户开放. 是一种特殊的 Digest 模式, 使用 "world:anyone" 表示.
- `Super`: 超级用户控制. 是一种特殊的 Digest 模式.

> __授权对象 (ID)__

授权对象为权限模式下对应的实体, 以下为对应关系:

| 权限模式 | 授权对象 |
| :--- | :--- |
| IP | IP 地址或 IP 段, 如 `192.168.0.110` 或 `192.168.0.1/24` |
| Digest | `username:BASE64(SHA-1(username:password))`, 如 `foo:kWN6aNSbjcKWPqjiV7cg0N24raU=` |
| Word | 只有一个 ID: `anyone` |
| Super | 与 Digest 模式一致 |

> __权限 (Permission)__

ZooKeeper 对数据的操作权限分为以下五类:
- `CREATE (C)`: 数据节点的创建权限, 允许授权对象在该数据节点下创建子节点.
- `DELETE( D)`: 子节点的删除权限, 允许授权对象删除该数据节点的子节点.
- `READ (R)`: 数据节点的读取权限, 允许授权对象访问该数据节点并读取其数据内容或子节点列表等.
- `WRITE (W)`: 数据节点的更新权限, 允许授权对象对该数据节点进行更新.
- `ADMIN (A)`: 数据节点的管理权限, 允许授权对象对该数据节点进行 ACL 设置.

### 1.5.2 权限扩展体系

ZooKeeper 允许开发人员对权限进行扩展, 通过自定义和注册两个步骤完成.

> __自定义权限控制器__

自定义 `CustomAuthenticationProvider` 实现 ZooKeeper 的标准权限控制器 `AuthenticationProvider` 即可.

ZooKeeper 自带的 `DigestAuthenticationProvider` 和 `IPAuthenticationProvider` 也是基于该接口实现.

> __注册自定义权限控制器__

将自定义的权限控制器注册到 ZooKeeper 服务器中, 支持以下两种方式注册:
- 系统属性: 在 ZooKeeper 启动参数重配置 `-Dzookeeper.authProvider.1=com.zkbook.CustomAuthenticationProvider`.
- 配置文件: 在 zoo.cfg 中配置 `authProvider.1=com.zkbook.CustomAuthenticationProvider`.

### 1.5.3 ACL 管理

> __设置 ACL__

通过 zkCli 脚本登录 ZooKeeper 服务器, 可进行 ACL 设置, 以下为设置方式:
- 创建节点时设置: `create [-s] [-e] path data acl`, 如 `create -e /zk-book initData digest:foo:MiGs3Eiy1pP4rvH1Q1NwbP+oUF8=:cdrwa`.
- 后期指定: `setAcl path acl`, 如 `setAcl /zk-book digest:foo:MiGs3Eiy1pP4rvH1Q1NwbP+oUF8=:cdrwa`.

> __Super 模式__

一旦对一个数据节点设置了 ACL 权限, 那么其他没有被授权的 ZooKeeper 客户端将无法访问该节点. 此时开启 Super 模式, 可以实现超级管理员权限, 开启方法是在 ZooKeeper 启动时, 添加系统属性 `-Dzookeeper.DigestAuthenticationProvider.superDigest.superDigest=superUser:kWN6aNSbjcKWPQjiV7cg0N24raU=`, 其中 superUser 为超级管理员.

---

# 二 序列号与协议

## 2.1 Jute 介绍

Jute 是 ZooKeeper 的序列号组件, 最初是 Hadoop 默认序列化组件(0.21.0 版本后, 换成了 Avro).

## 2.2 使用 Jute 进行序列化

以下为使用 Jute 对某个对象进行序列化和反序列化的例子:

- 1. 定义对象, 实现 Record 接口:
```java
public class MockReqHeader implements Record {

    private long sessionId;
    private String type;

    public MockReqHeader() {
    }

    public MockReqHeader(long sessionId, String type) {
        this.sessionId = sessionId;
        this.type = type;
    }

    // set get ...

    @Override
    public void serialize(OutputArchive archive, String tag) throws IOException {
        archive.startRecord(this, tag);
        archive.writeLong(sessionId, "sessionId");
        archive.writeString(type, "type");
        archive.endRecord(this, tag);
    }

    @Override
    public void deserialize(InputArchive archive, String tag) throws IOException {
        archive.startRecord(tag);
        sessionId = archive.readLong("sessionId");
        type = archive.readString("type");
        archive.endRecord(tag);
    }

}
```

- 2. 序列化和反序列化:
```java
// 开始序列化
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
BinaryOutputArchive outputArchive = BinaryOutputArchive.getArchive(outputStream);

MockReqHeader mock = new MockReqHeader(0x244221eccb92a34el, "ping");
mock.serialize(outputArchive, "header");

// 这里通常是 TCP 网络传输对象
ByteBuffer buffer = ByteBuffer.wrap(outputStream.toByteArray());

// 开始反序列化
ByteBufferInputStream inputStream = new ByteBufferInputStream(buffer);
BinaryInputArchive inputArchive = BinaryInputArchive.getArchive(inputStream);

MockReqHeader receivedMock = new MockReqHeader();
receivedMock.deserialize(inputArchive, "header");

// 关闭流
inputStream.close();
outputStream.close();
```

## 2.3 深入 Jute

Record 为 Jute 定义的序列化格式, ZooKeeper 中所有需要进行网络传输或本地磁盘存储的类型, 都实现了该接口. 实体类通过实现 Record 定义的 serialize() 和 deserialize(), 来定义自己如何被序列化和反序列化.

其中, 参数 OutputArchive 和 InputArchive 是底层真正的序列化和反序列化器, 每个 archive 可以序列化和反序列化多个对象, 不同对象使用 tag 参数标识.

Archive 的官方实现包括:
- `BinaryOutputArchive`/`BinaryInputArchive`: 对数据对象的序列化和反序列化, 主要用于网络传输和本地磁盘存储.
- `CsvOutputArchive`/`CsvInputArchive`: 对数据的序列化, 方便数据对象的可视化展示.
- `XmlOutputArchive`/`XmlInputArchive`: 将数据对象以 XML 格式保存和还原.

ZooKeeper 的 `src/zookeeper.jute` 定义文件中会定义一些需要生成的类, 以下为定义 ID 和 ACL 示例:
```
module org.apache.zookeeper.data {
    class Id {
        ustring scheme;
        ustring id;
    }
    class ACL {
        int perms;
        Id id;
    }
    ...
}
```

Jute 组件会使用不同的代码生成器来生成实际编程语言 (Java / C / C++) 的文件, 如 Java 使用 JavaGenerator 来生成类文件 (都会实现 Record 接口), 存放在 `src/java/generated` 目录.

## 2.4 通信协议

ZooKeeper 通信协议设计包括:
- 请求: len + 请求头 + 请求体
- 响应: len + 响应头 + 响应体

### 2.4.1 协议解析: 请求部分

> __请求头: RequestHeader__

请求头包含:
```
module org.apache.zookeeper.proto {
    ...
    class RequestHeader {
        int xid;
        int type;
    }
    ...
}
```

- `xid`: 用于记录客户端请求发起先后顺序, 确保单个客户端请求的响应顺序.
- `type`: 代表请求类型, 有 20 种 (详情查看 OpCode (`org.apache.zookeeper.ZooDefs.OpCode`)), 以下为部分示例:
  - `OpCode.create (1)`: 创建节点
  - `OpCode.delete (2)`: 删除节点
  - `OpCode.exists (3)`: 节点是否存在
  - `OpCode.getData (4)`: 获取节点数据
  - `OpCode.setData (5)`: 设置节点数据

> __请求体: Request__

不同的 type 请求类型, 请求体的结构是不同的, 以下为部分示例:

- ConnectRequest: 会话创建

ZooKeeper 客户端和服务端创建会话时, 会发送 ConnectRequest 请求, 包含 protocolVersion (版本号), lastZxidSeen (最近一次接收到的服务器 ZXID lastZxidSeen), timeOut (会话超时时间), sessionId (会话标示), passwd (会话密码), 其数据结构在 `src/zookeeper.jute` 中定义如下:
```
module org.apache.zookeeper.proto {
    ...
    class ConnectRequest {
        int protocolVersion;
        long lastZxidSeen;
        int timeOut;
        long sessionId;
        buffer passwd;
    }
    ...
}
```

- GetDataRequest: 获取节点数据

ZooKeeper 客户端在向服务器发送获取节点数据请求时, 会发送 GetDataRequest 请求, 包含 path(节点路径), watch(是否注册 Watcher), 其数据结构在 `src/zookeeper.jute` 中如下:
```
module org.apache.zookeeper.proto {
    ...
    class GetDataRequest {
        ustring path;
        boolean watch;
    }
    ...
}
```

- SetDataRequest: 更新节点数据

ZooKeeper 客户端在向服务器发送更新节点数据请求时, 会发送 SetDataRequest 请求, 包含 path (节点路径), data (数据内容),version (期望版本号), 其数据结构在 `src/zookeeper.jute` 中如下:
```
module org.apache.zookeeper.proto {
    ...
    class SetDataRequest {
        ustring path;
        buffer data;
        int version;
    }
    ...
}
```

> __请求实例__

以下为 `获取节点数据` 的例子:

```java
public class GetDataRequestMain implements Watcher {

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

        ZooKeeper zooKeeper = new ZooKeeper("domain1.book.zookeeper", 5000, new GetDataRequestMain());
        zooKeeper.getData("/$7_2_4/get_data", true, null);
    }

    @Override
    public void process(WatchedEvent event) {
        // TODO
    }

}
```

ZooKeeper 客户端调用 getData() 接口, 实际上就是向 ZooKeeper 服务端发送了一个 GetDataRequest 请求. 通过 Wireshark 可以获取到发送的网络 TCP 包: `00,00,00,1d,00,00,00,01,00,00,00,04,00,00,00,10,2f,24,37,5f,32,5f,34,2f,67,65,74,5f,64,61,74,61,01`. 以下是其表示的含义:

| 十六进制位 | 协议部分 | 数值或字符串 |
| :--- | :--- | :--- |
| 00,00,00,1d | 0~3 位是 len, 代表整个请求的数据包长度 | 29 |
| 00,00,00,01 | 4~7 位是 xid, 代表客户端请求的发起序号 | 1 |
| 00,00,00,04 | 8~11 位是 type, 代表客户端请求类型 | 4 (代表 OpCode.getData) |
| 00,00,00,10 | 12~15 位是 len, 代表节点路径的长度 | 16 (代表节点路径长度转换成十六进制是 16位) |
| 2f,24,37,5f,32,5f,34,2f,67,65,74,5f,64,61,74,61 | 16~31 位是 path, 代表节点路径 | /$7_2_4/get_data (通过比对 ASCII 码表转换成十进制即可) |
| 01 | 32 位是 watch, 代表是否注册 Watcher | 1 (代表注册 Watcher) |

### 2.4.2 协议解析: 响应部分

> __响应头: ReplyHeader__

响应头包含:
```
module org.apache.zookeeper.proto {
    ...
    class ReplyHeader {
        int xid;
        long zxid;
        int err;
    }
    ...
}
```

- `xid`: 和请求头中的 xid 是一致的.
- `zxid`: 代表 ZooKeeper 服务器上当前最新的事务 ID.
- `err`: 错误码, 有 22 种 (详情查看 `org.apache.zookeeper.KeeperException.Code`), 以下为部分示例:
  - `Code.OK (0)`: 处理成功
  - `Code.NONODE (101)`: 节点不存在
  - `Code.NOAUTH (102)`: 没有权限

> __响应体: Response__

不同的 type 响应类型, 响应体的结构是不同的, 以下为部分示例:

- ConnectResponse: 会话创建

针对 ZooKeeper 客户端的会话创建请求, ZooKeeper 服务端会返回 ConnectResponse 响应, 包含 protocolVersion(), timeOut(), sessionId(), passwd(), 其数据结构在 `src/zookeeper.jute` 中定义如下:

```
module org.apache.zookeeper.proto {
    ...
    class ConnectResponse {
        int protocolVersion;
        int timeOut;
        long sessionId;
        buffer passwd;
    }
    ...
}
```

- GetDataResponse: 获取节点数据

针对 ZooKeeper 客户端的获取节点数据请求, ZooKeeper 服务端会返回 GetDataResponse 响应, 包含 data (数据内容), stat (节点状态), 其数据结构在 `src/zookeeper.jute` 中如下:
```
module org.apache.zookeeper.proto {
    ...
    class GetDataResponse {
        buffer data;
        org.apache.zookeeper.data.Stat stat;
    }
    ...
}
```

- SetDataResponse: 更新节点数据

针对 ZooKeeper 客户端的更新节点数据请求, ZooKeeper 服务端会返回 SetDataResponse 响应, 包含 stat (节点状态), 其数据结构在 `src/zookeeper.jute` 中如下:
```
module org.apache.zookeeper.proto {
    ...
    class SetDataResponse {
        org.apache.zookeeper.data.Stat stat;
    }
    ...
}
```

> __响应实例__

根据上文请求示例中 `获取节点数据` 的例子, 针对 ZooKeeper 客户端调用 getData() 接口请求, ZooKeeper 服务端会响应结果. 通过 Wireshark 可以获取到响应的网络 TCP 包: `00,00,00,63,00,00,00,05,00,00,00,00,00,00,00,04,00,00,00,00,00,00,00,0b,69,27,6b,5f,63,6f,6e,74,65,6e,74,00,00,00,00,00,00,00,04,00,00,00,00,00,00,00,04,00,00,01,43,67,bd,0e,08,00,00,01,43,67,bd,0e,08,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,0b,00,00,00,00,00,00,00,00,00,00,00,04`. 以下是其表示的含义:

| 十六进制位 | 协议部分 | 数值或字符串 |
| :--- | :--- | :--- |
| 00,00,00,63 | 0~3 位是 len, 代表整个响应的数据包长度 | 99 |
| 00,00,00,05 | 4~7 位是 xid, 代表客户端请求的发起序号 | 5 (代表本次请求是客户端会话创建后的第 5 次请求发送) |
| 00,00,00,00,00,00,00,04 | 8~15 位是 zxid, 代表当前服务端处理过第最新的 ZXID 值 | 4 |
| 00,00,00,00 | 16~19 位是 err, 代表错误码 | 0 (代表 Code.OK) |
| 00,00,00,0b | 20~23 位是 len, 代表节点数据内容的长度 | 11 (代表接下去的 11 位是数据内容的字节数组) |
| 69,27,6b,5f,63,6f,6e,74,65,6e,74 | 24~34 位是 data, 代表节点的数据内容 | i'm_content |
| 00,00,00,00,00,00,00,04 | 35~42 位是 czxid, 代表创建该数据节点时的 ZXID | 4 |
| 00,00,00,00,00,00,00,04 | 43~50 位是 mzxid, 代表最后一次修改该数据节点时的 ZXID | 4 |
| 00,00,01,43,67,bd,0e,08 | 51~58 位是 ctime, 代表数据节点的创建时间 | 1389014879752 (即: 2014-01-06 21:27:59) |
| 00,00,01,43,67,bd,0e,08 | 59~66 位是 mtime, 代表数据节点最后一次变更的时间 | 1389014879752 (即: 2014-01-06 21:27:59) |
| 00,00,00,00 | 67~70 位是 version, 代表数据节点的内容的版本号 | 0 |
| 00,00,00,00 | 71~74 位是 cversion, 代表数据节点的子节点的版本号 | 0 |
| 00,00,00,00 | 75~78 位是 aversion, 代表数据节点的 ACL 变更版本号 | 0 |
| 00,00,00,00,00,00,00,00 | 79~86 位是 ephemeralOwner, 如果该数据节点是临时节点, 那么就记录创建该临时节点的会话 ID, 如果是持久节点, 则为 0 | 0 (代表该节点是持久节点) |
| 00,00,00,0b | 87~90 位是 dataLength, 代表数据节点的数据内容长度 | 11 |
| 00,00,00,00 | 91~94 位是 numChildren, 代表数据节点的子节点个数 | 0 |
| 00,00,00,00,00,00,00,04 | 95~102 位是 pzxid, 代表最后一次对子节点列表变更的 PZXID | 4 |

---

# 三 客户端

ZooKeeper 客户端主要由以下几个核心组件组成:
- `ZooKeeper 实例`: 客户端的入口.
- `ClientWatchManager`: 客户端 Watcher 管理器.
- `HostProvider`: 客户端地址列表管理器.
- `ClientCnxn`: 客户端核心线程, 包括 `SendThread` (I/O 线程, 负责客户端和服务端之间的网络 I/O 通信) 和 `EventThread` (事件线程, 负责处理服务端事件) 两个线程.

以下为 ZooKeeper 客户端构造方法:
```java
ZooKeeper(String connectString, int sessionTimeout, Watcher watcher);
ZooKeeper(String connectString, int sessionTimeout, Watcher watcher, boolean canBeReadOnly);
ZooKeeper(String connectString, int sessionTimeout, Watcher watcher, long sessionId, byte[] sessionPasswd);
ZooKeeper(String connectString, int sessionTimeout, Watcher watcher, long sessionId, byte[] sessionPasswd, boolean canBeReadOnly);
```

客户端初始化过程大概如下:
- a. 设置默认 Watcher.
- b. 设置 ZooKeeper 服务器地址列表.
- c. 创建 ClientCnxn.

传入 ZooKeeper 构造方法的 Watcher 对象会被保存在 ZKWatchManager 的 defaultWatcher 中, 作为整个客户端会话期间的默认 Watcher.

## 3.1 一次会话的创建过程

一次客户端会话的创建过程可以分为初始化阶段, 会话创建阶段, 响应处理阶段.

### 3.1.1 初始化阶段

> 1. __初始化 ZooKeeper 对象__

调用构造方法实例化 ZooKeeper 对象, 并创建一个客户端的 Watcher 管理器: ClientWatchManager.

> 2. __设置默认 Watcher__

构造方法中传入的 Watcher 对象作为 ClientWatchManager 的默认 Watcher.

> 3. __构造 HostProvider__

构造方法中传入的服务器地址被存放在服务器地址列表管理器 HostProvider 中.

> 4. __初始化 ClientCnxn__

ZooKeeper 客户端会创建一个网络连接器 ClientCnxn, 用来管理客户端和服务端的网络交互. 客户端还会初始化两个核心队列 outgoingQueue (客户端的请求发送队列) 和 pendingQueue (服务端的响应等待队列). 另外, 客户端还会创建 ClientCnxnSocket (ClientCnxn 的底层网络 I/O 处理器).

> 5. __初始化 SendThread 和 EventThread__

客户端会创建两个核心网络线程 SendThread 和 EventThread. 其中, SendThread 用于管理客户端和服务端之间的所有网络 I/O, 客户端会将 ClientCnxnSocket 分配给 SendThread 作为底层网络 I/O 处理器. EventThread 用于进行客户端的事件处理, 客户端会初始化 EventTread 的待处理事件队列 waitingEvents.

### 3.1.2 会话创建阶段

> 6. __启动 SendThread 和 EventThread__

> 7. __获取一个服务器地址__

SendThread 从 HostProvider 中随机取出一个地址, 然后委托给 ClientCnxnSocket 去创建与 ZooKeeper 服务器之间的 TCP 连接.

> 8. __创建 TCP 连接__

获取到服务器地址后, ClientCnxnSocket 负责和服务器创建一个 TCP 长连接.

> 9. __构造 ConnectRequest 请求__

创建完客户端和服务器之间的 Socket 连接后, SendThread 根据当前情况构造出一个 ConnectRequest 请求并将该请求包装成 Packet 对象, 放入请求发送队列 outgoingQueue 中, 试图与服务端创建一个会话.

> 10. __发送请求__

当客户端请求准备完毕后. ClientCnxnSocket 从 outgoingQueue 中取出一个待发送的 Packet 对象, 将其序列化成 ByteBuffer 后, 发送到服务端.

### 3.1.3 响应处理阶段

> 11. __接收服务端响应__

ClientCnxnSocket 接收到服务端的响应后, 判断当前客户端是否已经初始化完毕, 如果未完成初始化, 就认为该响应一定是会话创建请求的响应, 直接交由 readConnectResult 方法来处理.

> 12. __处理 Response__

ClientCnxnSocket 将接收到的服务端响应反序列化, 得到 ConnectResponse 对象, 并从中获取到 ZooKeeper 服务端分配的会话 sessionId.

> 13. __连接成功__

连接成功后, 一方面通知 SendThread 线程, 进一步对客户端进行会话参数 (readTimeout 和 connectTimeout 等) 设置并更新客户端状态.另一方面, 将当前成功连接的服务器地址通知给 HostProvider 地址管理器.

> 14. __生成事件 SyncConnected-None__

为了让上层应用感知到会话已创建成功, SendThread 会生成一个 SyncConnected-None 事件, 将该事件传递给 EventThread 线程.

> 15. __查询 Watcher__

EventThread 线程接收到该事件后, 从 ClientWatchManager 管理器中查询出对应的 Watcher (针对 SyncConnected-None 事件, 对应默认 Watcher), 然后将其放到 EventThread 的 waitingEvents 队列中.

> 16. __处理事件__

EventThread 线程不断地从 waitingEvents 队列中取出待处理的 Watcher 对象, 然后调用该对象的 process 接口方法, 以达到触发 Watcher 的目的.

## 3.2 服务器地址列表

ZooKeeper 客户端接收到 connectString 参数如 `192.168.0.1:2181,192.168.0.2:2181,192.168.0.3:2181` 后, 会将其放入 ConnectStringParser 对象中, 该类简单结构如下:
```java
public final class ConnectStringParser {
    private final String chrootPath;
    private final ArrayList<InetSocketAddress> serverAddresses = new ArrayList<InetSocketAddress>();
}
```

### 3.2.1 Chroot: 客户端隔离命名空间

如果一个 ZooKeeper 客户端设置了 Chroot, 那么该客户端对服务端的所有操作, 都会被限制在自己的命名空间下. 通过设置 Chroot, 能够将一个客户端与服务端的一棵子树对应, 可以实现不同应用之间的相互隔离.

通过在 connectString 中添加后缀实现, 如下:
```
192.168.0.1:2181,192.168.0.2:2181,192.168.0.3:2181/apps/X
```

### 3.2.2 HostProvider: 地址列表管理器

ConnectStringParser 对象中的 serverAddresses 属性封装了客户端的服务器地址列表, 该地址列表会被进一步封装到 HostProvider 接口的实现类 StaticHostProvider 中.

以下为 HostProvider 接口:
```java
public interface HostProvider {
    public int size();
    public InetSocketAddress next(long spinDelay);
    public void onConnected();
}
```

- size(): 该方法用于返回当前服务器地址列表的个数.
- next(long spinDelay): 该方法用于返回一个服务器地址 InetSocketAddress, 以便客户端便于连接服务器.
- onConnected(): 回调方法, 如果客户端与服务端成功连接, 通过该方法通知 HostProvider.

> __StaticHostProvider__

StaticHostProvider 简单结构如下:
```java
public final class StaticHostProvider implements HostProvider {
    private final List<InetSocketAddress> serverAddresses = new ArrayList<InetSocketAddress>(5);
    private int lastIndex = -1;
    private int currentIndex = -1;
    public int size() {}
    public InetSocketAddress next(long spinDelay) {}
    public void onConnected() {}
}
```

StaticHostProvider 首先会使用 Collections 工具类的 shuffle 方法将服务器列表随机打散, 拼装成一个环形循环队列. 以后每次尝试获取一个服务器地址时, currentIndex 游标会向前移动一位, 如果发现游标移动超过了整个列表的长度, 就重置为 0. 如果发现当前游标位置和上次使用的地址位置一样, 即 currentIndex 和 lastIndex 相等时, 就进行 spinDelay 毫秒时间等待.

## 3.3 ClientCnxn: 网络 I/O

ClientCnxn 负责客户端和服务端之间的网络连接和通信. 以下为简单结构:
```java
public class ClientCnxn {

    private final LinkedList<Packet> outgoingQueue = new LinkedList<Packet>();
    private final LinkedList<Packet> pendingQueue = new LinkedList<Packet>();
    public static final int packetLen = Integer.getInteger("jute.maxbuffer", 4096 * 1024);
    final SendThread sendThread;
    final EventThread eventThread;

    private void conLossPacket(Packet p) {
    }
    private void finishPacket(Packet p) {
    }
    Packet queuePacket(RequestHeader h, ReplyHeader r, Record request, Record response, AsyncCallback cb, String clientPath, String serverPath, Object ctx, WatchRegistration watchRegistration) {
    }
    public void sendPacket(Record request, Record response, AsyncCallback cb, int opCode) throws IOException {
    }

    static class Packet {
        Packet(RequestHeader requestHeader, ReplyHeader replyHeader, Record request, Record response, WatchRegistration watchRegistration) {
        }
        Packet(RequestHeader requestHeader, ReplyHeader replyHeader, Record request, Record response, WatchRegistration watchRegistration, boolean readOnly) {
        }
    }
    class SendThread extends ZooKeeperThread {
        private final ClientCnxnSocket clientCnxnSocket;
        SendThread(ClientCnxnSocket clientCnxnSocket) {
        }
        ClientCnxnSocket getClientCnxnSocket() {
            return clientCnxnSocket;
        }
        public void sendPacket(Packet p) throws IOException {
        }
    }
    class EventThread extends ZooKeeperThread {
        private final LinkedBlockingQueue<Object> waitingEvents = new LinkedBlockingQueue<Object>();
        EventThread() {
        }
        public void queuePacket(Packet packet) {
        }
    }
}
```

### 3.3.1 Packet

Packet 是 ClientCnxn 内部的一个对协议层的封装对象，简单结构如下:
```java
public class ClientCnxn {
    static class Packet {
        RequestHeader requestHeader;
        ReplyHeader replyHeader;
        Record request;
        Record response;
        ByteBuffer bb;
        String clientPath;
        String serverPath;
        boolean finished;
        AsyncCallback cb;
        Object ctx;
        WatchRegistration watchRegistration;
        public boolean readOnly;
        Packet(RequestHeader requestHeader, ReplyHeader replyHeader, Record request, Record response, WatchRegistration watchRegistration) {
        }
        Packet(RequestHeader requestHeader, ReplyHeader replyHeader, Record request, Record response, WatchRegistration watchRegistration, boolean readOnly) {
        }
        public void createBB() {
        }
    }
}
```

- `requestHeader`: 最基本的请求头
- `replyHeader`: 响应头
- `request`: 请求体
- `response`: 响应体
- `clientPath/serverPath`: 节点路径
- `watchRegistration`: 注册的 Watcher
- `createBB()`: 该方法负责对 Packet 对象进行序列化, 只会将 requestHeader, request, readOnly 三个属性进行序列化, 转换成可用于客户端和服务端网络传输的 ByteBuffer 对象, 其余属性都保存在客户端的上下文中, 不会进行网络传输.

### 3.3.2 outgoingQueue 和 pendingQueue

- `outgoingQueue`: 存储需要发送到服务端的 Packet 集合.
- `pendingQueue`: 存储已经从客户端发送到服务端且需要等待服务端响应的 Packet 集合.

### 3.3.3 SendThread

SendThread 是 ClientCnxn 内部的一个核心 I/O 调度线程, 用于管理客户端和服务端之间的所有网络 I/O 操作. 主要有以下作用:

- a. SendThread 维护了客户端与服务端之间的会话生命周期, 通过一定频率向服务端发送 PING 包实现心跳检测. 同时, 在会话周期内, 如果客户端与服务端 TCP 连接断开, 会自动透明地完成重连.

- b. SendThread 管理了客户端所有请求发送和响应接收操作, 将其上层客户端 API 操作转换成响应请求发送到服务端, 并完成对同步调用的返回和异步调用的回调.

- c. SendThread 负责将来自服务端的事件传递给 EventThread 处理.

### 3.3.4 EventThread

EventThread 是 ClientCnxn 内部的一个核心调度线程, 负责客户端的事件处理, 并出发客户端注册的 Watcher 监听.

EventThread 内部有一个 waitingEvents 队列, 临时存放需要被触发的 Object (包括客户端注册的 Watcher 和 异步接口中注册的回调器 AsyncCallback). EventThread 会不断地从 waitingEvents 队列中取出 Object, 识别出具体类型 (Watcher 或 AsyncCallback), 并分别调用 process 和 processResult 接口方法实现对事件的触发和回调.

### 3.3.5 ClientCnxnSocket

ClientCnxnSocket 定义了底层 Socket 通信的接口.

可通过在 zookeeper.clientCnxnSocket 系统变量中配置 ClientCnxnSocket 实现类的全类名, 如 `-Dzookeeper.clientCnxnSocket=org.apache.zookeeper.ClientCnxnSocketNIO`. 默认实现是使用 Java 原生 NIO 接口的 `ClientCnxnSocketNIO`.

> __请求发送__

从 outgoingQueue 队列中提取出一个可发送的 Packet 对象, 同时生成一个客户端请求序号 XID 并将其设置到 Packet 对象中, 然后将其序列化后进行发送. 请求发送完毕后, 立即将该 Packet 保存到 pendingQueue 队列中, 等待服务端响应后进行处理.

> __响应接收__

客户端收到来自服务端的响应后, 根据不同客户端请求类型, 进行不同处理, 如下:
- 如果当前客户端尚未进行初始化, 说明当前客户端和服务端之间正在进行会话创建, 那么就将收到的 ByteBuffer 序列化成 ConnectResponse 对象.
- 如果当前客户端处于正常会话周期, 且收到的服务端响应是一个事件, 那么客户端会将收到的 ByteBuffer 序列化成 WatcherEvent 对象, 并将该事件放入待处理队列中.
- 如果是一个常规的请求响应 (Create, GetData, Exist 等), 那么会从 pendingQueue 队列中取出一个 Packet 来进行处理. 客户端首先会检测服务端响应中包含的 XID 值来确保请求处理的顺序性, 然后将收到的 ByteBuffer 序列化成相应的 Response 对象.

最后, 在 finishPacket 方法中处理 Watcher 注册等逻辑.

---

# 四 会话

ZooKeeper 的连接与会话就是客户端通过实例化 ZooKeeper 对象来实现客户端与服务器创建并保持 TCP 连接的过程.

## 4.1 会话状态

ZooKeeper 会话状态可分为: CONNECTING, CONNECTED, CLOSE. 以下为不同场景下的会话状态:

- 客户端创建 ZooKeeper 对象, 客户端状态此时变成 CONNECTING.
- 客户端成功连接上服务器,  客户端状态此时变成 CONNECTED.
- 客户端与服务端出现连接断开, 客户端会自动重连, 客户端状态此时变成 CONNECTING.
- 客户端再次成功连接上服务器,  客户端状态此时变成 CONNECTED.
- 如果出现会话超时, 权限检查失败, 客户端主动退出等情况, 客户端状态此时变成 CLOSE.

## 4.2 会话创建

### 4.2.1 Session

Session 是 ZooKeeper 中的会话实体, 代表了一个客户端会话. 包括以下基本属性:
- `sessionID`: 会话 ID, 用来唯一标识一个会话, ZooKeeper 会为每个会话创建一个全局唯一的 sessionID.
- `TimeOut`: 会话超时时间, 可配置. 客户端向服务器发送这个超时时间后, 服务器会根据自己的超时时间限制最终确定会话的超时时间.
- `TickTime`: 下次会话超时时间点, 用于 "分桶策略" 管理中, ZooKeeper 会为每个会话标记一个下次会话超时时间点. TickTime 是 13 位的 long 型整数, 其值接近但不完全等于 TimeOut.
- `isClosing`: 标记一个会话是否关闭. 当服务端检测到某个会话已经超时失效时, 会将该会话 isClosing 标记为关闭, 确保不再处理该会话的新请求.

### 4.2.2 sessionID

`SessionTracker` (ZooKeeper 服务端会话管理器) 初始化时, 会调用 initializeNextSession() 方法来生成一个初始化 sessionID, 后续会在该 sessionID 基础上为每个会话进行分配.

以下为 `SessionTrackerImpl` 初始化 sessionID 代码逻辑:
```java
public static long initializeNextSession(long id) {
    long nextSid = 0;
    nextSid = (Time.currentElapsedTime() << 24) >>> 8;
    nextSid =  nextSid | (id <<56);
    return nextSid;
}
```

该代码逻辑的简单解释如下:
- a. 获取当前时间的毫秒表示.
- b. 左移 24 位.
- c. 无符号右移 8 位.
- d. 添加机器标识: SID.
- e. 将步骤 c 和 d 得到的两个 64 位标识的数值进行 `|` 操作.

经过上述算法计算后, 可得到一个单机唯一的序列号. 该算法可概括为: 高 8 位确定了所在机器, 后 56 位使用当前时间的毫秒表示进行随机.

### 4.2.3 SessionTracker

SessionTracker 是 ZooKeeper 服务端的会话管理器, 负责会话的创建, 管理, 清理等工作.

每个会话在 SessionTracker 内部都保留了如下内容:
- `sessionsById`: 是一个 HashMap<Long, SessionImpl> 类型的数据结构, 根据 sessionID 来管理 Session 实体.
- `sessionsWithTimeout`: 是一个 ConcurrentHashMap<Long, Integer> 类型的数据结构, 根据 sessionID 来管理会话超时时间.
- `sessionSets`: 是一个 HashMap<Long, SessionSet> 类型的数据结构, 根据下次会话超时时间点来归档会话, 便于进行会话管理和超时检查.

### 4.2.4 创建连接

客户端发起 "会话创建" 请求后, 服务端的处理可分为四大步骤:
- 处理 `ConnectRequest` 请求
- 会话创建
- 处理器链路处理
- 会话响应

## 4.3 会话管理

### 4.3.1 分桶策略

SessionTracker 采用了 "分桶策略" 进行 ZooKeeper 的会话管理. 分桶策略是指将类似的会话放在同一区块中管理, 便于进行不同区块的隔离处理和同一区块的统一处理. ZooKeeper 通过每个会话的 ExpirationTime, 将所有会话分配在不同的区块中.

ExpirationTime 表示该会话最佳一次可能超时的时间点, 计算方式如下:
```
ExpirationTime_ = CurrentTime + SessionTimeout
ExpirationTime = (ExpirationTime_ / ExpirationInterval + 1) * ExpirationInterval
```

- `CurrentTime`: 当前时间 (毫秒).
- `SessionTimeout`: 会话设置的超时时间 (毫秒).
- `ExpirationInterval`: Leader 服务器在运行期间定时进行会话超时检查的时间间隔 (毫秒), 默认值是 tickTime 值 (2000).

假设当前时间毫秒表示为 1370907000000, 客户端会话超时时间为 15000 毫秒, 服务器设置的 tickTime 为 2000 毫秒 (即 ExpirationInterval 也为 2000 毫秒), 那么计算出的 ExpirationTime 值如下:
```
ExpirationTime_ = 1370907000000 + 15000 = 1370907015000
ExpirationTime = (1370907015000 / 2000 + 1) * 2000 = 1370907017000
```

即 ExpirationTime 的值总是 ExpirationInterval 的整数倍.

### 4.3.2 会话激活

为了保持客户端会话的有效性, 客户端会在会话超时过期范围内向服务端发送 PING 请求, 俗称 "心跳检测".

服务端会不断接收来自客户端的心跳, 并且需要重新激活对应的客户端会话, 激活过程称为 TouchSession, TouchSession 主要流程如下:

- a. 检测该会话是否已关闭.

Leader 会检测该会话是否已经关闭, 如果关闭, 那么不再继续激活该会话.

- b. 计算该会话新的超时时间 ExpirationTime_New.

如果会话未关闭, 那么需要使用上述公式计算出该会话下一次超时时间点 ExpirationTime_New, 并根据该时间定位到其所在的区块.

- c. 定位该会话当前的区块.

获取该会话老的超时时间点 ExpirationTime_Old, 并根据该时间定位到其所在的区块.

- d. 迁移会话.

将会话从老的区块中取出, 放入新的区块中.

> __激活条件__

从上述步骤可以看出, 只要客户端发送心跳检测, 服务端就会进行一次会话激活. 另外, 客户端有请求发送到服务端, 也会触发服务端的会话激活.

激活条件可以概括为以下两种:
- 如果客户端发现在 sessionTimeout / 3 时间内没有和服务器进行过任何通信, 那么就会主动发起一个 PING 请求, 服务端收到请求后触发一次会话激活.
- 只要客户端向服务端发送请求, 就会触发一次会话激活.

## 4.4 会话清理

### 4.4.1 会话超时检查

SessionTracker 中有一个单独的线程负责会话超时检查, 称为 "超时检查线程", 该线程会逐个依次地对会话桶中剩下的会话进行清理.

因为 ExpirationTime 是 ExpirationInterval 的整数倍, 所以超时检查线程只需要在对应的整数倍节点上进行检查, 效率较高.

### 4.4.2 会话清理

当 SessionTracker 整理出已经过期的会话后, 需要对会话进行清理, 步骤如下:

- a. 会话状态标记为 "已关闭"

SessionTracker 首先将会话的 isClosing 标记为 true, 保证在会话清理期间不会再处理接收到的该客户端的新请求.

- b. 发起 "会话关闭" 请求

提交 "会话关闭" 请求, 交付给 PrepRequestProcessor 处理器进行处理.

- c. 收集需要清理的临时节点

一旦某个会话失效, 那么和该会话相关的临时节点 (EPHEMERAL) 都需要被一并清除.

因为每个会话都单独保存了一份由该会话维护的所有临时节点集合在 ZooKeeper 内存数据库中, 因此清理时, 只需要根据 sessionID 就可以拿到这份临时节点列表.

- d. 添加 "节点删除" 事务变更

收集到所有临时节点后, ZooKeeper 会将这些临时节点逐个转换成 "节点删除" 请求, 并放入事务变更队列 outstandingChanges 中.

- e. 删除临时节点

FinalRequestProcessor 处理器会触发内存数据库, 删除该会话对应的所有临时节点.

- f. 移除会话

节点删除后, 需要将会话从 SessionTracker 内部 (即 sessionsById, sessionsWithTimeout, sessionsSets) 移除.

- g. 关闭 NIOServerCnxn

最后, 从 NIOServerCnxnFactory 中找到该会话对应的 NIOServerCnxn, 将其关闭.

## 4.5 重连

ZooKeeper 客户端和服务端维持的是一个长连接, 在 sessionTimeout 时间内, 服务端会不断检测客户端是否处于正常连接(客户端的每一次操作触发会话激活).

然而, 当客户端与服务端由于网络故障断开连接后, 客户端可能会出现以下异常:
- `CONNECTION_LOSS`: 连接断开
- `SESSION_EXPIRED`: 会话失效

### 4.5.1 连接断开: CONNECTION_LOSS

因为网络闪断或服务器故障导致连接断开, 此时, ZooKeeper 客户端会自动从地址列表中重新逐个选取新的地址尝试重新连接.

假设某应用使用 ZooKeeper 客户端进行 setData() 操作时出现 CONNECTION_LOSS 现象, 那么客户端会接收到 None-Disconnected 通知, 同时会抛出 ConnectionLossException (`org.apache.zookeeper.KeeperException.ConnectionLossException`) 异常. 因此, 应用应该捕获 ConnectionLossException 异常, 等待客户端自动重连.

客户端重连成功后, 会接收到 None-SyncConnected 通知.

### 4.5.2 会话失效: SESSION_EXPIRED

会话失效一般发生在 CONNECTION_LOSS 情况, 由于重连时间过长, 超过了 sessionTimeout (会话超时时间), 服务器认为该会话已经结束并进行了会话清理.

客户端此时重新连接服务器, 会被服务器告知 SESSION_EXPIRED (会话失效), 在这种情况下, 应用需要重新实例化一个 ZooKeeper 对象.

### 4.5.3 会话转移: SESSION_MOVED

会话转移是指客户端会话从一台服务器转移到另一台服务器.

假设客户端和服务器 S1 出现连接断开后, 成功重连了新的服务器 S2 并延续了有效会话, 那么就说明会话从 S1 转移到了 S2.

服务端在处理客户端请求时, 会首先检查会话的 Owner (所有者), 如果 Owner 不是当前服务器, 会抛出 SessionMovedException 异常. 这样做是为了避免会话转移过程中的事务问题 (在极端情况下, 如果客户端C1 与服务器 S1 建立连接, 某一时刻, C1 向 S1 发送了请求 R1: setData('/session', 1), 但是在请求到达前, 连接断开, 在很短时间内, C1 重新连接上服务器 S2, C1 向 S2 发送请求 R2: setData('/session', 1), 此时 S2 正常处理. 但是 R1 最终到达 S1 并被 S1 处理, 就出现了 R2 请求被 R1 请求覆盖的情况).

---

# 五 服务器启动

## 5.1 单机版服务器启动

### 5.1.1 预启动

> a. __统一由 QuorumPeerMain 作为启动类__

单机或集群模式下, `zkServer.sh` 脚本的启动类都是 QuorumPeerMain (`org.apache.zookeeper.server.quorum.QuorumPeerMain`).

> b. __解析配置文件 zoo.cfg__

`zoo.cfg` 配置了 tickTime, dataDir, clientPort 等参数.

> c. __创建并启动历史文件清理器 DatadirCleanupManager__

历史文件清理包括对快照数据文件和事务日志文件的定时清理.

> d. __判断当前集群模式__

根据 `zoo.cfg` 解析出的地址列表判断当前为单机模式还是集群模式. 如果是单机模式, 就委托给 ZooKeeperServerMain (`org.apache.zookeeper.server.ZooKeeperServerMain`) 处理.

> e. __再次解析配置文件 zoo.cfg__

> f. __创建服务器实例 ZooKeeperServer__

ZooKeeperServer (`org.apache.zookeeper.server.ZooKeeperServer`) 是单机版 ZooKeeper 服务端的核心类.

### 5.1.2 初始化

> g. __创建服务器统计器 ServerStats__

`org.apache.zookeeper.server.ServerStats` 是 ZooKeeper 服务器运行时的统计器, 简单结构如下:
```java
public class ServerStats {
    private long packetsSent;
    private long packetsReceived;
    private long maxLatency;
    private long minLatency = Long.MAX_VALUE;
    private long totalLatency = 0;
    private long count = 0;
    private AtomicLong fsyncThresholdExceedCount = new AtomicLong(0);
    public void incrementFsyncThresholdExceedCount() {
    }
    public void resetFsyncThresholdExceedCount() {
    }
    synchronized void updateLatency(long requestCreateTime) {
    }
    synchronized public void incrementPacketsReceived() {
    }
    synchronized public void incrementPacketsSent() {
    }
}
```

- `packetsSent`: 从 ZooKeeper 启动开始, 或最近一次重置统计信息后, 服务端向客户端发送的响应包次数.
- `packetsReceived`: 从 ZooKeeper 启动开始, 或最近一次重置统计信息后, 服务端接收到的来自客户端的请求包次数.
- `maxLatency`: 从 ZooKeeper 启动开始, 或最近一次重置统计信息后, 服务端请求处理的最大延时.
- `minLatency`: 从 ZooKeeper 启动开始, 或最近一次重置统计信息后, 服务端请求处理的最小延时.
- `totalLatency`: 从 ZooKeeper 启动开始, 或最近一次重置统计信息后, 服务端请求处理的总延时.
- `count`: 从 ZooKeeper 启动开始, 或最近一次重置统计信息后, 服务端处理的客户端请求总次数.

> h. __创建 ZooKeeper 数据管理器 FileTxnSnapLog__

FileTxnSnapLog (`org.apache.zookeeper.server.persistence.FileTxnSnapLog`) 位于 ZooKeeper 上层和底层数据存储之间的, 提供操作数据文件的接口.

操作的数据文件包括:
- `快照数据文件`: 存储在 zoo.cfg 的 `dataDir` 中.
- `事务日志文件`: 存储在 zoo.cfg 的 `dataLogDir` 中.

> i. __设置服务器 tickTime 和会话超时时间__

> j. __创建 ServerCnxnFactory__

可通过系统属性 `zookeeper.serverCnxnFactory` 指定.

> k. __初始化 ServerCnxnFactory__

> l. __启动 ServerCnxnFactory 主线程__

> m. __恢复本地数据__

每次启动 ZooKeeper 时, 都会从快照数据文件和事务日志文件中进行数据恢复.

> n. __创建并启动会话管理器__

创建一个会话管理器 SessionTracker. SessionTracker 会初始化 expirationInterval, nextExpirationTime, sessionsWithTimeout, 并计算出一个初始 sessionID.

SessionTracker 初始化完毕后, 会开始进行会话超时检查.

> o. __初始化 ZooKeeper 的请求处理链__

ZooKeeper 对请求的处理方式时责任链模式. 即一个请求会由多个处理器来处理, 这些处理器串联起来形成一个请求处理链.

单机版 ZooKeeper 的请求处理链依次为: `PrepRequestProcessor`, `SyncRequestProcessor`, `FinalRequestProcessor`.

> p. __注册 JMX 服务__

ZooKeeper 会将服务器运行时的信息以 JMX 的方式暴露出来.

> q. __注册 ZooKeeper 服务器实例__

## 5.2 集群版服务器启动

### 5.2.1 预启动

> a. __统一由 QuorumPeerMain 作为启动类__

> b. __解析配置文件 zoo.cfg__

> c. __创建并启动历史文件清理器 DatadirCleanupManager__

> d. __判断当前集群模式__

### 5.2.2 初始化

> e. __创建 ServerCnxnFactory__

> f. __初始化 ServerCnxnFactory__

> g. __创建 ZooKeeper 数据管理器 FileTxnSnapLog__

> h. __创建 QuorumPeer 实例__

Quorum 是 ZooKeeperServer (ZooKeeper 服务器实例) 的托管者, QuorumPeer 代表了 ZooKeeper 集群中的一台机器.

> i. __创建内存数据库 ZKDatabase__

ZKDatabase 是 ZooKeeper 的内存数据库, 负责管理 ZooKeeper 的会话记录, DataTree, 事务日志的存储.

> j. __初始化 QuorumPeer__

对 QuorumPeer 配置服务器地址列表, Leader 选举算法, 会话超时时间等.

另外, 因为 QuorumPeer 是 ZooKeeperServer 的托管者, 所以需要将一些核心组件 (FileTxnSnapLog, ServerCnxnFactory, ZKDatabase) 注册到 QuorumPeer 中.

> k. __恢复本地数据__

> l. __启动 ServerCnxnFactory 线程__

### 5.2.3 Leader 选举

> m. __初始化 Leader 选举__

首先, ZooKeeper 会根据自身的 SID (服务器 ID), lastLoggedZxid (最新的 ZXID), currentEpoch (当前服务器 epoch) 来生成一个初始化的投票. 然后, ZooKeeper 会根据 zoo.cfg 中的配置, 来创建相应的 Leader 选举算法实现 (目前只支持 `FastLeaderElection` 算法).

> n. __注册 JMX 服务__

> o. __检测当前服务器状态__

QuorumPeer 作为 ZooKeeperServer 的托管者, 其核心工作就是不断检测当前服务器状态并做出相应处理.、

ZooKeeper 服务器分为以下几个状态:
- `LOOKING`
- `LEADING`
- `FOLLOWING/OBSERVING`

在启动阶段, QuorumPeer 的初始化状态为 LOOKING.

> p. __Leader 选举__

集群中所有机器进行一系列投票后, 最终产生 Leader, 其余称为 Follower / Observer. 通常, 集群中那个机器处理的数据越新 (通过每个服务器处理过的最大的 ZXID 来比较), 其越有可能称为 Leader.

### 5.2.4 Leader 和 Follower 启动期交互过程

> q. __创建 Leader 服务器和 Follower 服务器__

每个服务器根据选举后自身的角色, 开始进入各角色创建流程.

> r. __Leader 服务器启动 Follower 接收器 LearnerCnxAcceptor__

Leader (`org.apache.zookeeper.server.quorum.Leader`) 服务器需要和非 Leader (`org.apache.zookeeper.server.quorum.Learner`) 服务器保持连接. LearnerCnxAcceptor (`org.apache.zookeeper.server.quorum.Leader.LearnerCnxAcceptor`) 负责接收所有 Learner 服务器的连接请求.

> s. __Learner 服务器开始和 Leader 建立连接__

Learner 启动完毕后, 与 Leader 建立连接.

> t. __Leader 服务器创建 LearnerHandler__

Leader 接收到连接请求后, 会创建一个 LearnerHandler (`org.apache.zookeeper.server.quorum.LearnerHandler`) 实例, 每个 LearnerHandler 实例对应一个 Leader 与 Learner 之间的连接, 其负责两者的所有消息通信和数据同步.

> u. __向 Leader 注册__

Learner 和 Leader 建立连接后, Learner 就开始向 Leader 进行注册 (Learner 将自己的基本信息发送给 Leader).

> v. __Leader 解析 Learner 信息, 计算新的 epoch__

Learner 接收到 Learner 的 SID 和 ZXID 后, 根据 ZXID 解析出 epoch_of_learner, 并判断如果 epoch_of_learner 比 Leader 自身的 epoch_of_leader 更大, 则更新 Leader 的 epoch_of_leader 值为如下:
```
epoch_of_leader = epoch_of_learner + 1
```

Leader 的 LearnerHandler 会进行等待, 直到过半的 Learner 向 Leader 注册并更新了 epoch_of_leader 后, Leader 就可以确定当前集群的 epoch.

> w. __发送 Leader 状态__

Leader 确定 epoch 后, 会将该信息以一个 LEADERINFO 的消息发送给 Learner, 并等待响应.

> x. __Learner 发送 ACK 消息__

Learner 收到 LEADERINFO 消息后, 解析出 epoch 和 ZXID, 然后反馈一个 ACKEPOCH 响应.

> y. __数据同步__

Leader 接收到 ACK 后, 就可以开始与其进行数据同步了.

> z. __启动 Leader 和 Learner 服务器__

当有过半的 Learner 完成了数据同步后, Leader 和 Learner 服务器实例就可以开始启动了.

### 5.2.5 Leader 和 Follower 启动

> Aa. __创建并启动会话管理器__

> Ab. __初始化 ZooKeeper 的请求处理链__

和单机版一样, 集群中的每个服务器都会启动串联的请求处理链 (不同角色的服务器会有不同的请求处理链).

> Ac. __注册 JMX 服务__

---

# 六 Leader 选举

## 6.1 Leader 选举概述

### 6.1.1 服务器启动时期的 Leader 选举

假设集群有 3 台机器, 以下为该集群在启动期间的 Leader 选举流程:

首先, 当 Server1 启动时, 因为只有一台机器, 无法进行 Leader 选举.

然后, 当 Server2 启动时, Server1 和 Server2 都试图找到一个 Leader, 以下为选举流程:

> a. __每个 Server 会发出一个投票__

Server1 和 Server2 由于都是初始情况, 所以都投给自己. 假设投票信息为 (`myid`, `ZXID`), 那么 Server1 投票为 (1, 0), Server2 投票为 (2, 0). 然后它们各自将这个投票发送给集群中其他所有机器.

> b. __接收来自各个服务器的投票__

每个服务器在接收到投票后, 会首先判断该投票的有效性 (检查是否是本轮投票, 检查是否来自 LOOKING 状态的服务器).

> c. __处理投票__

针对每个投票, 服务器都需要将别人的投票和自己的投票进行 PK, PK 规则如下:
- 优先检查 ZXID, ZXID 较大的优先作为 Leader.
- 如果 ZXID 相同, 就比较 myid, myid 较大的作为 Leader.

以下为 Server1 和 Server2 的处理:
- Server1: 接收到 Server2 (2, 0) 后, 比较自己的 (1, 0), 发现 Server2 的 myid 更大, 于是将自己的投票更新为 (2, 0), 然后将投票再发出去.
- Server2: 接收到 Server1 (1, 0) 后, 比较自己的 (2, 0), 发现自己的更大, 不需要更新, 然后将投票再发出去.

> d. __统计投票__

每次投票后, 服务器都会统计所有投票, 判断是否有过半 ("过半" 是指大于集群机器数量的一半) 的机器接收到相同投票.

在此场景中, 需要大于等于 2 台机器才过半. 由于 Server1 和 Server2 此时投票信息相同, 因此认为已经选出了 Leader.

> e. __改变服务器状态__

一旦确定 Leader, 每个服务器都会更新自己的状态: Follower 更新为 `FOLLOWING`, Leader 更新为 `LEADING`.

### 6.1.2 服务器运行期间的 Leader 选举

集群一旦选出一个 Leader, 那么所有服务器的集群角色都不会再变化 (即使有非 Leader 挂了或有新机器加入集群). 如果 Leader 挂了, 就会进入新一轮的 Leader 选举.

假设集群有 3 台机器, 某时刻作为 Leader 的 Server2 挂了, 以下为选举流程:

> a. __变更状态__

当 Leader 挂了后, 其他非 Observer 服务器会将状态变更为 LOOKING.

> b. __每个 Server 会发出一个投票__

运行期间每个服务器的 ZXID 可能不同, 假设 Server1 的 ZXID 为 123, Server3 的 ZXID 为 122.

第一轮投票中, Server1 和 Server3 都会投自己, 即 Server1 投票 (1, 123), Server3 投票 (3, 122), 然后各自将投票发送给集群中其他所有机器.

> c. __接收来自各个服务器的投票__

> d. __处理投票__

由于 Server1 的 ZXID (123) 大于 Server3 的 ZXID (122), 所以 Server1 成为 Leader.

> e. __统计投票__

> f. __改变服务器状态__

## 6.2 Leader 选举的算法分析

ZooKeeper 目前只支持 TCP 版本的 FastLeaderElection (`org.apache.zookeeper.server.quorum.FastLeaderElection`) 选举算法.

以下为相关术语介绍:
- `SID` (服务器 ID): 用来标识 ZooKeeper 集群中的一台机器, 和 myid 值一致.
- `ZXID` (事务 ID): 用来标识一次服务器状态的变更, 集群中每台机器的 ZXID 可能不一致.
- `Vote` (投票): 通过投票实现 Leader 选举.
- `Quorum` (过半机器数): 集群中过半的机器, 计算公式如下:
```
quorum = (n/2 + 1)
```
- `vote_sid`: 接收到的投票中所推举的 Leader 服务器的 SID.
- `vote_zxid`: 接收到的投票中所推举的 Leader 服务器的 ZXID.
- `self_sid`: 自己的 SID.
- `self_zxid`: 自己的 ZXID.

### 6.2.1 进入 Leader 选举

如果集群中已经存在一个 Leader, 那么后续启动的机器只需和 Leader 建立连接. 如果集群中不存在 Leader, 会进行 Leader 选举.

当整个集群刚初始化或者运行期间 Leader 挂了时, 集群中的所有机器都会处于 "LOOKING" 状态, 所有机器会向集群中的其他机器发送消息, 该消息称为 "投票", 以 `(SID, ZXID)` 来表示.

假设集群有 5 台机器, SID 为 1, 2, 3, 4, 5, ZXID 为 9, 9, 9, 8, 8. SID 为 2 的机器是 Leader, 某一时刻, SID 为 1 和 2 的机器出现故障, 因此集群开始选举.

### 6.2.2 开始投票

第一次投票, 每台机器都投给自己, 所以 3, 4, 5 机器的投票情况为:
- Server3: (3, 9)
- Server4: (4, 8)
- Server5: (5, 8)

### 6.2.3 变更投票

每台机器会收到其他机器的投票, 以下为对 (vote_sid, vote_zxid) 和 (self_sid, self_zxid) 的对比规则:
- 如果 vote_zxid > self_zxid, 就认可接收到的投票并更新, 然后将更新后的投票发送出去.
- 如果 vote_zxid < self_zxid, 就坚持自己的投票不做任何变更.
- 如果 vote_zxid == self_zxid 且 vote_sid > self_zxid, 就认可接收到的投票并更新, 然后将更新后的投票发送出去.
- 如果 vote_zxid == self_zxid 且 vote_sid < self_zxid, 就坚持自己的投票不做任何变更.

以下为该案例中每台机器的对比情况:
- Server3: 接收到 (4, 8) 和 (5, 8) 两个投票, 由于自己 (3, 9) 的 ZXID 大于接收到的两个投票, 因此坚持自己的投票不做任何变更.
- Server4: 接收到 (3, 9) 和 (5, 8) 两个投票, 由于 (3, 9) 的 ZXID 大于自己, 因此变更投票为 (3, 9), 然后将更新后的投票发送出去.
- Server5: 接收到 (3, 9) 和 (4, 8) 两个投票, 由于 (3, 9) 的 ZXID 大于自己, 因此变更投票为 (3, 9), 然后将更新后的投票发送出去.

### 6.2.4 确定 Leader

经过第二轮投票后, Server3 收到了 Server4, Server5 的投票, Server4 收到了 Server5 的投票, Server5 收到了 Server4 的投票, 此时开始统计投票.

统计投票规则为如果某一台机器收到了超过半数相同的投票, 那么投票对应的机器就为 Leader. 该案例中只要收到 3 个或 3 个以上相同的投票即可, 因为 Server3, Server4, Server5 都收到了 (3, 9), 所以 Leader 为 Server3.

### 6.2.5 小结

通常情况下, 那台服务器上的数据越新, 它的 ZXID 就越大, 也就代表越能够保证数据的恢复, 它就越有可能成为 Leader.

在 ZXID 相同的情况下, SID 较大的就成为 Leader.

## 6.3 Leader 选举的实现细节

ServerState (`org.apache.zookeeper.server.quorum.QuorumPeer.ServerState`) 中标识了服务器的四种状态:
- `LOOKING`: 寻找 Leader 状态, 此时需要进行 Leader 选举.
- `FOLLOWING`: 跟随者状态, 表明当前服务器是 Follower.
- `LEADING`: 领导者状态, 表明当前服务器是 Leader.
- `OBSERVING`: 观察者状态, 表明当前服务器是 Observer.

### 6.3.1 QuorumCnxManager & FastLeaderElection

- 每台服务器都会启动一个 QuorumCnxManager (`org.apache.zookeeper.server.quorum.QuorumCnxManager`), 用于服务器之间 Leader 选举的网络通信.

以下为 QuorumCnxManager 的简单结构:
```java
public class QuorumCnxManager {
    final ConcurrentHashMap<Long, SendWorker> senderWorkerMap;
    final ConcurrentHashMap<Long, ArrayBlockingQueue<ByteBuffer>> queueSendMap;
    final ConcurrentHashMap<Long, ByteBuffer> lastMessageSent;
    public final ArrayBlockingQueue<Message> recvQueue;
    public void receiveConnection(final Socket sock) {
    }
    public void receiveConnectionAsync(final Socket sock) {
    }
    public class Listener extends ZooKeeperThread {
        volatile ServerSocket ss = null;
        @Override
        public void run() {
        }
    }
    class SendWorker extends ZooKeeperThread {
        Long sid;
        Socket sock;
        RecvWorker recvWorker;
        volatile boolean running = true;
        DataOutputStream dout;
        @Override
        public void run() {
        }
    }
    class RecvWorker extends ZooKeeperThread {
        Long sid;
        Socket sock;
        volatile boolean running = true;
        final DataInputStream din;
        final SendWorker sw;
        @Override
        public void run() {
        }
    }
}
```

- ZooKeeper 的选举算法是通过 FastLeaderElection (`org.apache.zookeeper.server.quorum.FastLeaderElection`) 实现的.

以下为 FastLeaderElection 的简单结构:
```java
public class FastLeaderElection implements Election {
    LinkedBlockingQueue<ToSend> sendqueue;
    LinkedBlockingQueue<Notification> recvqueue;
    AtomicLong logicalclock = new AtomicLong();
    protected class Messenger {
        WorkerSender ws;
        WorkerReceiver wr;
        class WorkerReceiver extends ZooKeeperThread {
        }
        class WorkerSender extends ZooKeeperThread {
        }
    }
    public Vote lookForLeader() throws InterruptedException {
        HashMap<Long, Vote> recvset = new HashMap<Long, Vote>();
    }
}
```

### 6.3.2 QuorumCnxManager - 消息队列

QuorumCnxManager 内部会按照每台服务器 (自己和其他) 的 SID 分组, 每一组包含该服务器的队列集合 (接受队列, 发送队列), 不同组之间互不干扰.

- `senderWorkerMap`: 发送器集合, 每个 SendWorker, 都对应一台远程的 ZooKeeper 服务器, 负责消息的发送. 按照 SID 进行分组.
- `queueSendMap`: 消息发送队列, 用于保存待发送的消息. 按照 SID 进行分组, 分别为集群中的每台机器分配了一个单独队列.
- `lastMessageSent`: 最近发送过的消息. 为每个 SID 保留最近发送过的一个消息.
- `recvQueue`: 消息接收队列, 用于从其他服务器接受到的消息.

### 6.3.3 QuorumCnxManager - 建立连接

某台服务器的 QuorumCnxManager 启动的时候, 会创建一个 ServerSocket 来监听 Leader 选举的通信端口 (默认 3888).

开启监听后, 就能够不断地接受来自其他服务器的 "创建连接" 请求. 建立 TCP 连接规则为: 只允许 SID 大的服务器主动和其他服务器建立连接, 否则断开连接.

接收到其他服务器的 TCP 连接请求时, 会交给 receiveConnection() 方法处理. receiveConnection() 方法会对比自己和远程服务器的 SID 值, 如果自己的 SID 值更大, 就会断开当前连接, 然后自己主动去和远程服务器建立连接.

一旦建立起连接, 就会根据远程服务器的 SID 来创建相应的 SendWorker (消息发送器) 和 RecvWorker (消息接收器).

### 6.3.4 QuorumCnxManager - 消息接收与发送

- QuorumCnxManager 中, 消息的接收是由 RecvWorker 负责, ZooKeeper 会为每个远程服务器分配一个单独的 RecvWorker.

每个 RecvWorker 会不断地从这个 TCP 连接中读取 Message (消息), 并将读取到的 Message 保存到 recvQueue 中.

- QuorumCnxManager 中, 消息的发送是由 SendWorker 负责, ZooKeeper 会为每个远程服务器分配一个单独的 SendWorker.

每个 SendWorker 会不断地从消息发送队列 sendqueue 中读取一条消息来进行发送, 同时将这条消息放入 lastMessageSent (最近发送过的消息) 中.

一旦 ZooKeeper 发现针对的某台远程服务器的消息发送队列为空, 就需要从 lastMessageSent 中取出一个最近发送过的消息再次发送, 目的是为了解决接收方在消息接收前或者收到消息后服务器挂了, 导致消息未被正确处理, 当然, 消息接收方在处理消息的时候, 也会避免对消息的重复处理.

### 6.3.5 FastLeaderElection - 选票管理

以下为常用概念:
- 外部投票: 其他服务器发来的投票.
- 内部投票: 服务器自身的投票.
- 选举轮次: Leader 选举的次数, 即 logicalclock.
- PK: 内部投票和外部投票进行对比, 确定是否需要变更内部投票.

以下为 FastLeaderElection 中选票管理的组件:
- `sendqueue`: 选票发送队列.

- `recvqueue`: 选票接收队列.

- `WorkerReceiver`: 选票接收器.

该接收器会不断地从 QuorumCnxManager 中取出其他服务器发来的选举消息, 并将其转换成一个投票, 然后保存到 recvqueue 中. 会有以下几种情况:

a. 如果发现外部投票的选举轮次小鱼当前服务器, 就直接忽略这个外部投票, 同时发出自己的内部投票.

b. 如果当前服务器并不是 LOOKING 状态, 即已经选举出了 Leader, 那么也会忽略这个外部投票, 同时将 Leader 信息以投票的形式发送出去.

c. 如果接收到来自 Observer 服务器, 就忽略该消息, 同时发出自己的内部投票.

- `WorkerSender`: 选票发送器.

该发送器会不断地从 sendqueue 中获取待发送的选票, 并将其传递到底层的 QuorumCnxManager 中去.

### 6.3.6 FastLeaderElection - 算法核心

Leader 选举算法的基本流程, 在 lookForLeader() 方法中实现, 以下为核心步骤:

> a. __自增选举轮次__

FastLeaderElection 中有一个 logicalclock 属性, 用于标识当前 Leader 选举轮次, 所有有效投票都必须在同一轮次中. 因此在开始新的轮次时, logicalclock 会自增.

> b. __初始化选票__

在开始新的轮次时, 每个服务器都会初始化自己的选票, 初始化选票即 Vote 属性的初始化.

代表 Leader 选举投票的 Vote (`org.apache.zookeeper.server.quorum.Vote`) 数据结构简单定义如下:
```java
public class Vote {
    final private int version;
    final private long id;
    final private long zxid;
    final private long electionEpoch;
    final private long peerEpoch;
    final private ServerState state;
    public Vote(int version, long id, long zxid, long electionEpoch, long peerEpoch, ServerState state) {
    }
}
```

- `id`: 被推举的 Leader 的 SID.
- `zxid`: 被推举的 Leader 的事务 ID.
- `electionEpoch`: 逻辑时钟, 用来判断多个投票是否属于同一轮选举周期(该值在服务端是一个自增序列, 每次进入新一轮投票, 都会加 1).
- `peerEpoch`: 被推举的 Leader 的 epoch.
- `state`: 当前服务器状态.

以下为 Vote 初始化后的值:
- `id`: 当前服务器自身的 SID.
- `zxid`: 当前服务器最新的 ZXID.
- `electionEpoch`: 当前服务器的选举轮次.
- `peerEpoch`: 被推举的服务器的选举轮次.
- `state`: LOOKING.

> c. __发送初始化选票__

完成初始化后, 服务器会发起第一次投票, ZooKeeper 会将初始化好的选票放入 sendqueue 中, 由发送器 SendWorker 发送出去.

> d. __接收外部投票__

每台服务器会不断地从 recvQueue 中获取外部投票.

如果服务器发送无法获取到任何外部投票, 就会确认自己是否和集群中其他服务器保持有效连接. 如果没有连接, 就马上建立连接, 如果已经连接, 就再次发送自己的内部投票.

> e. __判断选举轮次__

当发送完初始选票后, 就开始处理外部投票, 会根据选举轮次来进行不同的处理.

- 外部投票的选举轮次大于内部投票

如果外部的选举轮次大于内部, 就会立即更新自己的 logicalclock (选举轮次), 并清空所有收到的投票, 然后初始化投票并和外部投票 PK 以确认是否变更内部投票, 最终再将内部投票发送出去.

- 外部投票的选举轮次小于内部投票

如果外部的选举轮次小于内部, 就忽略该外部投票, 不做任何处理, 并返回步骤 d.

- 外部投票的选举轮次等于内部投票

这是大多数场景, 如果相等, 就开始进行 PK.

> f. __选票 PK__

收到其他服务器有效的外部投票后, 就开始 PK, 其核心逻辑在 totalOrderPredicate() 方法中实现, 因为 PK 的目的是确定当前服务器是否需要变更投票, 因此该方法会返回 boolean 值.

以下三个条件, 只要符合任意一个就需要进行投票变更:
- a. 如果外部投票中被推举的 Leader 服务器的选举轮次大于内部投票, 那么就需要进行投票变更.
- b. 如果选举轮次一致, 就对比两者的 ZXID, 如果外部投票的 ZXID 大于内部投票, 那么就需要进行投票变更.
- c. 如果两者的 ZXID 一致, 就对比两者的 SID, 如果外部投票的 SID 大于内部投票, 那么就需要进行投票变更.

> g. __投票变更__

选票 PK 如果确认需要进行投票变更, 就使用外部投票覆盖内部投票, 然后将该内部投票发送出去.

> h. __选票归档__

无论是否进行了投票变更, 都会将刚收到的外部投票按照 SID 区分, 放入 totalOrderPredicate() 方法的变量 `HashMap<Long, Vote> recvset` (选票集合) 中, 用于记录当前服务器在本轮次中收到的所有外部投票, 如 {(1, vote1), (2, vote2), ...}.

> i. __统计投票__

投票归档后, 就开始统计投票, 其过程就是统计集群中是否有过半的机器认可了当前的内部投票, 如果有则认可该内部投票并终止投票.

> j. __更新服务器状态__

统计投票后, 如果确定可以终止投票, 就开始更新服务器状态.

服务器首先判断当前被过半服务器认可的投票是否是自己, 如果是自己, 就将自己的状态更新为 LEADING, 如果不是自己, 就根据情况确定自己是 FOLLOWING 或 OBSERVING.

> __小结__

以上步骤中的 `d ~ i` 会循环, 直到 Leader 选举产生.

在完成 步骤 i 后, 如果发现已经有过半的服务器认可了当前投票, 并不会立即进入下一步, 而是会等待一段时间 (默认 200ms) 来确认是否有新的更优的投票.

---

# 七 各服务器角色介绍

## 7.1 Leader

Leader 服务主要工作有以下两个:
- 事务请求的唯一调度和处理者, 保证集群事务处理的顺序性.
- 集群内部各服务器的调度者.

### 7.1.1 请求处理链

使用责任链模式来处理客户端的每一个请求.

![image](https://raw.githubusercontent.com/zozospider/note/master/distributed/ZooKeeper/ZooKeeper-book-ZooKeeper%E6%8A%80%E6%9C%AF%E5%86%85%E5%B9%95/71-Leader-Processing-chain.png)

> __PrepRequestProcessor__

PrepRequestProcessor (`org.apache.zookeeper.server.PrepRequestProcessor`) 是 Leader 服务器的顶级预处理器.

PrepRequestProcessor 能够识别出当前客户端请求是否是事务请求 (事务请求是指会改变服务器状态的请求, 比如创建会话, 创建节点, 更新数据, 删除节点等). 对于事务请求, PrepRequestProcessor 会对其进行一系列预处理, 如创建请求事务头, 事务体, 会话检查, ACL 检查, 版本检查等.

> __ProposalRequestProcessor__

ProposalRequestProcessor (`org.apache.zookeeper.server.quorum.ProposalRequestProcessor`) 是 Leader 服务器的事务投票处理器, 也是 Leader 服务器的事务处理流程的发起者.

对于非事务请求, ProposalRequestProcessor 会将请求流转到 CommitProcessor 处理器, 不再做其他处理.

对于事务请求, ProposalRequestProcessor 不仅会将请求交给 CommitProcessor 处理器, 还会将事务请求交给 SyncRequestProcessor 进行事务日志记录.

另外, 对于事务请求, ProposalRequestProcessor 还会根据请求类型创建对应的 Proposal 提议, 并发送给所有的 Follower 服务器来发起一次集群内的事务投票.

> __SyncRequestProcessor__

SyncRequestProcessor (`org.apache.zookeeper.server.SyncRequestProcessor`) 是事务日志记录器, 主要用来将事务请求记录到事务日志文件中, 同时还会触发 ZooKeeper 进行数据快照.

> __AckRequestProcessor__

AckRequestProcessor (`org.apache.zookeeper.server.quorum.AckRequestProcessor`) 是 Leader 特有的处理器, 负责在 SyncRequestProcessor 完成事务日志记录后, 向 Proposal 的投票收集器发送 ACK 反馈, 以通知投票收集器当前服务器已经完成了对该 Proposal 的事务日志记录.

> __CommitProcessor__

CommitProcessor (`org.apache.zookeeper.server.quorum.CommitProcessor`) 是事务提交处理器, 可以很好的控制对事务请求的顺序处理.

对于非事务请求, CommitProcessor 会将请求交给下一级处理器.

对于事务请求, CommitProcessor 会等待集群内针对 Proposal 的投票, 直到该 Proposal 可被提交.

> __ToBeAppliedRequestProcessor__

ToBeAppliedRequestProcessor (`org.apache.zookeeper.server.quorum.Leader.ToBeAppliedRequestProcessor`) 有一个 `ConcurrentLinkedQueue<Proposal> toBeApplied` 队列, 用来存储已经被 CommitProcessor 处理过的可被提交的 Proposal.

ToBeAppliedRequestProcessor 将这些请求逐个交给 FinalRequestProcessor 处理器, 等到 FinalRequestProcessor 处理器处理完之后, 再将其从 toBeApplied 队列中移除.

> __FinalRequestProcessor__

FinalRequestProcessor (`org.apache.zookeeper.server.FinalRequestProcessor`) 是最后一个处理器, 主要用来进行客户端请求返回之前的收尾工作.

针对事务请求, FinalRequestProcessor 还会将事务应用到内存数据库中.

### 7.1.2 LearnerHandler

LearnerHandler (`org.apache.zookeeper.server.quorum.LearnerHandler`) 是 Learner 服务器的管理器, 主要负责 Leader 服务器和 Follower/Observer 服务器之间的网络通信, 如数据同步, 请求转发, Proposal 提议投票等.

Leader 服务器会与每个 Follower/Observer 服务器建立一个 TCP 长连接, 会为每一个 Follower/Observer 服务器创建一个 LearnerHandler 实体.

## 7.2 Follower

Follower 服务主要工作有以下三个:
- 处理客户端非事务请求, 转发事务请求给 Leader.
- 参与事务请求 Proposal 投票.
- 参与 Leader 选举投票.

### 7.2.1 请求处理链

## 7.3 Observer

### 7.3.1 请求处理链

## 7.4 集群间消息通信

ZooKeeper 集群的工作都是由 Leader 服务器来负责进行协调, 各服务器间的网络通讯, 是通过不同类型的消息传递来实现的.

以下为 ZooKeeper 的四种消息类型:

### 7.4.1 数据同步型

数据同步型是指在进行数据同步的时候, Learner 和 Leader 相互通信所用的消息. 以下为消息类型:

| 消息类型 | 发送方 -> 接收方 | 说明 |
| :--- | :--- | :--- |
| DIFF, 13 | Leader -> Learner | 用于通知 Learner, Leader 即将与其进行 "DIFF" 方式的数据同步 |
| TRUNC, 14 | Leader -> Learner | 用于触发 Learner 进行内存数据库的回滚操作 |
| SNAP, 15 | Leader -> Learner | 用于通知 Learner, Leader 即将与其进行 "全量" 方式的数据同步 |
| UPTODATE, 12 | Leader -> Learner | 用来告诉 Learner, 已经完成了数据同步, 可以安康i是对外提供服务了 |

### 7.4.2 服务器初始化型

服务器初始化型是指在整个集群或是某些新机器初始化的时候, Learner 和 Leader 相互通信所用的消息. 以下为消息类型:

| 消息类型 | 发送方 -> 接收方 | 说明 |
| :--- | :--- | :--- |
| OBSERVERINFO, 16 | Observer -> Leader | Observer 在启动的时候发送给 Leader, 用于向 Leader 注册自己身份. 消息中包含 SID 和最新的 ZXID |
| FOLLOWERINFO, 11 | Follower -> Leader | Follower 在启动的时候发送给 Leader, 用于向 Leader 注册自己身份. 消息中包含 SID 和最新的 ZXID |
| LEADERINFO, 17 | Leader -> Learner | Learner 连接上 Leader 后, 会发送 LearnerInfo (OBSERVERINFO/FOLLOWERINFO), Leader 接收到消息后, 会将 LEADERINFO 发送给 Learner, 消息中包含当前 Leader 最新的 EPOCH 值 |
| ACKEPOCH, 18 | Learner -> Leader | Learner 收到 Leader 发送的 LEADERINFO 后, 会将 ACKEPOCH (自己最新的 ZXID 和 EPOCH) 发送给 Leader |
| NEWLEADER, 10 | Leader -> Learner | 该消息用于 Leader 向 Learner 发送一个阶段性标识 (带上 Leader 最新的 ZXID), 如完成数据同步, 足够多的 Follower 连接上 Leader 等 |

### 7.4.3 请求处理型

请求处理型是指在进行请求处理的时候, Learner 和 Leader 相互通信所用的消息. 以下为消息类型:

| 消息类型 | 发送方 -> 接收方 | 说明 |
| :--- | :--- | :--- |
| REQUEST, 1 | Learner -> Leader | 当 Learner 接收到客户端的事务请求后, 就会将请求以 REQUEST 消息的形式转发给 Leader 来处理 |
| PROPOSAL, 2 | Leader -> Follower | ZAB 协议中的提议. 在处理事务请求的时候, Leader 会见将事务请求以 PROPOSAL 消息的形式创建投票发送给所有 Follower 来进行事务日志的记录 |
| ACK, 3 | Follower -> Leader | FOllower 接收到 Leader 的 PROPOSAL 消息, 并完成事务日志的记录后, 会以 ACK 消息反馈给 Leader |
| COMMIT, 4 | Leader -> Follower | Leader 在接收到过半的 Follower 发来的 ACK 消息后, 就进入事务请求的最终提交流程, 生成 COMMIT 消息, 告知所有 Follower 进行事务请求的提交, 因为在这之前的事务请求投票阶段, Follower 已经接收过 PROPOSAL 消息, 因此 Follower 可以从之前的 Proposal 缓存中再次获取到事务请求 |
| INFORM, 8 | Learner -> Observer | Observer 由于之前没有参与事务请求的投票, Leader 通过在 INFORM 消息中携带事务请求的内容, 通知 Observer 进行事务请求的提交 |
| SYNC, 7 | Leader -> Learner | 用于通知 Learner 已经完成了 Sync 操作 |

### 7.4.4 会话管理型

会话管理型是指在进行会话管理的过程中, Learner 和 Leader 相互通信所用的消息. 以下为消息类型:

| 消息类型 | 发送方 -> 接收方 | 说明 |
| :--- | :--- | :--- |
| PING, 1 | Leader -> Learner | // TODO |
| REVALIDATE, 2 | Learner -> Leader | 通常发生在客户端重连的时候, 新的服务器需要向 Leader 发送 REVALIDATE 消息以确定该会话是否已经超时 |

---

# 八 请求处理

## 8.1 事务请求转发

为了保证事务请求被顺序执行, 从而保证 ZooKeeper 集群的数据一致性, 所有的事务请求都必须由 Leader 服务器来处理. 因此, 所有非 Leader 服务器接收到的事务请求, 都必须转发给 Leader 服务器来处理.

Follower 服务器的第一个请求处理器是 `FollowerRequestProcessor`, Observer 服务器的第一个请求处理器是是 `ObserverRequestProcessor`, 他们都会检查当前请求是否是事务请求, 如果是, 就会将客户端请求以 REQUEST 消息的形式转发给 Leader 服务器.

Leader 服务器接收到该消息后, 会解析出客户端的原始请求, 然后提交到自己的请求处理链中进行处理.

## 8.2 会话创建请求

### 8.2.1 请求接受

> a. __I/O 层接收来自客户端的请求__

客户端与服务端的所有通信是由 NIOServerCnxn (`org.apache.zookeeper.server.NIOServerCnxn`) 负责, 其会将客户端的请求内容从底层网络 I/O 中读取出来. 每个会话都对应一个 NIOServerCnxn 实体.

> b. __判断是否是客户端 "会话创建" 请求__

对于每个请求, ZooKeeper 都会检查当前会话对应的 NIOServerCnxn 实体是否已经初始化, 如果没有初始化, 说明该客户端请求是 "会话创建" 请求. 因此, 第一个请求一定是 "会话创建".

> c. __反序列化 ConnectRequest 请求__

对该请求进行反序列化, 生成一个 ConnectRequest 请求实体.

> d. __判断是否是 ReadOnly 客户端__

如果当前 ZooKeeper 服务端是以 ReadOnly 模式启动, 那么所有非 ReadOnly 请求都不会处理. 因此, 会检查 ConnectRequest 是否是 ReadOnly 客户端.

> e. __检查客户端 ZXID__

同一个 ZooKeeper 集群中, 服务端的 ZXID 一定大于客户端的 ZXID. 因此, 如果发现客户端的 ZXID 大于服务端的 ZXID, 将不接受该请求.

> f. __协商 sessionTimeout__

客户端在构造 ZooKeeper 实例的时候, 会有一个 sessionTimeout 参数用于指定会话超时时间.

服务端会根据自己的超时时间最终确定该会话的超时时间, 该过程称为 "sessionTimeout 协商".

默认情况下, 服务端会对超时时间限制在 2 个 tickTime ~ 20 个 tickTime 之间. 假设 tickTime 为 2000ms, 那么服务端就会限制客户端超时时间在 4s ~ 40s.

> g. __判断是否需要重新创建会话__

如果客户端请求中包含 sessionID, 就认为客户端正在进行会话重连, 此时服务端只需重新打开这个会话, 否则需要重新创建.

### 8.2.2 会话创建

> h. __为客户端生成 sessionID__

每个服务端启动时, 都会初始化一个 SessionTracker (会话管理器), 同时初始化 `基准 sessionID`, 针对每个客户端, 只需在这个基准 sessionID 的基础上逐个递增即可.

> i. __注册会话__

SessionTracker 中有 `ConcurrentHashMap<Long, Integer> sessionsWithTimeout` (根据 sessionID 保存了所有会话的超时时间) 和 `HashMap<Long, SessionImpl> sessionsById = new HashMap<Long, SessionImpl>()` (根据 sessionID 保存了所有会话实体) 两个属性.

在会话创建初期, 会将该客户端会话的相关信息保存到这两个属性中.

> j. __激活会话__

激活会话的核心是为会话安排一个区块, 以便会话清理程序能够高效清理会话 (参考 `分桶策略`).

> k. __生成会话密码__

服务端在创建会话时, 会生成一个会话密码, 以下为会话密码的生成方式:
```java
static final private long superSecret = 0XB3415C00L;
Random r = new Random(sessionId ^ superSecret);
r.nextBytes(passwd);
```

服务端会将 sessionID 和 会话密码一起发送给客户端, 作为会话在集群中不同机器间转移的凭证.

### 8.2.3 预处理

> l. __将请求交给 ZooKeeper 的 PrepRequestProcessor 处理器进行处理__

ZooKeeper 针对客户端的请求采用责任链模式处理. 在第一个请求处理器 PrepRequestProcessor 处理前, ZooKeeper 会根据请求所属的会话进行一次会话激活, 完成会话激活后, 就将该请求提交给 PrepRequestProcessor 处理器.

> m. __创建请求事务头__

对于事务请求, ZooKeeper 会为其创建请求事务头, 服务端后续的请求处理器都是基于该请求头来识别当前请求是否时事务请求.

请求事务头包含如下信息:
- `clientId`: 客户端 ID, 用于唯一标识该请求所属客户端.
- `cxid`: 客户端的操作序列号.
- `zxid`: 该事务请求对应的事务 ZXID.
- `time`: 服务器开始处理该事务请求的时间.
- `type`: 事务请求类型, 如 create, delete, setData, createSession (定义在 `org.apache.zookeeper.ZooDefs.OpCode` 中) 等.

> n. __创建请求事务体__

对于事务请求, ZooKeeper 会为其创建请求事务体, "会话创建" 对应 CreateSessionTxn (`org.apache.zookeeper.txn.CreateSessionTxn`).

> o. __注册与激活会话__

与步骤 i 相同, 当非 Leader 服务器转发会话创建请求过来时, 此时尚未在 Leader 的 SessionTracker 中进行会话注册, 因此需要在此进行一次注册与激活.

> p. __将请求交给 ProposalRequestProcessor 处理器__

ProposalRequestProcessor 会将请求交给下一级 ProposalRequestProcessor.

ProposalRequestProcessor 是一个与提案相关的处理器, 是 ZooKeeper 中针对事务请求所展开的一个投票流程中对事务操作的包装. 从该处理器开始, 会进入三个子处理流程: Sync 流程, Proposal 流程, Commit 流程.

### 8.2.4 事务处理 - Sync 处理

> r. __Sync 处理__

Sync 流程, 就是使用 SyncRequestProcessor (`org.apache.zookeeper.server.SyncRequestProcessor`) 处理器记录事务日志的过程. Leader 和 Follower 的请求处理链都有这个处理器, 两者在事务日志的记录功能上完全一致.

SyncRequestProcessor 首先会判断该请求是否是事务请求, 对于每一个事务请求, 都会通过事务日志的形式将其记录下来.

完成事务日志记录后, 每个 Follower 服务器都会向 Leader 服务器发送 ACK 消息, 表明自身完成了事务日志的记录, 以便 Leader 服务器统计每个事务请求的投票情况.

### 8.2.5 事务处理 - Proposal 流程

在 ZooKeeper 中, 每一个事务请求都需要集群中过半机器投票认可才能被真正应用到 ZooKeeper 的内存数据库中, 这个投票与统计的过程称为 `Proposal 流程`.

> s. __发起投票__

如果当前请求是事务请求, Leader 服务器就会发起一轮事务投票.

在发起投票前, 首先会检查当前服务器的 ZXID 是否可用, 如果不可用, 将会抛出 XidRolloverException 异常.

> t. __生成 Proposal 提议__

如果当前服务器的 ZXID 可用, 就可以开始事务投票了.

ZooKeeper 会将创建的事务请求头, 事务请求体, ZXID, 请求本身序列号到 Proposal 对象中 (Proposal 对象就是一个提议, 是针对 ZooKeeper 服务器状态变更的申请).

> u. __广播 Proposal 提议__

生成提议后, Leader 服务器会以 ZXID 为标识, 将该 Proposal 提议放入投票箱 outstandingProposals 中, 同时会将该 Proposal 提议广播给所有 Follower 服务器.

> v. __收集投票__

Follower 服务器在接收到 Leader 服务器发来的 Proposal 提议后, 会进入 Sync 流程来进行事务日志的记录, 一旦记录完成, 就会发送 ACK 消息给 Leader 服务器.

Leader 服务器根据这些 ACK 消息来统计每个 Proposal 提议的投票情况, 当一个 Proposal 提议获得了集群中过半机器的投票, 就认为该 Proposal 提议通过, 可以进入 Commit 提交阶段.

> w. __将请求放入 toBeApplied 队列__

在该 Proposal 提议被 commit 提交前, 首先会将其放入 toBeApplied 队列中.

> x. __广播 COMMIT 消息__

一旦 ZooKeeper 确认一个 Proposal 提议已经可以被提交, Leader 服务器就会向 Follower 服务器和 Observer 服务器发送 COMMIT 消息, 以便所有服务器都能够提交该 Proposal 提议.

由于 Observer 服务器并未参与之前的提议, 因此 Observer 服务器尚未保存任何关于该 Proposal 提议的信息, 所以在广播的时候, 需要区别对待. Leader 服务器会向其发送一个 INFORM 消息 (该消息中包含了当前 Proposal 提议的内容), 而 Follower 服务器由于已经保存了 Proposal 提议信息, 因此 Leader 服务器只需要向其发送 ZXID 即可.

### 8.2.6 事务处理 - Commit 流程

> y. __将请求交付给 CommitProcessor 处理器__

CommitProcessor (`org.apache.zookeeper.server.quorum.CommitProcessor`) 处理器收到 COMMIT 请求后, 并不会立即处理, 而是会将其放入 queuedRequests 队列中.

> z. __处理 queuedRequests 队列请求__

CommitProcessor 处理器会有一个单独的线程来处理从上一级处理器流转下来的请求. 当检测到 queuedRequests 队列中由新的请求时, 就会逐个从队列中取出请求进行处理.

> Aa. __标记 nextPending__

如果从 queuedRequests 队列中取出的是一个事务请求, 就需要将 `Request nextPending` 标记为当前请求 (便于 CommitProcessor 处理器检测当前集群是否正在进行事务请求的投票, 同时确保事务请求的顺序性).

另外, 需要进行集群中各服务器之间的投票处理.

> Ab. __等待 Proposal 投票__

在 Commit 流程处理的同时, Leader 已经根据当前事务请求生成一个 Proposal 提议, 并广播给了所有的 Follower 服务器, 因此, 这时候 Commit 流程需要等待, 直到投票结束.

> Ac. __投票通过__

如果一个 Proposal 提议已经获得过半机器的投票认可, 那么就会进入请求提交阶段, ZooKeeper 会将该请求放入 `LinkedList<Request> queuedRequests` 队列中, 同时唤醒 Commit 流程.

> Ad. __提交请求__

一旦发现 queuedRequests 队列中已经有可以提交的请求了, Commit 流程就会开始提交请求.

为了保证事务请求的顺序性, Commit 流程还会对比之前标记的 nextPending 和 queuedRequests 队列中的第一个请求是否一致. 如果检查通过, Commit 流程就会将该请求放入 `ArrayList<Request> toProcess` 队列中, 然后交给下一个 FinalRequestProcessor 处理器.

### 8.2.7 事务应用

> Ae. __交付给 FinalRequestProcessor 处理器__

FinalRequestProcessor (`org.apache.zookeeper.server.FinalRequestProcessor`) 处理器会先检查 `final List<ChangeRecord> outstandingChanges` 队列中请求的有效性, 如果发现这些请求已经落后于当前正在处理的请求, 就直接从 outstandingChanges 队列中移除.

> Af. __事务应用__

在之前的请求处理逻辑中, 仅仅是将事务请求记录到了事务日志中, 内存数据库中的状态尚未变更. 因此该步骤就是将事务变更应用到内存数据库中.

但是对于 "会话创建" 这类事务请求, ZooKeeper 做了特殊处理: 因为在 ZooKeeper 内存中, 会话管理都是由 SessionTracker 负责, 不涉及内存数据库的变更. 而在步骤 i 注册会话中, 已经将会话信息注册到了 SessionTracker 中, 因此此时只需要再次向 SessionTracker 注册会话即可.

> Ag. __将事务请求放入队列 commitProposal__

一旦完成事务请求的内存数据库应用, 就可以将该请求放入 commitProposal 队列中.

commitProposal 队列用来保存最近被提交的事务请求, 以便集群间机器进行数据的快速同步.

### 8.2.8 会话响应

客户端请求在经过 ZooKeeper 服务端处理链路的所有请求处理器处理后, 就会进入最后的会话响应阶段:

> Ah. __统计处理__

ZooKeeper 会计算请求在服务端处理所花费的时间, 还会统计客户端连接的基本信息, 如 lastZxid (最新的 ZXID), lastOp (最后一次和服务端的操作), lastLatency (最后一次请求处理所花费的时间) 等.

> Ai. __创建响应 ConnectResponse__

ConnectResponse (`org.apache.zookeeper.proto.ConnectResponse`) 包含了 protocolVersion (当前客户端与服务端之间的通信协议版本号), timeOut (会话超时时间), sessionId, passwd (会话密码).

> Aj. __序列化 ConnectResponse__

> Ak. __I/O 层发送响应给客户端__

## 8.3 SetData 请求

### 8.3.1 预处理

> a. __I/O 层接收来自客户端的请求__

> b. __判断是否是客户端 "创建会话" 请求__

ZooKeeper 会判断每一个客户端请求是否是 "创建会话" 请求, 由于此时已经完成了会话创建, 因此按照正常的事务请求进行处理.

> c. __将请求交给 ZooKeeper 的 PrepRequestProcessor 处理器进行处理__

> d. __创建请求事务头__

> e. __会话检查__

检查会话是否超时, 如果超时, 会抛出 `SessionExpiredException` 异常.

> f. __反序列化请求, 并创建 ChangeRecord 记录__

ZooKeeper 会将客户端请求反序列化并生成 SetDataRequest (`org.apache.zookeeper.proto.SetDataRequest`) 请求. SetDataRequest 中包含 path (节点路径), data (更新的数据内容), version (期望的数据节点版本号).

ZooKeeper 会根据请求中的 path 生成一个 `ZooKeeperServer` 中的 ChangeRecord (`org.apache.zookeeper.server.ZooKeeperServer.ChangeRecord`) 记录, 并放入 outstandingChanges (`final List<ChangeRecord> outstandingChanges`) 队列中, outstandingChanges 队列存放了当前 ZooKeeper 服务器正在处理的事务请求.

> g. __ACL 检查__

检查该客户端是否具有数据更新的权限, 如果没有, 会抛出 `NoAuthException` 异常.

> h. __数据版本检查__

ZooKeeper 可依靠 version 属性来实现乐观锁机制中的 "写入校验", 如果当前数据内容的版本号与客户端预期版本号不匹配, 就会抛出异常.

> i. __创建请求事务体 SetDataTxn__

> j. __保存事务操作到 outstandingChanges 队列中去__

### 8.3.2 事务处理

与 `会话创建` 中的事务处理一致.

### 8.3.3 事务应用

> k. __交付给 FinalRequestProcessor__

> l. __事务应用__

ZooKeeper 会将请求事务头和事务体交给内存数据库 ZKDatabase 进行事务应用.

> m. __将事务请求放入队列: commitProposal__

### 8.3.4 请求响应

> n. __统计处理__

> o. __创建响应体 SetDataResponse__

> p. __创建响应头__

> q. __序列化响应__

> r. __I/O 层发送响应给客户端__

## 8.4 GetData 请求

### 8.4.1 预处理

> a. __I/O 层接收来自客户端的请求__

> b. __判断是否是客户端 "创建会话" 请求__

> c. __将请求交给 ZooKeeper 的 PrepRequestProcessor 处理器进行处理__

> d. __创建请求事务头__

> e. __会话检查__

因为 GetData 请求是非事务请求, 因此无需进行事务处理逻辑 (如创建请求事务头, ChangeRecord, 请求事务体等).

### 8.4.2 非事务处理

> f. __反序列化 GetDataRequest 请求__

> g. __获取数据节点__

根据反序列化的 GetDataRequest 对象 (包括 path, Wathcer), ZooKeeper 会从内存数据库中获取到该节点及其 ACL 信息.

> h. __ACL 检查__

> i. __获取数据内容和 stat, 注册 Watcher__

### 8.4.3 请求响应

> j. __创建响应体 GetDataRespose__

> k. __创建响应头__

> l. __统计处理__

> m. __序列化响应__

> n. __I/O 层发送响应给客户端__

---

# 九 数据与存储

## 9.1 内存数据

ZooKeeper 数据存储分为内存数据存储和磁盘数据存储.

ZooKeeper 的数据模型是一棵树, 在 ZooKeeper 的内存数据库中, 存储了整棵树的内容 (包括所有节点路径, 节点数据及其 ACL 信息等).

ZooKeeper 会定时将这棵树存储到磁盘上.

### 9.1.3 ZKDatabase

ZKDatabase (`org.apache.zookeeper.server.ZKDatabase`) 是 ZooKeeper 的内存数据库, 负责管理 ZooKeeper 的所有会话, DataTree 存储和事务日志.

ZKDatabase 会定时向磁盘 dump 快照数据, 同时在 ZooKeeper 服务器启动的时候, 通过磁盘上的事务日志和快照数据文件恢复成一个完整的内存数据库.

ZKDatabase 的简单结构如下:
```java
public class ZKDatabase {
    protected DataTree dataTree;
    protected ConcurrentHashMap<Long, Integer> sessionsWithTimeouts;
    protected FileTxnSnapLog snapLog;
    protected LinkedList<Proposal> committedLog = new LinkedList<Proposal>();
    protected ReentrantReadWriteLock logLock = new ReentrantReadWriteLock();
    volatile private boolean initialized = false;
    public ZKDatabase(FileTxnSnapLog snapLog) {
    }
}
```

### 9.1.1 DataTree

DataTree 代表了内存中的一份完整的数据, DataTree 是独立的组件, 他不包含任何与网络, 客户端连接, 请求处理等相关逻辑.

DataTree 的简单结构如下:
```java
public class DataTree {
    // nodes
    private final ConcurrentHashMap<String, DataNode> nodes = new ConcurrentHashMap<String, DataNode>();
    public void addDataNode(String path, DataNode node) {
    }
    public DataNode getNode(String path) {
    }
    // ephemerals
    private final Map<Long, HashSet<String>> ephemerals = new ConcurrentHashMap<Long, HashSet<String>>();
    public HashSet<String> getEphemerals(long sessionId) {
    }
    // specialPath
    private DataNode root = new DataNode(null, new byte[0], -1L, new StatPersisted());
    private DataNode procDataNode = new DataNode(root, new byte[0], -1L, new StatPersisted());
    private DataNode quotaDataNode = new DataNode(procDataNode, new byte[0], -1L, new StatPersisted());
    boolean isSpecialPath(String path) {
    }
}
```

> __nodes__

DataTree 的 `nodes` 存放了所有的数据节点, 对于 ZooKeeper 数据的所有操作, 底层都是对这个 Map 结构的操作, 该 Map 的 key 为数据节点路径, value 为数据节点的内容: DataNode.

DataTree 的 `ephemerals` 保存了所有的临时节点, 该 Map 的 key 为 sessionId, value 为临时节点的路径集合.

### 9.1.2 DataNode

DataNode 是数据存储的最小单元.

DataNode 的简单结构如下:
```java
public class DataNode implements Record {
    DataNode parent;
    byte data[];
    Long acl;
    public StatPersisted stat;
    private Set<String> children = null;
    public synchronized boolean addChild(String child) {
    }
    public synchronized boolean removeChild(String child) {
    }
    synchronized public void deserialize(InputArchive archive, String tag) throws IOException {
    }
    synchronized public void serialize(OutputArchive archive, String tag) throws IOException {
    }
}
```

- `data[]`: 节点的数据内容
- `acl`: ACL 列表
- `stat`: 节点状态
- `parent`: 父节点
- `children`: 子节点列表

## 9.2 事物日志

### 9.2.1 文件存储

ZooKeeper 用于存储日志文件的目录是在 `zoo.cfg` 中配置的 `dataLogDir`, 如果 dataLogDir 没有配置, 则使用 `dataDir`.

假设 `dataLogDir=/home/user/zkData/zk_log`, 那么 ZooKeeper 在运行时会在该目录下创建一个 `version-2` 子目录, 该目录为当前 ZooKeeper 使用的事务日志格式版本号, 如果下次某个 ZooKeeper 版本对事务日志格式进行了变更, 该目录就会变更. 以下为 `/home/user/zkData/zk_log/version-2` 目录下的文件:
```
-rw-rw-r-- 1 user user 67108880 02-23 16:10 log.2c01631713
-rw-rw-r-- 1 user user 67108880 02-23 17:07 log.2c0164334d
-rw-rw-r-- 1 user user 67108880 02-23 16:10 log.2d01654af8
-rw-rw-r-- 1 user user 67108880 02-23 16:10 log.2d0166a224
```

这些日志为 ZooKeeper 的事务日志, 具有以下特点:
- 文件大小一致, 都是 67108880KB (64M).
- 文件名后缀有规律, 都是一个十六进制数字, 随着修改时间的推移, 该后缀变大.

该文件名后缀是一个 ZXID (事务 ID), 是写入该事务日志文件的第一条事务记录的 ZXID. 由于 ZXID 是由两部分组成, 高 32 位代表当前 Leader 的 epoch (周期), 低 32 位代表真正的操作序列号, 因此, 使用 ZXID 作为事务日志的后缀, 可以清楚的得出当前运行时 ZooKeeper 的 Leader 周期.

以上的 4 个事务日志, 前 2 个的 epoch 是十进制 44 (十六进制 2c), 后 2 个的 epoch 是十进制 45 (十六进制 2d). 

### 9.2.2 日志格式

用二进制编辑器打开某个事务日志文件后发现内容无法用肉眼识别, 里面的内容是序列化之后的事务日志. 文件前部分为有效内容, 后部分被 `0`/`\0` 填充.

ZooKeeper 提供了事务日志格式化工具 LogFormatter (`org.apache.zookeeper.server.LogFormatter`), 可将事务日志文件转换成可视化的事务操作日志, 使用方法如下:
```
java LogFormatter log.300000001
```

以下为执行后的输出结果示例:
```
ZooKeeper Transactional Log File with dbid 0 txnlog format version 2

...

..11:07:41 session 0x144699552020000 cxi 0x0 zxid 0x300000002 createSession 30000

..11:08:40 session 0x144699552020000 cxid 0x2 zxid 0x300000003 create '/test_log,#7631,v(s(31,s('world,'anyone))),F,2

..11:08:54 session ... cxid 0x3 zxid 0x300000004 setData 'test_log,#7632,1

..11:09:11 session ... cxid 0x4 zxid 0x300000005 create 'test_log/c,#7631,v(s(31,s('world,'anyone))),F,1

..11:09:26 session ... cxid 0x5 zxid 0x300000006 delete '/test_log/c

...

EOF reached after 7 txns.
```

由于这是一个事务操作日志, 因此没有任何读操作记录, 每一行对应一次事务操作, 节点内容也可以参考 LogFormatter 源码自行分析:

- 第一行: `ZooKeeper Transactional Log File with dbid 0 txnlog format version 2`

这是事务日志的文件头信息, 主要输出事务日志的 DBID, 日志格式版本号.

- 第二行: `..11:07:41 session 0x144699552020000 cxi 0x0 zxid 0x300000002`

这是一次客户端会话创建的事务, 从左到右分别为: 事务操作时间, 客户端会话 ID, CXID (客户端的操作序列号), ZXID, 操作类型, 会话超时时间.

- 第三行: `..11:08:40 session 0x144699552020000 cxid 0x2 zxid 0x300000003 create '/test_log,#7631,v(s(31,s('world,'anyone))),F,2`

这是一次节点创建的事务, 从左到右分别为: 事务操作时间, 客户端会话ID, CXID, ZXID, 操作类型, 节点路径, 节点数据内容 (#7631 初始化为 v1, 使用 #+内容的 ASCII 码值), 节点的 ACL 信息, 是否是临时节点 (F 代表持久节点, T 代表临时节点), 父节点的子节点版本号.

### 9.2.3 日志写入

### 9.2.4 日志截断

## 9.3 snapshot - 数据快照

## 9.4 初始化

## 9.5 数据同步

---
