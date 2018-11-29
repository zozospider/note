
# ACL 命令行 auth

使用 `setAcl /acc auth:zz:password:cdrwa` 使得 zz 用户具有权限。

注意：在设置前需要通过 `addauth digest zz:password` 注册该用户。设置的 `password` 密码在内部以 `X9c92HzVP35xlknPPYEgZobCNWk=` 密文方式存储。

```
[zk: localhost:2181(CONNECTED) 3] getAcl /acc
'world,'anyone
: cdrwa
[zk: localhost:2181(CONNECTED) 4] setAcl /acc auth:zz:password:cdrwa
Acl is not valid : /acc
[zk: localhost:2181(CONNECTED) 5] addauth digest zz:password
[zk: localhost:2181(CONNECTED) 6] setAcl /acc auth:zz:password:cdrwa
cZxid = 0x21
ctime = Thu Nov 29 12:30:44 CST 2018
mZxid = 0x21
mtime = Thu Nov 29 12:30:44 CST 2018
pZxid = 0x23
cversion = 1
dataVersion = 0
aclVersion = 3
ephemeralOwner = 0x0
dataLength = 3
numChildren = 1
[zk: localhost:2181(CONNECTED) 7] getAcl /acc
'digest,'zz:X9c92HzVP35xlknPPYEgZobCNWk=
: cdrwa
```

# ACL 命令行 digest

出现演示与实际操作结果不符的情况。待定。

# ACL 命令行 ip



# ACL 之 super 超级管理员

1. 修改 zkServer.sh 增加 super 用户：
```
140     nohup "$JAVA" "-Dzookeeper.log.dir=${ZOO_LOG_DIR}" "-Dzookeeper.root.log    ger=${ZOO_LOG4J_PROP}" \  （修改前）

140     nohup "$JAVA" "-Dzookeeper.log.dir=${ZOO_LOG_DIR}" "-Dzookeeper.root.log    ger=${ZOO_LOG4J_PROP}" "-Dzookeeper.DigestAuthenticationProvider.superDigest=zozo:X9c92HzVP35xlknPPYEgZobCNWk=" \  （修改后）
```

2. 然后重启 server。

```
./zkServer.sh restart
```

# ACL 的常用使用场景

1. 开发/测试环境分离，不同用户账号权限分离。
2. 控制 ip 访问服务。

# ZooKeeper 四字命令 Four Letter Words

通过四字命令简单命令来和服务器交互。

需要先安装 yum install nc.

命令格式为：`echo [command] | nc [ip] [port]` ，所有命令请参考：[ZooKeeper Administrator's Guide](http://zookeeper.apache.org/doc/r3.4.13/zookeeperAdmin.html#sc_zkCommands)

以下为部分常用命令：
* stat: 查看 ZooKeeper 状态信息，是否 mode。
* ruok: 查看 ZooKeeper 是否启动，如果启动则返回 imok。
* dump: 列出未经处理的会话和临时节点。
* conf: 查看服务器配置。
* cons: 展示连接到服务器的客户端信息。
* envi: 环境变量。
* mntr: 监控 ZooKeeper 健康信息。
* wchs: 展示 watch 信息。
* wchc 与 wchp: session 与 watch 及 path 与 watch 信息。

其中，部分命令需要权限（如 wchc 与 wchp），可在 zoo.cfg 中添加 `4lw.commands.whitelist=*` 配置来允许所有命令执行。也可通过 `4lw.commands.whitelist=stat, ruok, conf, isro` 配置来允许指定命令运行。


