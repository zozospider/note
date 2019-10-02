
- [一 集群间数据拷贝](#一-集群间数据拷贝)
- [二 小文件存档](#二-小文件存档)
    - [2.1 启动 YARN 进程](#21-启动-yarn-进程)
    - [2.2 归档文件](#22-归档文件)
    - [2.3 查看归档](#23-查看归档)
    - [2.4 解归档文件](#24-解归档文件)
- [三 回收站](#三-回收站)
    - [3.1 启用回收站](#31-启用回收站)
    - [3.2 重启集群 (如果已启动)](#32-重启集群-如果已启动)
    - [3.3 删除数据并查看回收站](#33-删除数据并查看回收站)
    - [3.4 恢复回收站数据](#34-恢复回收站数据)
    - [3.5 清空回收站](#35-清空回收站)
- [四 快照管理](#四-快照管理)
        - [4.1 开启 / 禁用指定目录的快照功能](#41-开启--禁用指定目录的快照功能)
        - [4.2 创建快照](#42-创建快照)
        - [4.3 列出当前用户所有可快照目录并查看](#43-列出当前用户所有可快照目录并查看)
        - [4.4 重命名快照](#44-重命名快照)
        - [4.5 比较两个快照的不同之处](#45-比较两个快照的不同之处)
        - [4.6 恢复快照](#46-恢复快照)

---

# 一 集群间数据拷贝

- 说明

```bash
# 使用 `distcp` 命令实现两个 Hadoop 集群间的递归数据复制
bin/hadoop distcp OPTIONS [source_path...] <target_path>
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop distcp
usage: distcp OPTIONS [source_path...] <target_path>
              OPTIONS
 -append                Reuse existing data in target files and append new
                        data to them if possible
 -async                 Should distcp execution be blocking
 -atomic                Commit all changes or none
 -bandwidth <arg>       Specify bandwidth per map in MB
 -delete                Delete from target, files missing in source
 -diff <arg>            Use snapshot diff report to identify the
                        difference between source and target
 -f <arg>               List of files that need to be copied
 -filelimit <arg>       (Deprecated!) Limit number of files copied to <= n
 -i                     Ignore failures during copy
 -log <arg>             Folder on DFS where distcp execution logs are
                        saved
 -m <arg>               Max number of concurrent maps to use for copy
 -mapredSslConf <arg>   Configuration for ssl config file, to use with
                        hftps://
 -overwrite             Choose to overwrite target files unconditionally,
                        even if they exist.
 -p <arg>               preserve status (rbugpcaxt)(replication,
                        block-size, user, group, permission,
                        checksum-type, ACL, XATTR, timestamps). If -p is
                        specified with no <arg>, then preserves
                        replication, block size, user, group, permission,
                        checksum type and timestamps. raw.* xattrs are
                        preserved when both the source and destination
                        paths are in the /.reserved/raw hierarchy (HDFS
                        only). raw.* xattrpreservation is independent of
                        the -p flag. Refer to the DistCp documentation for
                        more details.
 -sizelimit <arg>       (Deprecated!) Limit number of files copied to <= n
                        bytes
 -skipcrccheck          Whether to skip CRC checks between source and
                        target paths.
 -strategy <arg>        Copy strategy to use. Default is dividing work
                        based on file sizes
 -tmp <arg>             Intermediate work path to be used for atomic
                        commit
 -update                Update target, copying only missingfiles or
                        directories
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
bin/hadoop distcp hdfs://vm017:9000/user/zozo/f1 hdfs://vm117:9000/user/zozo/f1
```

---

# 二 小文件存档

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-HDFS_2.X%E6%96%B0%E7%89%B9%E6%80%A7/%E5%B0%8F%E6%96%87%E4%BB%B6%E5%AD%98%E6%A1%A3.png?raw=true)

## 2.1 启动 YARN 进程

```
[zozo@vm017 hadoop-2.7.2]$ sbin/start-yarn.sh
[zozo@vm017 hadoop-2.7.2]$ 
```

## 2.2 归档文件

- 说明

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop archive
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop   
archive -archiveName <NAME>.har -p <parent path> [-r <replication factor>]<src>* <dest>

Invalid usage.
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

把 `/user/zozo/d1/` 目录下的所有文件归档成一个 `d1.har` 文件, 并把归档后的文件存储到 `/user/zozo/har/` 路径下

```
bin/hadoop archive -archiveName d1.har -p /home/zozo/d1 /home/zozo/har
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /home/zozo/har
ls: `/home/zozo/har': No such file or directory
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /home/zozo/d1/
Found 6 items
-rw-r--r--   3 zozo supergroup         21 2019-10-02 19:59 /home/zozo/d1/appendToF1
-rw-r--r--   3 zozo supergroup         30 2019-10-02 19:59 /home/zozo/d1/f1
-rw-r--r--   3 zozo supergroup         38 2019-10-02 19:59 /home/zozo/d1/f12
-rw-r--r--   3 zozo supergroup          8 2019-10-02 19:59 /home/zozo/d1/f2
-rw-r--r--   3 zozo supergroup       1722 2019-10-02 19:59 /home/zozo/d1/multiLines
-rw-r--r--   3 zozo supergroup        225 2019-10-02 20:00 /home/zozo/d1/safemode_wait.sh
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /
-rw-r--r--   3 zozo supergroup         30 2019-10-02 18:15 /f1
-rw-r--r--   3 zozo supergroup          8 2019-10-02 18:20 /f2
drwxr-xr-x   - zozo supergroup          0 2019-10-02 19:54 /home
drwxr-xr-x   - zozo supergroup          0 2019-10-02 20:03 /home/zozo
drwxr-xr-x   - zozo supergroup          0 2019-10-02 20:00 /home/zozo/d1
-rw-r--r--   3 zozo supergroup         21 2019-10-02 19:59 /home/zozo/d1/appendToF1
-rw-r--r--   3 zozo supergroup         30 2019-10-02 19:59 /home/zozo/d1/f1
-rw-r--r--   3 zozo supergroup         38 2019-10-02 19:59 /home/zozo/d1/f12
-rw-r--r--   3 zozo supergroup          8 2019-10-02 19:59 /home/zozo/d1/f2
-rw-r--r--   3 zozo supergroup       1722 2019-10-02 19:59 /home/zozo/d1/multiLines
-rw-r--r--   3 zozo supergroup        225 2019-10-02 20:00 /home/zozo/d1/safemode_wait.sh
[zozo@vm017 hadoop-2.7.2]$ 
[zozo@vm017 hadoop-2.7.2]$ 
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop archive -archiveName d1.har -p /home/zozo/d1 /home/zozo/har
19/10/02 20:02:48 INFO client.RMProxy: Connecting to ResourceManager at vm03/172.16.0.3:8032
19/10/02 20:02:49 INFO client.RMProxy: Connecting to ResourceManager at vm03/172.16.0.3:8032
19/10/02 20:02:49 INFO client.RMProxy: Connecting to ResourceManager at vm03/172.16.0.3:8032
19/10/02 20:02:49 INFO mapreduce.JobSubmitter: number of splits:1
19/10/02 20:02:50 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1570011013337_0001
19/10/02 20:02:50 INFO impl.YarnClientImpl: Submitted application application_1570011013337_0001
19/10/02 20:02:50 INFO mapreduce.Job: The url to track the job: http://vm03:8088/proxy/application_1570011013337_0001/
19/10/02 20:02:50 INFO mapreduce.Job: Running job: job_1570011013337_0001
19/10/02 20:03:02 INFO mapreduce.Job: Job job_1570011013337_0001 running in uber mode : false
19/10/02 20:03:02 INFO mapreduce.Job:  map 0% reduce 0%
19/10/02 20:03:08 INFO mapreduce.Job:  map 100% reduce 0%
19/10/02 20:03:16 INFO mapreduce.Job:  map 100% reduce 100%
19/10/02 20:03:16 INFO mapreduce.Job: Job job_1570011013337_0001 completed successfully
19/10/02 20:03:17 INFO mapreduce.Job: Counters: 49
	File System Counters
		FILE: Number of bytes read=532
		FILE: Number of bytes written=239309
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=2657
		HDFS: Number of bytes written=2551
		HDFS: Number of read operations=25
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=7
	Job Counters 
		Launched map tasks=1
		Launched reduce tasks=1
		Other local map tasks=1
		Total time spent by all maps in occupied slots (ms)=4239
		Total time spent by all reduces in occupied slots (ms)=4903
		Total time spent by all map tasks (ms)=4239
		Total time spent by all reduce tasks (ms)=4903
		Total vcore-milliseconds taken by all map tasks=4239
		Total vcore-milliseconds taken by all reduce tasks=4903
		Total megabyte-milliseconds taken by all map tasks=4340736
		Total megabyte-milliseconds taken by all reduce tasks=5020672
	Map-Reduce Framework
		Map input records=7
		Map output records=7
		Map output bytes=512
		Map output materialized bytes=532
		Input split bytes=116
		Combine input records=0
		Combine output records=0
		Reduce input groups=7
		Reduce shuffle bytes=532
		Reduce input records=7
		Reduce output records=0
		Spilled Records=14
		Shuffled Maps =1
		Failed Shuffles=0
		Merged Map outputs=1
		GC time elapsed (ms)=159
		CPU time spent (ms)=1610
		Physical memory (bytes) snapshot=426258432
		Virtual memory (bytes) snapshot=4214915072
		Total committed heap usage (bytes)=309854208
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters 
		Bytes Read=497
	File Output Format Counters 
		Bytes Written=0
[zozo@vm017 hadoop-2.7.2]$ 
```

## 2.3 查看归档

- 使用 __hdfs__ 协议查看:

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /home/zozo/har/
drwxr-xr-x   - zozo supergroup          0 2019-10-02 20:03 /home/zozo/har/d1.har
-rw-r--r--   3 zozo supergroup          0 2019-10-02 20:03 /home/zozo/har/d1.har/_SUCCESS
-rw-r--r--   5 zozo supergroup        484 2019-10-02 20:03 /home/zozo/har/d1.har/_index
-rw-r--r--   5 zozo supergroup         23 2019-10-02 20:03 /home/zozo/har/d1.har/_masterindex
-rw-r--r--   3 zozo supergroup       2044 2019-10-02 20:03 /home/zozo/har/d1.har/part-0
[zozo@vm017 hadoop-2.7.2]$ 
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /
-rw-r--r--   3 zozo supergroup         30 2019-10-02 18:15 /f1
-rw-r--r--   3 zozo supergroup          8 2019-10-02 18:20 /f2
drwxr-xr-x   - zozo supergroup          0 2019-10-02 19:54 /home
drwxr-xr-x   - zozo supergroup          0 2019-10-02 20:12 /home/zozo
drwxr-xr-x   - zozo supergroup          0 2019-10-02 20:00 /home/zozo/d1
-rw-r--r--   3 zozo supergroup         21 2019-10-02 19:59 /home/zozo/d1/appendToF1
-rw-r--r--   3 zozo supergroup         30 2019-10-02 19:59 /home/zozo/d1/f1
-rw-r--r--   3 zozo supergroup         38 2019-10-02 19:59 /home/zozo/d1/f12
-rw-r--r--   3 zozo supergroup          8 2019-10-02 19:59 /home/zozo/d1/f2
-rw-r--r--   3 zozo supergroup       1722 2019-10-02 19:59 /home/zozo/d1/multiLines
-rw-r--r--   3 zozo supergroup        225 2019-10-02 20:00 /home/zozo/d1/safemode_wait.sh
drwxr-xr-x   - zozo supergroup          0 2019-10-02 20:03 /home/zozo/har
drwxr-xr-x   - zozo supergroup          0 2019-10-02 20:03 /home/zozo/har/d1.har
-rw-r--r--   3 zozo supergroup          0 2019-10-02 20:03 /home/zozo/har/d1.har/_SUCCESS
-rw-r--r--   5 zozo supergroup        484 2019-10-02 20:03 /home/zozo/har/d1.har/_index
-rw-r--r--   5 zozo supergroup         23 2019-10-02 20:03 /home/zozo/har/d1.har/_masterindex
-rw-r--r--   3 zozo supergroup       2044 2019-10-02 20:03 /home/zozo/har/d1.har/part-0
drwx------   - zozo supergroup          0 2019-10-02 20:02 /tmp
drwx------   - zozo supergroup          0 2019-10-02 20:02 /tmp/hadoop-yarn
drwx------   - zozo supergroup          0 2019-10-02 20:02 /tmp/hadoop-yarn/staging
drwxr-xr-x   - zozo supergroup          0 2019-10-02 20:02 /tmp/hadoop-yarn/staging/history
drwxrwxrwt   - zozo supergroup          0 2019-10-02 20:02 /tmp/hadoop-yarn/staging/history/done_intermediate
drwxrwx---   - zozo supergroup          0 2019-10-02 20:03 /tmp/hadoop-yarn/staging/history/done_intermediate/zozo
-rwxrwx---   3 zozo supergroup      33380 2019-10-02 20:03 /tmp/hadoop-yarn/staging/history/done_intermediate/zozo/job_1570011013337_0001-1570017770352-zozo-hadoop%2Darchives%2D2.7.2.jar-1570017795439-1-1-SUCCEEDED-default-1570017780857.jhist
-rwxrwx---   3 zozo supergroup        362 2019-10-02 20:03 /tmp/hadoop-yarn/staging/history/done_intermediate/zozo/job_1570011013337_0001.summary
-rwxrwx---   3 zozo supergroup     117465 2019-10-02 20:03 /tmp/hadoop-yarn/staging/history/done_intermediate/zozo/job_1570011013337_0001_conf.xml
drwx------   - zozo supergroup          0 2019-10-02 20:02 /tmp/hadoop-yarn/staging/zozo
drwx------   - zozo supergroup          0 2019-10-02 20:03 /tmp/hadoop-yarn/staging/zozo/.staging
[zozo@vm017 hadoop-2.7.2]$ 
```

- 使用 __har__ 协议查看:

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R har:///home/zozo/har/
ls: Invalid path for the Har Filesystem. har:///home/zozo/har
[zozo@vm017 hadoop-2.7.2]$ 
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R har:///home/zozo/har/d1.har
-rw-r--r--   3 zozo supergroup         21 2019-10-02 19:59 har:///home/zozo/har/d1.har/appendToF1
-rw-r--r--   3 zozo supergroup         30 2019-10-02 19:59 har:///home/zozo/har/d1.har/f1
-rw-r--r--   3 zozo supergroup         38 2019-10-02 19:59 har:///home/zozo/har/d1.har/f12
-rw-r--r--   3 zozo supergroup          8 2019-10-02 19:59 har:///home/zozo/har/d1.har/f2
-rw-r--r--   3 zozo supergroup       1722 2019-10-02 19:59 har:///home/zozo/har/d1.har/multiLines
-rw-r--r--   3 zozo supergroup        225 2019-10-02 20:00 har:///home/zozo/har/d1.har/safemode_wait.sh
[zozo@vm017 hadoop-2.7.2]$ 
```

## 2.4 解归档文件

把归档文件 `/user/zozo/har/d1.har` 解到 `/home/zozo/unhar/d1/` 目录下.

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -cp har:///home/zozo/har/d1.har/* /home/zozo/unhar/d1/
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /home/zozo/unhar/d1/
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -cp har:///home/zozo/har/d1.har/* /home/zozo/unhar/d1/
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /home/zozo/unhar/d1/
-rw-r--r--   3 zozo supergroup         21 2019-10-02 20:17 /home/zozo/unhar/d1/appendToF1
-rw-r--r--   3 zozo supergroup         30 2019-10-02 20:17 /home/zozo/unhar/d1/f1
-rw-r--r--   3 zozo supergroup         38 2019-10-02 20:17 /home/zozo/unhar/d1/f12
-rw-r--r--   3 zozo supergroup          8 2019-10-02 20:17 /home/zozo/unhar/d1/f2
-rw-r--r--   3 zozo supergroup       1722 2019-10-02 20:17 /home/zozo/unhar/d1/multiLines
-rw-r--r--   3 zozo supergroup        225 2019-10-02 20:17 /home/zozo/unhar/d1/safemode_wait.sh
[zozo@vm017 hadoop-2.7.2]$ 
```

---

# 三 回收站

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-HDFS_2.X%E6%96%B0%E7%89%B9%E6%80%A7/%E5%9B%9E%E6%94%B6%E7%AB%99.png?raw=true)

## 3.1 启用回收站

在所有节点的 `core-site.xml` 中增加如下配置:

```xml
  <!-- 回收站文件存活时间 (分钟), (默认 0 表示禁用) -->
  <property>
    <name>fs.trash.interval</name>
    <!-- <value>0</value> -->
    <value>2</value>
    <description>Number of minutes after which the checkpoint gets deleted.  If zero, the trash feature is disabled. This option may be configured both on the server and the client. If trash is disabled server side then the client side configuration is checked. If trash is enabled on the server side then the value configured on the server is used and the client configuration value is ignored.
    </description>
  </property>

  <!-- Static Web User Filter properties. -->
  <!-- 默认用户需要修改为 zozo, 否则为默认用户 dr.who -->
  <property>
    <description>
    The user name to filter as, on static web filters while rendering content. An example use is the HDFS web UI (user to be used for browsing files).
    </description>
    <name>hadoop.http.staticuser.user</name>
    <!-- <value>dr.who</value> -->
    <value>zozo</value>
  </property>
```

## 3.2 重启集群 (如果已启动)


## 3.3 删除数据并查看回收站

- 删除数据并查看回收站:

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -rm /home/zozo/d1/f1
19/10/02 20:50:50 INFO fs.TrashPolicyDefault: Namenode trash configuration: Deletion interval = 2 minutes, Emptier interval = 0 minutes.
Moved: 'hdfs://vm017:9000/home/zozo/d1/f1' to trash at: hdfs://vm017:9000/user/zozo/.Trash/Current
[zozo@vm017 hadoop-2.7.2]$ 
[zozo@vm017 hadoop-2.7.2]$ 
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /user/zozo/.Trash/
drwx------   - zozo supergroup          0 2019-10-02 20:50 /user/zozo/.Trash/Current
drwx------   - zozo supergroup          0 2019-10-02 20:50 /user/zozo/.Trash/Current/home
drwx------   - zozo supergroup          0 2019-10-02 20:50 /user/zozo/.Trash/Current/home/zozo
drwx------   - zozo supergroup          0 2019-10-02 20:50 /user/zozo/.Trash/Current/home/zozo/d1
-rw-r--r--   3 zozo supergroup         30 2019-10-02 19:59 /user/zozo/.Trash/Current/home/zozo/d1/f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -cat /user/zozo/.Trash/Current/home/zozo/d1/f1
I am f1
[zozo@vm017 hadoop-2.7.2]$ 
```

- 2 分钟后再查看回收站, 回收站中的文件被移到当前目录的时间戳文件夹下 (__并没有被彻底删除__):

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /user/zozo/.Trash/
drwx------   - zozo supergroup          0 2019-10-02 20:50 /user/zozo/.Trash/191002205200
drwx------   - zozo supergroup          0 2019-10-02 20:50 /user/zozo/.Trash/191002205200/home
drwx------   - zozo supergroup          0 2019-10-02 20:50 /user/zozo/.Trash/191002205200/home/zozo
drwx------   - zozo supergroup          0 2019-10-02 20:50 /user/zozo/.Trash/191002205200/home/zozo/d1
-rw-r--r--   3 zozo supergroup         30 2019-10-02 19:59 /user/zozo/.Trash/191002205200/home/zozo/d1/f1
```

- 一段时间后再查看回收站 (__此时已被彻底删除__):

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /user/zozo/.Trash/
[zozo@vm017 hadoop-2.7.2]$ 
```

## 3.4 恢复回收站数据

恢复回收站的数据需要在回收站的数据删除前, 将数据拷贝出来. 否则无法恢复.

## 3.5 清空回收站

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -expunge
```

- 将 HDFS 文件删除后, 文件会被移动到回收站中:

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /f2
-rw-r--r--   3 zozo supergroup          8 2019-10-02 18:20 /f2
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -rm /f2
19/10/02 21:38:24 INFO fs.TrashPolicyDefault: Namenode trash configuration: Deletion interval = 2 minutes, Emptier interval = 0 minutes.
Moved: 'hdfs://vm017:9000/f2' to trash at: hdfs://vm017:9000/user/zozo/.Trash/Current
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /user/zozo/.Trash
drwx------   - zozo supergroup          0 2019-10-02 21:38 /user/zozo/.Trash/Current
-rw-r--r--   3 zozo supergroup          8 2019-10-02 18:20 /user/zozo/.Trash/Current/f2
```

- 执行清空回收站命令后, 回收站中的文件被移到当前目录的时间戳文件夹下 (__并没有被彻底删除__)

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -expunge
19/10/02 21:38:52 INFO fs.TrashPolicyDefault: Namenode trash configuration: Deletion interval = 2 minutes, Emptier interval = 0 minutes.
19/10/02 21:38:52 INFO fs.TrashPolicyDefault: Created trash checkpoint: /user/zozo/.Trash/191002213852
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /user/zozo/.Trash
drwx------   - zozo supergroup          0 2019-10-02 21:38 /user/zozo/.Trash/191002213852
-rw-r--r--   3 zozo supergroup          8 2019-10-02 18:20 /user/zozo/.Trash/191002213852/f2
[zozo@vm017 hadoop-2.7.2]$ 
```

- 一段时间后再查看回收站 (__此时已被彻底删除__):

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /user/zozo/.Trash
[zozo@vm017 hadoop-2.7.2]$ 
```

---

# 四 快照管理

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-HDFS_2.X%E6%96%B0%E7%89%B9%E6%80%A7/%E5%BF%AB%E7%85%A7%E7%AE%A1%E7%90%86.png?raw=true)

### 4.1 开启 / 禁用指定目录的快照功能

```bash
# 开启指定目录的快照功能
bin/hdfs dfsadmin -allowSnapshot <snapshotDir>
# 禁用指定目录的快照功能
bin/hdfs dfsadmin -disallowSnapshot <snapshotDir>
```

```bash
# 开启指定目录的快照功能
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -help allowSnapshot
-allowSnapshot <snapshotDir>:
	Allow snapshots to be taken on a directory.

[zozo@vm017 hadoop-2.7.2]$ 

# 禁用指定目录的快照功能
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -help disallowSnapshot
-disallowSnapshot <snapshotDir>:
	Do not allow snapshots to be taken on a directory any more.

[zozo@vm017 hadoop-2.7.2]$ 
```

### 4.2 创建快照

```bash
# 对目录创建快照
bin/hdfs dfs -createSnapshot <snapshotDir> [<snapshotName>]
```

```bash
# 对目录创建快照
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -help createSnapshot
-createSnapshot <snapshotDir> [<snapshotName>] :
  Create a snapshot on a directory
[zozo@vm017 hadoop-2.7.2]$ 
```

### 4.3 列出当前用户所有可快照目录并查看

```bash
# 列出当前用户所有可快照目录
bin/hdfs lsSnapshottableDir
```

```bash
# 列出当前用户所有可快照目录
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs lsSnapshottableDir -help
Usage: 
hdfs lsSnapshottableDir: 
	Get the list of snapshottable directories that are owned by the current user.
	Return all the snapshottable directories if the current user is a super user.

[zozo@vm017 hadoop-2.7.2]$ 
```

### 4.4 重命名快照

```bash
# 重命名快照
bin/hdfs dfs -renameSnapshot <snapshotDir> <oldName> <newName>
```

```bash
# 重命名快照
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -help renameSnapshot
-renameSnapshot <snapshotDir> <oldName> <newName> :
  Rename a snapshot from oldName to newName
[zozo@vm017 hadoop-2.7.2]$ 
```

### 4.5 比较两个快照的不同之处

```bash
# 比较两个快照的不同之处
bin/hdfs snapshotDiff <snapshotDir> <from> <to>
```

```bash
# 比较两个快照的不同之处
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs snapshotDiff -help
Usage: 
hdfs snapshotDiff <snapshotDir> <from> <to>:
	Get the difference between two snapshots, 
	or between a snapshot and the current tree of a directory.
	For <from>/<to>, users can use "." to present the current status,
	and use ".snapshot/snapshot_name" to present a snapshot,
	where ".snapshot/" can be omitted

[zozo@vm017 hadoop-2.7.2]$ 
```

### 4.6 恢复快照

将快照目录下的文件拷贝出来.

---
