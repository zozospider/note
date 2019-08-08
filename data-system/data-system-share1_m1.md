
```shell
[zozo@VM_0_17_centos bin]$ ll
总用量 2492
-rwxr-xr-x 1 zozo zozo     232 6月  30 2018 README.txt
-rwxr-xr-x 1 zozo zozo    1937 6月  30 2018 zkCleanup.sh
-rwxr-xr-x 1 zozo zozo    1056 6月  30 2018 zkCli.cmd
-rwxr-xr-x 1 zozo zozo    1534 6月  30 2018 zkCli.sh
-rwxr-xr-x 1 zozo zozo    1759 6月  30 2018 zkEnv.cmd
-rwxr-xr-x 1 zozo zozo    2696 6月  30 2018 zkEnv.sh
-rwxr-xr-x 1 zozo zozo    1089 6月  30 2018 zkServer.cmd
-rwxr-xr-x 1 zozo zozo    6773 6月  30 2018 zkServer.sh
-rwxr-xr-x 1 zozo zozo     996 6月  30 2018 zkTxnLogToolkit.cmd
-rwxr-xr-x 1 zozo zozo    1385 6月  30 2018 zkTxnLogToolkit.sh
-rw-rw-r-- 1 zozo zozo 2498687 5月   9 20:08 zookeeper.out
[zozo@VM_0_17_centos bin]$ pwd
/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin
[zozo@VM_0_17_centos bin]$ ./zkCli.sh
Connecting to localhost:2181
2019-05-09 20:10:28,390 [myid:] - INFO  [main:Environment@100] - Client environment:zookeeper.version=3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03, built on 06/29/2018 04:05 GMT
2019-05-09 20:10:28,394 [myid:] - INFO  [main:Environment@100] - Client environment:host.name=VM_0_17_centos
2019-05-09 20:10:28,394 [myid:] - INFO  [main:Environment@100] - Client environment:java.version=1.8.0_192
2019-05-09 20:10:28,396 [myid:] - INFO  [main:Environment@100] - Client environment:java.vendor=Oracle Corporation
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:java.home=/home/zozo/app/java/jdk1.8.0_192/jre
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:java.class.path=/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../build/classes:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../build/lib/*.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/slf4j-log4j12-1.7.25.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/slf4j-api-1.7.25.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/netty-3.10.6.Final.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/log4j-1.2.17.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/jline-0.9.94.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/audience-annotations-0.5.0.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../zookeeper-3.4.13.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../src/java/lib/*.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../conf:.:/home/zozo/app/java/jdk1.8.0_192/lib/dt.jar:/home/zozo/app/java/jdk1.8.0_192/lib/tools.jar
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:java.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:java.io.tmpdir=/tmp
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:java.compiler=<NA>
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:os.name=Linux
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:os.arch=amd64
2019-05-09 20:10:28,397 [myid:] - INFO  [main:Environment@100] - Client environment:os.version=3.10.0-514.21.1.el7.x86_64
2019-05-09 20:10:28,398 [myid:] - INFO  [main:Environment@100] - Client environment:user.name=zozo
2019-05-09 20:10:28,398 [myid:] - INFO  [main:Environment@100] - Client environment:user.home=/home/zozo
2019-05-09 20:10:28,398 [myid:] - INFO  [main:Environment@100] - Client environment:user.dir=/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin
2019-05-09 20:10:28,399 [myid:] - INFO  [main:ZooKeeper@442] - Initiating client connection, connectString=localhost:2181 sessionTimeout=30000 watcher=org.apache.zookeeper.ZooKeeperMain$MyWatcher@277050dc
Welcome to ZooKeeper!
2019-05-09 20:10:28,427 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1029] - Opening socket connection to server localhost/127.0.0.1:2181. Will not attempt to authenticate using SASL (unknown error)
JLine support is enabled
2019-05-09 20:10:28,513 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@879] - Socket connection established to localhost/127.0.0.1:2181, initiating session
2019-05-09 20:10:28,525 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1303] - Session establishment complete on server localhost/127.0.0.1:2181, sessionid = 0x2042db0dea60035, negotiated timeout = 30000

WATCHER::

WatchedEvent state:SyncConnected type:None path:null
[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper]
[zk: localhost:2181(CONNECTED) 1] create -e /master "master1.example.com:2223"
Created /master
[zk: localhost:2181(CONNECTED) 2] ls /
[zookeeper, master]
[zk: localhost:2181(CONNECTED) 3] get /master
master1.example.com:2223
cZxid = 0x1000005e1
ctime = Thu May 09 20:11:06 CST 2019
mZxid = 0x1000005e1
mtime = Thu May 09 20:11:06 CST 2019
pZxid = 0x1000005e1
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x2042db0dea60035
dataLength = 24
numChildren = 0
[zk: localhost:2181(CONNECTED) 4] [zozo@VM_0_17_centos bin]$ ./zkCli.sh
Connecting to localhost:2181
2019-05-09 20:17:03,575 [myid:] - INFO  [main:Environment@100] - Client environment:zookeeper.version=3.4.13-2d71af4dbe22557fda74f9a9b4309b15a7487f03, built on 06/29/2018 04:05 GMT
2019-05-09 20:17:03,579 [myid:] - INFO  [main:Environment@100] - Client environment:host.name=VM_0_17_centos
2019-05-09 20:17:03,579 [myid:] - INFO  [main:Environment@100] - Client environment:java.version=1.8.0_192
2019-05-09 20:17:03,582 [myid:] - INFO  [main:Environment@100] - Client environment:java.vendor=Oracle Corporation
2019-05-09 20:17:03,582 [myid:] - INFO  [main:Environment@100] - Client environment:java.home=/home/zozo/app/java/jdk1.8.0_192/jre
2019-05-09 20:17:03,582 [myid:] - INFO  [main:Environment@100] - Client environment:java.class.path=/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../build/classes:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../build/lib/*.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/slf4j-log4j12-1.7.25.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/slf4j-api-1.7.25.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/netty-3.10.6.Final.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/log4j-1.2.17.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/jline-0.9.94.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../lib/audience-annotations-0.5.0.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../zookeeper-3.4.13.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../src/java/lib/*.jar:/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin/../conf:.:/home/zozo/app/java/jdk1.8.0_192/lib/dt.jar:/home/zozo/app/java/jdk1.8.0_192/lib/tools.jar
2019-05-09 20:17:03,582 [myid:] - INFO  [main:Environment@100] - Client environment:java.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:java.io.tmpdir=/tmp
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:java.compiler=<NA>
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:os.name=Linux
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:os.arch=amd64
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:os.version=3.10.0-514.21.1.el7.x86_64
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:user.name=zozo
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:user.home=/home/zozo
2019-05-09 20:17:03,583 [myid:] - INFO  [main:Environment@100] - Client environment:user.dir=/home/zozo/app/zookeeper/five/zookeeper-3.4.13/bin
2019-05-09 20:17:03,585 [myid:] - INFO  [main:ZooKeeper@442] - Initiating client connection, connectString=localhost:2181 sessionTimeout=30000 watcher=org.apache.zookeeper.ZooKeeperMain$MyWatcher@277050dc
2019-05-09 20:17:03,612 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1029] - Opening socket connection to server localhost/127.0.0.1:2181. Will not attempt to authenticate using SASL (unknown error)
Welcome to ZooKeeper!
JLine support is enabled
2019-05-09 20:17:03,697 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@879] - Socket connection established to localhost/127.0.0.1:2181, initiating session
2019-05-09 20:17:03,709 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1303] - Session establishment complete on server localhost/127.0.0.1:2181, sessionid = 0x2042db0dea60036, negotiated timeout = 30000

WATCHER::

WatchedEvent state:SyncConnected type:None path:null
[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper, master, workers, tasks, assign]
[zk: localhost:2181(CONNECTED) 1] create -e /workers/worker1.example.com "worker1.example.com:2224"
Created /workers/worker1.example.com
[zk: localhost:2181(CONNECTED) 2] create /assign/worker1.example.com ""
Created /assign/worker1.example.com
[zk: localhost:2181(CONNECTED) 3] ls /assign/worker1.example.com true
[]
[zk: localhost:2181(CONNECTED) 4]
WATCHER::

WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/assign/worker1.example.com

[zk: localhost:2181(CONNECTED) 4] ls /assign/worker1.example.com
[task-0000000000]
[zk: localhost:2181(CONNECTED) 5] create /tasks/task-0000000000/status "done"
Created /tasks/task-0000000000/status
[zk: localhost:2181(CONNECTED) 6]
```

