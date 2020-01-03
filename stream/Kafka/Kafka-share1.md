
监控.
结合生产实际.
ZooKeeper 目录结构.

高性能读写: 批量 & 压缩
高性能写: 分段日志 & 顺写
高性能读: 索引 & 预读 & 零拷贝

`7`: Kafka 为数据管道带来的主要价值在于，它可以作为数据管道各个数据段之间的大型缓冲区, 有效地解耦管道数据的生产者和消费者。
Flume 就是生产者和消费者的耦合性太强, 导致上游到下游到每个节点都相互影响.

`7.4`: 几乎所有的流式处理框架都具备从 Kafka 读取数据并将数据写入外部系统的能力。

- [理解JMX之介绍和简单使用](https://blog.csdn.net/lmy86263/article/details/71037316)

测试:
  - 同等配置, 单个进程.
  - 日志量太大, Flume 读取失败怎么办?

zero-copy: `transferTo()`;
文件顺序写入: `preallocate`
- [Kafka源码系列之kafka如何实现高性能读写的](https://cloud.tencent.com/developer/article/1032487)
- [Kafka源码分析-Server-日志存储(1](https://www.jianshu.com/p/107ea6311eae)
- [为什么Kafka速度那么快](https://www.cnblogs.com/binyue/p/10308754.html)
