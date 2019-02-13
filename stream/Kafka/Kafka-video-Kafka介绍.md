
- [Document & Code](#document--code)

---

# Document & Code

- [../Kafka-video](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video.md)

---

# 一. 消息模式

## 1.1 点对点模式 (一对一模式)

消费者主动拉取数据, 收到消息后清除消息.

## 1.2 发布/订阅模式 (一对多模式)

数据生产后, 推送给所有订阅者.

# 二. 为什么需要消息队列

## 2.1 解耦

允许独立的扩展或修改两边的处理过程, 只要确保他们遵守同样的接口约束.

## 2.2 冗余

消息队列把数据进行持久化直到他们已经被处理完毕, 通过这一方式规避了数据丢失风险.

许多消息队列采用的 `插入-获取-删除` 范式中, 在把一个消息从队列中删除前, 需要你的处理系统明确的指出该消息已经被处理完毕 (确保数据被安全的保存直到使用完毕).

## 2.3 拓展性

因为消息队列解耦了你的处理过程, 所以增大消息入队和处理的频率是很容易的, 只要另外增加处理过程即可.

## 2.4 灵活性 & 峰值处理能力

使用消息队列能够使关键组件顶住突发的访问压力, 而不会因为突发的超负荷请求而完全奔溃 (参考 [Kafka-video-分布式原理: 分布式系统](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video-%E5%88%86%E5%B8%83%E5%BC%8F%E5%8E%9F%E7%90%86.md#%E4%BA%8C-%E5%88%86%E5%B8%83%E5%BC%8F%E7%B3%BB%E7%BB%9F)).

## 2.5 可恢复性

系统的一部分组件失败不会影响整个系统.

消息队列降低了进程间的耦合, 所以即使一个处理消息的进程挂掉, 加入队列中的消息任然可以在系统恢复后被处理.

## 2.6 顺序保证

在大多数使用场景下, 数据处理的顺序都很重要.

大部分消息队列本身就是排序的, 并且能保证数据会按照特定的顺序来处理 (Kafka 保证一个 Partition 内的消息的有序性).

## 2.7 缓冲

有助于控制和优化数据流经过系统的速度, 解决生产消息和消费消息的处理速度不一致的情况.

## 2.8 异步通信

很多时候, 用户不想也不需要立即处理消息.

消息队列提供了异步处理机制, 允许用户把一个消息放入队列, 但并不处理他. 想向队列中放多少消息就放多少, 然后在需要的时候再去处理他们.

# 三. 什么是 Kafka

在流式计算中, Kafka 一般用来缓存数据, Storm/Spark 通过消费 Kafka 的数据进行计算.

Kafka 是 Apache 软件基金会开发的由 Scala 编写的一个开源消息系统.

Kafka 最初是 LinkedIn 公司开发, 于 2011 年初开源, 2012 年 10 月从 Apache Incubator 毕业. 该项目的目标是为处理实时数据提供一个统一, 高通量, 低等待的平台.

Kafka 是一个分布式消息队列, Kafka 对消息保存时根据 `Topic` 进行归类, 消息发送者称为 `Producer`, 消息接受者称为 `Consumer`, 此外 Kafka 集群由多个 Kafka 实例 (Server) 组成, 每个实例称为 `Broker`.

无论是 Kafka 集群还是 Consumer, 都依赖 ZooKeeper 集群保存一些 meta 信息以保证系统的可用性.

# 四. Kafka 架构

以下为官方示例图:

![image](https://raw.githubusercontent.com/zozospider/note/master/stream/Kafka/Kafka-video-Kafka%E4%BB%8B%E7%BB%8D/kafka-apis.png)

以下为详细架构图:

![image](https://raw.githubusercontent.com/zozospider/note/master/stream/Kafka/Kafka-video-Kafka%E4%BB%8B%E7%BB%8D/Kafka%E8%AF%A6%E7%BB%86%E6%9E%B6%E6%9E%84.png)

