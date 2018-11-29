# Document & Code

* [../Zookeeper-video](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-video.md)

---

# ZooKeeper 常用命令行操作

通过 ./zkCli.sh 打开客户端连接服务端。

## ls 命令

`ls`: 查看节点
```
[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper]
[zk: localhost:2181(CONNECTED) 1] ls /zookeeper
[quota]
[zk: localhost:2181(CONNECTED) 2] ls /zookeeper/quota
[]
```

## ls2 和 stat 命令

`ls2`:
```
[zk: localhost:2181(CONNECTED) 3] ls2 /
[zookeeper]
cZxid = 0x0
ctime = Thu Jan 01 08:00:00 CST 1970
mZxid = 0x0
mtime = Thu Jan 01 08:00:00 CST 1970
pZxid = 0x0
cversion = -1
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 0
numChildren = 1
```

`stat`:
```
[zk: localhost:2181(CONNECTED) 6] stat /
cZxid = 0x0
ctime = Thu Jan 01 08:00:00 CST 1970
mZxid = 0x0
mtime = Thu Jan 01 08:00:00 CST 1970
pZxid = 0x0
cversion = -1
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 0
numChildren = 1
```

## get 命令

`get`: 当前节点的数据。
* cZxid: 为节点分配的 id。
* ctime: 创建时间。
* mZxid: 修改后的节点分配的 id。
* mtime: 修改时间。
* pZxid: 子节点 id。
* cversion: 子节点 version。
* dataVersion: 当前节点数据版本号，修改后会累加。
* aclVersion: acl 权限版本，权限变化后会累加。
* ephemeralOwner: 
* dataLength: 数据长度。
* numChildren: 子节点数。
```
[zk: localhost:2181(CONNECTED) 7] get /

cZxid = 0x0
ctime = Thu Jan 01 08:00:00 CST 1970
mZxid = 0x0
mtime = Thu Jan 01 08:00:00 CST 1970
pZxid = 0x0
cversion = -1
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 0
numChildren = 1
```

# session 的基本原理与 create 命令的使用

## sesion 基本原理

* 客户端与服务端之间的连接称为会话。
* 每个会话可以设置一个超时时间。
* 心跳结束，session 则过期。
* 心跳机制：客户端向服务端的 ping 包请求。
* session 过期，临时节点 ZNode 则会被抛弃。

## create 命令

### 默认创建节点

非顺序，持久化。
```
[zk: localhost:2181(CONNECTED) 10] create /zozo zozo-data
Created /zozo
[zk: localhost:2181(CONNECTED) 11] get /zozo
zozo-data
cZxid = 0x4
ctime = Wed Nov 28 20:30:38 CST 2018
mZxid = 0x4
mtime = Wed Nov 28 20:30:38 CST 2018
pZxid = 0x4
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 9
numChildren = 0
```

### 创建临时节点

`ephemeralOwner` 可用于判断是否临时节点。

```
[zk: localhost:2181(CONNECTED) 12] create -e /zozo/tmp zozo-data
Created /zozo/tmp
[zk: localhost:2181(CONNECTED) 13] get /zozo
zozo-data
cZxid = 0x4
ctime = Wed Nov 28 20:30:38 CST 2018
mZxid = 0x4
mtime = Wed Nov 28 20:30:38 CST 2018
pZxid = 0x5
cversion = 1
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 9
numChildren = 1
[zk: localhost:2181(CONNECTED) 14] get /zozo/tmp
zozo-data
cZxid = 0x5
ctime = Wed Nov 28 20:34:14 CST 2018
mZxid = 0x5
mtime = Wed Nov 28 20:34:14 CST 2018
pZxid = 0x5
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x1051d69c7a60000
dataLength = 9
numChildren = 0
```

退出客户端后，且超过 session 超时时间，则 tmp 被删除。

```
[zk: localhost:2181(CONNECTED) 0] ls /zozo
[tmp]
[zk: localhost:2181(CONNECTED) 1] ls /zozo
[]
```

### 创建顺序节点

```
[zk: localhost:2181(CONNECTED) 2] create -s /zozo/sec seq
Created /zozo/sec0000000001
[zk: localhost:2181(CONNECTED) 3] create -s /zozo/sec seq
Created /zozo/sec0000000002
[zk: localhost:2181(CONNECTED) 4] create -s /zozo/sec seq
Created /zozo/sec0000000003
```

# set与delete命令的使用

## set 命令

`set /zozo new-data` 操作后，dataVersion 会加 1.

