
- [Document & Code](#document--code)
- [一 配置详解](#一-配置详解)
    - [1.1 基本配置](#11-基本配置)
    - [1.2 高级配置](#12-高级配置)
- [二 四字命令](#二-四字命令)
- [三 JMX](#三-jmx)
    - [3.1 开启远程 JMX](#31-开启远程-jmx)
    - [3.2 通过 JConsole 连接 ZooKeeper](#32-通过-jconsole-连接-zookeeper)
- [四 监控](#四-监控)
    - [4.1 实时监控](#41-实时监控)
        - [4.1.1 节点可用性自检](#411-节点可用性自检)
        - [4.1.2 读写 TPS 监控](#412-读写-tps-监控)
    - [4.2 数据统计](#42-数据统计)
- [五 构建一个高可用的集群](#五-构建一个高可用的集群)
    - [5.1 集群组成](#51-集群组成)
    - [5.2 容灾](#52-容灾)
        - [5.2.1 单点问题](#521-单点问题)
        - [5.2.2 容灾](#522-容灾)
        - [5.2.3 三机房部署](#523-三机房部署)
        - [5.2.4 双机房部署](#524-双机房部署)
    - [5.3 扩容与缩容](#53-扩容与缩容)
        - [5.3.1 整体重启](#531-整体重启)
        - [5.3.2 逐台重启](#532-逐台重启)
- [六 日常运维](#六-日常运维)
    - [6.1 数据与日志管理](#61-数据与日志管理)
        - [6.1.1 Shell 脚本进行清理](#611-shell-脚本进行清理)
        - [6.1.2 使用清理工具 PurgeTxnLog](#612-使用清理工具-purgetxnlog)
        - [6.1.3 使用清理脚本 zkCleanup.sh](#613-使用清理脚本-zkcleanupsh)
        - [6.1.4 自动清理机制](#614-自动清理机制)
    - [6.2 Too many connections](#62-too-many-connections)
    - [6.3 磁盘管理](#63-磁盘管理)
- [七 各发行版本变更记录](#七-各发行版本变更记录)
    - [7.1 3.3 系列](#71-33-系列)
        - [7.1.1 3.3.0 版本](#711-330-版本)
        - [7.1.2 3.3.1 版本](#712-331-版本)
        - [7.1.3 3.3.2 版本](#713-332-版本)
        - [7.1.4 3.3.4 版本](#714-334-版本)
    - [7.2 3.4 系列](#72-34-系列)
        - [7.2.1 3.4.0 版本](#721-340-版本)
        - [7.2.2 3.4.4 版本](#722-344-版本)
        - [7.2.3 3.4.6 版本](#723-346-版本)
- [八 ZooKeeper 源代码阅读指引](#八-zookeeper-源代码阅读指引)
    - [8.1 整体结构](#81-整体结构)
    - [8.2 客户端 API 设计与实现](#82-客户端-api-设计与实现)
    - [8.3 序列化与协议](#83-序列化与协议)
    - [8.4 网络通信](#84-网络通信)
    - [8.5 Watcher 机制](#85-watcher-机制)
    - [8.6 数据与存储](#86-数据与存储)
    - [8.7 请求处理链](#87-请求处理链)
    - [8.8 Leader 选举](#88-leader-选举)
    - [8.9 服务端各角色工作原理](#89-服务端各角色工作原理)
    - [8.10 权限认证](#810-权限认证)
    - [8.11 JMX 相关](#811-jmx-相关)
    - [8.12 静态变量定义和 ZooKeeper 异常定义](#812-静态变量定义和-zookeeper-异常定义)

---

# Document & Code

* [../Zookeeper-book](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-book.md)

---

# 一 配置详解

## 1.1 基本配置

基本配置是指运行 ZooKeeper 必须的参数, 部分参数 ZooKeeper 会为其设置默认值.

以下为基本配置说明:

| 参数名 | 说明 |
| :--- | :--- |
| clientPort | 该参数无默认值, 必须配置, 不支持系统属性方式 (启动命令行加参数 -D) 配置. |
|  | clientPort 用于配置当前 ZooKeeper 服务器对外的服务端口, 客户端通过该端口和服务器创建连接, 一般设置为 `2181`. |
| dataDir | 该参数无默认值, 必须配置, 不支持系统属性方式配置. |
|  | dataDir 用于配置 ZooKeeper 服务器存储快照文件的目录, 默认情况下, 如果没有配置 dataLogDir, 那么事务日志也会存储在这个目录中. 考虑到事务日志的写性能直接影响 ZooKeeper 服务的整体性能, 建议同时设置参数 dataLogDir 来配置 ZooKeeper 事务日志的存储目录. |
| tickTime | 该参数有默认值 `3000` (单位: ms), 可以不配置, 不支持系统属性方式配置. |
|  | tickTime 用于配置 ZooKeeper 中最小时间单元的长度, 很多运行时的时间间隔都是使用 tickTime 的倍数来表示的. 如 ZooKeeper 的会话最小超时时间默认是 2 * tickTime. |

## 1.2 高级配置

以下为高级配置说明:

| 参数名 | 说明 |
| :--- | :--- |
| dataLogDir | 该参数有默认值 `dataDir`, 可以不配置, 不支持系统属性方式配置. |
|  | dataLogDir 用于配置 ZooKeeper 服务器存储事务日志文件的目录. 默认情况下, ZooKeeper 会将事务日志文件和快照数据文件存储在同一个目录, 应该尽量将他们分开. |
|  | 事务日志记录对磁盘的性能非常高, 为了保证数据一致性, ZooKeeper 在返回客户端事务请求响应之前, 必须将本次请求对应的事务日志写入到磁盘上. 因此, 事务日志写入性能直接决定了 ZooKeeper 在处理事务请求时的吞吐. 由于针对同一块磁盘的其他并发读写操作 (如 ZooKeeper 运行时的数据快照操作, 日志输出, 操作系统自身读写等) 会极大的影响事务日志的写性能, 因此如果条件允许, 建议将事务日志的存储配置一个单独的磁盘或挂载点, 以提升 ZooKeeper 的性能. |
| initLimit | 该参数有默认值 `10`, 即 `10 * tickTime`, 必须配置一个正整数, 不支持系统属性方式配置. |
|  | initLimit 用于配置 Leader 等待 Follower 启动, 并完成数据同步的时间. Follower 在启动过程中, 会与 Leader 建立连接并完成对数据的同步, 从而确定自己对外提供服务的起始状态, Leader 允许 Follower 在 initLimit 时间内完成这个工作. |
|  | 通常情况下, 运维人员使用该参数的默认值即可. 但是如果随着 ZooKeeper 集群管理的数据量增大, Follower 在启动时, 从 Leader 上进行同步数据的时间也会变长. 因此, 在这种情况下, 有必要适当调大该参数. |
| syncLimit | 该参数有默认值 `5`, 即 `5 * tickTime`, 必须配置一个正整数, 不支持系统属性方式配置. |
|  | syncLimit 用于配置 Leader 和 Follower 之间进行心跳检测的最大延长时间. 在 ZooKeeper 集群运行过程中, Leader 会与所有 Follower 进行心跳检测来确定该服务器是否存活. 如果 Leader 在 syncLimit 时间内无法获取到 Follower 的心跳检测响应, 那么就认为该 Follower 已经脱离了和自己的同步. |
|  | 通常情况下, 运维人员使用该参数的默认值即可. 但是如果部署 ZooKeeper 集群的网络环境质量较低 (如网络延时大, 丢包严重等), 那么可以适当调大这个参数. |
| snapCount | 该参数有默认值 `100000`, 可以不配置, 仅支持系统属性方式配置: `zookeeper.snapCount`. |
|  | snapCount 用于配置相邻两次数据快照之间的事务操作次数, 即 ZooKeeper 会在 snapCount 次事务操作之后进行一次数据快照. |
| preAllocSize | 该参数有默认值 `65536` (单位: KB), 即 `64MB`, 可以不配置, 仅支持系统属性方式配置: `zookeeper.preAllocSize`. |
|  | preAllocSize 用于配置 ZooKeeper 事务日志文件预分配的磁盘空间大小. |
|  | 通常情况下, 运维人员使用该参数的默认值即可. 但是如果将参数 snapCount 设置得比默认值更小或更大, 那么 preAllocSize 也要随之做出变更. 例如: snapCount 设置为 500, 预估每次事务操作的数据量大小最多 20KB, 那么 preAllocSize 设置为 10000 就足够了. |
| minSessionTimeout / maxSessionTimeout | 这两个参数有默认值, 分别是 `2` 和 `20` (单位: ms), 即默认的会话超时时间在 `2 * tickTime` ~ `20 * tickTime` 范围内, 可以不配置, 不支持系统属性方式配置. |
|  | 这两个参数用于服务端对客户端会话的超时时间进行限制, 如果客户端设置的超时时间不在该范围内, 那么会被服务端强制设置为 minSessionTimeout 或 maxSessionTimeout 超时时间. |
| maxClientCnxns | 该参数有默认值 `60`, 可以不配置, 不支持系统属性方式配置. |
|  | maxClientCnxns 从 Socket 层面限制单个客户端与单台服务器之间的连接并发数, 即以 IP 地址粒度来进行连接数的限制. 如果该参数设置为 0, 则表示连接数不作任何限制. |
|  | ZooKeeper 服务端会记录每一个和自己创建连接的客户端, 如果超过参数值, 会打印告警日志, 在参数值范围内的客户端连接不会受到影响, 但是服务端会拒绝超出部分的客户端连接请求. |
|  | 需要注意, 该连接数限制选项的适用范围, 其仅仅是对单台客户端机器与单台 ZooKeeper 服务器之间的连接数限制, 并不控制所有客户端的连接数总和. |
| jute.maxbuffer | 该参数有默认值 `1048575` (单位: byte), 可以不配置, 仅支持系统属性方式配置: `jute.maxbuffer`. |
|  | jute.maxbuffer 用于配置单个 ZNode (数据节点) 上可以存储的最大数据量大小. |
|  | 通常情况下, 运维人员使用该参数的默认值即可. 同时考虑到 ZooKeeper 上不宜存储太多的数据, 往往还需要调小该参数. 另外, 在变更该参数的时候, 需要在 ZooKeeper 集群的所有机器以及所有的客户端上均设置才能生效. |
| clientPortAddress | 该参数没有默认值, 可以不配置, 不支持系统属性方式配置. |
|  | 针对那些多网卡的机器, 该参数允许为每个 IP 地址指定不同的监听端口 |
| server.id=host:port:port | 该参数没有默认值, 在单机模式下可以不配置, 不支持系统属性方式配置. |
|  | server.id=host:port:port 用于配置组成 ZooKeeper 集群的机器列表, 其中 id 即为 ServerID, 与每台服务器 myid 文件中的数字相对应. 同时, 在该参数中, 会配置两个端口: 第一个端口, 第一个端口用于指定 Follower 与 Leader 进行运行时通信和数据同步时所使用的端口, 第二个端口用于进行 Leader 选举过程的投票通信. |
|  | 在 ZooKeeper 服务器启动时, 会根据 myid 文件中配置的 ServerID 来确定自己是哪台服务器, 并使用对应配置的度单口来进行启动. |
|  | 如果在同一台服务器上部署多个 ZooKeeper 来搭建伪分布式集群, 这些端口要不同, 如下: |
|  | server.1=192.168.0.1:2777:3777 |
|  | server.1=192.168.0.1:2888:3888 |
|  | server.1=192.168.0.1:2999:3999 |
| autopurge.snapRetainCount | 该参数有默认值 `3`, 可以不配置, 不支持系统属性方式配置. |
|  | autopurge.snapRetainCount 用于配置 ZooKeeper 在自动清理历史事务日志和快照数据的时候需要保留的快照数据文件数量和对应的事务日志文件. |
|  | 需要注意, 并不是磁盘上的所有事务日志和快照数据文件都可以被清理掉 (那样将无法恢复数据), 因此, 参数 autopurge.snapRetainCount 的最小值是 `3`, 如果配置的值比 3 小的话, 就会被调整成 3, 即至少需要保留 3 个快照数据文件和对应的事务日志文件. |
| autopurge.purgeInterval | 该参数有默认值 `0` (单位: 小时), 可以不配置, 不支持系统属性方式配置. |
|  | 参数 autopurge.purgeInterval 和参数 autopurge.snapRetainCount 配套使用, 适用于配置 ZooKeeper 进行历史文件自动清理的频率. |
|  | 如果配置该值为 `0` 或负数, 那么就表示不需要开启定时清理功能. ZooKeeper 默认不开启这个功能. |
| fsync.warningthresholdms | 该参数有默认值 `1000` (单位: ms), 可以不配置, 仅支持系统属性方式配置: `fsync.warningthresholdms`. |
|  | fsync.warningthresholdms 用于配置 ZooKeeper 进行事务日志 fsync 操作时消耗时间的报警阈值. 一旦进行一个 fsync 操作小号的时间大于 fsync.warningthresholdms 值, 就会在日志中打印报警日志. |
| forceSync | 该参数有默认值 `yes`, 可以不配置, 可选配置项为 `yes` 和 `no`, 仅支持系统属性方式配置: `zookeeper.forceSync`. |
|  | forceSync 用于配置 ZooKeeper 服务器是否在事务提交的时候, 将日志写入操作强制刷入磁盘 (即调用 `java.nio.channels.FileChannel.force(boolean metaData)` 接口), 默认是 yes, 即每次事务日志写入操作都会实时刷入磁盘. 如果设置为 no, 则能一定程度提高 ZooKeeper 的写性能, 但是会存在类似于机器断电的安全风险. |
| globalOutstandingLimit | 该参数有默认值 `1000`, 可以不配置, 仅支持系统属性方式配置: `zookeeper.globalOutstandingLimit`. |
|  | globalOutstandingLimit 用于配置 ZooKeeper 服务器最大请求堆积数量. 在 ZooKeeper 服务器运行过程中, 客户端会源源不断的将请求发送到服务端, 为了防止服务端资源 (如 CPU, 内存, 网络等) 耗尽, 服务端必须限制同时处理的请求数, 即最大请求堆积数量. |
| leaderServes | 该参数有默认值 `yes`, 可以不配置, 可选配置项为 `yes` 和 `no`, 仅支持系统属性方式配置: `zookeeper.leaderServes`. |
|  | leaderServes 用于配置 Leader 是否能够接受客户端的连接, 即是否允许 Leader 向服务器提供服务, 默认情况下, Leader 能够接受并处理客户端的所有读写请求. 在 ZooKeeper 设计中, Leader 主要用来进行对事务更新请求的协调和集群本身的运行时协调. 因此, 可以设置让 Leader 不接受客户端的连接, 以使其专注于分布式协调. |
| SkipAcl | 该参数有默认值 `no`, 可以不配置, 可选配置项为 `yes` 和 `no`, 仅支持系统属性方式配置: `zookeeper.SkipAcl`. |
|  | SkipAcl 用于配置 ZooKeeper 服务器是否跳过 ACL 权限检查, 默认为 no, 即会对每一个客户端请求进行权限检查. 如果将其设置为 yes, 则能一定程度的提高 ZooKeeper 的读写性能, 但同时也将向所有客户端开放权限, 包括之前设置过的 ACL 权限的数据节点都将不再接受权限控制. |
| cnxTimeout | 该参数有默认值 `5000` (单位: ms), 可以不配置, 仅支持系统属性方式配置: `zookeeper.cnxTimeout`. |
|  | cnxTimeout 用于配置在 Leader 选举过程中, 各服务器之间进行 TCP 连接创建的超时时间. |

---

# 二 四字命令

四字命令通常用来发送请求到 ZooKeeper 服务器用于获取信息.

请参考 [ZooKeeper 四字命令 Four Letter Words](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-video-ZK%E5%9F%BA%E6%9C%AC%E7%89%B9%E6%80%A7%E4%B8%8E%E5%9F%BA%E4%BA%8ELinux%E7%9A%84ZK%E5%AE%A2%E6%88%B7%E7%AB%AF%E5%91%BD%E4%BB%A4%E8%A1%8C%E5%AD%A6%E4%B9%A0.md#zookeeper-%E5%9B%9B%E5%AD%97%E5%91%BD%E4%BB%A4-four-letter-words)

---

# 三 JMX

JMX (Java Management Extensions, Java 管理拓展), 是一个为应用程序, 设备, 系统等植入管理功能的框架, 能够非常方便地让 Java 系统对外提供运行时数据信息获取和系统管控的接口.

ZooKeeper 使用了标准的 JMX 方式来对外提供运行时数据信息和便捷的管控接口.

## 3.1 开启远程 JMX

// TODO

## 3.2 通过 JConsole 连接 ZooKeeper

// TODO

---

# 四 监控

阿里中间件的软负载团队开发的 `TaoKeeper` (官网 [GitHub-alibaba-taokeeper](https://github.com/alibaba/taokeeper)) 主要从实时监控和数据统计两方面来保障 ZooKeeper 的稳定性.

## 4.1 实时监控

### 4.1.1 节点可用性自检

节点可用性自检是指对一个 ZooKeeper 集群中每个服务器节点上的指定数据节点 `/TAOKEEPER.MONITOR.ACTIVE.CHECK` 定期进行三次如下操作序列:
```
创建连接 - 发布数据 - 接收数据更新通知 - 获取数据 - 比对数据
```

如果三次流程都成功且每次耗费的时间再指定范围内则认为该节点处于正常状态.

### 4.1.2 读写 TPS 监控

在 TaoKeeper 上, 可以清楚地看到每台 ZooKeeper 服务器的读写 TPS 详情, 如连接的创建于断开, 数据节点的创建于删除, 数据节点内容的读取与更新, 子节点列表的读取等.

## 4.2 数据统计

TaoKeeper 将 ZooKeeper 的四字命令的返回结果进行了整合, 同时持久化到数据库中, 这样, 就能够可视化地看到 ZooKeeper 的运行时数据和数据的变化趋势, 如连接数, 订阅者数, 数据节点总数, 收发的数据量大小等.

---

# 五 构建一个高可用的集群

## 5.1 集群组成

ZooKeeper 官方建议集群机器的数量为奇数. 但是使用偶数也不会有问题. 以下为解释:

`过半存活及可用` 特性表明一个 ZooKeeper 集群如果要对外提供可用的服务, 那么集群中至少要有过半的机器正常工作且彼此正常通信.

因此, 如果想要一个集群能够允许 F 台机器挂掉, 那么集群需要 (2F + 1) 台机器. 以下为示例:
- 一个 3 台机器构成的集群, 能够允许 1 台机器挂掉.
- 一个 5 台机器构成的集群, 能够允许 2 台机器挂掉.
- 一个 6 台机器构成的集群, 能够允许 2 台机器挂掉.
- 一个 7 台机器构成的集群, 能够允许 3 台机器挂掉.
- 一个 8 台机器构成的集群, 能够允许 3 台机器挂掉.

如上所示, 因为偶数台服务器构成的集群对比奇数台服务器构成的集群在容灾能力上没有显著优势, 因此建议设计成奇数台服务器.

## 5.2 容灾

容灾是值计算机系统具有一种在遭受诸如火灾, 水灾, 地震, 断电, 其他基础网络设备故障等毁灭性灾难的时候, 依然能够对外提供可用服务的能力.

### 5.2.1 单点问题

单点问题是指在一个分布式系统中, 如果某一个组件出现故障就会引起整个系统的可用性大大下降甚至瘫痪, 那么就认为该组件存在单点问题.

对于普通应用, 可在一个机房多台机器上部署组成集群, 解决单点问题.

ZooKeeper 基于 `过半` 原则, 在运行期间, 集群中至少有过半的机器保存了最新的数据, 因此, 只要集群中有超过一半的机器还在正常工作, 那么这一半的机器就至少会有一台保存了最新的数据, 整个集群就是健康的, 可以对外提供服务.

### 5.2.2 容灾

对于核心应用, 需要在多个机房多台机器上部署组成集群, 防止某个机房出现事故导致所有机器失效.

### 5.2.3 三机房部署

假设有三个网络情况良好的机房可以部署, 那么就可以在这三个机房中部署若干个机器来组成一个 ZooKeeper 集群.

假设 ZooKeeper 集群的机器总数为 N. 那么要使 ZooKeeper 集群具备容灾能力, 可以采用如下方案:

- a. 计算 N1 (机房 1 的机器数).

`N1 = (N - 1) / 2` (除法向下取整)

- b. 计算 N2 (机房 2 的机器数).

`1 < N2 < (N - N1) / 2`

- c. 计算 N3 (机房 3 的机器数), 同时确定 N2.

`N3 = N - N1 - N2` & `N3 < N1 + N2`

以下为分配算法 Java 代码实现:
```java
/**
 * 三机房部署方案
 */
public class HostAssignment_3 {

    /**
     * ZooKeeper 集群机器总数
     */
    static int n = 7;

    public static void main(String[] args) {

        int n1, n2, n3;
        n1 = (n - 1) / 2;
        int n2_max = (n - n1) / 2;
        for (int i = 1; i <= n2_max; i++) {
            n2 = i;
            n3 = n - n1 - n2;
            if (n3 >= (n1 + n2)) {
                continue;
            }
            System.out.println("n1=" + n1 + " n2=" + n2 + " n3=" + n3);
        }

    }

}
```

如上所示, 假设 ZooKeeper 集群的机器总数为 7, 那么可以得到以下两种部署方案:
```
n1=3 n2=1 n3=3
n1=3 n2=2 n3=2
```

### 5.2.4 双机房部署

目前版本 (3.4.6) 中, 还没有办法能够在双机房下实现较好的容灾效果, 因为无论那个机房发生异常, 都有可能使 ZooKeeper 集群中可用的机器无法超过半数.

在这种情况下, 唯一能做的就是尽量在主要机房中部署更多的机器. 如 7 台机器的集群, 4 台部署在主要机房, 3 台部署在另一个机房.

## 5.3 扩容与缩容

ZooKeeper 在水平扩容方面做得并不完美. 有以下两种方式:

### 5.3.1 整体重启

先将整个集群停止, 然后更新再启动.

### 5.3.2 逐台重启

每次更新并重启一台机器, 直到所有机器都完成更新和重启.

---

# 六 日常运维

## 6.1 数据与日志管理

ZooKeeper 正常运行过程中, 会不断地把快照数据和事务日志存储到 dataDir 和 dataLogDir 这两个目录, 并且如果没有人为操作的话, 默认情况下 ZooKeeper 不会清理这些文件. 以下有四种清理方式:

### 6.1.1 Shell 脚本进行清理

运维人员写一个删除历史文件的脚本, 定时执行.

以下脚本表示至多保留 60 个快照数据, 事务日志, ZooKeeper 运行时日志文件:
```bash
#!/bin/bash

# snapshot file dir
dataDir=/home/user/zookeeper/zk_data/version-2

# tran log dir
dataLogDir=/home/user/zookeeper/zk_log/version-2

# zk log dir
logDir=/homeuser/zookeeper/logs

# Leave 60 files
count=60
count=${$count+1}

ls -t $dataLogDir/log.* | tail -n +$count | xargs rm -f
ls -t $dataDir/snapshot.* | tail -n +$count | xargs rm -f
ls -t $logDir/zookeeper.log.* | tail -n +$count | xargs rm -f
```

### 6.1.2 使用清理工具 PurgeTxnLog

ZooKeeper 提供一个工具类 PurgeTxnLog (`org.apache.zookeeper.server.PurgeTxnLog`), 实现了简单的文件清理策略.

以下命令表示至多保留 15 个快照数据文件和相对应的事务日志文件:
```
java -cp zookeeper-3.4.5.jar:lib/slf4j-api-1.6.1.jar:lib/slf4j-log4j12-1.6.1.jar:lib/log4j-1.2.15.jar:conf org.apache.zookeeper.server.PurgeTxnLog /home/user/zookeeper/zk_data /home/user/zookeeper/zk_data -n 15
```

为了避免运维人员操作, 保证 ZooKeeper 进行数据恢复的需要, PurgeTxnLog 限制了至少需要保留 3 个快照数据文件.

### 6.1.3 使用清理脚本 zkCleanup.sh

`ZOOKEEPER_HOME/bin/zkCleanup.sh` 封装了 PurgeTxnLog, 使用方式如下:
```
sh zkCleanup.sh -n 15
```

### 6.1.4 自动清理机制

ZooKeeper 提供了一种自动清理历史快照数据和事务日志文件的机制, 通过配置 `autopurge.snapRetainCount` 和 `autopurge.purgeInterval` 来实现定时清理, 详情见 [1.2 高级配置](#12-高级配置).

## 6.2 Too many connections

通过配置 `maxClientCnxns` 来限制客户端连接数, 详情见 [1.2 高级配置](#12-高级配置).

## 6.3 磁盘管理

通过配置 `dataLogDir` 来管理事务日志文件的目录, 详情见 [1.2 高级配置](#12-高级配置).

---

# 七 各发行版本变更记录

## 7.1 3.3 系列

该版本添加了 Observer 角色, 四字命令, JMX 管理等.

### 7.1.1 3.3.0 版本

| ISSUE | 说明 |
| :--- | :--- |
|  |  |
|  |  |
|  |  |
|  |  |

### 7.1.2 3.3.1 版本

| ISSUE | 说明 |
| :--- | :--- |
|  |  |

### 7.1.3 3.3.2 版本

| ISSUE | 说明 |
| :--- | :--- |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |

### 7.1.4 3.3.4 版本

| ISSUE | 说明 |
| :--- | :--- |
|  |  |
|  |  |
|  |  |

## 7.2 3.4 系列

该版本添加了 Netty 框架, 运维机制和工具等.

### 7.2.1 3.4.0 版本

| ISSUE | 说明 |
| :--- | :--- |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |

### 7.2.2 3.4.4 版本

| ISSUE | 说明 |
| :--- | :--- |
|  |  |
|  |  |
|  |  |
|  |  |
|  |  |

### 7.2.3 3.4.6 版本

| ISSUE | 说明 |
| :--- | :--- |
|  |  |
|  |  |

---

# 八 ZooKeeper 源代码阅读指引

如需对 ZooKeeper 进行运行和调试, 修改源代码, 可以从 [官方 SVN](http:svn.apache.org/repos/asf/zookeeper/tags/) 下载源代码 (官方基于 Ant 构建), 然后导入本地进行开发.

## 8.1 整体结构

ZooKeeper 的源代码结构主要由 Java 源代码, 测试代码, 可执行脚本, 配置文件, 文档, Ant 配置, 项目说明这 6 大部分组成.

以下为核心部分:
- `src/java/main`: ZooKeeper 工程的所有 Java 源代码.
- `src/java/generated`: 初次进行 ZooKeeper 源代码编译构建过程中生成的实体类和协议层的类定义 (如 Jute 组件生成的相关类).
- `src/java/test` / `src/java/systest`: 测试代码.
- `bin`: 可执行脚本.
- `conf`: 配置文件.

## 8.2 客户端 API 设计与实现

ZooKeeper 客户端 API 和服务端程序包含在同一个包 `zookeeper-n.n.n.jar` 中 (经常被用户抱怨).

`org.apache.zookeeper.ZooKeeper` 类包含了 ZooKeeper 所有客户端 API 设计与实现.

`ZOOKEEPER_HOME/bin/zkCli.sh` 是一个简易的客户端脚本程序, 对应 `org.apache.zookeeper.ZooKeeperMain` 类.

## 8.3 序列化与协议

序列化与协议层, 使用 Jute 组件进行序列化和反序列化, 相关代码在 `org.apache.jute` 包中.

## 8.4 网络通信

以下为 ZooKeeper 客户端的网络通信的相关类的实现:
- `org.apache.zookeeper.ClientCnxn`
- `org.apache.zookeeper.ClientCnxnSocket`
- `org.apache.zookeeper.ClientCnxnSocketNIO`

ZooKeeper 服务端网络通信有两套实现, 分别是 ZooKeeper 自己实现的 NIO 和 Netty 实现.

以下为相关类的实现:
- `org.apache.zookeeper.server.NIOServerCnxn`
- `org.apache.zookeeper.server.NIOServerCnxnFactory`
- `org.apache.zookeeper.server.NettyServerCnxn`
- `org.apache.zookeeper.server.NettyServerCnxnFactory`

通过这些类可以了解到 ZooKeeper 客户端和服务端之间如何建立会话, 维持会话, 请求发送, 请求响应等.

## 8.5 Watcher 机制

Watcher 机制构建了整个 ZooKeeper 服务端和客户端的事件通知机制.

以下为相关类的实现:
- `org.apache.zookeeper.Watcher`
- `org.apache.zookeeper.WatchedEvent`
- `org.apache.zookeeper.ClientWatchManager`
- `org.apache.zookeeper.ZooKeeper.ZKWatchManager`

## 8.6 数据与存储

ZooKeeper 内部数据与存储相关的实现逻辑, 尤其是事务日志和数据快照技术, 是其保证分布式数据一致性的核心.

以下为相关类的实现:
- `org.apache.zookeeper.server.persistence` 包中的所有类
- `org.apache.zookeeper.server.quorum.LearnerHandler`

## 8.7 请求处理链

以下为 ZooKeeper 服务端的请求处理链的相关类的实现:
- `org.apache.zookeeper.server.PrepRequestProcessor`
- `org.apache.zookeeper.server.quorum.ProposalRequestProcessor`
- `org.apache.zookeeper.server.SyncRequestProcessor`
- `org.apache.zookeeper.server.RequestProcessor` 接口的其他实现类

## 8.8 Leader 选举

以下为相关类的实现:
- `org.apache.zookeeper.server.quorum.Election`: 选举算法的接口定义
- `org.apache.zookeeper.server.quorum.FastLeaderElection`: 选举算法实现
- `org.apache.zookeeper.server.quorum.LeaderElection`: 选举算法实现 (已废弃)
- `org.apache.zookeeper.server.quorum.AuthFastLeaderElection`: 选举算法实现 (已废弃)
- `org.apache.zookeeper.server.quorum.QuorumCnxManager`: 网络通信

## 8.9 服务端各角色工作原理

ZooKeeper 服务器分为 Leader, Follower, Observer 三种角色.

以下为相关类的实现:
- 集群模式下服务器基本功能定义
  - `org.apache.zookeeper.server.ZooKeeperServer`
  - `org.apache.zookeeper.server.quorum.QuorumZooKeeperServer`
- 3 种服务器角色
  - `org.apache.zookeeper.server.quorum.LeaderZooKeeperServer`
  - `org.apache.zookeeper.server.quorum.FollowerZooKeeperServer`
  - `org.apache.zookeeper.server.quorum.ObserverZooKeeperServer`
- ZAB 协议相关
  - `org.apache.zookeeper.server.quorum.Leader`
  - `org.apache.zookeeper.server.quorum.Follower`
  - `org.apache.zookeeper.server.quorum.Learner`
  - `org.apache.zookeeper.server.quorum.LearnerHandler`

## 8.10 权限认证

ZooKeeper 权限认证是插件化的, 提供了 Digest, IP, SASL 三种权限认证模式, 开发人员可在此基础上实现自己的权限认证方式.

以下为相关类的实现:
- `org.apache.zookeeper.server.auth` 包中的所有类

## 8.11 JMX 相关

ZooKeeper 使用 JMX 来帮助运维人员对 ZooKeeper 服务器进行监控和管理.

以下为相关类的实现:
- `org.apache.zookeeper.jmx` 包中的所有类

## 8.12 静态变量定义和 ZooKeeper 异常定义

ZooKeeper 的 `org.apache.zookeeper.ZooDefs` 类定义了许多静态变量, 如操作类型, 权限控制, ACL 定义等.

ZooKeeper 的 `org.apache.zookeeper.KeeperException` 类定义了 ZooKeeper 的异常以及他们对应的错误码.

---
