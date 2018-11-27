# Document & Code

* [../Zookeeper-video](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-video.md)

---

# 分布式系统的瓶颈以及 ZooKeeper 的相关特性

## ZooKeeper 的特性

ZooKeeper 具有以下特性：
* 一致性：数据一致性，数据按照顺序分批入库
* 原子性：事务要么成功，要么失败，不会局部化
* 单一视图：客户端连接集群中的任一 zk 节点，数据都是一致的
* 可靠性：每次对 zk 的操作状态都会保存在服务端
* 实时性：客户端可以读取到 zk 服务端的最新数据
