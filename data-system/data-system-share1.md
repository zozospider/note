
# 大纲

`ZooKeeper 原理与实践`

- 事务
  - ACID
  - 弱隔离级别
  - 串行化
- 复制
  - 主节点与从节点
  - 复制滞后问题
- 分布式系统的挑战
  - 不可靠的网络
  - 不可靠的时钟
- 一致性与共识
  - 顺序和一致性 (可线性化)
  - 两阶段提交
  - 全序关系广播
  - 成员与协调服务 (ZooKeeper)
- ZooKeeper
  - ZooKeeper 概念
    - 事务日志 & 快照
    - 角色
    - 会话
  - ZooKeeper 应用
    - 开源软件
    - Demo
    - Configurator

# 总体进度

从单节点事务开始讲述普通的数据系统满足的 ACID 特性
- 1. 并讲述其中的隔离级别
  - 脏读, 脏写 -> 读 - 提交
  - 读倾斜 -> 可重复读
  - 写倾斜 -> 防止更新丢失
- 2. 串行化
  - 存储过程
  - 两阶段加锁
  - 悲观与乐观的并发控制

然后引入多节点系统
- 1. 介绍主要的分类:
  - 多主, 无主, 主从 (脑裂), 分区, 复制
  - 分布式: 高性能, 云计算
- 2. 说明其中的挑战:
  - 网络
  - 时钟
  - 拜占庭式故障
- 3. 说明需求:
  - 顺序概念和一致性保证 (可线性化)
  - 原子事务提交
  - 锁与租约 / 唯一性约束
  - 成员 / 协调服务
- 4. 并表示需要一个理论系统模型与现实, 从而引出共识算法解决所有需求.
- 5. 然后引入 2PC 解决共识问题并说明缺点.
- 6. 然后引出全序关系广播解决共识问题. (Raft, Zab, Paxos)
- 7. 并进而引出 ZOoKeeper 通过全序关系广播解决共识问题的实践. (线性化的原子操作)
- 9. 介绍 ZooKeeper
- 10. ZooKeeper 架构和特性:
  - 数据结构, cversion, zxid, ACL > 操作全序
  - Watcher > 故障检测, 更改通知
  - 角色 (Leader, Follower, Observer), 会话, Leader 选举
  - 事务日志 & 快照
- 11. ZooKeeper 应用
  - 开源软件
  - Demo
  - Configurator


- machine 1

