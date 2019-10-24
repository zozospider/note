
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
- [进程](#进程)
    - [查看进程](#查看进程)
    - [进程自动拉起](#进程自动拉起)
- [删除](#删除)
    - [清空正在写的文件内容](#清空正在写的文件内容)
    - [删除 7 天前的文件](#删除-7-天前的文件)
- [查找过滤](#查找过滤)
- [统计行数](#统计行数)
- [重复, 去重](#重复-去重)
- [文件个数](#文件个数)
- [打开文件数](#打开文件数)
- [awk](#awk)

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

# 进程

## 查看进程

- 查看进程详细信息
```bash
ps -ef | grep flume
ps aux | grep flume
```

- 查看进程启动时间和运行时间
```bash
ps -p PID -o lstart,etime
```

## 进程自动拉起

- 1. 新建 `/home/zozo/app/flume/check` 文件夹
- 2. 新建 `/home/zozo/app/flume/check_flume.sh` 文件, 内容如下:
```bash
#!/bin/sh
cd $(dirname $0)

# 查找进程 | 查找 flume 关键词 | 查找 41414 关键词 | 排除 grep 关键词 | 排除 check_flume 关键词 | 统计结果条数
n=$(ps aux | grep "flume" | grep "41414" | grep -v "grep" | grep -v "check_flume" | wc -l)
# 如果结果等于 0, 表示该进程不存在, 然后启动该进程
if [ "$n" -eq "0" ]; then
  t=$(date)
  echo "$t: flume 41414 restart" >> check/check.log
  cd apache-flume-1.9.0-bin
  nohup bin/flume-ng agent -n a1 -c conf -f conf/flume-conf.properties -Dflume.monitoring.type=http -Dflume.monitoring.port=41414 &
fi
```

- 3. 新建 crontab 定时任务
```bash
# flume check
*/1 * * * * /bin/bash /home/zozo/app/flume/check_flume.sh >> /home/zozo/app/flume/check/$(date +"\%Y-\%m-\%d").log 2>&1
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
# 打印每一条重复行
sort file | uniq -d
# 打印每一条重复行和重复的次数
sort file | uniq -dc
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

# 磁盘 IO

## top

```
> top
top - 16:15:05 up 6 days,  6:25,  2 users,  load average: 1.45, 1.77, 2.14
Tasks: 147 total,   1 running, 146 sleeping,   0 stopped,   0 zombie
Cpu(s):  0.2% us,  0.2% sy,  0.0% ni, 86.9% id, 12.6% wa,  0.0% hi,  0.0% si
Mem:   4037872k total,  4003648k used,    34224k free,     5512k buffers
Swap:  7164948k total,   629192k used,  6535756k free,  3511184k cached
```

`12.6% wa`: IO 等待所占用的 CPU 时间的百分比, 高过 30% 时 IO 压力高

## IOSTAT

- [Linux下iostat监控磁盘IO状况](https://blog.csdn.net/zqtsx/article/details/25484829)

Iostat 是 sysstat 工具集的一个工具, 需要安装:
- Centos 的安装方式是: `yum install sysstat`
- Ubuntu 的安装方式是: `aptitude install sysstat`

### iostat -x

```
> iostat -x 1 10
Linux 2.6.18-92.el5xen 02/03/2009
avg-cpu: %user %nice %system %iowait %steal %idle
1.10 0.00 4.82 39.54 0.07 54.46
Device: rrqm/s wrqm/s r/s w/s rsec/s wsec/s avgrq-sz avgqu-sz await svctm %util
sda 0.00 3.50 0.40 2.50 5.60 48.00 18.48 0.00 0.97 0.97 0.28
sdb 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00
sdc 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00
sdd 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00
sde 0.00 0.10 0.30 0.20 2.40 2.40 9.60 0.00 1.60 1.60 0.08
sdf 17.40 0.50 102.00 0.20 12095.20 5.60 118.40 0.70 6.81 2.09 21.36
sdg 232.40 1.90 379.70 0.50 76451.20 19.20 201.13 4.94 13.78 2.45 93.16
```

- `rrqm/s`: 每秒进行 merge 的读操作数目, 即 `delta(rmerge)/s`
- `wrqm/s`: 每秒进行 merge 的写操作数目, 即 `delta(wmerge)/s`
- `r/s`: 每秒完成的读 I/O 设备次数, 即 `delta(rio)/s`
- `w/s`: 每秒完成的写 I/O 设备次数, 即 `delta(wio)/s`
- `rsec/s`: 每秒读扇区数, 即 `delta(rsect)/s`
- `wsec/s`: 每秒写扇区数, 即 `delta(wsect)/s`
- `rkB/s`: 每秒读K字节数, 是 `rsect/s` 的一半，因为每扇区大小为 512 字节 (需要计算)
- `wkB/s`: 每秒写K字节数, 是 `wsect/s` 的一半 (需要计算)
- `avgrq-sz`: 平均每次设备 I/O 操作的数据大小 (扇区), 即 `delta(rsect+wsect)/delta(rio+wio)`
- `avgqu-sz`: 平均 I/O 队列长度, 即 `delta(aveq)/s/1000` (因为 `aveq` 的单位为毫秒)
- `await`: 平均每次设备 I/O 操作的等待时间 (毫秒), 即 `delta(ruse+wuse)/delta(rio+wio)`
- `svctm`: 平均每次设备 I/O 操作的服务时间 (毫秒), 即 `delta(use)/delta(rio+wio)`
- `%util`: 1 秒中有百分之多少的时间用于 I/O 操作，或者说 1 秒中有多少时间 I/O 队列是非空的, 即 `delta(use)/s/1000` (因为 `use` 的单位为毫秒). 如果 `%util` 接近 100%, 说明产生的 I/O 请求太多, I/O 系统已经满负荷, 该磁盘可能存在瓶颈. `idle` 小于 70% IO 压力就较大了, 一般读取速度有较多的 wait. 同时可以结合 vmstat 查看查看 `b` 参数 (等待资源的进程数) 和 `wa` 参数 (IO 等待所占用的 CPU 时间的百分比, 高过 30% 时 IO 压力高)
另外还可以参考 `svctm` 一般要小于 await (因为同时等待的请求的等待时间被重复计算了), svctm 的大小一般和磁盘性能有关, CPU / 内存的负荷也会对其有影响, 请求过多也会间接导致 svctm 的增加. await 的大小一般取决于服务时间(svctm) 以及 I/O 队列的长度和 I/O 请求的发出模式。如果 svctm 比较接近 await，说明 I/O 几乎没有等待时间；如果 await 远大于 svctm，说明 I/O 队列太长，应用得到的响应时间变慢，如果响应时间超过了用户可以容许的范围，这时可以考虑更换更快的磁盘, 调整内核 elevator 算法, 优化应用, 或者升级 CPU. 队列长度 (`avgqu-sz`) 也可作为衡量系统 I/O 负荷的指标, 但由于 `avgqu-sz` 是按照单位时间的平均值, 所以不能反映瞬间的 I/O 洪水.

`TODO`

---

# awk

- 查找某个文件某个字段是否等于某个值, 打印整行内容:
```bash
awk -F '|' '$10 == "value" {print $0}' file
```

---
