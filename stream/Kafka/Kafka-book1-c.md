
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

---

