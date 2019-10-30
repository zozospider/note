
- [一 准备 3 台机器](#一-准备-3-台机器)
- [二 安装 JDK, Hadoop, 配置环境变量](#二-安装-jdk-hadoop-配置环境变量)
- [三 配置集群](#三-配置集群)
    - [3.1 修改 Hadoop 配置](#31-修改-hadoop-配置)
    - [3.2 修改 HDFS 配置](#32-修改-hdfs-配置)
    - [3.3 修改 YARN 配置](#33-修改-yarn-配置)
    - [3.4 修改 MapReduce 配置](#34-修改-mapreduce-配置)
- [四 集群每个节点单独启动](#四-集群每个节点单独启动)
    - [4.1 vm017: 格式化, 启动 NameNode, DataNode](#41-vm017-格式化-启动-namenode-datanode)
    - [4.2 vm06: 启动 DataNode](#42-vm06-启动-datanode)
    - [4.3 vm03: 启动 DataNode](#43-vm03-启动-datanode)
    - [4.4 浏览器查看 HDFS](#44-浏览器查看-hdfs)
- [五 配置 SSH 免密登录](#五-配置-ssh-免密登录)
- [六 集群群起](#六-集群群起)
    - [6.1 停止已有的服务 (如果有)](#61-停止已有的服务-如果有)
    - [6.2 配置 slaves](#62-配置-slaves)
    - [6.3 格式化 NameNode (视情况而定)](#63-格式化-namenode-视情况而定)
    - [6.4 群起 HDFS](#64-群起-hdfs)
    - [6.5 群起 YARN](#65-群起-yarn)
    - [6.6 集群基本测试](#66-集群基本测试)
    - [6.7 集群启动 / 停止方式总结](#67-集群启动--停止方式总结)
    - [6.8 集群时间同步](#68-集群时间同步)

---

集群部署规划如下:

| 模块 / 节点 | __vm017__ | __vm06__ | __vm03__ |
| :--- | :--- | :--- | :--- |
| __HDFS__ | DataNode | DataNode | DataNode |
|  | __NameNode__ | __SecondaryNameNode__ |  |
| __YARN__ | NodeManager | NodeManager | NodeManager |
|  |  |  | __ResourceManager__ |

| 节点 / 模块 | __HDFS__ | __YARN__ |
| :--- | :--- | :--- |
| __vm017__ | DataNode | NodeManager |
|  | __NameNode__ |  |
| __vm06__ | DataNode | NodeManager |
|  | __SecondaryNameNode__ |  |
| __vm03__ | DataNode | NodeManager |
|  |  | __ResourceManager__ |

集群操作步骤如下:
- 准备 3 台机器
- 安装 JDK, Hadoop, 配置环境变量
- 配置集群
- 集群每个节点单独启动
- 配置 SSH 免密登录
- 群起
- 测试

# 一 准备 3 台机器

配置 hostname 和 host, 请参考: [Hadoop-video1-Hadoop运行环境搭建 - hostname 和 host 设置 (本地, 伪分布式, 完全分布式都需要配置)](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA.md#%E4%B8%80-hostname-%E5%92%8C-host-%E8%AE%BE%E7%BD%AE-%E6%9C%AC%E5%9C%B0-%E4%BC%AA%E5%88%86%E5%B8%83%E5%BC%8F-%E5%AE%8C%E5%85%A8%E5%88%86%E5%B8%83%E5%BC%8F%E9%83%BD%E9%9C%80%E8%A6%81%E9%85%8D%E7%BD%AE)

# 二 安装 JDK, Hadoop, 配置环境变量

请参考: [Hadoop-video1-Hadoop运行环境搭建 - 下载解压, 配置环境变量 (本地, 伪分布式, 完全分布式都需要配置)](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA.md#%E4%B8%89-%E4%B8%8B%E8%BD%BD%E8%A7%A3%E5%8E%8B-%E9%85%8D%E7%BD%AE%E7%8E%AF%E5%A2%83%E5%8F%98%E9%87%8F-%E6%9C%AC%E5%9C%B0-%E4%BC%AA%E5%88%86%E5%B8%83%E5%BC%8F-%E5%AE%8C%E5%85%A8%E5%88%86%E5%B8%83%E5%BC%8F%E9%83%BD%E9%9C%80%E8%A6%81%E9%85%8D%E7%BD%AE)

# 三 配置集群

集群配置如下 (3 台机的配置一样):

## 3.1 修改 Hadoop 配置

- 1. 修改配置 `./etc/hadoop/hadoop-env.sh`

```bash
# The only required environment variable is JAVA_HOME.  All others are optional.  When running a distributed configuration it is best to set JAVA_HOME in this file, so that it is correctly defined on remote nodes.
# 唯一需要的环境变量是JAVA_HOME. 所有其他都是可选的. 运行分布式配置时, 最好在此文件中设置 JAVA_HOME, 以便在远程节点上正确定义它.
# The java implementation to use.
# export JAVA_HOME=${JAVA_HOME}

# custom
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
```

- 2. 修改配置 `./etc/hadoop/core-site.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration>

  <!-- 指定 HDFS 中 NameNode 的地址 -->
  <property>
    <name>fs.defaultFS</name>
    <!-- value: file:/// -->
    <value>hdfs://vm017:9000</value>
    <description>
      The name of the default file system. A URI whose scheme and authority determine the FileSystem implementation. The uri's scheme determines the config property (fs.SCHEME.impl) naming the FileSystem implementation class. The uri's authority is used to determine the host, port, etc. for a filesystem.
    </description>
  </property>

  <!-- 指定 Hadoop 运行时产生文件的存储目录 -->
  <property>
    <name>hadoop.tmp.dir</name>
    <!-- value: /tmp/hadoop-${user.name} -->
    <value>/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp</value>
    <description>
      A base for other temporary directories.
    </description>
  </property>

<configuration>
```

## 3.2 修改 HDFS 配置

- 1. 修改配置 `./etc/hadoop/hdfs-site.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration>

  <!-- 指定 Hadoop SecondaryNameNode 的地址 -->
  <property>
    <name>dfs.namenode.secondary.http-address</name>
    <value>vm06:50090</value>
    <!-- value: 0.0.0.0:50090 -->
    <description>
      The secondary namenode http server address and port.
    </description>
  </property>

  <!-- 指定 HDFS 副本数 -->
  <!--
  <property>
    <name>dfs.replication</name>
    <value>3</value>
    <description>
      Default block replication. The actual number of replications can be specified when the file is created. The default is used if replication is not specified in create time.
    </description>
  </property>
  -->

  <!-- namenode 元数据存储目录 -->
  <!--
  <property>
    <name>dfs.namenode.name.dir</name>
    <value>file://${hadoop.tmp.dir}/dfs/name</value>
    <description>
      Determines where on the local filesystem the DFS name node should store the name table(fsimage). If this is a comma-delimited list of directories then the name table is replicated in all of the directories, for redundancy.
    </description>
  </property>
  -->

  <!-- datanode 上数据块的物理存储位置 -->
  <!--
  <property>
    <name>dfs.datanode.data.dir</name>
    <value>file://${hadoop.tmp.dir}/dfs/data</value>
    <description>
      Determines where on the local filesystem an DFS data node should store its blocks. If this is a comma-delimited list of directories, then data will be stored in all named directories, typically on different devices. The directories should be tagged with corresponding storage types ([SSD]/[DISK]/[ARCHIVE]/[RAM_DISK]) for HDFS storage policies. The default storage type will be DISK if the directory does not have a storage type tagged explicitly. Directories that do not exist will be created if local filesystem permission allows.
    </description>
  </property>
  -->

</configuration>
```

## 3.3 修改 YARN 配置

- 1. 修改配置 `./etc/hadoop/yarn-env.sh`

```bash
# some Java parameters
# export JAVA_HOME=/home/y/libexec/jdk1.6.0/

# custom
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
```

- 2. 修改配置 `./etc/hadoop/yarn-site.xml`

```xml
<?xml version="1.0"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->
<configuration>

  <!-- Reducer 获取数据的方式 -->
  <property>
    <name>yarn.nodemanager.aux-services</name>
    <!-- value: -->
    <value>mapreduce_shuffle</value>
    <description>
      A comma separated list of services where service name should only contain a-zA-Z0-9_ and can not start with numbers
    </description>
  </property>

  <!-- 指定 YARN 的 ResourceManager 对应节点的 hostname -->
  <property>
    <name>yarn.resourcemanager.hostname</name>
    <!-- value: 0.0.0.0 -->
    <value>vm03</value>
    <description>
      The hostname of the RM.
    </description>
  </property>

<configuration>
```

## 3.4 修改 MapReduce 配置

- 1. 修改配置 `./etc/hadoop/mapred-env.sh`

```bash
# export JAVA_HOME=/home/y/libexec/jdk1.6.0/

# custom
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
```

- 2. 修改配置 `./etc/hadoop/mapred-site.xml`

```xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration>

  <!-- 指定 MapReduce 运行在 YARN 上 -->
  <property>
    <name>mapreduce.framework.name</name>
    <!-- value: local -->
    <value>yarn</value>
    <description>
      The runtime framework for executing MapReduce jobs. Can be one of local, classic or yarn.
    </description>
  </property>

</configuration>
```

# 四 集群每个节点单独启动

## 4.1 vm017: 格式化, 启动 NameNode, DataNode

- 1. 在 __vm017__ 上执行格式化, 成功后会在当前节点生成 `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name` 目录

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs namenode -format
```

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs namenode -format
19/06/02 21:03:30 INFO namenode.NameNode: STARTUP_MSG: 
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   host = vm017/172.16.0.17
STARTUP_MSG:   args = [-format]
STARTUP_MSG:   version = 2.7.2
STARTUP_MSG:   classpath = /home/zozo/app/hadoop/hadoop-2.7.2/etc/hadoop:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpcore-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-client-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/mockito-all-1.8.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-auth-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-core-1.8.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-recipes-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-kerberos-codec-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/java-xmlbuilder-0.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-httpclient-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-1.7.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsp-api-2.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-util-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-net-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-digester-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jets3t-0.9.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpclient-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/gson-2.2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-api-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-math3-3.1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsch-0.1.42.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-asn1-api-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-configuration-1.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-framework-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xml-apis-1.3.04.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-daemon-1.0.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xercesImpl-2.9.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-all-4.0.23.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-client-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-web-proxy-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-nodemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-resourcemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-tests-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-sharedcachemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-registry-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-applicationhistoryservice-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-client-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-api-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-shuffle-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-plugins-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-app-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/contrib/capacity-scheduler/*.jar
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r b165c4fe8a74265c792ce23f546c64604acf0e41; compiled by 'jenkins' on 2016-01-26T00:08Z
STARTUP_MSG:   java = 1.8.0_192
************************************************************/
19/06/02 21:03:30 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
19/06/02 21:03:30 INFO namenode.NameNode: createNameNode [-format]
Formatting using clusterid: CID-a0449250-8d30-4a06-992e-02cfc8068501
19/06/02 21:03:30 INFO namenode.FSNamesystem: No KeyProvider found.
19/06/02 21:03:30 INFO namenode.FSNamesystem: fsLock is fair:true
19/06/02 21:03:30 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
19/06/02 21:03:30 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
19/06/02 21:03:30 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
19/06/02 21:03:30 INFO blockmanagement.BlockManager: The block deletion will start around 2019 六月 02 21:03:30
19/06/02 21:03:30 INFO util.GSet: Computing capacity for map BlocksMap
19/06/02 21:03:30 INFO util.GSet: VM type       = 64-bit
19/06/02 21:03:30 INFO util.GSet: 2.0% max memory 889 MB = 17.8 MB
19/06/02 21:03:30 INFO util.GSet: capacity      = 2^21 = 2097152 entries
19/06/02 21:03:30 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
19/06/02 21:03:30 INFO blockmanagement.BlockManager: defaultReplication         = 3
19/06/02 21:03:30 INFO blockmanagement.BlockManager: maxReplication             = 512
19/06/02 21:03:30 INFO blockmanagement.BlockManager: minReplication             = 1
19/06/02 21:03:30 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
19/06/02 21:03:30 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
19/06/02 21:03:30 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
19/06/02 21:03:30 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
19/06/02 21:03:30 INFO namenode.FSNamesystem: fsOwner             = zozo (auth:SIMPLE)
19/06/02 21:03:30 INFO namenode.FSNamesystem: supergroup          = supergroup
19/06/02 21:03:30 INFO namenode.FSNamesystem: isPermissionEnabled = true
19/06/02 21:03:30 INFO namenode.FSNamesystem: HA Enabled: false
19/06/02 21:03:30 INFO namenode.FSNamesystem: Append Enabled: true
19/06/02 21:03:31 INFO util.GSet: Computing capacity for map INodeMap
19/06/02 21:03:31 INFO util.GSet: VM type       = 64-bit
19/06/02 21:03:31 INFO util.GSet: 1.0% max memory 889 MB = 8.9 MB
19/06/02 21:03:31 INFO util.GSet: capacity      = 2^20 = 1048576 entries
19/06/02 21:03:31 INFO namenode.FSDirectory: ACLs enabled? false
19/06/02 21:03:31 INFO namenode.FSDirectory: XAttrs enabled? true
19/06/02 21:03:31 INFO namenode.FSDirectory: Maximum size of an xattr: 16384
19/06/02 21:03:31 INFO namenode.NameNode: Caching file names occuring more than 10 times
19/06/02 21:03:31 INFO util.GSet: Computing capacity for map cachedBlocks
19/06/02 21:03:31 INFO util.GSet: VM type       = 64-bit
19/06/02 21:03:31 INFO util.GSet: 0.25% max memory 889 MB = 2.2 MB
19/06/02 21:03:31 INFO util.GSet: capacity      = 2^18 = 262144 entries
19/06/02 21:03:31 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
19/06/02 21:03:31 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
19/06/02 21:03:31 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
19/06/02 21:03:31 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.window.num.buckets = 10
19/06/02 21:03:31 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.num.users = 10
19/06/02 21:03:31 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.windows.minutes = 1,5,25
19/06/02 21:03:31 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
19/06/02 21:03:31 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
19/06/02 21:03:31 INFO util.GSet: Computing capacity for map NameNodeRetryCache
19/06/02 21:03:31 INFO util.GSet: VM type       = 64-bit
19/06/02 21:03:31 INFO util.GSet: 0.029999999329447746% max memory 889 MB = 273.1 KB
19/06/02 21:03:31 INFO util.GSet: capacity      = 2^15 = 32768 entries
19/06/02 21:03:31 INFO namenode.FSImage: Allocated new BlockPoolId: BP-958959802-172.16.0.17-1559480611076
19/06/02 21:03:31 INFO common.Storage: Storage directory /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name has been successfully formatted.
19/06/02 21:03:31 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
19/06/02 21:03:31 INFO util.ExitUtil: Exiting with status 0
19/06/02 21:03:31 INFO namenode.NameNode: SHUTDOWN_MSG: 
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at vm017/172.16.0.17
************************************************************/
[zozo@vm017 hadoop-2.7.2]$ cd ..
[zozo@vm017 hadoop]$ ll
总用量 207092
drwxr-xr-x 9 zozo zozo      4096 1月  26 2016 hadoop-2.7.2
drwxrwxr-x 3 zozo zozo      4096 6月   2 21:03 hadoop-2.7.2-data
-rw-r--r-- 1 zozo zozo 212046774 6月   2 00:46 hadoop-2.7.2.tar.gz
[zozo@vm017 hadoop]$ ll -R hadoop-2.7.2-data
hadoop-2.7.2-data:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 6月   2 21:03 tmp

hadoop-2.7.2-data/tmp:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 6月   2 21:03 dfs

hadoop-2.7.2-data/tmp/dfs:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 6月   2 21:03 name

hadoop-2.7.2-data/tmp/dfs/name:
总用量 4
drwxrwxr-x 2 zozo zozo 4096 6月   2 21:03 current

hadoop-2.7.2-data/tmp/dfs/name/current:
总用量 16
-rw-rw-r-- 1 zozo zozo 351 6月   2 21:03 fsimage_0000000000000000000
-rw-rw-r-- 1 zozo zozo  62 6月   2 21:03 fsimage_0000000000000000000.md5
-rw-rw-r-- 1 zozo zozo   2 6月   2 21:03 seen_txid
-rw-rw-r-- 1 zozo zozo 202 6月   2 21:03 VERSION
[zozo@vm017 hadoop]$ 
```

- 2. 在 __vm017__ 上启动 NameNode

```
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start namenode
```

```
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start namenode
starting namenode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-namenode-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
8276 sun.tools.jps.Jps -m -l
8200 org.apache.hadoop.hdfs.server.namenode.NameNode
[zozo@vm017 hadoop-2.7.2]$ 
```

- 3. 在 __vm017__ 上启动 DataNode, 成功后会在当前节点生成 `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data` 目录

```
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start datanode
```

```
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start datanode
starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
8418 sun.tools.jps.Jps -m -l
8327 org.apache.hadoop.hdfs.server.datanode.DataNode
8200 org.apache.hadoop.hdfs.server.namenode.NameNode
[zozo@vm017 hadoop-2.7.2]$ cd ..
[zozo@vm017 hadoop]$ ll -R hadoop-2.7.2-data
hadoop-2.7.2-data:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 6月   2 21:03 tmp

hadoop-2.7.2-data/tmp:
总用量 4
drwxrwxr-x 4 zozo zozo 4096 6月   2 21:06 dfs

hadoop-2.7.2-data/tmp/dfs:
总用量 8
drwx------ 3 zozo zozo 4096 6月   2 21:06 data
drwxrwxr-x 3 zozo zozo 4096 6月   2 21:05 name

hadoop-2.7.2-data/tmp/dfs/data:
总用量 8
drwxrwxr-x 3 zozo zozo 4096 6月   2 21:06 current
-rw-rw-r-- 1 zozo zozo   10 6月   2 21:06 in_use.lock

hadoop-2.7.2-data/tmp/dfs/data/current:
总用量 8
drwx------ 4 zozo zozo 4096 6月   2 21:06 BP-958959802-172.16.0.17-1559480611076
-rw-rw-r-- 1 zozo zozo  229 6月   2 21:06 VERSION

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076:
总用量 12
drwxrwxr-x 4 zozo zozo 4096 6月   2 21:06 current
-rw-rw-r-- 1 zozo zozo  166 6月   2 21:06 scanner.cursor
drwxrwxr-x 2 zozo zozo 4096 6月   2 21:06 tmp

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076/current:
总用量 12
drwxrwxr-x 2 zozo zozo 4096 6月   2 21:06 finalized
drwxrwxr-x 2 zozo zozo 4096 6月   2 21:06 rbw
-rw-rw-r-- 1 zozo zozo  129 6月   2 21:06 VERSION

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076/current/finalized:
总用量 0

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076/current/rbw:
总用量 0

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076/tmp:
总用量 0

hadoop-2.7.2-data/tmp/dfs/name:
总用量 8
drwxrwxr-x 2 zozo zozo 4096 6月   2 21:05 current
-rw-rw-r-- 1 zozo zozo   10 6月   2 21:05 in_use.lock

hadoop-2.7.2-data/tmp/dfs/name/current:
总用量 1040
-rw-rw-r-- 1 zozo zozo 1048576 6月   2 21:05 edits_inprogress_0000000000000000001
-rw-rw-r-- 1 zozo zozo     351 6月   2 21:03 fsimage_0000000000000000000
-rw-rw-r-- 1 zozo zozo      62 6月   2 21:03 fsimage_0000000000000000000.md5
-rw-rw-r-- 1 zozo zozo       2 6月   2 21:05 seen_txid
-rw-rw-r-- 1 zozo zozo     202 6月   2 21:03 VERSION
[zozo@vm017 hadoop]$ 
```

## 4.2 vm06: 启动 DataNode

- 1. 在 __vm06__ 上启动 DataNode, 成功后会在当前节点生成 `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data` 目录

```
[zozo@vm06 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start datanode
```

```
[zozo@vm06 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start datanode
starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm06.out
[zozo@vm06 hadoop-2.7.2]$ jps -m -l
7475 sun.tools.jps.Jps -m -l
7397 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm06 hadoop-2.7.2]$ cd ..
[zozo@vm06 hadoop]$ ll
总用量 207088
drwxr-xr-x 10 zozo zozo      4096 6月   2 21:07 hadoop-2.7.2
drwxrwxr-x  3 zozo zozo      4096 6月   2 21:07 hadoop-2.7.2-data
-rw-r--r--  1 zozo zozo 212046774 6月   2 00:58 hadoop-2.7.2.tar.gz
[zozo@vm06 hadoop]$ ll -R hadoop-2.7.2-data
hadoop-2.7.2-data:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 6月   2 21:07 tmp

hadoop-2.7.2-data/tmp:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 6月   2 21:07 dfs

hadoop-2.7.2-data/tmp/dfs:
总用量 4
drwx------ 3 zozo zozo 4096 6月   2 21:07 data

hadoop-2.7.2-data/tmp/dfs/data:
总用量 8
drwxrwxr-x 3 zozo zozo 4096 6月   2 21:07 current
-rw-rw-r-- 1 zozo zozo    9 6月   2 21:07 in_use.lock

hadoop-2.7.2-data/tmp/dfs/data/current:
总用量 8
drwx------ 4 zozo zozo 4096 6月   2 21:07 BP-958959802-172.16.0.17-1559480611076
-rw-rw-r-- 1 zozo zozo  229 6月   2 21:07 VERSION

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076:
总用量 12
drwxrwxr-x 4 zozo zozo 4096 6月   2 21:07 current
-rw-rw-r-- 1 zozo zozo  166 6月   2 21:07 scanner.cursor
drwxrwxr-x 2 zozo zozo 4096 6月   2 21:07 tmp

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076/current:
总用量 12
drwxrwxr-x 2 zozo zozo 4096 6月   2 21:07 finalized
drwxrwxr-x 2 zozo zozo 4096 6月   2 21:07 rbw
-rw-rw-r-- 1 zozo zozo  129 6月   2 21:07 VERSION

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076/current/finalized:
总用量 0

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076/current/rbw:
总用量 0

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076/tmp:
总用量 0
[zozo@vm06 hadoop]$ 
```

## 4.3 vm03: 启动 DataNode

- 1. 在 __vm03__ 上启动 DataNode, 成功后会在当前节点生成 `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data` 目录

```
[zozo@vm03 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start datanode
```

```
[zozo@vm03 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start datanode
starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm03.out
[zozo@vm03 hadoop-2.7.2]$ jps -m -l
16387 org.apache.hadoop.hdfs.server.datanode.DataNode
16486 sun.tools.jps.Jps -m -l
[zozo@vm03 hadoop-2.7.2]$ cd ..
[zozo@vm03 hadoop]$ ll
总用量 207092
drwxr-xr-x 10 zozo zozo      4096 6月   2 21:07 hadoop-2.7.2
drwxrwxr-x  3 zozo zozo      4096 6月   2 21:07 hadoop-2.7.2-data
-rw-r--r--  1 zozo zozo 212046774 6月   2 00:57 hadoop-2.7.2.tar.gz
[zozo@vm03 hadoop]$ ll -R hadoop-2.7.2-data
hadoop-2.7.2-data:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 6月   2 21:07 tmp

hadoop-2.7.2-data/tmp:
总用量 4
drwxrwxr-x 3 zozo zozo 4096 6月   2 21:07 dfs

hadoop-2.7.2-data/tmp/dfs:
总用量 4
drwx------ 3 zozo zozo 4096 6月   2 21:07 data

hadoop-2.7.2-data/tmp/dfs/data:
总用量 8
drwxrwxr-x 3 zozo zozo 4096 6月   2 21:07 current
-rw-rw-r-- 1 zozo zozo   10 6月   2 21:07 in_use.lock

hadoop-2.7.2-data/tmp/dfs/data/current:
总用量 8
drwx------ 4 zozo zozo 4096 6月   2 21:07 BP-958959802-172.16.0.17-1559480611076
-rw-rw-r-- 1 zozo zozo  229 6月   2 21:07 VERSION

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076:
总用量 12
drwxrwxr-x 4 zozo zozo 4096 6月   2 21:07 current
-rw-rw-r-- 1 zozo zozo  166 6月   2 21:07 scanner.cursor
drwxrwxr-x 2 zozo zozo 4096 6月   2 21:07 tmp

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076/current:
总用量 12
drwxrwxr-x 2 zozo zozo 4096 6月   2 21:07 finalized
drwxrwxr-x 2 zozo zozo 4096 6月   2 21:07 rbw
-rw-rw-r-- 1 zozo zozo  129 6月   2 21:07 VERSION

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076/current/finalized:
总用量 0

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076/current/rbw:
总用量 0

hadoop-2.7.2-data/tmp/dfs/data/current/BP-958959802-172.16.0.17-1559480611076/tmp:
总用量 0
[zozo@vm03 hadoop]$ 
```

## 4.4 浏览器查看 HDFS

访问 HDFS 控制台 URL: http://193.112.38.200:50070 检查是否可用

# 五 配置 SSH 免密登录

请参考: [Hadoop-video1-Hadoop运行环境搭建 - 配置 SSH 免密登录 (仅完全分布式需要配置)](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA.md#%E4%BA%8C-%E9%85%8D%E7%BD%AE-ssh-%E5%85%8D%E5%AF%86%E7%99%BB%E5%BD%95-%E4%BB%85%E5%AE%8C%E5%85%A8%E5%88%86%E5%B8%83%E5%BC%8F%E9%9C%80%E8%A6%81%E9%85%8D%E7%BD%AE)

# 六 集群群起

## 6.1 停止已有的服务 (如果有)

- 1. 在 __vm017__ 上停止 NameNode

```
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh stop namenode
```

```
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
29079 org.apache.hadoop.hdfs.server.namenode.NameNode
29197 org.apache.hadoop.hdfs.server.datanode.DataNode
29614 sun.tools.jps.Jps -m -l
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh stop namenode
stopping namenode
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
29767 sun.tools.jps.Jps -m -l
29197 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ 
```

- 2. 在 __vm017__ 上停止 DataNode

```
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh stop datanode
```

```
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
29828 sun.tools.jps.Jps -m -l
29197 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh stop datanode
stopping datanode
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
29876 sun.tools.jps.Jps -m -l
[zozo@vm017 hadoop-2.7.2]$ 
```

- 3. 在 __vm06__ 上停止 DataNode

```
[zozo@vm06 hadoop-2.7.2]$ sbin/hadoop-daemon.sh stop datanode
```

```
[zozo@vm06 hadoop-2.7.2]$ jps -m -l
7362 org.apache.hadoop.hdfs.server.datanode.DataNode
7979 sun.tools.jps.Jps -m -l
[zozo@vm06 hadoop-2.7.2]$ sbin/hadoop-daemon.sh stop datanode
stopping datanode
[zozo@vm06 hadoop-2.7.2]$ jps -m -l
8028 sun.tools.jps.Jps -m -l
[zozo@vm06 hadoop-2.7.2]$ 
```

- 4. 在 __vm03__ 上停止 DataNode

```
[zozo@vm03 hadoop-2.7.2]$ sbin/hadoop-daemon.sh stop datanode
```

```
[zozo@vm03 hadoop-2.7.2]$ jps -m -l
24388 sun.tools.jps.Jps -m -l
23727 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm03 hadoop-2.7.2]$ sbin/hadoop-daemon.sh stop datanode
stopping datanode
[zozo@vm03 hadoop-2.7.2]$ jps -m -l
24446 sun.tools.jps.Jps -m -l
[zozo@vm03 hadoop-2.7.2]$ 
```

## 6.2 配置 slaves

修改所有节点的配置文件 `./etc/hadoop/slaves`, 新增所有 DataNode 节点的 hostname

```
[zozo@vm017 hadoop-2.7.2]$ cat etc/hadoop/slaves
vm017
vm06
vm03
[zozo@vm017 hadoop-2.7.2]$ 
```

```
[zozo@vm06 hadoop-2.7.2]$ cat etc/hadoop/slaves
vm017
vm06
vm03
[zozo@vm06 hadoop-2.7.2]$ 
```

```
[zozo@vm03 hadoop-2.7.2]$ cat etc/hadoop/slaves
vm017
vm06
vm03
[zozo@vm03 hadoop-2.7.2]$ 
```

## 6.3 格式化 NameNode (视情况而定)

`TODO`

## 6.4 群起 HDFS

- 在 __vm017__ (NameNode 所在节点) 上启动 HDFS

```
[zozo@vm017 hadoop-2.7.2]$ sbin/start-dfs.sh
```

```
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
31148 sun.tools.jps.Jps -m -l
[zozo@vm017 hadoop-2.7.2]$ sbin/start-dfs.sh
Starting namenodes on [vm017]
vm017: starting namenode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-namenode-vm017.out
vm03: starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm03.out
vm017: starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm017.out
vm06: starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm06.out
Starting secondary namenodes [vm06]
vm06: starting secondarynamenode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-secondarynamenode-vm06.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
31654 sun.tools.jps.Jps -m -l
31416 org.apache.hadoop.hdfs.server.datanode.DataNode
31276 org.apache.hadoop.hdfs.server.namenode.NameNode
[zozo@vm017 hadoop-2.7.2]$ 
```

```
[zozo@vm06 hadoop-2.7.2]$ jps -m -l
9408 sun.tools.jps.Jps -m -l
9328 org.apache.hadoop.hdfs.server.namenode.SecondaryNameNode
9217 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm06 hadoop-2.7.2]$ 
```

```
[zozo@vm03 hadoop-2.7.2]$ jps -m -l
25690 sun.tools.jps.Jps -m -l
25550 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm03 hadoop-2.7.2]$ 
```

- 访问 HDFS 控制台 URL: http://193.112.38.200:50070 检查服务是否正常

## 6.5 群起 YARN

- 在 __vm03__ (ResourceManager 所在节点) 上启动 HDFS

```
[zozo@vm03 hadoop-2.7.2]$ sbin/start-yarn.sh
```

```
[zozo@vm03 hadoop-2.7.2]$ jps -m -l
26019 sun.tools.jps.Jps -m -l
25550 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm03 hadoop-2.7.2]$ sbin/start-yarn.sh
starting yarn daemons
starting resourcemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-resourcemanager-vm03.out
vm017: starting nodemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-nodemanager-vm017.out
vm06: starting nodemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-nodemanager-vm06.out
vm03: starting nodemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-nodemanager-vm03.out
[zozo@vm03 hadoop-2.7.2]$ jps -m -l
26480 sun.tools.jps.Jps -m -l
26195 org.apache.hadoop.yarn.server.nodemanager.NodeManager
26086 org.apache.hadoop.yarn.server.resourcemanager.ResourceManager
25550 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm03 hadoop-2.7.2]$ 
```

```
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
32064 org.apache.hadoop.yarn.server.nodemanager.NodeManager
31416 org.apache.hadoop.hdfs.server.datanode.DataNode
31276 org.apache.hadoop.hdfs.server.namenode.NameNode
32206 sun.tools.jps.Jps -m -l
[zozo@vm017 hadoop-2.7.2]$ 
```

```
[zozo@vm06 hadoop-2.7.2]$ jps -m -l
9328 org.apache.hadoop.hdfs.server.namenode.SecondaryNameNode
9217 org.apache.hadoop.hdfs.server.datanode.DataNode
9954 sun.tools.jps.Jps -m -l
9800 org.apache.hadoop.yarn.server.nodemanager.NodeManager
[zozo@vm06 hadoop-2.7.2]$ 
```

- 访问 YARN 控制台 URL: http://111.230.233.137:8088 检查服务是否正常

## 6.6 集群基本测试

- 1. 上传小文件到集群

```
[zozo@vm017 hadoop-2.7.2]$ cat /home/zozo/app/hadoop/fortest/wcinput/wc.input
aaa
bbb
aaa aaa
b cc dd
ee vv ee
qq
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -put /home/zozo/app/hadoop/fortest/wcinput/wc.input /
[zozo@vm017 hadoop-2.7.2]$ 
```

- 2. 上传大文件到集群

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -put /home/zozo/app/hadoop/hadoop-2.7.2.tar.gz /
[zozo@vm017 hadoop-2.7.2]$ 
```

- 3. 控制台查看文件位置

访问 HDFS 控制台 URL: http://193.112.38.200:50070 检查文件是否上传成功

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E9%9B%86%E7%BE%A4%E6%B5%8B%E8%AF%95%E6%9F%A5%E7%9C%8BHDFS-1.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E9%9B%86%E7%BE%A4%E6%B5%8B%E8%AF%95%E6%9F%A5%E7%9C%8BHDFS-2.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E9%9B%86%E7%BE%A4%E6%B5%8B%E8%AF%95%E6%9F%A5%E7%9C%8BHDFS-3.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E9%9B%86%E7%BE%A4%E6%B5%8B%E8%AF%95%E6%9F%A5%E7%9C%8BHDFS-4.png?raw=true)

- 4. 服务器查看文件存储位置

之前从本地上传的 `/home/zozo/app/hadoop/fortest/wcinput/wc.input` 文件保存在如下 1 个文件中:

- `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data/current/BP-1195551085-172.16.0.17-1569330784638/current/finalized/subdir0/subdir0/blk_1073741825`

```
[zozo@vm017 subdir0]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data/current/BP-1195551085-172.16.0.17-1569330784638/current/finalized/subdir0/subdir0
[zozo@vm017 subdir0]$ ll
总用量 208720
-rw-rw-r-- 1 zozo zozo        36 9月  25 20:04 blk_1073741825
-rw-rw-r-- 1 zozo zozo        11 9月  25 20:04 blk_1073741825_1001.meta
-rw-rw-r-- 1 zozo zozo 134217728 9月  25 20:04 blk_1073741826
-rw-rw-r-- 1 zozo zozo   1048583 9月  25 20:04 blk_1073741826_1002.meta
-rw-rw-r-- 1 zozo zozo  77829046 9月  25 20:04 blk_1073741827
-rw-rw-r-- 1 zozo zozo    608047 9月  25 20:04 blk_1073741827_1003.meta
[zozo@vm017 subdir0]$ cat blk_1073741825
aaa
bbb
aaa aaa
b cc dd
ee vv ee
qq
[zozo@vm017 subdir0]$ 
```

之前从本地上传的 `/home/zozo/app/hadoop/hadoop-2.7.2.tar.gz` 文件被切割保存在如下 2 个文件中, 且这 2 个文件在服务器上的大小对应 HDFS 控制台中 `Block0` 和 `Block1` 显示的 `Size` (即分别为 `134217728` 和 `77829046`). 若将服务器上的这 2 个文件追加到一个文件中, 合并后的文件和之前从本地上传的 `/home/zozo/app/hadoop/hadoop-2.7.2.tar.gz` 文件一致.

- `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data/current/BP-1195551085-172.16.0.17-1569330784638/current/finalized/subdir0/subdir0/blk_1073741826`
- `/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data/current/BP-1195551085-172.16.0.17-1569330784638/current/finalized/subdir0/subdir0/blk_1073741827`

```
[zozo@vm017 subdir0]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data/current/BP-1195551085-172.16.0.17-1569330784638/current/finalized/subdir0/subdir0
[zozo@vm017 subdir0]$ ll
总用量 208720
-rw-rw-r-- 1 zozo zozo        36 9月  25 20:04 blk_1073741825
-rw-rw-r-- 1 zozo zozo        11 9月  25 20:04 blk_1073741825_1001.meta
-rw-rw-r-- 1 zozo zozo 134217728 9月  25 20:04 blk_1073741826
-rw-rw-r-- 1 zozo zozo   1048583 9月  25 20:04 blk_1073741826_1002.meta
-rw-rw-r-- 1 zozo zozo  77829046 9月  25 20:04 blk_1073741827
-rw-rw-r-- 1 zozo zozo    608047 9月  25 20:04 blk_1073741827_1003.meta
[zozo@vm017 subdir0]$ cat blk_1073741826 >> /home/zozo/app/hadoop/hadoop-2.7.2.tar.gz.tmp
[zozo@vm017 subdir0]$ cat blk_1073741827 >> /home/zozo/app/hadoop/hadoop-2.7.2.tar.gz.tmp
[zozo@vm017 subdir0]$ ll /home/zozo/app/hadoop/hadoop-2.7.2.tar.gz*
-rw-r--r-- 1 zozo zozo 212046774 6月   2 00:46 /home/zozo/app/hadoop/hadoop-2.7.2.tar.gz
-rw-rw-r-- 1 zozo zozo 212046774 9月  25 20:38 /home/zozo/app/hadoop/hadoop-2.7.2.tar.gz.tmp
```

## 6.7 集群启动 / 停止方式总结

- 1. 各个服务组件逐一启动 / 停止

```bash
# start/stop hdfs/yarn
sbin/hadoop-daemon.sh start/stop namenode/datanode/secodarynamenode
sbin/yarn-daemon.sh start/stop resourcemanager/nodemanager
```

```bash
# start/stop hdfs
sbin/hadoop-daemon.sh start namenode
sbin/hadoop-daemon.sh stop namenode

sbin/hadoop-daemon.sh start datanode
sbin/hadoop-daemon.sh stop datanode

sbin/hadoop-daemon.sh start secodarynamenode
sbin/hadoop-daemon.sh stop secodarynamenode

# start/stop yarn
sbin/yarn-daemon.sh start resourcemanager
sbin/yarn-daemon.sh stop resourcemanager

sbin/yarn-daemon.sh start nodemanager
sbin/yarn-daemon.sh stop nodemanager
```

```bash
# start hdfs/yarn
sbin/hadoop-daemon.sh start namenode
sbin/hadoop-daemon.sh start datanode
sbin/hadoop-daemon.sh start secodarynamenode
sbin/yarn-daemon.sh start resourcemanager
sbin/yarn-daemon.sh start nodemanager

# stop hdfs/yarn
sbin/hadoop-daemon.sh stop namenode
sbin/hadoop-daemon.sh stop datanode
sbin/hadoop-daemon.sh stop secodarynamenode
sbin/yarn-daemon.sh stop resourcemanager
sbin/yarn-daemon.sh stop nodemanager
```

- 2. 各个模块分开启动 / 停止

```bash
# start/stop hdfs/yarn
sbin/start-dfs.sh / stop-dfs.sh
sbin/start-yarn.sh / stop-yarn.sh
```

```bash
# start/stop hdfs
sbin/start-dfs.sh
sbin/stop-dfs.sh

# start/stop yarn
sbin/start-yarn.sh
sbin/stop-yarn.sh
```

```bash
# start hdfs/yarn
sbin/start-dfs.sh
sbin/start-yarn.sh

# stop hdfs/yarn
sbin/stop-dfs.sh
sbin/stop-yarn.sh
```

## 6.8 集群时间同步

`TODO`

---
