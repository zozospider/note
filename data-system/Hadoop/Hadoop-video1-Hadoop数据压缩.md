


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

| 压缩格式 | 编解码器 |
| :--- | :--- |
| Deflate | `org.apache.hadoop.io.compress.DeflateCodec` |
| Gzip | `org.apache.hadoop.io.compress.GzipCodec` |
| BZip2 | `org.apache.hadoop.io.compress.BZip2Codec` |
| LZO | `com.hadoop.compression.lzo.LzopCodec` |
| Snappy | `org.apache.hadoop.io.compress.SnappyCodec` |

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

---

# 四 压缩选择

## Gzip

__优点__:
- 压缩率较高, 压缩 / 解压速度较快.
- Hadoop 本身支持, 无需安装, 在应用中处理 BZip2 格式的文件就和直接处理文本一样. 且大部分 Linux 系统都自带 Gzip 命令, 使用方便.

__缺点__:
- 不支持 Split.

__应用场景__:
- 当每个文件压缩之后在 130M 以内 (1 个块大小以内), 可以考虑用 Gzip 压缩. 例如 1 天或者 1 小时的日志压缩成一个 Gzip 文件.

## BZip2

__优点__:
- 支持 Split.
- 压缩率很高 (比 Gzip 压缩率高).
- Hadoop 本身支持, 无需安装, 在应用中处理 BZip2 格式的文件就和直接处理文本一样.

__缺点__:
- 压缩 / 解压速度很慢.

__应用场景__:
- 1. 适合对速度要求不高, 但需要较高对压缩率对时候.
- 2. 输出之后对数据较大, 处理后对数据需要压缩存档以减少磁盘空间且以后数据使用较少.
- 3. 对单个很大对文本文件想压缩减少存储空间, 同时又需要支持 Split, 且兼容之前对应用程序.

## LZO

__优点__:
- 压缩率适中 (比 Gzip 低), 压缩 / 解压速度较快.

__缺点__:
- 在应用中对 LZO 格式对文件需要做一些特殊处理 (为了支持 Split 需要建索引, 还需要指定 InputFormat 为 LZO 格式).
- Hadoop 本身不支持, 需要安装.

__应用场景__:
- 一个很大对文本文件, 压缩之后还大于 200M 以上可以考虑, 且单个文件越大, 优点越明显.

## Snappy

__优点__:
- 压缩率适中 (比 Gzip 低)， 压缩 / 解压速度很快.

__缺点__:
- 不支持 Split.
- Hadoop 本身不支持, 需要安装.

__应用场景__:
- 当 MapReduce 作业的 Map 输出的数据比较大的时候, 作为 Map 到 Reduce 的中间数据的压缩格式.
- 作为 1 个 MapReduce 作业的输出和另 1 个 MapReduce 作业的输入.

---

# 五 压缩位置选择

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E6%95%B0%E6%8D%AE%E5%8E%8B%E7%BC%A9/%E5%8E%8B%E7%BC%A9%E4%BD%8D%E7%BD%AE%E9%80%89%E6%8B%A9.png?raw=true)

---

# 六 压缩参数配置

要在 Hadoop 中启用压缩, 可以配置如下参数:

## 6.1 Mapper 输入

- 位置: `core-site.xml`
- 备注: Hadoop 使用文件扩展名判断是否支持某种编解码器

```xml
<property>
  <name>io.compression.codecs</name>
  <value></value>
  <description>A comma-separated list of the compression codec classes that can
  be used for compression/decompression. In addition to any classes specified
  with this property (which take precedence), codec classes on the classpath
  are discovered using a Java ServiceLoader.</description>
</property>
```

## 6.2 Mapper 输出

- 位置: `mapred-site.xml`
- 备注: 这个参数设置为 true 启用压缩

```xml
<property>
  <name>mapreduce.map.output.compress</name>
  <value>false</value>
  <description>Should the outputs of the maps be compressed before being
               sent across the network. Uses SequenceFile compression.
  </description>
</property>
```

- 位置: `mapred-site.xml`
- 备注: 此阶段企业较多使用 LZO / Snappy

```xml
<property>
  <name>mapreduce.map.output.compress.codec</name>
  <value>org.apache.hadoop.io.compress.DefaultCodec</value>
  <description>If the map outputs are compressed, how should they be 
               compressed?
  </description>
</property>
```

## 6.3 Reduce 输出

- 位置: `mapred-site.xml`
- 备注: 这个参数设置为 true 启用压缩

```xml
<property>
  <name>mapreduce.output.fileoutputformat.compress</name>
  <value>false</value>
  <description>Should the job outputs be compressed?
  </description>
</property>
```

- 位置: `mapred-site.xml`
- 备注: 使用标准工具或编解码器, 如 gzip / bzip2

```xml
<property>
  <name>mapreduce.output.fileoutputformat.compress.codec</name>
  <value>org.apache.hadoop.io.compress.DefaultCodec</value>
  <description>If the job outputs are compressed, how should they be compressed?
  </description>
</property>
```

- 位置: `mapred-site.xml`
- 备注: SequenceFile 输出使用的压缩类型: NONE / BLOCK

```xml
<property>
  <name>mapreduce.output.fileoutputformat.compress.type</name>
  <value>RECORD</value>
  <description>If the job outputs are to compressed as SequenceFiles, how should
               they be compressed? Should be one of NONE, RECORD or BLOCK.
  </description>
</property>
```

---

# 七 代码测试

## 7.1 数据流的压缩和解压缩

CompressionCodec 有两个方法可以用于轻松地压缩或解压缩数据:
- 要想对正在被写入一个输出流的数据进行压缩, 可调用 createOutputStream(OutputStreamout) 方法创建一个 CompressionOutputStream, 将其以压缩格式写入底层的流.
- 要想对从输入流读取而来的数据进行解压缩, 可调用 createInputStream(InputStreamin) 方法创建一个 CompressionInputStream, 从底层的流读取未压缩的数据.

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 ()](https://github.com/zozospider/note-hadoop-video1)

## 7.2 Map 输出端采用压缩

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 ()](https://github.com/zozospider/note-hadoop-video1)

## 7.3 Reduce 输出端采用压缩

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 ()](https://github.com/zozospider/note-hadoop-video1)

---
