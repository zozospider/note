
# 下载解压

Apache 版本官网和下载地址如下:
- 官网地址: http://hadoop.apache.org/
- 下载地址: https://archive.apache.org/dist/hadoop/common/

下载后解压到指定路径, 并配置环境变量:
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

# 目录结构

- __bin__: 命令 (重要)
- __sbin__: Hadoop 启动停止等命令, 大部分命令都会使用 (重要)
- __etc__: 配置文件, 需修改 (重要)
- __lib__: 本地库
- __libexec__: 本地库
- __include__: 其他语言如 C 语言库
- __share__: 说明文档, 案例

---

# hostname 和 host 设置

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

## 1 修改 hostname

注: Hadoop 集群中的 hostname 不能含有 `_`

### 1.1 非腾讯云 CentOs7: 修改 `/etc/sysconfig/network`

```
[root@VM_0_17_centos ~]# cat /etc/sysconfig/network
NETWORKING=yes
HOSTNAME=vm017
```

### 1.2 腾讯云 CentOs7: 修改 `/etc/hostname`

```
[root@VM_0_17_centos ~]# cat /etc/hostname
vm017
[root@VM_0_17_centos ~]#
```

## 2. 修改 hosts 映射

如在本地 hosts 中配置了 `192.168.0.2 wwww.domain.com` 映射, 那么表示本地应用程序可以通过 `wwww.domain.com` 域名找到 IP 为 `192.168.0.2` 的远程 (或本地) 机器.

### 2.1 非腾讯云 CentOs7: 修改 `/etc/hosts`

增加 `172.16.0.17 vm017 vm017`, 其中 `172.16.0.17` 对应本机内网 IP, 表示本地应用程序可以通过 `vm017` 域名找到 IP 为 `172.16.0.17` 的机器.

```
[root@VM_0_17_centos ~]# cat /etc/hosts
# The following lines are desirable for IPv4 capable hosts
127.0.0.1 localhost.localdomain localhost
127.0.0.1 localhost4.localdomain4 localhost4

172.16.0.17 vm017 vm017

# The following lines are desirable for IPv6 capable hosts
::1 localhost.localdomain localhost
::1 localhost6.localdomain6 localhost6
```

### 2.2 腾讯云 CentOs7: 修改 `/etc/cloud/templates/hosts.redhat.tmpl` 而非 `/etc/hosts`

注: [腾讯云修改无效解决方案](https://www.jianshu.com/p/2e27a4d7b9aa)

增加 `172.16.0.17 vm017 vm017`, 其中 `172.16.0.17` 对应本机内网 IP, 表示本地应用程序可以通过 `vm017` 域名找到 IP 为 `172.16.0.17` 的机器.

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

172.16.0.17 vm017 vm017

# The following lines are desirable for IPv6 capable hosts
::1 {{fqdn}} {{hostname}}
::1 localhost.localdomain localhost
::1 localhost6.localdomain6 localhost6

[root@VM_0_17_centos ~]#
```

重启后 `etc/hosts` 会被 `/etc/cloud/templates/hosts.redhat.tmpl` 覆盖, 如下:

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

172.16.0.17 vm017 vm017

# The following lines are desirable for IPv6 capable hosts
::1 VM_0_17_centos VM_0_17_centos
::1 localhost.localdomain localhost
::1 localhost6.localdomain6 localhost6

[root@vm017 ~]#
```

## 3 重启生效

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

