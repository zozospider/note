
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


---