```
[zozo@VM_0_17_centos bin]$ ll
总用量 2492
-rwxr-xr-x 1 zozo zozo     232 6月  30 2018 README.txt
-rwxr-xr-x 1 zozo zozo    1937 6月  30 2018 zkCleanup.sh
-rwxr-xr-x 1 zozo zozo    1056 6月  30 2018 zkCli.cmd
-rwxr-xr-x 1 zozo zozo    1534 6月  30 2018 zkCli.sh
-rwxr-xr-x 1 zozo zozo    1759 6月  30 2018 zkEnv.cmd
-rwxr-xr-x 1 zozo zozo    2696 6月  30 2018 zkEnv.sh
-rwxr-xr-x 1 zozo zozo    1089 6月  30 2018 zkServer.cmd
-rwxr-xr-x 1 zozo zozo    6773 6月  30 2018 zkServer.sh
-rwxr-xr-x 1 zozo zozo     996 6月  30 2018 zkTxnLogToolkit.cmd
-rwxr-xr-x 1 zozo zozo    1385 6月  30 2018 zkTxnLogToolkit.sh
-rw-rw-r-- 1 zozo zozo 2498687 5月   9 20:08 zookeeper.out
[zozo@VM_0_17_centos bin]$ pwd
/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin
[zozo@VM_0_17_centos bin]$ ./zkCli.sh
Connecting to localhost:2181
2019-05-09 20:10:28,390 [myid:] - INFO  [main:Environment@100] - Client environment:zookeeper.version=3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03, built on 06/29/2018 04:05 GMT
2019-05-09 20:10:28,394 [myid:] - INFO  [main:Environment@100] - Client environment:host.name=VM_0_17_centos
2019-05-09 20:10:28,394 [myid:] - INFO  [main:Environment@100] - Client environment:java.version=1.8.0_192
2019-05-09 20:10:28,396 [myid:] - INFO  [main:Environment@100] - Client environment:java.vendor=Oracle Corporation
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:java.home=/home/zozo/app/java/jdk1.8.0_192/jre
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:java.class.path=/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../build/classes:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../build/lib/*.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/slf4j-log4j12-1.7.25.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/slf4j-api-1.7.25.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/netty-3.10.6.Final.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/log4j-1.2.17.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/jline-0.9.94.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/audience-annotations-0.5.0.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../zookeeper-3.4.13.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../src/java/lib/*.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../conf:.:/home/zozo/app/java/jdk1.8.0_192/lib/dt.jar:/home/zozo/app/java/jdk1.8.0_192/lib/tools.jar
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:java.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:java.io.tmpdir=/tmp
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:java.compiler=<NA>
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:os.name=Linux
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:os.arch=amd64
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:os.version=3.10.0-514.21.1.el7.x86_64
2019-05-09 20:10:28,398 [myid:] - INFO  [main:Environment@100] - Client environment:user.name=zozo
2019-05-09 20:10:28,398 [myid:] - INFO  [main:Environment@100] - Client environment:user.home=/home/zozo
2019-05-09 20:10:28,398 [myid:] - INFO  [main:Environment@100] - Client environment:user.dir=/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin
2019-05-09 20:10:28,399 [myid:] - INFO  [main:ZooKeeper@442] - Initiating client connection, connectString=localhost:2181 sessionTimeout=30000 watcher=org.apache.zookeeper.ZooKeeperMain$MyWatcher@277050dc
Welcome to ZooKeeper!
2019-05-09 20:10:28,427 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1029] - Opening socket connection to server localhost/127.0.0.1:2181. Will not attempt to authenticate using SASL (unknown error)
JLine support is enabled
2019-05-09 20:10:28,513 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@879] - Socket connection established to localhost/127.0.0.1:2181, initiating session
2019-05-09 20:10:28,525 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1303] - Session establishment complete on server localhost/127.0.0.1:2181, sessionid = 0x2042db0dea60035, negotiated timeout = 30000

WATCHER::

WatchedEvent state:SyncConnected type:None path:null
[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper]
[zk: localhost:2181(CONNECTED) 1] create -e /master "master1.example.com:2223"
Created /master
[zk: localhost:2181(CONNECTED) 2] ls /
[zookeeper, master]
[zk: localhost:2181(CONNECTED) 3] get /master
master1.example.com:2223
cZxid = 0x1000005e1
ctime = Thu May 09 20:11:06 CST 2019
mZxid = 0x1000005e1
mtime = Thu May 09 20:11:06 CST 2019
pZxid = 0x1000005e1
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x2042db0dea60035
dataLength = 24
numChildren = 0
[zk: localhost:2181(CONNECTED) 4] [zozo@VM_0_17_centos bin]$ ./zkCli.sh
Connecting to localhost:2181
2019-05-09 20:17:03,575 [myid:] - INFO  [main:Environment@100] - Client environment:zookeeper.version=3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03, built on 06/29/2018 04:05 GMT
2019-05-09 20:17:03,579 [myid:] - INFO  [main:Environment@100] - Client environment:host.name=VM_0_17_centos
2019-05-09 20:17:03,579 [myid:] - INFO  [main:Environment@100] - Client environment:java.version=1.8.0_192
2019-05-09 20:17:03,582 [myid:] - INFO  [main:Environment@100] - Client environment:java.vendor=Oracle Corporation
2019-05-09 20:17:03,582 [myid:] - INFO  [main:Environment@100] - Client environment:java.home=/home/zozo/app/java/jdk1.8.0_192/jre
2019-05-09 20:17:03,582 [myid:] - INFO  [main:Environment@100] - Client environment:java.class.path=/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../build/classes:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../build/lib/*.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/slf4j-log4j12-1.7.25.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/slf4j-api-1.7.25.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/netty-3.10.6.Final.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/log4j-1.2.17.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/jline-0.9.94.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/audience-annotations-0.5.0.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../zookeeper-3.4.13.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../src/java/lib/*.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../conf:.:/home/zozo/app/java/jdk1.8.0_192/lib/dt.jar:/home/zozo/app/java/jdk1.8.0_192/lib/tools.jar
2019-05-09 20:17:03,582 [myid:] - INFO  [main:Environment@100] - Client environment:java.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:java.io.tmpdir=/tmp
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:java.compiler=<NA>
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:os.name=Linux
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:os.arch=amd64
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:os.version=3.10.0-514.21.1.el7.x86_64
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:user.name=zozo
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:user.home=/home/zozo
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:user.dir=/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin
2019-05-09 20:17:03,585 [myid:] - INFO  [main:ZooKeeper@442] - Initiating client connection, connectString=localhost:2181 sessionTimeout=30000 watcher=org.apache.zookeeper.ZooKeeperMain$MyWatcher@277050dc
2019-05-09 20:17:03,612 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1029] - Opening socket connection to server localhost/127.0.0.1:2181. Will not attempt to authenticate using SASL (unknown error)
Welcome to ZooKeeper!
JLine support is enabled
2019-05-09 20:17:03,697 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@879] - Socket connection established to localhost/127.0.0.1:2181, initiating session
2019-05-09 20:17:03,709 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1303] - Session establishment complete on server localhost/127.0.0.1:2181, sessionid = 0x2042db0dea60036, negotiated timeout = 30000

WATCHER::

WatchedEvent state:SyncConnected type:None path:null
[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper, master, workers, tasks, assign]
[zk: localhost:2181(CONNECTED) 1] create -e /workers/worker1.example.com "worker1.example.com:2224"
Created /workers/worker1.example.com
[zk: localhost:2181(CONNECTED) 2] create /assign/worker1.example.com ""
Created /assign/worker1.example.com
[zk: localhost:2181(CONNECTED) 3] ls /assign/worker1.example.com true
[]
[zk: localhost:2181(CONNECTED) 4]
WATCHER::

WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/assign/worker1.example.com

[zk: localhost:2181(CONNECTED) 4] ls /assign/worker1.example.com
[task-0000000000]
[zk: localhost:2181(CONNECTED) 5] create /tasks/task-0000000000/status "done"
Created /tasks/task-0000000000/status
[zk: localhost:2181(CONNECTED) 6]
```

