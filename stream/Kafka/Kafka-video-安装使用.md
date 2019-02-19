
---

# Document & Code

- [../Kafka-video](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video.md)

---

# 一. 安装

[下载地址](http://kafka.apache.org/downloads)

# 二. 配置

`config/server.properties` 具有如下部分属性, 可参考 [Kafka配置文件 server.properties 三个版本](https://blog.csdn.net/l1028386804/article/details/79194929):

```properties
# broker 的全局唯一编号, 不能重复
# The id of the broker. This must be set to a unique integer for each broker.
broker.id=0

# 套接字服务器坚挺的地址, 如果没有配置, 就使用 java.net.InetAddress.getCanonicalHostName() 的返回值
# The address the socket server listens on. It will get the value returned from 
# java.net.InetAddress.getCanonicalHostName() if not configured.
#   FORMAT:
#     listeners = listener_name://host_name:port
#   EXAMPLE:
#     listeners = PLAINTEXT://your.host.name:9092
#listeners=PLAINTEXT://:9092

# 节点的主机名会通知给生产者和消费者. 
# 如果配置了 "listeners" 就使用 "listeners" 的值, 否则就使用java.net.InetAddress.getCanonicalHostName() 的返回值
# Hostname and port the broker will advertise to producers and consumers. If not set, 
# it uses the value for "listeners" if configured.  Otherwise, it will use the value
# returned from java.net.InetAddress.getCanonicalHostName().
#advertised.listeners=PLAINTEXT://your.host.name:9092

# 处理网络请求的线程数量
# The number of threads that the server uses for receiving requests from the network and sending responses to the network
num.network.threads=3

# 处理磁盘 IO 的线程数量
# The number of threads that the server uses for processing requests, which may include disk I/O
num.io.threads=8

# 发送套接字的缓冲区大小
# The send buffer (SO_SNDBUF) used by the socket server
socket.send.buffer.bytes=102400

# 接收套接字的缓冲区大小
# The receive buffer (SO_RCVBUF) used by the socket server
socket.receive.buffer.bytes=102400

# 请求套接字的缓冲区大小
# The maximum size of a request that the socket server will accept (protection against OOM)
socket.request.max.bytes=104857600

# kafka 运行日志存放的路径
# A comma separated list of directories under which to store log files
log.dirs=/tmp/kafka-logs

# topic 在当前 broker 上的分区个数
# The default number of log partitions per topic. More partitions allow greater
# parallelism for consumption, but this will also result in more files across
# the brokers.
num.partitions=1

# 用来恢复和清理 data 下数据的线程数量
# The number of threads per data directory to be used for log recovery at startup and flushing at shutdown.
# This value is recommended to be increased for installations with data dirs located in RAID array.
num.recovery.threads.per.data.dir=1

# segment 文件保留的最长时间, 超时将被删除
# The minimum age of a log file to be eligible for deletion due to age
log.retention.hours=168

# 配置连接 ZooKeeper 集群地址
# Zookeeper connection string (see zookeeper docs for details).
# This is a comma separated host:port pairs, each corresponding to a zk
# server. e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002".
# You can also append an optional chroot string to the urls to specify the
# root directory for all kafka znodes.
zookeeper.connect=localhost:2181
```

# 三. 命令

## 3.1 启动集群

注意需要先确保配置的 ZooKeeper 集群可用.

前台启动方式如下:
```
bin/kafka-server-start.sh config/server.properties &
```

后台启动方式如下:
```
bin/kafka-server-start.sh -daemon config/server.properties
```

启动后三台机器的 logs 目录如下:
```
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ tree logs
logs
├── cleaner-offset-checkpoint
├── controller.log
├── kafka-authorizer.log
├── kafka-request.log
├── kafkaServer-gc.log.0.current
├── log-cleaner.log
├── log-start-offset-checkpoint
├── meta.properties
├── recovery-point-offset-checkpoint
├── replication-offset-checkpoint
├── server.log
└── state-change.log

0 directories, 12 files
```

## 3.2 关闭集群

依次在三台机器上运行如下命令:
```
bin/kafka-server-stop.sh stop
```

## 3.3 查看当前服务器中的所有 Topic

运行如下命令列出所有 Topic:
```
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --zookeeeper localhost:211 --list
first
```

## 3.4 创建 Topic

运行如下命令创建一个名为 first 的 Topic, 设置 3 个 partition 分区, 2 个 replication-factor 副本因子:
```
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --zookeeper 172.16.0.6:2181 --create --topic first --partitions 3 --replication-factor 2
Created topic "first".
```

创建后三台机器的 logs 目录如下:

第 1 台机的 logs 目录如下:
```
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ tree logs
logs
|-- cleaner-offset-checkpoint
|-- controller.log
|-- first-0
|   |-- 00000000000000000000.index
|   |-- 00000000000000000000.log
|   |-- 00000000000000000000.timeindex
|   `-- leader-epoch-checkpoint
|-- first-2
|   |-- 00000000000000000000.index
|   |-- 00000000000000000000.log
|   |-- 00000000000000000000.timeindex
|   `-- leader-epoch-checkpoint
|-- kafka-authorizer.log
|-- kafka-request.log
|-- kafkaServer-gc.log.0.current
|-- kafkaServer.out
|-- log-cleaner.log
|-- log-start-offset-checkpoint
|-- meta.properties
|-- recovery-point-offset-checkpoint
|-- replication-offset-checkpoint
|-- server.log
`-- state-change.log

2 directories, 21 files
```

第 2 台机的 logs 目录如下:
```
[zozo@VM_0_17_centos kafka_2.12-2.1.0]$ tree logs
logs
|-- cleaner-offset-checkpoint
|-- controller.log
|-- first-0
|   |-- 00000000000000000000.index
|   |-- 00000000000000000000.log
|   |-- 00000000000000000000.timeindex
|   `-- leader-epoch-checkpoint
|-- first-1
|   |-- 00000000000000000000.index
|   |-- 00000000000000000000.log
|   |-- 00000000000000000000.timeindex
|   `-- leader-epoch-checkpoint
|-- kafka-authorizer.log
|-- kafka-request.log
|-- kafkaServer-gc.log.0.current
|-- kafkaServer.out
|-- log-cleaner.log
|-- log-start-offset-checkpoint
|-- meta.properties
|-- recovery-point-offset-checkpoint
|-- replication-offset-checkpoint
|-- server.log
`-- state-change.log

2 directories, 21 files
```

第 3 台机的 logs 目录如下:
```
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ tree logs
logs
|-- cleaner-offset-checkpoint
|-- controller.log
|-- first-1
|   |-- 00000000000000000000.index
|   |-- 00000000000000000000.log
|   |-- 00000000000000000000.timeindex
|   `-- leader-epoch-checkpoint
|-- first-2
|   |-- 00000000000000000000.index
|   |-- 00000000000000000000.log
|   |-- 00000000000000000000.timeindex
|   `-- leader-epoch-checkpoint
|-- kafka-authorizer.log
|-- kafka-request.log
|-- kafkaServer-gc.log.0.current
|-- kafkaServer.out
|-- log-cleaner.log
|-- log-start-offset-checkpoint
|-- meta.properties
|-- recovery-point-offset-checkpoint
|-- replication-offset-checkpoint
|-- server.log
`-- state-change.log

2 directories, 21 files
```

如上, 三台机器中的 `logs/first-0`, `logs/first-1`, `logs/first-2` 文件夹表示的是名为 `fist` 的 Topic 有 3 个 partitions 分区 (分别为 `0` 号 partition 分区, `1` 号 partition 分区，`2` 号 partition 分区) 且每个分区有 2 个 replication-factor 副本因子. 

## 3.5 查看 Topic

执行如下命令查看名为 `first` 的 Topic 信息:
```
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --zookeeper 172.16.0.6:2181 --describe --topic first
Topic:first     PartitionCount:3        ReplicationFactor:2     Configs:
        Topic: first    Partition: 0    Leader: 1       Replicas: 1,2   Isr: 1,2
        Topic: first    Partition: 1    Leader: 2       Replicas: 2,3   Isr: 2,3
        Topic: first    Partition: 2    Leader: 3       Replicas: 3,1   Isr: 3,1
```

如上, 表示如下:
- 名为 `fist` 的 Topic 有 3 个 partitions 分区, 每个 partition 有 2 个副本因子, 1 个 Leader.
- `0` 号 partition 分区的 2 个副本因子在 `1` 号 broker 机器和 `2` 号 broker 机器上存储, 且 `1` 号 broker 机器为 Leader. InSyncReplication 正在同步的副本在 `1` 号 broker 机器和 `2` 号 broker 机器上.
- `1` 号 partition 分区的 2 个副本因子在 `2` 号 broker 机器和 `3` 号 broker 机器上存储, 且 `2` 号 broker 机器为 Leader. InSyncReplication 正在同步的副本在 `2` 号 broker 机器和 `3` 号 broker 机器上.
- `2` 号 partition 分区的 2 个副本因子在 `3` 号 broker 机器和 `1` 号 broker 机器上存储, 且 `3` 号 broker 机器为 Leader. InSyncReplication 正在同步的副本在 `3` 号 broker 机器和 `1` 号 broker 机器上.

此时通过模拟 3 号机器挂掉, 并再次查看名为 `first` 的 Topic 信息:
```
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ jps
25746 Kafka
23850 QuorumPeerMain
29580 Jps
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ kill -9 25746
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ jps
30487 Jps
23850 QuorumPeerMain
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --zookeeper 172.16.0.6:2181 --describe --topic first
Topic:first     PartitionCount:3        ReplicationFactor:2     Configs:
        Topic: first    Partition: 0    Leader: 1       Replicas: 1,2   Isr: 1,2
        Topic: first    Partition: 1    Leader: 2       Replicas: 2,3   Isr: 2
        Topic: first    Partition: 2    Leader: 1       Replicas: 3,1   Isr: 1
```

此时再次启动 3 号机器的服务, 并再次查看名为 `first` 的 Topic 信息:
```
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ jps
30487 Jps
23850 QuorumPeerMain
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ bin/kafka-server-start.sh -daemon config/server.properties
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ jps
30873 Jps
23850 QuorumPeerMain
30783 Kafka
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --zookeeper 172.16.0.6:2181 --describe --topic first
Topic:first     PartitionCount:3        ReplicationFactor:2     Configs:
        Topic: first    Partition: 0    Leader: 1       Replicas: 1,2   Isr: 1,2
        Topic: first    Partition: 1    Leader: 2       Replicas: 2,3   Isr: 2,3
        Topic: first    Partition: 2    Leader: 1       Replicas: 3,1   Isr: 1,3
```

如上, 如需要再次平衡, 可执行相应的平衡命令.

## 3.6 删除 Topic

运行如下命令删除名为 `first` 的 Topic:
```
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --zookeeper 172.16.0.6:2181 --delete --topic first
Topic first is marked for deletion.
Note: This will have no impact if delete.topic.enable is not set to true.
```

在执行上述命令一段时间后再次查看三台机器的 `logs` 目录, 发现 `logs/first-0`, `logs/first-1`, `logs/first-2` 文件夹已经被删除:
```
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ tree logs
logs
|-- cleaner-offset-checkpoint
|-- controller.log
|-- controller.log.2019-02-19-21
|-- kafka-authorizer.log
|-- kafka-request.log
|-- kafkaServer-gc.log.0.current
|-- kafkaServer.out
|-- log-cleaner.log
|-- log-cleaner.log.2019-02-19-21
|-- log-start-offset-checkpoint
|-- meta.properties
|-- recovery-point-offset-checkpoint
|-- replication-offset-checkpoint
|-- server.log
|-- server.log.2019-02-19-21
|-- state-change.log
`-- state-change.log.2019-02-19-21

0 directories, 17 files
```

