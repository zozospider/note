
监控.
结合生产实际.
ZooKeeper 目录结构.

最后要加上:
- 新方案 broker 个数, Topic 名称, Partition 个数, ack 等关键指标如何配置.
- 新方案补录方式.

期望功能: 
- 存储大量数据 - 可拓展
- 数据不能丢失 - 持久化, 容错性
- 快速生产和消费 - 低延迟, 高性能

原理图: Followes 节点就像普通的 consumer 那样从 Leader 节点那里拉去消息并保存在自己的日志文件中. (document: replication 章节)
ack 机制: in sync 同步副本定义 (ISR) 和数量配置等 (document: replication 章节)

ZooKeeper 结构: ZooKeeper 目录章节

调优: 磁盘: 固态硬盘 > 机械硬盘, 但合理使用机械硬盘也可以有不错的性能, 性价比高.

最重要的新的 Java 版本的 producer 配置
acks
compression
batch size
最重要的 consumer 配置是 fetch size。

监控:
- [Kafka监控工具汇总](https://juejin.im/post/5d5f62085188255d803faebb)
JmxTool Cruise-control

前面可以考虑加上:
传统消息队列模式, 列举其缺点, 再引入 Kafka.

4.2 持久化
不要害怕文件系统: 顺序写入和随机写入性能相差 6000 倍.
常量时间就够了: 这种架构的优点在于所有的操作复杂度都是O(1)

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
- [kafka之六：为什么Kafka那么快](https://www.cnblogs.com/duanxz/p/4705164.html)
- [KAFKA：如何做到1秒发布百万级条消息](http://rdc.hundsun.com/portal/article/709.html)
- [Why Is Kafka So Fast](http://searene.me/2017/07/09/Why-is-Kafka-so-fast/)
