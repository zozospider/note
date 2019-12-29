
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
__首选的 Leader 选举__: 


---

