
- [一. 基本语法](#一-基本语法)
- [二. 常用命令](#二-常用命令)
    - [-help](#-help)
    - [-mkdir](#-mkdir)
    - [-moveFromLocal](#-movefromlocal)

---

# 一. 基本语法

以下两个命令都可以对 HDFS 进行操作:

```bash
bin/hadoop fs ...
bin/hdfs dfs ...
```

```bash
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs
Usage: hadoop fs [generic options]
	[-appendToFile <localsrc> ... <dst>]
	[-cat [-ignoreCrc] <src> ...]
	[-checksum <src> ...]
	[-chgrp [-R] GROUP PATH...]
	[-chmod [-R] <MODE[,MODE]... | OCTALMODE> PATH...]
	[-chown [-R] [OWNER][:[GROUP]] PATH...]
	[-copyFromLocal [-f] [-p] [-l] <localsrc> ... <dst>]
	[-copyToLocal [-p] [-ignoreCrc] [-crc] <src> ... <localdst>]
	[-count [-q] [-h] <path> ...]
	[-cp [-f] [-p | -p[topax]] <src> ... <dst>]
	[-createSnapshot <snapshotDir> [<snapshotName>]]
	[-deleteSnapshot <snapshotDir> <snapshotName>]
	[-df [-h] [<path> ...]]
	[-du [-s] [-h] <path> ...]
	[-expunge]
	[-find <path> ... <expression> ...]
	[-get [-p] [-ignoreCrc] [-crc] <src> ... <localdst>]
	[-getfacl [-R] <path>]
	[-getfattr [-R] {-n name | -d} [-e en] <path>]
	[-getmerge [-nl] <src> <localdst>]
	[-help [cmd ...]]
	[-ls [-d] [-h] [-R] [<path> ...]]
	[-mkdir [-p] <path> ...]
	[-moveFromLocal <localsrc> ... <dst>]
	[-moveToLocal <src> <localdst>]
	[-mv <src> ... <dst>]
	[-put [-f] [-p] [-l] <localsrc> ... <dst>]
	[-renameSnapshot <snapshotDir> <oldName> <newName>]
	[-rm [-f] [-r|-R] [-skipTrash] <src> ...]
	[-rmdir [--ignore-fail-on-non-empty] <dir> ...]
	[-setfacl [-R] [{-b|-k} {-m|-x <acl_spec>} <path>]|[--set <acl_spec> <path>]]
	[-setfattr {-n name [-v value] | -x name} <path>]
	[-setrep [-R] [-w] <rep> <path> ...]
	[-stat [format] <path> ...]
	[-tail [-f] <file>]
	[-test -[defsz] <path>]
	[-text [-ignoreCrc] <src> ...]
	[-touchz <path> ...]
	[-truncate [-w] <length> <path> ...]
	[-usage [cmd ...]]

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

[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs
Usage: hadoop fs [generic options]
	[-appendToFile <localsrc> ... <dst>]
	[-cat [-ignoreCrc] <src> ...]
	[-checksum <src> ...]
	[-chgrp [-R] GROUP PATH...]
	[-chmod [-R] <MODE[,MODE]... | OCTALMODE> PATH...]
	[-chown [-R] [OWNER][:[GROUP]] PATH...]
	[-copyFromLocal [-f] [-p] [-l] <localsrc> ... <dst>]
	[-copyToLocal [-p] [-ignoreCrc] [-crc] <src> ... <localdst>]
	[-count [-q] [-h] <path> ...]
	[-cp [-f] [-p | -p[topax]] <src> ... <dst>]
	[-createSnapshot <snapshotDir> [<snapshotName>]]
	[-deleteSnapshot <snapshotDir> <snapshotName>]
	[-df [-h] [<path> ...]]
	[-du [-s] [-h] <path> ...]
	[-expunge]
	[-find <path> ... <expression> ...]
	[-get [-p] [-ignoreCrc] [-crc] <src> ... <localdst>]
	[-getfacl [-R] <path>]
	[-getfattr [-R] {-n name | -d} [-e en] <path>]
	[-getmerge [-nl] <src> <localdst>]
	[-help [cmd ...]]
	[-ls [-d] [-h] [-R] [<path> ...]]
	[-mkdir [-p] <path> ...]
	[-moveFromLocal <localsrc> ... <dst>]
	[-moveToLocal <src> <localdst>]
	[-mv <src> ... <dst>]
	[-put [-f] [-p] [-l] <localsrc> ... <dst>]
	[-renameSnapshot <snapshotDir> <oldName> <newName>]
	[-rm [-f] [-r|-R] [-skipTrash] <src> ...]
	[-rmdir [--ignore-fail-on-non-empty] <dir> ...]
	[-setfacl [-R] [{-b|-k} {-m|-x <acl_spec>} <path>]|[--set <acl_spec> <path>]]
	[-setfattr {-n name [-v value] | -x name} <path>]
	[-setrep [-R] [-w] <rep> <path> ...]
	[-stat [format] <path> ...]
	[-tail [-f] <file>]
	[-test -[defsz] <path>]
	[-text [-ignoreCrc] <src> ...]
	[-touchz <path> ...]
	[-truncate [-w] <length> <path> ...]
	[-usage [cmd ...]]

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

[zozo@vm017 hadoop-2.7.2]$ 
```

---

# 二. 常用命令

## -help

```bash
# 输出指定命令参数的帮助文档
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop -help /HDFS_CMD
```

Demo:

```bash
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop -help ls
Usage: hadoop [--config confdir] [COMMAND | CLASSNAME]
  CLASSNAME            run the class named CLASSNAME
 or
  where COMMAND is one of:
  fs                   run a generic filesystem user client
  version              print the version
  jar <jar>            run a jar file
                       note: please use "yarn jar" to launch
                             YARN applications, not this command.
  checknative [-a|-h]  check native hadoop and compression libraries availability
  distcp <srcurl> <desturl> copy file or directories recursively
  archive -archiveName NAME -p <parent path> <src>* <dest> create a hadoop archive
  classpath            prints the class path needed to get the
  credential           interact with credential providers
                       Hadoop jar and the required libraries
  daemonlog            get/set the log level for each daemon
  trace                view and modify Hadoop tracing settings

Most commands print help when invoked w/o parameters.
[zozo@vm017 hadoop-2.7.2]$ 
```

 ## -ls

```bash
# 显示目录信息
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /HDFS_PATH
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help ls
-ls [-d] [-h] [-R] [<path> ...] :
  List the contents that match the specified file pattern. If path is not
  specified, the contents of /user/<currentUser> will be listed. Directory entries
  are of the form:
  	permissions - userId groupId sizeOfDirectory(in bytes)
  modificationDate(yyyy-MM-dd HH:mm) directoryName

  and file entries are of the form:
  	permissions numberOfReplicas userId groupId sizeOfFile(in bytes)
  modificationDate(yyyy-MM-dd HH:mm) fileName

  -d  Directories are listed as plain files.
  -h  Formats the sizes of files in a human-readable fashion rather than a number
      of bytes.
  -R  Recursively list the contents of directories.
[zozo@vm017 hadoop-2.7.2]$ 
```

DEMO:

```bash
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /
Found 2 items
-rw-r--r--   3 zozo supergroup  212046774 2019-09-25 20:04 /hadoop-2.7.2.tar.gz
-rw-r--r--   3 zozo supergroup         36 2019-09-25 20:04 /wc.input
[zozo@vm017 hadoop-2.7.2]$ 
```

## -mkdir

```bash
# 在 HDFS 上创建目录
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -mkdir -p /HDFS_PATH
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help mkdir
-mkdir [-p] <path> ... :
  Create a directory in specified location.

  -p  Do not fail if the directory already exists
[zozo@vm017 hadoop-2.7.2]$ 
```

DEMO:

```bash
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -mkdir -p /d1/d1_a
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /
Found 3 items
drwxr-xr-x   - zozo supergroup          0 2019-09-27 13:51 /d1
-rw-r--r--   3 zozo supergroup  212046774 2019-09-25 20:04 /hadoop-2.7.2.tar.gz
-rw-r--r--   3 zozo supergroup         36 2019-09-25 20:04 /wc.input
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /
drwxr-xr-x   - zozo supergroup          0 2019-09-27 13:51 /d1
drwxr-xr-x   - zozo supergroup          0 2019-09-27 13:51 /d1/d1_a
-rw-r--r--   3 zozo supergroup  212046774 2019-09-25 20:04 /hadoop-2.7.2.tar.gz
-rw-r--r--   3 zozo supergroup         36 2019-09-25 20:04 /wc.input
[zozo@vm017 hadoop-2.7.2]$ 
```

## -moveFromLocal

```bash
# 从本地剪切到 HDFS
bin/hadoop fs -moveFromLocal /LOCAL_PATH/local_file /HDFS_PATH
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help moveFromLocal
-moveFromLocal <localsrc> ... <dst> :
  Same as -put, except that the source is deleted after it's copied.
[zozo@vm017 hadoop-2.7.2]$
```

DEMO:

```bash
[zozo@vm017 hadoop-2.7.2]$ ll /home/zozo/app/hadoop/fortest/f1
-rw-rw-r-- 1 zozo zozo 8 9月  27 13:55 /home/zozo/app/hadoop/fortest/f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -moveFromLocal /home/zozo/app/hadoop/fortest/f1 /d1/d1_a/
[zozo@vm017 hadoop-2.7.2]$ ll /home/zozo/app/hadoop/fortest/f1
ls: 无法访问/home/zozo/app/hadoop/fortest/f1: 没有那个文件或目录
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/
Found 1 items
-rw-r--r--   3 zozo supergroup          8 2019-09-27 13:57 /d1/d1_a/f1
[zozo@vm017 hadoop-2.7.2]$ 
```

---
