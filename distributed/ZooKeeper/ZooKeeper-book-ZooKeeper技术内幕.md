# Document & Code

* [../Zookeeper-book](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-book.md)

---

# 一 系统模型

## 1.1 数据模型

ZooKeeper 使用 `数据节点`, 称为 `ZNode`, 是 ZooKeeper 中数据的最小单元, 每个 ZNode 可以保存数据, 也可以挂载子节点.

## 1.2 节点特性

ZooKeeper 的每个数据节点都是有生命周期的, 节点类型可分为: 持久节点(`PERSISTENT`), 临时节点(`EPHEMERAL`), 顺序节点(`SEQUENTIAL`). 可生成以下四种组合类型:
- 持久节点(`PERSISTENT`): 是指数据节点被创建后, 会一直存在 ZooKeeper 服务器上, 直到有主动删除操作.
- 持久顺序节点(`PERSISTENT_SEQUENTIAL`): 在持久节点基础上增加顺序性. 创建子节点的时候, 可以设置一个顺序标记, 在创建时, ZooKeeper 会自动为给定节点加上一个数字后缀(上限是整型的最大值), 作为一个新的, 完整的节点名.
- 临时节点(`EPHEMERAL`): 生命周期和客户端会话绑定, 如果客户端会话失效, 节点会自动清理.ZooKeeper 规定了临时节点只能作为叶子节点(不能基于临时节点来创建子节点).
- 临时顺序节点(`EPHEMERAL_SEQUENTIAL`): 在临时节点基础上增加顺序性.

### 1.2.1 状态信息

每个数据节点除了存储数据内容外, 还存储节点本身的状态信息(可通过 `get` 命令获取). 以下为 get 命令获取的结果:
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
  - `pzxid`: 该节点的子节点列表最后一次被修改时的事务 ID(只有子节点列表变更才会更新 pzxid, 子节点内容变更不会影响 pzxid).

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

乐观锁称为乐观并发控制(Optimistic Concurrency Control, OCC), 具有宽松和友好性. 乐观锁假定不同事务之间处理过程不会相互影响, 因此在事务处理的绝大部分时间里不需要进行加锁处理, 只在更新请求提交前, 检查当前事务在读取数据后的这段时间内, 是否由其他事务对该数据进行了修改, 如果没有修改, 则提交, 如果有修改则回滚. 乐观锁适合使用在数据并发竞争不大, 事务冲突较少的应用场景中.

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
| SyncConnected(3): 此时客户端和服务器处于连接状态 | None(-1) | 客户端与服务器成功建立会话 |
|  | NodeCreated(1) | Watcher 监听的对应数据节点被创建 |
|  | NodeDeleted(2) | Watcher 监听的对应数据节点被删除 |
|  | NodeDataChanged(3) | Watcher 监听的对应数据节点的数据内容发生变化 |
|  | NodeChildrenChanged(4) | Watcher 监听的对应数据节点的子节点列表发生变化 |
| DisConnected(0): 此时客户端和服务器处于断开连接状态 | None(-1) | 客户端与 ZooKeeper 服务端断开连接 |
| Expired(-112): 此时客户端会话失效, 通常同时也会收到 SessionExpiredException 异常 | None(-1) | 会话超时 |
| AuthFailed(4): 授权失败, 通常也会收到 AuthFailedException 异常 | None(-1) | 使用错误的 scheme 进行权限检查 或 SASL 权限检查失败 |

由于 Watcher 机制细节较为复杂, 详情请参考 `[从 Paxos 到 ZooKeeper] - 7.1.4 Watcher - 数据变更的通知`.

## 1.5 ACL - 保障数据的安全

`UGO(User, Group, Others)`: 应用最广泛的权限控制方式, 广泛应用于 Unix/Linux 系统中.

`ACL(Access Control List)`: 访问控制列表, 是一种更细粒度的权限管理方式, 可以针对任意用户和组进行细粒度的权限控制. 目前大部分 Unix 系统已经支持, Linux 也从 2.6 版本的内核开始支持.

### 1.5.1 ACL 介绍

ZooKeeper 的 ACL 权限控制和 Unix/Linux 操作系统的 ACL 有一些区别. ACL 具有以下三个概念:

