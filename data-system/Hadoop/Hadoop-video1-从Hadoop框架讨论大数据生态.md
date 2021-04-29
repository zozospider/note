
# Hadoop 是什么

- Hadoop 是一个由 Apache 基金会所开发的分布式系统基础架构
- 主要解决海量数据的存储和分析计算问题
- 广义上, Hadoop 通常是指一个更广泛的概念: Hadoop 生态圈

---

# Hadoop 三大发行版本

## Apache

- 官网地址: http://hadoop.apache.org/
- 下载地址: https://archive.apache.org/dist/hadoop/common/

原始版本, 入门学习好.

## Cloudera

- 官网地址: https://www.cloudera.com/
- 下载地址1: https://www.cloudera.com/downloads/cdh/6-2-0.html
- 下载地址2: http://archive.cloudera.com/cdh5/cdh/5/

大型互联网应用多. 免费使用, 出现问题解决收费.

2008 年成立, 2009 年 Hadoop 创始人 Doug Cutting 也加盟该公司, 产品主要为 CDH, Cloudera Manager, Cloudera Support.

- __CDH__: 是 Cloudera 的 Hadoop 发行版, 完全开源, 比 Apache Hadoop 在兼容性, 安全性, 稳定性上有所提升.
- __Cloudera Manager__: 是集群的软件分发及管理监控平台, 可以在几个小时内部署好一个 Hadoop 集群, 并对集群的节点及服务进行实时监控.
- __Cloudera Support__: 是对 Hadoop 的技术支持.

## Hortonworks

- 官网地址: https://hortonworks.com/
- 官网地址: https://hortonworks.com/products/data-platforms/hdp/
- 下载地址: https://www.cloudera.com/downloads.html#data-platform

文档好, 已经与 Cloudera 合并.

2011 年成立, 由雅虎与硅谷风投公司 Benchmark Capital 合资组建. 成立之初吸纳了 25 ~ 30 名专门研究 Hadoop 的雅虎工程师, 上述工程师在 2005 年开始协助雅虎开发 Hadoop, 贡献了 Hadoop 80% 的代码.

- __HDP__: Hortonworks 主打产品是 Hortonworks Data Platform (HDP), 100% 开源.
- __Ambari__: 一款开源的安装和管理系统.
- __HCatalog__: 一个元数据管理系统, HCatalog 现已集成到 Hive 中.

---

# Hadoop 的优势

- 高可靠性
- 高拓展性
- 高效性
- 高容错性

---

# Hadoop 1.x 和 2.x 的区别

- 1.x
  - MapReduce (计算 + 资源调度)
  - HDFS (数据存储)
  - Common (辅助工具)
- 2.x
  - MapReduce (计算)
  - YARN (资源调度)
  - HDFS (数据存储)
  - Common (辅助工具)

# HDFS 架构概述

- __NameNode (nm)__: 存储文件的元数据, 如文件名, 文件目录结构, 文件属性 (生成时间, 副本数, 文件权限), 以及每个文件的块列表和块所在的 DataNode 等.
- __Secondary NameNode (2nm)__: 用来监控 HDFS 状态的辅助后台程序, 每个一段时间获取 HDFS 元数据的快照.
- __DataNode (dn)__: 在本地文件系统存储文件块数据, 以及块数据的校验和.

---

# YARN 架构概述

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-从Hadoop框架讨论大数据生态/YARN架构.png)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-从Hadoop框架讨论大数据生态/YARN架构-3v.png)

以下为官网图片:

![image](https://raw.githubusercontent.com/zozospider/note/master/data-system/Hadoop/Hadoop-video1-从Hadoop框架讨论大数据生态/yarn_architecture.gif)

---

# MapReduce 架构概述

- __Map 阶段__: 并行处理输入数据.
- __Reduce 阶段__: 对 Map 结果进行汇总.

![image](https://raw.githubusercontent.com/zozospider/note/master/data-system/Hadoop/Hadoop-video1-从Hadoop框架讨论大数据生态/MapReduce架构-3v.png)

---

# 大数据技术生态体系

![image](https://raw.githubusercontent.com/zozospider/note/master/data-system/Hadoop/Hadoop-video1-从Hadoop框架讨论大数据生态/大数据技术生态体系.PNG)

---

# 推荐系统框架图

![image](https://raw.githubusercontent.com/zozospider/note/master/data-system/Hadoop/Hadoop-video1-从Hadoop框架讨论大数据生态/推荐系统项目架构.PNG)

---
