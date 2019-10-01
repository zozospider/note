
- [一 NameNode 和 SecondaryNameNode 工作机制](#一-namenode-和-secondarynamenode-工作机制)
- [二 Fsimage 和 Edis 解析](#二-fsimage-和-edis-解析)
    - [2.1 查看 Fsimage 和 Edits](#21-查看-fsimage-和-edits)
        - [2.1.1 查看 Fsimage](#211-查看-fsimage)
        - [2.1.2 查看 Edits](#212-查看-edits)
    - [2.2 操作测试](#22-操作测试)
    - [2.3 操作测试 - 每小时滚动前 - Fsimage 和 Edis 文件存储情况](#23-操作测试---每小时滚动前---fsimage-和-edis-文件存储情况)
    - [2.4 操作测试 - 每小时滚动前 - 查看 Fsimage 内容](#24-操作测试---每小时滚动前---查看-fsimage-内容)
    - [2.5 操作测试 - 每小时滚动前 - 查看 Edits 内容](#25-操作测试---每小时滚动前---查看-edits-内容)
    - [2.6 操作测试 - 每小时滚动后 - Fsimage 和 Edis 文件存储情况](#26-操作测试---每小时滚动后---fsimage-和-edis-文件存储情况)
    - [2.7 操作测试 - 每小时滚动后 - 查看 Fsimage 内容](#27-操作测试---每小时滚动后---查看-fsimage-内容)
    - [2.8 操作测试 - 每小时滚动后 - 查看 Edits 内容](#28-操作测试---每小时滚动后---查看-edits-内容)
- [三 CheckPoint 时间设置](#三-checkpoint-时间设置)
- [四 NameNode 故障处理](#四-namenode-故障处理)
- [五 集群安全模式](#五-集群安全模式)
- [六 NameNode 多目录设置](#六-namenode-多目录设置)

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

NameNode 的 data 目录 (`{}/dfs/name/current`) 下存储了多个 Fsimage 和 Edits 文件. 可以通过 `hdfs oiv` 和 `hdfs oev` 命令将 Fsimage 和 Edits 文件内容转换成指定格式的可读文件.

其中最近的一个 `{}/dfs/name/current/fsimage_xxx` 文件保存了每小时滚动前的 HDFS 所有元数据, `{}/dfs/name/current/edits_inprogress_xxx` 文件保存了当前小时的操作记录. 每次小时滚动时, 会将 `edits_inprogress_xxx` 文件内的操作记录合并到新建的最新的 `fsimage_xxx` 文件中, 然后将重新产生一个新的 `{}/dfs/name/current/edits_inprogress_xxx` 文件继续保存下一个小时的操作记录.

## 2.1 查看 Fsimage 和 Edits

### 2.1.1 查看 Fsimage

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

### 2.1.2 查看 Edits

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

## 2.2 操作测试

在 __每小时滚动前__, 在 HDFS 上执行删除操作, 将 `/d2/d2_c` 和 `/d2/d2_d` 两个文件夹 (包含子文件) 删除.

然后在执行操作后的 __每小时滚动前__ 和 __每小时滚动后__ 分别观察 Fsimage 和 Edis 文件变化情况, 如下:

## 2.3 操作测试 - 每小时滚动前 - Fsimage 和 Edis 文件存储情况

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

执行以下命令将 `fsimage_0000000000000000659` 文件转换成 XML 格式的 `fsimage_0000000000000000659_viewer` 可视化文件:

```bash
# [zozo@vm017 current]$ hdfs oiv -p XML -i fsimage_0000000000000000659 -o fsimage_0000000000000000659_viewer
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs oiv -p XML -i /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current/fsimage_0000000000000000659 -o /home/zozo/app/hadoop/fortest/fsimage_0000000000000000659_viewer
[zozo@vm017 hadoop-2.7.2]$ 
```

格式化之后的 `fsimage_0000000000000000659_viewer` 文件内容中包含了近乎所有节点信息 (最近操作的除外), 如下所示 (此时包含 hdfs 中 `/d2/d2_c` 和 `/d2/d2_b` 这两个文件夹):

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

执行以下命令将 `fsimage_0000000000000000663` 文件转换成 XML 格式的 `fsimage_0000000000000000663_viewer` 可视化文件:

```bash
# [zozo@vm017 current]$ hdfs oiv -p XML -i fsimage_0000000000000000663 -o fsimage_0000000000000000663_viewer
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs oiv -p XML -i /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current/fsimage_0000000000000000663 -o /home/zozo/app/hadoop/fortest/fsimage_0000000000000000663_viewer
[zozo@vm017 hadoop-2.7.2]$ 
```

格式化之后的 `fsimage_0000000000000000663_viewer` 文件内容中包含了近乎所有节点信息 (最近操作的除外), 如下所示 (此时 HDFS 中 `/d2/d2_c` 和 `/d2/d2_b` 这两个文件夹已经不存在了):

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

---

# 四 NameNode 故障处理

---

# 五 集群安全模式

---

# 六 NameNode 多目录设置

---
