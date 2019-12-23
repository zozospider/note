
# Document & Code

* [../Zookeeper-video](https://github.com/zozospider/note/blob/master/distributed/ZooKeeper/ZooKeeper-video.md)

---

# ZooKeeper 下载、安装以及配置环境变量

参考 [ZooKeeper安装和配置](https://www.jianshu.com/p/de90172ea680)

# ZooKeeper 文件夹主要目录介绍

* `bin`: 主要的运行命令。
* `conf`: 存放配置文件，主要修改 `zk.cfg`。
* `contrib`: 附件功能。
* `dist-maven`: mvn 编译后的目录。
* `docs`: 文档。
* `lib`: 需要依赖的 jar 包。
* `recipes`: 案例 Demo 代码。
* `src`: 源码。

# ZooKeeper 配置文件介绍，运行 ZooKeeper

## ./conf/zoo.cfg 配置

* `tickTime`: 用于计算时间的单元，其他配置为该数值的整数倍。如 session 超时：N * tickTime。
* `initLimit`: 用于集群，从节点连接并同步到 Master 节点的初始化连接时间（tickTime 的倍数）。
* `syncLimit`: 用于集群，Master 节点与从节点之间发送消息，请求和应答的时间长度（心跳机制）（tickTime 的倍数）。
* `dataDir`: 数据目录。
* `dataLogDir`: 日志目录，默认和 dataDir 共享。
* `clientPort`: 连接服务器的端口，默认 2181。

## ./bin/zkServer.sh

`./zkServer.sh ` 可携带如下参数：
```
[centos@VM_0_6_centos bin]$ ./zkServer.sh
ZooKeeper JMX enabled by default
Using config: /home/centos/app/zookeeper/zookeeper-3.4.13/bin/../conf/zoo.cfg
Usage: ./zkServer.sh {start|start-foreground|stop|restart|status|upgrade|print-cmd}
```

以下为部分参数介绍：
- `./zkServer.sh start`: 启动 ZooKeeper.
- `nohup bin/zkServer.sh start &`: 后台启动.
- `./zkServer.sh status`: 查看当前 ZooKeeper 状态.
- `./zkServer.sh stop`: 停止 ZooKeeper.
- `./zkServer.sh restart`: 如果当前 ZooKeeper 正在运行, 则先 stop, 再 start. 如果当前 ZooKeeper 没有运行, 则 start.

以下为部分运行记录：
```
[centos@VM_0_6_centos bin]$ ./zkServer.sh
ZooKeeper JMX enabled by default
Using config: /home/centos/app/zookeeper/zookeeper-3.4.13/bin/../conf/zoo.cfg
Usage: ./zkServer.sh {start|start-foreground|stop|restart|status|upgrade|print-cmd}
[centos@VM_0_6_centos bin]$ ./zkServer.sh start
ZooKeeper JMX enabled by default
Using config: /home/centos/app/zookeeper/zookeeper-3.4.13/bin/../conf/zoo.cfg
Starting zookeeper ... STARTED
```
```
[centos@VM_0_6_centos bin]$ ./zkServer.sh status
ZooKeeper JMX enabled by default
Using config: /home/centos/app/zookeeper/zookeeper-3.4.13/bin/../conf/zoo.cfg
Mode: standalone
```
```
[centos@VM_0_6_centos bin]$ ./zkServer.sh stop
ZooKeeper JMX enabled by default
Using config: /home/centos/app/zookeeper/zookeeper-3.4.13/bin/../conf/zoo.cfg
Stopping zookeeper ... STOPPED
```
```
[centos@VM_0_6_centos bin]$ ./zkServer.sh restart
ZooKeeper JMX enabled by default
Using config: /home/centos/app/zookeeper/zookeeper-3.4.13/bin/../conf/zoo.cfg
ZooKeeper JMX enabled by default
Using config: /home/centos/app/zookeeper/zookeeper-3.4.13/bin/../conf/zoo.cfg
Stopping zookeeper ... STOPPED
ZooKeeper JMX enabled by default
Using config: /home/centos/app/zookeeper/zookeeper-3.4.13/bin/../conf/zoo.cfg
Starting zookeeper ... STARTED
```


