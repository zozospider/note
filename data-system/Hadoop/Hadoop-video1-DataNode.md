


---

# 一 DataNode 工作机制

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-DataNode/DataNode%E5%B7%A5%E4%BD%9C%E6%9C%BA%E5%88%B6.png?raw=true)

---

# 二 数据完整性

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-DataNode/%E6%95%B0%E6%8D%AE%E5%AE%8C%E6%95%B4%E6%80%A7.png?raw=true)

---

# 三 掉线时限参数设置

Timeout = 2 * `dfs.namenode.heartbeat.recheck-interval` + 10 * `dfs.heartbeat.interval`

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-DataNode/DataNode%E6%8E%89%E7%BA%BF%E6%97%B6%E9%99%90%E5%8F%82%E6%95%B0%E8%AE%BE%E7%BD%AE.png?raw=true)

`./etc/hadoop/hdfs-site.xml` 新增如下配置:

```xml
  <!-- 默认 300000 毫秒 (5 分钟) -->
  <property>
    <name>dfs.namenode.heartbeat.recheck-interval</name>
    <value>300000</value>
    <description>
      This time decides the interval to check for expired datanodes. With this value and dfs.heartbeat.interval, the interval of deciding the datanode is stale or not is also calculated. The unit of this configuration is millisecond.
    </description>
  </property>

  <!-- 默认 3 秒 -->
  <property>
    <name>dfs.heartbeat.interval</name>
    <value>3</value>
    <description>Determines datanode heartbeat interval in seconds.</description>
  </property>
```

即默认 Timeout = 2 * 5 分钟 + 10 * 3 秒 = 10 分 30 秒

---

# 四 服役新节点

`TODO`

---

# 五 添加白名单

添加到白名单的主机节点, 都允许访问 NameNode, 不在白名单的主机节点都会被退出.

操作步骤如下:

1. 在 NameNode 上创建 `./etc/hadoop/dfs.hosts` 文件, 内容如下:

```
[zozo@vm017 hadoop]$ cat /home/zozo/app/hadoop/hadoop-2.7.2/etc/hadoop/dfs.hosts
vm017
vm06
vm03
[zozo@vm017 hadoop]$ 
```

2. 所有节点的 `./etc/hadoop/hdfs-site.xml` 新增如下配置:

```xml
  <!-- 白名单 -->
  <property>
    <name>dfs.hosts</name>
    <value>/home/zozo/app/hadoop/hadoop-2.7.2/etc/hadoop/dfs.hosts</value>
    <description>Names a file that contains a list of hosts that are permitted to connect to the namenode. The full pathname of the file must be specified.  If the value is empty, all hosts are permitted.</description>
  </property>
```

3. 刷新 NameNode

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -refreshNodes
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -refreshNodes
Refresh nodes successful
[zozo@vm017 hadoop-2.7.2]$ 
```

4. 刷新 ResourceManager

```
[zozo@vm03 hadoop-2.7.2]$ bin/yarn rmadmin -refreshNodes
```

```
[zozo@vm03 hadoop-2.7.2]$ bin/yarn rmadmin -refreshNodes
19/10/02 17:24:35 INFO client.RMProxy: Connecting to ResourceManager at vm03/172.16.0.3:8033
[zozo@vm03 hadoop-2.7.2]$ 
```

5. 如果负载不均衡, 执行再平衡命令

```
[zozo@vm017 hadoop-2.7.2]$ sbin/start-balancer.sh 
```

```
[zozo@vm017 hadoop-2.7.2]$ sbin/start-balancer.sh 
starting balancer, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-balancer-vm017.out
Time Stamp               Iteration#  Bytes Already Moved  Bytes Left To Move  Bytes Being Moved
[zozo@vm017 hadoop-2.7.2]$ 
```

---

# 黑名单退役

在黑名单上面的主机都会被强制退出.

操作步骤如下:

1. 在 NameNode 上创建 `./etc/hadoop/dfs.hosts.exclude` 文件

2. 所有节点的 `./etc/hadoop/hdfs-site.xml` 新增如下配置:

```xml
<!-- 黑名单 -->
  <property>
    <name>dfs.hosts.exclude</name>
    <value></value>
    <description>Names a file that contains a list of hosts that are not permitted to connect to the namenode.  The full pathname of the file must be specified.  If the value is empty, no hosts are excluded.</description>
  </property> 
```

3. 刷新 NameNode

4. 刷新 ResourceManager

5. 检查 HDFS 控制台: http://193.112.38.200:50070, 退役节点状态为 `Decommission in progress` (退役中), 说明数据节点正在复制数据块到其他节点. 等待退役节点状态直到变为 `Decommissioned` (已退役).

![image]()

![image]()

6. 停止该节点. 注意: 如果服役的节点小于等于数据块副本数, 是不能退役成功的, 需要修改副本数后才能退役.

```
sbin/hadoop-daemon.sh stop datanode
sbin/hadoop-daemon.sh stop nodemanager
```

6. 如果负载不均衡, 执行再平衡命令

---
