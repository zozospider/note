
# 九. 管理 Kafka

`136 9.1`: __主题操作__
__创建主题__: 主题名字, 复制系数, 分区.  使用 `--if-not-exists` 参数，这样即使主题已经存在，也不会抛出重复创建主题的错误。
```bash
kafka-topics.sh --zookeeper <zookeeper connect> --create --topic <string> --replication-factor <interger> --partitions <integer>
# 创建一个叫作 my-topic 的主题, 主题包含 8 个分区, 每个分区拥有两个副本.
kafka-topics.sh --zookeeper zoo1.example.com:2181/kafka-cluster --create --topic my-topic --replication-factor 2 --partitions 8
```
__增加分区__: 主题基于分区进行伸缩和复制，增加分区主要是为了扩展主题容量或者降低单个分区的吞吐量。
调整键的主题: 从消费者角度来看，为基于键的主题添加分区是很困难的。因为如果改变了分区的数量，键到分区之间 的映射也会发生变化。
我们无法减少主题的分区数量。因为如果删除了分区，分区里的数据也一并被删除，导致数据不一致.
```bash
# 将 my-topic 主题的分区数量增加到 16.
kafka-toplcs.sh --zookeeper zoo1.example.com:2181/kafka-cluster --alter --topic my-topic --partitions 16
```
__删除主题__: 为了能够删除主题， broker 的 `delete.topic.enable` 参数必须被设置为 true。
```bash
# 删除 my-topic 主题.
kafka-topics.sh --zookeeper zoo1.example.com:2181/kafka-cluster --delete --topic my-topic
```
__列出集群里的所有主题__:
```bash
# 列出集群里的所有主题.
kafka-topics.sh --zookeeper zoo1.example.com:2181/kafka-cluster --list
```
__列出主题详细信息__:
```bash
# 列出集群里所有主题的详细信息.
kafka-topics.sh --zookeeper zoo1.example.com:2181/kafka-cluster --describe
# 使用 --topics-with-overrides 参数可以找出所有包含覆盖配置的主题，它只会列出包含了与集群不一样配置的主题.
# 使用 --under-replicated-partitions 参数可以列出所有包含不同步副本的分区.
kafka-topics.sh --zookeeper zoo1.example.com:2181/kafka-cluster --describe --under-replicated-partitions
# 使用 --unavailable-partitions 参数可以列出所有没有 Leader 的分区, 这些分区已经处于离线状态, 对于生产者和消费者来说是不可用的.
```

`140 9.2`: __消费者群组__:
__列出并描述群组__:
```bash
# 列出旧版本的消费者群组.
kafka-consumer-groups.sh --zookeeper zoo1.example.com:2181/kafka-cluster --list
# 列出新版本的消费者群组.
kafka-consumer-groups.sh --new-consumer --bootstrap-server kafka1.example.com:9092/kafka-cluster --list
# 获取旧版本消费者群组 testgroup 的详细信息.
#    GROUP: 消费者群组的名字
#    TOPIC: 正在被读取的主题名字
#    PARTITION: 正在被读取的分区 ID
#    CURRENT-OFFSET: 消费者群组最近提交的偏移盏, 也就是消费者在分区里读取的当前位置
#    LOG-END-OFFSET: 当前高水位偏移量, 也就是最近一个被读取消息的偏移量, 同时也是最近一个被提交到集群的偏移量
#    LAG: 消费者的 CURRENT-OFFSET 和 broker 的 LOG-END-OFFSET 之间的差距
#    OWNER: 消费者群组里正在读取该分区的消费者。这是一个消费者的 ID, 不一定包含消费者的主机名
kafka-consumer-groups.sh --zookeeper zoo1.example.com:2181/kafka-cluster --describe --group testgroup
```
__删除群组__: 只有旧版本的消费者客户端才支持删除群组的操作.
__偏移量管理__: 只有旧版本可通过 kafka-run-class.sh 脚本实现.