```
[zk: localhost:2181(CONNECTED) 5] get /zozo
zozo-data
cZxid = 0x4
ctime = Wed Nov 28 20:30:38 CST 2018
mZxid = 0x4
mtime = Wed Nov 28 20:30:38 CST 2018
pZxid = 0xa
cversion = 5
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 9
numChildren = 3
[zk: localhost:2181(CONNECTED) 6] set /zozo new-data
cZxid = 0x4
ctime = Wed Nov 28 20:30:38 CST 2018
mZxid = 0xb
mtime = Wed Nov 28 20:41:14 CST 2018
pZxid = 0xa
cversion = 5
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 8
numChildren = 3
[zk: localhost:2181(CONNECTED) 7] get /zozo
new-data
cZxid = 0x4
ctime = Wed Nov 28 20:30:38 CST 2018
mZxid = 0xb
mtime = Wed Nov 28 20:41:14 CST 2018
pZxid = 0xa
cversion = 5
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 8
numChildren = 3
```

`set /zozo data 1` 操作，指定了版本号 1，如果在高并发操作下，如果指定版本号已经过期的命令，则会操作失败。（乐观锁）

```
[zk: localhost:2181(CONNECTED) 8] set /zozo data 1
cZxid = 0x4
ctime = Wed Nov 28 20:30:38 CST 2018
mZxid = 0xc
mtime = Wed Nov 28 20:44:10 CST 2018
pZxid = 0xa
cversion = 5
dataVersion = 2
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 4
numChildren = 3
[zk: localhost:2181(CONNECTED) 9] set /zozo data 1
version No is not valid : /zozo
```

## delete 命令

`delete /zozo/sec0000000001` 操作后，会直接删除.

```
[zk: localhost:2181(CONNECTED) 11] ls /zozo
[sec0000000003, sec0000000001, sec0000000002]
[zk: localhost:2181(CONNECTED) 13] delete /zozo/sec0000000001
```

`delete /zozo/sec0000000002 0`操作，指定了版本号 0，如果在高并发操作下，如果指定版本号已经过期的命令，则会操作失败（乐观锁）。

`[zk: localhost:2181(CONNECTED) 22] delete /zozo/sec0000000002 1`操作，指定为当前版本号 1，才能操作成功。

```
[zk: localhost:2181(CONNECTED) 17] get /zozo/sec0000000002
123
cZxid = 0x9
ctime = Wed Nov 28 20:38:40 CST 2018
mZxid = 0xf
mtime = Wed Nov 28 20:48:34 CST 2018
pZxid = 0x9
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 3
numChildren = 0
[zk: localhost:2181(CONNECTED) 18] delete /zozo/sec0000000002 0
version No is not valid : /zozo/sec0000000002
[zk: localhost:2181(CONNECTED) 22] delete /zozo/sec0000000002 1
```

# 理解 watcher 机制

* 每个节点都有一个 watcher，当监控的某个对象（ZNode）发生变化，则触发事件。
* ZooKeeper 的 watcher 是一次性的，触发后立即销毁。
* 父节点，子节点增删改都能触发 watcher，可分为（子）节点创建事件、（子）节点删除事件、（子）节点数据变化事件。

# 父节点 watcher 事件

可使用 `stat path [watch]` 或 `ls path [watch]` 或 `ls2 path [watch]` 或 `get path [watch]` 命令设置 watcher 事件。

## 创建父节点触发

创建父节点时触发 `NodeCreated` 事件，注意：watcher 为一次性事件。

```
[zk: localhost:2181(CONNECTED) 23] ls /
[zozo, zookeeper]
[zk: localhost:2181(CONNECTED) 24] stat /zoo watch
Node does not exist: /zoo
[zk: localhost:2181(CONNECTED) 25] create /zoo 123

WATCHER::

WatchedEvent state:SyncConnected type:NodeCreated path:/zoo
Created /zoo
```

## 修改父节点数据触发

修改父节点时触发 `NodeDataChanged` 事件，注意：watcher 为一次性事件，需要重新设置。

```
[zk: localhost:2181(CONNECTED) 26] get /zoo watch
123
cZxid = 0x12
ctime = Wed Nov 28 21:15:48 CST 2018
mZxid = 0x12
mtime = Wed Nov 28 21:15:48 CST 2018
pZxid = 0x12
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 3
numChildren = 0
[zk: localhost:2181(CONNECTED) 27] set /zoo 456

WATCHER::

WatchedEvent state:SyncConnected type:NodeDataChanged path:/zoo
cZxid = 0x12
ctime = Wed Nov 28 21:15:48 CST 2018
mZxid = 0x13
mtime = Wed Nov 28 21:18:31 CST 2018
pZxid = 0x12
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 3
numChildren = 0
```

