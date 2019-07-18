
- https://www.linuxidc.com/Linux/2018-03/151403.htm
- https://www.jianshu.com/p/58ab8109f355
- https://www.jianshu.com/p/16682746137b
- [centos7.6安装MariaDB10.3.11]https://www.ywops.com/archives/centos76-mariadb10311-66.html
- [Mariadb学习笔记（二十一）：MariaDB的各种日志](https://www.54371.net/linux/mariadb/mariadb-many-logs.html)

# step1. 添加 MariaDB yum 仓库

新增 `/etc/yum.repos.d/MariaDB.repo` 文件
```bash
cd /etc/yum.repos.d
touch MariaDB.repo
vi MariaDB.repo
```

文件内容如下:
```
[mariadb]
name=MariaDB
baseurl=http://yum.mariadb.org/10.2/centos7-amd64
gpgkey=https://yum.mariadb.org/RPM-GPG-KEY-MariaDB
gpgcheck=1
```

# step2. 安装 MariaDB

执行安装:
```bash
yum install MariaDB-server MariaDB-client -y
```

安装记录如下:
```
Last login: Thu Jun 20 11:45:40 on console
spiderxmac:~ zoz$ ssh root@111.230.233.137
root@111.230.233.137's password: 
Last login: Sat Jun  1 23:42:44 2019 from 119.35.80.25
[root@vm03 ~]# ll
总用量 0
[root@vm03 ~]# pwd
/root
[root@vm03 ~]# ll
总用量 0
[root@vm03 ~]# df -h
文件系统        容量  已用  可用 已用% 挂载点
/dev/vda1        50G  2.8G   44G    6% /
devtmpfs        3.9G     0  3.9G    0% /dev
tmpfs           3.9G   24K  3.9G    1% /dev/shm
tmpfs           3.9G  392K  3.9G    1% /run
tmpfs           3.9G     0  3.9G    0% /sys/fs/cgroup
tmpfs           783M     0  783M    0% /run/user/0
tmpfs           783M     0  783M    0% /run/user/1000
[root@vm03 ~]# free -g
              total        used        free      shared  buff/cache   available
Mem:              7           0           5           0           2           6
Swap:             0           0           0
[root@vm03 ~]# cat /etc/yum.repos.d/MariaDB.repo
cat: /etc/yum.repos.d/MariaDB.repo: 没有那个文件或目录
[root@vm03 ~]# ll /etc/yum.repos.d
总用量 8
-rw-r--r-- 1 root root 614 6月   1 23:37 CentOS-Base.repo
-rw-r--r-- 1 root root 230 6月   1 23:37 CentOS-Epel.repo
[root@vm03 ~]# touch MariaDB.repo^C
[root@vm03 ~]# cd /etc/yum.repos.d/
[root@vm03 yum.repos.d]# touch MariaDB.repo
[root@vm03 yum.repos.d]# vi MariaDB.repo 
[root@vm03 yum.repos.d]# vi MariaDB.repo 
[root@vm03 yum.repos.d]# cat MariaDB.repo 
[mariadb]
name=MariaDB
baseurl=http://yum.mariadb.org/10.2/centos7-amd64
gpgkey=https://yum.mariadb.org/RPM-GPG-KEY-MariaDB
gpgcheck=1
[root@vm03 yum.repos.d]# yum install MariaDB-server MariaDB-client -y
已加载插件：fastestmirror, langpacks
epel                                                                       | 5.3 kB  00:00:00     
extras                                                                     | 3.4 kB  00:00:00     
mariadb                                                                    | 2.9 kB  00:00:00     
os                                                                         | 3.6 kB  00:00:00     
updates                                                                    | 3.4 kB  00:00:00     
(1/5): extras/7/x86_64/primary_db                                          | 205 kB  00:00:00     
(2/5): epel/7/x86_64/updateinfo                                            | 982 kB  00:00:00     
(3/5): updates/7/x86_64/primary_db                                         | 6.5 MB  00:00:00     
(4/5): epel/7/x86_64/primary_db                                            | 6.8 MB  00:00:00     
(5/5): mariadb/primary_db                                                  |  54 kB  00:00:44     
Determining fastest mirrors
正在解决依赖关系
--> 正在检查事务
---> 软件包 MariaDB-client.x86_64.0.10.2.25-1.el7.centos 将被 安装
--> 正在处理依赖关系 MariaDB-common，它被软件包 MariaDB-client-10.2.25-1.el7.centos.x86_64 需要
---> 软件包 MariaDB-server.x86_64.0.10.2.25-1.el7.centos 将被 安装
--> 正在处理依赖关系 perl(DBI)，它被软件包 MariaDB-server-10.2.25-1.el7.centos.x86_64 需要
--> 正在处理依赖关系 galera，它被软件包 MariaDB-server-10.2.25-1.el7.centos.x86_64 需要
--> 正在处理依赖关系 perl(Data::Dumper)，它被软件包 MariaDB-server-10.2.25-1.el7.centos.x86_64 需要
--> 正在检查事务
---> 软件包 MariaDB-common.x86_64.0.10.2.25-1.el7.centos 将被 安装
--> 正在处理依赖关系 MariaDB-compat，它被软件包 MariaDB-common-10.2.25-1.el7.centos.x86_64 需要
---> 软件包 galera.x86_64.0.25.3.26-1.rhel7.el7.centos 将被 安装
--> 正在处理依赖关系 libboost_program_options.so.1.53.0()(64bit)，它被软件包 galera-25.3.26-1.rhel7.el7.centos.x86_64 需要
---> 软件包 perl-DBI.x86_64.0.1.627-4.el7 将被 安装
--> 正在处理依赖关系 perl(RPC::PlServer) >= 0.2001，它被软件包 perl-DBI-1.627-4.el7.x86_64 需要
--> 正在处理依赖关系 perl(RPC::PlClient) >= 0.2000，它被软件包 perl-DBI-1.627-4.el7.x86_64 需要
---> 软件包 perl-Data-Dumper.x86_64.0.2.145-3.el7 将被 安装
--> 正在检查事务
---> 软件包 MariaDB-compat.x86_64.0.10.2.25-1.el7.centos 将被 舍弃
---> 软件包 boost-program-options.x86_64.0.1.53.0-27.el7 将被 安装
---> 软件包 mariadb-libs.x86_64.1.5.5.56-2.el7 将被 取代
---> 软件包 perl-PlRPC.noarch.0.0.2020-14.el7 将被 安装
--> 正在处理依赖关系 perl(Net::Daemon) >= 0.13，它被软件包 perl-PlRPC-0.2020-14.el7.noarch 需要
--> 正在处理依赖关系 perl(Net::Daemon::Test)，它被软件包 perl-PlRPC-0.2020-14.el7.noarch 需要
--> 正在处理依赖关系 perl(Net::Daemon::Log)，它被软件包 perl-PlRPC-0.2020-14.el7.noarch 需要
--> 正在处理依赖关系 perl(Compress::Zlib)，它被软件包 perl-PlRPC-0.2020-14.el7.noarch 需要
--> 正在检查事务
---> 软件包 perl-IO-Compress.noarch.0.2.061-2.el7 将被 安装
--> 正在处理依赖关系 perl(Compress::Raw::Zlib) >= 2.061，它被软件包 perl-IO-Compress-2.061-2.el7.noarch 需要
--> 正在处理依赖关系 perl(Compress::Raw::Bzip2) >= 2.061，它被软件包 perl-IO-Compress-2.061-2.el7.noarch 需要
---> 软件包 perl-Net-Daemon.noarch.0.0.48-5.el7 将被 安装
--> 正在检查事务
---> 软件包 perl-Compress-Raw-Bzip2.x86_64.0.2.061-3.el7 将被 安装
---> 软件包 perl-Compress-Raw-Zlib.x86_64.1.2.061-4.el7 将被 安装
--> 解决依赖关系完成

依赖关系解决

==================================================================================================
 Package                       架构         版本                              源             大小
==================================================================================================
正在安装:
 MariaDB-client                x86_64       10.2.25-1.el7.centos              mariadb        11 M
 MariaDB-compat                x86_64       10.2.25-1.el7.centos              mariadb       2.8 M
      替换  mariadb-libs.x86_64 1:5.5.56-2.el7
 MariaDB-server                x86_64       10.2.25-1.el7.centos              mariadb        24 M
为依赖而安装:
 MariaDB-common                x86_64       10.2.25-1.el7.centos              mariadb        78 k
 boost-program-options         x86_64       1.53.0-27.el7                     os            156 k
 galera                        x86_64       25.3.26-1.rhel7.el7.centos        mariadb       8.1 M
 perl-Compress-Raw-Bzip2       x86_64       2.061-3.el7                       os             32 k
 perl-Compress-Raw-Zlib        x86_64       1:2.061-4.el7                     os             57 k
 perl-DBI                      x86_64       1.627-4.el7                       os            802 k
 perl-Data-Dumper              x86_64       2.145-3.el7                       os             47 k
 perl-IO-Compress              noarch       2.061-2.el7                       os            260 k
 perl-Net-Daemon               noarch       0.48-5.el7                        os             51 k
 perl-PlRPC                    noarch       0.2020-14.el7                     os             36 k

事务概要
==================================================================================================
安装  3 软件包 (+10 依赖软件包)

总下载量：47 M
Downloading packages:
警告：/var/cache/yum/x86_64/7/mariadb/packages/MariaDB-common-10.2.25-1.el7.centos.x86_64.rpm: 头V4 DSA/SHA1 Signature, 密钥 ID 1bb943db: NOKEY
MariaDB-common-10.2.25-1.el7.centos.x86_64.rpm 的公钥尚未安装
(1/13): MariaDB-common-10.2.25-1.el7.centos.x86_64.rpm                     |  78 kB  00:00:12     
MariaDB-client-10.2.25-1.el7.c FAILED                                           kB 1792:43:39 ETA 
http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-client-10.2.25-1.el7.centos.x86_64.rpm: [Errno 12] Timeout on http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-client-10.2.25-1.el7.centos.x86_64.rpm: (28, 'Operation too slow. Less than 1000 bytes/sec transferred the last 30 seconds')
正在尝试其它镜像。
(2/13): boost-program-options-1.53.0-27.el7.x86_64.rpm                     | 156 kB  00:00:00     
MariaDB-compat-10.2.25-1.el7.c FAILED                                          2 kB  --:--:-- ETA 
http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-compat-10.2.25-1.el7.centos.x86_64.rpm: [Errno 12] Timeout on http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-compat-10.2.25-1.el7.centos.x86_64.rpm: (28, 'Operation too slow. Less than 1000 bytes/sec transferred the last 30 seconds')
正在尝试其它镜像。
MariaDB-server-10.2.25-1.el7.c FAILED                                           MB 6478:40:20 ETA 
http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-server-10.2.25-1.el7.centos.x86_64.rpm: [Errno 12] Timeout on http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-server-10.2.25-1.el7.centos.x86_64.rpm: (28, 'Operation too slow. Less than 1000 bytes/sec transferred the last 30 seconds')
正在尝试其它镜像。
(3/13): perl-Compress-Raw-Bzip2-2.061-3.el7.x86_64.rpm                     |  32 kB  00:00:00     
(4/13): perl-Compress-Raw-Zlib-2.061-4.el7.x86_64.rpm                      |  57 kB  00:00:00     
(5/13): perl-Data-Dumper-2.145-3.el7.x86_64.rpm                            |  47 kB  00:00:00     
(6/13): perl-DBI-1.627-4.el7.x86_64.rpm                                    | 802 kB  00:00:00     
(7/13): perl-Net-Daemon-0.48-5.el7.noarch.rpm                              |  51 kB  00:00:00     
(8/13): perl-PlRPC-0.2020-14.el7.noarch.rpm                                |  36 kB  00:00:00     
(9/13): perl-IO-Compress-2.061-2.el7.noarch.rpm                            | 260 kB  00:00:00     
(10/13): galera-25.3.26-1.rhel7.el 10% [==-                     ] 8.6 kB/s | 4.9 MB  01:22:50 ETA 
(10/13): galera-25.3.26-1.rhel7.el 10% [==-                     ] 6.2 kB/s | 5.0 MB  01:54:48 ETA 
(10/13): galera-25.3.26-1.rhel7.el 12% [===                     ] 3.1 kB/s | 5.9 MB  03:48:48 ETA 
(10/13): galera-25.3.26-1.rhel7.el 13% [===                     ] 3.7 kB/s | 6.3 MB  03:05:05 ETA 
(10/13): galera-25.3.26-1.rhel7.el 14% [===                     ]  14 kB/s | 6.8 MB  00:50:30 ETA 
(10/13): galera-25.3.26-1.rhel7.el 15% [===-                    ] 5.3 kB/s | 7.1 MB  02:08:42 ETA 
galera-25.3.26-1.rhel7.el7.cen FAILED                                           MB 6947:22:15 ETA 
http://yum.mariadb.org/10.2/centos7-amd64/rpms/galera-25.3.26-1.rhel7.el7.centos.x86_64.rpm: [Errno 12] Timeout on http://yum.mariadb.org/10.2/centos7-amd64/rpms/galera-25.3.26-1.rhel7.el7.centos.x86_64.rpm: (28, 'Operation too slow. Less than 1000 bytes/sec transferred the last 30 seconds')
正在尝试其它镜像。
(10/13): MariaDB-client-10.2.25-1. 4% [=                        ] 3.6 kB/s | 2.3 MB  03:29:31 ETA 
(10/13): MariaDB-client-10.2.25-1. 6% [=-                       ] 6.5 kB/s | 3.0 MB  01:55:12 ETA 
(10/13): MariaDB-client-10.2.25-1. 6% [=-                       ] 3.1 kB/s | 3.0 MB  04:01:19 ETA 
(10/13): MariaDB-client-10.2.25-1. 8% [==                       ]  385 B/s | 3.8 MB  32:31:03 ETA 
(10/13): MariaDB-client-10.2.25-1.el7.centos.x86_64.rpm                    |  11 MB  00:37:20     
(11/13): MariaDB-compat-10.2.25-1.el7.centos.x86_64.rpm                    | 2.8 MB  00:04:33     
(12/13): MariaDB-server-10.2.25-1. 48% [===========-            ] 2.3 kB/s |  23 MB  03:03:18 ETA 
MariaDB-server-10.2.25-1.el7.c FAILED                                           MB 2895:01:52 ETA 
http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-server-10.2.25-1.el7.centos.x86_64.rpm: [Errno 12] Timeout on http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-server-10.2.25-1.el7.centos.x86_64.rpm: (28, 'Operation too slow. Less than 1000 bytes/sec transferred the last 30 seconds')
正在尝试其它镜像。
galera-25.3.26-1.rhel7.el7.cen FAILED                                           MB 1841:02:34 ETA 
http://yum.mariadb.org/10.2/centos7-amd64/rpms/galera-25.3.26-1.rhel7.el7.centos.x86_64.rpm: [Errno 12] Timeout on http://yum.mariadb.org/10.2/centos7-amd64/rpms/galera-25.3.26-1.rhel7.el7.centos.x86_64.rpm: (28, 'Operation too slow. Less than 1000 bytes/sec transferred the last 30 seconds')
正在尝试其它镜像。
MariaDB-server-10.2.25-1.el7.c FAILED                                          3 MB 256:29:58 ETA 
http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-server-10.2.25-1.el7.centos.x86_64.rpm: [Errno 12] Timeout on http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-server-10.2.25-1.el7.centos.x86_64.rpm: (28, 'Operation too slow. Less than 1000 bytes/sec transferred the last 30 seconds')
正在尝试其它镜像。
galera-25.3.26-1.rhel7.el7.cen FAILED                                           MB 8959:11:52 ETA 
http://yum.mariadb.org/10.2/centos7-amd64/rpms/galera-25.3.26-1.rhel7.el7.centos.x86_64.rpm: [Errno 12] Timeout on http://yum.mariadb.org/10.2/centos7-amd64/rpms/galera-25.3.26-1.rhel7.el7.centos.x86_64.rpm: (28, 'Operation too slow. Less than 1000 bytes/sec transferred the last 30 seconds')
正在尝试其它镜像。
(12/13): MariaDB-server-10.2.25-1. 49% [===========-            ] 3.7 kB/s |  23 MB  01:49:59 ETA 
MariaDB-server-10.2.25-1.el7.c FAILED                                           MB 1889:12:36 ETA 
http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-server-10.2.25-1.el7.centos.x86_64.rpm: [Errno 12] Timeout on http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-server-10.2.25-1.el7.centos.x86_64.rpm: (28, 'Operation too slow. Less than 1000 bytes/sec transferred the last 30 seconds')
正在尝试其它镜像。
galera-25.3.26-1.rhel7.el7.cen FAILED                                           MB 1102:19:40 ETA 
http://yum.mariadb.org/10.2/centos7-amd64/rpms/galera-25.3.26-1.rhel7.el7.centos.x86_64.rpm: [Errno 12] Timeout on http://yum.mariadb.org/10.2/centos7-amd64/rpms/galera-25.3.26-1.rhel7.el7.centos.x86_64.rpm: (28, 'Operation too slow. Less than 1000 bytes/sec transferred the last 30 seconds')
正在尝试其它镜像。
MariaDB-server-10.2.25-1.el7.c FAILED                                           MB 6444:51:16 ETA 
http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-server-10.2.25-1.el7.centos.x86_64.rpm: [Errno 12] Timeout on http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-server-10.2.25-1.el7.centos.x86_64.rpm: (28, 'Operation too slow. Less than 1000 bytes/sec transferred the last 30 seconds')
正在尝试其它镜像。
galera-25.3.26-1.rhel7.el7.cen FAILED                                           MB 4595:03:05 ETA 
http://yum.mariadb.org/10.2/centos7-amd64/rpms/galera-25.3.26-1.rhel7.el7.centos.x86_64.rpm: [Errno 12] Timeout on http://yum.mariadb.org/10.2/centos7-amd64/rpms/galera-25.3.26-1.rhel7.el7.centos.x86_64.rpm: (28, 'Operation too slow. Less than 1000 bytes/sec transferred the last 30 seconds')
正在尝试其它镜像。
MariaDB-server-10.2.25-1.el7.c FAILED                                           MB 2894:33:15 ETA 
http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-server-10.2.25-1.el7.centos.x86_64.rpm: [Errno 12] Timeout on http://yum.mariadb.org/10.2/centos7-amd64/rpms/MariaDB-server-10.2.25-1.el7.centos.x86_64.rpm: (28, 'Operation too slow. Less than 1000 bytes/sec transferred the last 30 seconds')
正在尝试其它镜像。
(12/13): galera-25.3.26-1.rhel7.el7.centos.x86_64.rpm                      | 8.1 MB  00:06:44     
(13/13): MariaDB-server-10.2.25-1.el7.centos.x86_64.rpm                    |  24 MB  00:43:14     
--------------------------------------------------------------------------------------------------
总计                                                              4.7 kB/s |  47 MB  02:48:51     
从 https://yum.mariadb.org/RPM-GPG-KEY-MariaDB 检索密钥
导入 GPG key 0x1BB943DB:
 用户ID     : "MariaDB Package Signing Key <package-signing-key@mariadb.org>"
 指纹       : 1993 69e5 404b d5fc 7d2f e43b cbcb 082a 1bb9 43db
 来自       : https://yum.mariadb.org/RPM-GPG-KEY-MariaDB
Running transaction check
Running transaction test
Transaction test succeeded
Running transaction
  正在安装    : perl-Data-Dumper-2.145-3.el7.x86_64                                          1/14 
  正在安装    : MariaDB-common-10.2.25-1.el7.centos.x86_64                                   2/14 
  正在安装    : MariaDB-compat-10.2.25-1.el7.centos.x86_64                                   3/14 
  正在安装    : MariaDB-client-10.2.25-1.el7.centos.x86_64                                   4/14 
  正在安装    : 1:perl-Compress-Raw-Zlib-2.061-4.el7.x86_64                                  5/14 
  正在安装    : boost-program-options-1.53.0-27.el7.x86_64                                   6/14 
  正在安装    : galera-25.3.26-1.rhel7.el7.centos.x86_64                                     7/14 
  正在安装    : perl-Net-Daemon-0.48-5.el7.noarch                                            8/14 
  正在安装    : perl-Compress-Raw-Bzip2-2.061-3.el7.x86_64                                   9/14 
  正在安装    : perl-IO-Compress-2.061-2.el7.noarch                                         10/14 
  正在安装    : perl-PlRPC-0.2020-14.el7.noarch                                             11/14 
  正在安装    : perl-DBI-1.627-4.el7.x86_64                                                 12/14 
  正在安装    : MariaDB-server-10.2.25-1.el7.centos.x86_64                                  13/14 
chown: 无效的用户: "mysql"


PLEASE REMEMBER TO SET A PASSWORD FOR THE MariaDB root USER !
To do so, start the server, then issue the following commands:

'/usr/bin/mysqladmin' -u root password 'new-password'
'/usr/bin/mysqladmin' -u root -h vm03 password 'new-password'

Alternatively you can run:
'/usr/bin/mysql_secure_installation'

which will also give you the option of removing the test
databases and anonymous user created by default.  This is
strongly recommended for production servers.

See the MariaDB Knowledgebase at http://mariadb.com/kb or the
MySQL manual for more instructions.

Please report any problems at http://mariadb.org/jira

The latest information about MariaDB is available at http://mariadb.org/.
You can find additional information about the MySQL part at:
http://dev.mysql.com
Consider joining MariaDB's strong and vibrant community:
https://mariadb.org/get-involved/

  正在删除    : 1:mariadb-libs-5.5.56-2.el7.x86_64                                          14/14 
  验证中      : perl-Compress-Raw-Bzip2-2.061-3.el7.x86_64                                   1/14 
  验证中      : perl-Net-Daemon-0.48-5.el7.noarch                                            2/14 
  验证中      : boost-program-options-1.53.0-27.el7.x86_64                                   3/14 
  验证中      : perl-Data-Dumper-2.145-3.el7.x86_64                                          4/14 
  验证中      : MariaDB-compat-10.2.25-1.el7.centos.x86_64                                   5/14 
  验证中      : MariaDB-client-10.2.25-1.el7.centos.x86_64                                   6/14 
  验证中      : perl-IO-Compress-2.061-2.el7.noarch                                          7/14 
  验证中      : 1:perl-Compress-Raw-Zlib-2.061-4.el7.x86_64                                  8/14 
  验证中      : galera-25.3.26-1.rhel7.el7.centos.x86_64                                     9/14 
  验证中      : MariaDB-server-10.2.25-1.el7.centos.x86_64                                  10/14 
  验证中      : perl-DBI-1.627-4.el7.x86_64                                                 11/14 
  验证中      : MariaDB-common-10.2.25-1.el7.centos.x86_64                                  12/14 
  验证中      : perl-PlRPC-0.2020-14.el7.noarch                                             13/14 
  验证中      : 1:mariadb-libs-5.5.56-2.el7.x86_64                                          14/14 

已安装:
  MariaDB-client.x86_64 0:10.2.25-1.el7.centos    MariaDB-compat.x86_64 0:10.2.25-1.el7.centos   
  MariaDB-server.x86_64 0:10.2.25-1.el7.centos   

作为依赖被安装:
  MariaDB-common.x86_64 0:10.2.25-1.el7.centos    boost-program-options.x86_64 0:1.53.0-27.el7   
  galera.x86_64 0:25.3.26-1.rhel7.el7.centos      perl-Compress-Raw-Bzip2.x86_64 0:2.061-3.el7   
  perl-Compress-Raw-Zlib.x86_64 1:2.061-4.el7     perl-DBI.x86_64 0:1.627-4.el7                  
  perl-Data-Dumper.x86_64 0:2.145-3.el7           perl-IO-Compress.noarch 0:2.061-2.el7          
  perl-Net-Daemon.noarch 0:0.48-5.el7             perl-PlRPC.noarch 0:0.2020-14.el7              

替代:
  mariadb-libs.x86_64 1:5.5.56-2.el7                                                              

完毕！
[root@vm03 yum.repos.d]# 
[root@vm03 yum.repos.d]# 
[root@vm03 yum.repos.d]# 
[root@vm03 yum.repos.d]# 
[root@vm03 yum.repos.d]# 
[root@vm03 yum.repos.d]# 
[root@vm03 yum.repos.d]# 
[root@vm03 yum.repos.d]# 
[root@vm03 yum.repos.d]# 
[root@vm03 yum.repos.d]# 
[root@vm03 yum.repos.d]# 
[root@vm03 yum.repos.d]# 
[root@vm03 yum.repos.d]# 
[root@vm03 yum.repos.d]# packet_write_wait: Connection to 111.230.233.137 port 22: Broken pipe
spiderxmac:~ zoz$ 
spiderxmac:~ zoz$ pwd
/Users/zoz
spiderxmac:~ zoz$ exit
logout
Saving session...
...copying shared history...
...saving history...truncating history files...
...completed.
Deleting expired sessions...45 completed.

[进程已完成]

```

# step3. 设置 MariaDB 服务器自动启动

```bash
# 马上启动MariaDB
systemctl start mariadb

# 设置开机自动启动
systemctl enable mariadb
```

# step4. 初次设置

- 如果是全新安装的 MariaDB, 则还需要进行初始化设置:
```bash
/usr/bin/mysql_secure_installation
```

- 然后按照以下步骤就行设置:
```bash
# 输入 root 账号密码 (没有请直接回车), 是否设置 root 用户密码, 直接回车
Enter current password for root (enter for none):
# 为 root 设置密码? 按 Y
Set root password? [Y/n]
# 设置 root 用户的密码
New password:
# 再输入一次你设置的密码
Re-enter new password:
# 删除匿名用户, 按 Y
Remove anonymous users? [Y/n]
# 禁止 root 远程访问, 需要远程管理, 请按 n
Disallow root login remotely? [Y/n]
# 删除 test 数据库及其访问权限, 按 Y
Remove test database and access to it? [Y/n]
# 重新加载访问权限, 按 Y
Reload privilege tables now? [Y/n]
```

- 配置完成后, 执行以下命令测试登录. 其中 `root` 为要登录的用户名, `password` 为刚才设置的 `root` 用户的密码.
```bash
# 推荐
mysql -uroot -p
# 不推荐
mysql -uroot -ppassword
```

操作记录如下:
```
[root@vm03 ~]# systemctl start mariadb
[root@vm03 ~]# ps -ef|grep mysql
mysql    19232     1  0 15:40 ?        00:00:00 /usr/sbin/mysqld
root     19341 19135  0 15:41 pts/0    00:00:00 grep --color=auto mysql
[root@vm03 ~]# /usr/bin/mysql_secure_installation

NOTE: RUNNING ALL PARTS OF THIS SCRIPT IS RECOMMENDED FOR ALL MariaDB
      SERVERS IN PRODUCTION USE!  PLEASE READ EACH STEP CAREFULLY!

In order to log into MariaDB to secure it, we'll need the current
password for the root user.  If you've just installed MariaDB, and
you haven't set the root password yet, the password will be blank,
so you should just press enter here.

Enter current password for root (enter for none):
OK, successfully used password, moving on...

Setting the root password ensures that nobody can log into the MariaDB
root user without the proper authorisation.

Set root password? [Y/n] Y
New password:
Re-enter new password:
Password updated successfully!
Reloading privilege tables..
 ... Success!


By default, a MariaDB installation has an anonymous user, allowing anyone
to log into MariaDB without having to have a user account created for
them.  This is intended only for testing, and to make the installation
go a bit smoother.  You should remove them before moving into a
production environment.

Remove anonymous users? [Y/n] Y
 ... Success!

Normally, root should only be allowed to connect from 'localhost'.  This
ensures that someone cannot guess at the root password from the network.

Disallow root login remotely? [Y/n] n
 ... skipping.

By default, MariaDB comes with a database named 'test' that anyone can
access.  This is also intended only for testing, and should be removed
before moving into a production environment.

Remove test database and access to it? [Y/n] Y
 - Dropping test database...
 ... Success!
 - Removing privileges on test database...
 ... Success!

Reloading the privilege tables will ensure that all changes made so far
will take effect immediately.

Reload privilege tables now? [Y/n] Y
 ... Success!

Cleaning up...

All done!  If you've completed all of the above steps, your MariaDB
installation should now be secure.

Thanks for using MariaDB!
[root@vm03 ~]# 
[root@vm03 ~]# mysql -uroot -ppassword
Welcome to the MariaDB monitor.  Commands end with ; or \g.
Your MariaDB connection id is 16
Server version: 10.2.25-MariaDB MariaDB Server

Copyright (c) 2000, 2018, Oracle, MariaDB Corporation Ab and others.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

MariaDB [(none)]>
MariaDB [(none)]> exit
Bye
[root@vm03 ~]#
```

# step5. 修改配置

- 登录, 使用以下命令查看当前使用的字符集, 应该有好几个不是 utf8 格式:
```
MariaDB [(none)]> SHOW VARIABLES LIKE 'character%';
+--------------------------+----------------------------+
| Variable_name            | Value                      |
+--------------------------+----------------------------+
| character_set_client     | utf8                       |
| character_set_connection | utf8                       |
| character_set_database   | latin1                     |
| character_set_filesystem | binary                     |
| character_set_results    | utf8                       |
| character_set_server     | latin1                     |
| character_set_system     | utf8                       |
| character_sets_dir       | /usr/share/mysql/charsets/ |
+--------------------------+----------------------------+
8 rows in set (0.00 sec)

MariaDB [(none)]> SHOW VARIABLES LIKE "%collation%";
+----------------------+-------------------+
| Variable_name        | Value             |
+----------------------+-------------------+
| collation_connection | utf8_general_ci   |
| collation_database   | latin1_swedish_ci |
| collation_server     | latin1_swedish_ci |
+----------------------+-------------------+
3 rows in set (0.00 sec)

MariaDB [(none)]>
```

- 配置 MariaDB 的字符集

查看 `/etc/my.cnf` (或者 includedir 下的配置, 配置在 `/etc/my.cnf` 或者 `/etc/my.cnf.d` 目录下的文件中都可以, 重要的是 [mysqld], [mysql], [client] 这些)
```
[root@vm03 ~]# cat /etc/my.cnf
#
# This group is read both both by the client and the server
# use it for options that affect everything
#
[client-server]

#
# include all files from the config directory
#
!includedir /etc/my.cnf.d

[root@vm03 ~]# ll /etc/my.cnf.d
总用量 12
-rw-r--r-- 1 root root  763 6月  15 02:29 enable_encryption.preset
-rw-r--r-- 1 root root  232 6月  15 02:29 mysql-clients.cnf
-rw-r--r-- 1 root root 1080 6月  15 02:29 server.cnf
[root@vm03 ~]#
```

- 找到存有 [mysqld] 的配置文件 (如果没有可以新建), 在 [mysqld] 中添加:
```properties

[mysqld]

# 字符集
character-set-server=utf8
collation-server=utf8_unicode_ci

# 初始化连接都设置为 utf8 字符集
# init_connect='SET collation_connection = utf8_unicode_ci'
# init_connect='SET NAMES utf8'
# 忽略客户端字符集设置, 不论客户端是何种字符集, 都按照 init_connect 中的设置进行使用
# 当为 true 时，设置 [client] 和 [mysql] 的 default-character-set, 都不会影响到 mariadb 字符集, 只有通过 set names 来更改才会影响.
# 当为 false 时 (默认), 设置 [client] 和 [mysql] 的 default-character-set, 会影响到 mariadb 字符集, 通过 set names 也一样会影响.
# skip-character-set-client-handshake

# 设置为不区分大小写, Linux 下默认会区分大小写
lower_case_table_name=1

# 将数据库服务器绑定到 Loopback 地址
# bind-address=127.0.0.1

# 修改默认端口
# port=3600

# 错误日志默认是开启的, 可以使用 log-error=filename 来设置将日志保存到哪里, 如果未设置的话则保存到数据目录下的 HOSTNAME.err 文件中去
# log-error=/usr/local/mysql/log/error.log
# 通用查询日志记录了每个客户端的连接和断开以及所收到客户端发送来的数据, 默认是没有开启的, 不建议开启, 因为会将所有的 SQL 语句都记录到该日志中去, 所以会增长的特别快
# general-log-file=/usr/local/mysql/log/mysql.log
```

- 找到存有 [mysql] 的配置文件 (如果没有可以新建), 在 [mysql] 中添加:
```properties

[mysql]

# 字符集
default-character-set=utf8
```

- 找到存有 [client] 的配置文件 (如果没有可以新建), 在 [client] 中添加:
```properties

[client]

# 字符集
default-character-set=utf8
```

- 重启 MariaDB 生效:
```
systemctl restart mariadb
```

- 查看:
```sql
SHOW VARIABLES LIKE 'character%';
SHOW VARIABLES LIKE "%collation%";
```

以下为记录:
```
[root@vm03 ~]# cat /etc/my.cnf
#
# This group is read both both by the client and the server
# use it for options that affect everything
#
[client-server]

#
# include all files from the config directory
#
!includedir /etc/my.cnf.d

[root@vm03 ~]# ll /etc/my.cnf.d
总用量 12
-rw-r--r-- 1 root root  763 6月  15 02:29 enable_encryption.preset
-rw-r--r-- 1 root root  232 6月  15 02:29 mysql-clients.cnf
-rw-r--r-- 1 root root 1080 6月  15 02:29 server.cnf
[root@vm03 ~]# cd /etc/my.cnf.d
[root@vm03 my.cnf.d]# cat enable_encryption.preset
#
# !include this file into your my.cnf (or any of *.cnf files in /etc/my.cnf.d)
# and it will enable data at rest encryption. This is a simple way to
# ensure that everything that can be encrypted will be and your
# data will not leak unencrypted.
#
# DO NOT EDIT THIS FILE! On MariaDB upgrades it might be replaced with a
# newer version and your edits will be lost. Instead, add your edits
# to the .cnf file after the !include directive.
#
# NOTE that you also need to install an encryption plugin for the encryption
# to work. See https://mariadb.com/kb/en/mariadb/data-at-rest-encryption/#encryption-key-management
#
[mariadb]
aria-encrypt-tables
encrypt-binlog
encrypt-tmp-disk-tables
encrypt-tmp-files
loose-innodb-encrypt-log
loose-innodb-encrypt-tables
[root@vm03 my.cnf.d]# cat mysql-clients.cnf
#
# These groups are read by MariaDB command-line tools
# Use it for options that affect only one utility
#

[mysql]

[mysql_upgrade]

[mysqladmin]

[mysqlbinlog]

[mysqlcheck]

[mysqldump]

[mysqlimport]

[mysqlshow]

[mysqlslap]

[root@vm03 my.cnf.d]# cat server.cnf
#
# These groups are read by MariaDB server.
# Use it for options that only the server (but not clients) should see
#
# See the examples of server my.cnf files in /usr/share/mysql/
#

# this is read by the standalone daemon and embedded servers
[server]

# this is only for the mysqld standalone daemon
[mysqld]

#
# * Galera-related settings
#
[galera]
# Mandatory settings
#wsrep_on=ON
#wsrep_provider=
#wsrep_cluster_address=
#binlog_format=row
#default_storage_engine=InnoDB
#innodb_autoinc_lock_mode=2
#
# Allow server to accept connections on all interfaces.
#
#bind-address=0.0.0.0
#
# Optional setting
#wsrep_slave_threads=1
#innodb_flush_log_at_trx_commit=0

# this is only for embedded server
[embedded]

# This group is only read by MariaDB servers, not by MySQL.
# If you use the same .cnf file for MySQL and MariaDB,
# you can put MariaDB-only options here
[mariadb]

# This group is only read by MariaDB-10.2 servers.
# If you use the same .cnf file for MariaDB of different versions,
# use this group for options that older servers don't understand
[mariadb-10.2]

[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]# vi server.cnf
[root@vm03 my.cnf.d]# vi mysql-clients.cnf
[root@vm03 my.cnf.d]# vi /etc/my.cnf
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]# cat /etc/my.cnf
#
# This group is read both both by the client and the server
# use it for options that affect everything
#
[client-server]

[client]
default-character-set=utf8

#
# include all files from the config directory
#
!includedir /etc/my.cnf.d

[root@vm03 my.cnf.d]# cat /etc/my.cnf.d/mysql-clients.cnf
#
# These groups are read by MariaDB command-line tools
# Use it for options that affect only one utility
#

[mysql]
default-character-set=utf8

[mysql_upgrade]

[mysqladmin]

[mysqlbinlog]

[mysqlcheck]

[mysqldump]

[mysqlimport]

[mysqlshow]

[mysqlslap]

[root@vm03 my.cnf.d]# cat /etc/my.cnf.d/server.cnf
#
# These groups are read by MariaDB server.
# Use it for options that only the server (but not clients) should see
#
# See the examples of server my.cnf files in /usr/share/mysql/
#

# this is read by the standalone daemon and embedded servers
[server]

# this is only for the mysqld standalone daemon
[mysqld]
character-set-server=utf8
collation-server=utf8_unicode_ci
lower_case_table_name=1

#
# * Galera-related settings
#
[galera]
# Mandatory settings
#wsrep_on=ON
#wsrep_provider=
#wsrep_cluster_address=
#binlog_format=row
#default_storage_engine=InnoDB
#innodb_autoinc_lock_mode=2
#
# Allow server to accept connections on all interfaces.
#
#bind-address=0.0.0.0
#
# Optional setting
#wsrep_slave_threads=1
#innodb_flush_log_at_trx_commit=0

# this is only for embedded server
[embedded]

# This group is only read by MariaDB servers, not by MySQL.
# If you use the same .cnf file for MySQL and MariaDB,
# you can put MariaDB-only options here
[mariadb]

# This group is only read by MariaDB-10.2 servers.
# If you use the same .cnf file for MariaDB of different versions,
# use this group for options that older servers don't understand
[mariadb-10.2]

[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]#
[root@vm03 my.cnf.d]# systemctl restart mariadb
[root@vm03 my.cnf.d]#
```

- 登录, 使用以下命令查看当前使用的字符集, 已经生效:
```
MariaDB [(none)]> SHOW VARIABLES LIKE 'character%';
+--------------------------+----------------------------+
| Variable_name            | Value                      |
+--------------------------+----------------------------+
| character_set_client     | utf8                       |
| character_set_connection | utf8                       |
| character_set_database   | utf8                       |
| character_set_filesystem | binary                     |
| character_set_results    | utf8                       |
| character_set_server     | utf8                       |
| character_set_system     | utf8                       |
| character_sets_dir       | /usr/share/mysql/charsets/ |
+--------------------------+----------------------------+
8 rows in set (0.00 sec)

MariaDB [(none)]> SHOW VARIABLES LIKE "%collation%";
+----------------------+-----------------+
| Variable_name        | Value           |
+----------------------+-----------------+
| collation_connection | utf8_general_ci |
| collation_database   | utf8_unicode_ci |
| collation_server     | utf8_unicode_ci |
+----------------------+-----------------+
3 rows in set (0.00 sec)

MariaDB [(none)]>
```

---

# 用户权限

- 创建用户并赋予所有操作权限:
  - `username`: 将要创建的用户名
  - `host`: 指定该用户在哪个主机上可以登陆, 如果是本地用户可用 localhost, 如果想让该用户可以从任意远程主机登陆, 可以使用通配符 %
  - `password`: 该用户的登陆密码, 密码可以为空, 如果为空则该用户可以不需要密码登陆服务器
```sql
CREATE USER 'username'@'host' IDENTIFIED BY 'password';
```

- 查看用户登录权限:
```sql
select User, host from mysql.user;
```

- 操作记录如下:
```
[root@vm03 ~]# mysql -uroot -p
Enter password:
MariaDB [(none)]> select User, host from mysql.user;
+------+-----------+
| User | host      |
+------+-----------+
| root | 127.0.0.1 |
| root | ::1       |
| root | localhost |
| root | vm03      |
+------+-----------+
4 rows in set (0.00 sec)

MariaDB [(none)]> CREATE USER 'zozo'@'%' IDENTIFIED BY '***password***';
Query OK, 0 rows affected (0.00 sec)

MariaDB [(none)]> select User, host from mysql.user;
+------+-----------+
| User | host      |
+------+-----------+
| zozo | %         |
| root | 127.0.0.1 |
| root | ::1       |
| root | localhost |
| root | vm03      |
+------+-----------+
5 rows in set (0.00 sec)

MariaDB [(none)]> exit
Bye
[root@vm03 ~]# mysql -uzozo -p
Enter password:
MariaDB [(none)]> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
+--------------------+
1 row in set (0.00 sec)

MariaDB [(none)]>
```

_此时可以远程登录, 但是没有操作权限_

- 给用户赋予操作权限 (root 用户登录):
  - `privileges`: 用户的操作权限, 如 `SELECT`, `INSERT`, `UPDATE` 等 (权限列表见文末). 如果要授予所的权限则使用 `ALL PRIVILEGES`
  - `databasename`: 数据库名
  - `tablename`: 表名, 如果要授予该用户对所有数据库和表的相应操作权限则可用表示, 如 `.*`
```sql
GRANT privileges ON databasename.tablename TO 'username'@'host';

-- 赋予部分权限, 其中的shopping.* 表示对以 shopping 所有文件操作
grant select, delete, update, insert on simpleshop.* to superboy@'localhost' identified by 'superboy';
-- 赋予所有权限
grant all privileges on simpleshop.* to superboy@localhost identified by 'iamsuperboy';
GRANT ALL PRIVILEGES ON *.* TO 'zozo'@'%';
flush privileges;
```

以下为操作记录:
```
MariaDB [(none)]> GRANT ALL PRIVILEGES ON *.* TO 'zozo'@'%';
Query OK, 0 rows affected (0.00 sec)

MariaDB [(none)]> flush privileges;
Query OK, 0 rows affected (0.00 sec)

MariaDB [(none)]> exit;
Bye
[root@vm03 ~]# mysql -uzozo -p
Enter password:
MariaDB [(none)]> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
+--------------------+
3 rows in set (0.00 sec)

MariaDB [(none)]>
```

_此时可以远程登录, 且有所有操作权限_

---

# 权限说明

| 权限 | 描述 |
| :--- | :--- |
| ALTER | Allows use of `ALTER TABLE` |
| ALTER ROUTINE | Alters or drops stored routines |





---
