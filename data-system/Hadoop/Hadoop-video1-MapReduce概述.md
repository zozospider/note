


---

# 一 MapReduce 定义

MapReduce 是一个分布式运算程序的编程框架, 是用于开发基于 Hadoop 的数据分析应用的核心框架.

MapReduce 核心功能是将用户编写的业务逻辑代码和自带默认组件整合成一个完整的分布式运算程序, 并发运行在一个 Hadoop 集群上.

---

# 二 MapReduce 优缺点

## 2.1 优点

- __MapReduce 易于编程__: 它简单的实现一些接口, 就可以完成一个分布式程序. 这个分布式程序可以分布到大量廉价的 PC 机器上运行. 也就是说写一个分布式程序跟写一个简单的串行程序是一样的, 就是因为这个特点使得 MapReduce 编程变得非常流行.

- __良好的扩展性__: 当计算资源不能得到满足的时候, 可以通过简单的增加机器来扩展它的计算能力.

- __高容错性__: MapReduce 设计的初衷就是使程序能够部署在廉价的 PC 机器上, 这就要求它具有很高的容错性. 比如其中一台机器挂了, 它可以把上面的计算任务转移到另一个节点上运行, 从而不至于这个任务失败. 而且这个过程不需要人工参与, 而完全是由 Hadoop 内部完成的.

- __适合 PB 级以上海量数据的离线处理__: 可以实现上千台服务器集群并发工作, 提供数据处理能力.

## 2.2 缺点

- __不擅长实时计算__: MapReduce 无法像 MapSQL 一样, 在毫秒或者秒级内返回结果.

- __不擅长流式计算__: 流式计算的输入数据是动态的, 而 MapReduce 的输入数据集是静态的, 不能动态变化. 这是因为 MapReduce 自身的设计特点决定了数据源必须是静态的.

- __不擅长 DAG (有向图) 计算__: 多个应用程序存在依赖关系, 后一个应用程序的输入作为前一个的输出. 在这种情况下, MapReduce 并不是不能做, 而是使用后, 每个 MapReduce 作业的输出结果都会写入到磁盘, 会造成大量的磁盘 IO, 导致性能非常的地下.

---

# 三 MapReduce 核心思想

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A6%82%E8%BF%B0/MapReduce%E6%A0%B8%E5%BF%83%E7%BC%96%E7%A8%8B%E6%80%9D%E6%83%B3.png?raw=true)

---

# 四 官方 WordCount 源码

`TODO`: 反编译官方 jar 包中的 WordCount 案例.

---

# 五 常用数据序列化类型

| Java 类型 | Hadoop Writable 类型 |
| :--- | :--- |
| boolean | BooleanWritable |
| byte | ByteWritable |
| int | IntWritable |
| float | FloatWritable |
| long | LongWritable |
| double | DoubleWritable |
| String | Text |
| map | MapWritable |
| array | ArrayWritable |

---

# 六 MapReduce 进程

一个完整的 MapReduce 程序在分布式运行时有 3 个实例进程:
- `MrAppMaster`: 负责整个程序的过程调度及状态协调.
- `MapTask`: 负责 Map 阶段的整个数据处理流程.
- `ReduceTask`: 负责 Reduce 阶段的整个数据处理流程.

---

# 七 MapReduce 编程规范

---

# 八 WordCount 案例实操

---