- machine 2

```
[zozo@VM_0_3_centos bin]$ ll
总用量 2444
-rwxr-xr-x 1 zozo zozo     232 6月  30 2018 README.txt
-rwxr-xr-x 1 zozo zozo    1937 6月  30 2018 zkCleanup.sh
-rwxr-xr-x 1 zozo zozo    1056 6月  30 2018 zkCli.cmd
-rwxr-xr-x 1 zozo zozo    1534 6月  30 2018 zkCli.sh
-rwxr-xr-x 1 zozo zozo    1759 6月  30 2018 zkEnv.cmd
-rwxr-xr-x 1 zozo zozo    2696 6月  30 2018 zkEnv.sh
-rwxr-xr-x 1 zozo zozo    1089 6月  30 2018 zkServer.cmd
-rwxr-xr-x 1 zozo zozo    6773 6月  30 2018 zkServer.sh
-rwxr-xr-x 1 zozo zozo     996 6月  30 2018 zkTxnLogToolkit.cmd
-rwxr-xr-x 1 zozo zozo    1385 6月  30 2018 zkTxnLogToolkit.sh
-rw-rw-r-- 1 zozo zozo 2449621 5月   9 15:07 zookeeper.out
[zozo@VM_0_3_centos bin]$ ./zkCli.sh
Connecting to localhost:2181
2019-05-09 20:10:17,558 [myid:] - INFO  [main:Environment@100] - Client environment:zookeeper.version=3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03, built on 06/29/2018 04:05 GMT
2019-05-09 20:10:17,572 [myid:] - INFO  [main:Environment@100] - Client environment:host.name=VM_0_3_centos
2019-05-09 20:10:17,572 [myid:] - INFO  [main:Environment@100] - Client environment:java.version=1.8.0_192
2019-05-09 20:10:17,590 [myid:] - INFO  [main:Environment@100] - Client environment:java.vendor=Oracle Corporation
2019-05-09 20:10:17,590 [myid:] - INFO  [main:Environment@100] - Client environment:java.home=/home/zozo/app/java/jdk1.8.0_192/jre
2019-05-09 20:10:17,590 [myid:] - INFO  [main:Environment@100] - Client environment:java.class.path=/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../build/classes:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../build/lib/*.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/slf4j-log4j12-1.7.25.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/slf4j-api-1.7.25.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/netty-3.10.6.Final.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/log4j-1.2.17.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/jline-0.9.94.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/audience-annotations-0.5.0.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../zookeeper-3.4.13.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../src/java/lib/*.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../conf:.:/home/zozo/app/java/jdk1.8.0_192/lib/dt.jar:/home/zozo/app/java/jdk1.8.0_192/lib/tools.jar
2019-05-09 20:10:17,590 [myid:] - INFO  [main:Environment@100] - Client environment:java.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
2019-05-09 20:10:17,590 [myid:] - INFO  [main:Environment@100] - Client environment:java.io.tmpdir=/tmp
2019-05-09 20:10:17,590 [myid:] - INFO  [main:Environment@100] - Client environment:java.compiler=<NA>
2019-05-09 20:10:17,590 [myid:] - INFO  [main:Environment@100] - Client environment:os.name=Linux
2019-05-09 20:10:17,590 [myid:] - INFO  [main:Environment@100] - Client environment:os.arch=amd64
2019-05-09 20:10:17,590 [myid:] - INFO  [main:Environment@100] - Client environment:os.version=3.10.0-514.21.1.el7.x86_64
2019-05-09 20:10:17,590 [myid:] - INFO  [main:Environment@100] - Client environment:user.name=zozo
2019-05-09 20:10:17,590 [myid:] - INFO  [main:Environment@100] - Client environment:user.home=/home/zozo
2019-05-09 20:10:17,591 [myid:] - INFO  [main:Environment@100] - Client environment:user.dir=/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin
2019-05-09 20:10:17,592 [myid:] - INFO  [main:ZooKeeper@442] - Initiating client connection, connectString=localhost:2181 sessionTimeout=30000 watcher=org.apache.zookeeper.ZooKeeperMain$MyWatcher@277050dc
Welcome to ZooKeeper!
2019-05-09 20:10:17,667 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1029] - Opening socket connection to server localhost/127.0.0.1:2181. Will not attempt to authenticate using SASL (unknown error)
JLine support is enabled
2019-05-09 20:10:17,813 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@879] - Socket connection established to localhost/127.0.0.1:2181, initiating session
2019-05-09 20:10:17,850 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1303] - Session establishment complete on server localhost/127.0.0.1:2181, sessionid = 0x301a7caf7e70003, negotiated timeout = 30000

WATCHER::

WatchedEvent state:SyncConnected type:None path:null
[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper, master]
[zk: localhost:2181(CONNECTED) 1] create -e /master "master2.example.com:2223"
Node already exists: /master
[zk: localhost:2181(CONNECTED) 2] stat /master true
cZxid = 0x1000005e1
ctime = Thu May 09 20:11:06 CST 2019
mZxid = 0x1000005e1
mtime = Thu May 09 20:11:06 CST 2019
pZxid = 0x1000005e1
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x2042db0dea60035
dataLength = 24
numChildren = 0
[zk: localhost:2181(CONNECTED) 3]
[zk: localhost:2181(CONNECTED) 3]
WATCHER::

WatchedEvent state:SyncConnected type:NodeDeleted path:/master

[zk: localhost:2181(CONNECTED) 3] ls /
[zookeeper]
[zk: localhost:2181(CONNECTED) 4] create -e /master "master2.example.com:2223"
Created /master
[zk: localhost:2181(CONNECTED) 5] create /workers ""
Created /workers
[zk: localhost:2181(CONNECTED) 6] create /tasks ""
Created /tasks
[zk: localhost:2181(CONNECTED) 7] create /assign ""
Created /assign
[zk: localhost:2181(CONNECTED) 8] ls /
[zookeeper, master, workers, tasks, assign]
[zk: localhost:2181(CONNECTED) 9] ls /workers true
[]
[zk: localhost:2181(CONNECTED) 10] ls /tasks true
[]
[zk: localhost:2181(CONNECTED) 11]
WATCHER::

WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/workers

WATCHER::

WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/tasks

[zk: localhost:2181(CONNECTED) 11] ls /tasks
[task-0000000000]
[zk: localhost:2181(CONNECTED) 12] ls /workers
[worker1.example.com]
[zk: localhost:2181(CONNECTED) 13] create /assign/worker1.example.com/task-0000000000 ""
Created /assign/worker1.example.com/task-0000000000
[zk: localhost:2181(CONNECTED) 14]
```

