# Document & Code

* [../Zookeeper-book](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-book.md)

---

# 一 系统模型

## 1.1 数据模型

## 1.2 节点特性

## 1.3 版本 - 保证分布式数据原子性操作

## 1.4 Watcher - 数据变更的通知

## 1.5 ACL - 保障数据的安全

---

# 二 序列号与协议

## 2.1 Jute 介绍

## 2.2 使用 Jute 进行序列化

## 2.3 深入 Jute

## 2.4 通信协议

---

# 三 客户端

## 3.1 一次会话的创建过程

## 3.2 服务器地址列表

## 3.3 ClientCnxn: 网络 I/O

---

# 四 会话

## 4.1 会话状态

## 4.2 会话创建

## 4.3 会话管理

## 4.4 会话清理

## 4.5 重连

---

# 五 服务器启动

## 5.1 单机版服务器启动

## 5.2 集群版服务器启动

---

# 六 Leader 选举

## 6.1 Leader 选举概述

## 6.2 Leader 选举的算法分析

## 6.3 Leader 选举的实现细节

---

# 七 各服务器角色介绍

## 7.1 Leader

## 7.2 Follower

## 7.3 Observer

## 7.4 集群间消息通信

---

# 八 请求处理

## 8.1 会话创建请求

## 8.2 SetData 请求

## 8.3 事务请求转发

## 8.4 GetData 请求

---

# 九 数据与存储

## 9.1 内存数据

## 9.2 事物日志

## 9.3 snapshot - 数据快照

## 9.4 初始化

## 9.5 数据同步

---
