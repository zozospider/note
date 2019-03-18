
# Document & Code

- page
  - 数据系统基础
    - [可靠可拓展与可维护的应用系统](https://github.com/zozospider/note/blob/master/distributed/Data-Intensive/Data-Intensive-book1cn-可靠可拓展与可维护的应用系统.md)
    - [数据模型与查询语言](https://github.com/zozospider/note/blob/master/distributed/Data-Intensive/Data-Intensive-book1cn-数据模型与查询语言.md)
    - [数据存储与检索](https://github.com/zozospider/note/blob/master/distributed/Data-Intensive/Data-Intensive-book1cn-数据存储与检索.md)
    - [数据编码与演化](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/Data-Intensive-book1cn-数据编码与演化.md)
  - 分布式数据系统
    - [数据复制](https://github.com/zozospider/note/blob/master/distributed/Data-Intensive/Data-Intensive-book1cn-数据复制.md)
    - [数据分区](https://github.com/zozospider/note/blob/master/distributed/Data-Intensive/Data-Intensive-book1cn-数据分区.md)
    - [事务](https://github.com/zozospider/note/blob/master/distributed/Data-Intensive/Data-Intensive-book1cn-事务.md)
    - [分布式系统的挑战](https://github.com/zozospider/note/blob/master/distributed/Data-Intensive/Data-Intensive-book1cn-分布式系统的挑战.md)
    - [一致性与共识](https://github.com/zozospider/note/blob/master/distributed/Data-Intensive/Data-Intensive-book1cn-一致性与共识.md)
  - 派生数据
    - [批处理系统](https://github.com/zozospider/note/blob/master/distributed/Data-Intensive/Data-Intensive-book1cn-批处理系统.md)
    - [流处理系统](https://github.com/zozospider/note/blob/master/distributed/Data-Intensive/Data-Intensive-book1cn-流处理系统.md)
    - [数据系统的未来](https://github.com/zozospider/note/blob/master/distributed/Data-Intensive/Data-Intensive-book1cn-数据系统的未来.md)

- book
  - [数据密集型应用系统设计](https://www.amazon.cn/dp/B07HGH8153)

---

# 前言

从事服务端或者后台系统软件开发的软件从业者, 近年来一定被层出不穷的商业名词所包围: NoSQL, Big Data, Web-scale, Sharding, Eventual consistency, ACID, CAP 理论, 云服务, MapReduce 和 Real-time 等, 所有这些其实都围绕着如何构建高效存储与数据处理这一核心主题.

什么算是 __数据密集型__ (data-intensive) 呢? 对于一个应用系统, 如果 __数据__ 是其成败决定性因素, 包括数据的规模, 数据的复杂度或者数据产生与变化的速率等, 我们就可以称为 __数据密集型应用系统__; 与之对应的是 __计算密集型__ (Compute-Intensive), CPU 主频往往是后者最大的制约瓶颈.

