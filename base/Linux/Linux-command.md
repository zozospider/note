
- [Document & Code](#document--code)
- [端口](#端口)
    - [查看端口是否被占用](#查看端口是否被占用)
- [用户名 & 密码](#用户名--密码)
    - [查看所有用户](#查看所有用户)
    - [添加用户](#添加用户)
    - [root 用户修改其他用户密码](#root-用户修改其他用户密码)
    - [用户修改自身密码](#用户修改自身密码)
- [上传 & 下载](#上传--下载)
    - [本地上传文件到远程](#本地上传文件到远程)
    - [本地上传文件夹到远程](#本地上传文件夹到远程)
    - [远程下载文件到本地](#远程下载文件到本地)
    - [远程下载文件夹到本地](#远程下载文件夹到本地)
- [解压 & 压缩](#解压--压缩)
    - [tar 命令](#tar-命令)
    - [常用](#常用)
    - [lzop](#lzop)
- [文件编码](#文件编码)
- [crontab](#crontab)
    - [注意事项](#注意事项)
    - [案例](#案例)
    - [追加到文件](#追加到文件)
- [删除](#删除)
    - [清空正在写的文件内容](#清空正在写的文件内容)
    - [删除 7 天前的文件](#删除-7-天前的文件)
- [查找过滤](#查找过滤)
- [打开文件数](#打开文件数)

---

# Document & Code

- [命令行的艺术](https://github.com/jlevy/the-art-of-command-line/blob/master/README-zh.md)
- [Linux工具快速教程](https://linuxtools-rst.readthedocs.io/zh_CN/latest/index.html)

---

# 端口

## 查看端口是否被占用
```
netstat -nlpt | grep 8080
```

---

# 用户名 & 密码

## 查看所有用户

执行以下命令：
```
grep bash /etc/passwd
```

## 添加用户

添加用户有两种方式，`adduser` 和 `useradd`。

* `adduser`: 会自动创建用户主目录，系统 shell 版本，创建时需要输入密码。
```
adduser zozo
```

* `useradd`: 需要使用参数指定基本设置，如果不使用任何参数，则创建的用户无主目录、无 shell 版本、无密码。

## root 用户修改其他用户密码

先执行以下命令，然后输入两次新密码即可。
```
passwd zozo
```

## 用户修改自身密码

执行以下命令，系统会要求输入原密码和新密码。
```
passwd
```

---

# 上传 & 下载

## 本地上传文件到远程

在本机执行以下命令：
```
scp /local_path/local_file remote_user@remote_ip:/remote_path
scp /Users/user/Downloads/zookeeper-3.4.13.tar.gz zozo@123.207.120.205:/home/zozo/app/zookeeper
scp /Users/user/Downloads/jdk-8u192-linux-x64.tar.gz zozo@123.207.120.205:/home/zozo/app/java
```

## 本地上传文件夹到远程

在本机执行以下命令：
```
scp -r /local_path/local_dir remote_user@remote_ip:/remote_path/remote_dir
```

## 远程下载文件到本地

在本机执行以下命令：
```
scp remote_user@remote_ip:/remote_path/remote_file /local_path
```

## 远程下载文件夹到本地

在本机执行以下命令：
```
scp -r remote_user@remote_ip:/remote_path/remote_dir /local_path/local_dir
```

---

# 解压 & 压缩 

## tar 命令

以下为 tar 命令参数：
* `-x`: 解压
* `-c`: 压缩
* `-t`: 查看

* `-z`: 有 gzip 属性的文件（`*.tar.gz` / `*.tgz`）
* `-v`: 显示过程

* `-f`: 在最后且必须

## 常用

以下为常用格式的解压 & 压缩命令：

`*.tar`
```
tar -xf File.tar

tar -cf File.tar Dir
```

`*.tar.gz` / `*.tgz`
```
tar -xzf File.tar.gz
tar -xzf File.tar.gz -C Dir

tar -czf File.tar.gz Dir
tar -czf File.tar.gz --exclude=tomcat/logs --exclude=tomcat/libs --exclude=tomcat/xiaoshan.txt Dir
```

`*.zip`
```
unzip File.zip
unzip File.zip -d Dir

zip File.zip Dir
```

`*.tar.xz`
```
xz -dk File.tar.xz
tar -xf File.tar

xz -zk Dir
```

## lzop

lzop 工具最适合在注重压缩速度的场合, 压缩文件时会新建 .lzo 文件, 而原文件保持不变 (使用 -U 选项除外)

- `lzop -v test`: 创建 test.lzo 压缩文件, 输出详细信息, 保留 test 文件不变
- `lzop -Uv test`: 创建 test.lzo 压缩文件, 输出详细信息, 删除 test 文件
- `lzop -t test.lzo`: 测试 test.lzo 压缩文件的完整性
- `lzop –info test.lzo`: 列出 test.lzo 中各个文件的文件头
- `lzop -l test.lzo`: 列出 test.lzo 中各个文件的压缩信息
- `lzop –ls test.lzo`: 列出 test.lzo 文件的内容, 同 ls -l 功能
- `cat test | lzop > t.lzo`: 压缩标准输入并定向到标准输出
- `lzop -dv test.lzo`: 解压 test.lzo 得到 test 文件, 输出详细信息, 保留 test.lzo 不变

---

# 文件编码

如需查看文件编码, 执行以下命令:
```
enca file
```

---

# crontab

- [crontab 定时任务](https://linuxtools-rst.readthedocs.io/zh_CN/latest/tool/crontab.html)

## 注意事项

因为 crontab 调用脚本和用户手动执行脚本的环境不一样，可能导致 crontab 执行脚本失败。所以脚本中需要保证环境变量被正确引入。

如下，curator 执行的环境变量需要在脚本中通过 `export CURATOR_HOME=xxx` 指定，另外 `curator.yml` 中调用了 `logfile_today` 环境变量，也需要在脚本中通过 `export logfile_today=xxx` 指定:
```shell
#!/bin/sh

# source /etc/profile

export CURATOR_HOME=/home/user/.local
export PATH=$CURATOR_HOME/bin:$PATH

today=`date +"%Y-%m-%d"`
export logfile_today=/home/user/app/curator/alias-$today.log

curator --config /home/user/app/curator/curator.yml /home/user/app/curator/action.yml
```

## 案例

- 每 1 分钟执行一次 myCommand:
```
* * * * * myCommand
```

- 每小时的第 3 和第 15 分钟执行:
```
3,15 * * * * myCommand
```

- 在上午 8 点到 11 点的第 3 和第 15 分钟执行:
```
3,15 8-11 * * * myCommand
```

- 每隔两天的上午 8 点到 11 点的第 3 和第 15 分钟执行:
```
3,15 8-11 */2  *  * myCommand
```

- 每周一上午 8 点到 11 点的第 3 和第 15 分钟执行:
```
3,15 8-11 * * 1 myCommand
```

- 每晚的 21:30 重启 smb:
```
30 21 * * * /etc/init.d/smb restart
```

- 每月 1, 10, 22 日的 4:45 重启 smb:
```
45 4 1,10,22 * * /etc/init.d/smb restart
```

- 每周六, 周日的 1:10 重启 smb
```
10 1 * * 6,0 /etc/init.d/smb restart
```

- 每天 18:00 至 23:00 之间每隔 30 分钟重启 smb:
```
0,30 18-23 * * * /etc/init.d/smb restart
```

- 每星期六的晚上 11:00 pm 重启 smb:
```
0 23 * * 6 /etc/init.d/smb restart
```

- 每一小时重启 smb
```
* */1 * * * /etc/init.d/smb restart
```

- 晚上 11 点到早上 7 点之间, 每隔一小时重启 smb:
```
0 23-7 * * * /etc/init.d/smb restart
```

## 追加到文件

- 每天 12 点执行 test.php, 执行的输出会追加到 test.log 文件 (2>&1 表示把标准错误输出重定向到与标准输出一致, 即 test.log ):
```
0 12 * * * php /Users/fdipzone/test.php >> /Users/fdipzone/test.log 2>&1
```

- 使用小时命名:
```
* * * * * php /Users/fdipzone/test.php >> "/Users/fdipzone/$(date +"\%Y-\%m-\%d_\%H").log" 2>&1
```

- 每天 12 点执行, 使用当天日期来命名重定向文件:
```
0 12 * * * php /Users/fdipzone/test.php >> "/Users/fdipzone/$(date +"\%Y-\%m-\%d").log" 2>&1
```

- 使用月份命名:
```
0 12 * * * php /Users/fdipzone/test.php >> "/Users/fdipzone/$(date +"\%Y-\%m").log" 2>&1
```

- 使用周命名:
```
0 12 * * * php /Users/fdipzone/test.php >> "/Users/fdipzone/$(date +"\%Y-W\%W").log" 2>&1
```

---

# 删除

## 清空正在写的文件内容

执行以下命令:
```
> file
```

## 删除 7 天前的文件

crontab 配置如下:
```bash
# remove data
0 */1 * * * /home/zozo/data/remove.sh
```

- 方式 1 (推荐): `remove.sh` 脚本如下:
```bash
#!/bin/sh

# remove data before 7 days
find /home/zozo/data/d1 -mtime +7 -name "backup-d1-*.log" -exec rm {} \;
find /home/zozo/data/d2 -mtime +7 -name "backup-d2-*.log" -exec rm {} \;
```

- 方式 2: `remove.sh` 脚本如下:
```bash
#!/bin/sh

# remove data before 7 days
/bin/find /home/zozo/data/d1 -ctime +7 -delete;
/bin/find /home/zozo/data/d1 -ctime +7 -delete;
```

---

# 查找过滤

排除字符:
```
cat file | grep -v EXCLUDE
```

---

# 统计行数

统计文件行数:
```
wc -l file
```

---

# 重复, 去重

- 查找重复行数, 打印到控制台:
```
sort file | uniq -d | wc -l
```

- 查找重复行, 打印到控制台:
```bash
sort file | uniq -d
```

- 查找非重复行, 打印到控制台:
```bash
sort file | uniq -u
```

- 去除重复行, 打印到控制台:
```bash
sort file | uniq
```

- 统计每一行出现的次数, 打印到控制台:
```
sort file | uniq -c
```

- 去重后, 输出到 `xxx.new` 文件中:
```bash
#!/bin/sh

file='test.txt'
# 方法 1
sort -n $file | uniq > $file\.new
# 方法 2
# sort -n $file | awk '{if($0!=line)print; line=$0}'
# 方法 3
# sort -n $file | sed '$!N; /^\(.*\)\n\1$/!P; D'
```

---

# 文件个数

-  统计当前目录下文件的个数 (不包括目录)
```bash
ls -l | grep "^-" | wc -l
```

- 统计当前目录下文件的个数 (包括子目录)
```bash
ls -lR | grep "^-" | wc -l
ls -lR dir | grep "^-" | wc -l
```

- 统计当前目录下文件夹的个数 (不包括文件)
```bash
ls -l | grep "^d" | wc -l
```

---

# 打开文件数

- 通过以下命令可以查看用户级别支持的最大打开文件数
```
ulimit -n
```

- 也可以通过以下命令查看, 其中 `open files` 为最大打开文件数
```
ulimit -a
```

- 通过以下命令可以查看系统级别支持的最大打开文件数
```
cat /proc/sys/fs/file-max
```

- 也可以通过以下命令查看, 其中 `fs.file-max` 为最大打开文件数
```
sysctl -a
sysctl -a | grep fs
```

---

# awk

- 查找某个文件某个字段是否等于某个值, 打印整行内容:
```bash
awk -F '|' '$10 == "value" {print $0}' file
```

---
