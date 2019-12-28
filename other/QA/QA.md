
# Kafka

- Kafka 中一个 topic (主题) 的 partition (分区) 数是否可以大于 broker (机器) 数量?

- Kafka 中一个 topic (主题) 的 replication (副本) 数是否可以大于 broker (机器) 数量?

- 为什么 Kafka 可以实现高吞吐? 单节点的吞吐量也比其他消息队列大, 为什么?

零拷贝, 预读, 后写, 分段日志, 批处理, 压缩.

- Kafka 的偏移量存放在哪, 为什么?

ZooKeeper / Kafka Cluster / 自定义

- Kafka 里面用什么方式消费数据, 拉还是推?

拉: poll

- 如何保证数据不会出现丢失或者重复消费的情况? 做过哪些预防措施, 怎么解决以上问题?

生产者丢失: 同步发送 / ack = -1
消费者重复消费: 自己维护 offset

- Kafka 元数据存在哪里?

ZooKeeper: /cluster, /controller, /broker ...

- 为什么要使用 Kafka, 可不可以用 Flume 直接将数据放在 HDFS 上?

Flume 只是一个传输框架, 无法持久化数据. Flume 可能丢失数据. 消费者处理比较麻烦, 耦合性太强.

- Kafka 用的哪个版本?

2.1.0

- Kafka 如何保证不同的订阅源都收到相同的一份内容?

HW & LEO

- Kafka 中 Leader 的选举机制?

/controller, 集群控制器, 多个节点同时创建 /controller 临时节点, 先创建的为集群控制器, 集群控制器再从 ISR 中选出一个 Leader (一般为第 1 个).

- Kafka 或 Flume 运行机制及设计实现描述.

原理图.

- 如何增加 Kafka 的消费速度?

增加分区和消费者, 增加拉取数据的大小, 增大批处理大小.

- Kafka 原理, ISR 中什么情况下 brokerid 会消失?

副本 down 掉, 网络阻塞, `replica.lag.time.max.ms` (副本落后的最大毫秒数)

- 为什么用 Kafka, Kafka 是如何存储数据的?

Kafka 应用场景.

- Kafka 吞吐量怎么优化?

- Kafka 为什么读写效率高?

- Flume 和 Kafka 有序吗?

Flume 有序, Kafka 同一个分区有序.

- Flume 和 Kafka 的区别.

- Kafka 的 offset 如何维护?

- kafka 的结构?

- 如何保证数据的一致性?

- Kafka 的 offset, flume 的组成, 项目中为什么要用两层?

- 怎么解决 Kafka 的数据丢失?

- Kafka 控制台向 topic 生产数据的命令及控制台消费 topic 数据的命令.

- Sink 怎么将数据发送到 Kafka?

- 怎么手动维护 offset?

关闭自动提交. 调用 commitSync() / commitAsync().

