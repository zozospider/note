


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

3. 执行以下命令刷新 NameNode

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfsadmin -refreshNodes
```


---