- machine 3

```
[zozo@VM_0_6_centos zookeeper-3.4.13]$ cd bin
[zozo@VM_0_6_centos bin]$ ./zkCli.sh
Connecting to localhost:2181
2019-05-09 20:24:16,544 [myid:] - INFO  [main:Environment@100] - Client environment:zookeeper.version=3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03, built on 06/29/2018 04:05 GMT
2019-05-09 20:24:16,674 [myid:] - INFO  [main:Environment@100] - Client environment:host.name=VM_0_6_centos
2019-05-09 20:24:16,674 [myid:] - INFO  [main:Environment@100] - Client environment:java.version=1.8.0_192
2019-05-09 20:24:16,739 [myid:] - INFO  [main:Environment@100] - Client environment:java.vendor=Oracle Corporation
2019-05-09 20:24:16,739 [myid:] - INFO  [main:Environment@100] - Client environment:java.home=/home/zozo/app/java/jdk1.8.0_192/jre
2019-05-09 20:24:16,739 [myid:] - INFO  [main:Environment@100] - Client environment:java.class.path=/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../build/classes:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../build/lib/*.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/slf4j-log4j12-1.7.25.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/slf4j-api-1.7.25.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/netty-3.10.6.Final.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/log4j-1.2.17.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/jline-0.9.94.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/audience-annotations-0.5.0.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../zookeeper-3.4.13.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../src/java/lib/*.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../conf:.:/home/zozo/app/java/jdk1.8.0_192/lib/dt.jar:/home/zozo/app/java/jdk1.8.0_192/lib/tools.jar
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:java.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:java.io.tmpdir=/tmp
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:java.compiler=<NA>
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:os.name=Linux
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:os.arch=amd64
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:os.version=3.10.0-693.el7.x86_64
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:user.name=zozo
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:user.home=/home/zozo
2019-05-09 20:24:16,741 [myid:] - INFO  [main:Environment@100] - Client environment:user.dir=/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin
2019-05-09 20:24:16,742 [myid:] - INFO  [main:ZooKeeper@442] - Initiating client connection, connectString=localhost:2181 sessionTimeout=30000 watcher=org.apache.zookeeper.ZooKeeperMain$MyWatcher@277050dc
Welcome to ZooKeeper!
2019-05-09 20:24:17,763 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1029] - Opening socket connection to server localhost/127.0.0.1:2181. Will not attempt to authenticate using SASL (unknown error)
JLine support is enabled
2019-05-09 20:24:18,526 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@879] - Socket connection established to localhost/127.0.0.1:2181, initiating session
2019-05-09 20:24:18,569 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1303] - Session establishment complete on server localhost/127.0.0.1:2181, sessionid = 0x1083cee35470003, negotiated timeout = 30000

WATCHER::

WatchedEvent state:SyncConnected type:None path:null
[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper, master, workers, tasks, assign]
[zk: localhost:2181(CONNECTED) 1] create -s /tasks/task- "cmd"
Created /tasks/task-0000000000
[zk: localhost:2181(CONNECTED) 2] ls /tasks/task-0000000000 true
[]
[zk: localhost:2181(CONNECTED) 3]
WATCHER::

WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/tasks/task-0000000000

[zk: localhost:2181(CONNECTED) 3] get /tasks/task-0000000000
cmd
cZxid = 0x1000005ec
ctime = Thu May 09 20:24:48 CST 2019
mZxid = 0x1000005ec
mtime = Thu May 09 20:24:48 CST 2019
pZxid = 0x1000005ee
cversion = 1
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 3
numChildren = 1
[zk: localhost:2181(CONNECTED) 4] get /tasks/task-0000000000/status
done
cZxid = 0x1000005ee
ctime = Thu May 09 20:27:08 CST 2019
mZxid = 0x1000005ee
mtime = Thu May 09 20:27:08 CST 2019
pZxid = 0x1000005ee
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 4
numChildren = 0
[zk: localhost:2181(CONNECTED) 5]
```



多节点数据系统存在复制滞后, 节点失效, 脑裂, 网络, 时钟, 拜占庭故障等诸多问题.
作为技术人员, 我们应该以严谨的态度认真对待, 墨菲定律: 所有可能出错的事情一定会出错.


通常在初期的版本, 用 ZooKeeper 解决做分布式协调服务是一个不错的选择, 到较高版本时, 也有部分大型系统选择放弃 ZooKeeper, 选择自己开发这一部分以保持更高的可定制化和针对性的优化.
总结: 大部分数据系统主要还是专注于数据写入, 存储, 读取上的优化, 将像 ZooKeeper 这种分布式协调系统用于解决了分布式系统中存在的一部分问题, 分布式系统中还需要面对各种各样的其他挑战.

