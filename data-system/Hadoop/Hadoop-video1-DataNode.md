
- [一 DataNode 工作机制](#一-datanode-工作机制)
- [二 数据完整性](#二-数据完整性)
- [三 掉线时限参数设置](#三-掉线时限参数设置)
- [四 服役新节点](#四-服役新节点)
- [五 添加白名单](#五-添加白名单)
- [六 黑名单退役](#六-黑名单退役)
- [七 DataNode 多目录配置](#七-datanode-多目录配置)
    - [7.1 配置](#71-配置)
    - [7.2 停止集群并删除数据 (可选)](#72-停止集群并删除数据-可选)
    - [7.3 格式化集群](#73-格式化集群)
    - [7.4 启动集群](#74-启动集群)
    - [7.5 添加测试数据](#75-添加测试数据)

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

# 六 黑名单退役

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

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-DataNode/%E9%BB%91%E5%90%8D%E5%8D%95%E9%80%80%E5%BD%B9%E4%B8%AD.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-DataNode/%E9%BB%91%E5%90%8D%E5%8D%95%E5%B7%B2%E9%80%80%E5%BD%B9.png?raw=true)

6. 停止该节点. 注意: 如果服役的节点小于等于数据块副本数, 是不能退役成功的, 需要修改副本数后才能退役.

```
sbin/hadoop-daemon.sh stop datanode
sbin/hadoop-daemon.sh stop nodemanager
```

6. 如果负载不均衡, 执行再平衡命令

---

# 七 DataNode 多目录配置

DataNode 可以配置成多个目录, 每个目录存储的数据不一样.

## 7.1 配置

在 `./etc/hadoop/hdfs-site.xml` 中增加如下配置:

```xml
  <!-- datanode 上数据块的物理存储位置 -->
  <property>
    <name>dfs.datanode.data.dir</name>
    <value>file://${hadoop.tmp.dir}/dfs/data1,file://${hadoop.tmp.dir}/dfs/data2,file://${hadoop.tmp.dir}/dfs/data3</value>
    <description>Determines where on the local filesystem an DFS data node should store its blocks.  If this is a comma-delimited list of directories, then data will be stored in all named directories, typically on different devices. The directories should be tagged with corresponding storage types ([SSD]/[DISK]/[ARCHIVE]/[RAM_DISK]) for HDFS storage policies. The default storage type will be DISK if the directory does not have a storage type tagged explicitly. Directories that do not exist will be created if local filesystem permission allows.
    </description>
  </property>
```

## 7.2 停止集群并删除数据 (可选)

如果集群非首次启动, 需要停止集群, 删除所有 data, logs 等数据

## 7.3 格式化集群

格式化集群

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs namenode -format
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs namenode -format
19/10/02 18:06:20 INFO namenode.NameNode: STARTUP_MSG: 
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   host = vm017/172.16.0.17
STARTUP_MSG:   args = [-format]
STARTUP_MSG:   version = 2.7.2
STARTUP_MSG:   classpath = /home/zozo/app/hadoop/hadoop-2.7.2/etc/hadoop:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpcore-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-client-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/mockito-all-1.8.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-auth-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-core-1.8.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-recipes-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-kerberos-codec-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/java-xmlbuilder-0.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-httpclient-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-1.7.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsp-api-2.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-util-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-net-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-digester-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jets3t-0.9.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpclient-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/gson-2.2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-api-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-math3-3.1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsch-0.1.42.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-asn1-api-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-configuration-1.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-framework-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xml-apis-1.3.04.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-daemon-1.0.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xercesImpl-2.9.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-all-4.0.23.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-client-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-web-proxy-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-nodemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-resourcemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-tests-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-sharedcachemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-registry-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-applicationhistoryservice-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-client-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-api-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-shuffle-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-plugins-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-app-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/contrib/capacity-scheduler/*.jar
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r b165c4fe8a74265c792ce23f546c64604acf0e41; compiled by 'jenkins' on 2016-01-26T00:08Z
STARTUP_MSG:   java = 1.8.0_192
************************************************************/
19/10/02 18:06:20 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
19/10/02 18:06:20 INFO namenode.NameNode: createNameNode [-format]
Formatting using clusterid: CID-b498809c-179e-4d03-a540-de31eb8ff894
19/10/02 18:06:20 INFO namenode.FSNamesystem: No KeyProvider found.
19/10/02 18:06:20 INFO namenode.FSNamesystem: fsLock is fair:true
19/10/02 18:06:20 INFO util.HostsFileReader: Adding vm017 to the list of included hosts from /home/zozo/app/hadoop/hadoop-2.7.2/etc/hadoop/dfs.hosts
19/10/02 18:06:20 INFO util.HostsFileReader: Adding vm06 to the list of included hosts from /home/zozo/app/hadoop/hadoop-2.7.2/etc/hadoop/dfs.hosts
19/10/02 18:06:20 INFO util.HostsFileReader: Adding vm03 to the list of included hosts from /home/zozo/app/hadoop/hadoop-2.7.2/etc/hadoop/dfs.hosts
19/10/02 18:06:20 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
19/10/02 18:06:20 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
19/10/02 18:06:20 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
19/10/02 18:06:20 INFO blockmanagement.BlockManager: The block deletion will start around 2019 十月 02 18:06:20
19/10/02 18:06:20 INFO util.GSet: Computing capacity for map BlocksMap
19/10/02 18:06:20 INFO util.GSet: VM type       = 64-bit
19/10/02 18:06:20 INFO util.GSet: 2.0% max memory 889 MB = 17.8 MB
19/10/02 18:06:20 INFO util.GSet: capacity      = 2^21 = 2097152 entries
19/10/02 18:06:21 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
19/10/02 18:06:21 INFO blockmanagement.BlockManager: defaultReplication         = 3
19/10/02 18:06:21 INFO blockmanagement.BlockManager: maxReplication             = 512
19/10/02 18:06:21 INFO blockmanagement.BlockManager: minReplication             = 1
19/10/02 18:06:21 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
19/10/02 18:06:21 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
19/10/02 18:06:21 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
19/10/02 18:06:21 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
19/10/02 18:06:21 INFO namenode.FSNamesystem: fsOwner             = zozo (auth:SIMPLE)
19/10/02 18:06:21 INFO namenode.FSNamesystem: supergroup          = supergroup
19/10/02 18:06:21 INFO namenode.FSNamesystem: isPermissionEnabled = true
19/10/02 18:06:21 INFO namenode.FSNamesystem: HA Enabled: false
19/10/02 18:06:21 INFO namenode.FSNamesystem: Append Enabled: true
19/10/02 18:06:21 INFO util.GSet: Computing capacity for map INodeMap
19/10/02 18:06:21 INFO util.GSet: VM type       = 64-bit
19/10/02 18:06:21 INFO util.GSet: 1.0% max memory 889 MB = 8.9 MB
19/10/02 18:06:21 INFO util.GSet: capacity      = 2^20 = 1048576 entries
19/10/02 18:06:21 INFO namenode.FSDirectory: ACLs enabled? false
19/10/02 18:06:21 INFO namenode.FSDirectory: XAttrs enabled? true
19/10/02 18:06:21 INFO namenode.FSDirectory: Maximum size of an xattr: 16384
19/10/02 18:06:21 INFO namenode.NameNode: Caching file names occuring more than 10 times
19/10/02 18:06:21 INFO util.GSet: Computing capacity for map cachedBlocks
19/10/02 18:06:21 INFO util.GSet: VM type       = 64-bit
19/10/02 18:06:21 INFO util.GSet: 0.25% max memory 889 MB = 2.2 MB
19/10/02 18:06:21 INFO util.GSet: capacity      = 2^18 = 262144 entries
19/10/02 18:06:21 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
19/10/02 18:06:21 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
19/10/02 18:06:21 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
19/10/02 18:06:21 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.window.num.buckets = 10
19/10/02 18:06:21 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.num.users = 10
19/10/02 18:06:21 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.windows.minutes = 1,5,25
19/10/02 18:06:21 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
19/10/02 18:06:21 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
19/10/02 18:06:21 INFO util.GSet: Computing capacity for map NameNodeRetryCache
19/10/02 18:06:21 INFO util.GSet: VM type       = 64-bit
19/10/02 18:06:21 INFO util.GSet: 0.029999999329447746% max memory 889 MB = 273.1 KB
19/10/02 18:06:21 INFO util.GSet: capacity      = 2^15 = 32768 entries
19/10/02 18:06:21 INFO namenode.FSImage: Allocated new BlockPoolId: BP-1579662978-172.16.0.17-1570010781276
19/10/02 18:06:21 INFO common.Storage: Storage directory /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name1 has been successfully formatted.
19/10/02 18:06:21 INFO common.Storage: Storage directory /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name2 has been successfully formatted.
19/10/02 18:06:21 INFO common.Storage: Storage directory /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name3 has been successfully formatted.
19/10/02 18:06:21 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
19/10/02 18:06:21 INFO util.ExitUtil: Exiting with status 0
19/10/02 18:06:21 INFO namenode.NameNode: SHUTDOWN_MSG: 
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at vm017/172.16.0.17
************************************************************/
[zozo@vm017 hadoop-2.7.2]$ 
```

此时 NameNode 数据目录已经建立, 但是 DataNode 数据目录还未生成 (因为集群还未启动)

## 7.4 启动集群

```
[zozo@vm017 hadoop-2.7.2]$ sbin/start-dfs.sh
[zozo@vm03 hadoop-2.7.2]$ sbin/start-yarn.sh
```

```
[zozo@vm017 hadoop-2.7.2]$ sbin/start-dfs.sh
Starting namenodes on [vm017]
vm017: starting namenode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-namenode-vm017.out
vm017: starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm017.out
vm03: starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm03.out
vm06: starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm06.out
Starting secondary namenodes [vm06]
vm06: starting secondarynamenode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-secondarynamenode-vm06.out
[zozo@vm017 hadoop-2.7.2]$ 
```

```
[zozo@vm03 hadoop-2.7.2]$ sbin/start-yarn.sh
starting yarn daemons
starting resourcemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-resourcemanager-vm03.out
vm017: starting nodemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-nodemanager-vm017.out
vm06: starting nodemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-nodemanager-vm06.out
vm03: starting nodemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-nodemanager-vm03.out
[zozo@vm03 hadoop-2.7.2]$ 
```

此时每台 DataNode 的数据目录已经建立:

```
[zozo@vm017 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs
总用量 24
drwx------ 3 zozo zozo 4096 10月  2 18:09 data1
drwx------ 3 zozo zozo 4096 10月  2 18:09 data2
drwx------ 3 zozo zozo 4096 10月  2 18:09 data3
drwxrwxr-x 3 zozo zozo 4096 10月  2 18:09 name1
drwxrwxr-x 3 zozo zozo 4096 10月  2 18:09 name2
drwxrwxr-x 3 zozo zozo 4096 10月  2 18:09 name3
[zozo@vm017 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data1/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm017 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data2/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm017 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data3/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm017 dfs]$ 
```

```
[zozo@vm06 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs
总用量 16
drwx------ 3 zozo zozo 4096 10月  2 18:09 data1
drwx------ 3 zozo zozo 4096 10月  2 18:09 data2
drwx------ 3 zozo zozo 4096 10月  2 18:09 data3
drwxrwxr-x 3 zozo zozo 4096 10月  2 18:10 namesecondary
[zozo@vm06 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data1/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm06 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data2/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm06 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data3/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm06 dfs]$ 
```

```
[zozo@vm03 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs
总用量 12
drwx------ 3 zozo zozo 4096 10月  2 18:09 data1
drwx------ 3 zozo zozo 4096 10月  2 18:09 data2
drwx------ 3 zozo zozo 4096 10月  2 18:09 data3
[zozo@vm03 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data1/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm03 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data2/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm03 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data3/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm03 dfs]$ 
```

## 7.5 添加测试数据

- 往集群中添加第 1 个文件, 然后查看每台 DataNode 的数据目录的变化情况:

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -put /home/zozo/app/hadoop/fortest/f1 /
[zozo@vm017 hadoop-2.7.2]$ 
```

```
[zozo@vm017 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data1/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/subdir0/subdir0/
总用量 8
-rw-rw-r-- 1 zozo zozo 30 10月  2 18:15 blk_1073741825
-rw-rw-r-- 1 zozo zozo 11 10月  2 18:15 blk_1073741825_1001.meta
[zozo@vm017 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data2/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm017 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data3/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm017 dfs]$ 
```

```
[zozo@vm06 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data1/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/subdir0/subdir0/
总用量 8
-rw-rw-r-- 1 zozo zozo 30 10月  2 18:15 blk_1073741825
-rw-rw-r-- 1 zozo zozo 11 10月  2 18:15 blk_1073741825_1001.meta
[zozo@vm06 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data2/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm06 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data3/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm06 dfs]$ 
```

```
[zozo@vm03 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data1/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/subdir0/subdir0/
总用量 8
-rw-rw-r-- 1 zozo zozo 30 10月  2 18:15 blk_1073741825
-rw-rw-r-- 1 zozo zozo 11 10月  2 18:15 blk_1073741825_1001.meta
[zozo@vm03 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data2/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm03 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data3/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm03 dfs]$ 
```

- 往集群中添加第 2 个文件, 然后查看每台 DataNode 的数据目录的变化情况:

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop fs -put /home/zozo/app/hadoop/fortest/f2 /
[zozo@vm017 hadoop-2.7.2]$ 
```

```
[zozo@vm017 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data1/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/subdir0/subdir0/
总用量 8
-rw-rw-r-- 1 zozo zozo 30 10月  2 18:15 blk_1073741825
-rw-rw-r-- 1 zozo zozo 11 10月  2 18:15 blk_1073741825_1001.meta
[zozo@vm017 dfs]$ 
[zozo@vm017 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data2/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/subdir0/subdir0/
总用量 8
-rw-rw-r-- 1 zozo zozo  8 10月  2 18:20 blk_1073741826
-rw-rw-r-- 1 zozo zozo 11 10月  2 18:20 blk_1073741826_1002.meta
[zozo@vm017 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data3/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm017 dfs]$ 
```

```
[zozo@vm06 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data1/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/subdir0/subdir0/
总用量 8
-rw-rw-r-- 1 zozo zozo 30 10月  2 18:15 blk_1073741825
-rw-rw-r-- 1 zozo zozo 11 10月  2 18:15 blk_1073741825_1001.meta
[zozo@vm06 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data2/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/subdir0/subdir0/
总用量 8
-rw-rw-r-- 1 zozo zozo  8 10月  2 18:20 blk_1073741826
-rw-rw-r-- 1 zozo zozo 11 10月  2 18:20 blk_1073741826_1002.meta
[zozo@vm06 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data3/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm06 dfs]$ 
```

```
[zozo@vm03 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data1/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/subdir0/subdir0/
总用量 8
-rw-rw-r-- 1 zozo zozo 30 10月  2 18:15 blk_1073741825
-rw-rw-r-- 1 zozo zozo 11 10月  2 18:15 blk_1073741825_1001.meta
[zozo@vm03 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data2/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/subdir0/subdir0/
总用量 8
-rw-rw-r-- 1 zozo zozo  8 10月  2 18:20 blk_1073741826
-rw-rw-r-- 1 zozo zozo 11 10月  2 18:20 blk_1073741826_1002.meta
[zozo@vm03 dfs]$ ll /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data3/current/BP-1579662978-172.16.0.17-1570010781276/current/finalized/
总用量 0
[zozo@vm03 dfs]$ 
```

---