## 删除父节点事件

删除父节点时触发 `NodeDeleted` 事件，注意：watcher 为一次性事件，需要重新设置。

```
[zk: localhost:2181(CONNECTED) 28] get /zoo watch
456
cZxid = 0x12
ctime = Wed Nov 28 21:15:48 CST 2018
mZxid = 0x13
mtime = Wed Nov 28 21:18:31 CST 2018
pZxid = 0x12
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 3
numChildren = 0
[zk: localhost:2181(CONNECTED) 29] delete /zoo

WATCHER::

WatchedEvent state:SyncConnected type:NodeDeleted path:/zoo
```

# 子节点 watcher 事件

## 创建子节点触发父节点事件

`ls /zzoo watch` 通过父节点设置子节点 watcher，当创建子节点时，触发 `NodeChildrenChanged` 事件。

```
[zk: localhost:2181(CONNECTED) 42] ls /
[zzoo, zozo, zookeeper]
[zk: localhost:2181(CONNECTED) 43] ls /zzoo watch
[]
[zk: localhost:2181(CONNECTED) 44] create /zzoo/abc 11

WATCHER::

WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/zzoo
Created /zzoo/abc
```

## 删除子节点触发父节点事件

`ls /zzoo watch` 通过父节点设置子节点 watcher，当删除子节点时，触发 `NodeChildrenChanged` 事件。

```
[zk: localhost:2181(CONNECTED) 48] ls /zzoo watch
[abc]
[zk: localhost:2181(CONNECTED) 49] delete /zzoo/abc

WATCHER::

WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/zzoo
```

## 修改子节点不触发父节点事件

`ls /zzoo watch` 通过父节点设置子节点 watcher，当修改子节点时，不会触发任何事件。

```
[zk: localhost:2181(CONNECTED) 53] ls /zzoo watch
[abc]
[zk: localhost:2181(CONNECTED) 54] set /zzoo/abc 22
cZxid = 0x1a
ctime = Wed Nov 28 21:36:00 CST 2018
mZxid = 0x1b
mtime = Wed Nov 28 21:36:13 CST 2018
pZxid = 0x1a
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 2
numChildren = 0
```

# watcher 常用使用场景

统一资源配置：ZooKeeper 集群维护一份配置文件，并在集群内保持同步。客户端在连接集群中任意节点时，对该配置文件进行 watch，如果该配置发生变化，会在集群内相互同步，同时客户端会监控到文件变化，就可以进行相应处理。

# ACL 权限详解

ACL(access control lists): 权限控制列表

## ACL 命令行

* getAcl: 获取某个节点的 ACL 权限信息。
* secAcl: 设置某个节点的 ACL 权限。
* addAuth: 输入认证授权信息，注册时输入明文密码登录，密码在 ZooKeep 内部是加密存储。

相关命令包括：`setAcl path acl`, `addauth scheme auth`, `getAcl path`。

# ACL 的构成

ACL 权限格式为：`scheme:id:permissions`：
* scheme: 采用哪种权限机制
* id: 允许访问的用户
* permissioins: 权限组合

## ACL 的构成: scheme

* world: world 下只有一个 id，即 anyone。格式：`world:anyone:[permissions]`。
* auth: 认证登录，需要有权限的注册用户（明文注册，明文登录），格式：`auth:user:password:[permissions]`。
* digest: 认证登录，需要有权限的注册用户（密文注册，明文登录），格式：`digest:user:BASE64(SHA1(password)):[permission]`。
* ip: 限制 ip 进行访问，格式：`ip:ip:[permissions]`。例如：`ip:192.168.1.1:[permissions]`。
* super: 代表超级管理员，需要在 `./bin/zkServer.sh` 中配置 super user。

## ACL 的构成: permissions

权限字符串缩写：`crdwa`：
* CREATE: 创建子节点
* READ: 获取节点/子节点
* DELETE: 删除子节点
* WRITE: 设置节点数据
* ADMIN: 设置权限

# ACL 命令行 world 讲解

`world:anyone:cdrwa`：该权限为创建节点后的默认权限。表示所有人都可以对节点进行任何操作。

