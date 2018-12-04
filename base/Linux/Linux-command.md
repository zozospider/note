
# 端口

## 查看端口是否被占用
```
netstat -nlpt | grep 8080
```

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

# 上传 & 下载

## 本地上传文件到远程

在本机执行以下命令：
```
scp /local_path/file user@remote_ip:/remote_path
scp /Users/user/Downloads/zookeeper-3.4.13.tar.gz zozo@123.207.120.205:/home/zozo/app/zookeeper
scp /Users/user/Downloads/jdk-8u192-linux-x64.tar.gz zozo@123.207.120.205:/home/zozo/app/java
```

scp /Users/user/Downloads/zookeeper-3.4.13.tar.gz zozo@111.230.233.137:/home/zozo/app/zookeeper
scp /Users/user/Downloads/jdk-8u192-linux-x64.tar.gz zozo@111.230.233.137:/home/zozo/app/java

## 本地上传文件夹到远程

在本机执行以下命令：
```
scp -r /local_path/local_dir user@remote_ip:/remote_path/remote_dir
```

## 远程下载文件到本地

在本机执行以下命令：
```
scp user@remote_ip:/remote_path/file /local_path
```

## 远程下载文件夹到本地

在本机执行以下命令：
```
scp -r user@remote_ip:/remote_path/remote_dir /local_path/local_dir
```
