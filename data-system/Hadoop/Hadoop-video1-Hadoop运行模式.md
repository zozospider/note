
# 本地运行模式

- 文档: https://hadoop.apache.org/docs/r2.7.2/


## grep 案例

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

---

# 伪分布式运行模式

---

# 完全分布式运行模式

---
