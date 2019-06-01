- [一. 本地运行模式](#一-本地运行模式)
    - [1.1 Grep 案例](#11-grep-案例)
    - [1.2 WordCount 案例](#12-wordcount-案例)
- [二. 伪分布式运行模式](#二-伪分布式运行模式)
    - [2.1 启动 HDFS 并运行 MapReduce 程序](#21-启动-hdfs-并运行-mapreduce-程序)
        - [2.1.1 step1 修改配置](#211-step1-修改配置)
        - [2.1.2 step2 启动 HDFS (NameNode, DataNode)](#212-step2-启动-hdfs-namenode-datanode)
        - [2.1.3 HDFS 命令](#213-hdfs-命令)
        - [2.1.4 浏览器查看 HDFS](#214-浏览器查看-hdfs)
        - [2.1.5 WordCount 案例](#215-wordcount-案例)
    - [2.2 启动 YARN 并运行 MapReduce 程序](#22-启动-yarn-并运行-mapreduce-程序)
        - [2.2.1 step1 修改配置](#221-step1-修改配置)
        - [2.2.2 step2 启动 YARN (ResourceManager, NodeManager)](#222-step2-启动-yarn-resourcemanager-nodemanager)
        - [2.2.3 浏览器查看 YARN](#223-浏览器查看-yarn)
        - [2.2.4 WordCount 案例](#224-wordcount-案例)
        - [2.2.5 配置历史服务器](#225-配置历史服务器)
        - [2.2.6 配置日志聚集](#226-配置日志聚集)
- [三. 完全分布式运行模式](#三-完全分布式运行模式)
    - [3.1 准备 3 台机器](#31-准备-3-台机器)
    - [3.2 安装 JDK, Hadoop, 配置环境变量](#32-安装-jdk-hadoop-配置环境变量)
    - [3.3 配置集群](#33-配置集群)
        - [3.3.1 修改 Hadoop 配置](#331-修改-hadoop-配置)
        - [3.3.2 修改 HDFS 配置](#332-修改-hdfs-配置)
        - [3.3.3 修改 YARN 配置](#333-修改-yarn-配置)
        - [3.3.4 修改 MapReduce 配置](#334-修改-mapreduce-配置)
    - [3.4 集群每个节点单独启动](#34-集群每个节点单独启动)
        - [3.4.1 vm017: 格式化, 启动 NameNode, DataNode](#341-vm017-格式化-启动-namenode-datanode)
        - [3.4.2 vm06: 启动 DataNode](#342-vm06-启动-datanode)
        - [3.4.3 vm03: 启动 DataNode](#343-vm03-启动-datanode)
        - [3.4.4 浏览器查看 HDFS](#344-浏览器查看-hdfs)
    - [3.5 配置 SSH 免密登录](#35-配置-ssh-免密登录)

---

# 一. 本地运行模式

在本地运行模式 (默认运行模式) 下, Hadoop 读取本地文件 (而非 HDFS) 并将结果输出到本地路径 (而非 HDFS) 下.

- 文档: https://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/SingleCluster.html

## 1.1 Grep 案例

拷贝一些 xml 文件到 input 文件夹下作为输入数据, 通过官方提供的 grep 案例, 找出 input 文件夹中符合正则表达式等文件内容, 将符合的内容输出到 output 文件夹下.

- 以下为官网说明:

By default, Hadoop is configured to run in a non-distributed mode, as a single Java process. This is useful for debugging.

The following example copies the unpacked conf directory to use as input and then finds and displays every match of the given regular expression. Output is written to the given output directory.

```
  $ mkdir input
  $ cp etc/hadoop/*.xml input
  $ bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar grep input output 'dfs[a-z.]+'
  $ cat output/*
```

- 以下为实际运行成功效果:
```bash
[zozo@VM_0_17_centos hadoop-2.7.2]$ mkdir input
[zozo@VM_0_17_centos hadoop-2.7.2]$ cp etc/hadoop/*.xml input
[zozo@VM_0_17_centos hadoop-2.7.2]$ bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar grep input output 'dfs[a-z.]+'
19/05/19 19:45:19 INFO Configuration.deprecation: session.id is deprecated. Instead, use dfs.metrics.session-id
19/05/19 19:45:19 INFO jvm.JvmMetrics: Initializing JVM Metrics with processName=JobTracker, sessionId=
19/05/19 19:45:19 INFO input.FileInputFormat: Total input paths to process : 8
19/05/19 19:45:19 INFO mapreduce.JobSubmitter: number of splits:8
19/05/19 19:45:20 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_local467097207_0001
19/05/19 19:45:20 INFO mapreduce.Job: The url to track the job: http://localhost:8080/
19/05/19 19:45:20 INFO mapreduce.Job: Running job: job_local467097207_0001
19/05/19 19:45:20 INFO mapred.LocalJobRunner: OutputCommitter set in config null
19/05/19 19:45:20 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:45:20 INFO mapred.LocalJobRunner: OutputCommitter is org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter
19/05/19 19:45:20 INFO mapred.LocalJobRunner: Waiting for map tasks
19/05/19 19:45:20 INFO mapred.LocalJobRunner: Starting task: attempt_local467097207_0001_m_000000_0
19/05/19 19:45:20 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:45:20 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:45:20 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/hadoop-policy.xml:0+9683
19/05/19 19:45:20 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:45:20 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:45:20 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:45:20 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:45:20 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:45:20 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:45:20 INFO mapred.LocalJobRunner: 
19/05/19 19:45:20 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:45:20 INFO mapred.MapTask: Spilling map output
19/05/19 19:45:20 INFO mapred.MapTask: bufstart = 0; bufend = 17; bufvoid = 104857600
19/05/19 19:45:20 INFO mapred.MapTask: kvstart = 26214396(104857584); kvend = 26214396(104857584); length = 1/6553600
19/05/19 19:45:20 INFO mapred.MapTask: Finished spill 0
19/05/19 19:45:20 INFO mapred.Task: Task:attempt_local467097207_0001_m_000000_0 is done. And is in the process of committing
19/05/19 19:45:21 INFO mapred.LocalJobRunner: map
19/05/19 19:45:21 INFO mapred.Task: Task 'attempt_local467097207_0001_m_000000_0' done.
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Finishing task: attempt_local467097207_0001_m_000000_0
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Starting task: attempt_local467097207_0001_m_000001_0
19/05/19 19:45:21 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:45:21 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:45:21 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/kms-site.xml:0+5511
19/05/19 19:45:21 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:45:21 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:45:21 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:45:21 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:45:21 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:45:21 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:45:21 INFO mapred.LocalJobRunner: 
19/05/19 19:45:21 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:45:21 INFO mapred.Task: Task:attempt_local467097207_0001_m_000001_0 is done. And is in the process of committing
19/05/19 19:45:21 INFO mapred.LocalJobRunner: map
19/05/19 19:45:21 INFO mapred.Task: Task 'attempt_local467097207_0001_m_000001_0' done.
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Finishing task: attempt_local467097207_0001_m_000001_0
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Starting task: attempt_local467097207_0001_m_000002_0
19/05/19 19:45:21 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:45:21 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:45:21 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/capacity-scheduler.xml:0+4436
19/05/19 19:45:21 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:45:21 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:45:21 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:45:21 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:45:21 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:45:21 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:45:21 INFO mapred.LocalJobRunner: 
19/05/19 19:45:21 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:45:21 INFO mapred.Task: Task:attempt_local467097207_0001_m_000002_0 is done. And is in the process of committing
19/05/19 19:45:21 INFO mapred.LocalJobRunner: map
19/05/19 19:45:21 INFO mapred.Task: Task 'attempt_local467097207_0001_m_000002_0' done.
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Finishing task: attempt_local467097207_0001_m_000002_0
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Starting task: attempt_local467097207_0001_m_000003_0
19/05/19 19:45:21 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:45:21 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:45:21 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/kms-acls.xml:0+3518
19/05/19 19:45:21 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:45:21 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:45:21 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:45:21 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:45:21 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:45:21 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:45:21 INFO mapred.LocalJobRunner: 
19/05/19 19:45:21 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:45:21 INFO mapred.Task: Task:attempt_local467097207_0001_m_000003_0 is done. And is in the process of committing
19/05/19 19:45:21 INFO mapred.LocalJobRunner: map
19/05/19 19:45:21 INFO mapred.Task: Task 'attempt_local467097207_0001_m_000003_0' done.
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Finishing task: attempt_local467097207_0001_m_000003_0
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Starting task: attempt_local467097207_0001_m_000004_0
19/05/19 19:45:21 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:45:21 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:45:21 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/hdfs-site.xml:0+775
19/05/19 19:45:21 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:45:21 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:45:21 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:45:21 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:45:21 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:45:21 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:45:21 INFO mapred.LocalJobRunner: 
19/05/19 19:45:21 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:45:21 INFO mapred.Task: Task:attempt_local467097207_0001_m_000004_0 is done. And is in the process of committing
19/05/19 19:45:21 INFO mapred.LocalJobRunner: map
19/05/19 19:45:21 INFO mapred.Task: Task 'attempt_local467097207_0001_m_000004_0' done.
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Finishing task: attempt_local467097207_0001_m_000004_0
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Starting task: attempt_local467097207_0001_m_000005_0
19/05/19 19:45:21 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:45:21 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:45:21 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/core-site.xml:0+774
19/05/19 19:45:21 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:45:21 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:45:21 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:45:21 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:45:21 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:45:21 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:45:21 INFO mapred.LocalJobRunner: 
19/05/19 19:45:21 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:45:21 INFO mapred.Task: Task:attempt_local467097207_0001_m_000005_0 is done. And is in the process of committing
19/05/19 19:45:21 INFO mapred.LocalJobRunner: map
19/05/19 19:45:21 INFO mapred.Task: Task 'attempt_local467097207_0001_m_000005_0' done.
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Finishing task: attempt_local467097207_0001_m_000005_0
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Starting task: attempt_local467097207_0001_m_000006_0
19/05/19 19:45:21 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:45:21 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:45:21 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/yarn-site.xml:0+690
19/05/19 19:45:21 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:45:21 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:45:21 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:45:21 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:45:21 INFO mapreduce.Job: Job job_local467097207_0001 running in uber mode : false
19/05/19 19:45:21 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:45:21 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:45:21 INFO mapred.LocalJobRunner: 
19/05/19 19:45:21 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:45:21 INFO mapred.Task: Task:attempt_local467097207_0001_m_000006_0 is done. And is in the process of committing
19/05/19 19:45:21 INFO mapred.LocalJobRunner: map
19/05/19 19:45:21 INFO mapred.Task: Task 'attempt_local467097207_0001_m_000006_0' done.
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Finishing task: attempt_local467097207_0001_m_000006_0
19/05/19 19:45:21 INFO mapreduce.Job:  map 100% reduce 0%
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Starting task: attempt_local467097207_0001_m_000007_0
19/05/19 19:45:21 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:45:21 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:45:21 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/httpfs-site.xml:0+620
19/05/19 19:45:21 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:45:21 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:45:21 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:45:21 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:45:21 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:45:21 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:45:21 INFO mapred.LocalJobRunner: 
19/05/19 19:45:21 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:45:21 INFO mapred.Task: Task:attempt_local467097207_0001_m_000007_0 is done. And is in the process of committing
19/05/19 19:45:21 INFO mapred.LocalJobRunner: map
19/05/19 19:45:21 INFO mapred.Task: Task 'attempt_local467097207_0001_m_000007_0' done.
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Finishing task: attempt_local467097207_0001_m_000007_0
19/05/19 19:45:21 INFO mapred.LocalJobRunner: map task executor complete.
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Waiting for reduce tasks
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Starting task: attempt_local467097207_0001_r_000000_0
19/05/19 19:45:21 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:45:21 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:45:21 INFO mapred.ReduceTask: Using ShuffleConsumerPlugin: org.apache.hadoop.mapreduce.task.reduce.Shuffle@3e79130e
19/05/19 19:45:21 INFO reduce.MergeManagerImpl: MergerManager: memoryLimit=334338464, maxSingleShuffleLimit=83584616, mergeThreshold=220663392, ioSortFactor=10, memToMemMergeOutputsThreshold=10
19/05/19 19:45:21 INFO reduce.EventFetcher: attempt_local467097207_0001_r_000000_0 Thread started: EventFetcher for fetching Map Completion Events
19/05/19 19:45:21 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local467097207_0001_m_000003_0 decomp: 2 len: 6 to MEMORY
19/05/19 19:45:21 INFO reduce.InMemoryMapOutput: Read 2 bytes from map-output for attempt_local467097207_0001_m_000003_0
19/05/19 19:45:21 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 2, inMemoryMapOutputs.size() -> 1, commitMemory -> 0, usedMemory ->2
19/05/19 19:45:21 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local467097207_0001_m_000000_0 decomp: 21 len: 25 to MEMORY
19/05/19 19:45:21 INFO reduce.InMemoryMapOutput: Read 21 bytes from map-output for attempt_local467097207_0001_m_000000_0
19/05/19 19:45:21 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 21, inMemoryMapOutputs.size() -> 2, commitMemory -> 2, usedMemory ->23
19/05/19 19:45:21 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:45:21 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:45:21 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local467097207_0001_m_000006_0 decomp: 2 len: 6 to MEMORY
19/05/19 19:45:21 INFO reduce.InMemoryMapOutput: Read 2 bytes from map-output for attempt_local467097207_0001_m_000006_0
19/05/19 19:45:21 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 2, inMemoryMapOutputs.size() -> 3, commitMemory -> 23, usedMemory ->25
19/05/19 19:45:21 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local467097207_0001_m_000005_0 decomp: 2 len: 6 to MEMORY
19/05/19 19:45:21 INFO reduce.InMemoryMapOutput: Read 2 bytes from map-output for attempt_local467097207_0001_m_000005_0
19/05/19 19:45:21 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 2, inMemoryMapOutputs.size() -> 4, commitMemory -> 25, usedMemory ->27
19/05/19 19:45:21 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:45:21 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:45:21 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local467097207_0001_m_000002_0 decomp: 2 len: 6 to MEMORY
19/05/19 19:45:21 INFO reduce.InMemoryMapOutput: Read 2 bytes from map-output for attempt_local467097207_0001_m_000002_0
19/05/19 19:45:21 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 2, inMemoryMapOutputs.size() -> 5, commitMemory -> 27, usedMemory ->29
19/05/19 19:45:21 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:45:21 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local467097207_0001_m_000001_0 decomp: 2 len: 6 to MEMORY
19/05/19 19:45:21 INFO reduce.InMemoryMapOutput: Read 2 bytes from map-output for attempt_local467097207_0001_m_000001_0
19/05/19 19:45:21 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 2, inMemoryMapOutputs.size() -> 6, commitMemory -> 29, usedMemory ->31
19/05/19 19:45:21 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:45:21 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local467097207_0001_m_000007_0 decomp: 2 len: 6 to MEMORY
19/05/19 19:45:21 INFO reduce.InMemoryMapOutput: Read 2 bytes from map-output for attempt_local467097207_0001_m_000007_0
19/05/19 19:45:21 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 2, inMemoryMapOutputs.size() -> 7, commitMemory -> 31, usedMemory ->33
19/05/19 19:45:21 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:45:21 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local467097207_0001_m_000004_0 decomp: 2 len: 6 to MEMORY
19/05/19 19:45:21 INFO reduce.InMemoryMapOutput: Read 2 bytes from map-output for attempt_local467097207_0001_m_000004_0
19/05/19 19:45:21 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 2, inMemoryMapOutputs.size() -> 8, commitMemory -> 33, usedMemory ->35
19/05/19 19:45:21 INFO reduce.EventFetcher: EventFetcher is interrupted.. Returning
19/05/19 19:45:21 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:45:21 INFO mapred.LocalJobRunner: 8 / 8 copied.
19/05/19 19:45:21 INFO reduce.MergeManagerImpl: finalMerge called with 8 in-memory map-outputs and 0 on-disk map-outputs
19/05/19 19:45:21 INFO mapred.Merger: Merging 8 sorted segments
19/05/19 19:45:21 INFO mapred.Merger: Down to the last merge-pass, with 1 segments left of total size: 10 bytes
19/05/19 19:45:21 INFO reduce.MergeManagerImpl: Merged 8 segments, 35 bytes to disk to satisfy reduce memory limit
19/05/19 19:45:21 INFO reduce.MergeManagerImpl: Merging 1 files, 25 bytes from disk
19/05/19 19:45:21 INFO reduce.MergeManagerImpl: Merging 0 segments, 0 bytes from memory into reduce
19/05/19 19:45:21 INFO mapred.Merger: Merging 1 sorted segments
19/05/19 19:45:21 INFO mapred.Merger: Down to the last merge-pass, with 1 segments left of total size: 10 bytes
19/05/19 19:45:21 INFO mapred.LocalJobRunner: 8 / 8 copied.
19/05/19 19:45:21 INFO Configuration.deprecation: mapred.skip.on is deprecated. Instead, use mapreduce.job.skiprecords
19/05/19 19:45:21 INFO mapred.Task: Task:attempt_local467097207_0001_r_000000_0 is done. And is in the process of committing
19/05/19 19:45:21 INFO mapred.LocalJobRunner: 8 / 8 copied.
19/05/19 19:45:21 INFO mapred.Task: Task attempt_local467097207_0001_r_000000_0 is allowed to commit now
19/05/19 19:45:21 INFO output.FileOutputCommitter: Saved output of task 'attempt_local467097207_0001_r_000000_0' to file:/home/zozo/app/hadoop/hadoop-2.7.2/grep-temp-1124643787/_temporary/0/task_local467097207_0001_r_000000
19/05/19 19:45:21 INFO mapred.LocalJobRunner: reduce > reduce
19/05/19 19:45:21 INFO mapred.Task: Task 'attempt_local467097207_0001_r_000000_0' done.
19/05/19 19:45:21 INFO mapred.LocalJobRunner: Finishing task: attempt_local467097207_0001_r_000000_0
19/05/19 19:45:21 INFO mapred.LocalJobRunner: reduce task executor complete.
19/05/19 19:45:22 INFO mapreduce.Job:  map 100% reduce 100%
19/05/19 19:45:22 INFO mapreduce.Job: Job job_local467097207_0001 completed successfully
19/05/19 19:45:22 INFO mapreduce.Job: Counters: 30
	File System Counters
		FILE: Number of bytes read=2694407
		FILE: Number of bytes written=4990826
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
	Map-Reduce Framework
		Map input records=745
		Map output records=1
		Map output bytes=17
		Map output materialized bytes=67
		Input split bytes=1005
		Combine input records=1
		Combine output records=1
		Reduce input groups=1
		Reduce shuffle bytes=67
		Reduce input records=1
		Reduce output records=1
		Spilled Records=2
		Shuffled Maps =8
		Failed Shuffles=0
		Merged Map outputs=8
		GC time elapsed (ms)=180
		Total committed heap usage (bytes)=2785542144
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters 
		Bytes Read=26007
	File Output Format Counters 
		Bytes Written=123
19/05/19 19:45:22 INFO jvm.JvmMetrics: Cannot initialize JVM Metrics with processName=JobTracker, sessionId= - already initialized
19/05/19 19:45:22 INFO input.FileInputFormat: Total input paths to process : 1
19/05/19 19:45:22 INFO mapreduce.JobSubmitter: number of splits:1
19/05/19 19:45:22 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_local1407018521_0002
19/05/19 19:45:22 INFO mapreduce.Job: The url to track the job: http://localhost:8080/
19/05/19 19:45:22 INFO mapreduce.Job: Running job: job_local1407018521_0002
19/05/19 19:45:22 INFO mapred.LocalJobRunner: OutputCommitter set in config null
19/05/19 19:45:22 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:45:22 INFO mapred.LocalJobRunner: OutputCommitter is org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter
19/05/19 19:45:23 INFO mapred.LocalJobRunner: Waiting for map tasks
19/05/19 19:45:23 INFO mapred.LocalJobRunner: Starting task: attempt_local1407018521_0002_m_000000_0
19/05/19 19:45:23 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:45:23 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:45:23 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/grep-temp-1124643787/part-r-00000:0+111
19/05/19 19:45:23 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:45:23 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:45:23 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:45:23 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:45:23 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:45:23 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:45:23 INFO mapred.LocalJobRunner: 
19/05/19 19:45:23 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:45:23 INFO mapred.MapTask: Spilling map output
19/05/19 19:45:23 INFO mapred.MapTask: bufstart = 0; bufend = 17; bufvoid = 104857600
19/05/19 19:45:23 INFO mapred.MapTask: kvstart = 26214396(104857584); kvend = 26214396(104857584); length = 1/6553600
19/05/19 19:45:23 INFO mapred.MapTask: Finished spill 0
19/05/19 19:45:23 INFO mapred.Task: Task:attempt_local1407018521_0002_m_000000_0 is done. And is in the process of committing
19/05/19 19:45:23 INFO mapred.LocalJobRunner: map
19/05/19 19:45:23 INFO mapred.Task: Task 'attempt_local1407018521_0002_m_000000_0' done.
19/05/19 19:45:23 INFO mapred.LocalJobRunner: Finishing task: attempt_local1407018521_0002_m_000000_0
19/05/19 19:45:23 INFO mapred.LocalJobRunner: map task executor complete.
19/05/19 19:45:23 INFO mapred.LocalJobRunner: Waiting for reduce tasks
19/05/19 19:45:23 INFO mapred.LocalJobRunner: Starting task: attempt_local1407018521_0002_r_000000_0
19/05/19 19:45:23 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:45:23 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:45:23 INFO mapred.ReduceTask: Using ShuffleConsumerPlugin: org.apache.hadoop.mapreduce.task.reduce.Shuffle@4047bfc4
19/05/19 19:45:23 INFO reduce.MergeManagerImpl: MergerManager: memoryLimit=334338464, maxSingleShuffleLimit=83584616, mergeThreshold=220663392, ioSortFactor=10, memToMemMergeOutputsThreshold=10
19/05/19 19:45:23 INFO reduce.EventFetcher: attempt_local1407018521_0002_r_000000_0 Thread started: EventFetcher for fetching Map Completion Events
19/05/19 19:45:23 INFO reduce.LocalFetcher: localfetcher#2 about to shuffle output of map attempt_local1407018521_0002_m_000000_0 decomp: 21 len: 25 to MEMORY
19/05/19 19:45:23 INFO reduce.InMemoryMapOutput: Read 21 bytes from map-output for attempt_local1407018521_0002_m_000000_0
19/05/19 19:45:23 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 21, inMemoryMapOutputs.size() -> 1, commitMemory -> 0, usedMemory ->21
19/05/19 19:45:23 INFO reduce.EventFetcher: EventFetcher is interrupted.. Returning
19/05/19 19:45:23 INFO mapred.LocalJobRunner: 1 / 1 copied.
19/05/19 19:45:23 INFO reduce.MergeManagerImpl: finalMerge called with 1 in-memory map-outputs and 0 on-disk map-outputs
19/05/19 19:45:23 INFO mapred.Merger: Merging 1 sorted segments
19/05/19 19:45:23 INFO mapred.Merger: Down to the last merge-pass, with 1 segments left of total size: 11 bytes
19/05/19 19:45:23 INFO reduce.MergeManagerImpl: Merged 1 segments, 21 bytes to disk to satisfy reduce memory limit
19/05/19 19:45:23 INFO reduce.MergeManagerImpl: Merging 1 files, 25 bytes from disk
19/05/19 19:45:23 INFO reduce.MergeManagerImpl: Merging 0 segments, 0 bytes from memory into reduce
19/05/19 19:45:23 INFO mapred.Merger: Merging 1 sorted segments
19/05/19 19:45:23 INFO mapred.Merger: Down to the last merge-pass, with 1 segments left of total size: 11 bytes
19/05/19 19:45:23 INFO mapred.LocalJobRunner: 1 / 1 copied.
19/05/19 19:45:23 INFO mapred.Task: Task:attempt_local1407018521_0002_r_000000_0 is done. And is in the process of committing
19/05/19 19:45:23 INFO mapred.LocalJobRunner: 1 / 1 copied.
19/05/19 19:45:23 INFO mapred.Task: Task attempt_local1407018521_0002_r_000000_0 is allowed to commit now
19/05/19 19:45:23 INFO output.FileOutputCommitter: Saved output of task 'attempt_local1407018521_0002_r_000000_0' to file:/home/zozo/app/hadoop/hadoop-2.7.2/output/_temporary/0/task_local1407018521_0002_r_000000
19/05/19 19:45:23 INFO mapred.LocalJobRunner: reduce > reduce
19/05/19 19:45:23 INFO mapred.Task: Task 'attempt_local1407018521_0002_r_000000_0' done.
19/05/19 19:45:23 INFO mapred.LocalJobRunner: Finishing task: attempt_local1407018521_0002_r_000000_0
19/05/19 19:45:23 INFO mapred.LocalJobRunner: reduce task executor complete.
19/05/19 19:45:23 INFO mapreduce.Job: Job job_local1407018521_0002 running in uber mode : false
19/05/19 19:45:23 INFO mapreduce.Job:  map 100% reduce 100%
19/05/19 19:45:23 INFO mapreduce.Job: Job job_local1407018521_0002 completed successfully
19/05/19 19:45:23 INFO mapreduce.Job: Counters: 30
	File System Counters
		FILE: Number of bytes read=1159828
		FILE: Number of bytes written=2216994
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
	Map-Reduce Framework
		Map input records=1
		Map output records=1
		Map output bytes=17
		Map output materialized bytes=25
		Input split bytes=138
		Combine input records=0
		Combine output records=0
		Reduce input groups=1
		Reduce shuffle bytes=25
		Reduce input records=1
		Reduce output records=1
		Spilled Records=2
		Shuffled Maps =1
		Failed Shuffles=0
		Merged Map outputs=1
		GC time elapsed (ms)=0
		Total committed heap usage (bytes)=918552576
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters 
		Bytes Read=123
	File Output Format Counters 
		Bytes Written=23
[zozo@VM_0_17_centos hadoop-2.7.2]$ ll
总用量 60
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 bin
drwxr-xr-x 3 zozo zozo  4096 1月  26 2016 etc
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 include
drwxrwxr-x 2 zozo zozo  4096 5月  19 19:44 input
drwxr-xr-x 3 zozo zozo  4096 1月  26 2016 lib
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 libexec
-rw-r--r-- 1 zozo zozo 15429 1月  26 2016 LICENSE.txt
-rw-r--r-- 1 zozo zozo   101 1月  26 2016 NOTICE.txt
drwxrwxr-x 2 zozo zozo  4096 5月  19 19:45 output
-rw-r--r-- 1 zozo zozo  1366 1月  26 2016 README.txt
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 sbin
drwxr-xr-x 4 zozo zozo  4096 1月  26 2016 share
[zozo@VM_0_17_centos hadoop-2.7.2]$ ll output/
总用量 4
-rw-r--r-- 1 zozo zozo 11 5月  19 19:45 part-r-00000
-rw-r--r-- 1 zozo zozo  0 5月  19 19:45 _SUCCESS
[zozo@VM_0_17_centos hadoop-2.7.2]$ cat output/part-r-00000 
1	dfsadmin
[zozo@VM_0_17_centos hadoop-2.7.2]$ 
```

- 注意: 运行前需要确保 output 文件夹不存在, 否则会抛出异常, 如下所示:
```bash
[zozo@VM_0_17_centos hadoop-2.7.2]$ ll
总用量 60
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 bin
drwxr-xr-x 3 zozo zozo  4096 1月  26 2016 etc
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 include
drwxrwxr-x 2 zozo zozo  4096 5月  19 19:44 input
drwxr-xr-x 3 zozo zozo  4096 1月  26 2016 lib
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 libexec
-rw-r--r-- 1 zozo zozo 15429 1月  26 2016 LICENSE.txt
-rw-r--r-- 1 zozo zozo   101 1月  26 2016 NOTICE.txt
drwxrwxr-x 2 zozo zozo  4096 5月  19 19:45 output
-rw-r--r-- 1 zozo zozo  1366 1月  26 2016 README.txt
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 sbin
drwxr-xr-x 4 zozo zozo  4096 1月  26 2016 share
[zozo@VM_0_17_centos hadoop-2.7.2]$ bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar grep input output 'dfs[a-z.]+'
19/05/19 19:50:34 INFO Configuration.deprecation: session.id is deprecated. Instead, use dfs.metrics.session-id
19/05/19 19:50:34 INFO jvm.JvmMetrics: Initializing JVM Metrics with processName=JobTracker, sessionId=
19/05/19 19:50:35 INFO input.FileInputFormat: Total input paths to process : 8
19/05/19 19:50:35 INFO mapreduce.JobSubmitter: number of splits:8
19/05/19 19:50:35 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_local1984050036_0001
19/05/19 19:50:35 INFO mapreduce.Job: The url to track the job: http://localhost:8080/
19/05/19 19:50:35 INFO mapreduce.Job: Running job: job_local1984050036_0001
19/05/19 19:50:35 INFO mapred.LocalJobRunner: OutputCommitter set in config null
19/05/19 19:50:35 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:50:35 INFO mapred.LocalJobRunner: OutputCommitter is org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter
19/05/19 19:50:35 INFO mapred.LocalJobRunner: Waiting for map tasks
19/05/19 19:50:35 INFO mapred.LocalJobRunner: Starting task: attempt_local1984050036_0001_m_000000_0
19/05/19 19:50:35 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:50:35 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:50:35 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/hadoop-policy.xml:0+9683
19/05/19 19:50:35 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:50:35 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:50:35 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:50:35 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:50:35 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:50:35 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:50:35 INFO mapred.LocalJobRunner: 
19/05/19 19:50:35 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:50:35 INFO mapred.MapTask: Spilling map output
19/05/19 19:50:35 INFO mapred.MapTask: bufstart = 0; bufend = 17; bufvoid = 104857600
19/05/19 19:50:35 INFO mapred.MapTask: kvstart = 26214396(104857584); kvend = 26214396(104857584); length = 1/6553600
19/05/19 19:50:35 INFO mapred.MapTask: Finished spill 0
19/05/19 19:50:35 INFO mapred.Task: Task:attempt_local1984050036_0001_m_000000_0 is done. And is in the process of committing
19/05/19 19:50:35 INFO mapred.LocalJobRunner: map
19/05/19 19:50:35 INFO mapred.Task: Task 'attempt_local1984050036_0001_m_000000_0' done.
19/05/19 19:50:35 INFO mapred.LocalJobRunner: Finishing task: attempt_local1984050036_0001_m_000000_0
19/05/19 19:50:35 INFO mapred.LocalJobRunner: Starting task: attempt_local1984050036_0001_m_000001_0
19/05/19 19:50:35 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:50:35 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:50:35 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/kms-site.xml:0+5511
19/05/19 19:50:35 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:50:35 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:50:35 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:50:35 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:50:35 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:50:35 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:50:35 INFO mapred.LocalJobRunner: 
19/05/19 19:50:35 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:50:35 INFO mapred.Task: Task:attempt_local1984050036_0001_m_000001_0 is done. And is in the process of committing
19/05/19 19:50:35 INFO mapred.LocalJobRunner: map
19/05/19 19:50:35 INFO mapred.Task: Task 'attempt_local1984050036_0001_m_000001_0' done.
19/05/19 19:50:35 INFO mapred.LocalJobRunner: Finishing task: attempt_local1984050036_0001_m_000001_0
19/05/19 19:50:35 INFO mapred.LocalJobRunner: Starting task: attempt_local1984050036_0001_m_000002_0
19/05/19 19:50:35 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:50:35 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:50:35 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/capacity-scheduler.xml:0+4436
19/05/19 19:50:35 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:50:35 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:50:35 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:50:35 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:50:35 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:50:35 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:50:35 INFO mapred.LocalJobRunner: 
19/05/19 19:50:35 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:50:35 INFO mapred.Task: Task:attempt_local1984050036_0001_m_000002_0 is done. And is in the process of committing
19/05/19 19:50:35 INFO mapred.LocalJobRunner: map
19/05/19 19:50:35 INFO mapred.Task: Task 'attempt_local1984050036_0001_m_000002_0' done.
19/05/19 19:50:35 INFO mapred.LocalJobRunner: Finishing task: attempt_local1984050036_0001_m_000002_0
19/05/19 19:50:35 INFO mapred.LocalJobRunner: Starting task: attempt_local1984050036_0001_m_000003_0
19/05/19 19:50:35 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:50:35 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:50:35 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/kms-acls.xml:0+3518
19/05/19 19:50:35 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:50:35 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:50:35 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:50:35 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:50:35 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:50:35 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:50:35 INFO mapred.LocalJobRunner: 
19/05/19 19:50:35 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:50:35 INFO mapred.Task: Task:attempt_local1984050036_0001_m_000003_0 is done. And is in the process of committing
19/05/19 19:50:35 INFO mapred.LocalJobRunner: map
19/05/19 19:50:35 INFO mapred.Task: Task 'attempt_local1984050036_0001_m_000003_0' done.
19/05/19 19:50:35 INFO mapred.LocalJobRunner: Finishing task: attempt_local1984050036_0001_m_000003_0
19/05/19 19:50:35 INFO mapred.LocalJobRunner: Starting task: attempt_local1984050036_0001_m_000004_0
19/05/19 19:50:35 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:50:35 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:50:35 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/hdfs-site.xml:0+775
19/05/19 19:50:35 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:50:35 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:50:35 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:50:35 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:50:35 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:50:35 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:50:35 INFO mapred.LocalJobRunner: 
19/05/19 19:50:35 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:50:35 INFO mapred.Task: Task:attempt_local1984050036_0001_m_000004_0 is done. And is in the process of committing
19/05/19 19:50:35 INFO mapred.LocalJobRunner: map
19/05/19 19:50:35 INFO mapred.Task: Task 'attempt_local1984050036_0001_m_000004_0' done.
19/05/19 19:50:35 INFO mapred.LocalJobRunner: Finishing task: attempt_local1984050036_0001_m_000004_0
19/05/19 19:50:35 INFO mapred.LocalJobRunner: Starting task: attempt_local1984050036_0001_m_000005_0
19/05/19 19:50:35 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:50:35 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:50:35 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/core-site.xml:0+774
19/05/19 19:50:35 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:50:35 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:50:35 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:50:35 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:50:35 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:50:35 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:50:35 INFO mapred.LocalJobRunner: 
19/05/19 19:50:35 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:50:35 INFO mapred.Task: Task:attempt_local1984050036_0001_m_000005_0 is done. And is in the process of committing
19/05/19 19:50:35 INFO mapred.LocalJobRunner: map
19/05/19 19:50:35 INFO mapred.Task: Task 'attempt_local1984050036_0001_m_000005_0' done.
19/05/19 19:50:35 INFO mapred.LocalJobRunner: Finishing task: attempt_local1984050036_0001_m_000005_0
19/05/19 19:50:35 INFO mapred.LocalJobRunner: Starting task: attempt_local1984050036_0001_m_000006_0
19/05/19 19:50:35 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:50:35 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:50:35 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/yarn-site.xml:0+690
19/05/19 19:50:36 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:50:36 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:50:36 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:50:36 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:50:36 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:50:36 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:50:36 INFO mapred.LocalJobRunner: 
19/05/19 19:50:36 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:50:36 INFO mapred.Task: Task:attempt_local1984050036_0001_m_000006_0 is done. And is in the process of committing
19/05/19 19:50:36 INFO mapred.LocalJobRunner: map
19/05/19 19:50:36 INFO mapred.Task: Task 'attempt_local1984050036_0001_m_000006_0' done.
19/05/19 19:50:36 INFO mapred.LocalJobRunner: Finishing task: attempt_local1984050036_0001_m_000006_0
19/05/19 19:50:36 INFO mapred.LocalJobRunner: Starting task: attempt_local1984050036_0001_m_000007_0
19/05/19 19:50:36 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:50:36 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:50:36 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input/httpfs-site.xml:0+620
19/05/19 19:50:36 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 19:50:36 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 19:50:36 INFO mapred.MapTask: soft limit at 83886080
19/05/19 19:50:36 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 19:50:36 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 19:50:36 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 19:50:36 INFO mapred.LocalJobRunner: 
19/05/19 19:50:36 INFO mapred.MapTask: Starting flush of map output
19/05/19 19:50:36 INFO mapred.Task: Task:attempt_local1984050036_0001_m_000007_0 is done. And is in the process of committing
19/05/19 19:50:36 INFO mapred.LocalJobRunner: map
19/05/19 19:50:36 INFO mapred.Task: Task 'attempt_local1984050036_0001_m_000007_0' done.
19/05/19 19:50:36 INFO mapred.LocalJobRunner: Finishing task: attempt_local1984050036_0001_m_000007_0
19/05/19 19:50:36 INFO mapred.LocalJobRunner: map task executor complete.
19/05/19 19:50:36 INFO mapred.LocalJobRunner: Waiting for reduce tasks
19/05/19 19:50:36 INFO mapred.LocalJobRunner: Starting task: attempt_local1984050036_0001_r_000000_0
19/05/19 19:50:36 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 19:50:36 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 19:50:36 INFO mapred.ReduceTask: Using ShuffleConsumerPlugin: org.apache.hadoop.mapreduce.task.reduce.Shuffle@564730a5
19/05/19 19:50:36 INFO reduce.MergeManagerImpl: MergerManager: memoryLimit=334338464, maxSingleShuffleLimit=83584616, mergeThreshold=220663392, ioSortFactor=10, memToMemMergeOutputsThreshold=10
19/05/19 19:50:36 INFO reduce.EventFetcher: attempt_local1984050036_0001_r_000000_0 Thread started: EventFetcher for fetching Map Completion Events
19/05/19 19:50:36 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local1984050036_0001_m_000000_0 decomp: 21 len: 25 to MEMORY
19/05/19 19:50:36 INFO reduce.InMemoryMapOutput: Read 21 bytes from map-output for attempt_local1984050036_0001_m_000000_0
19/05/19 19:50:36 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 21, inMemoryMapOutputs.size() -> 1, commitMemory -> 0, usedMemory ->21
19/05/19 19:50:36 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:50:36 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local1984050036_0001_m_000003_0 decomp: 2 len: 6 to MEMORY
19/05/19 19:50:36 INFO reduce.InMemoryMapOutput: Read 2 bytes from map-output for attempt_local1984050036_0001_m_000003_0
19/05/19 19:50:36 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 2, inMemoryMapOutputs.size() -> 2, commitMemory -> 21, usedMemory ->23
19/05/19 19:50:36 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local1984050036_0001_m_000006_0 decomp: 2 len: 6 to MEMORY
19/05/19 19:50:36 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:50:36 INFO reduce.InMemoryMapOutput: Read 2 bytes from map-output for attempt_local1984050036_0001_m_000006_0
19/05/19 19:50:36 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 2, inMemoryMapOutputs.size() -> 3, commitMemory -> 23, usedMemory ->25
19/05/19 19:50:36 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local1984050036_0001_m_000002_0 decomp: 2 len: 6 to MEMORY
19/05/19 19:50:36 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:50:36 INFO reduce.InMemoryMapOutput: Read 2 bytes from map-output for attempt_local1984050036_0001_m_000002_0
19/05/19 19:50:36 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 2, inMemoryMapOutputs.size() -> 4, commitMemory -> 25, usedMemory ->27
19/05/19 19:50:36 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local1984050036_0001_m_000005_0 decomp: 2 len: 6 to MEMORY
19/05/19 19:50:36 INFO reduce.InMemoryMapOutput: Read 2 bytes from map-output for attempt_local1984050036_0001_m_000005_0
19/05/19 19:50:36 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 2, inMemoryMapOutputs.size() -> 5, commitMemory -> 27, usedMemory ->29
19/05/19 19:50:36 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local1984050036_0001_m_000004_0 decomp: 2 len: 6 to MEMORY
19/05/19 19:50:36 INFO reduce.InMemoryMapOutput: Read 2 bytes from map-output for attempt_local1984050036_0001_m_000004_0
19/05/19 19:50:36 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 2, inMemoryMapOutputs.size() -> 6, commitMemory -> 29, usedMemory ->31
19/05/19 19:50:36 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local1984050036_0001_m_000007_0 decomp: 2 len: 6 to MEMORY
19/05/19 19:50:36 INFO reduce.InMemoryMapOutput: Read 2 bytes from map-output for attempt_local1984050036_0001_m_000007_0
19/05/19 19:50:36 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 2, inMemoryMapOutputs.size() -> 7, commitMemory -> 31, usedMemory ->33
19/05/19 19:50:36 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local1984050036_0001_m_000001_0 decomp: 2 len: 6 to MEMORY
19/05/19 19:50:36 INFO reduce.InMemoryMapOutput: Read 2 bytes from map-output for attempt_local1984050036_0001_m_000001_0
19/05/19 19:50:36 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 2, inMemoryMapOutputs.size() -> 8, commitMemory -> 33, usedMemory ->35
19/05/19 19:50:36 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:50:36 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:50:36 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:50:36 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:50:36 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 19:50:36 INFO reduce.EventFetcher: EventFetcher is interrupted.. Returning
19/05/19 19:50:36 INFO mapred.LocalJobRunner: 8 / 8 copied.
19/05/19 19:50:36 INFO reduce.MergeManagerImpl: finalMerge called with 8 in-memory map-outputs and 0 on-disk map-outputs
19/05/19 19:50:36 INFO mapreduce.Job: Job job_local1984050036_0001 running in uber mode : false
19/05/19 19:50:36 INFO mapreduce.Job:  map 100% reduce 0%
19/05/19 19:50:36 INFO mapred.Merger: Merging 8 sorted segments
19/05/19 19:50:36 INFO mapred.Merger: Down to the last merge-pass, with 1 segments left of total size: 10 bytes
19/05/19 19:50:36 INFO reduce.MergeManagerImpl: Merged 8 segments, 35 bytes to disk to satisfy reduce memory limit
19/05/19 19:50:36 INFO reduce.MergeManagerImpl: Merging 1 files, 25 bytes from disk
19/05/19 19:50:36 INFO reduce.MergeManagerImpl: Merging 0 segments, 0 bytes from memory into reduce
19/05/19 19:50:36 INFO mapred.Merger: Merging 1 sorted segments
19/05/19 19:50:36 INFO mapred.Merger: Down to the last merge-pass, with 1 segments left of total size: 10 bytes
19/05/19 19:50:36 INFO mapred.LocalJobRunner: 8 / 8 copied.
19/05/19 19:50:36 INFO Configuration.deprecation: mapred.skip.on is deprecated. Instead, use mapreduce.job.skiprecords
19/05/19 19:50:36 INFO mapred.Task: Task:attempt_local1984050036_0001_r_000000_0 is done. And is in the process of committing
19/05/19 19:50:36 INFO mapred.LocalJobRunner: 8 / 8 copied.
19/05/19 19:50:36 INFO mapred.Task: Task attempt_local1984050036_0001_r_000000_0 is allowed to commit now
19/05/19 19:50:36 INFO output.FileOutputCommitter: Saved output of task 'attempt_local1984050036_0001_r_000000_0' to file:/home/zozo/app/hadoop/hadoop-2.7.2/grep-temp-1716745456/_temporary/0/task_local1984050036_0001_r_000000
19/05/19 19:50:36 INFO mapred.LocalJobRunner: reduce > reduce
19/05/19 19:50:36 INFO mapred.Task: Task 'attempt_local1984050036_0001_r_000000_0' done.
19/05/19 19:50:36 INFO mapred.LocalJobRunner: Finishing task: attempt_local1984050036_0001_r_000000_0
19/05/19 19:50:36 INFO mapred.LocalJobRunner: reduce task executor complete.
19/05/19 19:50:37 INFO mapreduce.Job:  map 100% reduce 100%
19/05/19 19:50:37 INFO mapreduce.Job: Job job_local1984050036_0001 completed successfully
19/05/19 19:50:37 INFO mapreduce.Job: Counters: 30
	File System Counters
		FILE: Number of bytes read=2694407
		FILE: Number of bytes written=5004416
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
	Map-Reduce Framework
		Map input records=745
		Map output records=1
		Map output bytes=17
		Map output materialized bytes=67
		Input split bytes=1005
		Combine input records=1
		Combine output records=1
		Reduce input groups=1
		Reduce shuffle bytes=67
		Reduce input records=1
		Reduce output records=1
		Spilled Records=2
		Shuffled Maps =8
		Failed Shuffles=0
		Merged Map outputs=8
		GC time elapsed (ms)=268
		Total committed heap usage (bytes)=2718433280
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters 
		Bytes Read=26007
	File Output Format Counters 
		Bytes Written=123
19/05/19 19:50:37 INFO jvm.JvmMetrics: Cannot initialize JVM Metrics with processName=JobTracker, sessionId= - already initialized
org.apache.hadoop.mapred.FileAlreadyExistsException: Output directory file:/home/zozo/app/hadoop/hadoop-2.7.2/output already exists
	at org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.checkOutputSpecs(FileOutputFormat.java:146)
	at org.apache.hadoop.mapreduce.JobSubmitter.checkSpecs(JobSubmitter.java:266)
	at org.apache.hadoop.mapreduce.JobSubmitter.submitJobInternal(JobSubmitter.java:139)
	at org.apache.hadoop.mapreduce.Job$10.run(Job.java:1290)
	at org.apache.hadoop.mapreduce.Job$10.run(Job.java:1287)
	at java.security.AccessController.doPrivileged(Native Method)
	at javax.security.auth.Subject.doAs(Subject.java:422)
	at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1657)
	at org.apache.hadoop.mapreduce.Job.submit(Job.java:1287)
	at org.apache.hadoop.mapreduce.Job.waitForCompletion(Job.java:1308)
	at org.apache.hadoop.examples.Grep.run(Grep.java:94)
	at org.apache.hadoop.util.ToolRunner.run(ToolRunner.java:70)
	at org.apache.hadoop.examples.Grep.main(Grep.java:103)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.hadoop.util.ProgramDriver$ProgramDescription.invoke(ProgramDriver.java:71)
	at org.apache.hadoop.util.ProgramDriver.run(ProgramDriver.java:144)
	at org.apache.hadoop.examples.ExampleDriver.main(ExampleDriver.java:74)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.hadoop.util.RunJar.run(RunJar.java:221)
	at org.apache.hadoop.util.RunJar.main(RunJar.java:136)
[zozo@VM_0_17_centos hadoop-2.7.2]$ 
```

## 1.2 WordCount 案例

统计单词出现到次数.

```
[zozo@VM_0_17_centos hadoop-2.7.2]$ mkdir input-wordcount
[zozo@VM_0_17_centos hadoop-2.7.2]$ ll
总用量 64
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 bin
drwxr-xr-x 3 zozo zozo  4096 1月  26 2016 etc
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 include
drwxrwxr-x 2 zozo zozo  4096 5月  19 19:44 input
drwxrwxr-x 2 zozo zozo  4096 5月  19 21:10 input-wordcount
drwxr-xr-x 3 zozo zozo  4096 1月  26 2016 lib
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 libexec
-rw-r--r-- 1 zozo zozo 15429 1月  26 2016 LICENSE.txt
-rw-r--r-- 1 zozo zozo   101 1月  26 2016 NOTICE.txt
drwxrwxr-x 2 zozo zozo  4096 5月  19 19:45 output
-rw-r--r-- 1 zozo zozo  1366 1月  26 2016 README.txt
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 sbin
drwxr-xr-x 4 zozo zozo  4096 1月  26 2016 share
[zozo@VM_0_17_centos hadoop-2.7.2]$ cd input-wordcount/
[zozo@VM_0_17_centos input-wordcount]$ vi wordcount.input
[zozo@VM_0_17_centos input-wordcount]$ cat wordcount.input 
hadoop yarn
hadoop mapreduce
yarn
zozo zozo

good
[zozo@VM_0_17_centos input-wordcount]$ cd ..
[zozo@VM_0_17_centos hadoop-2.7.2]$ bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar wordcount input-wordcount output-wordcount
19/05/19 21:14:17 INFO Configuration.deprecation: session.id is deprecated. Instead, use dfs.metrics.session-id
19/05/19 21:14:17 INFO jvm.JvmMetrics: Initializing JVM Metrics with processName=JobTracker, sessionId=
19/05/19 21:14:18 INFO input.FileInputFormat: Total input paths to process : 1
19/05/19 21:14:18 INFO mapreduce.JobSubmitter: number of splits:1
19/05/19 21:14:18 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_local1435582805_0001
19/05/19 21:14:18 INFO mapreduce.Job: The url to track the job: http://localhost:8080/
19/05/19 21:14:18 INFO mapreduce.Job: Running job: job_local1435582805_0001
19/05/19 21:14:18 INFO mapred.LocalJobRunner: OutputCommitter set in config null
19/05/19 21:14:18 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 21:14:18 INFO mapred.LocalJobRunner: OutputCommitter is org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter
19/05/19 21:14:18 INFO mapred.LocalJobRunner: Waiting for map tasks
19/05/19 21:14:18 INFO mapred.LocalJobRunner: Starting task: attempt_local1435582805_0001_m_000000_0
19/05/19 21:14:18 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 21:14:18 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 21:14:18 INFO mapred.MapTask: Processing split: file:/home/zozo/app/hadoop/hadoop-2.7.2/input-wordcount/wordcount.input:0+50
19/05/19 21:14:18 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/19 21:14:18 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/19 21:14:18 INFO mapred.MapTask: soft limit at 83886080
19/05/19 21:14:18 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/19 21:14:18 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/19 21:14:18 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/19 21:14:18 INFO mapred.LocalJobRunner: 
19/05/19 21:14:18 INFO mapred.MapTask: Starting flush of map output
19/05/19 21:14:18 INFO mapred.MapTask: Spilling map output
19/05/19 21:14:18 INFO mapred.MapTask: bufstart = 0; bufend = 81; bufvoid = 104857600
19/05/19 21:14:18 INFO mapred.MapTask: kvstart = 26214396(104857584); kvend = 26214368(104857472); length = 29/6553600
19/05/19 21:14:18 INFO mapred.MapTask: Finished spill 0
19/05/19 21:14:18 INFO mapred.Task: Task:attempt_local1435582805_0001_m_000000_0 is done. And is in the process of committing
19/05/19 21:14:18 INFO mapred.LocalJobRunner: map
19/05/19 21:14:18 INFO mapred.Task: Task 'attempt_local1435582805_0001_m_000000_0' done.
19/05/19 21:14:18 INFO mapred.LocalJobRunner: Finishing task: attempt_local1435582805_0001_m_000000_0
19/05/19 21:14:18 INFO mapred.LocalJobRunner: map task executor complete.
19/05/19 21:14:18 INFO mapred.LocalJobRunner: Waiting for reduce tasks
19/05/19 21:14:18 INFO mapred.LocalJobRunner: Starting task: attempt_local1435582805_0001_r_000000_0
19/05/19 21:14:18 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/19 21:14:18 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/19 21:14:18 INFO mapred.ReduceTask: Using ShuffleConsumerPlugin: org.apache.hadoop.mapreduce.task.reduce.Shuffle@aa769df
19/05/19 21:14:18 INFO reduce.MergeManagerImpl: MergerManager: memoryLimit=334338464, maxSingleShuffleLimit=83584616, mergeThreshold=220663392, ioSortFactor=10, memToMemMergeOutputsThreshold=10
19/05/19 21:14:18 INFO reduce.EventFetcher: attempt_local1435582805_0001_r_000000_0 Thread started: EventFetcher for fetching Map Completion Events
19/05/19 21:14:18 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local1435582805_0001_m_000000_0 decomp: 64 len: 68 to MEMORY
19/05/19 21:14:18 INFO reduce.InMemoryMapOutput: Read 64 bytes from map-output for attempt_local1435582805_0001_m_000000_0
19/05/19 21:14:18 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 64, inMemoryMapOutputs.size() -> 1, commitMemory -> 0, usedMemory ->64
19/05/19 21:14:18 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/19 21:14:18 INFO reduce.EventFetcher: EventFetcher is interrupted.. Returning
19/05/19 21:14:18 INFO mapred.LocalJobRunner: 1 / 1 copied.
19/05/19 21:14:18 INFO reduce.MergeManagerImpl: finalMerge called with 1 in-memory map-outputs and 0 on-disk map-outputs
19/05/19 21:14:18 INFO mapred.Merger: Merging 1 sorted segments
19/05/19 21:14:18 INFO mapred.Merger: Down to the last merge-pass, with 1 segments left of total size: 57 bytes
19/05/19 21:14:18 INFO reduce.MergeManagerImpl: Merged 1 segments, 64 bytes to disk to satisfy reduce memory limit
19/05/19 21:14:18 INFO reduce.MergeManagerImpl: Merging 1 files, 68 bytes from disk
19/05/19 21:14:18 INFO reduce.MergeManagerImpl: Merging 0 segments, 0 bytes from memory into reduce
19/05/19 21:14:18 INFO mapred.Merger: Merging 1 sorted segments
19/05/19 21:14:18 INFO mapred.Merger: Down to the last merge-pass, with 1 segments left of total size: 57 bytes
19/05/19 21:14:18 INFO mapred.LocalJobRunner: 1 / 1 copied.
19/05/19 21:14:18 INFO Configuration.deprecation: mapred.skip.on is deprecated. Instead, use mapreduce.job.skiprecords
19/05/19 21:14:18 INFO mapred.Task: Task:attempt_local1435582805_0001_r_000000_0 is done. And is in the process of committing
19/05/19 21:14:18 INFO mapred.LocalJobRunner: 1 / 1 copied.
19/05/19 21:14:18 INFO mapred.Task: Task attempt_local1435582805_0001_r_000000_0 is allowed to commit now
19/05/19 21:14:18 INFO output.FileOutputCommitter: Saved output of task 'attempt_local1435582805_0001_r_000000_0' to file:/home/zozo/app/hadoop/hadoop-2.7.2/output-wordcount/_temporary/0/task_local1435582805_0001_r_000000
19/05/19 21:14:18 INFO mapred.LocalJobRunner: reduce > reduce
19/05/19 21:14:18 INFO mapred.Task: Task 'attempt_local1435582805_0001_r_000000_0' done.
19/05/19 21:14:18 INFO mapred.LocalJobRunner: Finishing task: attempt_local1435582805_0001_r_000000_0
19/05/19 21:14:18 INFO mapred.LocalJobRunner: reduce task executor complete.
19/05/19 21:14:19 INFO mapreduce.Job: Job job_local1435582805_0001 running in uber mode : false
19/05/19 21:14:19 INFO mapreduce.Job:  map 100% reduce 100%
19/05/19 21:14:19 INFO mapreduce.Job: Job job_local1435582805_0001 completed successfully
19/05/19 21:14:19 INFO mapreduce.Job: Counters: 30
	File System Counters
		FILE: Number of bytes read=547520
		FILE: Number of bytes written=1108378
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
	Map-Reduce Framework
		Map input records=6
		Map output records=8
		Map output bytes=81
		Map output materialized bytes=68
		Input split bytes=136
		Combine input records=8
		Combine output records=5
		Reduce input groups=5
		Reduce shuffle bytes=68
		Reduce input records=5
		Reduce output records=5
		Spilled Records=10
		Shuffled Maps =1
		Failed Shuffles=0
		Merged Map outputs=1
		GC time elapsed (ms)=11
		Total committed heap usage (bytes)=425721856
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters 
		Bytes Read=50
	File Output Format Counters 
		Bytes Written=54
[zozo@VM_0_17_centos hadoop-2.7.2]$ ll
总用量 68
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 bin
drwxr-xr-x 3 zozo zozo  4096 1月  26 2016 etc
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 include
drwxrwxr-x 2 zozo zozo  4096 5月  19 19:44 input
drwxrwxr-x 2 zozo zozo  4096 5月  19 21:12 input-wordcount
drwxr-xr-x 3 zozo zozo  4096 1月  26 2016 lib
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 libexec
-rw-r--r-- 1 zozo zozo 15429 1月  26 2016 LICENSE.txt
-rw-r--r-- 1 zozo zozo   101 1月  26 2016 NOTICE.txt
drwxrwxr-x 2 zozo zozo  4096 5月  19 19:45 output
drwxrwxr-x 2 zozo zozo  4096 5月  19 21:14 output-wordcount
-rw-r--r-- 1 zozo zozo  1366 1月  26 2016 README.txt
drwxr-xr-x 2 zozo zozo  4096 1月  26 2016 sbin
drwxr-xr-x 4 zozo zozo  4096 1月  26 2016 share
[zozo@VM_0_17_centos hadoop-2.7.2]$ ll output-wordcount/
总用量 4
-rw-r--r-- 1 zozo zozo 42 5月  19 21:14 part-r-00000
-rw-r--r-- 1 zozo zozo  0 5月  19 21:14 _SUCCESS
[zozo@VM_0_17_centos hadoop-2.7.2]$ cat output-wordcount/part-r-00000 
good	1
hadoop	2
mapreduce	1
yarn	2
zozo	2
[zozo@VM_0_17_centos hadoop-2.7.2]$ 
```

---

# 二. 伪分布式运行模式

配置文件说明:
- [core-default.xml](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/core-default.xml)
- [hdfs-default.xml](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/hdfs-default.xml)
- [mapred-default.xml](http://hadoop.apache.org/docs/r2.7.2/hadoop-mapreduce-client/hadoop-mapreduce-client-core/mapred-default.xml)
- [yarn-default.xml](http://hadoop.apache.org/docs/r2.7.2/hadoop-yarn/hadoop-yarn-common/yarn-default.xml)

## 2.1 启动 HDFS 并运行 MapReduce 程序

注: 需要先配置 hostname 和 host

### 2.1.1 step1 修改配置

- 修改配置 `./etc/hadoop/hadoop-env.sh`

```bash
# The only required environment variable is JAVA_HOME.  All others are optional.  When running a distributed configuration it is best to set JAVA_HOME in this file, so that it is correctly defined on remote nodes.
# 唯一需要的环境变量是JAVA_HOME. 所有其他都是可选的. 运行分布式配置时, 最好在此文件中设置 JAVA_HOME, 以便在远程节点上正确定义它.
# The java implementation to use.
# export JAVA_HOME=${JAVA_HOME}

# custom
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
```

- 修改配置 `./etc/hadoop/core-site.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration>

  <!-- 指定 HDFS 中 NameNode 的地址 -->
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://vm017:9000</value>
		<!-- value: file:/// -->
    <description>
      The name of the default file system. A URI whose scheme and authority determine the FileSystem implementation. The uri's scheme determines the config property (fs.SCHEME.impl) naming the FileSystem implementation class. The uri's authority is used to determine the host, port, etc. for a filesystem.
    </description>
  </property>

  <!-- 指定 Hadoop 运行时产生文件的存储目录 -->
  <property>
    <name>hadoop.tmp.dir</name>
    <value>/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp</value>
		<!-- value: /tmp/hadoop-${user.name} -->
    <description>
      A base for other temporary directories.
    </description>
  </property>

<configuration>
```

- 修改配置 `./etc/hadoop/hdfs-site.xml` (可选)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration>

  <!-- 指定 HDFS 副本数 -->
  <property>
    <name>dfs.replication</name>
    <value>1</value>
		<!-- value: 3 -->
    <description>
      Default block replication. The actual number of replications can be specified when the file is created. The default is used if replication is not specified in create time.
    </description>
  </property>

  <!-- namenode 元数据存储目录 -->
  <!--
  <property>
    <name>dfs.namenode.name.dir</name>
    <value>file://${hadoop.tmp.dir}/dfs/name</value>
    <description>
      Determines where on the local filesystem the DFS name node should store the name table(fsimage). If this is a comma-delimited list of directories then the name table is replicated in all of the directories, for redundancy.
    </description>
  </property>
  -->

  <!-- datanode 上数据块的物理存储位置 -->
  <!--
  <property>
    <name>dfs.datanode.data.dir</name>
    <value>file://${hadoop.tmp.dir}/dfs/data</value>
    <description>
      Determines where on the local filesystem an DFS data node should store its blocks. If this is a comma-delimited list of directories, then data will be stored in all named directories, typically on different devices. The directories should be tagged with corresponding storage types ([SSD]/[DISK]/[ARCHIVE]/[RAM_DISK]) for HDFS storage policies. The default storage type will be DISK if the directory does not have a storage type tagged explicitly. Directories that do not exist will be created if local filesystem permission allows.
    </description>
  </property>
  -->

</configuration>
```

### 2.1.2 step2 启动 HDFS (NameNode, DataNode)

- 格式化 NameNode (首次启动)

注: 第一次启动时格式化, 以后就不要总格式化

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs namenode -format
19/05/22 21:19:12 INFO namenode.NameNode: STARTUP_MSG:
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   host = VM_0_17_centos/127.0.0.1
STARTUP_MSG:   args = [-format]
STARTUP_MSG:   version = 2.7.2
STARTUP_MSG:   classpath = /home/zozo/app/hadoop/hadoop-2.7.2/etc/hadoop:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jets3t-0.9.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-1.7.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-configuration-1.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-httpclient-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-kerberos-codec-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-auth-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-framework-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/mockito-all-1.8.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-math3-3.1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/java-xmlbuilder-0.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-api-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/gson-2.2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-asn1-api-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-core-1.8.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-client-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-net-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsp-api-2.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpclient-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-util-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-recipes-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-digester-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpcore-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsch-0.1.42.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-daemon-1.0.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-all-4.0.23.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xml-apis-1.3.04.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xercesImpl-2.9.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-client-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-api-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-registry-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-applicationhistoryservice-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-tests-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-resourcemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-sharedcachemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-nodemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-web-proxy-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-client-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-shuffle-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-app-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-plugins-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/contrib/capacity-scheduler/*.jar
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r b165c4fe8a74265c792ce23f546c64604acf0e41; compiled by 'jenkins' on 2016-01-26T00:08Z
STARTUP_MSG:   java = 1.8.0_192
************************************************************/
19/05/22 21:19:12 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
19/05/22 21:19:12 INFO namenode.NameNode: createNameNode [-format]
Formatting using clusterid: CID-d90a1e0a-07a0-47b4-88dc-eb645b70fd03
19/05/22 21:19:14 INFO namenode.FSNamesystem: No KeyProvider found.
19/05/22 21:19:14 INFO namenode.FSNamesystem: fsLock is fair:true
19/05/22 21:19:14 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
19/05/22 21:19:14 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
19/05/22 21:19:14 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
19/05/22 21:19:14 INFO blockmanagement.BlockManager: The block deletion will start around 2019 五月 22 21:19:14
19/05/22 21:19:14 INFO util.GSet: Computing capacity for map BlocksMap
19/05/22 21:19:14 INFO util.GSet: VM type       = 64-bit
19/05/22 21:19:14 INFO util.GSet: 2.0% max memory 889 MB = 17.8 MB
19/05/22 21:19:14 INFO util.GSet: capacity      = 2^21 = 2097152 entries
19/05/22 21:19:14 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
19/05/22 21:19:14 INFO blockmanagement.BlockManager: defaultReplication         = 1
19/05/22 21:19:14 INFO blockmanagement.BlockManager: maxReplication             = 512
19/05/22 21:19:14 INFO blockmanagement.BlockManager: minReplication             = 1
19/05/22 21:19:14 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
19/05/22 21:19:14 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
19/05/22 21:19:14 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
19/05/22 21:19:14 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
19/05/22 21:19:14 INFO namenode.FSNamesystem: fsOwner             = zozo (auth:SIMPLE)
19/05/22 21:19:14 INFO namenode.FSNamesystem: supergroup          = supergroup
19/05/22 21:19:14 INFO namenode.FSNamesystem: isPermissionEnabled = true
19/05/22 21:19:14 INFO namenode.FSNamesystem: HA Enabled: false
19/05/22 21:19:14 INFO namenode.FSNamesystem: Append Enabled: true
19/05/22 21:19:15 INFO util.GSet: Computing capacity for map INodeMap
19/05/22 21:19:15 INFO util.GSet: VM type       = 64-bit
19/05/22 21:19:15 INFO util.GSet: 1.0% max memory 889 MB = 8.9 MB
19/05/22 21:19:15 INFO util.GSet: capacity      = 2^20 = 1048576 entries
19/05/22 21:19:15 INFO namenode.FSDirectory: ACLs enabled? false
19/05/22 21:19:15 INFO namenode.FSDirectory: XAttrs enabled? true
19/05/22 21:19:15 INFO namenode.FSDirectory: Maximum size of an xattr: 16384
19/05/22 21:19:15 INFO namenode.NameNode: Caching file names occuring more than 10 times
19/05/22 21:19:15 INFO util.GSet: Computing capacity for map cachedBlocks
19/05/22 21:19:15 INFO util.GSet: VM type       = 64-bit
19/05/22 21:19:15 INFO util.GSet: 0.25% max memory 889 MB = 2.2 MB
19/05/22 21:19:15 INFO util.GSet: capacity      = 2^18 = 262144 entries
19/05/22 21:19:15 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
19/05/22 21:19:15 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
19/05/22 21:19:15 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
19/05/22 21:19:15 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.window.num.buckets = 10
19/05/22 21:19:15 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.num.users = 10
19/05/22 21:19:15 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.windows.minutes = 1,5,25
19/05/22 21:19:15 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
19/05/22 21:19:15 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
19/05/22 21:19:15 INFO util.GSet: Computing capacity for map NameNodeRetryCache
19/05/22 21:19:15 INFO util.GSet: VM type       = 64-bit
19/05/22 21:19:15 INFO util.GSet: 0.029999999329447746% max memory 889 MB = 273.1 KB
19/05/22 21:19:15 INFO util.GSet: capacity      = 2^15 = 32768 entries
19/05/22 21:19:15 INFO namenode.FSImage: Allocated new BlockPoolId: BP-608257527-127.0.0.1-1558531155258
19/05/22 21:19:15 INFO common.Storage: Storage directory /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name has been successfully formatted.
19/05/22 21:19:15 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
19/05/22 21:19:15 INFO util.ExitUtil: Exiting with status 0
19/05/22 21:19:15 INFO namenode.NameNode: SHUTDOWN_MSG:
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at VM_0_17_centos/127.0.0.1
************************************************************/
[zozo@VM_0_17_centos hadoop-2.7.2]$
```

完成后查看 data 目录, 有如下文件产生:
```
[zozo@VM_0_17_centos hadoop]$ ll
总用量 207296
drwxr-xr-x 13 zozo zozo      4096 5月  19 21:14 hadoop-2.7.2
drwxrwxr-x  3 zozo zozo      4096 5月  22 21:19 hadoop-2.7.2-data
-rw-r--r--  1 zozo zozo 212046774 5月  19 16:38 hadoop-2.7.2.tar.gz
[zozo@VM_0_17_centos hadoop]$ ll hadoop-2.7.2-data/
总用量 4
drwxrwxr-x 3 zozo zozo 4096 5月  22 21:19 tmp
[zozo@VM_0_17_centos hadoop]$ ll hadoop-2.7.2-data/tmp/
总用量 4
drwxrwxr-x 3 zozo zozo 4096 5月  22 21:19 dfs
[zozo@VM_0_17_centos hadoop]$ ll hadoop-2.7.2-data/tmp/dfs/
总用量 4
drwxrwxr-x 3 zozo zozo 4096 5月  22 21:19 name
[zozo@VM_0_17_centos hadoop]$ ll hadoop-2.7.2-data/tmp/dfs/name/
总用量 4
drwxrwxr-x 2 zozo zozo 4096 5月  22 21:19 current
[zozo@VM_0_17_centos hadoop]$ ll hadoop-2.7.2-data/tmp/dfs/name/current/
总用量 16
-rw-rw-r-- 1 zozo zozo 351 5月  22 21:19 fsimage_0000000000000000000
-rw-rw-r-- 1 zozo zozo  62 5月  22 21:19 fsimage_0000000000000000000.md5
-rw-rw-r-- 1 zozo zozo   2 5月  22 21:19 seen_txid
-rw-rw-r-- 1 zozo zozo 201 5月  22 21:19 VERSION
[zozo@VM_0_17_centos hadoop]$
```

- 格式化 NameNode (非首次启动)

将 `./etc/hadoop/hdfs-site.xml` 配置的的 `dfs.namenode.name.dir` 和 `dfs.datanode.data.dir` 这两个指定目录删除.

注: 需要将 name 和 data 文件夹都删除, 否则可能出现 namenode 和 datanode 的 clusterID (集群 ID) 不一致的情况.

将 ``./etc/hadoop/core-site.xml`` 配置的 `fs.defaultFS` 这个指定目录删除.

执行命令 `bin/hdfs namenode -format`, 完成后数据被全部清除, 产生一个新的 HDFS.

```
[zozo@vm017 tmp]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp
[zozo@vm017 tmp]$ rm -rf *
[zozo@vm017 tmp]$ cd ../../hadoop-2.7.2
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs namenode -format
19/05/26 18:22:16 INFO namenode.NameNode: STARTUP_MSG: 
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   host = vm017/172.16.0.17
STARTUP_MSG:   args = [-format]
STARTUP_MSG:   version = 2.7.2
STARTUP_MSG:   classpath = /home/zozo/app/hadoop/hadoop-2.7.2/etc/hadoop:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jets3t-0.9.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-1.7.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-configuration-1.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-httpclient-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-kerberos-codec-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-auth-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-framework-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/mockito-all-1.8.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-math3-3.1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/java-xmlbuilder-0.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-api-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/gson-2.2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-asn1-api-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-core-1.8.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-client-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-net-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsp-api-2.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpclient-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-util-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-recipes-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-digester-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpcore-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsch-0.1.42.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-daemon-1.0.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-all-4.0.23.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xml-apis-1.3.04.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xercesImpl-2.9.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-client-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-api-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-registry-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-applicationhistoryservice-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-tests-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-resourcemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-sharedcachemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-nodemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-web-proxy-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-client-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-shuffle-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-app-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-plugins-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/contrib/capacity-scheduler/*.jar
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r b165c4fe8a74265c792ce23f546c64604acf0e41; compiled by 'jenkins' on 2016-01-26T00:08Z
STARTUP_MSG:   java = 1.8.0_192
************************************************************/
19/05/26 18:22:16 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
19/05/26 18:22:16 INFO namenode.NameNode: createNameNode [-format]
Formatting using clusterid: CID-1267ccd5-4b0b-4a85-b0b5-d97decec672a
19/05/26 18:22:19 INFO namenode.FSNamesystem: No KeyProvider found.
19/05/26 18:22:19 INFO namenode.FSNamesystem: fsLock is fair:true
19/05/26 18:22:19 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
19/05/26 18:22:19 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
19/05/26 18:22:19 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
19/05/26 18:22:19 INFO blockmanagement.BlockManager: The block deletion will start around 2019 五月 26 18:22:19
19/05/26 18:22:19 INFO util.GSet: Computing capacity for map BlocksMap
19/05/26 18:22:19 INFO util.GSet: VM type       = 64-bit
19/05/26 18:22:19 INFO util.GSet: 2.0% max memory 889 MB = 17.8 MB
19/05/26 18:22:19 INFO util.GSet: capacity      = 2^21 = 2097152 entries
19/05/26 18:22:19 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
19/05/26 18:22:19 INFO blockmanagement.BlockManager: defaultReplication         = 1
19/05/26 18:22:19 INFO blockmanagement.BlockManager: maxReplication             = 512
19/05/26 18:22:19 INFO blockmanagement.BlockManager: minReplication             = 1
19/05/26 18:22:19 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
19/05/26 18:22:19 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
19/05/26 18:22:19 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
19/05/26 18:22:19 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
19/05/26 18:22:19 INFO namenode.FSNamesystem: fsOwner             = zozo (auth:SIMPLE)
19/05/26 18:22:19 INFO namenode.FSNamesystem: supergroup          = supergroup
19/05/26 18:22:19 INFO namenode.FSNamesystem: isPermissionEnabled = true
19/05/26 18:22:19 INFO namenode.FSNamesystem: HA Enabled: false
19/05/26 18:22:19 INFO namenode.FSNamesystem: Append Enabled: true
19/05/26 18:22:19 INFO util.GSet: Computing capacity for map INodeMap
19/05/26 18:22:19 INFO util.GSet: VM type       = 64-bit
19/05/26 18:22:19 INFO util.GSet: 1.0% max memory 889 MB = 8.9 MB
19/05/26 18:22:19 INFO util.GSet: capacity      = 2^20 = 1048576 entries
19/05/26 18:22:20 INFO namenode.FSDirectory: ACLs enabled? false
19/05/26 18:22:20 INFO namenode.FSDirectory: XAttrs enabled? true
19/05/26 18:22:20 INFO namenode.FSDirectory: Maximum size of an xattr: 16384
19/05/26 18:22:20 INFO namenode.NameNode: Caching file names occuring more than 10 times
19/05/26 18:22:20 INFO util.GSet: Computing capacity for map cachedBlocks
19/05/26 18:22:20 INFO util.GSet: VM type       = 64-bit
19/05/26 18:22:20 INFO util.GSet: 0.25% max memory 889 MB = 2.2 MB
19/05/26 18:22:20 INFO util.GSet: capacity      = 2^18 = 262144 entries
19/05/26 18:22:20 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
19/05/26 18:22:20 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
19/05/26 18:22:20 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
19/05/26 18:22:20 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.window.num.buckets = 10
19/05/26 18:22:20 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.num.users = 10
19/05/26 18:22:20 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.windows.minutes = 1,5,25
19/05/26 18:22:20 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
19/05/26 18:22:20 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
19/05/26 18:22:20 INFO util.GSet: Computing capacity for map NameNodeRetryCache
19/05/26 18:22:20 INFO util.GSet: VM type       = 64-bit
19/05/26 18:22:20 INFO util.GSet: 0.029999999329447746% max memory 889 MB = 273.1 KB
19/05/26 18:22:20 INFO util.GSet: capacity      = 2^15 = 32768 entries
19/05/26 18:22:20 INFO namenode.FSImage: Allocated new BlockPoolId: BP-1771539892-172.16.0.17-1558866140038
19/05/26 18:22:20 INFO common.Storage: Storage directory /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name has been successfully formatted.
19/05/26 18:22:20 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
19/05/26 18:22:20 INFO util.ExitUtil: Exiting with status 0
19/05/26 18:22:20 INFO namenode.NameNode: SHUTDOWN_MSG: 
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at vm017/172.16.0.17
************************************************************/
[zozo@vm017 hadoop-2.7.2]$ 
```

完成后查看 data 目录, 有如下文件产生, 正常情况下, name 和 data 文件夹下的 clusterID 会一致:
```
[zozo@vm017 current]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current
[zozo@vm017 current]$ ll
总用量 1044
-rw-rw-r-- 1 zozo zozo 1048576 5月  26 19:52 edits_inprogress_0000000000000000001
-rw-rw-r-- 1 zozo zozo     351 5月  26 18:22 fsimage_0000000000000000000
-rw-rw-r-- 1 zozo zozo      62 5月  26 18:22 fsimage_0000000000000000000.md5
-rw-rw-r-- 1 zozo zozo       2 5月  26 18:26 seen_txid
-rw-rw-r-- 1 zozo zozo     203 5月  26 18:22 VERSION
[zozo@vm017 current]$ cat VERSION 
#Sun May 26 18:22:20 CST 2019
namespaceID=530423119
clusterID=CID-1267ccd5-4b0b-4a85-b0b5-d97decec672a
cTime=0
storageType=NAME_NODE
blockpoolID=BP-1771539892-172.16.0.17-1558866140038
layoutVersion=-63
[zozo@vm017 current]$ 

[zozo@vm017 current]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data/current
[zozo@vm017 current]$ ll
总用量 8
drwx------ 4 zozo zozo 4096 5月  26 18:27 BP-1771539892-172.16.0.17-1558866140038
-rw-rw-r-- 1 zozo zozo  229 5月  26 18:27 VERSION
[zozo@vm017 current]$ cat VERSION 
#Sun May 26 18:27:14 CST 2019
storageID=DS-e526874a-f794-4ad3-8262-cadcdc4d47df
clusterID=CID-1267ccd5-4b0b-4a85-b0b5-d97decec672a
cTime=0
datanodeUuid=4dd7eacb-8177-41ee-8986-5b5092b00e69
storageType=DATA_NODE
layoutVersion=-56
[zozo@vm017 current]$ 
```

- 启动 NameNode (守护进程)

```
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start namenode
starting namenode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-namenode-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
22034 org.apache.hadoop.hdfs.server.namenode.NameNode
[zozo@vm017 hadoop-2.7.2]$ 
```

- 启动 DataNode (守护进程)

```
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start datanode
starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
22034 org.apache.hadoop.hdfs.server.namenode.NameNode
22282 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ 
```

### 2.1.3 HDFS 命令

- HDFS 创建文件夹
```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -mkdir -p /user/zozo/input-wordcount
```

- HDFS 查看目录
```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -ls /
Found 1 items
drwxr-xr-x   - zozo supergroup          0 2019-05-26 18:29 /user
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -ls -R /
drwxr-xr-x   - zozo supergroup          0 2019-05-26 18:29 /user
drwxr-xr-x   - zozo supergroup          0 2019-05-26 18:29 /user/zozo
drwxr-xr-x   - zozo supergroup          0 2019-05-26 18:29 /user/zozo/input-wordcount
```

- 本地文件上传到 HDFS
```
[zozo@vm017 hadoop-2.7.2]$ ll input-wordcount/
总用量 4
-rw-rw-r-- 1 zozo zozo 50 5月  19 21:12 wordcount.input
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -put input-wordcount/wordcount.input /user/zozo/input-wordcount
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -ls -R /
drwxr-xr-x   - zozo supergroup          0 2019-05-26 18:29 /user
drwxr-xr-x   - zozo supergroup          0 2019-05-26 18:29 /user/zozo
drwxr-xr-x   - zozo supergroup          0 2019-05-26 18:30 /user/zozo/input-wordcount
-rw-r--r--   1 zozo supergroup         50 2019-05-26 18:30 /user/zozo/input-wordcount/wordcount.input
```

- HDFS 查看文件信息
```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -cat /user/zozo/input-wordcount/wordcount.input
hadoop yarn
hadoop mapreduce
yarn
zozo zozo

good
```

### 2.1.4 浏览器查看 HDFS

HDFS 控制台 URL: http://193.112.38.200:50070

以下为导航栏:
- `Overview`: 总体介绍
- `Datanodes`: 数据节点
- `Datanode Volume Failures`: 数据节点故障
- `Snapshot`: 快照
- `Startup Progress`: 启动处理
- `Utilities`: 工具
  - `Browse the file system`: 浏览文件系统
	- `Logs`: 日志

以下为通过 `Utilities` - `Browse the file system` 查看 HDFS 的文件信息

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BHDFS-1.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BHDFS-2.png?raw=true)

### 2.1.5 WordCount 案例

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -ls /user/zozo/input-wordcount
Found 1 items
-rw-r--r--   1 zozo supergroup         50 2019-05-26 18:30 /user/zozo/input-wordcount/wordcount.input
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar wordcount /user/zozo/input-wordcount /user/zozo/output-wordcount
19/05/26 19:52:47 INFO Configuration.deprecation: session.id is deprecated. Instead, use dfs.metrics.session-id
19/05/26 19:52:47 INFO jvm.JvmMetrics: Initializing JVM Metrics with processName=JobTracker, sessionId=
19/05/26 19:52:48 INFO input.FileInputFormat: Total input paths to process : 1
19/05/26 19:52:48 INFO mapreduce.JobSubmitter: number of splits:1
19/05/26 19:52:48 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_local1405270075_0001
19/05/26 19:52:48 INFO mapreduce.Job: The url to track the job: http://localhost:8080/
19/05/26 19:52:48 INFO mapreduce.Job: Running job: job_local1405270075_0001
19/05/26 19:52:48 INFO mapred.LocalJobRunner: OutputCommitter set in config null
19/05/26 19:52:48 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/26 19:52:48 INFO mapred.LocalJobRunner: OutputCommitter is org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter
19/05/26 19:52:48 INFO mapred.LocalJobRunner: Waiting for map tasks
19/05/26 19:52:48 INFO mapred.LocalJobRunner: Starting task: attempt_local1405270075_0001_m_000000_0
19/05/26 19:52:48 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/26 19:52:48 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/26 19:52:48 INFO mapred.MapTask: Processing split: hdfs://vm017:9000/user/zozo/input-wordcount/wordcount.input:0+50
19/05/26 19:52:49 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/26 19:52:49 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/26 19:52:49 INFO mapred.MapTask: soft limit at 83886080
19/05/26 19:52:49 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/26 19:52:49 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/26 19:52:49 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/26 19:52:49 INFO mapred.LocalJobRunner: 
19/05/26 19:52:49 INFO mapred.MapTask: Starting flush of map output
19/05/26 19:52:49 INFO mapred.MapTask: Spilling map output
19/05/26 19:52:49 INFO mapred.MapTask: bufstart = 0; bufend = 81; bufvoid = 104857600
19/05/26 19:52:49 INFO mapred.MapTask: kvstart = 26214396(104857584); kvend = 26214368(104857472); length = 29/6553600
19/05/26 19:52:49 INFO mapred.MapTask: Finished spill 0
19/05/26 19:52:49 INFO mapred.Task: Task:attempt_local1405270075_0001_m_000000_0 is done. And is in the process of committing
19/05/26 19:52:49 INFO mapred.LocalJobRunner: map
19/05/26 19:52:49 INFO mapred.Task: Task 'attempt_local1405270075_0001_m_000000_0' done.
19/05/26 19:52:49 INFO mapred.LocalJobRunner: Finishing task: attempt_local1405270075_0001_m_000000_0
19/05/26 19:52:49 INFO mapred.LocalJobRunner: map task executor complete.
19/05/26 19:52:49 INFO mapred.LocalJobRunner: Waiting for reduce tasks
19/05/26 19:52:49 INFO mapred.LocalJobRunner: Starting task: attempt_local1405270075_0001_r_000000_0
19/05/26 19:52:49 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/26 19:52:49 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/26 19:52:49 INFO mapred.ReduceTask: Using ShuffleConsumerPlugin: org.apache.hadoop.mapreduce.task.reduce.Shuffle@31ef8b56
19/05/26 19:52:49 INFO reduce.MergeManagerImpl: MergerManager: memoryLimit=334338464, maxSingleShuffleLimit=83584616, mergeThreshold=220663392, ioSortFactor=10, memToMemMergeOutputsThreshold=10
19/05/26 19:52:49 INFO reduce.EventFetcher: attempt_local1405270075_0001_r_000000_0 Thread started: EventFetcher for fetching Map Completion Events
19/05/26 19:52:49 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local1405270075_0001_m_000000_0 decomp: 64 len: 68 to MEMORY
19/05/26 19:52:49 INFO reduce.InMemoryMapOutput: Read 64 bytes from map-output for attempt_local1405270075_0001_m_000000_0
19/05/26 19:52:49 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 64, inMemoryMapOutputs.size() -> 1, commitMemory -> 0, usedMemory ->64
19/05/26 19:52:49 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/26 19:52:49 INFO reduce.EventFetcher: EventFetcher is interrupted.. Returning
19/05/26 19:52:49 INFO mapred.LocalJobRunner: 1 / 1 copied.
19/05/26 19:52:49 INFO reduce.MergeManagerImpl: finalMerge called with 1 in-memory map-outputs and 0 on-disk map-outputs
19/05/26 19:52:49 INFO mapred.Merger: Merging 1 sorted segments
19/05/26 19:52:49 INFO mapred.Merger: Down to the last merge-pass, with 1 segments left of total size: 57 bytes
19/05/26 19:52:49 INFO reduce.MergeManagerImpl: Merged 1 segments, 64 bytes to disk to satisfy reduce memory limit
19/05/26 19:52:49 INFO reduce.MergeManagerImpl: Merging 1 files, 68 bytes from disk
19/05/26 19:52:49 INFO reduce.MergeManagerImpl: Merging 0 segments, 0 bytes from memory into reduce
19/05/26 19:52:49 INFO mapred.Merger: Merging 1 sorted segments
19/05/26 19:52:49 INFO mapred.Merger: Down to the last merge-pass, with 1 segments left of total size: 57 bytes
19/05/26 19:52:49 INFO mapred.LocalJobRunner: 1 / 1 copied.
19/05/26 19:52:49 INFO Configuration.deprecation: mapred.skip.on is deprecated. Instead, use mapreduce.job.skiprecords
19/05/26 19:52:49 INFO mapred.Task: Task:attempt_local1405270075_0001_r_000000_0 is done. And is in the process of committing
19/05/26 19:52:49 INFO mapred.LocalJobRunner: 1 / 1 copied.
19/05/26 19:52:49 INFO mapred.Task: Task attempt_local1405270075_0001_r_000000_0 is allowed to commit now
19/05/26 19:52:49 INFO output.FileOutputCommitter: Saved output of task 'attempt_local1405270075_0001_r_000000_0' to hdfs://vm017:9000/user/zozo/output-wordcount/_temporary/0/task_local1405270075_0001_r_000000
19/05/26 19:52:49 INFO mapred.LocalJobRunner: reduce > reduce
19/05/26 19:52:49 INFO mapred.Task: Task 'attempt_local1405270075_0001_r_000000_0' done.
19/05/26 19:52:49 INFO mapred.LocalJobRunner: Finishing task: attempt_local1405270075_0001_r_000000_0
19/05/26 19:52:49 INFO mapred.LocalJobRunner: reduce task executor complete.
19/05/26 19:52:49 INFO mapreduce.Job: Job job_local1405270075_0001 running in uber mode : false
19/05/26 19:52:49 INFO mapreduce.Job:  map 100% reduce 100%
19/05/26 19:52:49 INFO mapreduce.Job: Job job_local1405270075_0001 completed successfully
19/05/26 19:52:49 INFO mapreduce.Job: Counters: 35
	File System Counters
		FILE: Number of bytes read=547388
		FILE: Number of bytes written=1149896
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=100
		HDFS: Number of bytes written=42
		HDFS: Number of read operations=13
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=4
	Map-Reduce Framework
		Map input records=6
		Map output records=8
		Map output bytes=81
		Map output materialized bytes=68
		Input split bytes=124
		Combine input records=8
		Combine output records=5
		Reduce input groups=5
		Reduce shuffle bytes=68
		Reduce input records=5
		Reduce output records=5
		Spilled Records=10
		Shuffled Maps =1
		Failed Shuffles=0
		Merged Map outputs=1
		GC time elapsed (ms)=0
		Total committed heap usage (bytes)=545259520
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters 
		Bytes Read=50
	File Output Format Counters 
		Bytes Written=42
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -ls /user/zozo/output-wordcount
Found 2 items
-rw-r--r--   1 zozo supergroup          0 2019-05-26 19:52 /user/zozo/output-wordcount/_SUCCESS
-rw-r--r--   1 zozo supergroup         42 2019-05-26 19:52 /user/zozo/output-wordcount/part-r-00000
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -cat /user/zozo/output-wordcount/part-r-00000
good	1
hadoop	2
mapreduce	1
yarn	2
zozo	2
[zozo@vm017 hadoop-2.7.2]$ 
```

## 2.2 启动 YARN 并运行 MapReduce 程序

### 2.2.1 step1 修改配置

- 修改配置 `./etc/hadoop/yarn-env.sh`

```bash
# some Java parameters
# export JAVA_HOME=/home/y/libexec/jdk1.6.0/

# custom
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
```

- 修改配置 `./etc/hadoop/yarn-site.xml`

```xml
<?xml version="1.0"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->
<configuration>

  <!-- Reducer 获取数据的方式 -->
  <property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
    <!-- value: -->
    <description>
      A comma separated list of services where service name should only contain a-zA-Z0-9_ and can not start with numbers
    </description>
  </property>

  <!-- 指定 YARN 的 ResourceManager 对应节点的 hostname -->
  <property>
    <name>yarn.resourcemanager.hostname</name>
    <value>vm017</value>
		<!-- value: 0.0.0.0 -->
    <description>
      The hostname of the RM.
    </description>
  </property>

<configuration>
```

- 修改配置 `./etc/hadoop/mapred-env.sh`

```bash
# export JAVA_HOME=/home/y/libexec/jdk1.6.0/

# custom
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
```

- 修改配置 `./etc/hadoop/mapred-site.xml`

```xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration>

  <!-- 指定 MapReduce 运行在 YARN 上 -->
  <property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
    <!-- value: local -->
    <description>
      The runtime framework for executing MapReduce jobs. Can be one of local, classic or yarn.
    </description>
  </property>

</configuration>
```

### 2.2.2 step2 启动 YARN (ResourceManager, NodeManager)

- 启动 ResourceManager (守护进程)

```
[zozo@vm017 hadoop-2.7.2]$ sbin/yarn-daemon.sh start resourcemanager
starting resourcemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-resourcemanager-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
32245 org.apache.hadoop.yarn.server.resourcemanager.ResourceManager
32474 sun.tools.jps.Jps -m -l
8975 org.apache.hadoop.hdfs.server.namenode.NameNode
9119 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ 
```

- 启动 NodeManager (守护进程)

```
[zozo@vm017 hadoop-2.7.2]$ sbin/yarn-daemon.sh start nodemanager
starting nodemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-nodemanager-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
32245 org.apache.hadoop.yarn.server.resourcemanager.ResourceManager
32521 org.apache.hadoop.yarn.server.nodemanager.NodeManager
32652 sun.tools.jps.Jps -m -l
8975 org.apache.hadoop.hdfs.server.namenode.NameNode
9119 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ 
```

### 2.2.3 浏览器查看 YARN

YARN 控制台 URL: http://193.112.38.200:8088

以下为初始化首页

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BYARN-1.png?raw=true)

### 2.2.4 WordCount 案例

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -ls /user/zozo/input-wordcount
Found 1 items
-rw-r--r--   1 zozo supergroup         50 2019-05-26 18:30 /user/zozo/input-wordcount/wordcount.input
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar wordcount /user/zozo/input-wordcount /user/zozo/output-wordcount-2
19/05/26 23:04:00 INFO client.RMProxy: Connecting to ResourceManager at vm017/172.16.0.17:8032
19/05/26 23:04:01 INFO input.FileInputFormat: Total input paths to process : 1
19/05/26 23:04:02 INFO mapreduce.JobSubmitter: number of splits:1
19/05/26 23:04:02 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1558881403273_0001
19/05/26 23:04:03 INFO impl.YarnClientImpl: Submitted application application_1558881403273_0001
19/05/26 23:04:03 INFO mapreduce.Job: The url to track the job: http://vm017:8088/proxy/application_1558881403273_0001/
19/05/26 23:04:03 INFO mapreduce.Job: Running job: job_1558881403273_0001
19/05/26 23:04:14 INFO mapreduce.Job: Job job_1558881403273_0001 running in uber mode : false
19/05/26 23:04:14 INFO mapreduce.Job:  map 0% reduce 0%
19/05/26 23:04:20 INFO mapreduce.Job:  map 100% reduce 0%
19/05/26 23:04:25 INFO mapreduce.Job:  map 100% reduce 100%
19/05/26 23:04:26 INFO mapreduce.Job: Job job_1558881403273_0001 completed successfully
19/05/26 23:04:26 INFO mapreduce.Job: Counters: 49
	File System Counters
		FILE: Number of bytes read=68
		FILE: Number of bytes written=235105
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=174
		HDFS: Number of bytes written=42
		HDFS: Number of read operations=6
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=2
	Job Counters 
		Launched map tasks=1
		Launched reduce tasks=1
		Data-local map tasks=1
		Total time spent by all maps in occupied slots (ms)=3349
		Total time spent by all reduces in occupied slots (ms)=2936
		Total time spent by all map tasks (ms)=3349
		Total time spent by all reduce tasks (ms)=2936
		Total vcore-milliseconds taken by all map tasks=3349
		Total vcore-milliseconds taken by all reduce tasks=2936
		Total megabyte-milliseconds taken by all map tasks=3429376
		Total megabyte-milliseconds taken by all reduce tasks=3006464
	Map-Reduce Framework
		Map input records=6
		Map output records=8
		Map output bytes=81
		Map output materialized bytes=68
		Input split bytes=124
		Combine input records=8
		Combine output records=5
		Reduce input groups=5
		Reduce shuffle bytes=68
		Reduce input records=5
		Reduce output records=5
		Spilled Records=10
		Shuffled Maps =1
		Failed Shuffles=0
		Merged Map outputs=1
		GC time elapsed (ms)=338
		CPU time spent (ms)=1500
		Physical memory (bytes) snapshot=427196416
		Virtual memory (bytes) snapshot=4209008640
		Total committed heap usage (bytes)=302514176
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters 
		Bytes Read=50
	File Output Format Counters 
		Bytes Written=42
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -ls /user/zozo/output-wordcount-2
Found 2 items
-rw-r--r--   1 zozo supergroup          0 2019-05-26 23:04 /user/zozo/output-wordcount-2/_SUCCESS
-rw-r--r--   1 zozo supergroup         42 2019-05-26 23:04 /user/zozo/output-wordcount-2/part-r-00000
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -cat /user/zozo/output-wordcount-2/part-r-00000
good	1
hadoop	2
mapreduce	1
yarn	2
zozo	2
[zozo@vm017 hadoop-2.7.2]$ 
```

在此过程中通过浏览器查看 YARN 的相关信息, 可监控到作业运行中的如下变化:

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BYARN-wordcount1.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BYARN-wordcount2.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BYARN-wordcount3.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BYARN-wordcount4.png?raw=true)

### 2.2.5 配置历史服务器

- 修改配置 `./etc/hadoop/mapred-site.xml`

```xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration>

  <!-- 历史服务器端地址 -->
  <property>
    <name>mapreduce.jobhistory.address</name>
    <value>vm017:10020</value>
    <!-- value: 0.0.0.0:10020 -->
    <description>
      MapReduce JobHistory Server IPC host:port
    </description>
  </property>

  <!-- 历史服务器端 web 端地址 -->
  <property>
    <name>mapreduce.jobhistory.webapp.address</name>
    <value>vm017:19888</value>
    <!-- value: 0.0.0.0:19888 -->
    <description>
      MapReduce JobHistory Server Web UI host:port
    </description>
  </property>

</configuration>
```

- 启动历史服务器

```
[zozo@vm017 hadoop-2.7.2]$ sbin/mr-jobhistory-daemon.sh start historyserver
starting historyserver, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/mapred-zozo-historyserver-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
32245 org.apache.hadoop.yarn.server.resourcemanager.ResourceManager
16040 sun.tools.jps.Jps -m -l
15976 org.apache.hadoop.mapreduce.v2.hs.JobHistoryServer
32521 org.apache.hadoop.yarn.server.nodemanager.NodeManager
8975 org.apache.hadoop.hdfs.server.namenode.NameNode
9119 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ 
```

- 浏览器查看

进入 [YARN 首页](http://193.112.38.200:8088), 通过点击对应任务的 `History` 按钮进入

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E5%8E%86%E5%8F%B2%E6%9C%8D%E5%8A%A1%E5%99%A81.png?raw=true)

`Overview`: 预览

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E5%8E%86%E5%8F%B2%E6%9C%8D%E5%8A%A1%E5%99%A82.png?raw=true)

`Counters`: 计数器

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E5%8E%86%E5%8F%B2%E6%9C%8D%E5%8A%A1%E5%99%A83.png?raw=true)

`Configuration`: 配置信息

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E5%8E%86%E5%8F%B2%E6%9C%8D%E5%8A%A1%E5%99%A84.png?raw=true)

`Map tasks`: Map 任务

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E5%8E%86%E5%8F%B2%E6%9C%8D%E5%8A%A1%E5%99%A85.png?raw=true)

`Reduce tasks`: Reduce 任务

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E5%8E%86%E5%8F%B2%E6%9C%8D%E5%8A%A1%E5%99%A86.png?raw=true)

### 2.2.6 配置日志聚集

- 停止历史服务器

```
[zozo@vm017 hadoop-2.7.2]$ sbin/mr-jobhistory-daemon.sh stop historyserver
stopping historyserver
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
19873 sun.tools.jps.Jps -m -l
32245 org.apache.hadoop.yarn.server.resourcemanager.ResourceManager
32521 org.apache.hadoop.yarn.server.nodemanager.NodeManager
8975 org.apache.hadoop.hdfs.server.namenode.NameNode
9119 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ 
```

- 停止 YARN NodeManager

```
[zozo@vm017 hadoop-2.7.2]$ sbin/yarn-daemon.sh stop nodemanager
stopping nodemanager
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
32245 org.apache.hadoop.yarn.server.resourcemanager.ResourceManager
20014 sun.tools.jps.Jps -m -l
8975 org.apache.hadoop.hdfs.server.namenode.NameNode
9119 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ 
```

- 停止 YARN ResourceManager

```
[zozo@vm017 hadoop-2.7.2]$ sbin/yarn-daemon.sh stop resourcemanager
stopping resourcemanager
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
8975 org.apache.hadoop.hdfs.server.namenode.NameNode
9119 org.apache.hadoop.hdfs.server.datanode.DataNode
20143 sun.tools.jps.Jps -m -l
[zozo@vm017 hadoop-2.7.2]$
```

- 修改配置 `./etc/hadoop/yarn-site.xml`

```xml
<?xml version="1.0"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->
<configuration>

  <!-- 开启日志聚集功能 -->
  <property>
    <name>yarn.log-aggregation-enable</name>
    <value>true</value>
    <!-- value: false -->
    <description>
      Whether to enable log aggregation. Log aggregation collects each container's logs and moves these logs onto a file-system, for e.g. HDFS, after the application completes. Users can configure the "yarn.nodemanager.remote-app-log-dir" and "yarn.nodemanager.remote-app-log-dir-suffix" properties to determine where these logs are moved to. Users can access the logs via the Application Timeline Server.
    </description>
  </property>

  <!-- 日志保留时间设置 7 天 -->
  <property>
    <name>yarn.log-aggregation.retain-seconds</name>
    <value>604800</value>
    <!-- value: -1 -->
    <description>
      How long to keep aggregation logs before deleting them. -1 disables. Be careful set this too small and you will spam the name node.
    </description>
  </property>

<configuration>
```

- 启动 YARN ResourceManager

```
[zozo@vm017 hadoop-2.7.2]$ sbin/yarn-daemon.sh start resourcemanager
```

- 启动 YARN NodeManager

```
[zozo@vm017 hadoop-2.7.2]$ sbin/yarn-daemon.sh start nodemanager
```

- 启动历史服务器

```
[zozo@vm017 hadoop-2.7.2]$ sbin/mr-jobhistory-daemon.sh start historyserver
```

- 重跑一个 WordCount 案例

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar wordcount /user/zozo/input-wordcount /user/zozo/output-wordcount-21
```

- 浏览器查看

进入 [YARN 首页](http://193.112.38.200:8088), 通过点击对应任务的 `History` 按钮进入 `Overview`, 然后点击 `Logs` 按钮

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E6%97%A5%E5%BF%971.png?raw=true)

`Logs` 首页, 可再次点击 `here` 进入详情

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E6%97%A5%E5%BF%972.png?raw=true)

`here`

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E6%97%A5%E5%BF%973.png?raw=true)

---

# 三. 完全分布式运行模式

集群部署规划如下:

| 模块 / 节点 | __vm017__ | __vm06__ | __vm03__ |
| :--- | :--- | :--- | :--- |
| __HDFS__ | DataNode | DataNode | DataNode |
|  | __NameNode__ | __SecondaryNameNode__ |  |
| __YARN__ | NodeManager | NodeManager | NodeManager |
|  |  |  | __ResourceManager__ |

| 节点 / 模块 | __HDFS__ | __YARN__ |
| :--- | :--- | :--- |
| __vm017__ | DataNode | NodeManager |
|  | __NameNode__ |  |
| __vm06__ | DataNode | NodeManager |
|  | __SecondaryNameNode__ |  |
| __vm03__ | DataNode | NodeManager |
|  |  | __ResourceManager__ |

集群操作步骤如下:
- 准备 3 台机器
- 安装 JDK, Hadoop, 配置环境变量
- 配置集群
- 集群每个节点单独启动
- 配置 SSH 免密登录
- 群起
- 测试

## 3.1 准备 3 台机器

配置 hostname 和 host

- __vm06__
```
[root@VM_0_6_centos ~]# cat /etc/hostname
vm06
[root@VM_0_6_centos ~]# cat /etc/cloud/templates/hosts.redhat.tmpl
## template:jinja
{#
This file /etc/cloud/templates/hosts.redhat.tmpl is only utilized
if enabled in cloud-config.  Specifically, in order to enable it
you need to add the following to config:
  manage_etc_hosts: True
-#}
# Your system has configured 'manage_etc_hosts' as True.
# As a result, if you wish for changes to this file to persist
# then you will need to either
# a.) make changes to the master file in /etc/cloud/templates/hosts.redhat.tmpl
# b.) change or remove the value of 'manage_etc_hosts' in
#     /etc/cloud/cloud.cfg or cloud-config from user-data
#
# The following lines are desirable for IPv4 capable hosts
127.0.0.1 {{fqdn}} {{hostname}}
127.0.0.1 localhost.localdomain localhost
127.0.0.1 localhost4.localdomain4 localhost4

172.16.0.6 vm06 vm06
172.16.0.17 vm017 vm017
172.16.0.3 vm03 vm03

# The following lines are desirable for IPv6 capable hosts
::1 {{fqdn}} {{hostname}}
::1 localhost.localdomain localhost
::1 localhost6.localdomain6 localhost6

[root@VM_0_6_centos ~]# reboot


[root@vm06 ~]# cat /etc/hosts
# Your system has configured 'manage_etc_hosts' as True.
# As a result, if you wish for changes to this file to persist
# then you will need to either
# a.) make changes to the master file in /etc/cloud/templates/hosts.redhat.tmpl
# b.) change or remove the value of 'manage_etc_hosts' in
#     /etc/cloud/cloud.cfg or cloud-config from user-data
#
# The following lines are desirable for IPv4 capable hosts
127.0.0.1 VM_0_6_centos VM_0_6_centos
127.0.0.1 localhost.localdomain localhost
127.0.0.1 localhost4.localdomain4 localhost4

172.16.0.6 vm06 vm06
172.16.0.17 vm017 vm017
172.16.0.3 vm03 vm03

# The following lines are desirable for IPv6 capable hosts
::1 VM_0_6_centos VM_0_6_centos
::1 localhost.localdomain localhost
::1 localhost6.localdomain6 localhost6

[root@vm06 ~]#
```

- __vm017__
```
[root@VM_0_17_centos ~]# cat /etc/hostname
vm017
[root@VM_0_17_centos ~]# cat /etc/cloud/templates/hosts.redhat.tmpl
## template:jinja
{#
This file /etc/cloud/templates/hosts.redhat.tmpl is only utilized
if enabled in cloud-config.  Specifically, in order to enable it
you need to add the following to config:
  manage_etc_hosts: True
-#}
127.0.0.1 {{fqdn}} {{hostname}}
127.0.0.1 localhost.localdomain localhost
127.0.0.1 localhost4.localdomain4 localhost4

172.16.0.6 vm06 vm06
172.16.0.17 vm017 vm017
172.16.0.3 vm03 vm03

::1 {{fqdn}} {{hostname}}
::1 localhost.localdomain localhost
::1 localhost6.localdomain6 localhost6

[root@VM_0_6_centos ~]# reboot


[root@vm017 ~]# cat /etc/hosts
127.0.0.1 VM_0_17_centos VM_0_17_centos
127.0.0.1 localhost.localdomain localhost
127.0.0.1 localhost4.localdomain4 localhost4

::1 VM_0_17_centos VM_0_17_centos
::1 localhost.localdomain localhost
::1 localhost6.localdomain6 localhost6

[root@vm017 ~]#
```

- __vm03__
```
[root@VM_0_3_centos ~]# cat /etc/hostname
vm03
[root@VM_0_3_centos ~]# cat /etc/cloud/templates/hosts.redhat.tmpl
## template:jinja
{#
This file /etc/cloud/templates/hosts.redhat.tmpl is only utilized
if enabled in cloud-config.  Specifically, in order to enable it
you need to add the following to config:
  manage_etc_hosts: True
-#}
# Your system has configured 'manage_etc_hosts' as True.
# As a result, if you wish for changes to this file to persist
# then you will need to either
# a.) make changes to the master file in /etc/cloud/templates/hosts.redhat.tmpl
# b.) change or remove the value of 'manage_etc_hosts' in
#     /etc/cloud/cloud.cfg or cloud-config from user-data
#
# The following lines are desirable for IPv4 capable hosts
127.0.0.1 {{fqdn}} {{hostname}}
127.0.0.1 localhost.localdomain localhost
127.0.0.1 localhost4.localdomain4 localhost4

172.16.0.6 vm06 vm06
172.16.0.17 vm017 vm017
172.16.0.3 vm03 vm03

# The following lines are desirable for IPv6 capable hosts
::1 {{fqdn}} {{hostname}}
::1 localhost.localdomain localhost
::1 localhost6.localdomain6 localhost6

[root@VM_0_6_centos ~]# reboot


[root@vm03 ~]# cat /etc/hosts
# Your system has configured 'manage_etc_hosts' as True.
# As a result, if you wish for changes to this file to persist
# then you will need to either
# a.) make changes to the master file in /etc/cloud/templates/hosts.redhat.tmpl
# b.) change or remove the value of 'manage_etc_hosts' in
#     /etc/cloud/cloud.cfg or cloud-config from user-data
#
# The following lines are desirable for IPv4 capable hosts
127.0.0.1 VM_0_3_centos VM_0_3_centos
127.0.0.1 localhost.localdomain localhost
127.0.0.1 localhost4.localdomain4 localhost4

# The following lines are desirable for IPv6 capable hosts
::1 VM_0_3_centos VM_0_3_centos
::1 localhost.localdomain localhost
::1 localhost6.localdomain6 localhost6

[root@vm03 ~]#
```

## 3.2 安装 JDK, Hadoop, 配置环境变量

以下为 `~/.bash_profile` 内容:
```
# set hadoop
export HADOOP_HOME=/home/zozo/app/hadoop/hadoop-2.7.2
export PATH=$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$PATH

# set java
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
```

## 3.3 配置集群

集群配置如下 (3 台机的配置一样):

### 3.3.1 修改 Hadoop 配置

- 1. 修改配置 `./etc/hadoop/hadoop-env.sh`

```bash
# The only required environment variable is JAVA_HOME.  All others are optional.  When running a distributed configuration it is best to set JAVA_HOME in this file, so that it is correctly defined on remote nodes.
# 唯一需要的环境变量是JAVA_HOME. 所有其他都是可选的. 运行分布式配置时, 最好在此文件中设置 JAVA_HOME, 以便在远程节点上正确定义它.
# The java implementation to use.
# export JAVA_HOME=${JAVA_HOME}

# custom
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
```

- 2. 修改配置 `./etc/hadoop/core-site.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration>

  <!-- 指定 HDFS 中 NameNode 的地址 -->
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://vm017:9000</value>
    <!-- value: file:/// -->
    <description>
      The name of the default file system. A URI whose scheme and authority determine the FileSystem implementation. The uri's scheme determines the config property (fs.SCHEME.impl) naming the FileSystem implementation class. The uri's authority is used to determine the host, port, etc. for a filesystem.
    </description>
  </property>

  <!-- 指定 Hadoop 运行时产生文件的存储目录 -->
  <property>
    <name>hadoop.tmp.dir</name>
    <value>/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp</value>
    <!-- value: /tmp/hadoop-${user.name} -->
    <description>
      A base for other temporary directories.
    </description>
  </property>

<configuration>
```

### 3.3.2 修改 HDFS 配置

- 1. 修改配置 `./etc/hadoop/hdfs-site.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration>

  <!-- 指定 Hadoop SecondaryNameNode 的地址 -->
  <property>
    <name>dfs.namenode.secondary.http-address</name>
    <value>vm06:50090</value>
    <!-- value: 0.0.0.0:50090 -->
    <description>
      The secondary namenode http server address and port.
    </description>
  </property>

  <!-- 指定 HDFS 副本数 -->
  <!--
  <property>
    <name>dfs.replication</name>
    <value>3</value>
    <description>
      Default block replication. The actual number of replications can be specified when the file is created. The default is used if replication is not specified in create time.
    </description>
  </property>
  -->

  <!-- namenode 元数据存储目录 -->
  <!--
  <property>
    <name>dfs.namenode.name.dir</name>
    <value>file://${hadoop.tmp.dir}/dfs/name</value>
    <description>
      Determines where on the local filesystem the DFS name node should store the name table(fsimage). If this is a comma-delimited list of directories then the name table is replicated in all of the directories, for redundancy.
    </description>
  </property>
  -->

  <!-- datanode 上数据块的物理存储位置 -->
  <!--
  <property>
    <name>dfs.datanode.data.dir</name>
    <value>file://${hadoop.tmp.dir}/dfs/data</value>
    <description>
      Determines where on the local filesystem an DFS data node should store its blocks. If this is a comma-delimited list of directories, then data will be stored in all named directories, typically on different devices. The directories should be tagged with corresponding storage types ([SSD]/[DISK]/[ARCHIVE]/[RAM_DISK]) for HDFS storage policies. The default storage type will be DISK if the directory does not have a storage type tagged explicitly. Directories that do not exist will be created if local filesystem permission allows.
    </description>
  </property>
  -->

</configuration>
```

### 3.3.3 修改 YARN 配置

- 1. 修改配置 `./etc/hadoop/yarn-env.sh`

```bash
# some Java parameters
# export JAVA_HOME=/home/y/libexec/jdk1.6.0/

# custom
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
```

- 2. 修改配置 `./etc/hadoop/yarn-site.xml`

```xml
<?xml version="1.0"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->
<configuration>

  <!-- Reducer 获取数据的方式 -->
  <property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
    <!-- value: -->
    <description>
      A comma separated list of services where service name should only contain a-zA-Z0-9_ and can not start with numbers
    </description>
  </property>

  <!-- 指定 YARN 的 ResourceManager 对应节点的 hostname -->
  <property>
    <name>yarn.resourcemanager.hostname</name>
    <value>vm03</value>
    <!-- value: 0.0.0.0 -->
    <description>
      The hostname of the RM.
    </description>
  </property>

<configuration>
```

### 3.3.4 修改 MapReduce 配置

- 1. 修改配置 `./etc/hadoop/mapred-env.sh`

```bash
# export JAVA_HOME=/home/y/libexec/jdk1.6.0/

# custom
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
```

- 2. 修改配置 `./etc/hadoop/mapred-site.xml`

```xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration>

  <!-- 指定 MapReduce 运行在 YARN 上 -->
  <property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
    <!-- value: local -->
    <description>
      The runtime framework for executing MapReduce jobs. Can be one of local, classic or yarn.
    </description>
  </property>

</configuration>
```

## 3.4 集群每个节点单独启动

### 3.4.1 vm017: 格式化, 启动 NameNode, DataNode

- 1. 在 __vm017__ 上执行格式化, 成功后会在当前节点生成 `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name` 目录
```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs namenode -format
19/05/30 20:31:21 INFO namenode.NameNode: STARTUP_MSG:
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   host = vm017/172.16.0.17
STARTUP_MSG:   args = [-format]
STARTUP_MSG:   version = 2.7.2
STARTUP_MSG:   classpath = /home/zozo/app/hadoop/hadoop-2.7.2/etc/hadoop:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jets3t-0.9.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-1.7.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-configuration-1.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-httpclient-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-kerberos-codec-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-auth-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-framework-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/mockito-all-1.8.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-math3-3.1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/java-xmlbuilder-0.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-api-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/gson-2.2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-asn1-api-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-core-1.8.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-client-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-net-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsp-api-2.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpclient-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-util-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-recipes-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-digester-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpcore-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsch-0.1.42.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-daemon-1.0.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-all-4.0.23.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xml-apis-1.3.04.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xercesImpl-2.9.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-client-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-api-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-registry-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-applicationhistoryservice-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-tests-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-resourcemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-sharedcachemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-nodemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-web-proxy-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-client-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-shuffle-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-app-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-plugins-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/contrib/capacity-scheduler/*.jar
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r b165c4fe8a74265c792ce23f546c64604acf0e41; compiled by 'jenkins' on 2016-01-26T00:08Z
STARTUP_MSG:   java = 1.8.0_192
************************************************************/
19/05/30 20:31:21 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
19/05/30 20:31:21 INFO namenode.NameNode: createNameNode [-format]
Formatting using clusterid: CID-e92be087-6559-483e-ad41-fed34226abe1
19/05/30 20:31:22 INFO namenode.FSNamesystem: No KeyProvider found.
19/05/30 20:31:22 INFO namenode.FSNamesystem: fsLock is fair:true
19/05/30 20:31:22 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
19/05/30 20:31:22 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
19/05/30 20:31:22 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
19/05/30 20:31:22 INFO blockmanagement.BlockManager: The block deletion will start around 2019 五月 30 20:31:22
19/05/30 20:31:22 INFO util.GSet: Computing capacity for map BlocksMap
19/05/30 20:31:22 INFO util.GSet: VM type       = 64-bit
19/05/30 20:31:22 INFO util.GSet: 2.0% max memory 889 MB = 17.8 MB
19/05/30 20:31:22 INFO util.GSet: capacity      = 2^21 = 2097152 entries
19/05/30 20:31:22 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
19/05/30 20:31:22 INFO blockmanagement.BlockManager: defaultReplication         = 3
19/05/30 20:31:22 INFO blockmanagement.BlockManager: maxReplication             = 512
19/05/30 20:31:22 INFO blockmanagement.BlockManager: minReplication             = 1
19/05/30 20:31:22 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
19/05/30 20:31:22 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
19/05/30 20:31:22 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
19/05/30 20:31:22 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
19/05/30 20:31:22 INFO namenode.FSNamesystem: fsOwner             = zozo (auth:SIMPLE)
19/05/30 20:31:22 INFO namenode.FSNamesystem: supergroup          = supergroup
19/05/30 20:31:22 INFO namenode.FSNamesystem: isPermissionEnabled = true
19/05/30 20:31:22 INFO namenode.FSNamesystem: HA Enabled: false
19/05/30 20:31:22 INFO namenode.FSNamesystem: Append Enabled: true
19/05/30 20:31:23 INFO util.GSet: Computing capacity for map INodeMap
19/05/30 20:31:23 INFO util.GSet: VM type       = 64-bit
19/05/30 20:31:23 INFO util.GSet: 1.0% max memory 889 MB = 8.9 MB
19/05/30 20:31:23 INFO util.GSet: capacity      = 2^20 = 1048576 entries
19/05/30 20:31:23 INFO namenode.FSDirectory: ACLs enabled? false
19/05/30 20:31:23 INFO namenode.FSDirectory: XAttrs enabled? true
19/05/30 20:31:23 INFO namenode.FSDirectory: Maximum size of an xattr: 16384
19/05/30 20:31:23 INFO namenode.NameNode: Caching file names occuring more than 10 times
19/05/30 20:31:23 INFO util.GSet: Computing capacity for map cachedBlocks
19/05/30 20:31:23 INFO util.GSet: VM type       = 64-bit
19/05/30 20:31:23 INFO util.GSet: 0.25% max memory 889 MB = 2.2 MB
19/05/30 20:31:23 INFO util.GSet: capacity      = 2^18 = 262144 entries
19/05/30 20:31:23 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
19/05/30 20:31:23 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
19/05/30 20:31:23 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
19/05/30 20:31:23 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.window.num.buckets = 10
19/05/30 20:31:23 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.num.users = 10
19/05/30 20:31:23 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.windows.minutes = 1,5,25
19/05/30 20:31:23 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
19/05/30 20:31:23 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
19/05/30 20:31:23 INFO util.GSet: Computing capacity for map NameNodeRetryCache
19/05/30 20:31:23 INFO util.GSet: VM type       = 64-bit
19/05/30 20:31:23 INFO util.GSet: 0.029999999329447746% max memory 889 MB = 273.1 KB
19/05/30 20:31:23 INFO util.GSet: capacity      = 2^15 = 32768 entries
19/05/30 20:31:23 INFO namenode.FSImage: Allocated new BlockPoolId: BP-1307457662-172.16.0.17-1559219483117
19/05/30 20:31:23 INFO common.Storage: Storage directory /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name has been successfully formatted.
19/05/30 20:31:23 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
19/05/30 20:31:23 INFO util.ExitUtil: Exiting with status 0
19/05/30 20:31:23 INFO namenode.NameNode: SHUTDOWN_MSG:
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at vm017/172.16.0.17
************************************************************/
[zozo@vm017 hadoop-2.7.2]$
[zozo@vm017 hadoop-2.7.2]$ cd ..
[zozo@vm017 hadoop]$ ll
总用量 207296
drwxr-xr-x 9 zozo zozo      4096 1月  26 2016 hadoop-2.7.2
drwxrwxr-x 3 zozo zozo      4096 5月  30 20:31 hadoop-2.7.2-data
-rw-r--r-- 1 zozo zozo 212046774 5月  29 00:35 hadoop-2.7.2.tar.gz
[zozo@vm017 hadoop]$ ll -R hadoop-2.7.2-data
hadoop-2.7.2-data:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 5月  30 20:31 tmp

hadoop-2.7.2-data/tmp:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 5月  30 20:31 dfs

hadoop-2.7.2-data/tmp/dfs:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 5月  30 20:31 name

hadoop-2.7.2-data/tmp/dfs/name:
总用量 4
drwxrwxr-x 2 zozo zozo 4096 5月  30 20:31 current

hadoop-2.7.2-data/tmp/dfs/name/current:
总用量 16
-rw-rw-r-- 1 zozo zozo 351 5月  30 20:31 fsimage_0000000000000000000
-rw-rw-r-- 1 zozo zozo  62 5月  30 20:31 fsimage_0000000000000000000.md5
-rw-rw-r-- 1 zozo zozo   2 5月  30 20:31 seen_txid
-rw-rw-r-- 1 zozo zozo 204 5月  30 20:31 VERSION
[zozo@vm017 hadoop]$
```

- 2. 在 __vm017__ 上启动 NameNode
```
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start namenode
starting namenode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-namenode-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
30225 sun.tools.jps.Jps -m -l
30148 org.apache.hadoop.hdfs.server.namenode.NameNode
[zozo@vm017 hadoop-2.7.2]$
```

- 3. 在 __vm017__ 上启动 DataNode, 成功后会在当前节点生成 `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data` 目录
```
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start datanode
starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
30148 org.apache.hadoop.hdfs.server.namenode.NameNode
30423 sun.tools.jps.Jps -m -l
30335 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ cd ..
[zozo@vm017 hadoop]$ ll -R hadoop-2.7.2-data
hadoop-2.7.2-data:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 5月  30 20:31 tmp

hadoop-2.7.2-data/tmp:
总用量 4
drwxrwxr-x 4 zozo zozo 4096 5月  30 20:43 dfs

hadoop-2.7.2-data/tmp/dfs:
总用量 8
drwx------ 3 zozo zozo 4096 5月  30 20:43 data
drwxrwxr-x 3 zozo zozo 4096 5月  30 20:42 name

hadoop-2.7.2-data/tmp/dfs/data:
总用量 8
drwxrwxr-x 3 zozo zozo 4096 5月  30 20:43 current
-rw-rw-r-- 1 zozo zozo   11 5月  30 20:43 in_use.lock

hadoop-2.7.2-data/tmp/dfs/data/current:
总用量 8
drwx------ 4 zozo zozo 4096 5月  30 20:43 BP-1307457662-172.16.0.17-1559219483117
-rw-rw-r-- 1 zozo zozo  229 5月  30 20:43 VERSION

hadoop-2.7.2-data/tmp/dfs/data/current/BP-1307457662-172.16.0.17-1559219483117:
总用量 12
drwxrwxr-x 4 zozo zozo 4096 5月  30 20:43 current
-rw-rw-r-- 1 zozo zozo  166 5月  30 20:43 scanner.cursor
drwxrwxr-x 2 zozo zozo 4096 5月  30 20:43 tmp

hadoop-2.7.2-data/tmp/dfs/data/current/BP-1307457662-172.16.0.17-1559219483117/current:
总用量 12
drwxrwxr-x 2 zozo zozo 4096 5月  30 20:43 finalized
drwxrwxr-x 2 zozo zozo 4096 5月  30 20:43 rbw
-rw-rw-r-- 1 zozo zozo  131 5月  30 20:43 VERSION

hadoop-2.7.2-data/tmp/dfs/data/current/BP-1307457662-172.16.0.17-1559219483117/current/finalized:
总用量 0

hadoop-2.7.2-data/tmp/dfs/data/current/BP-1307457662-172.16.0.17-1559219483117/current/rbw:
总用量 0

hadoop-2.7.2-data/tmp/dfs/data/current/BP-1307457662-172.16.0.17-1559219483117/tmp:
总用量 0

hadoop-2.7.2-data/tmp/dfs/name:
总用量 8
drwxrwxr-x 2 zozo zozo 4096 5月  30 20:42 current
-rw-rw-r-- 1 zozo zozo   11 5月  30 20:42 in_use.lock

hadoop-2.7.2-data/tmp/dfs/name/current:
总用量 1044
-rw-rw-r-- 1 zozo zozo 1048576 5月  30 20:42 edits_inprogress_0000000000000000001
-rw-rw-r-- 1 zozo zozo     351 5月  30 20:31 fsimage_0000000000000000000
-rw-rw-r-- 1 zozo zozo      62 5月  30 20:31 fsimage_0000000000000000000.md5
-rw-rw-r-- 1 zozo zozo       2 5月  30 20:42 seen_txid
-rw-rw-r-- 1 zozo zozo     204 5月  30 20:31 VERSION
[zozo@vm017 hadoop]$
```

### 3.4.2 vm06: 启动 DataNode

- 1. 在 __vm06__ 上启动 DataNode, 成功后会在当前节点生成 `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data` 目录
```
[zozo@vm06 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start datanode
starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm06.out
[zozo@vm06 hadoop-2.7.2]$ jps -m -l
29220 sun.tools.jps.Jps -m -l
29179 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm06 hadoop-2.7.2]$ cd ..
[zozo@vm06 hadoop]$ ll
总用量 207296
drwxr-xr-x 10 zozo zozo      4096 5月  30 20:46 hadoop-2.7.2
drwxrwxr-x  3 zozo zozo      4096 5月  30 20:47 hadoop-2.7.2-data
-rw-r--r--  1 zozo zozo 212046774 5月  19 16:30 hadoop-2.7.2.tar.gz
[zozo@vm06 hadoop]$ ll -R hadoop-2.7.2-data
hadoop-2.7.2-data:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 5月  30 20:47 tmp

hadoop-2.7.2-data/tmp:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 5月  30 20:47 dfs

hadoop-2.7.2-data/tmp/dfs:
总用量 4
drwx------ 2 zozo zozo 4096 5月  30 20:47 data

hadoop-2.7.2-data/tmp/dfs/data:
总用量 0
[zozo@vm06 hadoop]$
```

### 3.4.3 vm03: 启动 DataNode

- 1. 在 __vm03__ 上启动 DataNode, 成功后会在当前节点生成 `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data` 目录
```
[zozo@vm03 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start datanode
starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm03.out
[zozo@vm03 hadoop-2.7.2]$ jps -m -l
29476 org.apache.hadoop.hdfs.server.datanode.DataNode
29557 sun.tools.jps.Jps -m -l
[zozo@vm03 hadoop-2.7.2]$ cd ..
[zozo@vm03 hadoop]$ ll
总用量 207296
drwxr-xr-x 10 zozo zozo      4096 5月  30 20:48 hadoop-2.7.2
drwxrwxr-x  3 zozo zozo      4096 5月  30 20:48 hadoop-2.7.2-data
-rw-r--r--  1 zozo zozo 212046774 5月  19 16:50 hadoop-2.7.2.tar.gz
[zozo@vm03 hadoop]$ ll -R hadoop-2.7.2-data
hadoop-2.7.2-data:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 5月  30 20:48 tmp

hadoop-2.7.2-data/tmp:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 5月  30 20:48 dfs

hadoop-2.7.2-data/tmp/dfs:
总用量 4
drwx------ 2 zozo zozo 4096 5月  30 20:48 data

hadoop-2.7.2-data/tmp/dfs/data:
总用量 0
[zozo@vm03 hadoop]$
```

### 3.4.4 浏览器查看 HDFS

访问 [HDFS 控制台](http://193.112.38.200:50070) 检查是否可用

## 3.5 配置 SSH 免密登录

- 因为 __vm017__ 为 NameNode, 需要配置 SSH 免密登录所有节点. 这样在调用 `start-all.sh` 脚本时, 无需输入密码.
- 因为 __vm03__ 为 NodeManager, 需要配置 SSH 免密登录所有节点.

注: 需要配置免密登录所有节点 (包括本机).

- link
  - [SSH免密登录原理及实现](https://blog.csdn.net/qq_26907251/article/details/78804367)  (参考: 图)
	- [ssh免密码登录配置方法](https://blog.csdn.net/universe_hao/article/details/52296811)  (参考: 操作)
  - [SSH免密登录原理及配置](https://my.oschina.net/binxin/blog/651565)  (参考: 权限)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/SSH%E5%85%8D%E5%AF%86%E7%99%BB%E5%BD%95%E5%8E%9F%E7%90%86.png?raw=true)

`~/.ssh` 目录下文件说明:

| 文件 | 说明 |
| :--- | :--- |
| `~/.ssh/know_hosts` | 记录 SSH 访问过的其他服务器 |
| `~/.ssh/id_rsa` | 本机生成的私钥 |
| `~/.ssh/id_rsa.pub` | 本机生成的公钥 (可免密登录其他服务器) |
| `~/.ssh/authorized_keys` | 存放已授权其他服务器 (可免密登录本机 (包括本机)) 的公钥 |

以下以 __vm017__ 免密登录到所有机器的具体步骤, __vm03__ 类似操作:

### 3.5.1 vm017 生成密钥

如果没有 `~/.ssh`, 可以先通过 ssh 登录其他机器, 就会产生 `~/.ssh/known_hosts` 文件, 如下:
```
[zozo@vm017 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm017 .ssh]$ cat known_hosts
172.16.0.3 ecdsa-sha2-nistp256 AAAAE2VjZHNhLxxxxxxxxxxxxxxxxxxxOxBeGA7kqpUJGNbIz0EC0Mqwi0FJNc+V1aQkmx8c+olPBBUhVFSGHxyyyyyyyyyyyyyyyyyyyyys2No=
172.16.0.6 ecdsa-sha2-nistp256 AAAAE2VjZHNxxxxxxxxxxxxxxxxxxxOxBeGA7kqpUJGNbIz0EC0Mqwi0FJNc+V1YXMY+Z3zzzzzzzzzzzzzzejRVJ/gQ3OU67ybhgwwwwwwwwwwwwN8crgYxw=
[zozo@vm017 .ssh]$
```

在 __vm017__ `~/.ssh` 目录下执行以下命令, 通过 RSA 算法 (非对称加密算法) 进行加密, 提示输入 3 次回车后, 该目录下将会产生 `id_rsa` (私钥), `id_rsa.pub` (公钥) 文件:
```
[zozo@vm017 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm017 .ssh]$ ll
总用量 4
-rw-r--r-- 1 zozo zozo 344 5月  30 20:17 known_hosts
[zozo@vm017 .ssh]$ ssh-keygen -t rsa
Generating public/private rsa key pair.
Enter file in which to save the key (/home/zozo/.ssh/id_rsa):
Enter passphrase (empty for no passphrase):
Enter same passphrase again:
Your identification has been saved in /home/zozo/.ssh/id_rsa.
Your public key has been saved in /home/zozo/.ssh/id_rsa.pub.
The key fingerprint is:
SHA256:iscm/wJxOeN04zld8YgSOhQc3TdfTqLwMLW5DC7hIVs zozo@vm017
The key's randomart image is:
+---[RSA 2048]----+
|     .o+ . ..    |
|      o o = +o. o|
|     ..oE..BoB = |
|    . O=++.o=.o .|
|     =.*S=..o    |
|    .o..+..      |
|    o.=  .       |
|     =.          |
|      .o.        |
+----[SHA256]-----+
[zozo@vm017 .ssh]$ ll
总用量 12
-rw------- 1 zozo zozo 1675 6月   1 15:16 id_rsa
-rw-r--r-- 1 zozo zozo  392 6月   1 15:16 id_rsa.pub
-rw-r--r-- 1 zozo zozo  344 5月  30 20:17 known_hosts
[zozo@vm017 .ssh]$
```

### 3.5.2 vm017 发送 authorized_keys

将 __vm017__ 的公钥发送给 __vm06__, __vm03__, __vm017__ 完成后 __vm06__, __vm03__, __vm017__ 会生成 `~/.ssh/authorized_keys` 文件, 且该文件内容和 __vm017__ 的 `~/.ssh/id_rsa.pub` 相同.

- 以下为 __vm017__ 的操作:
```
[zozo@vm017 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm017 .ssh]$ ll
总用量 12
-rw------- 1 zozo zozo 1675 6月   1 15:16 id_rsa
-rw-r--r-- 1 zozo zozo  392 6月   1 15:16 id_rsa.pub
-rw-r--r-- 1 zozo zozo  344 5月  30 20:17 known_hosts
[zozo@vm017 .ssh]$ cat id_rsa.pub
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABTTTTTTTTxxVVjseYFy/ZNpgYFFooD5Tf8obtsVmvzbbbdccdffff0hCdaNc2P1m8ynYmeHhU8e4ZtNc2YW2ZCcn433Z6241M0/sN6HecsEBjK/3tn5jNvyWJoKFNyUofURULEhtE/0aB8F/aHArneRW5m36FPHD/huo0Cf2dfdffdfffGBQHwxjelr+3BcRY8ZPvzGljhhsLlxvC1gd/xyGorUs3814WiRNEoaYh0asiYF2RQrtUDS5xvzyvsS45glsL2yLySSr3ponD8WSBAtzS2HegJKYPEShi9zdferefdxckDY+RGJ2tDAW24/MW4JObKX1qdq7EeOVF zozo@vm017
[zozo@vm017 .ssh]$ ssh-copy-id zozo@172.16.0.6
/usr/bin/ssh-copy-id: INFO: Source of key(s) to be installed: "/home/zozo/.ssh/id_rsa.pub"
/usr/bin/ssh-copy-id: INFO: attempting to log in with the new key(s), to filter out any that are already installed
/usr/bin/ssh-copy-id: INFO: 1 key(s) remain to be installed -- if you are prompted now it is to install the new keys
zozo@172.16.0.6's password:

Number of key(s) added: 1

Now try logging into the machine, with:   "ssh 'zozo@172.16.0.6'"
and check to make sure that only the key(s) you wanted were added.

[zozo@vm017 .ssh]$ ssh-copy-id zozo@172.16.0.3
/usr/bin/ssh-copy-id: INFO: Source of key(s) to be installed: "/home/zozo/.ssh/id_rsa.pub"
/usr/bin/ssh-copy-id: INFO: attempting to log in with the new key(s), to filter out any that are already installed
/usr/bin/ssh-copy-id: INFO: 1 key(s) remain to be installed -- if you are prompted now it is to install the new keys
zozo@172.16.0.3's password:

Number of key(s) added: 1

Now try logging into the machine, with:   "ssh 'zozo@172.16.0.3'"
and check to make sure that only the key(s) you wanted were added.

[zozo@vm017 .ssh]$ ssh-copy-id zozo@172.16.0.17
/usr/bin/ssh-copy-id: INFO: Source of key(s) to be installed: "/home/zozo/.ssh/id_rsa.pub"
The authenticity of host '172.16.0.17 (172.16.0.17)' can't be established.
ECDSA key fingerprint is SHA256:+ThGOg/FjnUGE1evxEs2R4173M48Nmq9RMJbHNALqFI.
ECDSA key fingerprint is MD5:16:a6:bb:11:fd:c5:a7:5b:7b:c9:35:04:4a:2e:12:5b.
Are you sure you want to continue connecting (yes/no)? yes
/usr/bin/ssh-copy-id: INFO: attempting to log in with the new key(s), to filter out any that are already installed
/usr/bin/ssh-copy-id: INFO: 1 key(s) remain to be installed -- if you are prompted now it is to install the new keys
zozo@172.16.0.17's password:

Number of key(s) added: 1

Now try logging into the machine, with:   "ssh 'zozo@172.16.0.17'"
and check to make sure that only the key(s) you wanted were added.

[zozo@vm017 .ssh]$ ll
总用量 16
-rw------- 1 zozo zozo  392 6月   1 17:30 authorized_keys
-rw------- 1 zozo zozo 1675 6月   1 15:16 id_rsa
-rw-r--r-- 1 zozo zozo  392 6月   1 15:16 id_rsa.pub
-rw-r--r-- 1 zozo zozo  517 6月   1 17:30 known_hosts
[zozo@vm017 .ssh]$
```

- 以下为 __vm06__ 的 `~/.ssh/authorized_keys` 文件:
```
[zozo@vm06 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm06 .ssh]$ ll
总用量 8
-rw------- 1 zozo zozo 392 6月   1 16:48 authorized_keys
-rw-r--r-- 1 zozo zozo 345 6月   1 15:11 known_hosts
[zozo@vm06 .ssh]$ cat authorized_keys
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABTTTTTTTTxxVVjseYFy/ZNpgYFFooD5Tf8obtsVmvzbbbdccdffff0hCdaNc2P1m8ynYmeHhU8e4ZtNc2YW2ZCcn433Z6241M0/sN6HecsEBjK/3tn5jNvyWJoKFNyUofURULEhtE/0aB8F/aHArneRW5m36FPHD/huo0Cf2dfdffdfffGBQHwxjelr+3BcRY8ZPvzGljhhsLlxvC1gd/xyGorUs3814WiRNEoaYh0asiYF2RQrtUDS5xvzyvsS45glsL2yLySSr3ponD8WSBAtzS2HegJKYPEShi9zdferefdxckDY+RGJ2tDAW24/MW4JObKX1qdq7EeOVF zozo@vm017
[zozo@vm06 .ssh]$
```

- 以下为 __vm03__ 的 `~/.ssh/authorized_keys` 文件:
```
[zozo@vm03 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm03 .ssh]$ ll
总用量 8
-rw------- 1 zozo zozo 392 6月   1 16:51 authorized_keys
-rw-r--r-- 1 zozo zozo 345 6月   1 15:12 known_hosts
[zozo@vm03 .ssh]$ cat authorized_keys
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABTTTTTTTTxxVVjseYFy/ZNpgYFFooD5Tf8obtsVmvzbbbdccdffff0hCdaNc2P1m8ynYmeHhU8e4ZtNc2YW2ZCcn433Z6241M0/sN6HecsEBjK/3tn5jNvyWJoKFNyUofURULEhtE/0aB8F/aHArneRW5m36FPHD/huo0Cf2dfdffdfffGBQHwxjelr+3BcRY8ZPvzGljhhsLlxvC1gd/xyGorUs3814WiRNEoaYh0asiYF2RQrtUDS5xvzyvsS45glsL2yLySSr3ponD8WSBAtzS2HegJKYPEShi9zdferefdxckDY+RGJ2tDAW24/MW4JObKX1qdq7EeOVF zozo@vm017
[zozo@vm03 .ssh]$
```

- 以下为 __vm017__ 的 `~/.ssh/authorized_keys` 文件:
```
[zozo@vm017 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm017 .ssh]$ ll
总用量 16
-rw------- 1 zozo zozo  392 6月   1 17:30 authorized_keys
-rw------- 1 zozo zozo 1675 6月   1 15:16 id_rsa
-rw-r--r-- 1 zozo zozo  392 6月   1 15:16 id_rsa.pub
-rw-r--r-- 1 zozo zozo  517 6月   1 17:30 known_hosts
[zozo@vm017 .ssh]$ cat authorized_keys
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABTTTTTTTTxxVVjseYFy/ZNpgYFFooD5Tf8obtsVmvzbbbdccdffff0hCdaNc2P1m8ynYmeHhU8e4ZtNc2YW2ZCcn433Z6241M0/sN6HecsEBjK/3tn5jNvyWJoKFNyUofURULEhtE/0aB8F/aHArneRW5m36FPHD/huo0Cf2dfdffdfffGBQHwxjelr+3BcRY8ZPvzGljhhsLlxvC1gd/xyGorUs3814WiRNEoaYh0asiYF2RQrtUDS5xvzyvsS45glsL2yLySSr3ponD8WSBAtzS2HegJKYPEShi9zdferefdxckDY+RGJ2tDAW24/MW4JObKX1qdq7EeOVF zozo@vm017
[zozo@vm017 .ssh]$
```

### 3.5.3 确认 vm06, vm03, vm017 的文件权限

需要确保 __vm06__, __vm03__, __vm017__ 的 `~/.ssh` 文件夹权限为 `700`, `~/.ssh/authorized_keys` 文件权限为 `600`, 如下:
```
[zozo@vm06 ~]$ pwd
/home/zozo
[zozo@vm06 ~]$ ls -al
总用量 88
drwx------  6 zozo zozo  4096 6月   1 15:10 .
drwxr-xr-x. 5 root root  4096 11月 30 2018 ..
drwxrwxr-x  6 zozo zozo  4096 5月  19 16:19 app
-rw-------  1 zozo zozo 37895 6月   1 17:00 .bash_history
-rw-r--r--  1 zozo zozo    18 8月   3 2017 .bash_logout
-rw-r--r--  1 zozo zozo   602 5月  29 20:06 .bash_profile
-rw-r--r--  1 zozo zozo   231 8月   3 2017 .bashrc
drwxrwxr-x  3 zozo zozo  4096 11月 30 2018 .cache
drwxrwxr-x  3 zozo zozo  4096 11月 30 2018 .config
drwx------  2 zozo zozo  4096 6月   1 16:48 .ssh
-rw-------  1 zozo zozo 10105 5月  29 21:07 .viminfo
[zozo@vm06 ~]$ cd .ssh
[zozo@vm06 .ssh]$ ll
总用量 8
-rw------- 1 zozo zozo 392 6月   1 16:48 authorized_keys
-rw-r--r-- 1 zozo zozo 345 6月   1 15:11 known_hosts
[zozo@vm06 .ssh]$
```

如果如果权限不对需要进行设置, 如下所示:
```
[zozo@vm06 ~]$ pwd
/home/zozo
[zozo@vm06 ~]$ chmod 700 .ssh
[zozo@vm06 ~]$ ls -al
总用量 88
drwx------  6 zozo zozo  4096 6月   1 15:10 .
drwxr-xr-x. 5 root root  4096 11月 30 2018 ..
drwxrwxr-x  6 zozo zozo  4096 5月  19 16:19 app
-rw-------  1 zozo zozo 38049 6月   1 17:01 .bash_history
-rw-r--r--  1 zozo zozo    18 8月   3 2017 .bash_logout
-rw-r--r--  1 zozo zozo   602 5月  29 20:06 .bash_profile
-rw-r--r--  1 zozo zozo   231 8月   3 2017 .bashrc
drwxrwxr-x  3 zozo zozo  4096 11月 30 2018 .cache
drwxrwxr-x  3 zozo zozo  4096 11月 30 2018 .config
drwx------  2 zozo zozo  4096 6月   1 16:48 .ssh
-rw-------  1 zozo zozo 10105 5月  29 21:07 .viminfo
[zozo@vm06 ~]$ cd .ssh
[zozo@vm06 .ssh]$ chmod 600 authorized_keys
[zozo@vm06 .ssh]$ ll
总用量 8
-rw------- 1 zozo zozo 392 6月   1 16:48 authorized_keys
-rw-r--r-- 1 zozo zozo 345 6月   1 15:11 known_hosts
[zozo@vm06 .ssh]$
```

### 3.5.4 测试免密登录

在 __vm017__ 上测试免密登录 __vm06__, __vm03__, __vm017__:
```
[zozo@vm017 .ssh]$ ssh zozo@172.16.0.6
Last login: Sat Jun  1 15:12:09 2019 from 172.16.0.3
[zozo@vm06 ~]$ exit
登出
Connection to 172.16.0.6 closed.
[zozo@vm017 .ssh]$ ssh zozo@172.16.0.3
Last login: Sat Jun  1 15:11:05 2019 from 172.16.0.6
[zozo@vm03 ~]$ exit
登出
Connection to 172.16.0.3 closed.
[zozo@vm017 .ssh]$ ssh zozo@172.16.0.17
Last login: Sat Jun  1 16:42:32 2019 from 14.29.126.59
[zozo@vm017 ~]$ exit
登出
Connection to 172.16.0.17 closed.
[zozo@vm017 .ssh]$
```


---