> __权限模式(Scheme)__

权限模式是权限校验使用的策略. 分为以下四种模式:
- `IP`: 通过 IP 地址细粒度控制. 如 `ip:192.168.0.110` 表示权限控制针对该 IP.`ip:192.168.0.1/24` 表示权限控制针对 "192.168.0.*" IP 段.
- `Digest`: 针对不同应用进行权限控制. 用 `username:password` 表示, 其中 ZooKeeper 会对 username:password 进行 SHA-1 算法加密和 BASE64 编码, 最后 username:password 被混淆为一个无法辨识的字符串.
- `World`: 对所有用户开放. 是一种特殊的 Digest 模式, 使用 "world:anyone" 表示.
- `Super`: 超级用户控制. 是一种特殊的 Digest 模式.

> __授权对象(ID)__

授权对象为权限模式下对应的实体, 以下为对应关系:

| 权限模式 | 授权对象 |
| :--- | :--- |
| IP | IP 地址或 IP 段, 如 `192.168.0.110` 或 `192.168.0.1/24` |
| Digest | `username:BASE64(SHA-1(username:password))`, 如 `foo:kWN6aNSbjcKWPqjiV7cg0N24raU=` |
| Word | 只有一个 ID: `anyone` |
| Super | 与 Digest 模式一致 |

> __权限(Permission)__

ZooKeeper 对数据的操作权限分为以下五类:
- `CREATE(C)`: 数据节点的创建权限, 允许授权对象在该数据节点下创建子节点.
- `DELETE(D)`: 子节点的删除权限, 允许授权对象删除该数据节点的子节点.
- `READ(R)`: 数据节点的读取权限, 允许授权对象访问该数据节点并读取其数据内容或子节点列表等.
- `WRITE(W)`: 数据节点的更新权限, 允许授权对象对该数据节点进行更新.
- `ADMIN(A)`: 数据节点的管理权限, 允许授权对象对该数据节点进行 ACL 设置.

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

Jute 组件会使用不同的代码生成器来生成实际编程语言(Java / C / C++)的文件, 如 Java 使用 JavaGenerator 来生成类文件(都会实现 Record 接口), 存放在 `src/java/generated` 目录.

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
- `type`: 代表请求类型, 有 20 种(详情查看 `org.apache.zookeeper.ZooDefs.OpCode`), 以下为部分示例:
  - `OpCode.create(1)`: 创建节点
  - `OpCode.delete(2)`: 删除节点
  - `OpCode.exists(3)`: 节点是否存在
  - `OpCode.getData(4)`: 获取节点数据
  - `OpCode.setData(5)`: 设置节点数据

> __请求体: Request__

不同的 type 请求类型, 请求体的结构是不同的, 以下为部分示例:

- ConnectRequest: 会话创建

ZooKeeper 客户端和服务端创建会话时, 会发送 ConnectRequest 请求, 包含 protocolVersion(版本号), lastZxidSeen(最近一次接收到的服务器 ZXID lastZxidSeen), timeOut(会话超时时间), sessionId(会话标示), passwd(会话密码), 其数据结构在 `src/zookeeper.jute` 中定义如下:
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

ZooKeeper 客户端在向服务器发送更新节点数据请求时, 会发送 SetDataRequest 请求, 包含 path(节点路径), data(数据内容),version(期望版本号), 其数据结构在 `src/zookeeper.jute` 中如下:
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
| 00,00,00,04 | 8~11 位是 type, 代表客户端请求类型 | 4(代表 OpCode.getData) |
| 00,00,00,10 | 12~15 位是 len, 代表节点路径的长度 | 16(代表节点路径长度转换成十六进制是 16位) |
| 2f,24,37,5f,32,5f,34,2f,67,65,74,5f,64,61,74,61 | 16~31 位是 path, 代表节点路径 | /$7_2_4/get_data(通过比对 ASCII 码表转换成十进制即可) |
| 01 | 32 位是 watch, 代表是否注册 Watcher | 1(代表注册 Watcher) |

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
- `err`: 错误码, 有 22 种(详情查看 `org.apache.zookeeper.KeeperException.Code`), 以下为部分示例:
  - `Code.OK(0)`: 处理成功
  - `Code.NONODE(101)`: 节点不存在
  - `Code.NOAUTH(102)`: 没有权限

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

