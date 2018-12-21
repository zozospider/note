# Document & Code

* [../Zookeeper-book](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-book.md)

---

# 一. 典型应用场景及实现

## 数据发布/订阅

发布/订阅系统一般有两种模式：
* 推（Push）模式：服务端主动将数据更新发送给所有订阅的客户端。
* 拉（Pull）模式：客户端主动请求服务端获取最新数据，通常采用定时轮询拉取方式。

ZooKeeper 采用推（Push）拉（Pull）结合。客户端向服务端注册关注的节点，一旦该节点变化，服务端会向注册客户端发送 Watcher 事件通知（推），客户端收到通知后，需要主动到服务端获取最新数据（拉）。

数据一般为机器列表，运行时的开关配置，数据库配置等全局配置。具有以下特性：
* 数据量较小
* 数据在运行时会发生变化
* 集群中配置一致，各机器共享

### ZooKeeper 应用示例

该示例为 `数据库切换` 场景，客户端从 ZooKeeper 获取数据库配置，在 ZooKeeper 的数据库配置发生变化的时候，客户端需要作出相应更新。

1. 配置存储

一般存储在 ZooKeeper 的 `/configer/app1/database_config` 节点，内容如下：
```properties
#DBCP
dbcp.driverClassName=com.mysql.jdbc.Driver
dbcp.dbJDBCUrl=jdbc:mysql://1.1.1.1:3306/app1
dbcp.characterEncording=utf-8
dbcp.username=xiaoming
dbcp.password=123456
dbcp.maxActive=30
dbcp.maxIdle=10
dbcp.maxWait=10000
```

2. 配置获取

集群中每台机器在启动阶段，首先从 ZooKeeper 读取数据库配置，同时，在该节点上注册一个数据变更的 Watcher 监听。

3. 配置变更

数据库配置变更后，对 ZooKeeper 配置节点内容进行更新。此时 ZooKeeper 会将变更通知发送到注册的客户端，客户端接收到通知后，重新获取最新数据。

## 负载均衡

用来对多个计算机、网络连接、CPU、磁盘驱动或其他资源进行分配负载，以达到优化资源使用、最大化吞吐率、最小化响应时间和避免过载的目的。分为硬件负载均衡和软件负载均衡。

### ZooKeeper 应用示例

该示例为 `自动化 DNS 服务` 场景。

1. 域名注册

每个服务在启动时，会把自己的域名信息注册到 Register 服务中。例如 A 机器提供 serviceA.xxx.com 服务，它就通过一个 Rest 请求向 Register 发送一个请求: "serviceA.xxx.com -> 192.168.0.1:8080"。

Register 服务获取到数据后，将其写入到 ZooKeeper 对应节点上。

2. 域名解析

客户端在使用该域名时，会向 Dispatcher 发出域名解析请求。

Dispatcher 服务收到请求后，会从 ZooKeeper 指定节点上获取对应 IP:PORT 数据列表，通过一定策略选取其中一个返回给前端应用。

## 命名服务

被命名的实体可以是集群中的机器、提供的服务地址或远程对象等，统称为名字（Name）。比如 RPC 中的服务地址列表，通过使用命名服务，客户端能够根据名字获取资源的实体、服务地址和提供者信息。

### ZooKeeper 应用实例

该示例为 `全局唯一 ID` 场景。

以下为使用 ZooKeeper 生成唯一 ID 的步骤：

1. 所有客户端都会根据自己的任务类型，通过 create() 创建一个顺序节点，例如 `job-` 节点。

2. 因为每个数据节点都可以维护一份子节点的顺序序列，自动以后缀的形式添加一个序号。所以，创建完毕后，create() 接口会返回一个完整的节点名，如 `job-0000000003`。

3. 客户端拿到返回值后，拼接上 type 类型，如 `type2-job-0000000003`，就可以作为全局唯一 ID 了。

## 分布式协调/通知


## 集群管理


## Master 选举


## 分布式锁


## 分布式队列


# 二. ZooKeeper在大型分布式系统中的应用

## Hadoop


## HBase


## Kafka


# 三. ZooKeeper在阿里巴巴的实践与应用

## 案例1 消息中间件：Metamorphosis


## 案例2 RPC服务款姐：Dubbo


## 案例3 基于MySQL Binlog的增量订阅和消费组件：Canal


## 案例4 分布式数据库同步系统：Otter


## 案例5 轻量级分布式通用搜索平台：终搜


## 案例6 实时计算引擎


