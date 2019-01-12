
# 大纲

- 分布式系统特点
  - 集中式 & 分布式
  - ACID & CAP
  - Paxos 算法 & ZAB 协议
- ZooKeeper 核心特性
  - 数据与存储
    - 数据结构
    - 节点特性 & 版本 & Watcher & ACL
    - 事务日志 & 快照
    - 初始化 & 数据同步
  - 会话
    - 会话管理
    - 会话清理
  - Leader 选举
  - 事务处理
- 使用和运维
  - 服务端 (单机 & 伪分布式 & 分布式)
  - 客户端 (shell & Java API (ZooKeeper & Curator))
  - JMX 监控
- 应用场景
  - 数据发布/订阅 & 负载均衡 & 命名服务 & 分布式协调/通知 & 集群管理 & Master 选举 & 分布式锁 & 分布式队列
  - Haoop & HBase & Kafka
  - Dubbo & JStorm
  - configure
