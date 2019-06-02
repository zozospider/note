- [一. hostname 和 host 设置 (本地, 伪分布式, 完全分布式都需要配置)](#一-hostname-和-host-设置-本地-伪分布式-完全分布式都需要配置)
    - [1.1 修改 hostname](#11-修改-hostname)
        - [1.1.1 非腾讯云 CentOs7: 修改 `/etc/sysconfig/network`](#111-非腾讯云-centos7-修改-etcsysconfignetwork)
        - [1.1.2 腾讯云 CentOs7: 修改 `/etc/hostname`](#112-腾讯云-centos7-修改-etchostname)
    - [1.2. 修改 hosts 映射](#12-修改-hosts-映射)
        - [1.2.1 非腾讯云 CentOs7: 修改 `/etc/hosts`](#121-非腾讯云-centos7-修改-etchosts)
        - [1.2.2 腾讯云 CentOs7 如果尝试修改 `/etc/hosts` 无效, 请修改 `/etc/cloud/templates/hosts.redhat.tmpl`](#122-腾讯云-centos7-如果尝试修改-etchosts-无效-请修改-etccloudtemplateshostsredhattmpl)
    - [1.3 重启生效](#13-重启生效)
- [二. 配置 SSH 免密登录 (仅完全分布式需要配置)](#二-配置-ssh-免密登录-仅完全分布式需要配置)
    - [2.1 案例说明](#21-案例说明)
    - [2.2 vm017 生成密钥](#22-vm017-生成密钥)
    - [2.3 vm017 发送 authorized_keys](#23-vm017-发送-authorized_keys)
    - [2.4 确认 vm06, vm03, vm017 的文件权限](#24-确认-vm06-vm03-vm017-的文件权限)
    - [2.5 vm017 测试免密登录](#25-vm017-测试免密登录)
    - [2.6 vm03 同样配置免密登录所有节点](#26-vm03-同样配置免密登录所有节点)
- [三. 下载解压, 配置环境变量 (本地, 伪分布式, 完全分布式都需要配置)](#三-下载解压-配置环境变量-本地-伪分布式-完全分布式都需要配置)
- [四. 目录结构](#四-目录结构)

---

# 一. hostname 和 host 设置 (本地, 伪分布式, 完全分布式都需要配置)

在 Hadoop 中, 各个节点会会进行如下两部操作:

- step1. 获取本机的 hostname 值
```java
String hostname = impl.getLocalHostName(); // 调用方法, 得到本机的 hostname 值
```
在 Linux 中, 该值在 `/etc/sysconfig/network` 或 `/etc/hostname` 中配置, 具体配置方式见下文.

- step2. 读取本机的 hostname 值对应的 IP
```java
InetAddress.getAddressesFromNameService(hostname, null) // 得到 hostname 对应的 IP
```
在 Linux 中, 该值在 `/etc/hosts` 或 `/etc/cloud/templates/hosts.redhat.tmpl` 中配置, 具体配置方式见下文.

以下为详细操作步骤 (腾讯云 CentOs7 与其他方式操作有区别):

## 1.1 修改 hostname

注: Hadoop 集群中的 hostname 不能含有 `_`

### 1.1.1 非腾讯云 CentOs7: 修改 `/etc/sysconfig/network`

```
[root@VM_0_17_centos ~]# cat /etc/sysconfig/network
NETWORKING=yes
HOSTNAME=vm017
```

### 1.1.2 腾讯云 CentOs7: 修改 `/etc/hostname`

```
[root@VM_0_17_centos ~]# cat /etc/hostname
vm017
[root@VM_0_17_centos ~]#
```

## 1.2. 修改 hosts 映射

如在本地 hosts 中配置了 `192.168.0.2 wwww.domain.com` 映射, 那么表示本地应用程序可以通过 `wwww.domain.com` 域名找到 IP 为 `192.168.0.2` 的远程 (或本地) 机器.

### 1.2.1 非腾讯云 CentOs7: 修改 `/etc/hosts`

增加 `172.16.0.17 vm017 vm017`, 其中 `172.16.0.17` 对应本机内网 IP, 表示本地应用程序可以通过 `vm017` 域名找到 IP 为 `172.16.0.17` 的机器.

```
[root@VM_0_17_centos ~]# cat /etc/hosts
# The following lines are desirable for IPv4 capable hosts
127.0.0.1 localhost.localdomain localhost
127.0.0.1 localhost4.localdomain4 localhost4

172.16.0.6 vm06 vm06
172.16.0.17 vm017 vm017
172.16.0.3 vm03 vm03

# The following lines are desirable for IPv6 capable hosts
::1 localhost.localdomain localhost
::1 localhost6.localdomain6 localhost6
```

### 1.2.2 腾讯云 CentOs7 如果尝试修改 `/etc/hosts` 无效, 请修改 `/etc/cloud/templates/hosts.redhat.tmpl`

注: 腾讯云 CentOs7 __请先尝试__ 通过 __2.1 非腾讯云 CentOs7: 修改 `/etc/hosts`__ 的方式. 

- 重启后如果有效, 则无需再进行其他操作.
- 重启后如果无效, 再尝试采用修改 `/etc/cloud/templates/hosts.redhat.tmpl` 的方式参考: [腾讯云修改无效解决方案](https://www.jianshu.com/p/2e27a4d7b9aa), 以下为操作步骤:

- 1. 修改 `/etc/cloud/templates/hosts.redhat.tmpl`:
```
[root@VM_0_17_centos ~]# cat /etc/cloud/templates/hosts.redhat.tmpl
## template:jinja
{#
This file /etc/cloud/templates/hosts.redhat.tmpl is only utilized
if enabled in cloud-config.  Specifically, in order to enable it
you need to add the following to config:
  manage_etc_hosts: True
-#}
# Your system has configured 'manage_etc_hosts' as True.
# As a result, if you wish for changes to this file to persist
# then you will need to either
# a.) make changes to the master file in /etc/cloud/templates/hosts.redhat.tmpl
# b.) change or remove the value of 'manage_etc_hosts' in
#     /etc/cloud/cloud.cfg or cloud-config from user-data
#
# The following lines are desirable for IPv4 capable hosts
127.0.0.1 {{fqdn}} {{hostname}}
127.0.0.1 localhost.localdomain localhost
127.0.0.1 localhost4.localdomain4 localhost4

172.16.0.6 vm06 vm06
172.16.0.17 vm017 vm017
172.16.0.3 vm03 vm03

# The following lines are desirable for IPv6 capable hosts
::1 {{fqdn}} {{hostname}}
::1 localhost.localdomain localhost
::1 localhost6.localdomain6 localhost6

[root@VM_0_17_centos ~]#
```

- 2. 重启后 `etc/hosts` 会被 `/etc/cloud/templates/hosts.redhat.tmpl` 覆盖, 如下:
```
[root@vm017 ~]# cat /etc/hosts
# Your system has configured 'manage_etc_hosts' as True.
# As a result, if you wish for changes to this file to persist
# then you will need to either
# a.) make changes to the master file in /etc/cloud/templates/hosts.redhat.tmpl
# b.) change or remove the value of 'manage_etc_hosts' in
#     /etc/cloud/cloud.cfg or cloud-config from user-data
#
# The following lines are desirable for IPv4 capable hosts
127.0.0.1 VM_0_17_centos VM_0_17_centos
127.0.0.1 localhost.localdomain localhost
127.0.0.1 localhost4.localdomain4 localhost4

172.16.0.6 vm06 vm06
172.16.0.17 vm017 vm017
172.16.0.3 vm03 vm03

# The following lines are desirable for IPv6 capable hosts
::1 VM_0_17_centos VM_0_17_centos
::1 localhost.localdomain localhost
::1 localhost6.localdomain6 localhost6

[root@vm017 ~]#
```

## 1.3 重启生效

```
[root@VM_0_17_centos ~]# reboot
Connection to 193.112.38.200 closed by remote host.
Connection to 193.112.38.200 closed.
➜  ~
```

```
➜  ~ ssh root@193.112.38.200
[root@vm017 ~]# hostname
vm017
[root@vm017 ~]#
```

# 二. 配置 SSH 免密登录 (仅完全分布式需要配置)

## 2.1 案例说明

下面为一个完全分布式的免密登录案例:

集群部署规划如下:

| 模块 / 节点 | __vm017__ | __vm06__ | __vm03__ |
| :--- | :--- | :--- | :--- |
| __HDFS__ | DataNode | DataNode | DataNode |
|  | __NameNode__ | __SecondaryNameNode__ |  |
| __YARN__ | NodeManager | NodeManager | NodeManager |
|  |  |  | __ResourceManager__ |

| 节点 / 模块 | __HDFS__ | __YARN__ |
| :--- | :--- | :--- |
| __vm017__ | DataNode | NodeManager |
|  | __NameNode__ |  |
| __vm06__ | DataNode | NodeManager |
|  | __SecondaryNameNode__ |  |
| __vm03__ | DataNode | NodeManager |
|  |  | __ResourceManager__ |

- 因为 __vm017__ 为 NameNode, 需要配置 SSH 免密登录所有节点. 这样在调用 `start-all.sh` 脚本时, 无需输入密码.
- 因为 __vm03__ 为 NodeManager, 需要配置 SSH 免密登录所有节点.

注: 需要配置免密登录所有节点 (包括本机).

- link
  - [SSH免密登录原理及实现](https://blog.csdn.net/qq_26907251/article/details/78804367)  (参考: 图)
  - [ssh免密码登录配置方法](https://blog.csdn.net/universe_hao/article/details/52296811)  (参考: 操作)
  - [SSH免密登录原理及配置](https://my.oschina.net/binxin/blog/651565)  (参考: 权限)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/SSH%E5%85%8D%E5%AF%86%E7%99%BB%E5%BD%95%E5%8E%9F%E7%90%86.png?raw=true)

`~/.ssh` 目录下文件说明:

| 文件 | 说明 |
| :--- | :--- |
| `~/.ssh/know_hosts` | 记录 SSH 访问过的其他服务器 |
| `~/.ssh/id_rsa` | 本机生成的私钥 |
| `~/.ssh/id_rsa.pub` | 本机生成的公钥 (可免密登录其他服务器) |
| `~/.ssh/authorized_keys` | 存放已授权其他服务器 (可免密登录本机 (包括本机)) 的公钥 |

以下以 __vm017__ 免密登录到所有机器的具体步骤, __vm03__ 类似操作:

## 2.2 vm017 生成密钥

如果没有 `~/.ssh`, 可以先通过 ssh 登录其他机器, 就会产生 `~/.ssh/known_hosts` 文件, 如下:
```
[zozo@vm017 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm017 .ssh]$ cat known_hosts
172.16.0.3 ecdsa-sha2-nistp256 AAAAE2VjZHNhLxxxxxxxxxxxxxxxxxxxOxBeGA7kqpUJGNbIz0EC0Mqwi0FJNc+V1aQkmx8c+olPBBUhVFSGHxyyyyyyyyyyyyyyyyyyyyys2No=
172.16.0.6 ecdsa-sha2-nistp256 AAAAE2VjZHNxxxxxxxxxxxxxxxxxxxOxBeGA7kqpUJGNbIz0EC0Mqwi0FJNc+V1YXMY+Z3zzzzzzzzzzzzzzejRVJ/gQ3OU67ybhgwwwwwwwwwwwwN8crgYxw=
[zozo@vm017 .ssh]$
```

在 __vm017__ `~/.ssh` 目录下执行以下命令, 通过 RSA 算法 (非对称加密算法) 进行加密, 提示输入 3 次回车后, 该目录下将会产生 `id_rsa` (私钥), `id_rsa.pub` (公钥) 文件:
```
[zozo@vm017 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm017 .ssh]$ ll
总用量 4
-rw-r--r-- 1 zozo zozo 344 5月  30 20:17 known_hosts
[zozo@vm017 .ssh]$ ssh-keygen -t rsa
Generating public/private rsa key pair.
Enter file in which to save the key (/home/zozo/.ssh/id_rsa):
Enter passphrase (empty for no passphrase):
Enter same passphrase again:
Your identification has been saved in /home/zozo/.ssh/id_rsa.
Your public key has been saved in /home/zozo/.ssh/id_rsa.pub.
The key fingerprint is:
SHA256:iscm/wJxOeN04zld8YgSOhQc3TdfTqLwMLW5DC7hIVs zozo@vm017
The key's randomart image is:
+---[RSA 2048]----+
|     .o+ . ..    |
|      o o = +o. o|
|     ..oE..BoB = |
|    . O=++.o=.o .|
|     =.*S=..o    |
|    .o..+..      |
|    o.=  .       |
|     =.          |
|      .o.        |
+----[SHA256]-----+
[zozo@vm017 .ssh]$ ll
总用量 12
-rw------- 1 zozo zozo 1675 6月   1 15:16 id_rsa
-rw-r--r-- 1 zozo zozo  392 6月   1 15:16 id_rsa.pub
-rw-r--r-- 1 zozo zozo  344 5月  30 20:17 known_hosts
[zozo@vm017 .ssh]$
```

## 2.3 vm017 发送 authorized_keys

将 __vm017__ 的公钥发送给 __vm06__, __vm03__, __vm017__ 完成后 __vm06__, __vm03__, __vm017__ 会生成 `~/.ssh/authorized_keys` 文件, 且该文件内容和 __vm017__ 的 `~/.ssh/id_rsa.pub` 相同.

- 以下为 __vm017__ 的操作:
```
[zozo@vm017 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm017 .ssh]$ ll
总用量 12
-rw------- 1 zozo zozo 1675 6月   1 15:16 id_rsa
-rw-r--r-- 1 zozo zozo  392 6月   1 15:16 id_rsa.pub
-rw-r--r-- 1 zozo zozo  344 5月  30 20:17 known_hosts
[zozo@vm017 .ssh]$ cat id_rsa.pub
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABTTTTTTTTxxVVjseYFy/ZNpgYFFooD5Tf8obtsVmvzbbbdccdffff0hCdaNc2P1m8ynYmeHhU8e4ZtNc2YW2ZCcn433Z6241M0/sN6HecsEBjK/3tn5jNvyWJoKFNyUofURULEhtE/0aB8F/aHArneRW5m36FPHD/huo0Cf2dfdffdfffGBQHwxjelr+3BcRY8ZPvzGljhhsLlxvC1gd/xyGorUs3814WiRNEoaYh0asiYF2RQrtUDS5xvzyvsS45glsL2yLySSr3ponD8WSBAtzS2HegJKYPEShi9zdferefdxckDY+RGJ2tDAW24/MW4JObKX1qdq7EeOVF zozo@vm017
[zozo@vm017 .ssh]$ ssh-copy-id zozo@172.16.0.6
/usr/bin/ssh-copy-id: INFO: Source of key(s) to be installed: "/home/zozo/.ssh/id_rsa.pub"
/usr/bin/ssh-copy-id: INFO: attempting to log in with the new key(s), to filter out any that are already installed
/usr/bin/ssh-copy-id: INFO: 1 key(s) remain to be installed -- if you are prompted now it is to install the new keys
zozo@172.16.0.6's password:

Number of key(s) added: 1

Now try logging into the machine, with:   "ssh 'zozo@172.16.0.6'"
and check to make sure that only the key(s) you wanted were added.

[zozo@vm017 .ssh]$ ssh-copy-id zozo@172.16.0.3
/usr/bin/ssh-copy-id: INFO: Source of key(s) to be installed: "/home/zozo/.ssh/id_rsa.pub"
/usr/bin/ssh-copy-id: INFO: attempting to log in with the new key(s), to filter out any that are already installed
/usr/bin/ssh-copy-id: INFO: 1 key(s) remain to be installed -- if you are prompted now it is to install the new keys
zozo@172.16.0.3's password:

Number of key(s) added: 1

Now try logging into the machine, with:   "ssh 'zozo@172.16.0.3'"
and check to make sure that only the key(s) you wanted were added.

[zozo@vm017 .ssh]$ ssh-copy-id zozo@172.16.0.17
/usr/bin/ssh-copy-id: INFO: Source of key(s) to be installed: "/home/zozo/.ssh/id_rsa.pub"
The authenticity of host '172.16.0.17 (172.16.0.17)' can't be established.
ECDSA key fingerprint is SHA256:+ThGOg/FjnUGE1evxEs2R4173M48Nmq9RMJbHNALqFI.
ECDSA key fingerprint is MD5:16:a6:bb:11:fd:c5:a7:5b:7b:c9:35:04:4a:2e:12:5b.
Are you sure you want to continue connecting (yes/no)? yes
/usr/bin/ssh-copy-id: INFO: attempting to log in with the new key(s), to filter out any that are already installed
/usr/bin/ssh-copy-id: INFO: 1 key(s) remain to be installed -- if you are prompted now it is to install the new keys
zozo@172.16.0.17's password:

Number of key(s) added: 1

Now try logging into the machine, with:   "ssh 'zozo@172.16.0.17'"
and check to make sure that only the key(s) you wanted were added.

[zozo@vm017 .ssh]$ ll
总用量 16
-rw------- 1 zozo zozo  392 6月   1 17:30 authorized_keys
-rw------- 1 zozo zozo 1675 6月   1 15:16 id_rsa
-rw-r--r-- 1 zozo zozo  392 6月   1 15:16 id_rsa.pub
-rw-r--r-- 1 zozo zozo  517 6月   1 17:30 known_hosts
[zozo@vm017 .ssh]$
```

- 以下为 __vm06__ 的 `~/.ssh/authorized_keys` 文件:
```
[zozo@vm06 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm06 .ssh]$ ll
总用量 8
-rw------- 1 zozo zozo 392 6月   1 16:48 authorized_keys
-rw-r--r-- 1 zozo zozo 345 6月   1 15:11 known_hosts
[zozo@vm06 .ssh]$ cat authorized_keys
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABTTTTTTTTxxVVjseYFy/ZNpgYFFooD5Tf8obtsVmvzbbbdccdffff0hCdaNc2P1m8ynYmeHhU8e4ZtNc2YW2ZCcn433Z6241M0/sN6HecsEBjK/3tn5jNvyWJoKFNyUofURULEhtE/0aB8F/aHArneRW5m36FPHD/huo0Cf2dfdffdfffGBQHwxjelr+3BcRY8ZPvzGljhhsLlxvC1gd/xyGorUs3814WiRNEoaYh0asiYF2RQrtUDS5xvzyvsS45glsL2yLySSr3ponD8WSBAtzS2HegJKYPEShi9zdferefdxckDY+RGJ2tDAW24/MW4JObKX1qdq7EeOVF zozo@vm017
[zozo@vm06 .ssh]$
```

- 以下为 __vm03__ 的 `~/.ssh/authorized_keys` 文件:
```
[zozo@vm03 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm03 .ssh]$ ll
总用量 8
-rw------- 1 zozo zozo 392 6月   1 16:51 authorized_keys
-rw-r--r-- 1 zozo zozo 345 6月   1 15:12 known_hosts
[zozo@vm03 .ssh]$ cat authorized_keys
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABTTTTTTTTxxVVjseYFy/ZNpgYFFooD5Tf8obtsVmvzbbbdccdffff0hCdaNc2P1m8ynYmeHhU8e4ZtNc2YW2ZCcn433Z6241M0/sN6HecsEBjK/3tn5jNvyWJoKFNyUofURULEhtE/0aB8F/aHArneRW5m36FPHD/huo0Cf2dfdffdfffGBQHwxjelr+3BcRY8ZPvzGljhhsLlxvC1gd/xyGorUs3814WiRNEoaYh0asiYF2RQrtUDS5xvzyvsS45glsL2yLySSr3ponD8WSBAtzS2HegJKYPEShi9zdferefdxckDY+RGJ2tDAW24/MW4JObKX1qdq7EeOVF zozo@vm017
[zozo@vm03 .ssh]$
```

- 以下为 __vm017__ 的 `~/.ssh/authorized_keys` 文件:
```
[zozo@vm017 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm017 .ssh]$ ll
总用量 16
-rw------- 1 zozo zozo  392 6月   1 17:30 authorized_keys
-rw------- 1 zozo zozo 1675 6月   1 15:16 id_rsa
-rw-r--r-- 1 zozo zozo  392 6月   1 15:16 id_rsa.pub
-rw-r--r-- 1 zozo zozo  517 6月   1 17:30 known_hosts
[zozo@vm017 .ssh]$ cat authorized_keys
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABTTTTTTTTxxVVjseYFy/ZNpgYFFooD5Tf8obtsVmvzbbbdccdffff0hCdaNc2P1m8ynYmeHhU8e4ZtNc2YW2ZCcn433Z6241M0/sN6HecsEBjK/3tn5jNvyWJoKFNyUofURULEhtE/0aB8F/aHArneRW5m36FPHD/huo0Cf2dfdffdfffGBQHwxjelr+3BcRY8ZPvzGljhhsLlxvC1gd/xyGorUs3814WiRNEoaYh0asiYF2RQrtUDS5xvzyvsS45glsL2yLySSr3ponD8WSBAtzS2HegJKYPEShi9zdferefdxckDY+RGJ2tDAW24/MW4JObKX1qdq7EeOVF zozo@vm017
[zozo@vm017 .ssh]$
```

## 2.4 确认 vm06, vm03, vm017 的文件权限

需要确保 __vm06__, __vm03__, __vm017__ 的 `~/.ssh` 文件夹权限为 `700`, `~/.ssh/authorized_keys` 文件权限为 `600`, 如下:
```
[zozo@vm06 ~]$ pwd
/home/zozo
[zozo@vm06 ~]$ ls -al
总用量 88
drwx------  6 zozo zozo  4096 6月   1 15:10 .
drwxr-xr-x. 5 root root  4096 11月 30 2018 ..
drwxrwxr-x  6 zozo zozo  4096 5月  19 16:19 app
-rw-------  1 zozo zozo 37895 6月   1 17:00 .bash_history
-rw-r--r--  1 zozo zozo    18 8月   3 2017 .bash_logout
-rw-r--r--  1 zozo zozo   602 5月  29 20:06 .bash_profile
-rw-r--r--  1 zozo zozo   231 8月   3 2017 .bashrc
drwxrwxr-x  3 zozo zozo  4096 11月 30 2018 .cache
drwxrwxr-x  3 zozo zozo  4096 11月 30 2018 .config
drwx------  2 zozo zozo  4096 6月   1 16:48 .ssh
-rw-------  1 zozo zozo 10105 5月  29 21:07 .viminfo
[zozo@vm06 ~]$ cd .ssh
[zozo@vm06 .ssh]$ ll
总用量 8
-rw------- 1 zozo zozo 392 6月   1 16:48 authorized_keys
-rw-r--r-- 1 zozo zozo 345 6月   1 15:11 known_hosts
[zozo@vm06 .ssh]$
```

如果如果权限不对需要进行设置, 如下所示:
```
[zozo@vm06 ~]$ pwd
/home/zozo
[zozo@vm06 ~]$ chmod 700 .ssh
[zozo@vm06 ~]$ ls -al
总用量 88
drwx------  6 zozo zozo  4096 6月   1 15:10 .
drwxr-xr-x. 5 root root  4096 11月 30 2018 ..
drwxrwxr-x  6 zozo zozo  4096 5月  19 16:19 app
-rw-------  1 zozo zozo 38049 6月   1 17:01 .bash_history
-rw-r--r--  1 zozo zozo    18 8月   3 2017 .bash_logout
-rw-r--r--  1 zozo zozo   602 5月  29 20:06 .bash_profile
-rw-r--r--  1 zozo zozo   231 8月   3 2017 .bashrc
drwxrwxr-x  3 zozo zozo  4096 11月 30 2018 .cache
drwxrwxr-x  3 zozo zozo  4096 11月 30 2018 .config
drwx------  2 zozo zozo  4096 6月   1 16:48 .ssh
-rw-------  1 zozo zozo 10105 5月  29 21:07 .viminfo
[zozo@vm06 ~]$ cd .ssh
[zozo@vm06 .ssh]$ chmod 600 authorized_keys
[zozo@vm06 .ssh]$ ll
总用量 8
-rw------- 1 zozo zozo 392 6月   1 16:48 authorized_keys
-rw-r--r-- 1 zozo zozo 345 6月   1 15:11 known_hosts
[zozo@vm06 .ssh]$
```

## 2.5 vm017 测试免密登录

在 __vm017__ 上测试免密登录 __vm06__, __vm03__, __vm017__:
```
[zozo@vm017 .ssh]$ ssh zozo@172.16.0.6
Last login: Sat Jun  1 15:12:09 2019 from 172.16.0.3
[zozo@vm06 ~]$ exit
登出
Connection to 172.16.0.6 closed.
[zozo@vm017 .ssh]$ ssh zozo@172.16.0.3
Last login: Sat Jun  1 15:11:05 2019 from 172.16.0.6
[zozo@vm03 ~]$ exit
登出
Connection to 172.16.0.3 closed.
[zozo@vm017 .ssh]$ ssh zozo@172.16.0.17
Last login: Sat Jun  1 16:42:32 2019 from 14.29.126.59
[zozo@vm017 ~]$ exit
登出
Connection to 172.16.0.17 closed.
[zozo@vm017 .ssh]$
```

## 2.6 vm03 同样配置免密登录所有节点

在 __vm03__ 执行类似的操作, 同样配置免密登录所有节点, 完成后如下:

- 以下为 __vm017__ 的 `~/.ssh` 文件夹内容:
```
[zozo@vm017 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm017 .ssh]$ ll
总用量 16
-rw------- 1 zozo zozo  783 6月   1 17:55 authorized_keys
-rw------- 1 zozo zozo 1675 6月   1 15:16 id_rsa
-rw-r--r-- 1 zozo zozo  392 6月   1 15:16 id_rsa.pub
-rw-r--r-- 1 zozo zozo  517 6月   1 17:30 known_hosts
[zozo@vm017 .ssh]$ cat authorized_keys
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABTTTTTTTTxxVVjseYFy/ZNpgYFFooD5Tf8obtsVmvzbbbdccdffff0hCdaNc2P1m8ynYmeHhU8e4ZtNc2YW2ZCcn433Z6241M0/sN6HecsEBjK/3tn5jNvyWJoKFNyUofURULEhtE/0aB8F/aHArneRW5m36FPHD/huo0Cf2dfdffdfffGBQHwxjelr+3BcRY8ZPvzGljhhsLlxvC1gd/xyGorUs3814WiRNEoaYh0asiYF2RQrtUDS5xvzyvsS45glsL2yLySSr3ponD8WSBAtzS2HegJKYPEShi9zdferefdxckDY+RGJ2tDAW24/MW4JObKX1qdq7EeOVF zozo@vm017
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDFK2ZAzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz3vjfwAN5SMLS6zNDA/VFVHbB3BwNCw0P2HwnReaBzqxpxg0TChQWors04yj2+XYQXc632goKf+BPj8EvBPPNkq4Ea/lv+JaI/G4ZtuvvvvvvFhGuHYVzjPC6w9TSxhR+gQJhlGbFCwqqqqqqqqqqqqqqqqqqqqqqqqqqqqCtq9G2YKbe7alFZuS7JzjvlYkMc/HxKSahNy+q1qhI+51AXUG0T7l+edt//jh0TDlWVfUrhuTX/yi91v0haixxxxx0MzSaUNqARtqrerefveqerffvfhsfhrtybvhyfbvgfyxm8JynLJn zozo@vm03
[zozo@vm017 .ssh]$
```

- 以下为 __vm016__ 的 `~/.ssh` 文件夹内容:
```
[zozo@vm06 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm06 .ssh]$ ll
总用量 8
-rw------- 1 zozo zozo 783 6月   1 17:55 authorized_keys
-rw-r--r-- 1 zozo zozo 345 6月   1 15:11 known_hosts
[zozo@vm06 .ssh]$ cat authorized_keys
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABTTTTTTTTxxVVjseYFy/ZNpgYFFooD5Tf8obtsVmvzbbbdccdffff0hCdaNc2P1m8ynYmeHhU8e4ZtNc2YW2ZCcn433Z6241M0/sN6HecsEBjK/3tn5jNvyWJoKFNyUofURULEhtE/0aB8F/aHArneRW5m36FPHD/huo0Cf2dfdffdfffGBQHwxjelr+3BcRY8ZPvzGljhhsLlxvC1gd/xyGorUs3814WiRNEoaYh0asiYF2RQrtUDS5xvzyvsS45glsL2yLySSr3ponD8WSBAtzS2HegJKYPEShi9zdferefdxckDY+RGJ2tDAW24/MW4JObKX1qdq7EeOVF zozo@vm017
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDFK2ZAzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz3vjfwAN5SMLS6zNDA/VFVHbB3BwNCw0P2HwnReaBzqxpxg0TChQWors04yj2+XYQXc632goKf+BPj8EvBPPNkq4Ea/lv+JaI/G4ZtuvvvvvvFhGuHYVzjPC6w9TSxhR+gQJhlGbFCwqqqqqqqqqqqqqqqqqqqqqqqqqqqqCtq9G2YKbe7alFZuS7JzjvlYkMc/HxKSahNy+q1qhI+51AXUG0T7l+edt//jh0TDlWVfUrhuTX/yi91v0haixxxxx0MzSaUNqARtqrerefveqerffvfhsfhrtybvhyfbvgfyxm8JynLJn zozo@vm03
[zozo@vm06 .ssh]$
```

- 以下为 __vm03__ 的 `~/.ssh` 文件夹内容:
```
[zozo@vm03 .ssh]$ pwd
/home/zozo/.ssh
[zozo@vm03 .ssh]$ ll
总用量 16
-rw------- 1 zozo zozo  783 6月   1 17:56 authorized_keys
-rw------- 1 zozo zozo 1679 6月   1 17:54 id_rsa
-rw-r--r-- 1 zozo zozo  391 6月   1 17:54 id_rsa.pub
-rw-r--r-- 1 zozo zozo  517 6月   1 17:56 known_hosts
[zozo@vm03 .ssh]$ cat authorized_keys
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABTTTTTTTTxxVVjseYFy/ZNpgYFFooD5Tf8obtsVmvzbbbdccdffff0hCdaNc2P1m8ynYmeHhU8e4ZtNc2YW2ZCcn433Z6241M0/sN6HecsEBjK/3tn5jNvyWJoKFNyUofURULEhtE/0aB8F/aHArneRW5m36FPHD/huo0Cf2dfdffdfffGBQHwxjelr+3BcRY8ZPvzGljhhsLlxvC1gd/xyGorUs3814WiRNEoaYh0asiYF2RQrtUDS5xvzyvsS45glsL2yLySSr3ponD8WSBAtzS2HegJKYPEShi9zdferefdxckDY+RGJ2tDAW24/MW4JObKX1qdq7EeOVF zozo@vm017
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDFK2ZAzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz3vjfwAN5SMLS6zNDA/VFVHbB3BwNCw0P2HwnReaBzqxpxg0TChQWors04yj2+XYQXc632goKf+BPj8EvBPPNkq4Ea/lv+JaI/G4ZtuvvvvvvFhGuHYVzjPC6w9TSxhR+gQJhlGbFCwqqqqqqqqqqqqqqqqqqqqqqqqqqqqCtq9G2YKbe7alFZuS7JzjvlYkMc/HxKSahNy+q1qhI+51AXUG0T7l+edt//jh0TDlWVfUrhuTX/yi91v0haixxxxx0MzSaUNqARtqrerefveqerffvfhsfhrtybvhyfbvgfyxm8JynLJn zozo@vm03
[zozo@vm03 .ssh]$
```

- 以下为在 __vm013__ 上测试免密登录 __vm017__, __vm06__, __vm03__:
```
[zozo@vm03 .ssh]$ ssh zozo@172.16.0.17
Last login: Sat Jun  1 17:33:35 2019 from 172.16.0.17
[zozo@vm017 ~]$ exit
登出
Connection to 172.16.0.17 closed.
[zozo@vm03 .ssh]$ ssh zozo@172.16.0.6
Last login: Sat Jun  1 17:09:32 2019 from 172.16.0.17
[zozo@vm06 ~]$ exit
登出
Connection to 172.16.0.6 closed.
[zozo@vm03 .ssh]$ ssh zozo@172.16.0.3
Last login: Sat Jun  1 17:09:45 2019 from 172.16.0.17
[zozo@vm03 ~]$ exit
登出
Connection to 172.16.0.3 closed.
[zozo@vm03 .ssh]$
```

---

# 三. 下载解压, 配置环境变量 (本地, 伪分布式, 完全分布式都需要配置)

Apache 版本官网和下载地址如下:
- 官网地址: http://hadoop.apache.org/
- 下载地址: https://archive.apache.org/dist/hadoop/common/

下载后解压到指定路径, 然后配置环境变量, 以下为 `~/.bash_profile` 内容:
```bash
# set hadoop
export HADOOP_HOME=/home/zozo/app/hadoop/hadoop-2.7.2
export PATH=$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$PATH

# set java
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
```

---

# 四. 目录结构

- __bin__: 命令 (重要)
- __sbin__: Hadoop 启动停止等命令, 大部分命令都会使用 (重要)
- __etc__: 配置文件, 需修改 (重要)
- __lib__: 本地库
- __libexec__: 本地库
- __include__: 其他语言如 C 语言库
- __share__: 说明文档, 案例

---
