

# 安装

# 配置

`config/server.properties` 具有如下属性:

```properties
# broker 的全局唯一编号, 不能重复
# The id of the broker. This must be set to a unique integer for each broker.
broker.id=0

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