针对 ZooKeeper 客户端的获取节点数据请求, ZooKeeper 服务端会返回 GetDataResponse 响应, 包含 data(数据内容), stat(节点状态), 其数据结构在 `src/zookeeper.jute` 中如下:
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

针对 ZooKeeper 客户端的更新节点数据请求, ZooKeeper 服务端会返回 SetDataResponse 响应, 包含 stat(节点状态), 其数据结构在 `src/zookeeper.jute` 中如下:
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
| 00,00,00,05 | 4~7 位是 xid, 代表客户端请求的发起序号 | 5(代表本次请求是客户端会话创建后的第 5 次请求发送) |
| 00,00,00,00,00,00,00,04 | 8~15 位是 zxid, 代表当前服务端处理过第最新的 ZXID 值 | 4 |
| 00,00,00,00 | 16~19 位是 err, 代表错误码 | 0(代表 Code.OK) |
| 00,00,00,0b | 20~23 位是 len, 代表节点数据内容的长度 | 11(代表接下去的 11 位是数据内容的字节数组) |
| 69,27,6b,5f,63,6f,6e,74,65,6e,74 | 24~34 位是 data, 代表节点的数据内容 | i'm_content |
| 00,00,00,00,00,00,00,04 | 35~42 位是 czxid, 代表创建该数据节点时的 ZXID | 4 |
| 00,00,00,00,00,00,00,04 | 43~50 位是 mzxid, 代表最后一次修改该数据节点时的 ZXID | 4 |
| 00,00,01,43,67,bd,0e,08 | 51~58 位是 ctime, 代表数据节点的创建时间 | 1389014879752(即: 2014-01-06 21:27:59) |
| 00,00,01,43,67,bd,0e,08 | 59~66 位是 mtime, 代表数据节点最后一次变更的时间 | 1389014879752(即: 2014-01-06 21:27:59) |
| 00,00,00,00 | 67~70 位是 version, 代表数据节点的内容的版本号 | 0 |
| 00,00,00,00 | 71~74 位是 cversion, 代表数据节点的子节点的版本号 | 0 |
| 00,00,00,00 | 75~78 位是 aversion, 代表数据节点的 ACL 变更版本号 | 0 |
| 00,00,00,00,00,00,00,00 | 79~86 位是 ephemeralOwner, 如果该数据节点是临时节点, 那么就记录创建该临时节点的会话 ID, 如果是持久节点, 则为 0 | 0(代表该节点是持久节点) |
| 00,00,00,0b | 87~90 位是 dataLength, 代表数据节点的数据内容长度 | 11 |
| 00,00,00,00 | 91~94 位是 numChildren, 代表数据节点的子节点个数 | 0 |
| 00,00,00,00,00,00,00,04 | 95~102 位是 pzxid, 代表最后一次对子节点列表变更的 PZXID | 4 |

---

# 三 客户端

ZooKeeper 客户端主要由以下几个核心组件组成:
- `ZooKeeper 实例`: 客户端的入口.
- `ClientWatchManager`: 客户端 Watcher 管理器.
- `HostProvider`: 客户端地址列表管理器.
- `ClientCnxn`: 客户端核心线程, 包括 `SendThread`(I/O 线程, 负责客户端和服务端之间的网络 I/O 通信) 和 `EventThread`(事件线程, 负责处理服务端事件) 两个线程.

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

ZooKeeper 客户端会创建一个网络连接器 ClientCnxn, 用来管理客户端和服务端的网络交互. 客户端还会初始化两个核心队列 outgoingQueue(客户端的请求发送队列) 和 pendingQueue(服务端的响应等待队列). 另外, 客户端还会创建 ClientCnxnSocket(ClientCnxn 的底层网络 I/O 处理器).

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

连接成功后, 一方面通知 SendThread 线程, 进一步对客户端进行会话参数(readTimeout 和 connectTimeout 等)设置并更新客户端状态.另一方面, 将当前成功连接的服务器地址通知给 HostProvider 地址管理器.

> 14. __生成事件 SyncConnected-None__

