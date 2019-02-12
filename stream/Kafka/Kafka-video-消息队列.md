
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

![image]()

