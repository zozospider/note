

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

---
