
# Document & Code

- [../Kafka-video](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video.md)

---

# 一. Flume

## 1.1 缺点

Flume 具有以下缺点:
- a. __数据都保存在内存中, 容易丢失, 且数据无法长时间保留__.
- b. __增加消费者不容易__: 当 Flume 需要增加一个消费者, 读取 Flume 的完整数据时, 有以下两种方案:
  - b1. 新增一个 agent.
  - b2. 在已有 agent 上新增一个 source, channel, sink 通道.

以下为 b2 方案示例:

![image](https://raw.githubusercontent.com/zozospider/note/master/stream/Kafka/Kafka-video-%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97/Flume-%E5%A2%9E%E5%8A%A0%E6%B6%88%E8%B4%B9%E8%80%85.png)

# 二. 消息队列系统预期效果

为解决 Flume 作为消息队列的缺点, 需要一个新的消息队列系统, 以下为该新系统的预期效果:

![image](https://raw.githubusercontent.com/zozospider/note/master/stream/Kafka/Kafka-video-%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97/%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97%E7%B3%BB%E7%BB%9F%E9%A2%84%E6%9C%9F%E6%95%88%E6%9E%9C.png)

- 消息队列本身应该是分布式的, 以保证可用性和高吞吐量.
- 保存的数据为文件形式, 以保证数据不丢失.
- 数据文件存在副本, 以保证高可用.
- 生产者需要关联协调调度中心, 用于获取消息队列的集群地址.
- 生产者可以向集群中多台机器发送数据.
- 多个消费者可以消费集群中的数据.
- 消费者采用拉取模式, 从集群中拉取数据, 避免推模式下消费者的消费速率跟不上生产速率, 同时也保证了消息队列的独立性.
