- [一. HDFS 产出背景及定义](#一-hdfs-产出背景及定义)
- [二. HDFS 优缺点](#二-hdfs-优缺点)
    - [2.1 优点](#21-优点)
    - [2.2 缺点](#22-缺点)
- [三. HDFS 组成架构](#三-hdfs-组成架构)
    - [NameNode](#namenode)
    - [DataNode](#datanode)
    - [Client](#client)
    - [Secondary NameNode](#secondary-namenode)
- [四. HDFS 文件块大小](#四-hdfs-文件块大小)

---

# 一. HDFS 产出背景及定义

HDFS (Hadoop Distributed File System) 是分布式文件管理系统中的一种.

HDFS 的使用场景: 适合一次写, 多次读的场景, 且不支持文件的修改. (适合做数据分析)

---

# 二. HDFS 优缺点

## 2.1 优点

- 高容错性
  - 数据自动保存多个副本, 通过增加副本的形式, 提高容错性.
  - 某一个副本丢失后, 可以自动恢复.
- 适合处理大数据
  - 数据规模: 能够达到 GB, TB, PB 级别.
  - 文件规模: 能够处理百万规模以上的文件数量, 数量大.
- 可构建在廉价机器上, 通过多副本机制, 提高可靠性.

## 2.2 缺点

- 不适合低延时数据访问, 比如毫秒级.
- 无法高效的对大量小文件进行存储.
  - 存储大量小文件, 会占用 NameNode 大量内存来存储文件目录和块信息.
  - 小文件存储的寻址时间会超过读取时间, 违反了 HDFS 的设计目标.
- 不支持并发写入, 文件随机修改.
  - 一个文件只能有一个写, 不允许多个线程同时写.
  - 仅支持数据追加, 不支持文件的随机修改.

---

# 三. HDFS 组成架构

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BHDFS-1.png?raw=true)

## NameNode

- 管理 HDFS 的名称空间
- 管理副本策略
- 管理数据块 (Block) 映射信息
- 处理客户端请求

## DataNode

- 存储实际的数据块
- 执行数据块的读 / 写操作

## Client

- 文件切分: 文件上传 HDFS 的时候, Client 将文件切分成一个个的 Block, 然后进行上传.
- 与 NameNode 交互, 获取文件的位置信息.
- 与 NameNode 交互, 读取 / 写入数据.
- Client 提供一些命令来管理 HDFS, 比如 NameNode 格式化.
- Client 可以通过一些命令来访问 HDFS, 比如对 HDFS 增删查改.

## Secondary NameNode

并非 NameNode 的热备份, 当 NameNode 挂掉的时候, 它并不能马上替换 NameNode 并提供服务.

- 辅助 NameNode, 分担其工作量, 比如定期合并 Fsimages 和 Edits, 并推送给 NameNode.
- 在紧急情况下, 可恢复 NameNode.

---

# 四. HDFS 文件块大小

HDFS 的文件在物理上是分块存储 (Block), 块的大小可以通过参数 (`hdfs-size.xml` 的 `dfs.blocksize`) 来配置, Hadoop 2.x 默认是 `128M`, 老版本默认是 `64M`.

评估块大小的最佳实践为: __寻址时间为传输时间的 `1%` 时为最佳状态.__

- 1. 如果寻址时间约为 `10ms`, 即查找到目标 Block 的时间为 `10ms`.
- 2. 因此, 传输时间 = 10ms / 0.01 = 1000ms = 1s.
- 3. 目前磁盘的传输速率普遍为 1000MB/s.
- 4. 因此 Block 大小 = 1s * 100MB/s = 100MB. (即块大小为 100MB 左右)

思考: 为什么 HDFS 块的大小不能设置太小, 也不能设置太大?

- 如果设置太小, 会增加寻址时间, 程序一直在找块的开始位置.
- 如果设置太大, 从磁盘传输数据的时间会明显大于定位这个块开始位置所需要的时间. 导致程序在处理这块数据时, 会非常慢.

总结: __HDFS 的块大小设置主要取决于磁盘传输速率__.

---
