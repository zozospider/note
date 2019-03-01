
# 总览

## 大纲

`ZooKeeper 原理与实践`

- 分布式系统特点
  - 集中式 & 分布式
  - ACID & CAP
  - Paxos 算法 & ZAB 协议
- ZooKeeper 概念
  - 介绍
  - 数据与存储
    - 数据结构 & 节点特性 & 版本 & ACL & Watcher
    - 事务日志 & 快照
    - 初始化 & 数据同步
  - 角色, 通信, 会话
    - 角色 (Leader, Follower, Observer)
    - 通信协议 (Jute)
    - 会话 (会话状态, 会话创建, 会话管理, 会话清理, 重连)
  - Leader 选举
  - 事务处理
- ZooKeeper 使用
  - 服务端 (单机 & 伪分布式 & 分布式)
  - 客户端 (shell & 四字命令 & Java API (ZooKeeper & Curator & ZKClient))
  - JMX & 监控
  - 数据 & 连接 & 磁盘
- ZooKeeper 应用
  - shell 模拟分布式系统 (主从模式) 应用 ZooKeeper
  - 数据发布/订阅 & 负载均衡 & 命名服务 & 分布式协调/通知 & 集群管理 & Master 选举 & 分布式锁 & 分布式队列
  - Haoop & HBase & Kafka & Dubbo & JStorm
  - Configurator

## 参考资料

- 书
  - [ZooKeeper Distributed Process Coordination](https://t.hao0.me/files/zookeeper.pdf)

- Paxos 算法
  - [如何浅显易懂地解说 Paxos 的算法?](https://www.zhihu.com/question/19787937)
  - [paxos 算法](https://www.processon.com/view/59c2295ae4b0bc4fef8a436d)
  - [数据一致性与 Paxos 算法](https://my.oschina.net/fileoptions/blog/1825760)
  - [分布式系统Paxos算法](https://www.jdon.com/artichect/paxos.html)
  
- 其他
  - [尴尬了！Spring Cloud微服务注册中心Eureka 2.x停止维护了咋办？【石杉的架构笔记】](https://juejin.im/post/5c7431f2f265da2db0739755)


---

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

session 周期
