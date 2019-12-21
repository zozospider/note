
- [Document & Code](#document--code)
- [一. 安装](#一-安装)
- [二. 配置](#二-配置)
- [三. 命令](#三-命令)
    - [3.0 常用命令](#30-常用命令)
    - [3.1 启动集群](#31-启动集群)
    - [3.2 关闭集群](#32-关闭集群)
    - [3.3 查看当前服务器中的所有 Topic](#33-查看当前服务器中的所有-topic)
    - [3.4 创建 Topic](#34-创建-topic)
    - [3.5 查看 Topic & 平衡 Leader](#35-查看-topic--平衡-leader)
    - [3.6 创建 Topic 的 Partition 和 Replication-factor 参数](#36-创建-topic-的-partition-和-replication-factor-参数)
    - [3.7 删除 Topic](#37-删除-topic)
    - [3.8 生产消息](#38-生产消息)
    - [3.9 消费消息](#39-消费消息)
    - [3.10 查看 log](#310-查看-log)

---

# Document & Code

- [../Kafka-video](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video.md)

---

# 一. 安装

[下载地址](http://kafka.apache.org/downloads)

---

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

---

# 三. 命令

## 3.0 常用命令

```bash
# 查看所有分区
bin/kafka-topics.sh --list --zookeeper 172.16.0.6:2181
# 删除一个分区
bin/kafka-topics.sh --delete --zookeeper 172.16.0.6:2181 --topic topic1
# 创建一个分区
bin/kafka-topics.sh --create --zookeeper 172.16.0.6:2181 --partitions 3 --replication-factor 2 --topic topic1
# 查看一个分区
bin/kafka-topics.sh --describe --zookeeper 172.16.0.6:2181 --topic topic1
# 查看一个消费者组
bin/kafka-consumer-groups.sh --bootstrap-server 172.16.0.6:9092 --describe --group group1
# 查看消费情况
bin/kafka-consumer-offset-checker.sh --zookeeper 172.16.0.6:2181 --group group1

# 控制台生产数据
bin/kafka-console-producer.sh --broker-list 172.16.0.6:9092 --topic topic1
# 控制台消费数据
bin/kafka-console-consumer.sh --zookeeper 172.16.0.6:2181 --topic topic1 --from-beginning
# 控制台消费数据 (指定消费者组)
bin/kafka-console-consumer.sh --zookeeper 172.16.0.6:2181 --topic topic1 --from-beginning --consumer.config config/consumer.properties
```

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
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --list --zookeeper 172.16.0.6:2181
first
```

## 3.4 创建 Topic

运行如下命令创建一个名为 first 的 Topic, 设置 3 个 partition 分区, 2 个 replication-factor 副本因子:
```
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --create --zookeeper 172.16.0.6:2181 --partitions 3 --replication-factor 2 --topic first
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

## 3.5 查看 Topic & 平衡 Leader

执行如下命令查看名为 `first` 的 Topic 信息:
```
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --describe --zookeeper 172.16.0.6:2181 --topic first
Topic:first	PartitionCount:3	ReplicationFactor:2	Configs:
	Topic: first	Partition: 0	Leader: 3	Replicas: 3,1	Isr: 3,1
	Topic: first	Partition: 1	Leader: 1	Replicas: 1,2	Isr: 1,2
	Topic: first	Partition: 2	Leader: 2	Replicas: 2,3	Isr: 2,3
```

如上, 表示如下:
- 名为 `fist` 的 Topic 有 3 个 partitions 分区, 每个 partition 有 2 个副本因子, 1 个 Leader.
- `0` 号 partition 分区的 2 个副本因子在 `3` 号 broker 机器和 `1` 号 broker 机器上存储, 且 `3` 号 broker 机器为 Leader. InSyncReplication 正在同步的副本在 `3` 号 broker 机器和 `1` 号 broker 机器上.
- `1` 号 partition 分区的 2 个副本因子在 `1` 号 broker 机器和 `2` 号 broker 机器上存储, 且 `1` 号 broker 机器为 Leader. InSyncReplication 正在同步的副本在 `1` 号 broker 机器和 `2` 号 broker 机器上.
- `2` 号 partition 分区的 2 个副本因子在 `2` 号 broker 机器和 `3` 号 broker 机器上存储, 且 `2` 号 broker 机器为 Leader. InSyncReplication 正在同步的副本在 `2` 号 broker 机器和 `3` 号 broker 机器上.

此时通过模拟 3 号机器挂掉, 并再次查看名为 `first` 的 Topic 信息:
```
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ jps
23850 QuorumPeerMain
25963 Jps
30783 Kafka
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ kill -9 30783
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ jps
26003 Jps
23850 QuorumPeerMain
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --describe --zookeeper 172.16.0.6:2181 --topic first
Topic:first	PartitionCount:3	ReplicationFactor:2	Configs:
	Topic: first	Partition: 0	Leader: 1	Replicas: 3,1	Isr: 1
	Topic: first	Partition: 1	Leader: 1	Replicas: 1,2	Isr: 1,2
	Topic: first	Partition: 2	Leader: 2	Replicas: 2,3	Isr: 2
```

此时再次启动 3 号机器的服务, 并再次查看名为 `first` 的 Topic 信息:
```
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ jps
23850 QuorumPeerMain
26413 Jps
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ bin/kafka-server-start.sh -daemon config/server.properties
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ jps
26771 Jps
23850 QuorumPeerMain
26731 Kafka
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --describe --zookeeper 172.16.0.6:2181 --topic first
Topic:first	PartitionCount:3	ReplicationFactor:2	Configs:
	Topic: first	Partition: 0	Leader: 1	Replicas: 3,1	Isr: 1,3
	Topic: first	Partition: 1	Leader: 1	Replicas: 1,2	Isr: 1,2
	Topic: first	Partition: 2	Leader: 2	Replicas: 2,3	Isr: 2,3
```

如上, 如需要再次平衡 Leader, 可执行如下的再平衡命令:
```
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ bin/kafka-preferred-replica-election.sh --zookeeper 172.16.0.6:2181
Created preferred replica election path with __consumer_offsets-22,__consumer_offsets-30,__consumer_offsets-8,__consumer_offsets-21,__consumer_offsets-4,__consumer_offsets-27,__consumer_offsets-7,__consumer_offsets-9,first-1,__consumer_offsets-46,__consumer_offsets-25,__consumer_offsets-35,__consumer_offsets-41,__consumer_offsets-33,__consumer_offsets-23,__consumer_offsets-49,__consumer_offsets-47,__consumer_offsets-16,test-0,__consumer_offsets-28,__consumer_offsets-31,__consumer_offsets-36,__consumer_offsets-42,__consumer_offsets-3,__consumer_offsets-18,first-2,__consumer_offsets-37,first-0,__consumer_offsets-15,__consumer_offsets-24,__consumer_offsets-38,__consumer_offsets-17,__consumer_offsets-48,__consumer_offsets-19,__consumer_offsets-11,__consumer_offsets-13,__consumer_offsets-2,__consumer_offsets-43,__consumer_offsets-6,__consumer_offsets-14,__consumer_offsets-20,__consumer_offsets-0,__consumer_offsets-44,__consumer_offsets-39,__consumer_offsets-12,__consumer_offsets-45,__consumer_offsets-1,__consumer_offsets-5,__consumer_offsets-26,__consumer_offsets-29,__consumer_offsets-34,__consumer_offsets-10,__consumer_offsets-32,__consumer_offsets-40
Successfully started preferred replica election for partitions Set(__consumer_offsets-22, __consumer_offsets-30, __consumer_offsets-8, __consumer_offsets-21, __consumer_offsets-4, __consumer_offsets-27, __consumer_offsets-7, __consumer_offsets-9, first-1, __consumer_offsets-46, __consumer_offsets-25, __consumer_offsets-35, __consumer_offsets-41, __consumer_offsets-33, __consumer_offsets-23, __consumer_offsets-49, __consumer_offsets-47, __consumer_offsets-16, test-0, __consumer_offsets-28, __consumer_offsets-31, __consumer_offsets-36, __consumer_offsets-42, __consumer_offsets-3, __consumer_offsets-18, first-2, __consumer_offsets-37, first-0, __consumer_offsets-15, __consumer_offsets-24, __consumer_offsets-38, __consumer_offsets-17, __consumer_offsets-48, __consumer_offsets-19, __consumer_offsets-11, __consumer_offsets-13, __consumer_offsets-2, __consumer_offsets-43, __consumer_offsets-6, __consumer_offsets-14, __consumer_offsets-20, __consumer_offsets-0, __consumer_offsets-44, __consumer_offsets-39, __consumer_offsets-12, __consumer_offsets-45, __consumer_offsets-1, __consumer_offsets-5, __consumer_offsets-26, __consumer_offsets-29, __consumer_offsets-34, __consumer_offsets-10, __consumer_offsets-32, __consumer_offsets-40)
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --describe --zookeeper 172.16.0.6:2181 --topic first
Topic:first	PartitionCount:3	ReplicationFactor:2	Configs:
	Topic: first	Partition: 0	Leader: 3	Replicas: 3,1	Isr: 1,3
	Topic: first	Partition: 1	Leader: 1	Replicas: 1,2	Isr: 1,2
	Topic: first	Partition: 2	Leader: 2	Replicas: 2,3	Isr: 2,3
```

## 3.6 创建 Topic 的 Partition 和 Replication-factor 参数

- 创建 Topic 时, 当指定的 Partition 数量大于 Broker 机器数量时, 可以创建成功, 如下:
```
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --create --zookeeper 172.16.0.6:2181 --partitions 4 --replication-factor 2 --topic second
Created topic "second".
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --describe --zookeeper 172.16.0.6:2181 --topic second
Topic:second	PartitionCount:4	ReplicationFactor:2	Configs:
	Topic: second	Partition: 0	Leader: 2	Replicas: 2,1	Isr: 2,1
	Topic: second	Partition: 1	Leader: 3	Replicas: 3,2	Isr: 3,2
	Topic: second	Partition: 2	Leader: 1	Replicas: 1,3	Isr: 1,3
	Topic: second	Partition: 3	Leader: 2	Replicas: 2,3	Isr: 2,3
```

如上所示, `2` 机器上有 2 个 Leader, `3` 机器上有 3 个 Replication.

- 创建 Topic 时, 当指定的 replication-factor 数量大于 Broker 机器数量时, 会创建失败, 如下:
```
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-topics.sh --create --zookeeper 172.16.0.6:2181 --partitions 3 --replication-factor 4 --topic third
Error while executing topic command : Replication factor: 4 larger than available brokers: 3.
[2019-02-26 21:14:46,048] ERROR org.apache.kafka.common.errors.InvalidReplicationFactorException: Replication factor: 4 larger than available brokers: 3.
 (kafka.admin.TopicCommand$)
```

如上所示, 因为副本数量大于机器数量时, 同一台机器会存在多于 1 个相同分区的副本, 没有意义, 所以创建失败.

## 3.7 删除 Topic

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

## 3.8 生产消息

通过连接 kafka 的 broker 进行生产:
```
[zozo@VM_0_3_centos kafka_2.12-2.1.0]$ bin/kafka-console-producer.sh --broker-list 172.16.0.6:9092 --topic first
>hello
>why
```

## 3.9 消费消息

通过连接 kafka 的 server 进行消费, 其中, `--from-beginning` 表示从头开始读:
```
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-console-consumer.sh --bootstrap-server 172.16.0.6:9092 --topic first --from-beginning
hello
why
```

查看三台机器的 `logs` 目录, 有如下 offsets 文件夹:
```
[zozo@VM_0_17_centos logs]$ pwd
/home/zozo/app/kafka/five/kafka_2.12-2.1.0/logs
[zozo@VM_0_17_centos logs]$ ll
总用量 1584
-rw-rw-r-- 1 zozo zozo      4 2月  19 22:37 cleaner-offset-checkpoint
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-1
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-10
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-13
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-16
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-19
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-22
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-25
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-28
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-31
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-34
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-37
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-4
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-40
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-43
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-46
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-49
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 __consumer_offsets-7
-rw-rw-r-- 1 zozo zozo    271 2月  23 02:01 controller.log
-rw-rw-r-- 1 zozo zozo    307 2月  19 21:28 controller.log.2019-02-19-21
drwxrwxr-x 2 zozo zozo   4096 2月  23 02:01 first-1
drwxrwxr-x 2 zozo zozo   4096 2月  20 21:57 first-2
-rw-rw-r-- 1 zozo zozo      0 2月  19 21:28 kafka-authorizer.log
-rw-rw-r-- 1 zozo zozo      0 2月  19 21:28 kafka-request.log
-rw-rw-r-- 1 zozo zozo 128277 2月  27 11:22 kafkaServer-gc.log.0.current
-rw-rw-r-- 1 zozo zozo 280143 2月  27 12:58 kafkaServer.out
-rw-rw-r-- 1 zozo zozo    422 2月  19 22:37 log-cleaner.log
-rw-rw-r-- 1 zozo zozo    172 2月  19 21:28 log-cleaner.log.2019-02-19-21
-rw-rw-r-- 1 zozo zozo      4 2月  27 12:58 log-start-offset-checkpoint
-rw-rw-r-- 1 zozo zozo     54 2月  19 21:28 meta.properties
-rw-rw-r-- 1 zozo zozo    463 2月  27 12:58 recovery-point-offset-checkpoint
-rw-rw-r-- 1 zozo zozo    463 2月  27 12:59 replication-offset-checkpoint
drwxrwxr-x 2 zozo zozo   4096 2月  26 21:12 second-0
drwxrwxr-x 2 zozo zozo   4096 2月  26 21:12 second-1
drwxrwxr-x 2 zozo zozo   4096 2月  26 21:12 second-3
-rw-rw-r-- 1 zozo zozo    942 2月  27 12:58 server.log
-rw-rw-r-- 1 zozo zozo  33514 2月  19 21:58 server.log.2019-02-19-21
-rw-rw-r-- 1 zozo zozo   6293 2月  19 22:58 server.log.2019-02-19-22
-rw-rw-r-- 1 zozo zozo    942 2月  19 23:58 server.log.2019-02-19-23
-rw-rw-r-- 1 zozo zozo   4430 2月  26 21:12 state-change.log
-rw-rw-r-- 1 zozo zozo   3202 2月  19 21:30 state-change.log.2019-02-19-21
```

如上的一系列 `__consumer_offsets-xx` 文件夹在三台机器上共有 50 个文件夹, 表示名为 `__consumer_offsets` 的 Topic 有 50 个 Partition, 用于记录消费者的 offset 信息. 另外, Kafka 也支持将 offset 信息保存在第三方服务 (如 Redis).

通过连接 ZooKeeper 进行消费 (新版本已废除, 会报如下错误):
```
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-console-consumer.sh --zookeeper 172.16.0.6:2181 --topic first --from-beginning
zookeeper is not a recognized option
Option                                   Description
------                                   -----------
--bootstrap-server <String: server to    REQUIRED: The server(s) to connect to.
  connect to>
--consumer-property <String:             A mechanism to pass user-defined
...
```

如果出现如下错误表示消费了一个名为 `test` 的不存在的 topic (These error are just Kafka’s way of telling us the topics didn’t exist but were created.):
```
[zozo@VM_0_6_centos kafka_2.12-2.1.0]$ bin/kafka-console-consumer.sh --bootstrap-server 172.16.0.6:9092 --topic test --from-beginning
[2019-02-20 21:09:08,834] WARN [Consumer clientId=consumer-1, groupId=console-consumer-93281] Error while fetching metadata with correlation id 2 : {test=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
[2019-02-20 21:09:08,957] WARN [Consumer clientId=consumer-1, groupId=console-consumer-93281] Error while fetching metadata with correlation id 6 : {test=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
[2019-02-20 21:09:09,062] WARN [Consumer clientId=consumer-1, groupId=console-consumer-93281] Error while fetching metadata with correlation id 7 : {test=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
[2019-02-20 21:09:09,167] WARN [Consumer clientId=consumer-1, groupId=console-consumer-93281] Error while fetching metadata with correlation id 8 : {test=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
[2019-02-20 21:09:09,986] WARN [Consumer clientId=consumer-1, groupId=console-consumer-93281] Error while fetching metadata with correlation id 12 : {test=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
[2019-02-20 21:09:10,226] WARN [Consumer clientId=consumer-1, groupId=console-consumer-93281] Error while fetching metadata with correlation id 16 : {test=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
[2019-02-20 21:09:10,639] WARN [Consumer clientId=consumer-1, groupId=console-consumer-93281] Error while fetching metadata with correlation id 24 : {test=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
```

## 3.10 查看 log

通过 `strings` 命令可以查看 logs 目录下的 .log 文件内容:
```
[zozo@VM_0_6_centos first-1]$ pwd
/home/zozo/app/kafka/five/kafka_2.12-2.1.0/logs/first-1
[zozo@VM_0_6_centos first-1]$ ll
总用量 20520
-rw-rw-r-- 1 zozo zozo 10485760 2月  20 20:52 00000000000000000000.index
-rw-rw-r-- 1 zozo zozo       73 2月  20 21:11 00000000000000000000.log
-rw-rw-r-- 1 zozo zozo 10485756 2月  20 20:52 00000000000000000000.timeindex
-rw-rw-r-- 1 zozo zozo        8 2月  20 20:52 leader-epoch-checkpoint
[zozo@VM_0_6_centos first-1]$ strings 00000000000000000000.log
hello
```

部分文件会出现无法 `strings` 命令解析的现象, 如下所示:
```
[zozo@VM_0_6_centos first-0]$ pwd
/home/zozo/app/kafka/five/kafka_2.12-2.1.0/logs/first-0
[zozo@VM_0_6_centos first-0]$ ll
总用量 20520
-rw-rw-r-- 1 zozo zozo 10485760 2月  20 20:52 00000000000000000000.index
-rw-rw-r-- 1 zozo zozo       71 2月  20 21:13 00000000000000000000.log
-rw-rw-r-- 1 zozo zozo 10485756 2月  20 20:52 00000000000000000000.timeindex
-rw-rw-r-- 1 zozo zozo        8 2月  20 21:13 leader-epoch-checkpoint
[zozo@VM_0_6_centos first-0]$ strings 00000000000000000000.log
[zozo@VM_0_6_centos first-0]$ cat 00000000000000000000.log
;�t�i

44i

44��������������why
```

---
