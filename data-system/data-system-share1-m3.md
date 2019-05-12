
```shell
[zozo@VM_0_6_centos zookeeper-3.4.13]$ cd bin
[zozo@VM_0_6_centos bin]$ ./zkCli.sh
Connecting to localhost:2181
2019-05-09 20:24:16,544 [myid:] - INFO  [main:Environment@100] - Client environment:zookeeper.version=3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03, built on 06/29/2018 04:05 GMT
2019-05-09 20:24:16,674 [myid:] - INFO  [main:Environment@100] - Client environment:host.name=VM_0_6_centos
2019-05-09 20:24:16,674 [myid:] - INFO  [main:Environment@100] - Client environment:java.version=1.8.0_192
2019-05-09 20:24:16,739 [myid:] - INFO  [main:Environment@100] - Client environment:java.vendor=Oracle Corporation
2019-05-09 20:24:16,739 [myid:] - INFO  [main:Environment@100] - Client environment:java.home=/home/zozo/app/java/jdk1.8.0_192/jre
2019-05-09 20:24:16,739 [myid:] - INFO  [main:Environment@100] - Client environment:java.class.path=/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../build/classes:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../build/lib/*.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/slf4j-log4j12-1.7.25.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/slf4j-api-1.7.25.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/netty-3.10.6.Final.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/log4j-1.2.17.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/jline-0.9.94.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/audience-annotations-0.5.0.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../zookeeper-3.4.13.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../src/java/lib/*.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../conf:.:/home/zozo/app/java/jdk1.8.0_192/lib/dt.jar:/home/zozo/app/java/jdk1.8.0_192/lib/tools.jar
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:java.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:java.io.tmpdir=/tmp
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:java.compiler=<NA>
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:os.name=Linux
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:os.arch=amd64
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:os.version=3.10.0-693.el7.x86_64
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:user.name=zozo
2019-05-09 20:24:16,740 [myid:] - INFO  [main:Environment@100] - Client environment:user.home=/home/zozo
2019-05-09 20:24:16,741 [myid:] - INFO  [main:Environment@100] - Client environment:user.dir=/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin
2019-05-09 20:24:16,742 [myid:] - INFO  [main:ZooKeeper@442] - Initiating client connection, connectString=localhost:2181 sessionTimeout=30000 watcher=org.apache.zookeeper.ZooKeeperMain$MyWatcher@277050dc
Welcome to ZooKeeper!
2019-05-09 20:24:17,763 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1029] - Opening socket connection to server localhost/127.0.0.1:2181. Will not attempt to authenticate using SASL (unknown error)
JLine support is enabled
2019-05-09 20:24:18,526 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@879] - Socket connection established to localhost/127.0.0.1:2181, initiating session
2019-05-09 20:24:18,569 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1303] - Session establishment complete on server localhost/127.0.0.1:2181, sessionid = 0x1083cee35470003, negotiated timeout = 30000

WATCHER::

WatchedEvent state:SyncConnected type:None path:null
[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper, master, workers, tasks, assign]
[zk: localhost:2181(CONNECTED) 1] create -s /tasks/task- "cmd"
Created /tasks/task-0000000000
[zk: localhost:2181(CONNECTED) 2] ls /tasks/task-0000000000 true
[]
[zk: localhost:2181(CONNECTED) 3]
WATCHER::

WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/tasks/task-0000000000

[zk: localhost:2181(CONNECTED) 3] get /tasks/task-0000000000
cmd
cZxid = 0x1000005ec
ctime = Thu May 09 20:24:48 CST 2019
mZxid = 0x1000005ec
mtime = Thu May 09 20:24:48 CST 2019
pZxid = 0x1000005ee
cversion = 1
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 3
numChildren = 1
[zk: localhost:2181(CONNECTED) 4] get /tasks/task-0000000000/status
done
cZxid = 0x1000005ee
ctime = Thu May 09 20:27:08 CST 2019
mZxid = 0x1000005ee
mtime = Thu May 09 20:27:08 CST 2019
pZxid = 0x1000005ee
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 4
numChildren = 0
[zk: localhost:2181(CONNECTED) 5]
```