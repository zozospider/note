
# 总览

## 大纲

`ZooKeeper 原理与实践`

- 分布式系统
  - 集中式 & 分布式 & ACID & CAP
  - 拜占庭将军 & Paxos (Multi-Paxos) 算法 & Raft 算法 & 一致性 Hash 算法 & ZAB 协议
- ZooKeeper 概念
  - 介绍
  - 数据与存储
    - 数据结构 & 节点特性 & 版本 & ACL & Watcher
    - 事务日志 & 快照
    - 初始化 & 数据同步
  - 角色, 通信, 会话
    - 角色 (Leader, Follower, Observer)
    - 通信协议 (Jute)
    - 会话 (会话状态, 会话创建, 会话管理 (bucket), 会话清理, 重连)
  - Leader 选举
  - 事务处理
    - 请求处理链
- ZooKeeper 概念 (二)
    - 介绍 & 特性
    - 数据结构 & 节点特性 & 版本 & ACL & Watcher
    - 事务日志 & 快照
    - 角色 (Leader, Follower, Observer)
    - 初始化 & 数据同步 & Leader 选举
    - 通信协议 (Jute) & 会话 (会话状态, 会话创建, 会话管理 (bucket), 会话清理, 重连)
    - 请求处理 & Watcher 触发
- ZooKeeper 使用
  - 服务端 (单机 & 伪分布式 & 分布式)
  - 客户端 (shell & 四字命令 & Java API (ZooKeeper & Curator & ZKClient))
  - JMX & 监控
  - 数据 & 连接 & 磁盘
- ZooKeeper 应用
  - shell 客户端
  - Java 客户端
  - 数据发布/订阅 & 负载均衡 & 命名服务 & 分布式协调/通知 & 集群管理 & Master 选举 & 分布式锁 & 分布式队列
  - Haoop & HBase & Kafka & Dubbo & JStorm
  - Configurator

## 参考资料

- 书
  - [ZooKeeper Distributed Process Coordination](https://t.hao0.me/files/zookeeper.pdf)

- 算法
  - [分布式理论(一) - CAP定理](https://juejin.im/post/5b26634b6fb9a00e765e75d1)
  - [比长狭还早，“不可能三角”的概念源来于此](http://www.sohu.com/a/297805900_100188881)
  - [区块链共识机制的演进](http://www.cnblogs.com/studyzy/p/8849818.html)
  - [分布式一致性与共识算法](https://draveness.me/consensus)
  - [分布式系统核心技术](https://yeasy.gitbooks.io/blockchain_guide/content/distribute_system/)
  - [分布式系列文章——Paxos算法原理与推导](http://linbingdong.com/2017/04/17/%E5%88%86%E5%B8%83%E5%BC%8F%E7%B3%BB%E5%88%97%E6%96%87%E7%AB%A0%E2%80%94%E2%80%94Paxos%E7%AE%97%E6%B3%95%E5%8E%9F%E7%90%86%E4%B8%8E%E6%8E%A8%E5%AF%BC/)
  - [raft算法与paxos算法相比有什么优势，使用场景有什么差异？](https://www.zhihu.com/question/36648084)
  - [Paxos lecture (Raft user study)](https://www.youtube.com/watch?v=JEpsBg0AO6o)
  - [漫画：什么是拜占庭将军问题？](https://blog.csdn.net/bjweimengshu/article/details/80222416)
  - [漫画：什么是分布式事务？](https://blog.csdn.net/bjweimengshu/article/details/79607522)
  - [如何浅显易懂地解说 Paxos 的算法?](https://www.zhihu.com/question/19787937)
  - [paxos 算法](https://www.processon.com/view/59c2295ae4b0bc4fef8a436d)
  - [数据一致性与 Paxos 算法](https://my.oschina.net/fileoptions/blog/1825760)
  - [分布式系统Paxos算法](https://www.jdon.com/artichect/paxos.html)
  - [Paxos Made Simple](https://www.microsoft.com/en-us/research/publication/paxos-made-simple/)
  - [一致性hash算法 - consistent hashing](https://blog.csdn.net/sparkliang/article/details/5279393)
  - [每天进步一点点——五分钟理解一致性哈希算法(consistent hashing)](https://blog.csdn.net/cywosp/article/details/23397179)

- 其他
  - [走向架构师之路](https://blog.csdn.net/cutesource/article/list/5)
  - [尴尬了！Spring Cloud微服务注册中心Eureka 2.x停止维护了咋办？【石杉的架构笔记】](https://juejin.im/post/5c7431f2f265da2db0739755)


---

# 分布式系统特点

# ACID & CAP

实现:
- 1.2 从 ACID 到 CAP
  - 1.2.1 ACID
  - 1.2.3 CAP 和 BASE 理论

# ZooKeeper 概念

## 介绍

分布式系统中的进程通信有两种选择: 直接通过网络进行信息交换, 或读写某些共享存储. ZooKeeper 使用共享存储模型来实现应用间的协作和同步原语.

提问: 进程间通信有哪几种方式?
- [进程间通信IPC (InterProcess Communication)](https://www.jianshu.com/p/c1015f5ffa74)
- [共享内存的优势](http://www.cnblogs.com/linuxbug/p/4882776.html)
- [Linux进程间通信的几种方式总结--linux内核剖析（七）](https://blog.csdn.net/gatieme/article/details/50908749)
- [目前linux进程间通信的常用方法是什么(pipe？信号量？消息队列？)?](https://www.zhihu.com/question/23995948)
- [semaphore和mutex的区别？](https://www.zhihu.com/question/47704079)

## 角色, 通信, 会话

session 周期: 图2-6

客户端重连: 图2-7

## Leader 选举

主节点竞选中可能的交错操作: 图4-1

# ZooKeeper 应用

## shell 客户端

讲解内容: shell 模拟分布式系统 (主从模式) 应用 ZooKeeper

实现:
- 2.4 一个主-从模式例子的实现(通过shell命令行截图讲解)
  - 2.4.1 主节点角色
  - 2.4.2 从节点, 任务和分配
  - 2.4.3 从节点角色
  - 2.4.4 客户端角色

## Java 客户端

讲解内容: Java 客户端模拟主从模式应用 ZooKeeper

实现:
- 4.4 主-从模式的例子(通过znode节点变化讲解)
  - 4.4.1 管理权变化
  - 4.4.2 主节点等待从节点列表的变化
  - 4.4.3 主节点等待新任务进行分配
  - 4.4.4 从节点等待分配新任务
  - 4.4.5 客户端等待任务的执行结果

分布式系统是一个硬件或软件组件分布在不同的网络计算机上, 彼此之间仅仅通过消息传递进行通信和协调的系统.
事务 (Transaction) 是由一系列对系统中数据进行访问与更新的操作所组成的一个程序执行逻辑单元 (Unit), 狭义上的事务特指数据库事务.
分布式事务是指事务的参与者, 支持事务的服务器, 资源服务器以及事务管理器分别位于分布式系统的不同节点之上. 通常一个分布式事务会涉及对多个数据源或业务系统的操作.
原子性（Atomicity）、一致性（Consistency）、隔离性（Isolation）和持久性（Durability）

指数据在多个副本之间能够保持一致的特性 (严格的一致性)
指系统提供的服务必须一直处于可用的状态, 每次请求都能获取到非错的响应 (不保证获取的数据为最新数据)
分布式系统在遇到任何网络分区故障的时候, 仍然能够对外提供满足一致性和可用性的服务, 除非整个网络环境都发生了故障.