`143 9.3`: __动态配置变更__:
__覆盖主题的默认配置__:
```bash
kafka-configs.sh --zookeeper zoo1.example.com:2181/kafka-cluster --alter --entity-type topics --entity-name <topic name> --add-config <key>=<value>[,<key>=<value>...]
# 将主题 my-topic 的消息保留时间设为 1 个小时 (3600000 ms)
kafka-configs.sh --zookeeper zoo1.example.com:2181/kafka-cluster --alter --entity-type topics --entity-name my-topic --add-config retention.ms=3600000
```
__覆盖客户端的默认配置__:
```bash
kafka-configs.sh --zookeeper zoo1.example.com:2181/kafka-cluster --alter --entity-type clients --entity-name <client ID> --add-config <key>=<value>[,<key>=<value>...]
# 可用客户端配置参数: producer_bytes_rate (单个生产者每秒钟可以往单个 broker 上生成的消息字节数), consumer_bytes_rate (单个消费者每秒钟可以从单个 broker 读取的消息字节数)
```
__列出被覆盖的配置__: 这个命令只能用于显示被覆盖的配置，不包含集群的默认配置。目前还无法通过 Zookeeper 或 Kafka 实现动态地获取 broker 本身的配置。
```bash
# 列出主题 my-topic 所有被覆盖的配置.
kafka-configs.sh --zookeeper zoo1.example.com:2181/kafka-cluster --describe --entity-type topics --entity-name my-topic
```
__移除被覆盖的配置__: 动态的配置完全可以被移除，从而恢复到集群的默认配置。
```bash
# 删除主题 my-topic 的 retention.ms 覆盖配置.
kafka-configs.sh --zookeeper zoo1.example.com:2181/kafka-cluster --alter --entity-type topics --entity-name my-topic --delete-config retention.ms
```

`146 9.4`: __分区管理__:
自动 Leader 再均衡: broker 有一个配置可以用于启用自动首领再均衡，不过到目前为止，并不建议在生产环境使用该功能。自动均衡会带来严重的性能问题，在大型的集群里，它会造成客户端流量的长时间停顿。
__首选的 Leader 选举__: ?
__修改分区副本__: ?
__修改副本因子__: ?
__转储日志片段__: 该工具可以让你在不读取捎息的情况下查看消息的内容。
```bash
# 解码日志片段 00000000000052368601. log ，显示消息的概要信息。
kafka-run-class.sh kafka.tools.DumpLogSegments --files 00000000000052368601.log
# --index-sanity-check 会检查无用的索引, --verify-index-only 将会检查索引的匹配度，但不会打印出所有的索引.
# 验证日志片段 00000000000052368601.log 索引文件的正确性。
kafka-run-class.sh kafka.tools.DumpLogSegments --files 00000000000052368601.index,00000000000052368601.log
```
__副本验证__: 可以使用 kafka-replica-verification.sh 工具来验证集群分区副本的一致性。
```bash
# 对 broker1 和 broker2 上以 my- 开头的主题副本进行验证。
kafka-replica-verification.sh --broker-list kafka1.example.com:9092,kafka2.example.com:9092 --topic-white-list 'my-.*'
```

`153 9.5`: __消费和生产__:
__控制台消费者__: 如果使用了新版本的消费者, 必须使用 `--new-consumer` 和 `--broker-list`
可以把消费者的其他配置参数传给控制台消费者, 可以通过 `--consumer.config CONFIGFILE` 指定配置文件, 或者通过 `--consumer-property KEY=VALUE` 的格式传递一个或多个参数.
```bash
# 读取一个主题 my-topic (旧版本)
Kafka-console-consumer.sh --zookeeper zoo1.example.com:2181/kafka-cluster --topic my-topic

# kafka-console-consumer.sh 参数
#   指定消息格式化器, 用于解码消息, 默认 kafka.tools.DefaultFormatter
--formatter CLASSNAME
#   指定从最旧的偏移量开始读取数据, 否则就从最新的偏移量开始读取
--from-beginning
#   指定在退出之前最多读取 NUM 个消息
--max-messages NUM
#   指定只读取 ID 为 NUM 的分区
--partition NUM
```
读取偏移量主题: 我们需要知道提交的消费者群组偏移量是多少，比如某个特定的群组是否在提交偏移量，或者偏移量提交的频度。可以通过控制台消费者读取一个特殊的内部主题 `__consumer_offsets` 来实现, 所有消费者的偏移量都以消息的形式写到这个主题上, 为了解码此主题消息, 需要使用 `kafka.coordinator.GroupMetadataManager$OffsetsMessageFormatter` 这个格式化器.
```bash
# 从偏移量主题读取一个消息 (旧版本)
Kafka-console-consumer.sh --zookeeper zoo1.example.com:2181/kafka-cluster --topic __consumer_offsets --formatter 'kafka.coordinator.GroupMetadataManager$OffsetsMessageFormatter' --max-messages 1
```
__控制台生产者__: 该工具将命令行输入的每一行视为一个消息，消息的键和值以 Tab 字符分隔 (如果没有出现 Tab 字符，那么键就是 null)
可以把生产者的其他配置参数传给控制台生产者, 可以通过 `--producer.config CONFIGFILE` 指定配置文件, 或者通过 `--producer-property KEY=VALUE` 的格式传递一个或多个参数.
```bash
# 向主题 my-topic 生成两个消息.
kafka-console-producer.sh --broker-list kafka1.example.com:9092,kafka2.example.com:9092 --topic my-topic

# kafka-console-producer.sh 参数
#   指定消息键的编码器类名, 默认是 kafka.serializer.DefaultEncoder
--key-serializer CLASSNAME
#   指定消息值的编码器类名，默认是 kafka.serializer.DefaultEncoder
--value-serializer CLASSNAME
#   指定生成消息所使用的压缩类型，可以是 none, gzip, snappy, lz4, 默认值是 gzip
--compression-codec STRING
#   确定以同步的方式生成消息, 也就是说, 在发送下一个消息之前会等待消息得到确认
--sync
```

