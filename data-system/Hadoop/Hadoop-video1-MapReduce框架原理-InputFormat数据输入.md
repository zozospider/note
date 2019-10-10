
- [一 切片与 MapTask 并行度决定机制](#一-切片与-maptask-并行度决定机制)
    - [1.1 问题引出](#11-问题引出)
    - [1.2 MapTask 并行度机制](#12-maptask-并行度机制)
- [二 Job 提交流程源码和切片源码](#二-job-提交流程源码和切片源码)
    - [2.1 Job 提交流程](#21-job-提交流程)
    - [2.2 Job 提交流程 - 切片](#22-job-提交流程---切片)
- [三 FileInputFormat 切片机制](#三-fileinputformat-切片机制)
- [四 CombineTextInputFormat 切片机制](#四-combinetextinputformat-切片机制)
    - [4.1 应用场景](#41-应用场景)
    - [4.2 虚拟存储切片最大值设置](#42-虚拟存储切片最大值设置)
    - [4.3 切片机制](#43-切片机制)
    - [4.4 源码逻辑](#44-源码逻辑)
    - [4.5 代码测试](#45-代码测试)
- [五 FileInputFormat 实现类](#五-fileinputformat-实现类)
- [六 FileInputFormat 实现类 - TextInputFormat](#六-fileinputformat-实现类---textinputformat)
- [七 FileInputFormat 实现类 - KeyValueTextInputFormat](#七-fileinputformat-实现类---keyvaluetextinputformat)
    - [7.1 代码测试](#71-代码测试)
- [八 FileInputFormat 实现类 - NLineInputFormat](#八-fileinputformat-实现类---nlineinputformat)
    - [8.1 代码测试](#81-代码测试)
- [九 FileInputFormat 实现类 - 自定义 InputFormat](#九-fileinputformat-实现类---自定义-inputformat)

---

- out
  - [MapReduce编程——输入类FileInputFormat（切片）及其4个实现类（kv）的用法](https://blog.csdn.net/wx1528159409/article/details/90236351)
  - [MapReduce Input Split（输入分/切片）详解](https://blog.csdn.net/Dr_Guo/article/details/51150278)
  - [Hadoop MapReduce Splits 切片源码分析及切片机制](https://blog.csdn.net/yljphp/article/details/89067858)
  - [Hadoop FileInputFormat 默认切片机制](https://blog.csdn.net/yljphp/article/details/89069951)
  - [Hadoop CombineTextInputFormat 切片机制](https://blog.csdn.net/yljphp/article/details/89070948)
  - [MapReduce-CombineTextInputFormat 切片机制](https://www.bbsmax.com/A/WpdK4b7mzV/)

---

# 一 切片与 MapTask 并行度决定机制

## 1.1 问题引出

MapTask 的并行度决定 Map 阶段的任务处理并发度, 进而影响到整个 Job 的处理速度.

_思考: 1G 的数据, 启动 8 个 MapTask, 可以提高集群的并发能力. 那么 1K 的数据, 也启动 8 个 MapTask, 会提高集群性能吗? MapTask 是否越多越好, 那些因素影响了 MapTask 并行度?_

## 1.2 MapTask 并行度机制

- __数据块__: Block 是 HDFS 物理上把数据分成一块一块.
- __数据切片__: 数据切片只是在逻辑上对输入进行分片, 并不会在磁盘上将其切分成片进行存储.

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-InputFormat%E6%95%B0%E6%8D%AE%E8%BE%93%E5%85%A5/%E6%95%B0%E6%8D%AE%E5%88%87%E7%89%87%E4%B8%8EMapTask%E5%B9%B6%E8%A1%8C%E5%BA%A6%E5%86%B3%E5%AE%9A%E6%9C%BA%E5%88%B6.png?raw=true)

---

# 二 Job 提交流程源码和切片源码

## 2.1 Job 提交流程

```java
Job
  waitForCompletion();
    submit();
      // 1 建立连接
      connect();
        // 创建提交 Job 的代理
        new Cluster(getConfiguration());
          // 判断是本地还是 YARN 模式
          initialize(jobTrackAddr, conf);
      // 2 提交 Job
      submitter.submitJobInternal(Job.this, cluster);
        // 创建给集群提交数据的 Stag 路径
        Path jobStagingArea = JobSubmissionFiles.getStagingDir(cluster, conf);
        // 获取 JobId, 并创建 Job 路径
        JobID jobId = submitClient.getNewJobID();
        // 拷贝 jar 包到集群
        copyAndConfigureFiles(job, submitJobDir);
          rUploader.uploadFiles(job, jobSubmitDir);
        // 计算切片, 生成切片规划文件
        int maps = writeSplits(job, submitJobDir);
          maps = writeNewSplits(job, jobSubmitDir);
            List<InputSplit> splits = input.getSplits(job);
        // 向 Stag 路径写 XML 配置文件
        writeConf(conf, submitJobFile);
          conf.writeXml(out);
        // 提交 Job, 返回提交状态
        status = submitClient.submitJob(jobId, submitJobDir.toString(), job.getCredentials());
```

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-InputFormat%E6%95%B0%E6%8D%AE%E8%BE%93%E5%85%A5/Job%E6%8F%90%E4%BA%A4%E6%B5%81%E7%A8%8B%E6%BA%90%E7%A0%81%E8%A7%A3%E6%9E%90.png?raw=true)

## 2.2 Job 提交流程 - 切片

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-InputFormat%E6%95%B0%E6%8D%AE%E8%BE%93%E5%85%A5/FileInputFormat%E5%88%87%E7%89%87%E6%BA%90%E7%A0%81%E8%A7%A3%E6%9E%90.png?raw=true)

---

# 三 FileInputFormat 切片机制

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-InputFormat%E6%95%B0%E6%8D%AE%E8%BE%93%E5%85%A5/FileInputFormat%E5%88%87%E7%89%87%E6%9C%BA%E5%88%B6.png?raw=true)

源码中计算切片大小的公式:

- `mapreduce.input.fileinputformat.split.minsize` = 1;
- `mapreduce.input.fileinputformat.split.maxsize` = Long.MAX_VALUE;

```java
Math.max(minSize, Math.min(maxSize, blockSize));
```

切片大小设置:

- `maxsize` (切片最大值): 此参数如果调得比 blockSize 小, 则会让切片变小且等于配置的参数值.
- `minsize` (切片最小值): 此参数如果调得比 blockSize 大, 则可以让切片变得比 blockSize 大.

获取切片信息 API:

```java
// 获取切片的文件名称
String name = inputSplit.getPath().getName();
// 根据文件类型获取切片信息
FileSplit inputSplit = (FileSplit) context.getInputSplit();
```

---

# 四 CombineTextInputFormat 切片机制

框架默认的 `TextInputFormat` 切片机制是对任务按文件规划切片, 不管文件多小, 都会是一个单独的切片, 都会交给一个 MapTask 处理. 如果有大量小文件, 就会产生大量的 MapTask, 处理效率很低. 此时可以考虑使用 `CombineTextInputFormat`.

## 4.1 应用场景

`CombineTextInputFormat` 用于小文件过多的场景, 它可以将多个小文件从逻辑上规划到一个切片中, 这样多个小文件就可以交给一个 MapTask 处理.

## 4.2 虚拟存储切片最大值设置

```java
// 注意: 虚拟存储切片最大值设置需要根据实际的小文件大小情况而定
// CombineTextInputFormat.setMaxInputSplitSize(job, 4194304);
// CombineFileInputFormat.setMaxInputSplitSize(job, 4194304);
FileInputFormat.setMaxInputSplitSize(job, 4194304);
```

## 4.3 切片机制

生成切片过程包括虚拟存储过程和切片过程:

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-InputFormat%E6%95%B0%E6%8D%AE%E8%BE%93%E5%85%A5/CombineTextInputFormat%E5%88%87%E7%89%87%E6%9C%BA%E5%88%B6.png?raw=true)

## 4.4 源码逻辑

```java
CombineFileInputFormat extends FileInputFormat
  getSplits(JobContext job)
    getMoreSplits(job, myPaths, maxSize, minSizeNode, minSizeRack, splits);
      files[i] = new OneFileInfo(stat, conf, isSplitable(job, stat.getPath()), rackToBlocks, blockToNodes, nodeToBlocks, rackToNodes, maxSize);
        if (left > maxSize && left < 2 * maxSize) {
```

## 4.5 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.input.combinetextinputformat.CombineTextInputFormatDriver1, com.zozospider.hadoop.mapreduce.input.combinetextinputformat.CombineTextInputFormatDriver2, com.zozospider.hadoop.mapreduce.input.combinetextinputformat.CombineTextInputFormatDriver3, com.zozospider.hadoop.mapreduce.input.combinetextinputformat.CombineTextInputFormatDriver4, com.zozospider.hadoop.mapreduce.input.combinetextinputformat.CombineTextInputFormatDriver5)](https://github.com/zozospider/note-hadoop-video1)

---

# 五 FileInputFormat 实现类

_思考: 在运行 MapReduce 程序时, 输入的文件格式包括: 基于行的日志文件, 二进制格式文件, 数据库表等. 那么, 针对不同的数据类型, MapReduce 是如何读取这些数据的呢?_

`FileInputFormat` 常见的接口实现类包括: `TextInputFormat`, `KeyValueTextInputFormat`, `NLineInputFormat`, `CombineTextInputFormat`, `自定义 InputFormat` 等.

---

# 六 FileInputFormat 实现类 - TextInputFormat

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-InputFormat%E6%95%B0%E6%8D%AE%E8%BE%93%E5%85%A5/TextInputFormat.png?raw=true)

---

# 七 FileInputFormat 实现类 - KeyValueTextInputFormat

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-InputFormat%E6%95%B0%E6%8D%AE%E8%BE%93%E5%85%A5/KeyValueTextInputFormat1.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-InputFormat%E6%95%B0%E6%8D%AE%E8%BE%93%E5%85%A5/KeyValueTextInputFormat2.png?raw=true)

## 7.1 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.input.keyvaluetextinputformat.KeyValueTextInputFormatDriver)](https://github.com/zozospider/note-hadoop-video1)

---

# 八 FileInputFormat 实现类 - NLineInputFormat

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-InputFormat%E6%95%B0%E6%8D%AE%E8%BE%93%E5%85%A5/NLineInputFormat1.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-InputFormat%E6%95%B0%E6%8D%AE%E8%BE%93%E5%85%A5/NLineInputFormat2.png?raw=true)

## 8.1 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.input.nlineinputformat.NLineInputFormatDriver)](https://github.com/zozospider/note-hadoop-video1)

---

# 九 FileInputFormat 实现类 - 自定义 InputFormat

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-InputFormat%E6%95%B0%E6%8D%AE%E8%BE%93%E5%85%A5/CustomInputFormat1.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-InputFormat%E6%95%B0%E6%8D%AE%E8%BE%93%E5%85%A5/CustomInputFormat2.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A1%86%E6%9E%B6%E5%8E%9F%E7%90%86-InputFormat%E6%95%B0%E6%8D%AE%E8%BE%93%E5%85%A5/CustomInputFormat3.png?raw=true)

## 9.1 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 ()](https://github.com/zozospider/note-hadoop-video1)

---