```
[zk: localhost:2181(CONNECTED) 2] create /acc 111
Created /acc
[zk: localhost:2181(CONNECTED) 4] getAcl /acc
'world,'anyone
: cdrwa
```

通过 `setAcl /acc world:anyone:crwa` 设置所有人对 `/acc` 的子节点具有创建、读取、设置节点数据、设置权限。但是没有删除子节点权限。

```
[zk: localhost:2181(CONNECTED) 5] setAcl /acc world:anyone:crwa
cZxid = 0x21
ctime = Thu Nov 29 12:30:44 CST 2018
mZxid = 0x21
mtime = Thu Nov 29 12:30:44 CST 2018
pZxid = 0x21
cversion = 0
dataVersion = 0
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 3
numChildren = 0
[zk: localhost:2181(CONNECTED) 6] getAcl /acc   
'world,'anyone
: crwa
[zk: localhost:2181(CONNECTED) 7] create /acc/abc 123
Created /acc/abc
[zk: localhost:2181(CONNECTED) 8] delete /acc/abc
Authentication is not valid : /acc/abc
```

# ACL 命令行 auth 讲解

使用 `setAcl /acc auth:zz:password:cdrwa` 使得 zz 用户具有权限。

注意：在设置前需要通过 `addauth digest zz:password` 注册该用户。设置的 `password` 密码在内部以 `X9c92HzVP35xlknPPYEgZobCNWk=` 密文方式存储。

```
[zk: localhost:2181(CONNECTED) 3] getAcl /acc
'world,'anyone
: cdrwa
[zk: localhost:2181(CONNECTED) 4] setAcl /acc auth:zz:password:cdrwa
Acl is not valid : /acc
[zk: localhost:2181(CONNECTED) 5] addauth digest zz:password
[zk: localhost:2181(CONNECTED) 6] setAcl /acc auth:zz:password:cdrwa
cZxid = 0x21
ctime = Thu Nov 29 12:30:44 CST 2018
mZxid = 0x21
mtime = Thu Nov 29 12:30:44 CST 2018
pZxid = 0x23
cversion = 1
dataVersion = 0
aclVersion = 3
ephemeralOwner = 0x0
dataLength = 3
numChildren = 1
[zk: localhost:2181(CONNECTED) 7] getAcl /acc
'digest,'zz:X9c92HzVP35xlknPPYEgZobCNWk=
: cdrwa
```

# ACL 命令行 digest

出现演示与实际操作结果不符的情况。待定。

# ACL 命令行 ip



# ACL 之 super 超级管理员

1. 修改 zkServer.sh 增加 super 用户：
```
140     nohup "$JAVA" "-Dzookeeper.log.dir=${ZOO_LOG_DIR}" "-Dzookeeper.root.log    ger=${ZOO_LOG4J_PROP}" \  （修改前）

140     nohup "$JAVA" "-Dzookeeper.log.dir=${ZOO_LOG_DIR}" "-Dzookeeper.root.log    ger=${ZOO_LOG4J_PROP}" "-Dzookeeper.DigestAuthenticationProvider.superDigest=zozo:X9c92HzVP35xlknPPYEgZobCNWk=" \  （修改后）
```

2. 然后重启 server。

```
./zkServer.sh restart
```

# ACL 的常用使用场景

1. 开发/测试环境分离，不同用户账号权限分离。
2. 控制 ip 访问服务。

# ZooKeeper 四字命令 Four Letter Words

通过四字命令简单命令来和服务器交互。

需要先安装 yum install nc.

命令格式为：`echo [command] | nc [ip] [port]` ，所有命令请参考：[ZooKeeper Administrator's Guide](http://zookeeper.apache.org/doc/r3.4.13/zookeeperAdmin.html#sc_zkCommands)

以下为部分常用命令：
* stat: 查看 ZooKeeper 状态信息，是否 mode。
* ruok: 查看 ZooKeeper 是否启动，如果启动则返回 imok。
* dump: 列出未经处理的会话和临时节点。
* conf: 查看服务器配置。
* cons: 展示连接到服务器的客户端信息。
* envi: 环境变量。
* mntr: 监控 ZooKeeper 健康信息。
* wchs: 展示 watch 信息。
* wchc 与 wchp: session 与 watch 及 path 与 watch 信息。

其中，部分命令需要权限（如 wchc 与 wchp），可在 zoo.cfg 中添加 `4lw.commands.whitelist=*` 配置来允许所有命令执行。也可通过 `4lw.commands.whitelist=stat, ruok, conf, isro` 配置来允许指定命令运行。