`157 9.6`: __客户端ACL__:
除了以下内容以外，不要直接修改 Zookeeper 的其他任何信息，一定要小心谨慎，因为这些操作都是很危险的。
移动集群控制器, 取消分区重分配, 移除待删除的主题, 手动删除主题.

---

# 十. 监控 Kafka

`160 10.1`: __度量指标基础__:
__度量指标在哪里__: Kafka 提供的所有度量指标都可以通过 Java Management Extensions (JMX ）接口 来访问: Nagios XI checkjmx 插件 / jmxtrans / Jolokia / MX4J.

`162 10.2`: __broker 的度量指标__:
`10.2.1`: __非同步分区 (重要)__: 该度量指明了作为 Leader 的 broker 有多少个分区处于非同步状态。当它的值大于零时，就应该想办怯采取相应的行动。
JMX MBean -> kafka.server:type=ReplicaManager,name=UnderReplicatedPartitionis
区间值 -> 非负整数
如果集群里多个 broker 的非同步分区数量一直保持不变，那说明集群中的某个 broker 已经离线了。整个集群的非同步分区数量等于离线 broker 的分区数量，而且离线 broker 不会生
成任何度量指标。
如果非同步分区的数量是被动的，或者虽然数量稳定但并没有 broker 离线，说明集群出现了性能问题。
如果非同步分区属于单个 broker ，那么这个 broker 就是问题的根源，表象是其他broker 无告从它那里复制消息。
如果多个 broker 都出现了非同步分区， 那么有可能是集群的问题，也有可能是单个 broker的问题。
```bash
# 列出集群的非同步分区
kafka-topics.sh --zookeeper zoo1.example.com:2181/kafka-cluster --describe --under-replicated
```
__集群级别的问题__: 集群问题一般分为: 不均衡的负载, 资源过度消耗。
Broker 分区  Leader  流入字节   流出字节
1      100  50      3.56MB/s  9.45MB/s
2      101  49      3.66MB/s  9.25MB/s
3      100  50      3.23MB/s  9.82MB/s
所有的 broker 几乎处理相同的流量。假设在运行了默认的副本选举之后，些度量指标出现了很大的偏差，那说明集群的流量出现了不均衡。

操作系统级别指标: CPU 使用 & 网络输入吞吐量 & 网络输出吞吐量 & 磁盘平均等待时间 & 磁盘使用百分比. 任何一种资源出现过度消耗，都会表现为分区的不同步。

__主机级别的问题__: 如果性能问题不是出现在整个集群上，而是出现在一两个 broker 里，那么就要检查 broker 所在的主机。
主机级别的问题一般分为: 硬件问题 (磁盘 / 网络 / CPU / 内存) & 进程冲突 & 本地配置的不一致 (推荐使用配置管理工具如: Chef 或 Puppet).
能够导致 Kafka 性能衰退的一个比较常见的硬件问题是磁盘故障。生产者的性能与磁盘的写入速度有直接关系。会影响生产者和复制消息的性能问题, 而复制消息的性能问题会导致分区不同步.

`166 10.2.2`: __broker度量指标__:
__活跃控制器数量__: 该指标表示 broker 是否就是当前的集群控制器，其值可以是 `1`或 `0`。如果是 `1` ，表示 broker 就是当前的控制器。任何时候，都应该只有一个 broker 是控制器，而且这个 broker
必须一直是集群控制器。
JMX MBean -> kafka.controller:type=KafkaController,name=ActiveControllerCount
值区间 -> 0 或 1
__请求处理器空闲率__: Kafka 使用了两个线程地来处理客户端的请求：网络处理器线程池和请求处理器线程池。网络处理器线程地负责通过网络读入和写出数据。
JMX MBean -> kafka.controller:type=KafkaRequestHandlerPool,name=RequestHandlerAvgIdlePercent
值区间 -> 从 0 到 1 的浮点数 (包括 1)
请求处理器平均空闲百分比这个度量指标表示请求处理器空闲时间的百分比。数值越低，说明 broker 的负载越高。经验表明，如果空闲百分比低于 20% ，说明存在潜在的问题，如果低于 10% ，说明出现了性能问题。
__主题流入字节__: 该指标可以用于确定何时该对集群进行扩展或开展其他与规模增长相关的工作。它也可以用于评估一个 broker 是否比集群里的其他 broker 接收了更多的流量, 如果出现了这种情况，就需要对分区进行再均衡。
JMX MBean -> kafka.controller:type=BrokerTOpicMetrics,name=BytesInPerSec
值区间 -> 速率为双精度浮点数, 计数为整数 (b/s)
除了速率属性外，速率指标还有一个 Count 属性，代表了从 broker 启动以来接收到流量的字节总数。
__主题流出字节__: 流出字节速率显示的是消费者从 broker 读取消息的速率。流出速率也包括副本流量。
JMX MBean -> kafka.controller:type=BrokerTopicMetrics,name=BytesOutPerSec
值区间 -> 速率为双精度浮点数, 计数为整数
__主题流入的消息__: 以每秒生成消息个数的方式来表示流量，而且不考虑消息的大小。
JMX MBean -> kafka.controller:type=BrokerTopicMetrics,name=MessagesInPerSec
值区间 -> 速率为双精度浮点数, 计数为整数
__分区数量__: 它是指分配给 broker 的分区总数。包括 Leader 和 Follower.
JMX MBean -> kafka.controller:type=ReplicaManager,name=PartitionCount
值区间 -> 非负整数
__首领数量__: 该度量指标表示 broker 拥有的首领分区数量。我们需要对该指标进行周期性地检查，井适时地发出告警，即使在副本的数量和大小看起来都很完美的时候，它仍然能够显示出集群的不均衡问题。
JMX MBean -> kafka.controller:type=ReplicaManager,name=LeaderCount
值区间 -> 非负整数
可以使用该指标与分区数量一起计算出 broker Leader 分区的百分比, 一个均衡的集群, 如果副本因子为 2, 那么所有 broker 应该都为 50% 左右的 分区的 Leader, 如果副本因子为 3, 那么为 30% 左右.
__离线分区__: 显示了集群里没有首领的分区数量。
发生这种情况主要有两个原因: 包含分区副本的所有 broker 都关闭了。由于消息数量不匹配，没有同步副本能够拿到首领身份（井且禁用了不完全首领选举）。
JMX MBean -> kafka.controller:type=KafkaController,name=OfflinePartitionsCount
值区间 -> 非负整数
在一个生产环境 Kafka 集群里，离线分区会影响生产者客户端，导致消息丢失，或者造成回压。这属于“站点去机”问题，需要立即解决。
__请求度量指标__: Kafka 协议，它有多种不同的请求，每种请求都有相应的度量指标, 每一种请求类型都有 8 个度量指标，它们分别体现了不同请求处理阶段的细节。
这些指标都是自 broker 启动以来开始计算的，所以在查看那些长时间没有变化的度量指标时，请记住： broker 代理运行的时间越长，数据就越稳定。
```bash
# 不同的请求
CreateTopics, DeleteTopics, DescribeGroups, Fetch, Heartbeat ...

# 8 个度量指标
# Total Time 表示 broker 花在处理请求上的时间，从收到请求开始计算，直到将响应返回给请求者
Total Time -> kafka.network:type=RequestMetrics,name=TotalTimeMs,request=Fetch
# 表示请求停留在队列里的时间，从收到请求开始计算，直到开始处理请求。
Request Queue Time -> kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request=Fetch
# 表示首领分区花在处理请求上的时间，包括把消息写入磁盘（但不一定要冲刷）
Local Time
# 表示在请求处理完毕之前，用于等待跟随者的时间。
Remote Time
# 表示暂时搁置响应的时间，以便拖慢请求者，把它们限定在客户端的配额范围内。
Throttle Time
# 表示响应被发送给请求者之前停留在队列里的时间。
Response Queue Time
# 表示实际用于发送响应的时间。
Response Send Time
# 速率指标: 表示在单位时间内收到并处理的请求个数。
Requests Per Second

# 每个度量指标的属性如下。
百分位: 50thPercentile, 75thPercentile, 95thPercentile, 98thPercentile, 99thPercentil, 999thPercentile
Count: 从 broker 启动至今处理的请求数量。
Min: 所有请求的最小值。
Max: 所有请求的最大值。
Mean: 所有请求的平均值.
StdDev: 整体的请求事件标准偏差.
```
我们至少要收集 Total Time 和 Requests Per Second 的平均值及较高的百分位（99% 或 99.9%），这样就可以获知发送请求的整体性能。

`173 10.2.3`: __主题和分区的度量指标__:
__主题实例的度量指标__:
Bytes in rate -> 
Bytes out rate ->
...
__分区实例的度量指标__: 
Partition size ->
Log segment count ->
Log end offset ->
Log start offset ->

`174 10.2.4`: __Java虚拟机监控__: 如果 JVM 频繁发生垃圾回收，就会影响 broker 的性能，在这种情况下，就应该得到告警。
__垃圾回收__: 对于 JVM 来说，最需要监控的是垃圾回收（GC）的状态。如果 JRE 使用 Oracle Java 1.8 并使用了 G1 垃圾回收器，那么需要监控:
Full GC cycles -> java.lang:type=GarbageCollector,name=G1 Old Generation
Young GC cycles -> java.lang:type=GarbageCollector,name=G1 young Generation
我们需要监控这两个指标的 `CollectionCount` (表示从 JVM 启动开始算起的垃圾回收次数), `CollectionTime` (表示从 JVM 启动开始算起的垃圾回收时间, 以 ms 为单位).
__Java 操作系统监控__: JVM 设置 java.lang:type=OperatingSystem, MaxFileDescriptorCount 表示 JVM 打开的文件描述符的最大值, OpenFileDescriptorCount 表示目前已经打开的文件描述符数量. 如果网络连接不能正常关闭, 很亏就会把文件描述符用完.

`175 10.2.5`: __操作系统监控__: 用户需要监控 CPU 的使用, 内存的使用, 磁盘的使用, 磁盘 IO 和网络的使用情况。
CPU: 系统负载: broker 在处理请求时使用了大量的 CPU;
内存: 运行 Kafka 不需要太大的内存; 它会使用堆外的一小部分内存来实现压缩功能, 其余大部分内存则用于缓存。可以通过监控总内存空间和可用交换内存空间来确保内存交换空间不会被占用。
磁盘: 最重要, Kafka 的性能严重依赖磁盘的性能。我们需要监控磁盘的每秒种读写速度、读写平均队列大小、平均等待时间和磁盘的使用百分比.
网络: 就是指流入和流出的网络流量，一般使用 b/s 来表示。在没有消费者时，1 个流入比特对应 1 个或多个流出比特，这个数字与主题的复制系数相等。根据消费者的实际数量，流入流量很容易比输出流量高出一个数量级.

`176 10.2.6`: __日志__:
`kafka.controller`: 日志用于记录集群控制器的信息, 集群中只有一个 broker 会使用此日志. 包含主题的创建和修改操作, broker 状态的变更, 集群的活动.
`kafka.server.ClientQuotaManager`: 记录生产和消费配额活动相关.
`kafka.request.logger`: 发送给 broker 的每一个请求的详细信息. (数据量大, 所以如果不是出于调试的目的，不建议启用这个日志)
`kafka.log.LogCleaner`, `kafka.log.Cleaner`, `kafka.log.LogCleanerManager`: 压缩相关.

`177 10.3`: __客户端监控__:
`10.3.1`: __生产者度量指标__:
Overall producer -> kafka.producer:type=producer-metrics,client-id=CLIENTID
Per-broker -> kafka.producer:type=producer-node-metrics,client-id=CLIENTID,node-id=node-BROKERID
Per-topic -> kafka.producer:type=producer-topic-metrics,client-id=CLIENTID,topic=TOPICNAME
上面的每一个 MBean 都有多个属性用于描述生产者的状态。最重要的属性为:
- 生产者整体度量指标:
  `record-error-rate`: 如果大于 0, 说明生产者正在丢弃无法发送的消息. 生产者配置了重试次数, 如果达到上限, 就会丢失.
  `record-retry-rate`:
  `request-latency-avg`: 表示生产者请求到 broker 需要的平均时间.
  `outgoing-byte-rate`: 每秒钟消息的字节数.
  `record-send-rate`: 每秒钟消息的数量.
  `request-rate`: 每秒钟生产者发送给 broker 的请求数.
  `request-size-avg`: 生产者发送请求的平均字节数.
  `batch-size-avg`: 表示单个消息批次的平均字节数.
  `records-per-request-avg`: 在生产者的单个请求里所包含的消息平均个数.
  `record-queue-time-avg`: 表示消息在发送给 Kafka 之前在生产者客户端等待的平均毫秒数.
     以下两种情况都会促使生产者客户端关闭当前批次，然后把它发送给 broker (调用 send 方法):
       - 生产者客户端有足够多的悄息来填充批次 (根据 `max.partition.bytes` 的配置)
       - 距离上 次发送批次已经有足够长的时间（根据 `linger.ms` 的配置)
     `record-queue-time-avg` 用于度量生产消息所使用的时间, 因此，可以通过调优上述两个参数来满足应用程序的延迟需求。
