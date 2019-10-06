
- [一 MapReduce 定义](#一-mapreduce-定义)
- [二 MapReduce 优缺点](#二-mapreduce-优缺点)
    - [2.1 优点](#21-优点)
    - [2.2 缺点](#22-缺点)
- [三 MapReduce 核心思想](#三-mapreduce-核心思想)
- [四 官方 WordCount 源码](#四-官方-wordcount-源码)
- [五 常用数据序列化类型](#五-常用数据序列化类型)
- [六 MapReduce 进程](#六-mapreduce-进程)
- [七 MapReduce 编程规范](#七-mapreduce-编程规范)
    - [7.1 Mapper](#71-mapper)
    - [7.2 Reducer](#72-reducer)
    - [7.3 Driver](#73-driver)
- [八 WordCount 案例实操](#八-wordcount-案例实操)

---

# 一 MapReduce 定义

MapReduce 是一个分布式运算程序的编程框架, 是用于开发基于 Hadoop 的数据分析应用的核心框架.

MapReduce 核心功能是将用户编写的业务逻辑代码和自带默认组件整合成一个完整的分布式运算程序, 并发运行在一个 Hadoop 集群上.

---

# 二 MapReduce 优缺点

## 2.1 优点

- __MapReduce 易于编程__: 它简单的实现一些接口, 就可以完成一个分布式程序. 这个分布式程序可以分布到大量廉价的 PC 机器上运行. 也就是说写一个分布式程序跟写一个简单的串行程序是一样的, 就是因为这个特点使得 MapReduce 编程变得非常流行.

- __良好的扩展性__: 当计算资源不能得到满足的时候, 可以通过简单的增加机器来扩展它的计算能力.

- __高容错性__: MapReduce 设计的初衷就是使程序能够部署在廉价的 PC 机器上, 这就要求它具有很高的容错性. 比如其中一台机器挂了, 它可以把上面的计算任务转移到另一个节点上运行, 从而不至于这个任务失败. 而且这个过程不需要人工参与, 而完全是由 Hadoop 内部完成的.

- __适合 PB 级以上海量数据的离线处理__: 可以实现上千台服务器集群并发工作, 提供数据处理能力.

## 2.2 缺点

- __不擅长实时计算__: MapReduce 无法像 MapSQL 一样, 在毫秒或者秒级内返回结果.

- __不擅长流式计算__: 流式计算的输入数据是动态的, 而 MapReduce 的输入数据集是静态的, 不能动态变化. 这是因为 MapReduce 自身的设计特点决定了数据源必须是静态的.

- __不擅长 DAG (有向图) 计算__: 多个应用程序存在依赖关系, 后一个应用程序的输入作为前一个的输出. 在这种情况下, MapReduce 并不是不能做, 而是使用后, 每个 MapReduce 作业的输出结果都会写入到磁盘, 会造成大量的磁盘 IO, 导致性能非常的地下.

---

# 三 MapReduce 核心思想

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A6%82%E8%BF%B0/MapReduce%E6%A0%B8%E5%BF%83%E7%BC%96%E7%A8%8B%E6%80%9D%E6%83%B3.png?raw=true)

---

# 四 官方 WordCount 源码

`TODO`: 反编译官方 jar 包中的 WordCount 案例.

---

# 五 常用数据序列化类型

| Java 类型 | Hadoop Writable 类型 |
| :--- | :--- |
| boolean | BooleanWritable |
| byte | ByteWritable |
| int | IntWritable |
| float | FloatWritable |
| long | LongWritable |
| double | DoubleWritable |
| String | Text |
| map | MapWritable |
| array | ArrayWritable |

---

# 六 MapReduce 进程

一个完整的 MapReduce 程序在分布式运行时有 3 个实例进程:
- `MrAppMaster`: 负责整个程序的过程调度及状态协调.
- `MapTask`: 负责 Map 阶段的整个数据处理流程.
- `ReduceTask`: 负责 Reduce 阶段的整个数据处理流程.

---

# 七 MapReduce 编程规范

用户编写的程序分为 3 个部分: `Mapper`, `Reducer`, `Driver`.

## 7.1 Mapper

- 用户自定义的 Mapper 要继承自己的父类.
- Mapper 的输入数据是 KV 对的形式 (KV 的类型可以自定义).
- Mapper 中的业务逻辑写在 map() 方法中.
- Mapper 的输出数据是 KV 对的形式 (KV 的类型可以自定义).
- MapTask 进程对每一个输入 KV 对调用一次 map() 方法.

## 7.2 Reducer

- 用户自定义的 Reducer 要继承自己的父类.
- Reducer 的输入数据类型对应 Mapper 的输出数据类型, 也是 KV.
- Reducer 的业务逻辑写在 reduce() 方法中.
- ReduceTask 进程对每一个组相同 key 的 KV 组调用一次 reduce() 方法.

## 7.3 Driver

- 相当于 YARN 集群的客户端, 用于提交我们整个程序到 YARN 集群, 提交的是封装了 MapReduce 程序相关的运行参数的 Job 对象.

---

# 八 WordCount 案例实操

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-MapReduce%E6%A6%82%E8%BF%B0/WordCount%E6%A1%88%E4%BE%8B%E5%88%86%E6%9E%90.png?raw=true)

## 8.1 代码和本地运行

参考以下项目:

- code
  - [zozospider/note-hadoop-video1](https://github.com/zozospider/note-hadoop-video1)

## 8.2 集群运行

- step1

maven 打包本地项目 (指定 main class).

- step2

将打包好的 jar 包 `note-hadoop-video1-1.0-SNAPSHOT.jar` 上传到集群 NameNode 节点.

- step3

运行 MapReduce 程序:

```
bin/hadoop jar /home/zozo/app/hadoop/mapReduce/jar/wordCount/note-hadoop-video1-1.0-SNAPSHOT.jar com.zozospider.hadoop.mapreduce.wordcount.WordCountDriver /user/zozo/mapReduce/input/wordCount /user/zozo/mapReduce/output/wordCount
```

运行前:
```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /
-rw-r--r--   3 zozo supergroup         30 2019-10-06 15:06 /f1
drwx------   - zozo supergroup          0 2019-10-06 15:07 /user
drwx------   - zozo supergroup          0 2019-10-06 15:20 /user/zozo
drwx------   - zozo supergroup          0 2019-10-06 15:30 /user/zozo/.Trash
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:20 /user/zozo/mapReduce
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:20 /user/zozo/mapReduce/input
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:22 /user/zozo/mapReduce/input/wordCount
-rw-r--r--   3 zozo supergroup         82 2019-10-06 15:22 /user/zozo/mapReduce/input/wordCount/inputWords
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:20 /user/zozo/mapReduce/output
[zozo@vm017 hadoop-2.7.2]$ 
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /user/zozo/mapReduce/
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:20 /user/zozo/mapReduce/input
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:22 /user/zozo/mapReduce/input/wordCount
-rw-r--r--   3 zozo supergroup         82 2019-10-06 15:22 /user/zozo/mapReduce/input/wordCount/inputWords
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:20 /user/zozo/mapReduce/output
[zozo@vm017 hadoop-2.7.2]$ 
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -cat /user/zozo/mapReduce/input/wordCount/inputWords
abc abc love
qq
see awesome book
qq who love
please google
enough book love me
end[zozo@vm017 hadoop-2.7.2]$ 
```

运行中:
```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop jar /home/zozo/app/hadoop/mapReduce/jar/wordCount/note-hadoop-video1-1.0-SNAPSHOT.jar com.zozospider.hadoop.mapreduce.wordcount.WordCountDriver /user/zozo/mapReduce/input/wordCount /user/zozo/mapReduce/output/wordCount
19/10/06 15:36:51 INFO client.RMProxy: Connecting to ResourceManager at vm03/172.16.0.3:8032
19/10/06 15:36:52 WARN mapreduce.JobResourceUploader: Hadoop command-line option parsing not performed. Implement the Tool interface and execute your application with ToolRunner to remedy this.
19/10/06 15:36:52 INFO input.FileInputFormat: Total input paths to process : 1
19/10/06 15:36:52 INFO mapreduce.JobSubmitter: number of splits:1
19/10/06 15:36:52 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1570345486962_0001
19/10/06 15:36:53 INFO impl.YarnClientImpl: Submitted application application_1570345486962_0001
19/10/06 15:36:53 INFO mapreduce.Job: The url to track the job: http://vm03:8088/proxy/application_1570345486962_0001/
19/10/06 15:36:53 INFO mapreduce.Job: Running job: job_1570345486962_0001
19/10/06 15:37:01 INFO mapreduce.Job: Job job_1570345486962_0001 running in uber mode : false
19/10/06 15:37:01 INFO mapreduce.Job:  map 0% reduce 0%
19/10/06 15:37:09 INFO mapreduce.Job:  map 100% reduce 0%
19/10/06 15:37:15 INFO mapreduce.Job:  map 100% reduce 100%
19/10/06 15:37:16 INFO mapreduce.Job: Job job_1570345486962_0001 completed successfully
19/10/06 15:37:17 INFO mapreduce.Job: Counters: 49
	File System Counters
		FILE: Number of bytes read=191
		FILE: Number of bytes written=235431
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=211
		HDFS: Number of bytes written=85
		HDFS: Number of read operations=6
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=2
	Job Counters 
		Launched map tasks=1
		Launched reduce tasks=1
		Data-local map tasks=1
		Total time spent by all maps in occupied slots (ms)=6038
		Total time spent by all reduces in occupied slots (ms)=3094
		Total time spent by all map tasks (ms)=6038
		Total time spent by all reduce tasks (ms)=3094
		Total vcore-milliseconds taken by all map tasks=6038
		Total vcore-milliseconds taken by all reduce tasks=3094
		Total megabyte-milliseconds taken by all map tasks=6182912
		Total megabyte-milliseconds taken by all reduce tasks=3168256
	Map-Reduce Framework
		Map input records=7
		Map output records=17
		Map output bytes=151
		Map output materialized bytes=191
		Input split bytes=129
		Combine input records=0
		Combine output records=0
		Reduce input groups=12
		Reduce shuffle bytes=191
		Reduce input records=17
		Reduce output records=12
		Spilled Records=34
		Shuffled Maps =1
		Failed Shuffles=0
		Merged Map outputs=1
		GC time elapsed (ms)=159
		CPU time spent (ms)=2030
		Physical memory (bytes) snapshot=421400576
		Virtual memory (bytes) snapshot=4208140288
		Total committed heap usage (bytes)=324009984
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters 
		Bytes Read=82
	File Output Format Counters 
		Bytes Written=85
[zozo@vm017 hadoop-2.7.2]$ 
```

运行后:
```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /
-rw-r--r--   3 zozo supergroup         30 2019-10-06 15:06 /f1
drwx------   - zozo supergroup          0 2019-10-06 15:36 /tmp
drwx------   - zozo supergroup          0 2019-10-06 15:36 /tmp/hadoop-yarn
drwx------   - zozo supergroup          0 2019-10-06 15:36 /tmp/hadoop-yarn/staging
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:36 /tmp/hadoop-yarn/staging/history
drwxrwxrwt   - zozo supergroup          0 2019-10-06 15:36 /tmp/hadoop-yarn/staging/history/done_intermediate
drwxrwx---   - zozo supergroup          0 2019-10-06 15:37 /tmp/hadoop-yarn/staging/history/done_intermediate/zozo
-rwxrwx---   3 zozo supergroup      33336 2019-10-06 15:37 /tmp/hadoop-yarn/staging/history/done_intermediate/zozo/job_1570345486962_0001-1570347413116-zozo-note%2Dhadoop%2Dvideo1%2D1.0%2DSNAPSHOT.jar-1570347434923-1-1-SUCCEEDED-default-1570347419816.jhist
-rwxrwx---   3 zozo supergroup        372 2019-10-06 15:37 /tmp/hadoop-yarn/staging/history/done_intermediate/zozo/job_1570345486962_0001.summary
-rwxrwx---   3 zozo supergroup     115879 2019-10-06 15:37 /tmp/hadoop-yarn/staging/history/done_intermediate/zozo/job_1570345486962_0001_conf.xml
drwx------   - zozo supergroup          0 2019-10-06 15:36 /tmp/hadoop-yarn/staging/zozo
drwx------   - zozo supergroup          0 2019-10-06 15:37 /tmp/hadoop-yarn/staging/zozo/.staging
drwx------   - zozo supergroup          0 2019-10-06 15:07 /user
drwx------   - zozo supergroup          0 2019-10-06 15:20 /user/zozo
drwx------   - zozo supergroup          0 2019-10-06 15:30 /user/zozo/.Trash
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:20 /user/zozo/mapReduce
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:20 /user/zozo/mapReduce/input
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:22 /user/zozo/mapReduce/input/wordCount
-rw-r--r--   3 zozo supergroup         82 2019-10-06 15:22 /user/zozo/mapReduce/input/wordCount/inputWords
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:36 /user/zozo/mapReduce/output
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:37 /user/zozo/mapReduce/output/wordCount
-rw-r--r--   3 zozo supergroup          0 2019-10-06 15:37 /user/zozo/mapReduce/output/wordCount/_SUCCESS
-rw-r--r--   3 zozo supergroup         85 2019-10-06 15:37 /user/zozo/mapReduce/output/wordCount/part-r-00000
[zozo@vm017 hadoop-2.7.2]$ 
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /user/zozo/mapReduce/
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:20 /user/zozo/mapReduce/input
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:22 /user/zozo/mapReduce/input/wordCount
-rw-r--r--   3 zozo supergroup         82 2019-10-06 15:22 /user/zozo/mapReduce/input/wordCount/inputWords
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:36 /user/zozo/mapReduce/output
drwxr-xr-x   - zozo supergroup          0 2019-10-06 15:37 /user/zozo/mapReduce/output/wordCount
-rw-r--r--   3 zozo supergroup          0 2019-10-06 15:37 /user/zozo/mapReduce/output/wordCount/_SUCCESS
-rw-r--r--   3 zozo supergroup         85 2019-10-06 15:37 /user/zozo/mapReduce/output/wordCount/part-r-00000
[zozo@vm017 hadoop-2.7.2]$ 
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /user/zozo/mapReduce/output/wordCount/
-rw-r--r--   3 zozo supergroup          0 2019-10-06 15:37 /user/zozo/mapReduce/output/wordCount/_SUCCESS
-rw-r--r--   3 zozo supergroup         85 2019-10-06 15:37 /user/zozo/mapReduce/output/wordCount/part-r-00000
[zozo@vm017 hadoop-2.7.2]$ 
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -cat /user/zozo/mapReduce/output/wordCount/part-r-00000
abc	2
awesome	1
book	2
end	1
enough	1
google	1
love	3
me	1
please	1
qq	2
see	1
who	1
[zozo@vm017 hadoop-2.7.2]$ 
```

---