为了让上层应用感知到会话已创建成功, SendThread 会生成一个 SyncConnected-None 事件, 将该事件传递给 EventThread 线程.

> 15. __查询 Watcher__

EventThread 线程接收到该事件后, 从 ClientWatchManager 管理器中查询出对应的 Watcher(针对 SyncConnected-None 事件, 对应默认 Watcher), 然后将其放到 EventThread 的 waitingEvents 队列中.

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

EventThread 内部有一个 waitingEvents 队列, 临时存放需要被触发的 Object(包括客户端注册的 Watcher 和 异步接口中注册的回调器 AsyncCallback). EventThread 会不断地从 waitingEvents 队列中取出 Object, 识别出具体类型(Watcher 或 AsyncCallback), 并分别调用 process 和 processResult 接口方法实现对事件的触发和回调.

### 3.3.5 ClientCnxnSocket

ClientCnxnSocket 定义了底层 Socket 通信的接口.

可通过在 zookeeper.clientCnxnSocket 系统变量中配置 ClientCnxnSocket 实现类的全类名, 如 `-Dzookeeper.clientCnxnSocket=org.apache.zookeeper.ClientCnxnSocketNIO`. 默认实现是使用 Java 原生 NIO 接口的 `ClientCnxnSocketNIO`.

> __请求发送__

从 outgoingQueue 队列中提取出一个可发送的 Packet 对象, 同时生成一个客户端请求序号 XID 并将其设置到 Packet 对象中, 然后将其序列化后进行发送. 请求发送完毕后, 立即将该 Packet 保存到 pendingQueue 队列中, 等待服务端响应后进行处理.

> __响应接收__

客户端收到来自服务端的响应后, 根据不同客户端请求类型, 进行不同处理, 如下:
- 如果当前客户端尚未进行初始化, 说明当前客户端和服务端之间正在进行会话创建, 那么就将收到的 ByteBuffer 序列化成 ConnectResponse 对象.
- 如果当前客户端处于正常会话周期, 且收到的服务端响应是一个事件, 那么客户端会将收到的 ByteBuffer 序列化成 WatcherEvent 对象, 并将该事件放入待处理队列中.
- 如果是一个常规的请求响应(Create, GetData, Exist 等), 那么会从 pendingQueue 队列中取出一个 Packet 来进行处理. 客户端首先会检测服务端响应中包含的 XID 值来确保请求处理的顺序性, 然后将收到的 ByteBuffer 序列化成相应的 Response 对象.

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

`SessionTracker`(ZooKeeper 服务端会话管理器) 初始化时, 会调用 initializeNextSession() 方法来生成一个初始化 sessionID, 后续会在该 sessionID 基础上为每个会话进行分配.

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

- `CurrentTime`: 当前时间(毫秒).
- `SessionTimeout`: 会话设置的超时时间(毫秒).
- `ExpirationInterval`: Leader 服务器在运行期间定时进行会话超时检查的时间间隔(毫秒), 默认值是 tickTime 值(2000).

假设当前时间毫秒表示为 1370907000000, 客户端会话超时时间为 15000 毫秒, 服务器设置的 tickTime 为 2000 毫秒(即 ExpirationInterval 也为 2000 毫秒), 那么计算出的 ExpirationTime 值如下:
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

一旦某个会话失效, 那么和该会话相关的临时节点(EPHEMERAL)都需要被一并清除.

因为每个会话都单独保存了一份由该会话维护的所有临时节点集合在 ZooKeeper 内存数据库中, 因此清理时, 只需要根据 sessionID 就可以拿到这份临时节点列表.

- d. 添加 "节点删除" 事务变更

收集到所有临时节点后, ZooKeeper 会将这些临时节点逐个转换成 "节点删除" 请求, 并放入事务变更队列 outstandingChanges 中.

- e. 删除临时节点

FinalRequestProcessor 处理器会触发内存数据库, 删除该会话对应的所有临时节点.

- f. 移除会话

节点删除后, 需要将会话从 SessionTracker 内部(即 sessionsById, sessionsWithTimeout, sessionsSets) 移除.

- g. 关闭 NIOServerCnxn

最后, 从 NIOServerCnxnFactory 中找到该会话对应的 NIOServerCnxn, 将其关闭.

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