- Per-broker 和 Per-topic 度量指标:
  在调试问题时，这些度量会很有用，但不建议对它们进行常规的监控。
  与 broker 实例的度量指标一样，主题实例的度量指标一般用于诊断问题。

`179 10.3.2`: __消费者度量指标__:
Overall consumer -> kafka.consumer:type=consumer-metrics,client-id=CLIENTID
Fetch manager -> kafka.consumer:type=consumer-fetch-manager-metrics,client-id=CLIENTID
Per-topic
Per-broker
Coordinator
- Fetch Manager 度量指标:
   `fetch-latency-avg`: 表示从消费者向 broker 发送请求所需要的时间。如果主题有相对稳定和足够的消息流量，那么查看这个指标或许会更有意义。
   `bytes-consumed-rate` / `records-consumed-rate`: 分别表示客户端每秒读取的消息字节数和每秒读取的消息个数。设置最小值告警阈值。
   `fetch-rate`: 表示消费者每秒发出请求的数量.
   `fetch-size-avg`: 表示这些请求的平均字节数.
   `records-per-request-avg`: 每个请求的平均消息个数.
- Per-broker 和 Per-topic 度量指标:
   `request-latency-avg`:
   `imcoming-byte-rate` / `request-rate`: 分别表示 broker 每秒读取的消息字节数和每秒请求数.
