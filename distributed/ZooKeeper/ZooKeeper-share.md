
# 大纲

`ZooKeeper 原理与实践`

- 分布式系统特点
  - 集中式 & 分布式
  - ACID & CAP
  - Paxos 算法 & ZAB 协议
- ZooKeeper 核心特性
  - 数据与存储
    - 数据结构 & 节点特性 & 版本 & ACL
    - Watcher
    - 事务日志 & 快照
    - 初始化 & 数据同步
  - 角色, 通信, 会话
    - 角色 (Leader, Follower, Observer)
    - 通信协议 (Jute)
    - 会话 (会话状态, 会话创建, 会话管理, 会话清理, 重连)
  - Leader 选举
  - 事务处理
- ZooKeeper 使用和运维
  - 服务端 (单机 & 伪分布式 & 分布式)
  - 客户端 (shell & 四字命令 & Java API (ZooKeeper & Curator & ZKClient))
  - JMX & 监控
  - 数据 & 连接 & 磁盘
- ZooKeeper 应用场景
  - 数据发布/订阅 & 负载均衡 & 命名服务 & 分布式协调/通知 & 集群管理 & Master 选举 & 分布式锁 & 分布式队列
  - Haoop & HBase & Kafka & Dubbo & JStorm
  - Configurator

---

# 书

- [ZooKeeper Distributed Process Coordination](https://t.hao0.me/files/zookeeper.pdf)

# Paxos 算法

- [如何浅显易懂地解说 Paxos 的算法?](https://www.zhihu.com/question/19787937)
- [paxos 算法](https://www.processon.com/view/59c2295ae4b0bc4fef8a436d)
- [数据一致性与 Paxos 算法](https://my.oschina.net/fileoptions/blog/1825760)
