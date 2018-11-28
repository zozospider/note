# Document & Code

* [../Zookeeper-video](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-video.md)

---

# ZooKeeper 数据模型介绍

## 结构

树形结构，类似文件系统

## 特性

* 每一个节点成为 Znode，它可以有子节点，也可以有数据。
* 节点可分为临时节点和永久节点，临时节点在客户端断开后小时。
* 每个节点都有自己的版本号（可以通过命令行查看），节点数据变化时，版本号会累加（乐观锁），如果删除/修改节点时，版本号不匹配，则会报错。
* 每个节点存储不宜过大，几 K 即可。
* 节点可以设置 ACL 权限，限制用户的访问。

# ZooKeeper 客户端连接关闭服务端，查看 Znode

ZooKeeper 客户端可连接 ZooKeeper 服务端进行操作。

首先，需要启动服务端，以下为部分记录：
```
[centos@VM_0_6_centos bin]$ ./zkServer.sh start
ZooKeeper JMX enabled by default
Using config: /home/centos/app/zookeeper/zookeeper-3.4.13/bin/../conf/zoo.cfg
Starting zookeeper ... STARTED
```

然后，启动客户端，客户端连接成功后，可以进行一系列操作，如 `ls`，客户端通过 `Ctrl + C` 可断开连接，以下为部分记录：
```
[centos@VM_0_6_centos bin]$ ./zkCli.sh
Connecting to localhost:2181
2018-11-27 22:34:56,061 [myid:] - INFO  [main:Environment@100] - Client environment:zookeeper.version=3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03, built on 06/29/2018 04:05 GMT
2018-11-27 22:34:56,064 [myid:] - INFO  [main:Environment@100] - Client environment:host.name=VM_0_6_centos
2018-11-27 22:34:56,065 [myid:] - INFO  [main:Environment@100] - Client environment:java.version=1.8.0_192
2018-11-27 22:34:56,069 [myid:] - INFO  [main:Environment@100] - Client environment:java.vendor=Oracle Corporation
2018-11-27 22:34:56,069 [myid:] - INFO  [main:Environment@100] - Client environment:java.home=/home/centos/app/java/jdk1.8.0_192/jre
2018-11-27 22:34:56,069 [myid:] - INFO  [main:Environment@100] - Client environment:java.class.path=/home/centos/app/zookeeper/zookeeper-3.4.13/bin/../build/classes:/home/centos/app/zookeeper/zookeeper-3.4.13/bin/../build/lib/*.jar:/home/centos/app/zookeeper/zookeeper-3.4.13/bin/../lib/slf4j-log4j12-1.7.25.jar:/home/centos/app/zookeeper/zookeeper-3.4.13/bin/../lib/slf4j-api-1.7.25.jar:/home/centos/app/zookeeper/zookeeper-3.4.13/bin/../lib/netty-3.10.6.Final.jar:/home/centos/app/zookeeper/zookeeper-3.4.13/bin/../lib/log4j-1.2.17.jar:/home/centos/app/zookeeper/zookeeper-3.4.13/bin/../lib/jline-0.9.94.jar:/home/centos/app/zookeeper/zookeeper-3.4.13/bin/../lib/audience-annotations-0.5.0.jar:/home/centos/app/zookeeper/zookeeper-3.4.13/bin/../zookeeper-3.4.13.jar:/home/centos/app/zookeeper/zookeeper-3.4.13/bin/../src/java/lib/*.jar:/home/centos/app/zookeeper/zookeeper-3.4.13/bin/../conf:.:/home/centos/app/java/jdk1.8.0_192/lib/dt.jar:/home/centos/app/java/jdk1.8.0_192/lib/tools.jar
2018-11-27 22:34:56,069 [myid:] - INFO  [main:Environment@100] - Client environment:java.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
2018-11-27 22:34:56,069 [myid:] - INFO  [main:Environment@100] - Client environment:java.io.tmpdir=/tmp
2018-11-27 22:34:56,069 [myid:] - INFO  [main:Environment@100] - Client environment:java.compiler=<NA>
2018-11-27 22:34:56,069 [myid:] - INFO  [main:Environment@100] - Client environment:os.name=Linux
2018-11-27 22:34:56,070 [myid:] - INFO  [main:Environment@100] - Client environment:os.arch=amd64
2018-11-27 22:34:56,070 [myid:] - INFO  [main:Environment@100] - Client environment:os.version=3.10.0-693.el7.x86_64
2018-11-27 22:34:56,070 [myid:] - INFO  [main:Environment@100] - Client environment:user.name=centos
2018-11-27 22:34:56,070 [myid:] - INFO  [main:Environment@100] - Client environment:user.home=/home/centos
2018-11-27 22:34:56,070 [myid:] - INFO  [main:Environment@100] - Client environment:user.dir=/home/centos/app/zookeeper/zookeeper-3.4.13/bin
2018-11-27 22:34:56,071 [myid:] - INFO  [main:ZooKeeper@442] - Initiating client connection, connectString=localhost:2181 sessionTimeout=30000 watcher=org.apache.zookeeper.ZooKeeperMain$MyWatcher@277050dc
2018-11-27 22:34:56,095 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1029] - Opening socket connection to server localhost/127.0.0.1:2181. Will not attempt to authenticate using SASL (unknown error)
Welcome to ZooKeeper!
JLine support is enabled
2018-11-27 22:34:56,227 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@879] - Socket connection established to localhost/127.0.0.1:2181, initiating session
2018-11-27 22:34:56,347 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1303] - Session establishment complete on server localhost/127.0.0.1:2181, sessionid = 0x10518cfcde90000, negotiated timeout = 30000

WATCHER::

WatchedEvent state:SyncConnected type:None path:null
[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper]
[zk: localhost:2181(CONNECTED) 1] ls /zookeeper
[quota]
[zk: localhost:2181(CONNECTED) 2] ls /zookeeper/quota
[]
[zk: localhost:2181(CONNECTED) 3] [centos@VM_0_6_centos bin]$ pwd
/home/centos/app/zookeeper/zookeeper-3.4.13/bin
[centos@VM_0_6_centos bin]$
```

# ZooKeeper 的作用体现

* Master 节点选举：主节点挂了以后，从节点会接手，保证唯一（首脑模式）。
* 统一配置文件管理：只需部署一台机，可把配置文件同步到其他机器。
* 发布与订阅：发布者存储数据到 Znode，订阅者会得到变更通知。
* 提供分布式锁：不同线程争夺资源。
* 集群管理：保证数据强一致性。