- Coordinator 度量指标:
   `sync-time-avg`: 表示同步活动所使用的平均毫秒数.
   `sync-rate`: 表示每秒钟群组发生的同步次数.
   `commit-latency-avg`: 提交偏移量所需要的平均时间.
   `assigned-partitions`: 表示分配给消费者客户端的分区数量.

`10.3.3`: __配额__: Kafka 可以对客户端的请求进行限流，防止客户端拖垮整个集群。
对于消费者和生产者客户端来说，这都是可配的，可以使用每秒钟允许单个客户端访问单个 broker 的流量字节数来表示. 当 broker 发现客户端的流量已经超出配额时，它就会暂缓向客户端返回响应，等待足够长的时间，直到客户端流量降到配额以下.
消费者 -> kafka.consumer:type=consumer-fetch-manager-metrics,client=CLIENTID 的属性 fetch-throttle-time-avg
生产者 -> kafka.producer:type=producer-metrics,client-id=CLIENTID 的属性 producer-throttle-time-avg
默认情况下， broker 不会开启配额功能。

`182 10.4`: __延时监控__: 对于消费者来说，最需要被监控的指标是消费者的延时。它表示分区最后一个消息和消费者最后读取的消息之间相差的消息个数。
监控消费者延时最好的办也是使用外部进程，它能够观察 broker 的分区状态，跟踪最近消息的偏移量，也能观察悄费者的状态 ，跟踪消费者提交的最新偏移量。
可以使用 Burrow 来完成这项工作。 (Burrow 是一个开源的应用程序，最初由 Linkedln 开发。)

`183 10.5`: __端到端监控__: Kafka Monitor (该工具由 Linkedln Kafka 团队开发并开源)

---
