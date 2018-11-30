# Document & Code

* [../Zookeeper-video](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-video.md)

---

# 集群的一些基本概念

## ZooKeeper 集群搭建注意点

* 配置数据文件 `myid` 值为： `1`/`2`/`3`。 对应： `server.1`/`server.2`/`server.3`。
* 通过 `./zkCli.sh -server [ip]:[port]` 检测集群是否配置成功。

# 伪分布式方式安装 ZooKeeper 集群

伪分布式：多个 ZooKeeper 运行在一台物理机上。

参考：[ZooKeeper伪分布式集群安装](https://my.oschina.net/vbird/blog/384043)

搭建规则：ip 相同，端口不同（数据同步端口，选举端口），myid 不同。

搭建步骤如下：

1. 创建对应的文件夹，并解压 ZooKeeper 安装包：
```
[centos@VM_0_6_centos zookeeper]$ ll
total 36384
drwxrwxr-x  5 centos centos     4096 Nov 29 22:24 node1
drwxrwxr-x  5 centos centos     4096 Nov 29 22:24 node2
drwxrwxr-x  5 centos centos     4096 Nov 29 22:24 node3
```

2. 创建对应的 dataDir 和 dataLogDir 文件夹：
```
[centos@VM_0_6_centos node3]$ ll
total 12
drwxr-xr-x 10 centos centos 4096 Jul  1 07:36 zookeeper-3.4.13
drwxrwxr-x  2 centos centos 4096 Nov 29 22:24 zookeeper-3.4.13-data
drwxrwxr-x  2 centos centos 4096 Nov 29 22:24 zookeeper-3.4.13-data-log
```

3. 在 dataDir 文件夹下创建对应的 myid
```
[centos@VM_0_6_centos zookeeper]$ echo "1" > node1/zookeeper-3.4.13-data/myid
[centos@VM_0_6_centos zookeeper]$ echo "2" > node2/zookeeper-3.4.13-data/myid
[centos@VM_0_6_centos zookeeper]$ echo "3" > node3/zookeeper-3.4.13-data/myid
```

4. 配置 zoo.cfg

以下为 node1 配置：
```
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/home/centos/app/zookeeper/node1/zookeeper-3.4.13-data
dataLogDir=/home/centos/app/zookeeper/node1/zookeeper-3.4.13-data-log
clientPort=2181
4lw.commands.whitelist=*
server.1=127.0.0.1:2887:3887
server.2=127.0.0.1:2888:3888
server.3=127.0.0.1:2889:3889
```

以下为 node2 配置：
```
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/home/centos/app/zookeeper/node2/zookeeper-3.4.13-data
dataLogDir=/home/centos/app/zookeeper/node2/zookeeper-3.4.13-data-log
clientPort=2182
4lw.commands.whitelist=*
server.1=127.0.0.1:2887:3887
server.2=127.0.0.1:2888:3888
server.3=127.0.0.1:2889:3889
```

以下为 node3 配置：
```
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/home/centos/app/zookeeper/node3/zookeeper-3.4.13-data
dataLogDir=/home/centos/app/zookeeper/node3/zookeeper-3.4.13-data-log
clientPort=2183
4lw.commands.whitelist=*
server.1=127.0.0.1:2887:3887
server.2=127.0.0.1:2888:3888
server.3=127.0.0.1:2889:3889
```

5. 启动每一个 ZooKeeper 服务：
```
[centos@VM_0_6_centos zookeeper]$ cd /home/centos/app/zookeeper/node1/zookeeper-3.4.13/bin
[centos@VM_0_6_centos bin]$ ./zkServer.sh start
[centos@VM_0_6_centos zookeeper]$ cd /home/centos/app/zookeeper/node2/zookeeper-3.4.13/bin
[centos@VM_0_6_centos bin]$ ./zkServer.sh start
[centos@VM_0_6_centos zookeeper]$ cd /home/centos/app/zookeeper/node3/zookeeper-3.4.13/bin
[centos@VM_0_6_centos bin]$ ./zkServer.sh start
```

6. 检查服务是否正常：

过四字命令检查：echo [command] | nc [ip] [port]
```
echo stat | nc 127.0.0.1 2181
echo stat | nc 127.0.0.1 2182
echo stat | nc 127.0.0.1 2183
```

7. 客户端连接某个服务端，创建文件后可同步到多个 node，且文件信息一致，表示集群搭建成功。
```
[centos@VM_0_6_centos bin]$ ./zkCli.sh -server 127.0.0.1:2181

[zk: 127.0.0.1:2181(CONNECTED) 0] ls /
[zookeeper]
[zk: 127.0.0.1:2181(CONNECTED) 1] create /hello 111
Created /hello
[zk: 127.0.0.1:2181(CONNECTED) 2] ls /
[hello, zookeeper]
[zk: 127.0.0.1:2181(CONNECTED) 0] get /hello
111
cZxid = 0x100000002
ctime = Thu Nov 29 22:42:14 CST 2018
mZxid = 0x100000002
mtime = Thu Nov 29 22:42:14 CST 2018
pZxid = 0x100000002
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 3
numChildren = 0


[centos@VM_0_6_centos bin]$ ./zkCli.sh -server 127.0.0.1:2182

[zk: 127.0.0.1:2182(CONNECTED) 0] ls /
[hello, zookeeper]
[zk: 127.0.0.1:2182(CONNECTED) 2] get /hello
111
cZxid = 0x100000002
ctime = Thu Nov 29 22:42:14 CST 2018
mZxid = 0x100000002
mtime = Thu Nov 29 22:42:14 CST 2018
pZxid = 0x100000002
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 3
numChildren = 0
```

# 集群方式安装 ZooKeeper 集群

搭建规则：ip 不同，端口相同（数据同步端口，选举端口），myid 不同。

搭建步骤如下：

1. 在每个节点上，创建 `ZooKeeper` 文件夹，并解压 ZooKeeper 安装包。

同时添加 `zookeeper-3.4.13-data` 和 `zookeeper-3.4.13-data-log` 作为 `conf/zoo.cfg` 中的 `dataDir` 和 `dataLogDir` 配置：

以下为某一个节点上的文件目录：
```
[zozo@VM_0_3_centos zookeeper]$ ll
总用量 36376
drwxr-xr-x 10 zozo zozo     4096 7月   1 07:36 zookeeper-3.4.13
drwxrwxr-x  2 zozo zozo     4096 11月 30 21:05 zookeeper-3.4.13-data
drwxrwxr-x  2 zozo zozo     4096 11月 30 21:05 zookeeper-3.4.13-data-log
-rw-r--r--  1 zozo zozo 37191810 11月 30 20:46 zookeeper-3.4.13.tar.gz
```

2. 在 dataDir 文件夹下创建对应的 myid。

以下为第 1 台节点：
```
[centos@VM_0_6_centos zookeeper]$ echo "1" > zookeeper-3.4.13-data/myid
```

以下为第 2 台节点：
```
[centos@VM_0_6_centos zookeeper]$ echo "2" > zookeeper-3.4.13-data/myid
```

以下为第 3 台节点：
```
[centos@VM_0_6_centos zookeeper]$ echo "3" > zookeeper-3.4.13-data/myid
```

3. 配置 zoo.cfg

以下为第 1/2/3 台节点配置（相同配置）：
```
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/home/zozo/app/zookeeper/zookeeper-3.4.13-data
dataLogDir=/home/zozo/app/zookeeper/zookeeper-3.4.13-data-log
clientPort=2181
4lw.commands.whitelist=*
server.1=172.16.0.6:2888:3888
server.2=172.16.0.17:2888:3888
server.3=172.16.0.3:2888:3888
```

4. 启动每一个 ZooKeeper 服务：
```
[centos@VM_0_6_centos bin]$ ./zkServer.sh start
[centos@VM_0_6_centos bin]$ ./zkServer.sh start
[centos@VM_0_6_centos bin]$ ./zkServer.sh start
```

5. 检查服务是否正常：

过四字命令检查：echo [command] | nc [ip] [port]
```
echo stat | nc 172.16.0.6 2181
echo stat | nc 172.16.0.17 2181
echo stat | nc 172.16.0.3 2181
```

6. 客户端连接某个服务端，创建文件后可同步到多个 node，且文件信息一致，表示集群搭建成功。
```
[zozo@VM_0_6_centos bin]$ ./zkCli.sh -server 172.16.0.6:2181
[zk: 172.16.0.6:2181(CONNECTED) 0] ls /
[zookeeper]
[zk: 172.16.0.6:2181(CONNECTED) 3] create /dist 123
Created /dist
[zk: 172.16.0.6:2181(CONNECTED) 4] ls /
[dist, zookeeper]

[zozo@VM_0_6_centos bin]$ ./zkCli.sh -server 172.16.0.17:2181
[zk: 172.16.0.6:2181(CONNECTED) 4] ls /
[dist, zookeeper]

[zozo@VM_0_6_centos bin]$ ./zkCli.sh -server 172.16.0.3:2181
[zk: 172.16.0.6:2181(CONNECTED) 4] ls /
[dist, zookeeper]
```


