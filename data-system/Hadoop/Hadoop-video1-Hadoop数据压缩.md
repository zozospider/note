


---

# 一 概述

压缩技术能够有效减少底层存储系统 (HDFS) 读写字节数. 压缩提高了网络宽带和磁盘空间的效率. 在运行 MapReduce 程序时, IO 操作, 网络数据传输, Shuffle 和 Merge 要花费大量的时间, 尤其是数据规模很大和共哟负载密集的情况下, 因此, 使用数据压缩显得非常重要.

鉴于磁盘 IO 和网络带宽是 Hadoop 的宝贵资源, 数据压缩对于节省资源, 最小化磁盘 IO 和网络传输非常有帮助. 可以在任意 MapReduce 阶段启用压缩. 不过, 尽管压缩与解压缩操作的 CPU 开销不高, 其性能的提升和资源的节省并非没有代价.

---

# 二 压缩策略和原则

压缩是提高 Hadoop 运行效率的一种优化策略. 通过对 Mapper, Reducer 运行过程的数据进行压缩, 以减少磁盘 IO, 提高 MapReduce 程序运行速度.

但是采用压缩技术减少磁盘 IO 的同时也增加了 CPU 运算负担, 所有压缩特性运用得当能够提高性能, 运用不当也可能降低性能. 压缩基本原则如下:
- 运算密集型的 Job, 少用压缩.
- IO 密集型的 Job, 多用压缩.

---

# 三 压缩编码方式

- MapReduce 支持的压缩编码方式如下:

| 压缩格式 | 是否 Hadoop 自带 | 算法 | 文件扩展名 | 是否可切片 | 换成压缩格式后, 原程序是否需要修改 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| Deflate | 是 | Deflate | .deflate | 否 | 和文本处理一样, 不需要修改 |
| Gzip | 是 | Deflate | .gz | 否 | 和文本处理一样, 不需要修改 |
| BZip2 | 是 | BZip2 | .bz2 | 是 | 和文本处理一样, 不需要修改 |
| LZO | 否, 需安装 | LZO | .lzo | 是 | 需要建索引, 需要指定输入格式 |
| Snappy | 否, 需安装 | Snappy | .snappy | 否 | 和文本处理一样, 不需要修改 |

- 压缩性能比较:

| 压缩算法 | 原始文件大小 | 压缩文件大小 | 压缩速度 | 解压速度 | 备注 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| Gzip | 8.3GB | 1.8GB | 17.5MB/s | 58MB/s | |
| BZip2 | 8.3GB | 1.1GB | 2.4MB/s | 9.5MB/s | |
| LZO | 8.3GB | 2.9GB | 49.3MB/s | 74.6MB/s | |
| Snappy | | | 250MB/s | 500MB/s | http://google.github.io/snappy/ |

- 对应编解码器:

| 压缩格式 | 编解码器 |
| :--- | :--- |
| Deflate | `org.apache.hadoop.io.compress.DeflateCodec` |
| Gzip | `org.apache.hadoop.io.compress.GzipCodec` |
| BZip2 | `org.apache.hadoop.io.compress.BZip2Codec` |
| LZO | `com.hadoop.compression.lzo.LzopCodec` |
| Snappy | `org.apache.hadoop.io.compress.SnappyCodec` |


---

# 四 

---

# 五 

---

# 六 

---
