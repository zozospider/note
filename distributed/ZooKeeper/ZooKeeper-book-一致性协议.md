# Document & Code

* [../Zookeeper-book](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-book.md)

---

# 一. 2PC与3PC

一致性协议与算法，2PC, 3PC, Paxos算法。

## 2PC

二阶段提交（Two-Phase-Commit），将事务分为两个阶段来处理：投票和执行。

核心是：每个事务先尝试后提交。（强一致性算法）

### 阶段1. 提交事务请求

提交事务请求分为如下3个步骤：
* a. 事务询问（向节点询问是否可以执行事务）
* b. 执行事务（各个节点执行事务，记录 Undo、Redo）
* c. 向协调者反馈响应（反馈 Yes / No）

### 阶段2. 执行事务提交

此阶段协调者根据阶段1的反馈，决定是否提交事务。包括两种个情况：

中断事务：
* a. 发送回滚请求（阶段1任何节点发送了 No，则协调者向所有节点发送 Rollback） 
* b. 事务回滚（节点收到 Rollback，利用阶段1记录的Undo执行回滚）
* c. 反馈回滚结果（节点向协调者发送 Ack）
* d. 中断事务（协调者收到所有 Ack 后中断事务）

执行事务：
* a. 发送事务提交（阶段1所有节点发送了 Yes，则协调者向所有节点发送 Commit）
* b. 事务提交（节点受到 Commit，执行提交）
* c. 反馈提交结果（节点向协调者发送 Ack）
* d. 完成事务（协调周收到所有 Ack 后完成事务）

## 3PC

# 二. Paxos算法

## 追本溯源

## Paxos理论的诞生

## Paxos算法详解

