
# 安装 netcat

在 root 用户下运行如下命令:

```bash
# 安装
yum install nc

# 检查
nc -help
```

# 命令

```bash
# 启动 7777 端口, 并向端口发送数据
# 如果连接的程序断开, 当前服务也断开
nc -lk 7777
hello word
hello
hello
```
