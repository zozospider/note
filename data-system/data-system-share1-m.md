
# step1 m1

- 创建 /master 成为主节点成功

```
[zozo@VM_0_17_centos bin]$ ./zkCli.sh
Connecting to localhost:2181
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
[zk: localhost:2181(CONNECTED) 4] 
```

# step2 m2

- 创建 /master 成为主节点失败，监控 /master 变化

```
[zozo@VM_0_3_centos bin]$ ./zkCli.sh
Connecting to localhost:2181
[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper, master]
[zk: localhost:2181(CONNECTED) 1] create -e /master "master2.example.com:2223"
Node already exists: /master
[zk: localhost:2181(CONNECTED) 2] stat /master true
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
[zk: localhost:2181(CONNECTED) 3] 
```

# step3 m1

- 断开主节点连接（模拟节点崩溃）

```
[zk: localhost:2181(CONNECTED) 4] 
```

# step4 m2

- 监控到 /master 变化，再次创建 /master 称为主节点成功

```
[zk: localhost:2181(CONNECTED) 3]
WATCHER::

WatchedEvent state:SyncConnected type:NodeDeleted path:/master

[zk: localhost:2181(CONNECTED) 3] ls /
[zookeeper]
[zk: localhost:2181(CONNECTED) 4] create -e /master "master2.example.com:2223"
Created /master
```

# step5 m2

- 创建 /workers（从节点） /tasks（任务） /assign（节点任务分配）
- 监控 /workers /tasks 子节点变化情况

```
[zk: localhost:2181(CONNECTED) 5] create /workers ""
Created /workers
[zk: localhost:2181(CONNECTED) 6] create /tasks ""
Created /tasks
[zk: localhost:2181(CONNECTED) 7] create /assign ""
Created /assign
[zk: localhost:2181(CONNECTED) 8] ls /
[zookeeper, master, workers, tasks, assign]
[zk: localhost:2181(CONNECTED) 9] ls /workers true
[]
[zk: localhost:2181(CONNECTED) 10] ls /tasks true
[]
[zk: localhost:2181(CONNECTED) 11] 
```

# step6 m1

- 监控到 /workers 被创建，建立临时子节点 /workers/worker1.example.com（此时主机点会监控到 /workers 的子节点变化情况）
- 监控到 /assign 被创建，建立子节点 /assign/worker1.example.com

```
[zozo@VM_0_17_centos bin]$ ./zkCli.sh
Connecting to localhost:2181
[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper, master, workers, tasks, assign]
[zk: localhost:2181(CONNECTED) 1] create -e /workers/worker1.example.com "worker1.example.com:2224"
Created /workers/worker1.example.com
[zk: localhost:2181(CONNECTED) 2] create /assign/worker1.example.com ""
Created /assign/worker1.example.com
[zk: localhost:2181(CONNECTED) 3] ls /assign/worker1.example.com true
[]
[zk: localhost:2181(CONNECTED) 4] 
```

# step7 m3

- 模拟客户端，添加 cmd 任务 /tasks/task-0000000000，监控 /tasks/task-0000000000 子节点。

```
[zozo@VM_0_6_centos bin]$ ./zkCli.sh
Connecting to localhost:2181
[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper, master, workers, tasks, assign]
[zk: localhost:2181(CONNECTED) 1] create -s /tasks/task- "cmd"
Created /tasks/task-0000000000
[zk: localhost:2181(CONNECTED) 2] ls /tasks/task-0000000000 true
[]
[zk: localhost:2181(CONNECTED) 3]
```

# step8 m2

- 主节点监控到 /tasks 子节点变化，检查该任务，检查可用从节点，并将该任务分配给某一个可用从节点

```
[zk: localhost:2181(CONNECTED) 11]
WATCHER::

WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/workers

WATCHER::

WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/tasks

[zk: localhost:2181(CONNECTED) 11] ls /tasks
[task-0000000000]
[zk: localhost:2181(CONNECTED) 12] ls /workers
[worker1.example.com]
[zk: localhost:2181(CONNECTED) 13] create /assign/worker1.example.com/task-0000000000 ""
Created /assign/worker1.example.com/task-0000000000
[zk: localhost:2181(CONNECTED) 14] 
```

# step9 m1

- 从节点监控到 /assign 子节点变化，检查该任务，并在完成任务后，在 /tasks 中添加子节点

```
[zk: localhost:2181(CONNECTED) 4]
WATCHER::

WatchedEvent state:SyncConnected type:NodeChildrenChanged path:/assign/worker1.example.com

[zk: localhost:2181(CONNECTED) 4] ls /assign/worker1.example.com
[task-0000000000]
[zk: localhost:2181(CONNECTED) 5] create /tasks/task-0000000000/status "done"
Created /tasks/task-0000000000/status
[zk: localhost:2181(CONNECTED) 6] 
```

# step10 m3

- 客户端监控到 /tasks/task-0000000000 子节点变化，获取执行结果

```
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
