
- [一 NameNode 和 SecondaryNameNode 工作机制](#一-namenode-和-secondarynamenode-工作机制)
- [二 Fsimage 和 Edis 解析](#二-fsimage-和-edis-解析)
    - [2.1 命令说明](#21-命令说明)
        - [2.1.1 转换 Fsimage 文件命令](#211-转换-fsimage-文件命令)
        - [2.1.2 转换 Edits 文件命令](#212-转换-edits-文件命令)
    - [2.2 操作测试 - 每小时滚动前 - 执行 HDFS 删除命令](#22-操作测试---每小时滚动前---执行-hdfs-删除命令)
    - [2.3 操作测试 - 每小时滚动前 - Fsimage 和 Edis 文件存储情况](#23-操作测试---每小时滚动前---fsimage-和-edis-文件存储情况)
    - [2.4 操作测试 - 每小时滚动前 - 查看 Fsimage 内容](#24-操作测试---每小时滚动前---查看-fsimage-内容)
    - [2.5 操作测试 - 每小时滚动前 - 查看 Edits 内容](#25-操作测试---每小时滚动前---查看-edits-内容)
    - [2.6 操作测试 - 每小时滚动后 - Fsimage 和 Edis 文件存储情况](#26-操作测试---每小时滚动后---fsimage-和-edis-文件存储情况)
    - [2.7 操作测试 - 每小时滚动后 - 查看 Fsimage 内容](#27-操作测试---每小时滚动后---查看-fsimage-内容)
    - [2.8 操作测试 - 每小时滚动后 - 查看 Edits 内容](#28-操作测试---每小时滚动后---查看-edits-内容)
- [三 CheckPoint 时间设置](#三-checkpoint-时间设置)
- [四 NameNode 故障处理](#四-namenode-故障处理)
    - [4.1 将 SecondaryNameNode 中的数据拷贝到 NameNode 中](#41-将-secondarynamenode-中的数据拷贝到-namenode-中)
        - [4.1.1 停止 NameNode 进程并删除存储数据](#411-停止-namenode-进程并删除存储数据)
        - [4.1.2 拷贝 SecondaryNameNode 中的数据到 NameNode 存储数据目录中](#412-拷贝-secondarynamenode-中的数据到-namenode-存储数据目录中)
        - [4.1.3 重新启动 NameNode 并测试](#413-重新启动-namenode-并测试)
    - [4.2 使用 -importCheckpoint 选项 (推荐) (`TODO`)](#42-使用--importcheckpoint-选项-推荐-todo)
        - [4.2.1 修改配置](#421-修改配置)
        - [4.2.2 停止 NameNode 进程并删除存储数据](#422-停止-namenode-进程并删除存储数据)
        - [4.2.3 将 SecondaryNameNode 存储目录拷贝到 NameNode 存储目录的平级目录上, 并删除 in_user.lock 文件](#423-将-secondarynamenode-存储目录拷贝到-namenode-存储目录的平级目录上-并删除-in_userlock-文件)
        - [4.2.4 执行导入检查点命令](#424-执行导入检查点命令)
        - [4.2.5 启动 NameNode](#425-启动-namenode)
- [五 集群安全模式](#五-集群安全模式)
    - [5.1 说明](#51-说明)
    - [5.2 语法](#52-语法)
    - [5.3 使用](#53-使用)
- [六 NameNode 多目录设置](#六-namenode-多目录设置)
    - [6.1 配置](#61-配置)
    - [6.2 停止集群并删除数据 (可选)](#62-停止集群并删除数据-可选)
    - [6.3 格式化集群](#63-格式化集群)
    - [6.4 启动集群](#64-启动集群)

---

# 一 NameNode 和 SecondaryNameNode 工作机制

NameNode 的元数据是如何存储的?

- 如果元数据存储在 NameNode 节点的磁盘上, 因为经常需要随机访问磁盘, 效率过低. 因此需要存储在内存中.
- 如果只存在内存中, 会有元数据丢失的风险. 因此产生磁盘备份 Fsimage.
- 如果内存中的元数据更新时, 同时更新 Fsimage, 就会导致效率过低, 如果不更新, 就会有一致性问题. 因此引入 Edits (只追加, 效率高), 每当有元数据需要更新时, 将更新操作追加到 Edits 中并更新内存. 这样就可以通过合并 Fsimage 和 Edits 还原完整的元数据.
- 如果长时间添加数据到 Edits 中会导致数据过大, 恢复时间过长. 因此需要定期合并 Fsimage 和 Edits.
- 如果定期合并 Fsimage 和 Edits 由 NameNode 节点完成, 效率过低. 因此引入 SecondaryNameNode 用于合并 Fsimage 和 Edits.

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-NameNode%E5%92%8CSecondaryNameNode/NameNode%E5%B7%A5%E4%BD%9C%E6%9C%BA%E5%88%B6.png?raw=true)

---

# 二 Fsimage 和 Edis 解析

NameNode 的目录 `{hadoop.tmp.dir}/dfs/name/current` 下存储了多个 Fsimage (镜像) 和 Edits (最近操作记录) 文件. 可以通过 `hdfs oiv` 和 `hdfs oev` 命令将 Fsimage 和 Edits 文件内容转换成指定格式的可读文件.

其中最近的一个 `{hadoop.tmp.dir}/dfs/name/current/fsimage_xxx` 文件保存了每小时滚动前的 HDFS 所有元数据, `{hadoop.tmp.dir}/dfs/name/current/edits_inprogress_xxx` 文件保存了当前小时的操作记录. 每次小时滚动时, 会将 `edits_inprogress_xxx` 文件内的操作记录合并到新建的最新的 `fsimage_xxx` 文件中, 然后将重新产生一个新的 `{hadoop.tmp.dir}/dfs/name/current/edits_inprogress_xxx` 文件继续保存下一个小时的操作记录.

## 2.1 命令说明

### 2.1.1 转换 Fsimage 文件命令

- 说明

apply the offline fsimage viewer to an fsimage.

```bash
# 使用 `hdfs oiv` 命令将 fsimage_xxx 文件内容转换成指定格式的可读文件
bin/hdfs oiv [OPTIONS] -i INPUTFILE -o OUTPUTFILE
```

```
[zozo@vm017 current]$ hdfs oiv
Usage: bin/hdfs oiv [OPTIONS] -i INPUTFILE -o OUTPUTFILE
Offline Image Viewer
View a Hadoop fsimage INPUTFILE using the specified PROCESSOR,
saving the results in OUTPUTFILE.

The oiv utility will attempt to parse correctly formed image files
and will abort fail with mal-formed image files.

The tool works offline and does not require a running cluster in
order to process an image file.

The following image processors are available:
  * XML: This processor creates an XML document with all elements of
    the fsimage enumerated, suitable for further analysis by XML
    tools.
  * FileDistribution: This processor analyzes the file size
    distribution in the image.
    -maxSize specifies the range [0, maxSize] of file sizes to be
     analyzed (128GB by default).
    -step defines the granularity of the distribution. (2MB by default)
  * Web: Run a viewer to expose read-only WebHDFS API.
    -addr specifies the address to listen. (localhost:5978 by default)
  * Delimited (experimental): Generate a text file with all of the elements common
    to both inodes and inodes-under-construction, separated by a
    delimiter. The default delimiter is \t, though this may be
    changed via the -delimiter argument.

Required command line arguments:
-i,--inputFile <arg>   FSImage file to process.

Optional command line arguments:
-o,--outputFile <arg>  Name of output file. If the specified
                       file exists, it will be overwritten.
                       (output to stdout by default)
-p,--processor <arg>   Select which type of processor to apply
                       against image file. (XML|FileDistribution|Web|Delimited)
                       (Web by default)
-delimiter <arg>       Delimiting string to use with Delimited processor.  
-t,--temp <arg>        Use temporary dir to cache intermediate result to generate
                       Delimited outputs. If not set, Delimited processor constructs
                       the namespace in memory before outputting text.
-h,--help              Display usage information and exit

[zozo@vm017 current]$ 
```

### 2.1.2 转换 Edits 文件命令

- 说明

apply the offline edits viewer to an edits file

```bash
# 使用 `hdfs oev` 命令将 edits_xxx 文件内容转换成指定格式的可读文件
bin/hdfs oev [OPTIONS] -i INPUT_FILE -o OUTPUT_FILE
```

```
[zozo@vm017 current]$ hdfs oev
Usage: bin/hdfs oev [OPTIONS] -i INPUT_FILE -o OUTPUT_FILE
Offline edits viewer
Parse a Hadoop edits log file INPUT_FILE and save results
in OUTPUT_FILE.
Required command line arguments:
-i,--inputFile <arg>   edits file to process, xml (case
                       insensitive) extension means XML format,
                       any other filename means binary format
-o,--outputFile <arg>  Name of output file. If the specified
                       file exists, it will be overwritten,
                       format of the file is determined
                       by -p option

Optional command line arguments:
-p,--processor <arg>   Select which type of processor to apply
                       against image file, currently supported
                       processors are: binary (native binary format
                       that Hadoop uses), xml (default, XML
                       format), stats (prints statistics about
                       edits file)
-h,--help              Display usage information and exit
-f,--fix-txids         Renumber the transaction IDs in the input,
                       so that there are no gaps or invalid                        transaction IDs.
-r,--recover           When reading binary edit logs, use recovery 
                       mode.  This will give you the chance to skip 
                       corrupt parts of the edit log.
-v,--verbose           More verbose output, prints the input and
                       output filenames, for processors that write
                       to a file, also output to screen. On large
                       image files this will dramatically increase
                       processing time (default is false).


Generic options supported are
-conf <configuration file>     specify an application configuration file
-D <property=value>            use value for given property
-fs <local|namenode:port>      specify a namenode
-jt <local|resourcemanager:port>    specify a ResourceManager
-files <comma separated list of files>    specify comma separated files to be copied to the map reduce cluster
-libjars <comma separated list of jars>    specify comma separated jar files to include in the classpath.
-archives <comma separated list of archives>    specify comma separated archives to be unarchived on the compute machines.

The general command line syntax is
bin/hadoop command [genericOptions] [commandOptions]

[zozo@vm017 current]$ 
```

## 2.2 操作测试 - 每小时滚动前 - 执行 HDFS 删除命令

_注: `2019-10-01 22:32:00` 前_

在 __每小时滚动前__, 在 HDFS 上执行删除操作, 将 `/d2/d2_c` 和 `/d2/d2_d` 文件夹 (包含子文件) 删除.

```bash
# `x` 表示不确认的操作记录
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /
drwxr-xr-x   - zozo supergroup          0 2019-10-01 21:50 /d2
drwxr-xr-x   - zozo supergroup          0 2019-09-28 18:47 /d2/d2_a
-rw-r--r--   3 zozo supergroup          8 2019-09-28 17:29 /d2/d2_a/f1
-rw-r--r--   2 zozo supergroup          8 2019-09-28 18:03 /d2/d2_a/f2_rename
drwxr-xr-x   - zozo supergroup          0 2019-09-xx xx:xx /d2/d2_b
-rw-r--r--   3 zozo supergroup          x 2019-09-xx xx:xx /d2/d2_b/f1
drwxr-xr-x   - zozo supergroup          0 2019-09-xx xx:xx /d2/d2_c
-rw-r--r--   3 zozo supergroup          x 2019-09-xx xx:xx /d2/d2_c/f3
-rw-r--r--   3 zozo supergroup  212046774 2019-09-25 20:04 /hadoop-2.7.2.tar.gz
-rw-r--r--   3 zozo supergroup         36 2019-09-25 20:04 /wc.input
# 执行时间: `2019-10-01 21:49:19`
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -rm -R /d2/d2_c
# 执行时间: `2019-10-01 21:50:01`
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -rm -R /d2/d2_b
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /
drwxr-xr-x   - zozo supergroup          0 2019-10-01 21:50 /d2
drwxr-xr-x   - zozo supergroup          0 2019-09-28 18:47 /d2/d2_a
-rw-r--r--   3 zozo supergroup          8 2019-09-28 17:29 /d2/d2_a/f1
-rw-r--r--   2 zozo supergroup          8 2019-09-28 18:03 /d2/d2_a/f2_rename
-rw-r--r--   3 zozo supergroup  212046774 2019-09-25 20:04 /hadoop-2.7.2.tar.gz
-rw-r--r--   3 zozo supergroup         36 2019-09-25 20:04 /wc.input
[zozo@vm017 hadoop-2.7.2]$ 
```

然后在执行操作后的 __每小时滚动前__ (`2019-10-01 22:32:00` 前) 和 __每小时滚动后__ (`2019-10-01 22:32:00` 后) 分别观察 Fsimage 和 Edis 文件变化情况, 如下:

## 2.3 操作测试 - 每小时滚动前 - Fsimage 和 Edis 文件存储情况

_注: `2019-10-01 22:32:00` 前_

- 进入 `vm017` (NameNode), 查看保存元数据路径下有多个 Fsimage 和 Edits 文件, 且还有 `seen_txid` 和 `edits_inprogress_0000000000000000660`.
- 进入 `vm06` (SecondaryNameNode), 查看保存元数据路径下有多个 Fsimage 和 Edits 文件, 且 Fsimage 和 Edits 文件数和内容和 `vm017` (NameNode) 相同, 但是没有 `seen_txid` 和 `edits_inprogress_0000000000000000660` 这两个文件.

- `vm017` (NameNode) 内容如下:

其中 `seen_txid` 文件中记录了滚动前最近的操作事物 ID 从 `660` 开始 (还未结束), `edits_inprogress_0000000000000000660` 文件内容中包含了最新 (事物 ID 从 `660` 开始) 的操作记录 (还未结束).

```
[zozo@vm017 current]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current
[zozo@vm017 current]$ ll | wc -l
178
[zozo@vm017 current]$ cat seen_txid
660
[zozo@vm017 current]$ ll
总用量 2756
-rw-rw-r-- 1 zozo zozo 1048576 9月  24 21:13 edits_0000000000000000001-0000000000000000001
-rw-rw-r-- 1 zozo zozo      42 9月  24 21:31 edits_0000000000000000002-0000000000000000003
-rw-rw-r-- 1 zozo zozo      42 9月  24 22:31 edits_0000000000000000004-0000000000000000005
-rw-rw-r-- 1 zozo zozo      42 9月  24 23:31 edits_0000000000000000006-0000000000000000007
-rw-rw-r-- 1 zozo zozo      42 9月  25 00:31 edits_0000000000000000008-0000000000000000009
-rw-rw-r-- 1 zozo zozo      42 9月  25 01:31 edits_0000000000000000010-0000000000000000011
-rw-rw-r-- 1 zozo zozo      42 9月  25 02:31 edits_0000000000000000012-0000000000000000013
-rw-rw-r-- 1 zozo zozo      42 9月  25 03:31 edits_0000000000000000014-0000000000000000015
-rw-rw-r-- 1 zozo zozo      42 9月  25 04:31 edits_0000000000000000016-0000000000000000017
-rw-rw-r-- 1 zozo zozo      42 9月  25 05:31 edits_0000000000000000018-0000000000000000019
-rw-rw-r-- 1 zozo zozo      42 9月  25 06:31 edits_0000000000000000020-0000000000000000021
-rw-rw-r-- 1 zozo zozo      42 9月  25 07:31 edits_0000000000000000022-0000000000000000023
-rw-rw-r-- 1 zozo zozo      42 9月  25 08:31 edits_0000000000000000024-0000000000000000025
-rw-rw-r-- 1 zozo zozo      42 9月  25 09:31 edits_0000000000000000026-0000000000000000027
-rw-rw-r-- 1 zozo zozo      42 9月  25 10:31 edits_0000000000000000028-0000000000000000029
-rw-rw-r-- 1 zozo zozo      42 9月  25 11:31 edits_0000000000000000030-0000000000000000031
-rw-rw-r-- 1 zozo zozo      42 9月  25 12:31 edits_0000000000000000032-0000000000000000033
-rw-rw-r-- 1 zozo zozo      42 9月  25 13:31 edits_0000000000000000034-0000000000000000035
-rw-rw-r-- 1 zozo zozo      42 9月  25 14:31 edits_0000000000000000036-0000000000000000037
-rw-rw-r-- 1 zozo zozo      42 9月  25 15:31 edits_0000000000000000038-0000000000000000039
-rw-rw-r-- 1 zozo zozo      42 9月  25 16:31 edits_0000000000000000040-0000000000000000041
-rw-rw-r-- 1 zozo zozo      42 9月  25 17:31 edits_0000000000000000042-0000000000000000043
-rw-rw-r-- 1 zozo zozo      42 9月  25 18:31 edits_0000000000000000044-0000000000000000045
-rw-rw-r-- 1 zozo zozo      42 9月  25 19:31 edits_0000000000000000046-0000000000000000047
-rw-rw-r-- 1 zozo zozo    1209 9月  25 20:31 edits_0000000000000000048-0000000000000000064
-rw-rw-r-- 1 zozo zozo      42 9月  25 21:31 edits_0000000000000000065-0000000000000000066
-rw-rw-r-- 1 zozo zozo      42 9月  25 22:31 edits_0000000000000000067-0000000000000000068
-rw-rw-r-- 1 zozo zozo      42 9月  25 23:31 edits_0000000000000000069-0000000000000000070
-rw-rw-r-- 1 zozo zozo      42 9月  26 00:31 edits_0000000000000000071-0000000000000000072
-rw-rw-r-- 1 zozo zozo      42 9月  26 01:31 edits_0000000000000000073-0000000000000000074
-rw-rw-r-- 1 zozo zozo      42 9月  26 02:31 edits_0000000000000000075-0000000000000000076
-rw-rw-r-- 1 zozo zozo      42 9月  26 03:31 edits_0000000000000000077-0000000000000000078
-rw-rw-r-- 1 zozo zozo      42 9月  26 04:31 edits_0000000000000000079-0000000000000000080
-rw-rw-r-- 1 zozo zozo      42 9月  26 05:31 edits_0000000000000000081-0000000000000000082
-rw-rw-r-- 1 zozo zozo      42 9月  26 06:31 edits_0000000000000000083-0000000000000000084
-rw-rw-r-- 1 zozo zozo      42 9月  26 07:31 edits_0000000000000000085-0000000000000000086
-rw-rw-r-- 1 zozo zozo      42 9月  26 08:31 edits_0000000000000000087-0000000000000000088
-rw-rw-r-- 1 zozo zozo      42 9月  26 09:31 edits_0000000000000000089-0000000000000000090
-rw-rw-r-- 1 zozo zozo      42 9月  26 10:31 edits_0000000000000000091-0000000000000000092
-rw-rw-r-- 1 zozo zozo      42 9月  26 11:31 edits_0000000000000000093-0000000000000000094
-rw-rw-r-- 1 zozo zozo      42 9月  26 12:31 edits_0000000000000000095-0000000000000000096
-rw-rw-r-- 1 zozo zozo      42 9月  26 13:31 edits_0000000000000000097-0000000000000000098
-rw-rw-r-- 1 zozo zozo      42 9月  26 14:31 edits_0000000000000000099-0000000000000000100
-rw-rw-r-- 1 zozo zozo      42 9月  26 15:31 edits_0000000000000000101-0000000000000000102
-rw-rw-r-- 1 zozo zozo      42 9月  26 16:31 edits_0000000000000000103-0000000000000000104
-rw-rw-r-- 1 zozo zozo      42 9月  26 17:31 edits_0000000000000000105-0000000000000000106
-rw-rw-r-- 1 zozo zozo      42 9月  26 18:31 edits_0000000000000000107-0000000000000000108
-rw-rw-r-- 1 zozo zozo      42 9月  26 19:31 edits_0000000000000000109-0000000000000000110
-rw-rw-r-- 1 zozo zozo      42 9月  26 20:31 edits_0000000000000000111-0000000000000000112
-rw-rw-r-- 1 zozo zozo      42 9月  26 21:31 edits_0000000000000000113-0000000000000000114
-rw-rw-r-- 1 zozo zozo      42 9月  26 22:31 edits_0000000000000000115-0000000000000000116
-rw-rw-r-- 1 zozo zozo      42 9月  26 23:31 edits_0000000000000000117-0000000000000000118
-rw-rw-r-- 1 zozo zozo      42 9月  27 00:31 edits_0000000000000000119-0000000000000000120
-rw-rw-r-- 1 zozo zozo      42 9月  27 01:31 edits_0000000000000000121-0000000000000000122
-rw-rw-r-- 1 zozo zozo      42 9月  27 02:31 edits_0000000000000000123-0000000000000000124
-rw-rw-r-- 1 zozo zozo      42 9月  27 03:31 edits_0000000000000000125-0000000000000000126
-rw-rw-r-- 1 zozo zozo      42 9月  27 04:31 edits_0000000000000000127-0000000000000000128
-rw-rw-r-- 1 zozo zozo      42 9月  27 05:31 edits_0000000000000000129-0000000000000000130
-rw-rw-r-- 1 zozo zozo      42 9月  27 06:31 edits_0000000000000000131-0000000000000000132
-rw-rw-r-- 1 zozo zozo      42 9月  27 07:31 edits_0000000000000000133-0000000000000000134
-rw-rw-r-- 1 zozo zozo      42 9月  27 08:31 edits_0000000000000000135-0000000000000000136
-rw-rw-r-- 1 zozo zozo      42 9月  27 09:31 edits_0000000000000000137-0000000000000000138
-rw-rw-r-- 1 zozo zozo      42 9月  27 10:31 edits_0000000000000000139-0000000000000000140
-rw-rw-r-- 1 zozo zozo      42 9月  27 11:31 edits_0000000000000000141-0000000000000000142
-rw-rw-r-- 1 zozo zozo      42 9月  27 12:32 edits_0000000000000000143-0000000000000000144
-rw-rw-r-- 1 zozo zozo      42 9月  27 13:32 edits_0000000000000000145-0000000000000000146
-rw-rw-r-- 1 zozo zozo     793 9月  27 14:32 edits_0000000000000000147-0000000000000000158
-rw-rw-r-- 1 zozo zozo      42 9月  27 15:32 edits_0000000000000000159-0000000000000000160
-rw-rw-r-- 1 zozo zozo      42 9月  27 16:32 edits_0000000000000000161-0000000000000000162
-rw-rw-r-- 1 zozo zozo      42 9月  27 17:32 edits_0000000000000000163-0000000000000000164
-rw-rw-r-- 1 zozo zozo      42 9月  27 18:32 edits_0000000000000000165-0000000000000000166
-rw-rw-r-- 1 zozo zozo      42 9月  27 19:32 edits_0000000000000000167-0000000000000000168
-rw-rw-r-- 1 zozo zozo      42 9月  27 20:32 edits_0000000000000000169-0000000000000000170
-rw-rw-r-- 1 zozo zozo      42 9月  27 21:32 edits_0000000000000000171-0000000000000000172
-rw-rw-r-- 1 zozo zozo      42 9月  27 22:32 edits_0000000000000000173-0000000000000000174
-rw-rw-r-- 1 zozo zozo      42 9月  27 23:32 edits_0000000000000000175-0000000000000000176
-rw-rw-r-- 1 zozo zozo     460 9月  28 00:32 edits_0000000000000000177-0000000000000000185
-rw-rw-r-- 1 zozo zozo     529 9月  28 01:32 edits_0000000000000000186-0000000000000000193
-rw-rw-r-- 1 zozo zozo    3566 9月  28 02:32 edits_0000000000000000194-0000000000000000241
-rw-rw-r-- 1 zozo zozo    4721 9月  28 03:32 edits_0000000000000000242-0000000000000000304
-rw-rw-r-- 1 zozo zozo      42 9月  28 04:32 edits_0000000000000000305-0000000000000000306
-rw-rw-r-- 1 zozo zozo      42 9月  28 05:32 edits_0000000000000000307-0000000000000000308
-rw-rw-r-- 1 zozo zozo      42 9月  28 06:32 edits_0000000000000000309-0000000000000000310
-rw-rw-r-- 1 zozo zozo      42 9月  28 07:32 edits_0000000000000000311-0000000000000000312
-rw-rw-r-- 1 zozo zozo      42 9月  28 08:32 edits_0000000000000000313-0000000000000000314
-rw-rw-r-- 1 zozo zozo      42 9月  28 09:32 edits_0000000000000000315-0000000000000000316
-rw-rw-r-- 1 zozo zozo      42 9月  28 10:32 edits_0000000000000000317-0000000000000000318
-rw-rw-r-- 1 zozo zozo      42 9月  28 11:32 edits_0000000000000000319-0000000000000000320
-rw-rw-r-- 1 zozo zozo      88 9月  28 12:32 edits_0000000000000000321-0000000000000000323
-rw-rw-r-- 1 zozo zozo     152 9月  28 13:32 edits_0000000000000000324-0000000000000000328
-rw-rw-r-- 1 zozo zozo      42 9月  28 14:32 edits_0000000000000000329-0000000000000000330
-rw-rw-r-- 1 zozo zozo     185 9月  28 15:32 edits_0000000000000000331-0000000000000000334
-rw-rw-r-- 1 zozo zozo      42 9月  28 16:32 edits_0000000000000000335-0000000000000000336
-rw-rw-r-- 1 zozo zozo    5830 9月  28 17:32 edits_0000000000000000337-0000000000000000455
-rw-rw-r-- 1 zozo zozo     794 9月  28 18:32 edits_0000000000000000456-0000000000000000467
-rw-rw-r-- 1 zozo zozo     864 9月  28 19:32 edits_0000000000000000468-0000000000000000480
-rw-rw-r-- 1 zozo zozo    1378 9月  28 20:32 edits_0000000000000000481-0000000000000000500
-rw-rw-r-- 1 zozo zozo     289 9月  28 21:32 edits_0000000000000000501-0000000000000000506
-rw-rw-r-- 1 zozo zozo     618 9月  28 22:32 edits_0000000000000000507-0000000000000000516
-rw-rw-r-- 1 zozo zozo      97 9月  28 23:32 edits_0000000000000000517-0000000000000000519
-rw-rw-r-- 1 zozo zozo      42 9月  29 00:32 edits_0000000000000000520-0000000000000000521
-rw-rw-r-- 1 zozo zozo      42 9月  29 01:32 edits_0000000000000000522-0000000000000000523
-rw-rw-r-- 1 zozo zozo      42 9月  29 02:32 edits_0000000000000000524-0000000000000000525
-rw-rw-r-- 1 zozo zozo      42 9月  29 03:32 edits_0000000000000000526-0000000000000000527
-rw-rw-r-- 1 zozo zozo      42 9月  29 04:32 edits_0000000000000000528-0000000000000000529
-rw-rw-r-- 1 zozo zozo      42 9月  29 05:32 edits_0000000000000000530-0000000000000000531
-rw-rw-r-- 1 zozo zozo      42 9月  29 06:32 edits_0000000000000000532-0000000000000000533
-rw-rw-r-- 1 zozo zozo      42 9月  29 07:32 edits_0000000000000000534-0000000000000000535
-rw-rw-r-- 1 zozo zozo      42 9月  29 08:32 edits_0000000000000000536-0000000000000000537
-rw-rw-r-- 1 zozo zozo      42 9月  29 09:32 edits_0000000000000000538-0000000000000000539
-rw-rw-r-- 1 zozo zozo      42 9月  29 10:32 edits_0000000000000000540-0000000000000000541
-rw-rw-r-- 1 zozo zozo      42 9月  29 11:32 edits_0000000000000000542-0000000000000000543
-rw-rw-r-- 1 zozo zozo      42 9月  29 12:32 edits_0000000000000000544-0000000000000000545
-rw-rw-r-- 1 zozo zozo      42 9月  29 13:32 edits_0000000000000000546-0000000000000000547
-rw-rw-r-- 1 zozo zozo      42 9月  29 14:32 edits_0000000000000000548-0000000000000000549
-rw-rw-r-- 1 zozo zozo      42 9月  29 15:32 edits_0000000000000000550-0000000000000000551
-rw-rw-r-- 1 zozo zozo      42 9月  29 16:32 edits_0000000000000000552-0000000000000000553
-rw-rw-r-- 1 zozo zozo      42 9月  29 17:32 edits_0000000000000000554-0000000000000000555
-rw-rw-r-- 1 zozo zozo      42 9月  29 18:32 edits_0000000000000000556-0000000000000000557
-rw-rw-r-- 1 zozo zozo      42 9月  29 19:32 edits_0000000000000000558-0000000000000000559
-rw-rw-r-- 1 zozo zozo      42 9月  29 20:32 edits_0000000000000000560-0000000000000000561
-rw-rw-r-- 1 zozo zozo      42 9月  29 21:32 edits_0000000000000000562-0000000000000000563
-rw-rw-r-- 1 zozo zozo      42 9月  29 22:32 edits_0000000000000000564-0000000000000000565
-rw-rw-r-- 1 zozo zozo      42 9月  29 23:32 edits_0000000000000000566-0000000000000000567
-rw-rw-r-- 1 zozo zozo      42 9月  30 00:32 edits_0000000000000000568-0000000000000000569
-rw-rw-r-- 1 zozo zozo      42 9月  30 01:32 edits_0000000000000000570-0000000000000000571
-rw-rw-r-- 1 zozo zozo      42 9月  30 02:32 edits_0000000000000000572-0000000000000000573
-rw-rw-r-- 1 zozo zozo      42 9月  30 03:32 edits_0000000000000000574-0000000000000000575
-rw-rw-r-- 1 zozo zozo      42 9月  30 04:32 edits_0000000000000000576-0000000000000000577
-rw-rw-r-- 1 zozo zozo      42 9月  30 05:32 edits_0000000000000000578-0000000000000000579
-rw-rw-r-- 1 zozo zozo      42 9月  30 06:32 edits_0000000000000000580-0000000000000000581
-rw-rw-r-- 1 zozo zozo      42 9月  30 07:32 edits_0000000000000000582-0000000000000000583
-rw-rw-r-- 1 zozo zozo      42 9月  30 08:32 edits_0000000000000000584-0000000000000000585
-rw-rw-r-- 1 zozo zozo      42 9月  30 09:32 edits_0000000000000000586-0000000000000000587
-rw-rw-r-- 1 zozo zozo      42 9月  30 10:32 edits_0000000000000000588-0000000000000000589
-rw-rw-r-- 1 zozo zozo      42 9月  30 11:32 edits_0000000000000000590-0000000000000000591
-rw-rw-r-- 1 zozo zozo      42 9月  30 12:32 edits_0000000000000000592-0000000000000000593
-rw-rw-r-- 1 zozo zozo      42 9月  30 13:32 edits_0000000000000000594-0000000000000000595
-rw-rw-r-- 1 zozo zozo      42 9月  30 14:32 edits_0000000000000000596-0000000000000000597
-rw-rw-r-- 1 zozo zozo      42 9月  30 15:32 edits_0000000000000000598-0000000000000000599
-rw-rw-r-- 1 zozo zozo      42 9月  30 16:32 edits_0000000000000000600-0000000000000000601
-rw-rw-r-- 1 zozo zozo      42 9月  30 17:32 edits_0000000000000000602-0000000000000000603
-rw-rw-r-- 1 zozo zozo      42 9月  30 18:32 edits_0000000000000000604-0000000000000000605
-rw-rw-r-- 1 zozo zozo      42 9月  30 19:32 edits_0000000000000000606-0000000000000000607
-rw-rw-r-- 1 zozo zozo      42 9月  30 20:32 edits_0000000000000000608-0000000000000000609
-rw-rw-r-- 1 zozo zozo      42 9月  30 21:32 edits_0000000000000000610-0000000000000000611
-rw-rw-r-- 1 zozo zozo      42 9月  30 22:32 edits_0000000000000000612-0000000000000000613
-rw-rw-r-- 1 zozo zozo      42 9月  30 23:32 edits_0000000000000000614-0000000000000000615
-rw-rw-r-- 1 zozo zozo      42 10月  1 00:32 edits_0000000000000000616-0000000000000000617
-rw-rw-r-- 1 zozo zozo      42 10月  1 01:32 edits_0000000000000000618-0000000000000000619
-rw-rw-r-- 1 zozo zozo      42 10月  1 02:32 edits_0000000000000000620-0000000000000000621
-rw-rw-r-- 1 zozo zozo      42 10月  1 03:32 edits_0000000000000000622-0000000000000000623
-rw-rw-r-- 1 zozo zozo      42 10月  1 04:32 edits_0000000000000000624-0000000000000000625
-rw-rw-r-- 1 zozo zozo      42 10月  1 05:32 edits_0000000000000000626-0000000000000000627
-rw-rw-r-- 1 zozo zozo      42 10月  1 06:32 edits_0000000000000000628-0000000000000000629
-rw-rw-r-- 1 zozo zozo      42 10月  1 07:32 edits_0000000000000000630-0000000000000000631
-rw-rw-r-- 1 zozo zozo      42 10月  1 08:32 edits_0000000000000000632-0000000000000000633
-rw-rw-r-- 1 zozo zozo      42 10月  1 09:32 edits_0000000000000000634-0000000000000000635
-rw-rw-r-- 1 zozo zozo      42 10月  1 10:32 edits_0000000000000000636-0000000000000000637
-rw-rw-r-- 1 zozo zozo      42 10月  1 11:32 edits_0000000000000000638-0000000000000000639
-rw-rw-r-- 1 zozo zozo      42 10月  1 12:32 edits_0000000000000000640-0000000000000000641
-rw-rw-r-- 1 zozo zozo      42 10月  1 13:32 edits_0000000000000000642-0000000000000000643
-rw-rw-r-- 1 zozo zozo      42 10月  1 14:32 edits_0000000000000000644-0000000000000000645
-rw-rw-r-- 1 zozo zozo      42 10月  1 15:32 edits_0000000000000000646-0000000000000000647
-rw-rw-r-- 1 zozo zozo      42 10月  1 16:32 edits_0000000000000000648-0000000000000000649
-rw-rw-r-- 1 zozo zozo      42 10月  1 17:32 edits_0000000000000000650-0000000000000000651
-rw-rw-r-- 1 zozo zozo      42 10月  1 18:32 edits_0000000000000000652-0000000000000000653
-rw-rw-r-- 1 zozo zozo      42 10月  1 19:32 edits_0000000000000000654-0000000000000000655
-rw-rw-r-- 1 zozo zozo      42 10月  1 20:32 edits_0000000000000000656-0000000000000000657
-rw-rw-r-- 1 zozo zozo      42 10月  1 21:32 edits_0000000000000000658-0000000000000000659
-rw-rw-r-- 1 zozo zozo 1048576 10月  1 21:50 edits_inprogress_0000000000000000660
-rw-rw-r-- 1 zozo zozo    1037 10月  1 20:32 fsimage_0000000000000000657
-rw-rw-r-- 1 zozo zozo      62 10月  1 20:32 fsimage_0000000000000000657.md5
-rw-rw-r-- 1 zozo zozo    1037 10月  1 21:32 fsimage_0000000000000000659
-rw-rw-r-- 1 zozo zozo      62 10月  1 21:32 fsimage_0000000000000000659.md5
-rw-rw-r-- 1 zozo zozo       4 10月  1 21:32 seen_txid
-rw-rw-r-- 1 zozo zozo     202 9月  24 21:13 VERSION
[zozo@vm017 current]$ 
```

- `vm06` (SecondaryNameNode)

```
[zozo@vm06 current]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/namesecondary/current
[zozo@vm06 current]$ ll | wc -l
176
[zozo@vm06 current]$ ll
总用量 1728
-rw-rw-r-- 1 zozo zozo 1048576 9月  24 21:31 edits_0000000000000000001-0000000000000000001
-rw-rw-r-- 1 zozo zozo      42 9月  24 21:31 edits_0000000000000000002-0000000000000000003
-rw-rw-r-- 1 zozo zozo      42 9月  24 22:31 edits_0000000000000000004-0000000000000000005
-rw-rw-r-- 1 zozo zozo      42 9月  24 23:31 edits_0000000000000000006-0000000000000000007
-rw-rw-r-- 1 zozo zozo      42 9月  25 00:31 edits_0000000000000000008-0000000000000000009
-rw-rw-r-- 1 zozo zozo      42 9月  25 01:31 edits_0000000000000000010-0000000000000000011
-rw-rw-r-- 1 zozo zozo      42 9月  25 02:31 edits_0000000000000000012-0000000000000000013
-rw-rw-r-- 1 zozo zozo      42 9月  25 03:31 edits_0000000000000000014-0000000000000000015
-rw-rw-r-- 1 zozo zozo      42 9月  25 04:31 edits_0000000000000000016-0000000000000000017
-rw-rw-r-- 1 zozo zozo      42 9月  25 05:31 edits_0000000000000000018-0000000000000000019
-rw-rw-r-- 1 zozo zozo      42 9月  25 06:31 edits_0000000000000000020-0000000000000000021
-rw-rw-r-- 1 zozo zozo      42 9月  25 07:31 edits_0000000000000000022-0000000000000000023
-rw-rw-r-- 1 zozo zozo      42 9月  25 08:31 edits_0000000000000000024-0000000000000000025
-rw-rw-r-- 1 zozo zozo      42 9月  25 09:31 edits_0000000000000000026-0000000000000000027
-rw-rw-r-- 1 zozo zozo      42 9月  25 10:31 edits_0000000000000000028-0000000000000000029
-rw-rw-r-- 1 zozo zozo      42 9月  25 11:31 edits_0000000000000000030-0000000000000000031
-rw-rw-r-- 1 zozo zozo      42 9月  25 12:31 edits_0000000000000000032-0000000000000000033
-rw-rw-r-- 1 zozo zozo      42 9月  25 13:31 edits_0000000000000000034-0000000000000000035
-rw-rw-r-- 1 zozo zozo      42 9月  25 14:31 edits_0000000000000000036-0000000000000000037
-rw-rw-r-- 1 zozo zozo      42 9月  25 15:31 edits_0000000000000000038-0000000000000000039
-rw-rw-r-- 1 zozo zozo      42 9月  25 16:31 edits_0000000000000000040-0000000000000000041
-rw-rw-r-- 1 zozo zozo      42 9月  25 17:31 edits_0000000000000000042-0000000000000000043
-rw-rw-r-- 1 zozo zozo      42 9月  25 18:31 edits_0000000000000000044-0000000000000000045
-rw-rw-r-- 1 zozo zozo      42 9月  25 19:31 edits_0000000000000000046-0000000000000000047
-rw-rw-r-- 1 zozo zozo    1209 9月  25 20:31 edits_0000000000000000048-0000000000000000064
-rw-rw-r-- 1 zozo zozo      42 9月  25 21:31 edits_0000000000000000065-0000000000000000066
-rw-rw-r-- 1 zozo zozo      42 9月  25 22:31 edits_0000000000000000067-0000000000000000068
-rw-rw-r-- 1 zozo zozo      42 9月  25 23:31 edits_0000000000000000069-0000000000000000070
-rw-rw-r-- 1 zozo zozo      42 9月  26 00:31 edits_0000000000000000071-0000000000000000072
-rw-rw-r-- 1 zozo zozo      42 9月  26 01:31 edits_0000000000000000073-0000000000000000074
-rw-rw-r-- 1 zozo zozo      42 9月  26 02:31 edits_0000000000000000075-0000000000000000076
-rw-rw-r-- 1 zozo zozo      42 9月  26 03:31 edits_0000000000000000077-0000000000000000078
-rw-rw-r-- 1 zozo zozo      42 9月  26 04:31 edits_0000000000000000079-0000000000000000080
-rw-rw-r-- 1 zozo zozo      42 9月  26 05:31 edits_0000000000000000081-0000000000000000082
-rw-rw-r-- 1 zozo zozo      42 9月  26 06:31 edits_0000000000000000083-0000000000000000084
-rw-rw-r-- 1 zozo zozo      42 9月  26 07:31 edits_0000000000000000085-0000000000000000086
-rw-rw-r-- 1 zozo zozo      42 9月  26 08:31 edits_0000000000000000087-0000000000000000088
-rw-rw-r-- 1 zozo zozo      42 9月  26 09:31 edits_0000000000000000089-0000000000000000090
-rw-rw-r-- 1 zozo zozo      42 9月  26 10:31 edits_0000000000000000091-0000000000000000092
-rw-rw-r-- 1 zozo zozo      42 9月  26 11:31 edits_0000000000000000093-0000000000000000094
-rw-rw-r-- 1 zozo zozo      42 9月  26 12:31 edits_0000000000000000095-0000000000000000096
-rw-rw-r-- 1 zozo zozo      42 9月  26 13:31 edits_0000000000000000097-0000000000000000098
-rw-rw-r-- 1 zozo zozo      42 9月  26 14:31 edits_0000000000000000099-0000000000000000100
-rw-rw-r-- 1 zozo zozo      42 9月  26 15:31 edits_0000000000000000101-0000000000000000102
-rw-rw-r-- 1 zozo zozo      42 9月  26 16:31 edits_0000000000000000103-0000000000000000104
-rw-rw-r-- 1 zozo zozo      42 9月  26 17:31 edits_0000000000000000105-0000000000000000106
-rw-rw-r-- 1 zozo zozo      42 9月  26 18:31 edits_0000000000000000107-0000000000000000108
-rw-rw-r-- 1 zozo zozo      42 9月  26 19:31 edits_0000000000000000109-0000000000000000110
-rw-rw-r-- 1 zozo zozo      42 9月  26 20:31 edits_0000000000000000111-0000000000000000112
-rw-rw-r-- 1 zozo zozo      42 9月  26 21:31 edits_0000000000000000113-0000000000000000114
-rw-rw-r-- 1 zozo zozo      42 9月  26 22:31 edits_0000000000000000115-0000000000000000116
-rw-rw-r-- 1 zozo zozo      42 9月  26 23:31 edits_0000000000000000117-0000000000000000118
-rw-rw-r-- 1 zozo zozo      42 9月  27 00:31 edits_0000000000000000119-0000000000000000120
-rw-rw-r-- 1 zozo zozo      42 9月  27 01:31 edits_0000000000000000121-0000000000000000122
-rw-rw-r-- 1 zozo zozo      42 9月  27 02:31 edits_0000000000000000123-0000000000000000124
-rw-rw-r-- 1 zozo zozo      42 9月  27 03:31 edits_0000000000000000125-0000000000000000126
-rw-rw-r-- 1 zozo zozo      42 9月  27 04:31 edits_0000000000000000127-0000000000000000128
-rw-rw-r-- 1 zozo zozo      42 9月  27 05:31 edits_0000000000000000129-0000000000000000130
-rw-rw-r-- 1 zozo zozo      42 9月  27 06:31 edits_0000000000000000131-0000000000000000132
-rw-rw-r-- 1 zozo zozo      42 9月  27 07:31 edits_0000000000000000133-0000000000000000134
-rw-rw-r-- 1 zozo zozo      42 9月  27 08:31 edits_0000000000000000135-0000000000000000136
-rw-rw-r-- 1 zozo zozo      42 9月  27 09:31 edits_0000000000000000137-0000000000000000138
-rw-rw-r-- 1 zozo zozo      42 9月  27 10:31 edits_0000000000000000139-0000000000000000140
-rw-rw-r-- 1 zozo zozo      42 9月  27 11:32 edits_0000000000000000141-0000000000000000142
-rw-rw-r-- 1 zozo zozo      42 9月  27 12:32 edits_0000000000000000143-0000000000000000144
-rw-rw-r-- 1 zozo zozo      42 9月  27 13:32 edits_0000000000000000145-0000000000000000146
-rw-rw-r-- 1 zozo zozo     793 9月  27 14:32 edits_0000000000000000147-0000000000000000158
-rw-rw-r-- 1 zozo zozo      42 9月  27 15:32 edits_0000000000000000159-0000000000000000160
-rw-rw-r-- 1 zozo zozo      42 9月  27 16:32 edits_0000000000000000161-0000000000000000162
-rw-rw-r-- 1 zozo zozo      42 9月  27 17:32 edits_0000000000000000163-0000000000000000164
-rw-rw-r-- 1 zozo zozo      42 9月  27 18:32 edits_0000000000000000165-0000000000000000166
-rw-rw-r-- 1 zozo zozo      42 9月  27 19:32 edits_0000000000000000167-0000000000000000168
-rw-rw-r-- 1 zozo zozo      42 9月  27 20:32 edits_0000000000000000169-0000000000000000170
-rw-rw-r-- 1 zozo zozo      42 9月  27 21:32 edits_0000000000000000171-0000000000000000172
-rw-rw-r-- 1 zozo zozo      42 9月  27 22:32 edits_0000000000000000173-0000000000000000174
-rw-rw-r-- 1 zozo zozo      42 9月  27 23:32 edits_0000000000000000175-0000000000000000176
-rw-rw-r-- 1 zozo zozo     460 9月  28 00:32 edits_0000000000000000177-0000000000000000185
-rw-rw-r-- 1 zozo zozo     529 9月  28 01:32 edits_0000000000000000186-0000000000000000193
-rw-rw-r-- 1 zozo zozo    3566 9月  28 02:32 edits_0000000000000000194-0000000000000000241
-rw-rw-r-- 1 zozo zozo    4721 9月  28 03:32 edits_0000000000000000242-0000000000000000304
-rw-rw-r-- 1 zozo zozo      42 9月  28 04:32 edits_0000000000000000305-0000000000000000306
-rw-rw-r-- 1 zozo zozo      42 9月  28 05:32 edits_0000000000000000307-0000000000000000308
-rw-rw-r-- 1 zozo zozo      42 9月  28 06:32 edits_0000000000000000309-0000000000000000310
-rw-rw-r-- 1 zozo zozo      42 9月  28 07:32 edits_0000000000000000311-0000000000000000312
-rw-rw-r-- 1 zozo zozo      42 9月  28 08:32 edits_0000000000000000313-0000000000000000314
-rw-rw-r-- 1 zozo zozo      42 9月  28 09:32 edits_0000000000000000315-0000000000000000316
-rw-rw-r-- 1 zozo zozo      42 9月  28 10:32 edits_0000000000000000317-0000000000000000318
-rw-rw-r-- 1 zozo zozo      42 9月  28 11:32 edits_0000000000000000319-0000000000000000320
-rw-rw-r-- 1 zozo zozo      88 9月  28 12:32 edits_0000000000000000321-0000000000000000323
-rw-rw-r-- 1 zozo zozo     152 9月  28 13:32 edits_0000000000000000324-0000000000000000328
-rw-rw-r-- 1 zozo zozo      42 9月  28 14:32 edits_0000000000000000329-0000000000000000330
-rw-rw-r-- 1 zozo zozo     185 9月  28 15:32 edits_0000000000000000331-0000000000000000334
-rw-rw-r-- 1 zozo zozo      42 9月  28 16:32 edits_0000000000000000335-0000000000000000336
-rw-rw-r-- 1 zozo zozo    5830 9月  28 17:32 edits_0000000000000000337-0000000000000000455
-rw-rw-r-- 1 zozo zozo     794 9月  28 18:32 edits_0000000000000000456-0000000000000000467
-rw-rw-r-- 1 zozo zozo     864 9月  28 19:32 edits_0000000000000000468-0000000000000000480
-rw-rw-r-- 1 zozo zozo    1378 9月  28 20:32 edits_0000000000000000481-0000000000000000500
-rw-rw-r-- 1 zozo zozo     289 9月  28 21:32 edits_0000000000000000501-0000000000000000506
-rw-rw-r-- 1 zozo zozo     618 9月  28 22:32 edits_0000000000000000507-0000000000000000516
-rw-rw-r-- 1 zozo zozo      97 9月  28 23:32 edits_0000000000000000517-0000000000000000519
-rw-rw-r-- 1 zozo zozo      42 9月  29 00:32 edits_0000000000000000520-0000000000000000521
-rw-rw-r-- 1 zozo zozo      42 9月  29 01:32 edits_0000000000000000522-0000000000000000523
-rw-rw-r-- 1 zozo zozo      42 9月  29 02:32 edits_0000000000000000524-0000000000000000525
-rw-rw-r-- 1 zozo zozo      42 9月  29 03:32 edits_0000000000000000526-0000000000000000527
-rw-rw-r-- 1 zozo zozo      42 9月  29 04:32 edits_0000000000000000528-0000000000000000529
-rw-rw-r-- 1 zozo zozo      42 9月  29 05:32 edits_0000000000000000530-0000000000000000531
-rw-rw-r-- 1 zozo zozo      42 9月  29 06:32 edits_0000000000000000532-0000000000000000533
-rw-rw-r-- 1 zozo zozo      42 9月  29 07:32 edits_0000000000000000534-0000000000000000535
-rw-rw-r-- 1 zozo zozo      42 9月  29 08:32 edits_0000000000000000536-0000000000000000537
-rw-rw-r-- 1 zozo zozo      42 9月  29 09:32 edits_0000000000000000538-0000000000000000539
-rw-rw-r-- 1 zozo zozo      42 9月  29 10:32 edits_0000000000000000540-0000000000000000541
-rw-rw-r-- 1 zozo zozo      42 9月  29 11:32 edits_0000000000000000542-0000000000000000543
-rw-rw-r-- 1 zozo zozo      42 9月  29 12:32 edits_0000000000000000544-0000000000000000545
-rw-rw-r-- 1 zozo zozo      42 9月  29 13:32 edits_0000000000000000546-0000000000000000547
-rw-rw-r-- 1 zozo zozo      42 9月  29 14:32 edits_0000000000000000548-0000000000000000549
-rw-rw-r-- 1 zozo zozo      42 9月  29 15:32 edits_0000000000000000550-0000000000000000551
-rw-rw-r-- 1 zozo zozo      42 9月  29 16:32 edits_0000000000000000552-0000000000000000553
-rw-rw-r-- 1 zozo zozo      42 9月  29 17:32 edits_0000000000000000554-0000000000000000555
-rw-rw-r-- 1 zozo zozo      42 9月  29 18:32 edits_0000000000000000556-0000000000000000557
-rw-rw-r-- 1 zozo zozo      42 9月  29 19:32 edits_0000000000000000558-0000000000000000559
-rw-rw-r-- 1 zozo zozo      42 9月  29 20:32 edits_0000000000000000560-0000000000000000561
-rw-rw-r-- 1 zozo zozo      42 9月  29 21:32 edits_0000000000000000562-0000000000000000563
-rw-rw-r-- 1 zozo zozo      42 9月  29 22:32 edits_0000000000000000564-0000000000000000565
-rw-rw-r-- 1 zozo zozo      42 9月  29 23:32 edits_0000000000000000566-0000000000000000567
-rw-rw-r-- 1 zozo zozo      42 9月  30 00:32 edits_0000000000000000568-0000000000000000569
-rw-rw-r-- 1 zozo zozo      42 9月  30 01:32 edits_0000000000000000570-0000000000000000571
-rw-rw-r-- 1 zozo zozo      42 9月  30 02:32 edits_0000000000000000572-0000000000000000573
-rw-rw-r-- 1 zozo zozo      42 9月  30 03:32 edits_0000000000000000574-0000000000000000575
-rw-rw-r-- 1 zozo zozo      42 9月  30 04:32 edits_0000000000000000576-0000000000000000577
-rw-rw-r-- 1 zozo zozo      42 9月  30 05:32 edits_0000000000000000578-0000000000000000579
-rw-rw-r-- 1 zozo zozo      42 9月  30 06:32 edits_0000000000000000580-0000000000000000581
-rw-rw-r-- 1 zozo zozo      42 9月  30 07:32 edits_0000000000000000582-0000000000000000583
-rw-rw-r-- 1 zozo zozo      42 9月  30 08:32 edits_0000000000000000584-0000000000000000585
-rw-rw-r-- 1 zozo zozo      42 9月  30 09:32 edits_0000000000000000586-0000000000000000587
-rw-rw-r-- 1 zozo zozo      42 9月  30 10:32 edits_0000000000000000588-0000000000000000589
-rw-rw-r-- 1 zozo zozo      42 9月  30 11:32 edits_0000000000000000590-0000000000000000591
-rw-rw-r-- 1 zozo zozo      42 9月  30 12:32 edits_0000000000000000592-0000000000000000593
-rw-rw-r-- 1 zozo zozo      42 9月  30 13:32 edits_0000000000000000594-0000000000000000595
-rw-rw-r-- 1 zozo zozo      42 9月  30 14:32 edits_0000000000000000596-0000000000000000597
-rw-rw-r-- 1 zozo zozo      42 9月  30 15:32 edits_0000000000000000598-0000000000000000599
-rw-rw-r-- 1 zozo zozo      42 9月  30 16:32 edits_0000000000000000600-0000000000000000601
-rw-rw-r-- 1 zozo zozo      42 9月  30 17:32 edits_0000000000000000602-0000000000000000603
-rw-rw-r-- 1 zozo zozo      42 9月  30 18:32 edits_0000000000000000604-0000000000000000605
-rw-rw-r-- 1 zozo zozo      42 9月  30 19:32 edits_0000000000000000606-0000000000000000607
-rw-rw-r-- 1 zozo zozo      42 9月  30 20:32 edits_0000000000000000608-0000000000000000609
-rw-rw-r-- 1 zozo zozo      42 9月  30 21:32 edits_0000000000000000610-0000000000000000611
-rw-rw-r-- 1 zozo zozo      42 9月  30 22:32 edits_0000000000000000612-0000000000000000613
-rw-rw-r-- 1 zozo zozo      42 9月  30 23:32 edits_0000000000000000614-0000000000000000615
-rw-rw-r-- 1 zozo zozo      42 10月  1 00:32 edits_0000000000000000616-0000000000000000617
-rw-rw-r-- 1 zozo zozo      42 10月  1 01:32 edits_0000000000000000618-0000000000000000619
-rw-rw-r-- 1 zozo zozo      42 10月  1 02:32 edits_0000000000000000620-0000000000000000621
-rw-rw-r-- 1 zozo zozo      42 10月  1 03:32 edits_0000000000000000622-0000000000000000623
-rw-rw-r-- 1 zozo zozo      42 10月  1 04:32 edits_0000000000000000624-0000000000000000625
-rw-rw-r-- 1 zozo zozo      42 10月  1 05:32 edits_0000000000000000626-0000000000000000627
-rw-rw-r-- 1 zozo zozo      42 10月  1 06:32 edits_0000000000000000628-0000000000000000629
-rw-rw-r-- 1 zozo zozo      42 10月  1 07:32 edits_0000000000000000630-0000000000000000631
-rw-rw-r-- 1 zozo zozo      42 10月  1 08:32 edits_0000000000000000632-0000000000000000633
-rw-rw-r-- 1 zozo zozo      42 10月  1 09:32 edits_0000000000000000634-0000000000000000635
-rw-rw-r-- 1 zozo zozo      42 10月  1 10:32 edits_0000000000000000636-0000000000000000637
-rw-rw-r-- 1 zozo zozo      42 10月  1 11:32 edits_0000000000000000638-0000000000000000639
-rw-rw-r-- 1 zozo zozo      42 10月  1 12:32 edits_0000000000000000640-0000000000000000641
-rw-rw-r-- 1 zozo zozo      42 10月  1 13:32 edits_0000000000000000642-0000000000000000643
-rw-rw-r-- 1 zozo zozo      42 10月  1 14:32 edits_0000000000000000644-0000000000000000645
-rw-rw-r-- 1 zozo zozo      42 10月  1 15:32 edits_0000000000000000646-0000000000000000647
-rw-rw-r-- 1 zozo zozo      42 10月  1 16:32 edits_0000000000000000648-0000000000000000649
-rw-rw-r-- 1 zozo zozo      42 10月  1 17:32 edits_0000000000000000650-0000000000000000651
-rw-rw-r-- 1 zozo zozo      42 10月  1 18:32 edits_0000000000000000652-0000000000000000653
-rw-rw-r-- 1 zozo zozo      42 10月  1 19:32 edits_0000000000000000654-0000000000000000655
-rw-rw-r-- 1 zozo zozo      42 10月  1 20:32 edits_0000000000000000656-0000000000000000657
-rw-rw-r-- 1 zozo zozo      42 10月  1 21:32 edits_0000000000000000658-0000000000000000659
-rw-rw-r-- 1 zozo zozo    1037 10月  1 20:32 fsimage_0000000000000000657
-rw-rw-r-- 1 zozo zozo      62 10月  1 20:32 fsimage_0000000000000000657.md5
-rw-rw-r-- 1 zozo zozo    1037 10月  1 21:32 fsimage_0000000000000000659
-rw-rw-r-- 1 zozo zozo      62 10月  1 21:32 fsimage_0000000000000000659.md5
-rw-rw-r-- 1 zozo zozo     202 10月  1 21:32 VERSION
[zozo@vm06 current]$ 
```

## 2.4 操作测试 - 每小时滚动前 - 查看 Fsimage 内容

_注: `2019-10-01 22:32:00` 前_

执行以下命令将 `fsimage_0000000000000000659` 文件转换成 XML 格式的 `fsimage_0000000000000000659_viewer` 可视化文件:

```bash
# [zozo@vm017 current]$ hdfs oiv -p XML -i fsimage_0000000000000000659 -o fsimage_0000000000000000659_viewer
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs oiv -p XML -i /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current/fsimage_0000000000000000659 -o /home/zozo/app/hadoop/fortest/fsimage_0000000000000000659_viewer
[zozo@vm017 hadoop-2.7.2]$ 
```

格式化之后的 `fsimage_0000000000000000659_viewer` 文件内容中包含了近乎所有节点信息 (最近 1 小时操作的除外), 如下所示:

- 此时虽然删除 `/d2/d2_c` 和 `/d2/d2_b` (包含子文件) 的操作已经执行成功, 但是在 Fsimage 文件中, 这两个文件夹 (包含子文件) 还存在 (因为保存的是 __每小时滚动前__ 的镜像).

```xml
<?xml version="1.0"?>
<fsimage>
    <NameSection>
        <genstampV1>1000</genstampV1>
        <genstampV2>1051</genstampV2>
        <genstampV1Limit>0</genstampV1Limit>
        <lastAllocatedBlockId>1073741874</lastAllocatedBlockId>
        <txid>659</txid>
    </NameSection>
    <INodeSection>
        <lastInodeId>16437</lastInodeId>
        <inode>
            <id>16385</id>
            <type>DIRECTORY</type>
            <name></name>
            <mtime>1569679770899</mtime>
            <permission>zozo:supergroup:rwxr-xr-x</permission>
            <nsquota>9223372036854775807</nsquota>
            <dsquota>-1</dsquota>
        </inode>
        <inode>
            <id>16386</id>
            <type>FILE</type>
            <name>wc.input</name>
            <replication>3</replication>
            <mtime>1569413064037</mtime>
            <atime>1569413062995</atime>
            <perferredBlockSize>134217728</perferredBlockSize>
            <permission>zozo:supergroup:rw-r--r--</permission>
            <blocks>
                <block>
                    <id>1073741825</id>
                    <genstamp>1001</genstamp>
                    <numBytes>36</numBytes>
                </block>
            </blocks>
        </inode>
        <inode>
            <id>16387</id>
            <type>FILE</type>
            <name>hadoop-2.7.2.tar.gz</name>
            <replication>3</replication>
            <mtime>1569413098417</mtime>
            <atime>1569681725982</atime>
            <perferredBlockSize>134217728</perferredBlockSize>
            <permission>zozo:supergroup:rw-r--r--</permission>
            <blocks>
                <block>
                    <id>1073741826</id>
                    <genstamp>1002</genstamp>
                    <numBytes>134217728</numBytes>
                </block>
                <block>
                    <id>1073741827</id>
                    <genstamp>1003</genstamp>
                    <numBytes>77829046</numBytes>
                </block>
            </blocks>
        </inode>
        <inode>
            <id>16413</id>
            <type>DIRECTORY</type>
            <name>d2</name>
            <mtime>1569679787986</mtime>
            <permission>zozo:supergroup:rwxr-xr-x</permission>
            <nsquota>-1</nsquota>
            <dsquota>-1</dsquota>
        </inode>
        <inode>
            <id>16414</id>
            <type>DIRECTORY</type>
            <name>d2_a</name>
            <mtime>1569667639914</mtime>
            <permission>zozo:supergroup:rwxr-xr-x</permission>
            <nsquota>-1</nsquota>
            <dsquota>-1</dsquota>
        </inode>
        <inode>
            <id>16425</id>
            <type>FILE</type>
            <name>f1</name>
            <replication>3</replication>
            <mtime>1569662954325</mtime>
            <atime>1569673192617</atime>
            <perferredBlockSize>134217728</perferredBlockSize>
            <permission>zozo:supergroup:rw-r--r--</permission>
            <blocks>
                <block>
                    <id>1073741868</id>
                    <genstamp>1045</genstamp>
                    <numBytes>8</numBytes>
                </block>
            </blocks>
        </inode>
        <inode>
            <id>16426</id>
            <type>FILE</type>
            <name>f2_rename</name>
            <replication>2</replication>
            <mtime>1569664981957</mtime>
            <atime>1569673226015</atime>
            <perferredBlockSize>134217728</perferredBlockSize>
            <permission>zozo:supergroup:rw-r--r--</permission>
            <blocks>
                <block>
                    <id>1073741869</id>
                    <genstamp>1046</genstamp>
                    <numBytes>8</numBytes>
                </block>
            </blocks>
        </inode>
        <inode>
            <id>16428</id>
            <type>DIRECTORY</type>
            <name>d2_b</name>
            <mtime>1569667753520</mtime>
            <permission>zozo:supergroup:rwxr-xr-x</permission>
            <nsquota>-1</nsquota>
            <dsquota>-1</dsquota>
        </inode>
        <inode>
            <id>16429</id>
            <type>FILE</type>
            <name>f1</name>
            <replication>3</replication>
            <mtime>1569667753509</mtime>
            <atime>1569667753260</atime>
            <perferredBlockSize>134217728</perferredBlockSize>
            <permission>zozo:supergroup:rw-r--r--</permission>
            <blocks>
                <block>
                    <id>1073741871</id>
                    <genstamp>1048</genstamp>
                    <numBytes>8</numBytes>
                </block>
            </blocks>
        </inode>
        <inode>
            <id>16436</id>
            <type>DIRECTORY</type>
            <name>d2_c</name>
            <mtime>1569680035361</mtime>
            <permission>zozo:supergroup:rwxr-xr-x</permission>
            <nsquota>-1</nsquota>
            <dsquota>-1</dsquota>
        </inode>
        <inode>
            <id>16437</id>
            <type>FILE</type>
            <name>f3</name>
            <replication>3</replication>
            <mtime>1569680035670</mtime>
            <atime>1569680035361</atime>
            <perferredBlockSize>134217728</perferredBlockSize>
            <permission>zozo:supergroup:rw-r--r--</permission>
            <blocks>
                <block>
                    <id>1073741874</id>
                    <genstamp>1051</genstamp>
                    <numBytes>8</numBytes>
                </block>
            </blocks>
        </inode>
    </INodeSection>
    <INodeReferenceSection></INodeReferenceSection>
    <SnapshotSection>
        <snapshotCounter>0</snapshotCounter>
    </SnapshotSection>
    <INodeDirectorySection>
        <directory>
            <parent>16385</parent>
            <inode>16413</inode>
            <inode>16387</inode>
            <inode>16386</inode>
        </directory>
        <directory>
            <parent>16413</parent>
            <inode>16414</inode>
            <inode>16428</inode>
            <inode>16436</inode>
        </directory>
        <directory>
            <parent>16414</parent>
            <inode>16425</inode>
            <inode>16426</inode>
        </directory>
        <directory>
            <parent>16428</parent>
            <inode>16429</inode>
        </directory>
        <directory>
            <parent>16436</parent>
            <inode>16437</inode>
        </directory>
    </INodeDirectorySection>
    <FileUnderConstructionSection></FileUnderConstructionSection>
    <SnapshotDiffSection>
        <diff>
            <inodeid>16385</inodeid>
        </diff>
    </SnapshotDiffSection>
    <SecretManagerSection>
        <currentId>0</currentId>
        <tokenSequenceNumber>0</tokenSequenceNumber>
    </SecretManagerSection>
    <CacheManagerSection>
        <nextDirectiveId>1</nextDirectiveId>
    </CacheManagerSection>
</fsimage>
```

## 2.5 操作测试 - 每小时滚动前 - 查看 Edits 内容

_注: `2019-10-01 22:32:00` 前_

- step 1

执行以下命令将 `edits_0000000000000000658-0000000000000000659` 文件转换成 XML 格式的 `edits_0000000000000000658-0000000000000000659_viewer` 可视化文件:

```bash
# [zozo@vm017 current]$ hdfs oev -p XML -i edits_0000000000000000658-0000000000000000659 -o edits_0000000000000000658-0000000000000000659_viewer
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs oev -p XML -i /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current/edits_0000000000000000658-0000000000000000659 -o /home/zozo/app/hadoop/fortest/edits_0000000000000000658-0000000000000000659_viewer
[zozo@vm017 hadoop-2.7.2]$ 
```

格式化之后的 `edits_0000000000000000658-0000000000000000659_viewer` 文件内容中包含了事物 ID `658 - 659` 阶段的操作记录, 如下所示:

- 事物 ID `658`: OP_START_LOG_SEGMENT (开始日志段)
- 事物 ID `659`: OP_END_LOG_SEGMENT (结束日志段)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<EDITS>
  <EDITS_VERSION>-63</EDITS_VERSION>
  <RECORD>
    <OPCODE>OP_START_LOG_SEGMENT</OPCODE>
    <DATA>
      <TXID>658</TXID>
    </DATA>
  </RECORD>
  <RECORD>
    <OPCODE>OP_END_LOG_SEGMENT</OPCODE>
    <DATA>
      <TXID>659</TXID>
    </DATA>
  </RECORD>
</EDITS>
```

- step 2

执行以下命令将 `edits_inprogress_0000000000000000660` 文件转换成 XML 格式的 `edits_inprogress_0000000000000000660_viewer` 可视化文件:

```bash
[zozo@vm017 current]$ du -h /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current/edits_inprogress_0000000000000000660
1.0M	/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current/edits_inprogress_0000000000000000660
# [zozo@vm017 current]$ hdfs oev -p XML -i edits_inprogress_0000000000000000660 -o edits_inprogress_0000000000000000660_viewer
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs oev -p XML -i /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current/edits_inprogress_0000000000000000660 -o /home/zozo/app/hadoop/fortest/edits_inprogress_0000000000000000660_viewer
[zozo@vm017 fortest]$ du -h /home/zozo/app/hadoop/fortest/edits_inprogress_0000000000000000660_viewer
4.0K	/home/zozo/app/hadoop/fortest/edits_inprogress_0000000000000000660_viewer
[zozo@vm017 fortest]$ 
```

格式化之后的 `edits_inprogress_0000000000000000660_viewer` 文件内容中包含了最新 (事物 ID 从 `660` 开始) 的操作记录 (还未结束), 如下所示:

- 事物 ID `660`: OP_START_LOG_SEGMENT (开始日志段)
- 事物 ID `661`: OP_DELETE (删除  `/d2/d2_c` 文件夹)
- 事物 ID `662`: OP_DELETE (删除  `/d2/d2_b` 文件夹)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<EDITS>
  <EDITS_VERSION>-63</EDITS_VERSION>
  <RECORD>
    <OPCODE>OP_START_LOG_SEGMENT</OPCODE>
    <DATA>
      <TXID>660</TXID>
    </DATA>
  </RECORD>
  <RECORD>
    <OPCODE>OP_DELETE</OPCODE>
    <DATA>
      <TXID>661</TXID>
      <LENGTH>0</LENGTH>
      <PATH>/d2/d2_c</PATH>
      <TIMESTAMP>1569937760907</TIMESTAMP>
      <RPC_CLIENTID>4d8c83b2-92fe-424d-8215-9ed8cf5d7652</RPC_CLIENTID>
      <RPC_CALLID>3</RPC_CALLID>
    </DATA>
  </RECORD>
  <RECORD>
    <OPCODE>OP_DELETE</OPCODE>
    <DATA>
      <TXID>662</TXID>
      <LENGTH>0</LENGTH>
      <PATH>/d2/d2_b</PATH>
      <TIMESTAMP>1569937803194</TIMESTAMP>
      <RPC_CLIENTID>59f74bb6-045c-4269-94d4-6ab8a2ab80a5</RPC_CLIENTID>
      <RPC_CALLID>3</RPC_CALLID>
    </DATA>
  </RECORD>
</EDITS>
```

## 2.6 操作测试 - 每小时滚动后 - Fsimage 和 Edis 文件存储情况

_注: `2019-10-01 22:32:00` 后_

- 进入 `vm017` (NameNode), 查看保存元数据路径下有多个 Fsimage 和 Edits 文件, 且还有 `seen_txid` 和 `edits_inprogress_0000000000000000664`.
- 进入 `vm06` (SecondaryNameNode), 查看保存元数据路径下有多个 Fsimage 和 Edits 文件, 且 Fsimage 和 Edits 文件数和内容和 `vm017` (NameNode) 相同, 但是没有 `seen_txid` 和 `edits_inprogress_0000000000000000664` 这两个文件.

- `vm017` (NameNode) 内容如下:

其中 `seen_txid` 文件中记录了滚动前最近的操作事物 ID 从 `664` 开始 (还未结束), `edits_inprogress_0000000000000000664` 文件内容中包含了最新 (事物 ID 从 `664` 开始) 的操作记录 (还未结束).

```
[zozo@vm017 current]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current
[zozo@vm017 current]$ ll | wc -l
179
[zozo@vm017 current]$ cat seen_txid 
664
[zozo@vm017 current]$ ll
总用量 2760
-rw-rw-r-- 1 zozo zozo 1048576 9月  24 21:13 edits_0000000000000000001-0000000000000000001
-rw-rw-r-- 1 zozo zozo      42 9月  24 21:31 edits_0000000000000000002-0000000000000000003
-rw-rw-r-- 1 zozo zozo      42 9月  24 22:31 edits_0000000000000000004-0000000000000000005
-rw-rw-r-- 1 zozo zozo      42 9月  24 23:31 edits_0000000000000000006-0000000000000000007
-rw-rw-r-- 1 zozo zozo      42 9月  25 00:31 edits_0000000000000000008-0000000000000000009
-rw-rw-r-- 1 zozo zozo      42 9月  25 01:31 edits_0000000000000000010-0000000000000000011
-rw-rw-r-- 1 zozo zozo      42 9月  25 02:31 edits_0000000000000000012-0000000000000000013
-rw-rw-r-- 1 zozo zozo      42 9月  25 03:31 edits_0000000000000000014-0000000000000000015
-rw-rw-r-- 1 zozo zozo      42 9月  25 04:31 edits_0000000000000000016-0000000000000000017
-rw-rw-r-- 1 zozo zozo      42 9月  25 05:31 edits_0000000000000000018-0000000000000000019
-rw-rw-r-- 1 zozo zozo      42 9月  25 06:31 edits_0000000000000000020-0000000000000000021
-rw-rw-r-- 1 zozo zozo      42 9月  25 07:31 edits_0000000000000000022-0000000000000000023
-rw-rw-r-- 1 zozo zozo      42 9月  25 08:31 edits_0000000000000000024-0000000000000000025
-rw-rw-r-- 1 zozo zozo      42 9月  25 09:31 edits_0000000000000000026-0000000000000000027
-rw-rw-r-- 1 zozo zozo      42 9月  25 10:31 edits_0000000000000000028-0000000000000000029
-rw-rw-r-- 1 zozo zozo      42 9月  25 11:31 edits_0000000000000000030-0000000000000000031
-rw-rw-r-- 1 zozo zozo      42 9月  25 12:31 edits_0000000000000000032-0000000000000000033
-rw-rw-r-- 1 zozo zozo      42 9月  25 13:31 edits_0000000000000000034-0000000000000000035
-rw-rw-r-- 1 zozo zozo      42 9月  25 14:31 edits_0000000000000000036-0000000000000000037
-rw-rw-r-- 1 zozo zozo      42 9月  25 15:31 edits_0000000000000000038-0000000000000000039
-rw-rw-r-- 1 zozo zozo      42 9月  25 16:31 edits_0000000000000000040-0000000000000000041
-rw-rw-r-- 1 zozo zozo      42 9月  25 17:31 edits_0000000000000000042-0000000000000000043
-rw-rw-r-- 1 zozo zozo      42 9月  25 18:31 edits_0000000000000000044-0000000000000000045
-rw-rw-r-- 1 zozo zozo      42 9月  25 19:31 edits_0000000000000000046-0000000000000000047
-rw-rw-r-- 1 zozo zozo    1209 9月  25 20:31 edits_0000000000000000048-0000000000000000064
-rw-rw-r-- 1 zozo zozo      42 9月  25 21:31 edits_0000000000000000065-0000000000000000066
-rw-rw-r-- 1 zozo zozo      42 9月  25 22:31 edits_0000000000000000067-0000000000000000068
-rw-rw-r-- 1 zozo zozo      42 9月  25 23:31 edits_0000000000000000069-0000000000000000070
-rw-rw-r-- 1 zozo zozo      42 9月  26 00:31 edits_0000000000000000071-0000000000000000072
-rw-rw-r-- 1 zozo zozo      42 9月  26 01:31 edits_0000000000000000073-0000000000000000074
-rw-rw-r-- 1 zozo zozo      42 9月  26 02:31 edits_0000000000000000075-0000000000000000076
-rw-rw-r-- 1 zozo zozo      42 9月  26 03:31 edits_0000000000000000077-0000000000000000078
-rw-rw-r-- 1 zozo zozo      42 9月  26 04:31 edits_0000000000000000079-0000000000000000080
-rw-rw-r-- 1 zozo zozo      42 9月  26 05:31 edits_0000000000000000081-0000000000000000082
-rw-rw-r-- 1 zozo zozo      42 9月  26 06:31 edits_0000000000000000083-0000000000000000084
-rw-rw-r-- 1 zozo zozo      42 9月  26 07:31 edits_0000000000000000085-0000000000000000086
-rw-rw-r-- 1 zozo zozo      42 9月  26 08:31 edits_0000000000000000087-0000000000000000088
-rw-rw-r-- 1 zozo zozo      42 9月  26 09:31 edits_0000000000000000089-0000000000000000090
-rw-rw-r-- 1 zozo zozo      42 9月  26 10:31 edits_0000000000000000091-0000000000000000092
-rw-rw-r-- 1 zozo zozo      42 9月  26 11:31 edits_0000000000000000093-0000000000000000094
-rw-rw-r-- 1 zozo zozo      42 9月  26 12:31 edits_0000000000000000095-0000000000000000096
-rw-rw-r-- 1 zozo zozo      42 9月  26 13:31 edits_0000000000000000097-0000000000000000098
-rw-rw-r-- 1 zozo zozo      42 9月  26 14:31 edits_0000000000000000099-0000000000000000100
-rw-rw-r-- 1 zozo zozo      42 9月  26 15:31 edits_0000000000000000101-0000000000000000102
-rw-rw-r-- 1 zozo zozo      42 9月  26 16:31 edits_0000000000000000103-0000000000000000104
-rw-rw-r-- 1 zozo zozo      42 9月  26 17:31 edits_0000000000000000105-0000000000000000106
-rw-rw-r-- 1 zozo zozo      42 9月  26 18:31 edits_0000000000000000107-0000000000000000108
-rw-rw-r-- 1 zozo zozo      42 9月  26 19:31 edits_0000000000000000109-0000000000000000110
-rw-rw-r-- 1 zozo zozo      42 9月  26 20:31 edits_0000000000000000111-0000000000000000112
-rw-rw-r-- 1 zozo zozo      42 9月  26 21:31 edits_0000000000000000113-0000000000000000114
-rw-rw-r-- 1 zozo zozo      42 9月  26 22:31 edits_0000000000000000115-0000000000000000116
-rw-rw-r-- 1 zozo zozo      42 9月  26 23:31 edits_0000000000000000117-0000000000000000118
-rw-rw-r-- 1 zozo zozo      42 9月  27 00:31 edits_0000000000000000119-0000000000000000120
-rw-rw-r-- 1 zozo zozo      42 9月  27 01:31 edits_0000000000000000121-0000000000000000122
-rw-rw-r-- 1 zozo zozo      42 9月  27 02:31 edits_0000000000000000123-0000000000000000124
-rw-rw-r-- 1 zozo zozo      42 9月  27 03:31 edits_0000000000000000125-0000000000000000126
-rw-rw-r-- 1 zozo zozo      42 9月  27 04:31 edits_0000000000000000127-0000000000000000128
-rw-rw-r-- 1 zozo zozo      42 9月  27 05:31 edits_0000000000000000129-0000000000000000130
-rw-rw-r-- 1 zozo zozo      42 9月  27 06:31 edits_0000000000000000131-0000000000000000132
-rw-rw-r-- 1 zozo zozo      42 9月  27 07:31 edits_0000000000000000133-0000000000000000134
-rw-rw-r-- 1 zozo zozo      42 9月  27 08:31 edits_0000000000000000135-0000000000000000136
-rw-rw-r-- 1 zozo zozo      42 9月  27 09:31 edits_0000000000000000137-0000000000000000138
-rw-rw-r-- 1 zozo zozo      42 9月  27 10:31 edits_0000000000000000139-0000000000000000140
-rw-rw-r-- 1 zozo zozo      42 9月  27 11:31 edits_0000000000000000141-0000000000000000142
-rw-rw-r-- 1 zozo zozo      42 9月  27 12:32 edits_0000000000000000143-0000000000000000144
-rw-rw-r-- 1 zozo zozo      42 9月  27 13:32 edits_0000000000000000145-0000000000000000146
-rw-rw-r-- 1 zozo zozo     793 9月  27 14:32 edits_0000000000000000147-0000000000000000158
-rw-rw-r-- 1 zozo zozo      42 9月  27 15:32 edits_0000000000000000159-0000000000000000160
-rw-rw-r-- 1 zozo zozo      42 9月  27 16:32 edits_0000000000000000161-0000000000000000162
-rw-rw-r-- 1 zozo zozo      42 9月  27 17:32 edits_0000000000000000163-0000000000000000164
-rw-rw-r-- 1 zozo zozo      42 9月  27 18:32 edits_0000000000000000165-0000000000000000166
-rw-rw-r-- 1 zozo zozo      42 9月  27 19:32 edits_0000000000000000167-0000000000000000168
-rw-rw-r-- 1 zozo zozo      42 9月  27 20:32 edits_0000000000000000169-0000000000000000170
-rw-rw-r-- 1 zozo zozo      42 9月  27 21:32 edits_0000000000000000171-0000000000000000172
-rw-rw-r-- 1 zozo zozo      42 9月  27 22:32 edits_0000000000000000173-0000000000000000174
-rw-rw-r-- 1 zozo zozo      42 9月  27 23:32 edits_0000000000000000175-0000000000000000176
-rw-rw-r-- 1 zozo zozo     460 9月  28 00:32 edits_0000000000000000177-0000000000000000185
-rw-rw-r-- 1 zozo zozo     529 9月  28 01:32 edits_0000000000000000186-0000000000000000193
-rw-rw-r-- 1 zozo zozo    3566 9月  28 02:32 edits_0000000000000000194-0000000000000000241
-rw-rw-r-- 1 zozo zozo    4721 9月  28 03:32 edits_0000000000000000242-0000000000000000304
-rw-rw-r-- 1 zozo zozo      42 9月  28 04:32 edits_0000000000000000305-0000000000000000306
-rw-rw-r-- 1 zozo zozo      42 9月  28 05:32 edits_0000000000000000307-0000000000000000308
-rw-rw-r-- 1 zozo zozo      42 9月  28 06:32 edits_0000000000000000309-0000000000000000310
-rw-rw-r-- 1 zozo zozo      42 9月  28 07:32 edits_0000000000000000311-0000000000000000312
-rw-rw-r-- 1 zozo zozo      42 9月  28 08:32 edits_0000000000000000313-0000000000000000314
-rw-rw-r-- 1 zozo zozo      42 9月  28 09:32 edits_0000000000000000315-0000000000000000316
-rw-rw-r-- 1 zozo zozo      42 9月  28 10:32 edits_0000000000000000317-0000000000000000318
-rw-rw-r-- 1 zozo zozo      42 9月  28 11:32 edits_0000000000000000319-0000000000000000320
-rw-rw-r-- 1 zozo zozo      88 9月  28 12:32 edits_0000000000000000321-0000000000000000323
-rw-rw-r-- 1 zozo zozo     152 9月  28 13:32 edits_0000000000000000324-0000000000000000328
-rw-rw-r-- 1 zozo zozo      42 9月  28 14:32 edits_0000000000000000329-0000000000000000330
-rw-rw-r-- 1 zozo zozo     185 9月  28 15:32 edits_0000000000000000331-0000000000000000334
-rw-rw-r-- 1 zozo zozo      42 9月  28 16:32 edits_0000000000000000335-0000000000000000336
-rw-rw-r-- 1 zozo zozo    5830 9月  28 17:32 edits_0000000000000000337-0000000000000000455
-rw-rw-r-- 1 zozo zozo     794 9月  28 18:32 edits_0000000000000000456-0000000000000000467
-rw-rw-r-- 1 zozo zozo     864 9月  28 19:32 edits_0000000000000000468-0000000000000000480
-rw-rw-r-- 1 zozo zozo    1378 9月  28 20:32 edits_0000000000000000481-0000000000000000500
-rw-rw-r-- 1 zozo zozo     289 9月  28 21:32 edits_0000000000000000501-0000000000000000506
-rw-rw-r-- 1 zozo zozo     618 9月  28 22:32 edits_0000000000000000507-0000000000000000516
-rw-rw-r-- 1 zozo zozo      97 9月  28 23:32 edits_0000000000000000517-0000000000000000519
-rw-rw-r-- 1 zozo zozo      42 9月  29 00:32 edits_0000000000000000520-0000000000000000521
-rw-rw-r-- 1 zozo zozo      42 9月  29 01:32 edits_0000000000000000522-0000000000000000523
-rw-rw-r-- 1 zozo zozo      42 9月  29 02:32 edits_0000000000000000524-0000000000000000525
-rw-rw-r-- 1 zozo zozo      42 9月  29 03:32 edits_0000000000000000526-0000000000000000527
-rw-rw-r-- 1 zozo zozo      42 9月  29 04:32 edits_0000000000000000528-0000000000000000529
-rw-rw-r-- 1 zozo zozo      42 9月  29 05:32 edits_0000000000000000530-0000000000000000531
-rw-rw-r-- 1 zozo zozo      42 9月  29 06:32 edits_0000000000000000532-0000000000000000533
-rw-rw-r-- 1 zozo zozo      42 9月  29 07:32 edits_0000000000000000534-0000000000000000535
-rw-rw-r-- 1 zozo zozo      42 9月  29 08:32 edits_0000000000000000536-0000000000000000537
-rw-rw-r-- 1 zozo zozo      42 9月  29 09:32 edits_0000000000000000538-0000000000000000539
-rw-rw-r-- 1 zozo zozo      42 9月  29 10:32 edits_0000000000000000540-0000000000000000541
-rw-rw-r-- 1 zozo zozo      42 9月  29 11:32 edits_0000000000000000542-0000000000000000543
-rw-rw-r-- 1 zozo zozo      42 9月  29 12:32 edits_0000000000000000544-0000000000000000545
-rw-rw-r-- 1 zozo zozo      42 9月  29 13:32 edits_0000000000000000546-0000000000000000547
-rw-rw-r-- 1 zozo zozo      42 9月  29 14:32 edits_0000000000000000548-0000000000000000549
-rw-rw-r-- 1 zozo zozo      42 9月  29 15:32 edits_0000000000000000550-0000000000000000551
-rw-rw-r-- 1 zozo zozo      42 9月  29 16:32 edits_0000000000000000552-0000000000000000553
-rw-rw-r-- 1 zozo zozo      42 9月  29 17:32 edits_0000000000000000554-0000000000000000555
-rw-rw-r-- 1 zozo zozo      42 9月  29 18:32 edits_0000000000000000556-0000000000000000557
-rw-rw-r-- 1 zozo zozo      42 9月  29 19:32 edits_0000000000000000558-0000000000000000559
-rw-rw-r-- 1 zozo zozo      42 9月  29 20:32 edits_0000000000000000560-0000000000000000561
-rw-rw-r-- 1 zozo zozo      42 9月  29 21:32 edits_0000000000000000562-0000000000000000563
-rw-rw-r-- 1 zozo zozo      42 9月  29 22:32 edits_0000000000000000564-0000000000000000565
-rw-rw-r-- 1 zozo zozo      42 9月  29 23:32 edits_0000000000000000566-0000000000000000567
-rw-rw-r-- 1 zozo zozo      42 9月  30 00:32 edits_0000000000000000568-0000000000000000569
-rw-rw-r-- 1 zozo zozo      42 9月  30 01:32 edits_0000000000000000570-0000000000000000571
-rw-rw-r-- 1 zozo zozo      42 9月  30 02:32 edits_0000000000000000572-0000000000000000573
-rw-rw-r-- 1 zozo zozo      42 9月  30 03:32 edits_0000000000000000574-0000000000000000575
-rw-rw-r-- 1 zozo zozo      42 9月  30 04:32 edits_0000000000000000576-0000000000000000577
-rw-rw-r-- 1 zozo zozo      42 9月  30 05:32 edits_0000000000000000578-0000000000000000579
-rw-rw-r-- 1 zozo zozo      42 9月  30 06:32 edits_0000000000000000580-0000000000000000581
-rw-rw-r-- 1 zozo zozo      42 9月  30 07:32 edits_0000000000000000582-0000000000000000583
-rw-rw-r-- 1 zozo zozo      42 9月  30 08:32 edits_0000000000000000584-0000000000000000585
-rw-rw-r-- 1 zozo zozo      42 9月  30 09:32 edits_0000000000000000586-0000000000000000587
-rw-rw-r-- 1 zozo zozo      42 9月  30 10:32 edits_0000000000000000588-0000000000000000589
-rw-rw-r-- 1 zozo zozo      42 9月  30 11:32 edits_0000000000000000590-0000000000000000591
-rw-rw-r-- 1 zozo zozo      42 9月  30 12:32 edits_0000000000000000592-0000000000000000593
-rw-rw-r-- 1 zozo zozo      42 9月  30 13:32 edits_0000000000000000594-0000000000000000595
-rw-rw-r-- 1 zozo zozo      42 9月  30 14:32 edits_0000000000000000596-0000000000000000597
-rw-rw-r-- 1 zozo zozo      42 9月  30 15:32 edits_0000000000000000598-0000000000000000599
-rw-rw-r-- 1 zozo zozo      42 9月  30 16:32 edits_0000000000000000600-0000000000000000601
-rw-rw-r-- 1 zozo zozo      42 9月  30 17:32 edits_0000000000000000602-0000000000000000603
-rw-rw-r-- 1 zozo zozo      42 9月  30 18:32 edits_0000000000000000604-0000000000000000605
-rw-rw-r-- 1 zozo zozo      42 9月  30 19:32 edits_0000000000000000606-0000000000000000607
-rw-rw-r-- 1 zozo zozo      42 9月  30 20:32 edits_0000000000000000608-0000000000000000609
-rw-rw-r-- 1 zozo zozo      42 9月  30 21:32 edits_0000000000000000610-0000000000000000611
-rw-rw-r-- 1 zozo zozo      42 9月  30 22:32 edits_0000000000000000612-0000000000000000613
-rw-rw-r-- 1 zozo zozo      42 9月  30 23:32 edits_0000000000000000614-0000000000000000615
-rw-rw-r-- 1 zozo zozo      42 10月  1 00:32 edits_0000000000000000616-0000000000000000617
-rw-rw-r-- 1 zozo zozo      42 10月  1 01:32 edits_0000000000000000618-0000000000000000619
-rw-rw-r-- 1 zozo zozo      42 10月  1 02:32 edits_0000000000000000620-0000000000000000621
-rw-rw-r-- 1 zozo zozo      42 10月  1 03:32 edits_0000000000000000622-0000000000000000623
-rw-rw-r-- 1 zozo zozo      42 10月  1 04:32 edits_0000000000000000624-0000000000000000625
-rw-rw-r-- 1 zozo zozo      42 10月  1 05:32 edits_0000000000000000626-0000000000000000627
-rw-rw-r-- 1 zozo zozo      42 10月  1 06:32 edits_0000000000000000628-0000000000000000629
-rw-rw-r-- 1 zozo zozo      42 10月  1 07:32 edits_0000000000000000630-0000000000000000631
-rw-rw-r-- 1 zozo zozo      42 10月  1 08:32 edits_0000000000000000632-0000000000000000633
-rw-rw-r-- 1 zozo zozo      42 10月  1 09:32 edits_0000000000000000634-0000000000000000635
-rw-rw-r-- 1 zozo zozo      42 10月  1 10:32 edits_0000000000000000636-0000000000000000637
-rw-rw-r-- 1 zozo zozo      42 10月  1 11:32 edits_0000000000000000638-0000000000000000639
-rw-rw-r-- 1 zozo zozo      42 10月  1 12:32 edits_0000000000000000640-0000000000000000641
-rw-rw-r-- 1 zozo zozo      42 10月  1 13:32 edits_0000000000000000642-0000000000000000643
-rw-rw-r-- 1 zozo zozo      42 10月  1 14:32 edits_0000000000000000644-0000000000000000645
-rw-rw-r-- 1 zozo zozo      42 10月  1 15:32 edits_0000000000000000646-0000000000000000647
-rw-rw-r-- 1 zozo zozo      42 10月  1 16:32 edits_0000000000000000648-0000000000000000649
-rw-rw-r-- 1 zozo zozo      42 10月  1 17:32 edits_0000000000000000650-0000000000000000651
-rw-rw-r-- 1 zozo zozo      42 10月  1 18:32 edits_0000000000000000652-0000000000000000653
-rw-rw-r-- 1 zozo zozo      42 10月  1 19:32 edits_0000000000000000654-0000000000000000655
-rw-rw-r-- 1 zozo zozo      42 10月  1 20:32 edits_0000000000000000656-0000000000000000657
-rw-rw-r-- 1 zozo zozo      42 10月  1 21:32 edits_0000000000000000658-0000000000000000659
-rw-rw-r-- 1 zozo zozo     156 10月  1 22:32 edits_0000000000000000660-0000000000000000663
-rw-rw-r-- 1 zozo zozo 1048576 10月  1 22:32 edits_inprogress_0000000000000000664
-rw-rw-r-- 1 zozo zozo    1037 10月  1 21:32 fsimage_0000000000000000659
-rw-rw-r-- 1 zozo zozo      62 10月  1 21:32 fsimage_0000000000000000659.md5
-rw-rw-r-- 1 zozo zozo     789 10月  1 22:32 fsimage_0000000000000000663
-rw-rw-r-- 1 zozo zozo      62 10月  1 22:32 fsimage_0000000000000000663.md5
-rw-rw-r-- 1 zozo zozo       4 10月  1 22:32 seen_txid
-rw-rw-r-- 1 zozo zozo     202 9月  24 21:13 VERSION
[zozo@vm017 current]$ 
```

- `vm06` (SecondaryNameNode)

```
[zozo@vm06 current]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/namesecondary/current
[zozo@vm06 current]$ ll | wc -l
177
[zozo@vm06 current]$ ll
总用量 1732
-rw-rw-r-- 1 zozo zozo 1048576 9月  24 21:31 edits_0000000000000000001-0000000000000000001
-rw-rw-r-- 1 zozo zozo      42 9月  24 21:31 edits_0000000000000000002-0000000000000000003
-rw-rw-r-- 1 zozo zozo      42 9月  24 22:31 edits_0000000000000000004-0000000000000000005
-rw-rw-r-- 1 zozo zozo      42 9月  24 23:31 edits_0000000000000000006-0000000000000000007
-rw-rw-r-- 1 zozo zozo      42 9月  25 00:31 edits_0000000000000000008-0000000000000000009
-rw-rw-r-- 1 zozo zozo      42 9月  25 01:31 edits_0000000000000000010-0000000000000000011
-rw-rw-r-- 1 zozo zozo      42 9月  25 02:31 edits_0000000000000000012-0000000000000000013
-rw-rw-r-- 1 zozo zozo      42 9月  25 03:31 edits_0000000000000000014-0000000000000000015
-rw-rw-r-- 1 zozo zozo      42 9月  25 04:31 edits_0000000000000000016-0000000000000000017
-rw-rw-r-- 1 zozo zozo      42 9月  25 05:31 edits_0000000000000000018-0000000000000000019
-rw-rw-r-- 1 zozo zozo      42 9月  25 06:31 edits_0000000000000000020-0000000000000000021
-rw-rw-r-- 1 zozo zozo      42 9月  25 07:31 edits_0000000000000000022-0000000000000000023
-rw-rw-r-- 1 zozo zozo      42 9月  25 08:31 edits_0000000000000000024-0000000000000000025
-rw-rw-r-- 1 zozo zozo      42 9月  25 09:31 edits_0000000000000000026-0000000000000000027
-rw-rw-r-- 1 zozo zozo      42 9月  25 10:31 edits_0000000000000000028-0000000000000000029
-rw-rw-r-- 1 zozo zozo      42 9月  25 11:31 edits_0000000000000000030-0000000000000000031
-rw-rw-r-- 1 zozo zozo      42 9月  25 12:31 edits_0000000000000000032-0000000000000000033
-rw-rw-r-- 1 zozo zozo      42 9月  25 13:31 edits_0000000000000000034-0000000000000000035
-rw-rw-r-- 1 zozo zozo      42 9月  25 14:31 edits_0000000000000000036-0000000000000000037
-rw-rw-r-- 1 zozo zozo      42 9月  25 15:31 edits_0000000000000000038-0000000000000000039
-rw-rw-r-- 1 zozo zozo      42 9月  25 16:31 edits_0000000000000000040-0000000000000000041
-rw-rw-r-- 1 zozo zozo      42 9月  25 17:31 edits_0000000000000000042-0000000000000000043
-rw-rw-r-- 1 zozo zozo      42 9月  25 18:31 edits_0000000000000000044-0000000000000000045
-rw-rw-r-- 1 zozo zozo      42 9月  25 19:31 edits_0000000000000000046-0000000000000000047
-rw-rw-r-- 1 zozo zozo    1209 9月  25 20:31 edits_0000000000000000048-0000000000000000064
-rw-rw-r-- 1 zozo zozo      42 9月  25 21:31 edits_0000000000000000065-0000000000000000066
-rw-rw-r-- 1 zozo zozo      42 9月  25 22:31 edits_0000000000000000067-0000000000000000068
-rw-rw-r-- 1 zozo zozo      42 9月  25 23:31 edits_0000000000000000069-0000000000000000070
-rw-rw-r-- 1 zozo zozo      42 9月  26 00:31 edits_0000000000000000071-0000000000000000072
-rw-rw-r-- 1 zozo zozo      42 9月  26 01:31 edits_0000000000000000073-0000000000000000074
-rw-rw-r-- 1 zozo zozo      42 9月  26 02:31 edits_0000000000000000075-0000000000000000076
-rw-rw-r-- 1 zozo zozo      42 9月  26 03:31 edits_0000000000000000077-0000000000000000078
-rw-rw-r-- 1 zozo zozo      42 9月  26 04:31 edits_0000000000000000079-0000000000000000080
-rw-rw-r-- 1 zozo zozo      42 9月  26 05:31 edits_0000000000000000081-0000000000000000082
-rw-rw-r-- 1 zozo zozo      42 9月  26 06:31 edits_0000000000000000083-0000000000000000084
-rw-rw-r-- 1 zozo zozo      42 9月  26 07:31 edits_0000000000000000085-0000000000000000086
-rw-rw-r-- 1 zozo zozo      42 9月  26 08:31 edits_0000000000000000087-0000000000000000088
-rw-rw-r-- 1 zozo zozo      42 9月  26 09:31 edits_0000000000000000089-0000000000000000090
-rw-rw-r-- 1 zozo zozo      42 9月  26 10:31 edits_0000000000000000091-0000000000000000092
-rw-rw-r-- 1 zozo zozo      42 9月  26 11:31 edits_0000000000000000093-0000000000000000094
-rw-rw-r-- 1 zozo zozo      42 9月  26 12:31 edits_0000000000000000095-0000000000000000096
-rw-rw-r-- 1 zozo zozo      42 9月  26 13:31 edits_0000000000000000097-0000000000000000098
-rw-rw-r-- 1 zozo zozo      42 9月  26 14:31 edits_0000000000000000099-0000000000000000100
-rw-rw-r-- 1 zozo zozo      42 9月  26 15:31 edits_0000000000000000101-0000000000000000102
-rw-rw-r-- 1 zozo zozo      42 9月  26 16:31 edits_0000000000000000103-0000000000000000104
-rw-rw-r-- 1 zozo zozo      42 9月  26 17:31 edits_0000000000000000105-0000000000000000106
-rw-rw-r-- 1 zozo zozo      42 9月  26 18:31 edits_0000000000000000107-0000000000000000108
-rw-rw-r-- 1 zozo zozo      42 9月  26 19:31 edits_0000000000000000109-0000000000000000110
-rw-rw-r-- 1 zozo zozo      42 9月  26 20:31 edits_0000000000000000111-0000000000000000112
-rw-rw-r-- 1 zozo zozo      42 9月  26 21:31 edits_0000000000000000113-0000000000000000114
-rw-rw-r-- 1 zozo zozo      42 9月  26 22:31 edits_0000000000000000115-0000000000000000116
-rw-rw-r-- 1 zozo zozo      42 9月  26 23:31 edits_0000000000000000117-0000000000000000118
-rw-rw-r-- 1 zozo zozo      42 9月  27 00:31 edits_0000000000000000119-0000000000000000120
-rw-rw-r-- 1 zozo zozo      42 9月  27 01:31 edits_0000000000000000121-0000000000000000122
-rw-rw-r-- 1 zozo zozo      42 9月  27 02:31 edits_0000000000000000123-0000000000000000124
-rw-rw-r-- 1 zozo zozo      42 9月  27 03:31 edits_0000000000000000125-0000000000000000126
-rw-rw-r-- 1 zozo zozo      42 9月  27 04:31 edits_0000000000000000127-0000000000000000128
-rw-rw-r-- 1 zozo zozo      42 9月  27 05:31 edits_0000000000000000129-0000000000000000130
-rw-rw-r-- 1 zozo zozo      42 9月  27 06:31 edits_0000000000000000131-0000000000000000132
-rw-rw-r-- 1 zozo zozo      42 9月  27 07:31 edits_0000000000000000133-0000000000000000134
-rw-rw-r-- 1 zozo zozo      42 9月  27 08:31 edits_0000000000000000135-0000000000000000136
-rw-rw-r-- 1 zozo zozo      42 9月  27 09:31 edits_0000000000000000137-0000000000000000138
-rw-rw-r-- 1 zozo zozo      42 9月  27 10:31 edits_0000000000000000139-0000000000000000140
-rw-rw-r-- 1 zozo zozo      42 9月  27 11:32 edits_0000000000000000141-0000000000000000142
-rw-rw-r-- 1 zozo zozo      42 9月  27 12:32 edits_0000000000000000143-0000000000000000144
-rw-rw-r-- 1 zozo zozo      42 9月  27 13:32 edits_0000000000000000145-0000000000000000146
-rw-rw-r-- 1 zozo zozo     793 9月  27 14:32 edits_0000000000000000147-0000000000000000158
-rw-rw-r-- 1 zozo zozo      42 9月  27 15:32 edits_0000000000000000159-0000000000000000160
-rw-rw-r-- 1 zozo zozo      42 9月  27 16:32 edits_0000000000000000161-0000000000000000162
-rw-rw-r-- 1 zozo zozo      42 9月  27 17:32 edits_0000000000000000163-0000000000000000164
-rw-rw-r-- 1 zozo zozo      42 9月  27 18:32 edits_0000000000000000165-0000000000000000166
-rw-rw-r-- 1 zozo zozo      42 9月  27 19:32 edits_0000000000000000167-0000000000000000168
-rw-rw-r-- 1 zozo zozo      42 9月  27 20:32 edits_0000000000000000169-0000000000000000170
-rw-rw-r-- 1 zozo zozo      42 9月  27 21:32 edits_0000000000000000171-0000000000000000172
-rw-rw-r-- 1 zozo zozo      42 9月  27 22:32 edits_0000000000000000173-0000000000000000174
-rw-rw-r-- 1 zozo zozo      42 9月  27 23:32 edits_0000000000000000175-0000000000000000176
-rw-rw-r-- 1 zozo zozo     460 9月  28 00:32 edits_0000000000000000177-0000000000000000185
-rw-rw-r-- 1 zozo zozo     529 9月  28 01:32 edits_0000000000000000186-0000000000000000193
-rw-rw-r-- 1 zozo zozo    3566 9月  28 02:32 edits_0000000000000000194-0000000000000000241
-rw-rw-r-- 1 zozo zozo    4721 9月  28 03:32 edits_0000000000000000242-0000000000000000304
-rw-rw-r-- 1 zozo zozo      42 9月  28 04:32 edits_0000000000000000305-0000000000000000306
-rw-rw-r-- 1 zozo zozo      42 9月  28 05:32 edits_0000000000000000307-0000000000000000308
-rw-rw-r-- 1 zozo zozo      42 9月  28 06:32 edits_0000000000000000309-0000000000000000310
-rw-rw-r-- 1 zozo zozo      42 9月  28 07:32 edits_0000000000000000311-0000000000000000312
-rw-rw-r-- 1 zozo zozo      42 9月  28 08:32 edits_0000000000000000313-0000000000000000314
-rw-rw-r-- 1 zozo zozo      42 9月  28 09:32 edits_0000000000000000315-0000000000000000316
-rw-rw-r-- 1 zozo zozo      42 9月  28 10:32 edits_0000000000000000317-0000000000000000318
-rw-rw-r-- 1 zozo zozo      42 9月  28 11:32 edits_0000000000000000319-0000000000000000320
-rw-rw-r-- 1 zozo zozo      88 9月  28 12:32 edits_0000000000000000321-0000000000000000323
-rw-rw-r-- 1 zozo zozo     152 9月  28 13:32 edits_0000000000000000324-0000000000000000328
-rw-rw-r-- 1 zozo zozo      42 9月  28 14:32 edits_0000000000000000329-0000000000000000330
-rw-rw-r-- 1 zozo zozo     185 9月  28 15:32 edits_0000000000000000331-0000000000000000334
-rw-rw-r-- 1 zozo zozo      42 9月  28 16:32 edits_0000000000000000335-0000000000000000336
-rw-rw-r-- 1 zozo zozo    5830 9月  28 17:32 edits_0000000000000000337-0000000000000000455
-rw-rw-r-- 1 zozo zozo     794 9月  28 18:32 edits_0000000000000000456-0000000000000000467
-rw-rw-r-- 1 zozo zozo     864 9月  28 19:32 edits_0000000000000000468-0000000000000000480
-rw-rw-r-- 1 zozo zozo    1378 9月  28 20:32 edits_0000000000000000481-0000000000000000500
-rw-rw-r-- 1 zozo zozo     289 9月  28 21:32 edits_0000000000000000501-0000000000000000506
-rw-rw-r-- 1 zozo zozo     618 9月  28 22:32 edits_0000000000000000507-0000000000000000516
-rw-rw-r-- 1 zozo zozo      97 9月  28 23:32 edits_0000000000000000517-0000000000000000519
-rw-rw-r-- 1 zozo zozo      42 9月  29 00:32 edits_0000000000000000520-0000000000000000521
-rw-rw-r-- 1 zozo zozo      42 9月  29 01:32 edits_0000000000000000522-0000000000000000523
-rw-rw-r-- 1 zozo zozo      42 9月  29 02:32 edits_0000000000000000524-0000000000000000525
-rw-rw-r-- 1 zozo zozo      42 9月  29 03:32 edits_0000000000000000526-0000000000000000527
-rw-rw-r-- 1 zozo zozo      42 9月  29 04:32 edits_0000000000000000528-0000000000000000529
-rw-rw-r-- 1 zozo zozo      42 9月  29 05:32 edits_0000000000000000530-0000000000000000531
-rw-rw-r-- 1 zozo zozo      42 9月  29 06:32 edits_0000000000000000532-0000000000000000533
-rw-rw-r-- 1 zozo zozo      42 9月  29 07:32 edits_0000000000000000534-0000000000000000535
-rw-rw-r-- 1 zozo zozo      42 9月  29 08:32 edits_0000000000000000536-0000000000000000537
-rw-rw-r-- 1 zozo zozo      42 9月  29 09:32 edits_0000000000000000538-0000000000000000539
-rw-rw-r-- 1 zozo zozo      42 9月  29 10:32 edits_0000000000000000540-0000000000000000541
-rw-rw-r-- 1 zozo zozo      42 9月  29 11:32 edits_0000000000000000542-0000000000000000543
-rw-rw-r-- 1 zozo zozo      42 9月  29 12:32 edits_0000000000000000544-0000000000000000545
-rw-rw-r-- 1 zozo zozo      42 9月  29 13:32 edits_0000000000000000546-0000000000000000547
-rw-rw-r-- 1 zozo zozo      42 9月  29 14:32 edits_0000000000000000548-0000000000000000549
-rw-rw-r-- 1 zozo zozo      42 9月  29 15:32 edits_0000000000000000550-0000000000000000551
-rw-rw-r-- 1 zozo zozo      42 9月  29 16:32 edits_0000000000000000552-0000000000000000553
-rw-rw-r-- 1 zozo zozo      42 9月  29 17:32 edits_0000000000000000554-0000000000000000555
-rw-rw-r-- 1 zozo zozo      42 9月  29 18:32 edits_0000000000000000556-0000000000000000557
-rw-rw-r-- 1 zozo zozo      42 9月  29 19:32 edits_0000000000000000558-0000000000000000559
-rw-rw-r-- 1 zozo zozo      42 9月  29 20:32 edits_0000000000000000560-0000000000000000561
-rw-rw-r-- 1 zozo zozo      42 9月  29 21:32 edits_0000000000000000562-0000000000000000563
-rw-rw-r-- 1 zozo zozo      42 9月  29 22:32 edits_0000000000000000564-0000000000000000565
-rw-rw-r-- 1 zozo zozo      42 9月  29 23:32 edits_0000000000000000566-0000000000000000567
-rw-rw-r-- 1 zozo zozo      42 9月  30 00:32 edits_0000000000000000568-0000000000000000569
-rw-rw-r-- 1 zozo zozo      42 9月  30 01:32 edits_0000000000000000570-0000000000000000571
-rw-rw-r-- 1 zozo zozo      42 9月  30 02:32 edits_0000000000000000572-0000000000000000573
-rw-rw-r-- 1 zozo zozo      42 9月  30 03:32 edits_0000000000000000574-0000000000000000575
-rw-rw-r-- 1 zozo zozo      42 9月  30 04:32 edits_0000000000000000576-0000000000000000577
-rw-rw-r-- 1 zozo zozo      42 9月  30 05:32 edits_0000000000000000578-0000000000000000579
-rw-rw-r-- 1 zozo zozo      42 9月  30 06:32 edits_0000000000000000580-0000000000000000581
-rw-rw-r-- 1 zozo zozo      42 9月  30 07:32 edits_0000000000000000582-0000000000000000583
-rw-rw-r-- 1 zozo zozo      42 9月  30 08:32 edits_0000000000000000584-0000000000000000585
-rw-rw-r-- 1 zozo zozo      42 9月  30 09:32 edits_0000000000000000586-0000000000000000587
-rw-rw-r-- 1 zozo zozo      42 9月  30 10:32 edits_0000000000000000588-0000000000000000589
-rw-rw-r-- 1 zozo zozo      42 9月  30 11:32 edits_0000000000000000590-0000000000000000591
-rw-rw-r-- 1 zozo zozo      42 9月  30 12:32 edits_0000000000000000592-0000000000000000593
-rw-rw-r-- 1 zozo zozo      42 9月  30 13:32 edits_0000000000000000594-0000000000000000595
-rw-rw-r-- 1 zozo zozo      42 9月  30 14:32 edits_0000000000000000596-0000000000000000597
-rw-rw-r-- 1 zozo zozo      42 9月  30 15:32 edits_0000000000000000598-0000000000000000599
-rw-rw-r-- 1 zozo zozo      42 9月  30 16:32 edits_0000000000000000600-0000000000000000601
-rw-rw-r-- 1 zozo zozo      42 9月  30 17:32 edits_0000000000000000602-0000000000000000603
-rw-rw-r-- 1 zozo zozo      42 9月  30 18:32 edits_0000000000000000604-0000000000000000605
-rw-rw-r-- 1 zozo zozo      42 9月  30 19:32 edits_0000000000000000606-0000000000000000607
-rw-rw-r-- 1 zozo zozo      42 9月  30 20:32 edits_0000000000000000608-0000000000000000609
-rw-rw-r-- 1 zozo zozo      42 9月  30 21:32 edits_0000000000000000610-0000000000000000611
-rw-rw-r-- 1 zozo zozo      42 9月  30 22:32 edits_0000000000000000612-0000000000000000613
-rw-rw-r-- 1 zozo zozo      42 9月  30 23:32 edits_0000000000000000614-0000000000000000615
-rw-rw-r-- 1 zozo zozo      42 10月  1 00:32 edits_0000000000000000616-0000000000000000617
-rw-rw-r-- 1 zozo zozo      42 10月  1 01:32 edits_0000000000000000618-0000000000000000619
-rw-rw-r-- 1 zozo zozo      42 10月  1 02:32 edits_0000000000000000620-0000000000000000621
-rw-rw-r-- 1 zozo zozo      42 10月  1 03:32 edits_0000000000000000622-0000000000000000623
-rw-rw-r-- 1 zozo zozo      42 10月  1 04:32 edits_0000000000000000624-0000000000000000625
-rw-rw-r-- 1 zozo zozo      42 10月  1 05:32 edits_0000000000000000626-0000000000000000627
-rw-rw-r-- 1 zozo zozo      42 10月  1 06:32 edits_0000000000000000628-0000000000000000629
-rw-rw-r-- 1 zozo zozo      42 10月  1 07:32 edits_0000000000000000630-0000000000000000631
-rw-rw-r-- 1 zozo zozo      42 10月  1 08:32 edits_0000000000000000632-0000000000000000633
-rw-rw-r-- 1 zozo zozo      42 10月  1 09:32 edits_0000000000000000634-0000000000000000635
-rw-rw-r-- 1 zozo zozo      42 10月  1 10:32 edits_0000000000000000636-0000000000000000637
-rw-rw-r-- 1 zozo zozo      42 10月  1 11:32 edits_0000000000000000638-0000000000000000639
-rw-rw-r-- 1 zozo zozo      42 10月  1 12:32 edits_0000000000000000640-0000000000000000641
-rw-rw-r-- 1 zozo zozo      42 10月  1 13:32 edits_0000000000000000642-0000000000000000643
-rw-rw-r-- 1 zozo zozo      42 10月  1 14:32 edits_0000000000000000644-0000000000000000645
-rw-rw-r-- 1 zozo zozo      42 10月  1 15:32 edits_0000000000000000646-0000000000000000647
-rw-rw-r-- 1 zozo zozo      42 10月  1 16:32 edits_0000000000000000648-0000000000000000649
-rw-rw-r-- 1 zozo zozo      42 10月  1 17:32 edits_0000000000000000650-0000000000000000651
-rw-rw-r-- 1 zozo zozo      42 10月  1 18:32 edits_0000000000000000652-0000000000000000653
-rw-rw-r-- 1 zozo zozo      42 10月  1 19:32 edits_0000000000000000654-0000000000000000655
-rw-rw-r-- 1 zozo zozo      42 10月  1 20:32 edits_0000000000000000656-0000000000000000657
-rw-rw-r-- 1 zozo zozo      42 10月  1 21:32 edits_0000000000000000658-0000000000000000659
-rw-rw-r-- 1 zozo zozo     156 10月  1 22:32 edits_0000000000000000660-0000000000000000663
-rw-rw-r-- 1 zozo zozo    1037 10月  1 21:32 fsimage_0000000000000000659
-rw-rw-r-- 1 zozo zozo      62 10月  1 21:32 fsimage_0000000000000000659.md5
-rw-rw-r-- 1 zozo zozo     789 10月  1 22:32 fsimage_0000000000000000663
-rw-rw-r-- 1 zozo zozo      62 10月  1 22:32 fsimage_0000000000000000663.md5
-rw-rw-r-- 1 zozo zozo     202 10月  1 22:32 VERSION
[zozo@vm06 current]$ 
```

## 2.7 操作测试 - 每小时滚动后 - 查看 Fsimage 内容

_注: `2019-10-01 22:32:00` 后_

执行以下命令将 `fsimage_0000000000000000663` 文件转换成 XML 格式的 `fsimage_0000000000000000663_viewer` 可视化文件:

```bash
# [zozo@vm017 current]$ hdfs oiv -p XML -i fsimage_0000000000000000663 -o fsimage_0000000000000000663_viewer
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs oiv -p XML -i /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current/fsimage_0000000000000000663 -o /home/zozo/app/hadoop/fortest/fsimage_0000000000000000663_viewer
[zozo@vm017 hadoop-2.7.2]$ 
```

格式化之后的 `fsimage_0000000000000000663_viewer` 文件内容中包含了近乎所有节点信息 (最近操作的除外), 如下所示:

- 此时, 在 Fsimage 文件中, `/d2/d2_c` 和 `/d2/d2_b` 这两个文件夹已经不存在了 (因为每小时滚动发生后, 已经将滚动前的镜像和删除操作记录合并了)

```xml
<?xml version="1.0"?>
<fsimage>
    <NameSection>
        <genstampV1>1000</genstampV1>
        <genstampV2>1051</genstampV2>
        <genstampV1Limit>0</genstampV1Limit>
        <lastAllocatedBlockId>1073741874</lastAllocatedBlockId>
        <txid>663</txid>
    </NameSection>
    <INodeSection>
        <lastInodeId>16437</lastInodeId>
        <inode>
            <id>16385</id>
            <type>DIRECTORY</type>
            <name></name>
            <mtime>1569679770899</mtime>
            <permission>zozo:supergroup:rwxr-xr-x</permission>
            <nsquota>9223372036854775807</nsquota>
            <dsquota>-1</dsquota>
        </inode>
        <inode>
            <id>16386</id>
            <type>FILE</type>
            <name>wc.input</name>
            <replication>3</replication>
            <mtime>1569413064037</mtime>
            <atime>1569413062995</atime>
            <perferredBlockSize>134217728</perferredBlockSize>
            <permission>zozo:supergroup:rw-r--r--</permission>
            <blocks>
                <block>
                    <id>1073741825</id>
                    <genstamp>1001</genstamp>
                    <numBytes>36</numBytes>
                </block>
            </blocks>
        </inode>
        <inode>
            <id>16387</id>
            <type>FILE</type>
            <name>hadoop-2.7.2.tar.gz</name>
            <replication>3</replication>
            <mtime>1569413098417</mtime>
            <atime>1569681725982</atime>
            <perferredBlockSize>134217728</perferredBlockSize>
            <permission>zozo:supergroup:rw-r--r--</permission>
            <blocks>
                <block>
                    <id>1073741826</id>
                    <genstamp>1002</genstamp>
                    <numBytes>134217728</numBytes>
                </block>
                <block>
                    <id>1073741827</id>
                    <genstamp>1003</genstamp>
                    <numBytes>77829046</numBytes>
                </block>
            </blocks>
        </inode>
        <inode>
            <id>16413</id>
            <type>DIRECTORY</type>
            <name>d2</name>
            <mtime>1569937803194</mtime>
            <permission>zozo:supergroup:rwxr-xr-x</permission>
            <nsquota>-1</nsquota>
            <dsquota>-1</dsquota>
        </inode>
        <inode>
            <id>16414</id>
            <type>DIRECTORY</type>
            <name>d2_a</name>
            <mtime>1569667639914</mtime>
            <permission>zozo:supergroup:rwxr-xr-x</permission>
            <nsquota>-1</nsquota>
            <dsquota>-1</dsquota>
        </inode>
        <inode>
            <id>16425</id>
            <type>FILE</type>
            <name>f1</name>
            <replication>3</replication>
            <mtime>1569662954325</mtime>
            <atime>1569673192617</atime>
            <perferredBlockSize>134217728</perferredBlockSize>
            <permission>zozo:supergroup:rw-r--r--</permission>
            <blocks>
                <block>
                    <id>1073741868</id>
                    <genstamp>1045</genstamp>
                    <numBytes>8</numBytes>
                </block>
            </blocks>
        </inode>
        <inode>
            <id>16426</id>
            <type>FILE</type>
            <name>f2_rename</name>
            <replication>2</replication>
            <mtime>1569664981957</mtime>
            <atime>1569673226015</atime>
            <perferredBlockSize>134217728</perferredBlockSize>
            <permission>zozo:supergroup:rw-r--r--</permission>
            <blocks>
                <block>
                    <id>1073741869</id>
                    <genstamp>1046</genstamp>
                    <numBytes>8</numBytes>
                </block>
            </blocks>
        </inode>
    </INodeSection>
    <INodeReferenceSection></INodeReferenceSection>
    <SnapshotSection>
        <snapshotCounter>0</snapshotCounter>
    </SnapshotSection>
    <INodeDirectorySection>
        <directory>
            <parent>16385</parent>
            <inode>16413</inode>
            <inode>16387</inode>
            <inode>16386</inode>
        </directory>
        <directory>
            <parent>16413</parent>
            <inode>16414</inode>
        </directory>
        <directory>
            <parent>16414</parent>
            <inode>16425</inode>
            <inode>16426</inode>
        </directory>
    </INodeDirectorySection>
    <FileUnderConstructionSection></FileUnderConstructionSection>
    <SnapshotDiffSection>
        <diff>
            <inodeid>16385</inodeid>
        </diff>
    </SnapshotDiffSection>
    <SecretManagerSection>
        <currentId>0</currentId>
        <tokenSequenceNumber>0</tokenSequenceNumber>
    </SecretManagerSection>
    <CacheManagerSection>
        <nextDirectiveId>1</nextDirectiveId>
    </CacheManagerSection>
</fsimage>
```

## 2.8 操作测试 - 每小时滚动后 - 查看 Edits 内容

_注: `2019-10-01 22:32:00` 后_

- step 1

执行以下命令将 `edits_0000000000000000660-0000000000000000663` 文件转换成 XML 格式的 `edits_0000000000000000660-0000000000000000663_viewer` 可视化文件:

```bash
# [zozo@vm017 current]$ hdfs oev -p XML -i edits_0000000000000000660-0000000000000000663 -o edits_0000000000000000660-0000000000000000663_viewer
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs oev -p XML -i /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current/edits_0000000000000000660-0000000000000000663 -o /home/zozo/app/hadoop/fortest/edits_0000000000000000660-0000000000000000663_viewer
[zozo@vm017 hadoop-2.7.2]$ 
```

格式化之后的 `edits_0000000000000000660-0000000000000000663_viewer` 文件内容中包含了事物 ID `660 - 663` 阶段的操作记录, 如下所示:

- 事物 ID `660`: OP_START_LOG_SEGMENT (开始日志段)
- 事物 ID `661`: OP_DELETE (删除  `/d2/d2_c` 文件夹)
- 事物 ID `662`: OP_DELETE (删除  `/d2/d2_b` 文件夹)
- 事物 ID `663`: OP_END_LOG_SEGMENT (结束日志段)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<EDITS>
  <EDITS_VERSION>-63</EDITS_VERSION>
  <RECORD>
    <OPCODE>OP_START_LOG_SEGMENT</OPCODE>
    <DATA>
      <TXID>660</TXID>
    </DATA>
  </RECORD>
  <RECORD>
    <OPCODE>OP_DELETE</OPCODE>
    <DATA>
      <TXID>661</TXID>
      <LENGTH>0</LENGTH>
      <PATH>/d2/d2_c</PATH>
      <TIMESTAMP>1569937760907</TIMESTAMP>
      <RPC_CLIENTID>4d8c83b2-92fe-424d-8215-9ed8cf5d7652</RPC_CLIENTID>
      <RPC_CALLID>3</RPC_CALLID>
    </DATA>
  </RECORD>
  <RECORD>
    <OPCODE>OP_DELETE</OPCODE>
    <DATA>
      <TXID>662</TXID>
      <LENGTH>0</LENGTH>
      <PATH>/d2/d2_b</PATH>
      <TIMESTAMP>1569937803194</TIMESTAMP>
      <RPC_CLIENTID>59f74bb6-045c-4269-94d4-6ab8a2ab80a5</RPC_CLIENTID>
      <RPC_CALLID>3</RPC_CALLID>
    </DATA>
  </RECORD>
  <RECORD>
    <OPCODE>OP_END_LOG_SEGMENT</OPCODE>
    <DATA>
      <TXID>663</TXID>
    </DATA>
  </RECORD>
</EDITS>
```

- step 2

执行以下命令将 `edits_inprogress_0000000000000000664` 文件转换成 XML 格式的 `edits_inprogress_0000000000000000664_viewer` 可视化文件:

```bash
[zozo@vm017 hadoop-2.7.2]$ du -h /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current/edits_inprogress_0000000000000000664 
1.0M	/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current/edits_inprogress_0000000000000000664
# [zozo@vm017 current]$ hdfs oev -p XML -i edits_inprogress_0000000000000000664 -o edits_inprogress_0000000000000000664_viewer
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs oev -p XML -i /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current/edits_inprogress_0000000000000000664 -o /home/zozo/app/hadoop/fortest/edits_inprogress_0000000000000000664_viewer
[zozo@vm017 hadoop-2.7.2]$ 
[zozo@vm017 fortest]$ du -h /home/zozo/app/hadoop/fortest/edits_inprogress_0000000000000000664_viewer
4.0K	/home/zozo/app/hadoop/fortest/edits_inprogress_0000000000000000664_viewer
[zozo@vm017 fortest]$ 
```

格式化之后的 `edits_inprogress_0000000000000000664_viewer` 文件内容中包含了最新 (事物 ID 从 `664` 开始) 的操作记录 (还未结束), 如下所示:

- 事物 ID `664`: OP_START_LOG_SEGMENT (开始日志段)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<EDITS>
  <EDITS_VERSION>-63</EDITS_VERSION>
  <RECORD>
    <OPCODE>OP_START_LOG_SEGMENT</OPCODE>
    <DATA>
      <TXID>664</TXID>
    </DATA>
  </RECORD>
</EDITS>
```

---

# 三 CheckPoint 时间设置

CheckPoint 触发条件:
- 定时时间到
- Edits 中的操作次数达到上限

`hdfs-site.xml` 文件中配置定时执行 CheckPoint 的时间间隔 (默认 1 小时):
```xml
<property>
  <name>dfs.namenode.checkpoint.period</name>
  <value>3600</value>
  <description>The number of seconds between two periodic checkpoints.
  </description>
</property>
```

`hdfs-site.xml` 文件中配置操作次数达到指定次数时 (默认 100 万次), 触发 CheckPoint.

```xml
<property>
  <name>dfs.namenode.checkpoint.txns</name>
  <value>1000000</value>
  <description>The Secondary NameNode or CheckpointNode will create a checkpoint
  of the namespace every 'dfs.namenode.checkpoint.txns' transactions, regardless
  of whether 'dfs.namenode.checkpoint.period' has expired.
  </description>
</property>
```

---

# 四 NameNode 故障处理

## 4.1 将 SecondaryNameNode 中的数据拷贝到 NameNode 中

### 4.1.1 停止 NameNode 进程并删除存储数据

- `vm017` (NameNode) 上将 NameNode 进程停止:

```
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
32064 org.apache.hadoop.yarn.server.nodemanager.NodeManager
9094 sun.tools.jps.Jps -m -l
31416 org.apache.hadoop.hdfs.server.datanode.DataNode
31276 org.apache.hadoop.hdfs.server.namenode.NameNode
[zozo@vm017 hadoop-2.7.2]$ kill -9 31276
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
32064 org.apache.hadoop.yarn.server.nodemanager.NodeManager
31416 org.apache.hadoop.hdfs.server.datanode.DataNode
9564 sun.tools.jps.Jps -m -l
[zozo@vm017 hadoop-2.7.2]$ 
```

此时已无法访问 HDFS 控制台: http://193.112.38.200:50070

- `vm017` (NameNode) 上将 NameNode 存储目录 `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name` 删除:

```
[zozo@vm017 dfs]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs
[zozo@vm017 dfs]$ ll
总用量 8
drwx------ 3 zozo zozo 4096 9月  24 21:30 data
drwxrwxr-x 3 zozo zozo 4096 9月  24 21:30 name
[zozo@vm017 dfs]$ mv name name_backup20191002_1
[zozo@vm017 dfs]$ ll
总用量 8
drwx------ 3 zozo zozo 4096 9月  24 21:30 data
drwxrwxr-x 3 zozo zozo 4096 9月  24 21:30 name_backup20191002_1
[zozo@vm017 dfs]$ 
```

### 4.1.2 拷贝 SecondaryNameNode 中的数据到 NameNode 存储数据目录中

- `vm06` (SecondaryNameNode) 上拷贝存储目录 `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/namesecondary` 到 `vm017` (NameNode) 的 `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/` 上, 并重命名为 `name`:

```
[zozo@vm06 dfs]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs
[zozo@vm06 dfs]$ ll
总用量 8
drwx------ 3 zozo zozo 4096 9月  24 21:30 data
drwxrwxr-x 3 zozo zozo 4096 9月  24 21:31 namesecondary
[zozo@vm06 dfs]$ scp -r /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/namesecondary zozo@vm017:/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/namesecondary
[zozo@vm06 dfs]$ 
```

```
[zozo@vm017 dfs]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs
[zozo@vm017 dfs]$ ll
总用量 12
drwx------ 3 zozo zozo 4096 9月  24 21:30 data
drwxrwxr-x 3 zozo zozo 4096 9月  24 21:30 name_backup20191002_1
drwxrwxr-x 3 zozo zozo 4096 10月  2 11:02 namesecondary
[zozo@vm017 dfs]$ mv namesecondary name
[zozo@vm017 dfs]$ ll
总用量 12
drwx------ 3 zozo zozo 4096 9月  24 21:30 data
drwxrwxr-x 3 zozo zozo 4096 10月  2 11:02 name
drwxrwxr-x 3 zozo zozo 4096 9月  24 21:30 name_backup20191002_1
[zozo@vm017 dfs]$ 
```

此时已成功将 SecondaryNameNode 中的数据拷贝到 NameNode 中

### 4.1.3 重新启动 NameNode 并测试

- `vm017` (NameNode) 上启动 NameNode:

```
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start namenode
```

```
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
32064 org.apache.hadoop.yarn.server.nodemanager.NodeManager
10854 sun.tools.jps.Jps -m -l
31416 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start namenode
starting namenode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-namenode-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
32064 org.apache.hadoop.yarn.server.nodemanager.NodeManager
31416 org.apache.hadoop.hdfs.server.datanode.DataNode
10908 org.apache.hadoop.hdfs.server.namenode.NameNode
10991 sun.tools.jps.Jps -m -l
[zozo@vm017 hadoop-2.7.2]$ 
```

- 测试集群: 此时集群可正常访问

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /
drwxr-xr-x   - zozo supergroup          0 2019-10-01 21:50 /d2
drwxr-xr-x   - zozo supergroup          0 2019-09-28 18:47 /d2/d2_a
-rw-r--r--   3 zozo supergroup          8 2019-09-28 17:29 /d2/d2_a/f1
-rw-r--r--   2 zozo supergroup          8 2019-09-28 18:03 /d2/d2_a/f2_rename
-rw-r--r--   3 zozo supergroup  212046774 2019-09-25 20:04 /hadoop-2.7.2.tar.gz
-rw-r--r--   3 zozo supergroup         36 2019-09-25 20:04 /wc.input
[zozo@vm017 hadoop-2.7.2]$ 
```

## 4.2 使用 -importCheckpoint 选项 (推荐) (`TODO`)

### 4.2.1 修改配置

- 所有节点修改 `etc/hadoop/hdfs-site.xml` 增加如下配置:

```xml
<!-- 注: 以下配置仅用于测试 -importCheckpoint 效果, 测试完成后需要删除该配置 (使用默认值 3600 秒) -->
<property>
  <name>dfs.namenode.checkpoint.period</name>
  <value>300</value>
  <description>The number of seconds between two periodic checkpoints.
  </description>
</property>
```

### 4.2.2 停止 NameNode 进程并删除存储数据

- `vm017` (NameNode) 上将 NameNode 进程停止:

```
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
27158 org.apache.hadoop.hdfs.server.datanode.DataNode
27465 org.apache.hadoop.yarn.server.nodemanager.NodeManager
27017 org.apache.hadoop.hdfs.server.namenode.NameNode
28063 sun.tools.jps.Jps -m -l
[zozo@vm017 hadoop-2.7.2]$ kill -9 27017
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
28099 sun.tools.jps.Jps -m -l
27158 org.apache.hadoop.hdfs.server.datanode.DataNode
27465 org.apache.hadoop.yarn.server.nodemanager.NodeManager
[zozo@vm017 hadoop-2.7.2]$ 
```

此时已无法访问 HDFS 控制台: http://193.112.38.200:50070

- `vm017` (NameNode) 上将 NameNode 存储目录 `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name` 下的子目录和文件删除:

```
[zozo@vm017 dfs]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs
[zozo@vm017 dfs]$ ll
总用量 12
drwx------ 3 zozo zozo 4096 10月  2 13:30 data
drwxrwxr-x 3 zozo zozo 4096 10月  2 13:30 name
drwxrwxr-x 3 zozo zozo 4096 9月  24 21:30 name_backup20191002_1
[zozo@vm017 dfs]$ mv name name_backup20191002_2
[zozo@vm017 dfs]$ mkdir name
[zozo@vm017 dfs]$ ll
总用量 16
drwx------ 3 zozo zozo 4096 10月  2 13:30 data
drwxrwxr-x 2 zozo zozo 4096 10月  2 13:36 name
drwxrwxr-x 3 zozo zozo 4096 9月  24 21:30 name_backup20191002_1
drwxrwxr-x 3 zozo zozo 4096 10月  2 13:30 name_backup20191002_2
[zozo@vm017 dfs]$ ll name
总用量 0
[zozo@vm017 dfs]$ 
```

### 4.2.3 将 SecondaryNameNode 存储目录拷贝到 NameNode 存储目录的平级目录上, 并删除 in_user.lock 文件

- `vm06` (SecondaryNameNode) 上拷贝存储目录 `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/namesecondary` 到 `vm017` (NameNode) 的 `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/` 上, 并删除 `in_user.lock` 文件:

```
[zozo@vm06 dfs]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs
[zozo@vm06 dfs]$ ll
总用量 8
drwx------ 3 zozo zozo 4096 10月  2 13:30 data
drwxrwxr-x 3 zozo zozo 4096 10月  2 13:30 namesecondary
[zozo@vm06 dfs]$ scp -r /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/namesecondary zozo@vm017:/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/namesecondary
[zozo@vm06 dfs]$ 
```

```
[zozo@vm017 dfs]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs
[zozo@vm017 dfs]$ ll
总用量 20
drwx------ 3 zozo zozo 4096 10月  2 13:30 data
drwxrwxr-x 2 zozo zozo 4096 10月  2 13:36 name
drwxrwxr-x 3 zozo zozo 4096 9月  24 21:30 name_backup20191002_1
drwxrwxr-x 3 zozo zozo 4096 10月  2 13:30 name_backup20191002_2
drwxrwxr-x 3 zozo zozo 4096 10月  2 13:37 namesecondary
[zozo@vm017 dfs]$ ll namesecondary/
总用量 24
drwxrwxr-x 2 zozo zozo 20480 10月  2 13:37 current
-rw-rw-r-- 1 zozo zozo    10 10月  2 13:37 in_use.lock
[zozo@vm017 dfs]$ rm namesecondary/in_use.lock 
[zozo@vm017 dfs]$ ll name
总用量 0
[zozo@vm017 dfs]$ ll namesecondary/
总用量 20
drwxrwxr-x 2 zozo zozo 20480 10月  2 13:37 current
[zozo@vm017 dfs]$ 
```

### 4.2.4 执行导入检查点命令

- `vm06` (SecondaryNameNode) 上执行以下命令:

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs namenode -importCheckpoint
```

执行失败! `TODO`

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs namenode -importCheckpoint
19/10/02 13:39:34 INFO namenode.NameNode: STARTUP_MSG: 
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   host = vm017/172.16.0.17
STARTUP_MSG:   args = [-importCheckpoint]
STARTUP_MSG:   version = 2.7.2
STARTUP_MSG:   classpath = /home/zozo/app/hadoop/hadoop-2.7.2/etc/hadoop:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpcore-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-client-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/mockito-all-1.8.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-auth-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-core-1.8.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-recipes-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-kerberos-codec-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/java-xmlbuilder-0.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-httpclient-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-1.7.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsp-api-2.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-util-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-net-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-digester-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jets3t-0.9.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpclient-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/gson-2.2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-api-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-math3-3.1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsch-0.1.42.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-asn1-api-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-configuration-1.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-framework-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xml-apis-1.3.04.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-daemon-1.0.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xercesImpl-2.9.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-all-4.0.23.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-client-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-web-proxy-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-nodemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-resourcemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-tests-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-sharedcachemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-registry-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-applicationhistoryservice-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-client-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-api-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-shuffle-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-plugins-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-app-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/contrib/capacity-scheduler/*.jar
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r b165c4fe8a74265c792ce23f546c64604acf0e41; compiled by 'jenkins' on 2016-01-26T00:08Z
STARTUP_MSG:   java = 1.8.0_192
************************************************************/
19/10/02 13:39:34 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
19/10/02 13:39:34 INFO namenode.NameNode: createNameNode [-importCheckpoint]
19/10/02 13:39:35 INFO impl.MetricsConfig: loaded properties from hadoop-metrics2.properties
19/10/02 13:39:35 INFO impl.MetricsSystemImpl: Scheduled snapshot period at 10 second(s).
19/10/02 13:39:35 INFO impl.MetricsSystemImpl: NameNode metrics system started
19/10/02 13:39:35 INFO namenode.NameNode: fs.defaultFS is hdfs://vm017:9000
19/10/02 13:39:35 INFO namenode.NameNode: Clients are to use vm017:9000 to access this namenode/service.
19/10/02 13:39:35 INFO hdfs.DFSUtil: Starting Web-server for hdfs at: http://0.0.0.0:50070
19/10/02 13:39:35 INFO mortbay.log: Logging to org.slf4j.impl.Log4jLoggerAdapter(org.mortbay.log) via org.mortbay.log.Slf4jLog
19/10/02 13:39:35 INFO server.AuthenticationFilter: Unable to initialize FileSignerSecretProvider, falling back to use random secrets.
19/10/02 13:39:35 INFO http.HttpRequestLog: Http request log for http.requests.namenode is not defined
19/10/02 13:39:35 INFO http.HttpServer2: Added global filter 'safety' (class=org.apache.hadoop.http.HttpServer2$QuotingInputFilter)
19/10/02 13:39:35 INFO http.HttpServer2: Added filter static_user_filter (class=org.apache.hadoop.http.lib.StaticUserWebFilter$StaticUserFilter) to context hdfs
19/10/02 13:39:35 INFO http.HttpServer2: Added filter static_user_filter (class=org.apache.hadoop.http.lib.StaticUserWebFilter$StaticUserFilter) to context static
19/10/02 13:39:35 INFO http.HttpServer2: Added filter static_user_filter (class=org.apache.hadoop.http.lib.StaticUserWebFilter$StaticUserFilter) to context logs
19/10/02 13:39:35 INFO http.HttpServer2: Added filter 'org.apache.hadoop.hdfs.web.AuthFilter' (class=org.apache.hadoop.hdfs.web.AuthFilter)
19/10/02 13:39:35 INFO http.HttpServer2: addJerseyResourcePackage: packageName=org.apache.hadoop.hdfs.server.namenode.web.resources;org.apache.hadoop.hdfs.web.resources, pathSpec=/webhdfs/v1/*
19/10/02 13:39:35 INFO http.HttpServer2: Jetty bound to port 50070
19/10/02 13:39:35 INFO mortbay.log: jetty-6.1.26
19/10/02 13:39:35 INFO mortbay.log: Started HttpServer2$SelectChannelConnectorWithSafeStartup@0.0.0.0:50070
19/10/02 13:39:35 WARN namenode.FSNamesystem: !!! WARNING !!!
	The NameNode currently runs without persistent storage.
	Any changes to the file system meta-data may be lost.
	Recommended actions:
		- shutdown and restart NameNode with configured "dfs.namenode.name.dir" in hdfs-site.xml;
		- use Backup Node as a persistent and up-to-date storage of the file system meta-data.
19/10/02 13:39:35 WARN namenode.FSNamesystem: !!! WARNING !!!
	The NameNode currently runs without persistent storage.
	Any changes to the file system meta-data may be lost.
	Recommended actions:
		- shutdown and restart NameNode with configured "dfs.namenode.edits.dir" in hdfs-site.xml;
		- use Backup Node as a persistent and up-to-date storage of the file system meta-data.
19/10/02 13:39:35 WARN namenode.FSNamesystem: !!! WARNING !!!
	The NameNode currently runs without persistent storage.
	Any changes to the file system meta-data may be lost.
	Recommended actions:
		- shutdown and restart NameNode with configured "dfs.namenode.name.dir" in hdfs-site.xml;
		- use Backup Node as a persistent and up-to-date storage of the file system meta-data.
19/10/02 13:39:35 WARN namenode.FSNamesystem: !!! WARNING !!!
	The NameNode currently runs without persistent storage.
	Any changes to the file system meta-data may be lost.
	Recommended actions:
		- shutdown and restart NameNode with configured "dfs.namenode.edits.dir.required" in hdfs-site.xml;
		- use Backup Node as a persistent and up-to-date storage of the file system meta-data.
19/10/02 13:39:35 WARN namenode.FSNamesystem: !!! WARNING !!!
	The NameNode currently runs without persistent storage.
	Any changes to the file system meta-data may be lost.
	Recommended actions:
		- shutdown and restart NameNode with configured "dfs.namenode.name.dir" in hdfs-site.xml;
		- use Backup Node as a persistent and up-to-date storage of the file system meta-data.
19/10/02 13:39:35 WARN namenode.FSNamesystem: !!! WARNING !!!
	The NameNode currently runs without persistent storage.
	Any changes to the file system meta-data may be lost.
	Recommended actions:
		- shutdown and restart NameNode with configured "dfs.namenode.edits.dir" in hdfs-site.xml;
		- use Backup Node as a persistent and up-to-date storage of the file system meta-data.
19/10/02 13:39:35 WARN namenode.FSNamesystem: !!! WARNING !!!
	The NameNode currently runs without persistent storage.
	Any changes to the file system meta-data may be lost.
	Recommended actions:
		- shutdown and restart NameNode with configured "dfs.namenode.name.dir" in hdfs-site.xml;
		- use Backup Node as a persistent and up-to-date storage of the file system meta-data.
19/10/02 13:39:35 INFO namenode.FSNamesystem: No KeyProvider found.
19/10/02 13:39:35 INFO namenode.FSNamesystem: fsLock is fair:true
19/10/02 13:39:36 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
19/10/02 13:39:36 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
19/10/02 13:39:36 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
19/10/02 13:39:36 INFO blockmanagement.BlockManager: The block deletion will start around 2019 十月 02 13:39:36
19/10/02 13:39:36 INFO util.GSet: Computing capacity for map BlocksMap
19/10/02 13:39:36 INFO util.GSet: VM type       = 64-bit
19/10/02 13:39:36 INFO util.GSet: 2.0% max memory 889 MB = 17.8 MB
19/10/02 13:39:36 INFO util.GSet: capacity      = 2^21 = 2097152 entries
19/10/02 13:39:36 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
19/10/02 13:39:36 INFO blockmanagement.BlockManager: defaultReplication         = 3
19/10/02 13:39:36 INFO blockmanagement.BlockManager: maxReplication             = 512
19/10/02 13:39:36 INFO blockmanagement.BlockManager: minReplication             = 1
19/10/02 13:39:36 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
19/10/02 13:39:36 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
19/10/02 13:39:36 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
19/10/02 13:39:36 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
19/10/02 13:39:36 INFO namenode.FSNamesystem: fsOwner             = zozo (auth:SIMPLE)
19/10/02 13:39:36 INFO namenode.FSNamesystem: supergroup          = supergroup
19/10/02 13:39:36 INFO namenode.FSNamesystem: isPermissionEnabled = true
19/10/02 13:39:36 INFO namenode.FSNamesystem: HA Enabled: false
19/10/02 13:39:36 INFO namenode.FSNamesystem: Append Enabled: true
19/10/02 13:39:36 INFO util.GSet: Computing capacity for map INodeMap
19/10/02 13:39:36 INFO util.GSet: VM type       = 64-bit
19/10/02 13:39:36 INFO util.GSet: 1.0% max memory 889 MB = 8.9 MB
19/10/02 13:39:36 INFO util.GSet: capacity      = 2^20 = 1048576 entries
19/10/02 13:39:36 INFO namenode.FSDirectory: ACLs enabled? false
19/10/02 13:39:36 INFO namenode.FSDirectory: XAttrs enabled? true
19/10/02 13:39:36 INFO namenode.FSDirectory: Maximum size of an xattr: 16384
19/10/02 13:39:36 INFO namenode.NameNode: Caching file names occuring more than 10 times
19/10/02 13:39:36 INFO util.GSet: Computing capacity for map cachedBlocks
19/10/02 13:39:36 INFO util.GSet: VM type       = 64-bit
19/10/02 13:39:36 INFO util.GSet: 0.25% max memory 889 MB = 2.2 MB
19/10/02 13:39:36 INFO util.GSet: capacity      = 2^18 = 262144 entries
19/10/02 13:39:36 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
19/10/02 13:39:36 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
19/10/02 13:39:36 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
19/10/02 13:39:36 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.window.num.buckets = 10
19/10/02 13:39:36 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.num.users = 10
19/10/02 13:39:36 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.windows.minutes = 1,5,25
19/10/02 13:39:36 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
19/10/02 13:39:36 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
19/10/02 13:39:36 INFO util.GSet: Computing capacity for map NameNodeRetryCache
19/10/02 13:39:36 INFO util.GSet: VM type       = 64-bit
19/10/02 13:39:36 INFO util.GSet: 0.029999999329447746% max memory 889 MB = 273.1 KB
19/10/02 13:39:36 INFO util.GSet: capacity      = 2^15 = 32768 entries
19/10/02 13:39:36 INFO common.Storage: Lock on /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/namesecondary/in_use.lock acquired by nodename 28708@vm017
19/10/02 13:39:36 WARN namenode.FSNamesystem: !!! WARNING !!!
	The NameNode currently runs without persistent storage.
	Any changes to the file system meta-data may be lost.
	Recommended actions:
		- shutdown and restart NameNode with configured "dfs.namenode.edits.dir.required" in hdfs-site.xml;
		- use Backup Node as a persistent and up-to-date storage of the file system meta-data.
19/10/02 13:39:36 INFO namenode.FileJournalManager: Recovering unfinalized segments in /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/namesecondary/current
19/10/02 13:39:36 INFO namenode.FSImage: No edit log streams selected.
19/10/02 13:39:36 INFO namenode.FSImageFormatPBINode: Loading 7 INodes.
19/10/02 13:39:36 INFO namenode.FSImageFormatProtobuf: Loaded FSImage in 0 seconds.
19/10/02 13:39:36 INFO namenode.FSImage: Loaded image for txid 692 from /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/namesecondary/current/fsimage_0000000000000000692
19/10/02 13:39:36 INFO namenode.FSImage: Save namespace ...
19/10/02 13:39:36 WARN namenode.FSNamesystem: Encountered exception loading fsimage
java.io.IOException: No image directories available!
	at org.apache.hadoop.hdfs.server.namenode.FSImage.saveFSImageInAllDirs(FSImage.java:1150)
	at org.apache.hadoop.hdfs.server.namenode.FSImage.saveNamespace(FSImage.java:1105)
	at org.apache.hadoop.hdfs.server.namenode.FSImage.saveNamespace(FSImage.java:1080)
	at org.apache.hadoop.hdfs.server.namenode.FSImage.doImportCheckpoint(FSImage.java:545)
	at org.apache.hadoop.hdfs.server.namenode.FSImage.recoverTransitionRead(FSImage.java:284)
	at org.apache.hadoop.hdfs.server.namenode.FSNamesystem.loadFSImage(FSNamesystem.java:975)
	at org.apache.hadoop.hdfs.server.namenode.FSNamesystem.loadFromDisk(FSNamesystem.java:681)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.loadNamesystem(NameNode.java:584)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.initialize(NameNode.java:644)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.<init>(NameNode.java:811)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.<init>(NameNode.java:795)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.createNameNode(NameNode.java:1488)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.main(NameNode.java:1554)
19/10/02 13:39:36 INFO mortbay.log: Stopped HttpServer2$SelectChannelConnectorWithSafeStartup@0.0.0.0:50070
19/10/02 13:39:36 INFO impl.MetricsSystemImpl: Stopping NameNode metrics system...
19/10/02 13:39:36 INFO impl.MetricsSystemImpl: NameNode metrics system stopped.
19/10/02 13:39:36 INFO impl.MetricsSystemImpl: NameNode metrics system shutdown complete.
19/10/02 13:39:36 ERROR namenode.NameNode: Failed to start namenode.
java.io.IOException: No image directories available!
	at org.apache.hadoop.hdfs.server.namenode.FSImage.saveFSImageInAllDirs(FSImage.java:1150)
	at org.apache.hadoop.hdfs.server.namenode.FSImage.saveNamespace(FSImage.java:1105)
	at org.apache.hadoop.hdfs.server.namenode.FSImage.saveNamespace(FSImage.java:1080)
	at org.apache.hadoop.hdfs.server.namenode.FSImage.doImportCheckpoint(FSImage.java:545)
	at org.apache.hadoop.hdfs.server.namenode.FSImage.recoverTransitionRead(FSImage.java:284)
	at org.apache.hadoop.hdfs.server.namenode.FSNamesystem.loadFSImage(FSNamesystem.java:975)
	at org.apache.hadoop.hdfs.server.namenode.FSNamesystem.loadFromDisk(FSNamesystem.java:681)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.loadNamesystem(NameNode.java:584)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.initialize(NameNode.java:644)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.<init>(NameNode.java:811)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.<init>(NameNode.java:795)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.createNameNode(NameNode.java:1488)
	at org.apache.hadoop.hdfs.server.namenode.NameNode.main(NameNode.java:1554)
19/10/02 13:39:36 INFO util.ExitUtil: Exiting with status 1
19/10/02 13:39:36 INFO namenode.NameNode: SHUTDOWN_MSG: 
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at vm017/172.16.0.17
************************************************************/
[zozo@vm017 hadoop-2.7.2]$ jps
27158 DataNode
27465 NodeManager
28766 Jps
[zozo@vm017 hadoop-2.7.2]$ 
```

### 4.2.5 启动 NameNode

- `vm017` (NameNode) 上启动 NameNode

(上一步执行失败, 无法启动! `TODO`)

---

# 五 集群安全模式

## 5.1 说明

- NameNode 启动

NameNode 启动时, 首先将 Fsimage (镜像) 载入内存, 并执行 Edits (操作记录日志) 中的各项操作. 一旦在内存中成功建立文件系统元数据的映像, 则创建一个新的 Fsimage 文件和一个空的 Edits (操作记录日志). 此时 NameNode 开始监听 DataNode 请求. 这个过程中, NameNode 一直处于安全模式.

- DataNode 启动

系统中的数据块的位置并不是由 NameNode 维护的, 而是以块列表的形式存储在 DataNode 中. 在系统正常工作期间, NameNode 在内存中保留所有块位置的映射信息. 在安全模式下, 各个 DataNode 会向 NameNode 发送最新的块列表信息, NameNode 了解到足够多的块信息后即可运行文件系统.

- 安全模式退出判断

如果满足 `最小副本条件`, NameNode 会在 30 秒之后就退出安全模式. `最小副本条件` 是指在整个文件系统中 `99.9%` 的块满足最小副本级别 (默认 dfs.replication.min=1).

_注: 在启动一个刚刚格式化的 HDFS 集群时, 因为系统中还没有任何块, 所以 NameNode 不会进入安全模式._

## 5.2 语法

```bash
-safemode <enter|leave|get|wait>

# 进入安全模式
bin/hdfs dfsadmin -safemode enter
# 离开安全模式
bin/hdfs dfsadmin -safemode leave
# 查看安全模式状态
bin/hdfs dfsadmin -safemode get
# 等待安全模式
bin/hdfs dfsadmin -safemode wait
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -help safemode
-safemode <enter|leave|get|wait>:  Safe mode maintenance command.
		Safe mode is a Namenode state in which it
			1.  does not accept changes to the name space (read-only)
			2.  does not replicate or delete blocks.
		Safe mode is entered automatically at Namenode startup, and
		leaves safe mode automatically when the configured minimum
		percentage of blocks satisfies the minimum replication
		condition.  Safe mode can also be entered manually, but then
		it can only be turned off manually as well.

[zozo@vm017 hadoop-2.7.2]$ 
```

## 5.3 使用

- 查看安全模式状态

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -safemode get
Safe mode is OFF
[zozo@vm017 hadoop-2.7.2]$ 
```

HDFS 控制台 URL: http://193.112.38.200:50070 查看状态如下:

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-NameNode%E5%92%8CSecondaryNameNode/safemode-1.png?raw=true)

- 进入安全模式

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -safemode enter
Safe mode is ON
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -safemode get
Safe mode is ON
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /
drwxr-xr-x   - zozo supergroup          0 2019-10-01 21:50 /d2
drwxr-xr-x   - zozo supergroup          0 2019-09-28 18:47 /d2/d2_a
-rw-r--r--   3 zozo supergroup          8 2019-09-28 17:29 /d2/d2_a/f1
-rw-r--r--   2 zozo supergroup          8 2019-09-28 18:03 /d2/d2_a/f2_rename
-rw-r--r--   3 zozo supergroup  212046774 2019-09-25 20:04 /hadoop-2.7.2.tar.gz
-rw-r--r--   3 zozo supergroup         36 2019-09-25 20:04 /wc.input
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -mkdir /d3
mkdir: Cannot create directory /d3. Name node is in safe mode.
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -put /home/zozo/app/hadoop/fortest/f1 /
put: Cannot create file/f2._COPYING_. Name node is in safe mode.
[zozo@vm017 hadoop-2.7.2]$ 
```

HDFS 控制台 URL: http://193.112.38.200:50070 查看状态如下:

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-NameNode%E5%92%8CSecondaryNameNode/safemode-2.png?raw=true)

- 离开安全模式状态

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -safemode leave
Safe mode is OFF
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -safemode get
Safe mode is OFF
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /
drwxr-xr-x   - zozo supergroup          0 2019-10-01 21:50 /d2
drwxr-xr-x   - zozo supergroup          0 2019-09-28 18:47 /d2/d2_a
-rw-r--r--   3 zozo supergroup          8 2019-09-28 17:29 /d2/d2_a/f1
-rw-r--r--   2 zozo supergroup          8 2019-09-28 18:03 /d2/d2_a/f2_rename
-rw-r--r--   3 zozo supergroup  212046774 2019-09-25 20:04 /hadoop-2.7.2.tar.gz
-rw-r--r--   3 zozo supergroup         36 2019-09-25 20:04 /wc.input
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -mkdir /d3
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -put /home/zozo/app/hadoop/fortest/f1 /
[zozo@vm017 hadoop-2.7.2]$ 
```

HDFS 控制台 URL: http://193.112.38.200:50070 查看状态如下:

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-NameNode%E5%92%8CSecondaryNameNode/safemode-3.png?raw=true)

- 等待安全模式

```bash
# 开启 safe mode
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -safemode enter
Safe mode is ON
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -safemode get
Safe mode is ON
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /f2
ls: /f2: No such file or directory

# 执行 safemode_wait.sh 脚本, 此时脚本被阻塞 (等待 safe mode 关闭)
[zozo@vm017 fortest]$ cat /home/zozo/app/hadoop/fortest/safemode_wait.sh 
#!/bin/sh

cd /home/zozo/app/hadoop/hadoop-2.7.2/

/home/zozo/app/hadoop/hadoop-2.7.2/bin/hdfs dfsadmin -safemode wait

# waiting ...

/home/zozo/app/hadoop/hadoop-2.7.2/bin/hadoop fs -put /home/zozo/app/hadoop/fortest/f2 /

[zozo@vm017 fortest]$ sh safemode_wait.sh 

```

```bash
# 离开 safe mode
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -safemode leave
Safe mode is OFF
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -safemode get
Safe mode is OFF
[zozo@vm017 hadoop-2.7.2]$ 
```

```bash
# 此时 safemode_wait.sh 脚本执行完毕
[zozo@vm017 fortest]$ sh safemode_wait.sh 
Safe mode is OFF
# 此时文件上传到 HDFS 成功
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /f2
-rw-r--r--   3 zozo supergroup          8 2019-10-02 15:13 /f2
[zozo@vm017 hadoop-2.7.2]$ 
```

---

# 六 NameNode 多目录设置

NameNode 的本地数据存储目录可以配置成多个, 每个目录存放的内容相同, 增加可靠性.

## 6.1 配置

在 `./etc/hadoop/hdfs-site.xml` 中增加如下配置:

```xml
<!-- namenode 元数据存储目录 -->
<property>
  <name>dfs.namenode.name.dir</name>
  <value>file://${hadoop.tmp.dir}/dfs/name1,file://${hadoop.tmp.dir}/dfs/name2,file://${hadoop.tmp.dir}/dfs/name3</value>
  <description>Determines where on the local filesystem the DFS name node
      should store the name table(fsimage).  If this is a comma-delimited list
      of directories then the name table is replicated in all of the
      directories, for redundancy. </description>
  <!--
  <description>确定 DFS 名称节点在 local filesystem 上应该存储名称表 (fsimage) 的位置.
  如果这是用逗号分隔的目录列表, 则将名称表复制到所有目录中, 以实现冗余. </description>
  -->
</property>
```

## 6.2 停止集群并删除数据 (可选)

如果集群非首次启动, 需要停止集群, 删除所有 data, logs 等数据

## 6.3 格式化集群

格式化集群

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs namenode -format
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs namenode -format
19/10/02 15:51:44 INFO namenode.NameNode: STARTUP_MSG: 
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   host = vm017/172.16.0.17
STARTUP_MSG:   args = [-format]
STARTUP_MSG:   version = 2.7.2
STARTUP_MSG:   classpath = /home/zozo/app/hadoop/hadoop-2.7.2/etc/hadoop:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpcore-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-client-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/mockito-all-1.8.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-auth-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-core-1.8.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-recipes-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-kerberos-codec-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/java-xmlbuilder-0.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-httpclient-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-1.7.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsp-api-2.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-util-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-net-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-digester-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jets3t-0.9.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpclient-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/gson-2.2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-api-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-math3-3.1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsch-0.1.42.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-asn1-api-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-configuration-1.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-framework-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xml-apis-1.3.04.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-daemon-1.0.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xercesImpl-2.9.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-all-4.0.23.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-client-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-web-proxy-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-nodemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-resourcemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-tests-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-sharedcachemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-registry-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-applicationhistoryservice-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-client-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-api-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-shuffle-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-plugins-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-app-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/contrib/capacity-scheduler/*.jar
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r b165c4fe8a74265c792ce23f546c64604acf0e41; compiled by 'jenkins' on 2016-01-26T00:08Z
STARTUP_MSG:   java = 1.8.0_192
************************************************************/
19/10/02 15:51:44 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
19/10/02 15:51:44 INFO namenode.NameNode: createNameNode [-format]
Formatting using clusterid: CID-3ee4f6a7-9905-4ff8-9589-d03537ec3674
19/10/02 15:51:44 INFO namenode.FSNamesystem: No KeyProvider found.
19/10/02 15:51:44 INFO namenode.FSNamesystem: fsLock is fair:true
19/10/02 15:51:44 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
19/10/02 15:51:44 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
19/10/02 15:51:44 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
19/10/02 15:51:44 INFO blockmanagement.BlockManager: The block deletion will start around 2019 十月 02 15:51:44
19/10/02 15:51:44 INFO util.GSet: Computing capacity for map BlocksMap
19/10/02 15:51:44 INFO util.GSet: VM type       = 64-bit
19/10/02 15:51:44 INFO util.GSet: 2.0% max memory 889 MB = 17.8 MB
19/10/02 15:51:44 INFO util.GSet: capacity      = 2^21 = 2097152 entries
19/10/02 15:51:44 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
19/10/02 15:51:44 INFO blockmanagement.BlockManager: defaultReplication         = 3
19/10/02 15:51:44 INFO blockmanagement.BlockManager: maxReplication             = 512
19/10/02 15:51:44 INFO blockmanagement.BlockManager: minReplication             = 1
19/10/02 15:51:44 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
19/10/02 15:51:44 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
19/10/02 15:51:44 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
19/10/02 15:51:44 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
19/10/02 15:51:44 INFO namenode.FSNamesystem: fsOwner             = zozo (auth:SIMPLE)
19/10/02 15:51:44 INFO namenode.FSNamesystem: supergroup          = supergroup
19/10/02 15:51:44 INFO namenode.FSNamesystem: isPermissionEnabled = true
19/10/02 15:51:44 INFO namenode.FSNamesystem: HA Enabled: false
19/10/02 15:51:44 INFO namenode.FSNamesystem: Append Enabled: true
19/10/02 15:51:45 INFO util.GSet: Computing capacity for map INodeMap
19/10/02 15:51:45 INFO util.GSet: VM type       = 64-bit
19/10/02 15:51:45 INFO util.GSet: 1.0% max memory 889 MB = 8.9 MB
19/10/02 15:51:45 INFO util.GSet: capacity      = 2^20 = 1048576 entries
19/10/02 15:51:45 INFO namenode.FSDirectory: ACLs enabled? false
19/10/02 15:51:45 INFO namenode.FSDirectory: XAttrs enabled? true
19/10/02 15:51:45 INFO namenode.FSDirectory: Maximum size of an xattr: 16384
19/10/02 15:51:45 INFO namenode.NameNode: Caching file names occuring more than 10 times
19/10/02 15:51:45 INFO util.GSet: Computing capacity for map cachedBlocks
19/10/02 15:51:45 INFO util.GSet: VM type       = 64-bit
19/10/02 15:51:45 INFO util.GSet: 0.25% max memory 889 MB = 2.2 MB
19/10/02 15:51:45 INFO util.GSet: capacity      = 2^18 = 262144 entries
19/10/02 15:51:45 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
19/10/02 15:51:45 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
19/10/02 15:51:45 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
19/10/02 15:51:45 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.window.num.buckets = 10
19/10/02 15:51:45 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.num.users = 10
19/10/02 15:51:45 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.windows.minutes = 1,5,25
19/10/02 15:51:45 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
19/10/02 15:51:45 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
19/10/02 15:51:45 INFO util.GSet: Computing capacity for map NameNodeRetryCache
19/10/02 15:51:45 INFO util.GSet: VM type       = 64-bit
19/10/02 15:51:45 INFO util.GSet: 0.029999999329447746% max memory 889 MB = 273.1 KB
19/10/02 15:51:45 INFO util.GSet: capacity      = 2^15 = 32768 entries
19/10/02 15:51:45 INFO namenode.FSImage: Allocated new BlockPoolId: BP-1121114602-172.16.0.17-1570002705071
19/10/02 15:51:45 INFO common.Storage: Storage directory /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name1 has been successfully formatted.
19/10/02 15:51:45 INFO common.Storage: Storage directory /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name2 has been successfully formatted.
19/10/02 15:51:45 INFO common.Storage: Storage directory /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name3 has been successfully formatted.
19/10/02 15:51:45 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
19/10/02 15:51:45 INFO util.ExitUtil: Exiting with status 0
19/10/02 15:51:45 INFO namenode.NameNode: SHUTDOWN_MSG: 
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at vm017/172.16.0.17
************************************************************/
[zozo@vm017 hadoop-2.7.2]$ 
```

此时 NameNode 数据目录已经建立, 且三个目录下的文件内容相同:

```
[zozo@vm017 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs
总用量 12
drwxrwxr-x 3 zozo zozo 4096 10月  2 15:51 name1
drwxrwxr-x 3 zozo zozo 4096 10月  2 15:51 name2
drwxrwxr-x 3 zozo zozo 4096 10月  2 15:51 name3
[zozo@vm017 dfs]$ ll name1/current/
总用量 16
-rw-rw-r-- 1 zozo zozo 351 10月  2 15:51 fsimage_0000000000000000000
-rw-rw-r-- 1 zozo zozo  62 10月  2 15:51 fsimage_0000000000000000000.md5
-rw-rw-r-- 1 zozo zozo   2 10月  2 15:51 seen_txid
-rw-rw-r-- 1 zozo zozo 204 10月  2 15:51 VERSION
[zozo@vm017 dfs]$ ll name2/current/
总用量 16
-rw-rw-r-- 1 zozo zozo 351 10月  2 15:51 fsimage_0000000000000000000
-rw-rw-r-- 1 zozo zozo  62 10月  2 15:51 fsimage_0000000000000000000.md5
-rw-rw-r-- 1 zozo zozo   2 10月  2 15:51 seen_txid
-rw-rw-r-- 1 zozo zozo 204 10月  2 15:51 VERSION
[zozo@vm017 dfs]$ ll name3/current/
总用量 16
-rw-rw-r-- 1 zozo zozo 351 10月  2 15:51 fsimage_0000000000000000000
-rw-rw-r-- 1 zozo zozo  62 10月  2 15:51 fsimage_0000000000000000000.md5
-rw-rw-r-- 1 zozo zozo   2 10月  2 15:51 seen_txid
-rw-rw-r-- 1 zozo zozo 204 10月  2 15:51 VERSION
[zozo@vm017 dfs]$ 
```

## 6.4 启动集群

```
[zozo@vm017 hadoop-2.7.2]$ sbin/start-dfs.sh
```

```
[zozo@vm017 hadoop-2.7.2]$ sbin/start-dfs.sh
Starting namenodes on [vm017]
vm017: starting namenode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-namenode-vm017.out
vm03: starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm03.out
vm017: starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm017.out
vm06: starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm06.out
Starting secondary namenodes [vm06]
vm06: starting secondarynamenode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-secondarynamenode-vm06.out
[zozo@vm017 hadoop-2.7.2]$ 
```

```
[zozo@vm03 hadoop-2.7.2]$ sbin/start-yarn.sh
starting yarn daemons
starting resourcemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-resourcemanager-vm03.out
vm017: starting nodemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-nodemanager-vm017.out
vm06: starting nodemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-nodemanager-vm06.out
vm03: starting nodemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-nodemanager-vm03.out
[zozo@vm03 hadoop-2.7.2]$ 
```

后续 NameNode 的三个数据目录下的文件内容会一直保持相同.

---
