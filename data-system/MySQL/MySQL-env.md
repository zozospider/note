
安装记录:
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