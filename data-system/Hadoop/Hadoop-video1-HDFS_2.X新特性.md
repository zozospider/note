


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

![image]()

---

# 三 回收站

---

# 四 快照管理

---
