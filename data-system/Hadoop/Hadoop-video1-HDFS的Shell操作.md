
- [一. 基本语法](#一-基本语法)
- [二. 常用命令](#二-常用命令)
    - [-help](#-help)
    - [-mkdir](#-mkdir)
    - [-moveFromLocal](#-movefromlocal)
    - [-cat](#-cat)
    - [-appendToFile](#-appendtofile)
    - [-chgrp, -chmod, -chown](#-chgrp--chmod--chown)
    - [-put / -copyFromLocal](#-put---copyfromlocal)
    - [-get / -copyToLocal](#-get---copytolocal)
    - [-getmerge](#-getmerge)
    - [-cp](#-cp)
    - [-mv](#-mv)
    - [-tail](#-tail)
    - [-rm](#-rm)
    - [-rmdir](#-rmdir)
    - [-du](#-du)
    - [-setrep](#-setrep)

---

# 一. 基本语法

以下两个命令都可以对 HDFS 进行操作:

```
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

- 说明

```bash
# 输出指定 HDFS 命令参数的帮助文档
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help HDFS_CMD
```

- Demo

```bash
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

- 通过以下命令可查看所有说明

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help
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

-appendToFile <localsrc> ... <dst> :
  Appends the contents of all the given local files to the given dst file. The dst
  file will be created if it does not exist. If <localSrc> is -, then the input is
  read from stdin.

-cat [-ignoreCrc] <src> ... :
  Fetch all files that match the file pattern <src> and display their content on
  stdout.

-checksum <src> ... :
  Dump checksum information for files that match the file pattern <src> to stdout.
  Note that this requires a round-trip to a datanode storing each block of the
  file, and thus is not efficient to run on a large number of files. The checksum
  of a file depends on its content, block size and the checksum algorithm and
  parameters used for creating the file.

-chgrp [-R] GROUP PATH... :
  This is equivalent to -chown ... :GROUP ...

-chmod [-R] <MODE[,MODE]... | OCTALMODE> PATH... :
  Changes permissions of a file. This works similar to the shell's chmod command
  with a few exceptions.
                                                                                 
  -R           modifies the files recursively. This is the only option currently 
               supported.                                                        
  <MODE>       Mode is the same as mode used for the shell's command. The only   
               letters recognized are 'rwxXt', e.g. +t,a+r,g-w,+rwx,o=r.         
  <OCTALMODE>  Mode specifed in 3 or 4 digits. If 4 digits, the first may be 1 or
               0 to turn the sticky bit on or off, respectively.  Unlike the     
               shell command, it is not possible to specify only part of the     
               mode, e.g. 754 is same as u=rwx,g=rx,o=r.                         
  
  If none of 'augo' is specified, 'a' is assumed and unlike the shell command, no
  umask is applied.

-chown [-R] [OWNER][:[GROUP]] PATH... :
  Changes owner and group of a file. This is similar to the shell's chown command
  with a few exceptions.
                                                                                 
  -R  modifies the files recursively. This is the only option currently          
      supported.                                                                 
  
  If only the owner or group is specified, then only the owner or group is
  modified. The owner and group names may only consist of digits, alphabet, and
  any of [-_./@a-zA-Z0-9]. The names are case sensitive.
  
  WARNING: Avoid using '.' to separate user name and group though Linux allows it.
  If user names have dots in them and you are using local file system, you might
  see surprising results since the shell command 'chown' is used for local files.

-copyFromLocal [-f] [-p] [-l] <localsrc> ... <dst> :
  Identical to the -put command.

-copyToLocal [-p] [-ignoreCrc] [-crc] <src> ... <localdst> :
  Identical to the -get command.

-count [-q] [-h] <path> ... :
  Count the number of directories, files and bytes under the paths
  that match the specified file pattern.  The output columns are:
  DIR_COUNT FILE_COUNT CONTENT_SIZE FILE_NAME or
  QUOTA REMAINING_QUOTA SPACE_QUOTA REMAINING_SPACE_QUOTA 
        DIR_COUNT FILE_COUNT CONTENT_SIZE FILE_NAME
  The -h option shows file sizes in human readable format.

-cp [-f] [-p | -p[topax]] <src> ... <dst> :
  Copy files that match the file pattern <src> to a destination.  When copying
  multiple files, the destination must be a directory. Passing -p preserves status
  [topax] (timestamps, ownership, permission, ACLs, XAttr). If -p is specified
  with no <arg>, then preserves timestamps, ownership, permission. If -pa is
  specified, then preserves permission also because ACL is a super-set of
  permission. Passing -f overwrites the destination if it already exists. raw
  namespace extended attributes are preserved if (1) they are supported (HDFS
  only) and, (2) all of the source and target pathnames are in the /.reserved/raw
  hierarchy. raw namespace xattr preservation is determined solely by the presence
  (or absence) of the /.reserved/raw prefix and not by the -p option.

-createSnapshot <snapshotDir> [<snapshotName>] :
  Create a snapshot on a directory

-deleteSnapshot <snapshotDir> <snapshotName> :
  Delete a snapshot from a directory

-df [-h] [<path> ...] :
  Shows the capacity, free and used space of the filesystem. If the filesystem has
  multiple partitions, and no path to a particular partition is specified, then
  the status of the root partitions will be shown.
                                                                                 
  -h  Formats the sizes of files in a human-readable fashion rather than a number
      of bytes.                                                                  

-du [-s] [-h] <path> ... :
  Show the amount of space, in bytes, used by the files that match the specified
  file pattern. The following flags are optional:
                                                                                 
  -s  Rather than showing the size of each individual file that matches the      
      pattern, shows the total (summary) size.                                   
  -h  Formats the sizes of files in a human-readable fashion rather than a number
      of bytes.                                                                  
  
  Note that, even without the -s option, this only shows size summaries one level
  deep into a directory.
  
  The output is in the form 
  	size	name(full path)

-expunge :
  Empty the Trash

-find <path> ... <expression> ... :
  Finds all files that match the specified expression and
  applies selected actions to them. If no <path> is specified
  then defaults to the current working directory. If no
  expression is specified then defaults to -print.
  
  The following primary expressions are recognised:
    -name pattern
    -iname pattern
      Evaluates as true if the basename of the file matches the
      pattern using standard file system globbing.
      If -iname is used then the match is case insensitive.
  
    -print
    -print0
      Always evaluates to true. Causes the current pathname to be
      written to standard output followed by a newline. If the -print0
      expression is used then an ASCII NULL character is appended rather
      than a newline.
  
  The following operators are recognised:
    expression -a expression
    expression -and expression
    expression expression
      Logical AND operator for joining two expressions. Returns
      true if both child expressions return true. Implied by the
      juxtaposition of two expressions and so does not need to be
      explicitly specified. The second expression will not be
      applied if the first fails.

-get [-p] [-ignoreCrc] [-crc] <src> ... <localdst> :
  Copy files that match the file pattern <src> to the local name.  <src> is kept. 
  When copying multiple files, the destination must be a directory. Passing -p
  preserves access and modification times, ownership and the mode.

-getfacl [-R] <path> :
  Displays the Access Control Lists (ACLs) of files and directories. If a
  directory has a default ACL, then getfacl also displays the default ACL.
                                                                  
  -R      List the ACLs of all files and directories recursively. 
  <path>  File or directory to list.                              

-getfattr [-R] {-n name | -d} [-e en] <path> :
  Displays the extended attribute names and values (if any) for a file or
  directory.
                                                                                 
  -R             Recursively list the attributes for all files and directories.  
  -n name        Dump the named extended attribute value.                        
  -d             Dump all extended attribute values associated with pathname.    
  -e <encoding>  Encode values after retrieving them.Valid encodings are "text", 
                 "hex", and "base64". Values encoded as text strings are enclosed
                 in double quotes ("), and values encoded as hexadecimal and     
                 base64 are prefixed with 0x and 0s, respectively.               
  <path>         The file or directory.                                          

-getmerge [-nl] <src> <localdst> :
  Get all the files in the directories that match the source file pattern and
  merge and sort them to only one file on local fs. <src> is kept.
                                                        
  -nl  Add a newline character at the end of each file. 

-help [cmd ...] :
  Displays help for given command or all commands if none is specified.

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

-mkdir [-p] <path> ... :
  Create a directory in specified location.
                                                  
  -p  Do not fail if the directory already exists 

-moveFromLocal <localsrc> ... <dst> :
  Same as -put, except that the source is deleted after it's copied.

-moveToLocal <src> <localdst> :
  Not implemented yet

-mv <src> ... <dst> :
  Move files that match the specified file pattern <src> to a destination <dst>. 
  When moving multiple files, the destination must be a directory.

-put [-f] [-p] [-l] <localsrc> ... <dst> :
  Copy files from the local file system into fs. Copying fails if the file already
  exists, unless the -f flag is given.
  Flags:
                                                                       
  -p  Preserves access and modification times, ownership and the mode. 
  -f  Overwrites the destination if it already exists.                 
  -l  Allow DataNode to lazily persist the file to disk. Forces        
         replication factor of 1. This flag will result in reduced
         durability. Use with care.

-renameSnapshot <snapshotDir> <oldName> <newName> :
  Rename a snapshot from oldName to newName

-rm [-f] [-r|-R] [-skipTrash] <src> ... :
  Delete all files that match the specified file pattern. Equivalent to the Unix
  command "rm <src>"
                                                                                 
  -skipTrash  option bypasses trash, if enabled, and immediately deletes <src>   
  -f          If the file does not exist, do not display a diagnostic message or 
              modify the exit status to reflect an error.                        
  -[rR]       Recursively deletes directories                                    

-rmdir [--ignore-fail-on-non-empty] <dir> ... :
  Removes the directory entry specified by each directory argument, provided it is
  empty.

-setfacl [-R] [{-b|-k} {-m|-x <acl_spec>} <path>]|[--set <acl_spec> <path>] :
  Sets Access Control Lists (ACLs) of files and directories.
  Options:
                                                                                 
  -b          Remove all but the base ACL entries. The entries for user, group   
              and others are retained for compatibility with permission bits.    
  -k          Remove the default ACL.                                            
  -R          Apply operations to all files and directories recursively.         
  -m          Modify ACL. New entries are added to the ACL, and existing entries 
              are retained.                                                      
  -x          Remove specified ACL entries. Other ACL entries are retained.      
  --set       Fully replace the ACL, discarding all existing entries. The        
              <acl_spec> must include entries for user, group, and others for    
              compatibility with permission bits.                                
  <acl_spec>  Comma separated list of ACL entries.                               
  <path>      File or directory to modify.                                       

-setfattr {-n name [-v value] | -x name} <path> :
  Sets an extended attribute name and value for a file or directory.
                                                                                 
  -n name   The extended attribute name.                                         
  -v value  The extended attribute value. There are three different encoding     
            methods for the value. If the argument is enclosed in double quotes, 
            then the value is the string inside the quotes. If the argument is   
            prefixed with 0x or 0X, then it is taken as a hexadecimal number. If 
            the argument begins with 0s or 0S, then it is taken as a base64      
            encoding.                                                            
  -x name   Remove the extended attribute.                                       
  <path>    The file or directory.                                               

-setrep [-R] [-w] <rep> <path> ... :
  Set the replication level of a file. If <path> is a directory then the command
  recursively changes the replication factor of all files under the directory tree
  rooted at <path>.
                                                                                 
  -w  It requests that the command waits for the replication to complete. This   
      can potentially take a very long time.                                     
  -R  It is accepted for backwards compatibility. It has no effect.              

-stat [format] <path> ... :
  Print statistics about the file/directory at <path>
  in the specified format. Format accepts filesize in
  blocks (%b), type (%F), group name of owner (%g),
  name (%n), block size (%o), replication (%r), user name
  of owner (%u), modification date (%y, %Y).
  %y shows UTC date as "yyyy-MM-dd HH:mm:ss" and
  %Y shows milliseconds since January 1, 1970 UTC.
  If the format is not specified, %y is used by default.

-tail [-f] <file> :
  Show the last 1KB of the file.
                                             
  -f  Shows appended data as the file grows. 

-test -[defsz] <path> :
  Answer various questions about <path>, with result via exit status.
    -d  return 0 if <path> is a directory.
    -e  return 0 if <path> exists.
    -f  return 0 if <path> is a file.
    -s  return 0 if file <path> is greater than zero bytes in size.
    -z  return 0 if file <path> is zero bytes in size, else return 1.

-text [-ignoreCrc] <src> ... :
  Takes a source file and outputs the file in text format.
  The allowed formats are zip and TextRecordInputStream and Avro.

-touchz <path> ... :
  Creates a file of zero length at <path> with current time as the timestamp of
  that <path>. An error is returned if the file exists with non-zero length

-truncate [-w] <length> <path> ... :
  Truncate all files that match the specified file pattern to the specified
  length.
                                                                                 
  -w  Requests that the command wait for block recovery to complete, if          
      necessary.                                                                 

-usage [cmd ...] :
  Displays the usage for given command or all commands if none is specified.

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

 ## -ls

- 说明

```bash
# 显示 HDFS 目录信息
bin/hadoop fs -ls [-d] [-h] [-R] [<path> ...]
```

```bash
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

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /
Found 2 items
-rw-r--r--   3 zozo supergroup  212046774 2019-09-25 20:04 /hadoop-2.7.2.tar.gz
-rw-r--r--   3 zozo supergroup         36 2019-09-25 20:04 /wc.input
[zozo@vm017 hadoop-2.7.2]$ 
```

## -mkdir

- 说明

```bash
# 在 HDFS 上创建目录
bin/hadoop fs -mkdir [-p] <path> ...
```

```bash
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help mkdir
-mkdir [-p] <path> ... :
  Create a directory in specified location.

  -p  Do not fail if the directory already exists
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /
Found 2 items
-rw-r--r--   3 zozo supergroup  212046774 2019-09-25 20:04 /hadoop-2.7.2.tar.gz
-rw-r--r--   3 zozo supergroup         36 2019-09-25 20:04 /wc.input
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

- 说明

```bash
# 从本地剪切到 HDFS
bin/hadoop fs -moveFromLocal <localsrc> ... <dst>
```

```bash
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help moveFromLocal
-moveFromLocal <localsrc> ... <dst> :
  Same as -put, except that the source is deleted after it's copied.
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ ll /home/zozo/app/hadoop/fortest/f1
-rw-r--r-- 1 zozo zozo 30 9月  28 02:06 /home/zozo/app/hadoop/fortest/f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/f1
ls: `/d1/d1_a/f1': No such file or directory
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -moveFromLocal /home/zozo/app/hadoop/fortest/f1 /d1/d1_a/
[zozo@vm017 hadoop-2.7.2]$ ll /home/zozo/app/hadoop/fortest/f1
ls: 无法访问/home/zozo/app/hadoop/fortest/f1: 没有那个文件或目录
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/f1
-rw-r--r--   3 zozo supergroup         30 2019-09-28 02:09 /d1/d1_a/f1
[zozo@vm017 hadoop-2.7.2]$ 
```

## -cat

- 说明

```bash
# 显示 HDFS 文件内容
bin/hadoop fs -cat [-ignoreCrc] <src> ...
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help cat
-cat [-ignoreCrc] <src> ... :
  Fetch all files that match the file pattern <src> and display their content on
  stdout.
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -cat /d1/d1_a/f1
I am f1
[zozo@vm017 hadoop-2.7.2]$ 
```

## -appendToFile

- 说明

```bash
# 追加一个本地文件到 HDFS 上已经存在的文件末尾
bin/hadoop fs -appendToFile <localsrc> ... <dst>
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help appendToFile
-appendToFile <localsrc> ... <dst> :
  Appends the contents of all the given local files to the given dst file. The dst
  file will be created if it does not exist. If <localSrc> is -, then the input is
  read from stdin.
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -cat /d1/d1_a/f1
I am f1
[zozo@vm017 hadoop-2.7.2]$ cat /home/zozo/app/hadoop/fortest/appendToF1
I am appender for f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -appendToFile /home/zozo/app/hadoop/fortest/appendToF1 /d1/d1_a/f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -cat /d1/d1_a/f1
I am f1
I am appender for f1
[zozo@vm017 hadoop-2.7.2]$ 
```

## -chgrp, -chmod, -chown

- 说明

```bash
# -chgrp: 修改 HDFS 文件 / 文件夹所属组
bin/hadoop fs -chgrp [-R] GROUP PATH...
# -chmod: 修改 HDFS 文件 / 文件夹操作权限
bin/hadoop fs -chmod [-R] <MODE[,MODE]... | OCTALMODE> PATH...
# -chown: 修改 HDFS 文件 / 文件夹所有者
bin/hadoop fs -chown [-R] [OWNER][:[GROUP]] PATH...
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help chgrp
-chgrp [-R] GROUP PATH... :
  This is equivalent to -chown ... :GROUP ...
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help chmod
-chmod [-R] <MODE[,MODE]... | OCTALMODE> PATH... :
  Changes permissions of a file. This works similar to the shell's chmod command
  with a few exceptions.
                                                                                 
  -R           modifies the files recursively. This is the only option currently 
               supported.                                                        
  <MODE>       Mode is the same as mode used for the shell's command. The only   
               letters recognized are 'rwxXt', e.g. +t,a+r,g-w,+rwx,o=r.         
  <OCTALMODE>  Mode specifed in 3 or 4 digits. If 4 digits, the first may be 1 or
               0 to turn the sticky bit on or off, respectively.  Unlike the     
               shell command, it is not possible to specify only part of the     
               mode, e.g. 754 is same as u=rwx,g=rx,o=r.                         
  
  If none of 'augo' is specified, 'a' is assumed and unlike the shell command, no
  umask is applied.
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help chown
-chown [-R] [OWNER][:[GROUP]] PATH... :
  Changes owner and group of a file. This is similar to the shell's chown command
  with a few exceptions.
                                                                                 
  -R  modifies the files recursively. This is the only option currently          
      supported.                                                                 
  
  If only the owner or group is specified, then only the owner or group is
  modified. The owner and group names may only consist of digits, alphabet, and
  any of [-_./@a-zA-Z0-9]. The names are case sensitive.
  
  WARNING: Avoid using '.' to separate user name and group though Linux allows it.
  If user names have dots in them and you are using local file system, you might
  see surprising results since the shell command 'chown' is used for local files.
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/f1
-rw-r--r--   3 zozo supergroup         30 2019-09-27 23:59 /d1/d1_a/f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -chgrp zozo /d1/d1_a/f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/f1
-rw-r--r--   3 zozo zozo         30 2019-09-27 23:59 /d1/d1_a/f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -chmod 754 /d1/d1_a/f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/f1
-rwxr-xr--   3 zozo zozo         30 2019-09-27 23:59 /d1/d1_a/f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -chown zozo /d1/d1_a/f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/f1
-rwxr-xr--   3 zozo zozo         30 2019-09-27 23:59 /d1/d1_a/f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -chown zozo:zozo /d1/d1_a/f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/f1
-rwxr-xr--   3 zozo zozo         30 2019-09-27 23:59 /d1/d1_a/f1
[zozo@vm017 hadoop-2.7.2]$ 
```

## -put / -copyFromLocal

- 说明

```bash
# 从本地拷贝文件到 HDFS
bin/hadoop fs -put [-f] [-p] [-l] <localsrc> ... <dst>
bin/hadoop fs -copyFromLocal [-f] [-p] [-l] <localsrc> ... <dst>
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help put
-put [-f] [-p] [-l] <localsrc> ... <dst> :
  Copy files from the local file system into fs. Copying fails if the file already
  exists, unless the -f flag is given.
  Flags:
                                                                       
  -p  Preserves access and modification times, ownership and the mode. 
  -f  Overwrites the destination if it already exists.                 
  -l  Allow DataNode to lazily persist the file to disk. Forces        
         replication factor of 1. This flag will result in reduced
         durability. Use with care.
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help copyFromLocal
-copyFromLocal [-f] [-p] [-l] <localsrc> ... <dst> :
  Identical to the -put command.
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ ll /home/zozo/app/hadoop/fortest/f2
-rw-rw-r-- 1 zozo zozo 8 9月  28 00:30 /home/zozo/app/hadoop/fortest/f2
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/f2
ls: `/d1/d1_a/f2': No such file or directory
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -put /home/zozo/app/hadoop/fortest/f2 /d1/d1_a/
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/f2
-rw-r--r--   3 zozo supergroup          8 2019-09-28 02:08 /d1/d1_a/f2
[zozo@vm017 hadoop-2.7.2]$ 
```

## -get / -copyToLocal

- 说明

```bash
# 从 HDFS 拷贝文件到本地
bin/hadoop fs -get [-p] [-ignoreCrc] [-crc] <src> ... <localdst>
bin/hadoop fs -copyToLocal [-p] [-ignoreCrc] [-crc] <src> ... <localdst>
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help get
-get [-p] [-ignoreCrc] [-crc] <src> ... <localdst> :
  Copy files that match the file pattern <src> to the local name.  <src> is kept. 
  When copying multiple files, the destination must be a directory. Passing -p
  preserves access and modification times, ownership and the mode.
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help copyToLocal
-copyToLocal [-p] [-ignoreCrc] [-crc] <src> ... <localdst> :
  Identical to the -get command.
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 fortest]$ ll /home/zozo/app/hadoop/fortest/f1
ls: 无法访问/home/zozo/app/hadoop/fortest/f1: 没有那个文件或目录
-rw-rw-r-- 1 zozo zozo 21 9月  28 00:00 appendToF1
-rw-rw-r-- 1 zozo zozo  8 9月  28 00:30 f2
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -get /d1/d1_a/f1 /home/zozo/app/hadoop/fortest/
[zozo@vm017 hadoop-2.7.2]$ ll /home/zozo/app/hadoop/fortest/f1
-rw-r--r-- 1 zozo zozo 30 9月  28 02:06 /home/zozo/app/hadoop/fortest/f1
[zozo@vm017 hadoop-2.7.2]$ 
```

## -getmerge

- 说明

```bash
# 合并下载 HDFS 的多个文件到本地, 比如将 HDFS 目录下的 /d/f1, /d/f2, /d/f3 ... 合并成一个文件下载到本地
bin/hadoop fs -getmerge [-nl] <src> <localdst>
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help getmerge
-getmerge [-nl] <src> <localdst> :
  Get all the files in the directories that match the source file pattern and
  merge and sort them to only one file on local fs. <src> is kept.
                                                        
  -nl  Add a newline character at the end of each file. 
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ ll /home/zozo/app/hadoop/fortest/f12
ls: 无法访问/home/zozo/app/hadoop/fortest/f12: 没有那个文件或目录
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -cat /d1/d1_a/f1
I am f1
I am appender for f1 
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -cat /d1/d1_a/f2
I am f2
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -getmerge /d1/d1_a/f* /home/zozo/app/hadoop/fortest/f12
[zozo@vm017 hadoop-2.7.2]$ cat /home/zozo/app/hadoop/fortest/f12
I am f1
I am appender for f1 
I am f2
[zozo@vm017 hadoop-2.7.2]$ 
```

## -cp

- 说明

```bash
# 从 HDFS 到一个路径拷贝到另一个路径
bin/hadoop fs -cp [-f] [-p | -p[topax]] <src> ... <dst>
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help cp
-cp [-f] [-p | -p[topax]] <src> ... <dst> :
  Copy files that match the file pattern <src> to a destination.  When copying
  multiple files, the destination must be a directory. Passing -p preserves status
  [topax] (timestamps, ownership, permission, ACLs, XAttr). If -p is specified
  with no <arg>, then preserves timestamps, ownership, permission. If -pa is
  specified, then preserves permission also because ACL is a super-set of
  permission. Passing -f overwrites the destination if it already exists. raw
  namespace extended attributes are preserved if (1) they are supported (HDFS
  only) and, (2) all of the source and target pathnames are in the /.reserved/raw
  hierarchy. raw namespace xattr preservation is determined solely by the presence
  (or absence) of the /.reserved/raw prefix and not by the -p option.
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/f2
ls: `/d1/f2': No such file or directory
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -cp /d1/d1_a/f2 /d1/
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/f2
-rw-r--r--   3 zozo supergroup          8 2019-09-28 02:40 /d1/f2
[zozo@vm017 hadoop-2.7.2]$ 
```

## -mv

- 说明

```bash
# 在 HDFS 目录中移动文件
bin/hadoop fs -mv <src> ... <dst>
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help mv
-mv <src> ... <dst> :
  Move files that match the specified file pattern <src> to a destination <dst>. 
  When moving multiple files, the destination must be a directory.
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /f2
ls: `/f2': No such file or directory
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/f2
-rw-r--r--   3 zozo supergroup          8 2019-09-28 02:38 /d1/f2
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -mv /d1/f2 /
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /f2
-rw-r--r--   3 zozo supergroup          8 2019-09-28 02:38 /f2
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/f2
ls: `/d1/f2': No such file or directory
[zozo@vm017 hadoop-2.7.2]$ 
```

## -tail

- 说明

```bash
# 显示 HDFS 一个文件的末尾部分 (并非按照行来截取, 而是按照大小截取, 截取的位置可能不是完整的一行, 参考下文 DEMO)
bin/hadoop fs -cat [-ignoreCrc] <src> ...
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help cat
-cat [-ignoreCrc] <src> ... :
  Fetch all files that match the file pattern <src> and display their content on
  stdout.
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -cat /d1/d1_a/multiLines
ZooKeeper™: A high-performance coordination service for distributed applications.

Hadoop Common: The common utilities that support the other Hadoop modules.
Hadoop Distributed File System (HDFS™): A distributed file system that provides high-throughput access to application data.
Hadoop YARN: A framework for job scheduling and cluster resource management.
Hadoop MapReduce: A YARN-based system for parallel processing of large data sets.
Hadoop Ozone: An object store for Hadoop.
Hadoop Submarine: A machine learning engine for Hadoop.

Who Uses Hadoop?
A wide variety of companies and organizations use Hadoop for both research and production. Users are encouraged to add themselves to the Hadoop PoweredBy wiki page.
This is the second stable release of Apache Hadoop 3.2 line. It contains 493 bug fixes, improvements and enhancements since 3.2.0

Users are encouraged to read the overview of major changes since 3.2.0 For details of 493 bug fixes, improvements, and other enhancements since the previous 3.2.0 release, please check release notes and changelog detail the changes since 3.2.0
Ozone 0.4.0 alpha version supports kerberos and transparent data encryption. This is first secure Ozone release. It is compatible with apache Spark, Hive and Yarn.

For more information check the ozone site.
This is the second stable release of Apache Hadoop 3.1 line. It contains 325 bug fixes, improvements and enhancements since 3.1.1.

Apache Hadoop, Hadoop, Apache, the Apache feather logo, and the Apache Hadoop project logo are either registered trademarks or trademarks of the Apache Software Foundation in the United States and other countries

Copyright © 2018 The Apache Software Foundation.

Privacy policy

[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -tail /d1/d1_a/multiLines
Hadoop PoweredBy wiki page.
This is the second stable release of Apache Hadoop 3.2 line. It contains 493 bug fixes, improvements and enhancements since 3.2.0

Users are encouraged to read the overview of major changes since 3.2.0 For details of 493 bug fixes, improvements, and other enhancements since the previous 3.2.0 release, please check release notes and changelog detail the changes since 3.2.0
Ozone 0.4.0 alpha version supports kerberos and transparent data encryption. This is first secure Ozone release. It is compatible with apache Spark, Hive and Yarn.

For more information check the ozone site.
This is the second stable release of Apache Hadoop 3.1 line. It contains 325 bug fixes, improvements and enhancements since 3.1.1.

Apache Hadoop, Hadoop, Apache, the Apache feather logo, and the Apache Hadoop project logo are either registered trademarks or trademarks of the Apache Software Foundation in the United States and other countries

Copyright © 2018 The Apache Software Foundation.

Privacy policy

[zozo@vm017 hadoop-2.7.2]$ 
```

## -rm

- 说明

```bash
# 删除 HDFS 的文件 / 文件夹
bin/hadoop fs -rm [-f] [-r|-R] [-skipTrash] <src> ...
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help rm
-rm [-f] [-r|-R] [-skipTrash] <src> ... :
  Delete all files that match the specified file pattern. Equivalent to the Unix
  command "rm <src>"
                                                                                 
  -skipTrash  option bypasses trash, if enabled, and immediately deletes <src>   
  -f          If the file does not exist, do not display a diagnostic message or 
              modify the exit status to reflect an error.                        
  -[rR]       Recursively deletes directories                                    
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/multiLines
-rw-r--r--   3 zozo supergroup       1722 2019-09-28 02:23 /d1/d1_a/multiLines
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -rm /d1/d1_a/multiLines
19/09/28 02:29:32 INFO fs.TrashPolicyDefault: Namenode trash configuration: Deletion interval = 0 minutes, Emptier interval = 0 minutes.
Deleted /d1/d1_a/multiLines
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/multiLines
ls: `/d1/d1_a/multiLines': No such file or directory
[zozo@vm017 hadoop-2.7.2]$ 
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /d1
drwxr-xr-x   - zozo supergroup          0 2019-09-28 02:35 /d1/d1_a
-rw-r--r--   3 zozo supergroup         30 2019-09-28 02:35 /d1/d1_a/f1
-rw-r--r--   3 zozo supergroup          8 2019-09-28 02:35 /d1/d1_a/f2
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -rm -r /d1/d1_a
19/09/28 02:36:08 INFO fs.TrashPolicyDefault: Namenode trash configuration: Deletion interval = 0 minutes, Emptier interval = 0 minutes.
Deleted /d1/d1_a
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /d1
[zozo@vm017 hadoop-2.7.2]$ 
```

## -rmdir

- 说明

```bash
# 删除 HDFS 的空目录
bin/hadoop fs -rmdir [--ignore-fail-on-non-empty] <dir> ...
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help rmdir
-rmdir [--ignore-fail-on-non-empty] <dir> ... :
  Removes the directory entry specified by each directory argument, provided it is
  empty.
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /d1/d1_a
-rw-r--r--   3 zozo supergroup         30 2019-09-28 02:36 /d1/d1_a/f1
-rw-r--r--   3 zozo supergroup          8 2019-09-28 02:36 /d1/d1_a/f2
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -rmdir /d1/d1_a
rmdir: `/d1/d1_a': Directory is not empty
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /d1/d1_b
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -rmdir /d1/d1_b
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls -R /d1/d1_b
ls: `/d1/d1_b': No such file or directory
[zozo@vm017 hadoop-2.7.2]$ 
```

## -du

- 说明

```bash
# 统计文件 / 文件夹大小信息
bin/hadoop fs -du [-s] [-h] <path> ...
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help du
-du [-s] [-h] <path> ... :
  Show the amount of space, in bytes, used by the files that match the specified
  file pattern. The following flags are optional:
                                                                                 
  -s  Rather than showing the size of each individual file that matches the      
      pattern, shows the total (summary) size.                                   
  -h  Formats the sizes of files in a human-readable fashion rather than a number
      of bytes.                                                                  
  
  Note that, even without the -s option, this only shows size summaries one level
  deep into a directory.
  
  The output is in the form 
  	size	name(full path)
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /
Found 4 items
drwxr-xr-x   - zozo supergroup          0 2019-09-28 02:48 /d1
-rw-r--r--   3 zozo supergroup          8 2019-09-28 02:38 /f2
-rw-r--r--   3 zozo supergroup  212046774 2019-09-25 20:04 /hadoop-2.7.2.tar.gz
-rw-r--r--   3 zozo supergroup         36 2019-09-25 20:04 /wc.input
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -du /
38         /d1
8          /f2
212046774  /hadoop-2.7.2.tar.gz
36         /wc.input
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -du -h /
38       /d1
8        /f2
202.2 M  /hadoop-2.7.2.tar.gz
36       /wc.input
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -du -s /
212046856  /
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -du -s -h /
202.2 M  /
[zozo@vm017 hadoop-2.7.2]$ 
```

## -setrep

- 说明

```bash
# 设置 HDFS 中文件的副本数
# 如果设置的副本数小于节点数, 将在设置的副本数个节点上建立副本.
# 如果设置的副本数大于节点数, 将在所有节点上建立副本, 等到有节点数增加时, 增加相应的副本, 达到设置的副本数后不再增加副本.
bin/hadoop fs -setrep [-R] [-w] <rep> <path> ...
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -help setrep
-setrep [-R] [-w] <rep> <path> ... :
  Set the replication level of a file. If <path> is a directory then the command
  recursively changes the replication factor of all files under the directory tree
  rooted at <path>.
                                                                                 
  -w  It requests that the command waits for the replication to complete. This   
      can potentially take a very long time.                                     
  -R  It is accepted for backwards compatibility. It has no effect.              
[zozo@vm017 hadoop-2.7.2]$ 
```

- DEMO

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/f1
-rw-r--r--   3 zozo supergroup         30 2019-09-28 02:36 /d1/d1_a/f1
[zozo@vm017 subdir0]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data/current/BP-1195551085-172.16.0.17-1569330784638/current/finalized/subdir0/subdir0
[zozo@vm017 subdir0]$ ll blk_1073741840
-rw-rw-r-- 1 zozo zozo 30 9月  28 02:36 blk_1073741840

[zozo@vm06 subdir0]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data/current/BP-1195551085-172.16.0.17-1569330784638/current/finalized/subdir0/subdir0
[zozo@vm06 subdir0]$ ll blk_1073741840
-rw-rw-r-- 1 zozo zozo 30 9月  28 02:36 blk_1073741840
[zozo@vm06 subdir0]$ 

[zozo@vm03 subdir0]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data/current/BP-1195551085-172.16.0.17-1569330784638/current/finalized/subdir0/subdir0
[zozo@vm03 subdir0]$ ll blk_1073741840
-rw-rw-r-- 1 zozo zozo 30 9月  28 02:36 blk_1073741840
[zozo@vm03 subdir0]$ 


[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -setrep 2 /d1/d1_a/f1
Replication 2 set: /d1/d1_a/f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/f1
-rw-r--r--   2 zozo supergroup         30 2019-09-28 02:36 /d1/d1_a/f1
[zozo@vm017 subdir0]$ ll blk_1073741840
-rw-rw-r-- 1 zozo zozo 30 9月  28 02:36 blk_1073741840
[zozo@vm017 subdir0]$ 

[zozo@vm06 subdir0]$ ll blk_1073741840
-rw-rw-r-- 1 zozo zozo 30 9月  28 02:36 blk_1073741840
[zozo@vm06 subdir0]$ 

[zozo@vm03 subdir0]$ ll blk_1073741840
ls: 无法访问blk_1073741840: 没有那个文件或目录
[zozo@vm03 subdir0]$ 


[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -setrep 5 /d1/d1_a/f1
Replication 5 set: /d1/d1_a/f1
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -ls /d1/d1_a/f1
-rw-r--r--   5 zozo supergroup         30 2019-09-28 02:36 /d1/d1_a/f1
[zozo@vm017 subdir0]$ ll blk_1073741840
-rw-rw-r-- 1 zozo zozo 30 9月  28 02:36 blk_1073741840
[zozo@vm017 subdir0]$ 

[zozo@vm06 subdir0]$ ll blk_1073741840
-rw-rw-r-- 1 zozo zozo 30 9月  28 02:36 blk_1073741840
[zozo@vm06 subdir0]$ 

[zozo@vm03 subdir0]$ ll blk_1073741840
-rw-rw-r-- 1 zozo zozo 30 9月  28 12:41 blk_1073741840
[zozo@vm03 subdir0]$ 
```

---
