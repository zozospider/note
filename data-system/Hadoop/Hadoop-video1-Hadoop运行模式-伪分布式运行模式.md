
- [一 启动 HDFS 并运行 MapReduce 程序](#一-启动-hdfs-并运行-mapreduce-程序)
    - [1.1 step1 修改配置](#11-step1-修改配置)
    - [1.2 step2 启动 HDFS (NameNode, DataNode)](#12-step2-启动-hdfs-namenode-datanode)
    - [1.3 HDFS 命令](#13-hdfs-命令)
    - [1.4 浏览器查看 HDFS](#14-浏览器查看-hdfs)
    - [1.5 WordCount 案例](#15-wordcount-案例)
- [二 启动 YARN 并运行 MapReduce 程序](#二-启动-yarn-并运行-mapreduce-程序)
    - [2.1 step1 修改配置](#21-step1-修改配置)
    - [2.2 step2 启动 YARN (ResourceManager, NodeManager)](#22-step2-启动-yarn-resourcemanager-nodemanager)
    - [2.3 浏览器查看 YARN](#23-浏览器查看-yarn)
    - [2.4 WordCount 案例](#24-wordcount-案例)
    - [2.5 配置历史服务器](#25-配置历史服务器)
    - [2.6 配置日志聚集](#26-配置日志聚集)

---

配置文件说明:
- [core-default.xml](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/core-default.xml)
- [hdfs-default.xml](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/hdfs-default.xml)
- [mapred-default.xml](http://hadoop.apache.org/docs/r2.7.2/hadoop-mapreduce-client/hadoop-mapreduce-client-core/mapred-default.xml)
- [yarn-default.xml](http://hadoop.apache.org/docs/r2.7.2/hadoop-yarn/hadoop-yarn-common/yarn-default.xml)

# 一 启动 HDFS 并运行 MapReduce 程序

注: 需要先配置 hostname 和 host

## 1.1 step1 修改配置

- 修改配置 `./etc/hadoop/hadoop-env.sh`

```bash
# The only required environment variable is JAVA_HOME.  All others are optional.  When running a distributed configuration it is best to set JAVA_HOME in this file, so that it is correctly defined on remote nodes.
# 唯一需要的环境变量是JAVA_HOME. 所有其他都是可选的. 运行分布式配置时, 最好在此文件中设置 JAVA_HOME, 以便在远程节点上正确定义它.
# The java implementation to use.
# export JAVA_HOME=${JAVA_HOME}

# custom
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
```

- 修改配置 `./etc/hadoop/core-site.xml`

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

- 修改配置 `./etc/hadoop/hdfs-site.xml` (可选)

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

  <!-- 指定 HDFS 副本数 -->
  <property>
    <name>dfs.replication</name>
    <!-- value: 3 -->
    <value>1</value>
    <description>
      Default block replication. The actual number of replications can be specified when the file is created. The default is used if replication is not specified in create time.
    </description>
  </property>

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

## 1.2 step2 启动 HDFS (NameNode, DataNode)

- 格式化 NameNode (首次启动)

注: 第一次启动时格式化, 以后就不要总格式化

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs namenode -format
19/05/22 21:19:12 INFO namenode.NameNode: STARTUP_MSG:
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   host = VM_0_17_centos/127.0.0.1
STARTUP_MSG:   args = [-format]
STARTUP_MSG:   version = 2.7.2
STARTUP_MSG:   classpath = /home/zozo/app/hadoop/hadoop-2.7.2/etc/hadoop:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jets3t-0.9.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-1.7.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-configuration-1.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-httpclient-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-kerberos-codec-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-auth-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-framework-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/mockito-all-1.8.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-math3-3.1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/java-xmlbuilder-0.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-api-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/gson-2.2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-asn1-api-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-core-1.8.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-client-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-net-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsp-api-2.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpclient-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-util-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-recipes-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-digester-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpcore-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsch-0.1.42.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-daemon-1.0.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-all-4.0.23.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xml-apis-1.3.04.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xercesImpl-2.9.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-client-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-api-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-registry-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-applicationhistoryservice-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-tests-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-resourcemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-sharedcachemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-nodemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-web-proxy-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-client-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-shuffle-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-app-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-plugins-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/contrib/capacity-scheduler/*.jar
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r b165c4fe8a74265c792ce23f546c64604acf0e41; compiled by 'jenkins' on 2016-01-26T00:08Z
STARTUP_MSG:   java = 1.8.0_192
************************************************************/
19/05/22 21:19:12 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
19/05/22 21:19:12 INFO namenode.NameNode: createNameNode [-format]
Formatting using clusterid: CID-d90a1e0a-07a0-47b4-88dc-eb645b70fd03
19/05/22 21:19:14 INFO namenode.FSNamesystem: No KeyProvider found.
19/05/22 21:19:14 INFO namenode.FSNamesystem: fsLock is fair:true
19/05/22 21:19:14 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
19/05/22 21:19:14 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
19/05/22 21:19:14 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
19/05/22 21:19:14 INFO blockmanagement.BlockManager: The block deletion will start around 2019 五月 22 21:19:14
19/05/22 21:19:14 INFO util.GSet: Computing capacity for map BlocksMap
19/05/22 21:19:14 INFO util.GSet: VM type       = 64-bit
19/05/22 21:19:14 INFO util.GSet: 2.0% max memory 889 MB = 17.8 MB
19/05/22 21:19:14 INFO util.GSet: capacity      = 2^21 = 2097152 entries
19/05/22 21:19:14 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
19/05/22 21:19:14 INFO blockmanagement.BlockManager: defaultReplication         = 1
19/05/22 21:19:14 INFO blockmanagement.BlockManager: maxReplication             = 512
19/05/22 21:19:14 INFO blockmanagement.BlockManager: minReplication             = 1
19/05/22 21:19:14 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
19/05/22 21:19:14 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
19/05/22 21:19:14 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
19/05/22 21:19:14 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
19/05/22 21:19:14 INFO namenode.FSNamesystem: fsOwner             = zozo (auth:SIMPLE)
19/05/22 21:19:14 INFO namenode.FSNamesystem: supergroup          = supergroup
19/05/22 21:19:14 INFO namenode.FSNamesystem: isPermissionEnabled = true
19/05/22 21:19:14 INFO namenode.FSNamesystem: HA Enabled: false
19/05/22 21:19:14 INFO namenode.FSNamesystem: Append Enabled: true
19/05/22 21:19:15 INFO util.GSet: Computing capacity for map INodeMap
19/05/22 21:19:15 INFO util.GSet: VM type       = 64-bit
19/05/22 21:19:15 INFO util.GSet: 1.0% max memory 889 MB = 8.9 MB
19/05/22 21:19:15 INFO util.GSet: capacity      = 2^20 = 1048576 entries
19/05/22 21:19:15 INFO namenode.FSDirectory: ACLs enabled? false
19/05/22 21:19:15 INFO namenode.FSDirectory: XAttrs enabled? true
19/05/22 21:19:15 INFO namenode.FSDirectory: Maximum size of an xattr: 16384
19/05/22 21:19:15 INFO namenode.NameNode: Caching file names occuring more than 10 times
19/05/22 21:19:15 INFO util.GSet: Computing capacity for map cachedBlocks
19/05/22 21:19:15 INFO util.GSet: VM type       = 64-bit
19/05/22 21:19:15 INFO util.GSet: 0.25% max memory 889 MB = 2.2 MB
19/05/22 21:19:15 INFO util.GSet: capacity      = 2^18 = 262144 entries
19/05/22 21:19:15 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
19/05/22 21:19:15 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
19/05/22 21:19:15 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
19/05/22 21:19:15 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.window.num.buckets = 10
19/05/22 21:19:15 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.num.users = 10
19/05/22 21:19:15 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.windows.minutes = 1,5,25
19/05/22 21:19:15 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
19/05/22 21:19:15 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
19/05/22 21:19:15 INFO util.GSet: Computing capacity for map NameNodeRetryCache
19/05/22 21:19:15 INFO util.GSet: VM type       = 64-bit
19/05/22 21:19:15 INFO util.GSet: 0.029999999329447746% max memory 889 MB = 273.1 KB
19/05/22 21:19:15 INFO util.GSet: capacity      = 2^15 = 32768 entries
19/05/22 21:19:15 INFO namenode.FSImage: Allocated new BlockPoolId: BP-608257527-127.0.0.1-1558531155258
19/05/22 21:19:15 INFO common.Storage: Storage directory /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name has been successfully formatted.
19/05/22 21:19:15 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
19/05/22 21:19:15 INFO util.ExitUtil: Exiting with status 0
19/05/22 21:19:15 INFO namenode.NameNode: SHUTDOWN_MSG:
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at VM_0_17_centos/127.0.0.1
************************************************************/
[zozo@VM_0_17_centos hadoop-2.7.2]$
```

完成后查看 data 目录, 有如下文件产生:
```
[zozo@VM_0_17_centos hadoop]$ ll
总用量 207296
drwxr-xr-x 13 zozo zozo      4096 5月  19 21:14 hadoop-2.7.2
drwxrwxr-x  3 zozo zozo      4096 5月  22 21:19 hadoop-2.7.2-data
-rw-r--r--  1 zozo zozo 212046774 5月  19 16:38 hadoop-2.7.2.tar.gz
[zozo@VM_0_17_centos hadoop]$ ll hadoop-2.7.2-data/
总用量 4
drwxrwxr-x 3 zozo zozo 4096 5月  22 21:19 tmp
[zozo@VM_0_17_centos hadoop]$ ll hadoop-2.7.2-data/tmp/
总用量 4
drwxrwxr-x 3 zozo zozo 4096 5月  22 21:19 dfs
[zozo@VM_0_17_centos hadoop]$ ll hadoop-2.7.2-data/tmp/dfs/
总用量 4
drwxrwxr-x 3 zozo zozo 4096 5月  22 21:19 name
[zozo@VM_0_17_centos hadoop]$ ll hadoop-2.7.2-data/tmp/dfs/name/
总用量 4
drwxrwxr-x 2 zozo zozo 4096 5月  22 21:19 current
[zozo@VM_0_17_centos hadoop]$ ll hadoop-2.7.2-data/tmp/dfs/name/current/
总用量 16
-rw-rw-r-- 1 zozo zozo 351 5月  22 21:19 fsimage_0000000000000000000
-rw-rw-r-- 1 zozo zozo  62 5月  22 21:19 fsimage_0000000000000000000.md5
-rw-rw-r-- 1 zozo zozo   2 5月  22 21:19 seen_txid
-rw-rw-r-- 1 zozo zozo 201 5月  22 21:19 VERSION
[zozo@VM_0_17_centos hadoop]$
```

- 格式化 NameNode (非首次启动)

将 `./etc/hadoop/hdfs-site.xml` 配置的的 `dfs.namenode.name.dir` 和 `dfs.datanode.data.dir` 这两个指定目录删除.

注: 需要将 name 和 data 文件夹都删除, 否则可能出现 namenode 和 datanode 的 clusterID (集群 ID) 不一致的情况.

将 ``./etc/hadoop/core-site.xml`` 配置的 `fs.defaultFS` 这个指定目录删除.

执行命令 `bin/hdfs namenode -format`, 完成后数据被全部清除, 产生一个新的 HDFS.

```
[zozo@vm017 tmp]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp
[zozo@vm017 tmp]$ rm -rf *
[zozo@vm017 tmp]$ cd ../../hadoop-2.7.2
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs namenode -format
19/05/26 18:22:16 INFO namenode.NameNode: STARTUP_MSG: 
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   host = vm017/172.16.0.17
STARTUP_MSG:   args = [-format]
STARTUP_MSG:   version = 2.7.2
STARTUP_MSG:   classpath = /home/zozo/app/hadoop/hadoop-2.7.2/etc/hadoop:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jets3t-0.9.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-1.7.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-configuration-1.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-httpclient-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-kerberos-codec-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-auth-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-framework-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/mockito-all-1.8.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-math3-3.1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/java-xmlbuilder-0.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-api-1.7.10.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/gson-2.2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-asn1-api-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-beanutils-core-1.8.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-client-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-net-3.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsp-api-2.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpclient-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/api-util-1.0.0-M20.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/curator-recipes-2.7.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-digester-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/httpcore-4.2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/jsch-0.1.42.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/common/hadoop-common-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xmlenc-0.52.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-daemon-1.0.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/htrace-core-3.1.0-incubating.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/netty-all-4.0.23.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xml-apis-1.3.04.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/xercesImpl-2.9.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/hdfs/hadoop-hdfs-nfs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jettison-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-impl-2.2.3-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/activation-1.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-logging-1.1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/stax-api-1.0-2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-xc-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-collections-3.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-util-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jsr305-3.0.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/guava-11.0.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jaxb-api-2.2.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-cli-1.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jetty-6.1.26.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-codec-1.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-lang-2.6.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-jaxrs-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/zookeeper-3.4.6-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-client-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/jersey-json-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/lib/servlet-api-2.5.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-api-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-registry-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-applicationhistoryservice-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-tests-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-resourcemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-sharedcachemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-nodemanager-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-server-web-proxy-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/yarn/hadoop-yarn-client-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-servlet-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/log4j-1.2.17.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/protobuf-java-2.5.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/javax.inject-1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/snappy-java-1.0.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hamcrest-core-1.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/guice-3.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/netty-3.6.2.Final.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/hadoop-annotations-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/xz-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/avro-1.7.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/paranamer-2.3.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-core-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/aopalliance-1.0.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-guice-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-io-2.4.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/leveldbjni-all-1.8.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-mapper-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/junit-4.11.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/commons-compress-1.4.1.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jackson-core-asl-1.9.13.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/asm-3.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/lib/jersey-server-1.9.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-shuffle-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-app-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2-tests.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-plugins-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/share/hadoop/mapreduce/hadoop-mapreduce-client-common-2.7.2.jar:/home/zozo/app/hadoop/hadoop-2.7.2/contrib/capacity-scheduler/*.jar
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r b165c4fe8a74265c792ce23f546c64604acf0e41; compiled by 'jenkins' on 2016-01-26T00:08Z
STARTUP_MSG:   java = 1.8.0_192
************************************************************/
19/05/26 18:22:16 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
19/05/26 18:22:16 INFO namenode.NameNode: createNameNode [-format]
Formatting using clusterid: CID-1267ccd5-4b0b-4a85-b0b5-d97decec672a
19/05/26 18:22:19 INFO namenode.FSNamesystem: No KeyProvider found.
19/05/26 18:22:19 INFO namenode.FSNamesystem: fsLock is fair:true
19/05/26 18:22:19 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
19/05/26 18:22:19 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
19/05/26 18:22:19 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
19/05/26 18:22:19 INFO blockmanagement.BlockManager: The block deletion will start around 2019 五月 26 18:22:19
19/05/26 18:22:19 INFO util.GSet: Computing capacity for map BlocksMap
19/05/26 18:22:19 INFO util.GSet: VM type       = 64-bit
19/05/26 18:22:19 INFO util.GSet: 2.0% max memory 889 MB = 17.8 MB
19/05/26 18:22:19 INFO util.GSet: capacity      = 2^21 = 2097152 entries
19/05/26 18:22:19 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
19/05/26 18:22:19 INFO blockmanagement.BlockManager: defaultReplication         = 1
19/05/26 18:22:19 INFO blockmanagement.BlockManager: maxReplication             = 512
19/05/26 18:22:19 INFO blockmanagement.BlockManager: minReplication             = 1
19/05/26 18:22:19 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
19/05/26 18:22:19 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
19/05/26 18:22:19 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
19/05/26 18:22:19 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
19/05/26 18:22:19 INFO namenode.FSNamesystem: fsOwner             = zozo (auth:SIMPLE)
19/05/26 18:22:19 INFO namenode.FSNamesystem: supergroup          = supergroup
19/05/26 18:22:19 INFO namenode.FSNamesystem: isPermissionEnabled = true
19/05/26 18:22:19 INFO namenode.FSNamesystem: HA Enabled: false
19/05/26 18:22:19 INFO namenode.FSNamesystem: Append Enabled: true
19/05/26 18:22:19 INFO util.GSet: Computing capacity for map INodeMap
19/05/26 18:22:19 INFO util.GSet: VM type       = 64-bit
19/05/26 18:22:19 INFO util.GSet: 1.0% max memory 889 MB = 8.9 MB
19/05/26 18:22:19 INFO util.GSet: capacity      = 2^20 = 1048576 entries
19/05/26 18:22:20 INFO namenode.FSDirectory: ACLs enabled? false
19/05/26 18:22:20 INFO namenode.FSDirectory: XAttrs enabled? true
19/05/26 18:22:20 INFO namenode.FSDirectory: Maximum size of an xattr: 16384
19/05/26 18:22:20 INFO namenode.NameNode: Caching file names occuring more than 10 times
19/05/26 18:22:20 INFO util.GSet: Computing capacity for map cachedBlocks
19/05/26 18:22:20 INFO util.GSet: VM type       = 64-bit
19/05/26 18:22:20 INFO util.GSet: 0.25% max memory 889 MB = 2.2 MB
19/05/26 18:22:20 INFO util.GSet: capacity      = 2^18 = 262144 entries
19/05/26 18:22:20 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
19/05/26 18:22:20 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
19/05/26 18:22:20 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
19/05/26 18:22:20 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.window.num.buckets = 10
19/05/26 18:22:20 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.num.users = 10
19/05/26 18:22:20 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.windows.minutes = 1,5,25
19/05/26 18:22:20 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
19/05/26 18:22:20 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
19/05/26 18:22:20 INFO util.GSet: Computing capacity for map NameNodeRetryCache
19/05/26 18:22:20 INFO util.GSet: VM type       = 64-bit
19/05/26 18:22:20 INFO util.GSet: 0.029999999329447746% max memory 889 MB = 273.1 KB
19/05/26 18:22:20 INFO util.GSet: capacity      = 2^15 = 32768 entries
19/05/26 18:22:20 INFO namenode.FSImage: Allocated new BlockPoolId: BP-1771539892-172.16.0.17-1558866140038
19/05/26 18:22:20 INFO common.Storage: Storage directory /home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name has been successfully formatted.
19/05/26 18:22:20 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
19/05/26 18:22:20 INFO util.ExitUtil: Exiting with status 0
19/05/26 18:22:20 INFO namenode.NameNode: SHUTDOWN_MSG: 
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at vm017/172.16.0.17
************************************************************/
[zozo@vm017 hadoop-2.7.2]$ 
```

完成后查看 data 目录, 有如下文件产生, 正常情况下, name 和 data 文件夹下的 clusterID 会一致:
```
[zozo@vm017 current]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/name/current
[zozo@vm017 current]$ ll
总用量 1044
-rw-rw-r-- 1 zozo zozo 1048576 5月  26 19:52 edits_inprogress_0000000000000000001
-rw-rw-r-- 1 zozo zozo     351 5月  26 18:22 fsimage_0000000000000000000
-rw-rw-r-- 1 zozo zozo      62 5月  26 18:22 fsimage_0000000000000000000.md5
-rw-rw-r-- 1 zozo zozo       2 5月  26 18:26 seen_txid
-rw-rw-r-- 1 zozo zozo     203 5月  26 18:22 VERSION
[zozo@vm017 current]$ cat VERSION 
#Sun May 26 18:22:20 CST 2019
namespaceID=530423119
clusterID=CID-1267ccd5-4b0b-4a85-b0b5-d97decec672a
cTime=0
storageType=NAME_NODE
blockpoolID=BP-1771539892-172.16.0.17-1558866140038
layoutVersion=-63
[zozo@vm017 current]$ 

[zozo@vm017 current]$ pwd
/home/zozo/app/hadoop/hadoop-2.7.2-data/tmp/dfs/data/current
[zozo@vm017 current]$ ll
总用量 8
drwx------ 4 zozo zozo 4096 5月  26 18:27 BP-1771539892-172.16.0.17-1558866140038
-rw-rw-r-- 1 zozo zozo  229 5月  26 18:27 VERSION
[zozo@vm017 current]$ cat VERSION 
#Sun May 26 18:27:14 CST 2019
storageID=DS-e526874a-f794-4ad3-8262-cadcdc4d47df
clusterID=CID-1267ccd5-4b0b-4a85-b0b5-d97decec672a
cTime=0
datanodeUuid=4dd7eacb-8177-41ee-8986-5b5092b00e69
storageType=DATA_NODE
layoutVersion=-56
[zozo@vm017 current]$ 
```

- 启动 NameNode (守护进程)

```
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start namenode
starting namenode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-namenode-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
22034 org.apache.hadoop.hdfs.server.namenode.NameNode
[zozo@vm017 hadoop-2.7.2]$ 
```

- 启动 DataNode (守护进程)

```
[zozo@vm017 hadoop-2.7.2]$ sbin/hadoop-daemon.sh start datanode
starting datanode, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/hadoop-zozo-datanode-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
22034 org.apache.hadoop.hdfs.server.namenode.NameNode
22282 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ 
```

## 1.3 HDFS 命令

- HDFS 创建文件夹
```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -mkdir -p /user/zozo/input-wordcount
```

- HDFS 查看目录
```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -ls /
Found 1 items
drwxr-xr-x   - zozo supergroup          0 2019-05-26 18:29 /user
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -ls -R /
drwxr-xr-x   - zozo supergroup          0 2019-05-26 18:29 /user
drwxr-xr-x   - zozo supergroup          0 2019-05-26 18:29 /user/zozo
drwxr-xr-x   - zozo supergroup          0 2019-05-26 18:29 /user/zozo/input-wordcount
```

- 本地文件上传到 HDFS.
```
[zozo@vm017 hadoop-2.7.2]$ ll input-wordcount/
总用量 4
-rw-rw-r-- 1 zozo zozo 50 5月  19 21:12 wordcount.input
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -put input-wordcount/wordcount.input /user/zozo/input-wordcount
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -ls -R /
drwxr-xr-x   - zozo supergroup          0 2019-05-26 18:29 /user
drwxr-xr-x   - zozo supergroup          0 2019-05-26 18:29 /user/zozo
drwxr-xr-x   - zozo supergroup          0 2019-05-26 18:30 /user/zozo/input-wordcount
-rw-r--r--   1 zozo supergroup         50 2019-05-26 18:30 /user/zozo/input-wordcount/wordcount.input
```

Tips: 如果文件已存在, 会提示 File exists, 要强制覆盖, 需要加参数 `-f`
```
bin/hdfs dfs -put -f input-wordcount/wordcount.input /user/zozo/input-wordcount
```

- HDFS 查看文件信息
```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -cat /user/zozo/input-wordcount/wordcount.input
hadoop yarn
hadoop mapreduce
yarn
zozo zozo

good
```

## 1.4 浏览器查看 HDFS

HDFS 控制台 URL: http://193.112.38.200:50070

以下为导航栏:
- `Overview`: 总体介绍
- `Datanodes`: 数据节点
- `Datanode Volume Failures`: 数据节点故障
- `Snapshot`: 快照
- `Startup Progress`: 启动处理
- `Utilities`: 工具
  - `Browse the file system`: 浏览文件系统
  - `Logs`: 日志

以下为通过 `Utilities` - `Browse the file system` 查看 HDFS 的文件信息

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BHDFS-1.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BHDFS-2.png?raw=true)

## 1.5 WordCount 案例

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -ls /user/zozo/input-wordcount
Found 1 items
-rw-r--r--   1 zozo supergroup         50 2019-05-26 18:30 /user/zozo/input-wordcount/wordcount.input
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar wordcount /user/zozo/input-wordcount /user/zozo/output-wordcount
19/05/26 19:52:47 INFO Configuration.deprecation: session.id is deprecated. Instead, use dfs.metrics.session-id
19/05/26 19:52:47 INFO jvm.JvmMetrics: Initializing JVM Metrics with processName=JobTracker, sessionId=
19/05/26 19:52:48 INFO input.FileInputFormat: Total input paths to process : 1
19/05/26 19:52:48 INFO mapreduce.JobSubmitter: number of splits:1
19/05/26 19:52:48 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_local1405270075_0001
19/05/26 19:52:48 INFO mapreduce.Job: The url to track the job: http://localhost:8080/
19/05/26 19:52:48 INFO mapreduce.Job: Running job: job_local1405270075_0001
19/05/26 19:52:48 INFO mapred.LocalJobRunner: OutputCommitter set in config null
19/05/26 19:52:48 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/26 19:52:48 INFO mapred.LocalJobRunner: OutputCommitter is org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter
19/05/26 19:52:48 INFO mapred.LocalJobRunner: Waiting for map tasks
19/05/26 19:52:48 INFO mapred.LocalJobRunner: Starting task: attempt_local1405270075_0001_m_000000_0
19/05/26 19:52:48 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/26 19:52:48 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/26 19:52:48 INFO mapred.MapTask: Processing split: hdfs://vm017:9000/user/zozo/input-wordcount/wordcount.input:0+50
19/05/26 19:52:49 INFO mapred.MapTask: (EQUATOR) 0 kvi 26214396(104857584)
19/05/26 19:52:49 INFO mapred.MapTask: mapreduce.task.io.sort.mb: 100
19/05/26 19:52:49 INFO mapred.MapTask: soft limit at 83886080
19/05/26 19:52:49 INFO mapred.MapTask: bufstart = 0; bufvoid = 104857600
19/05/26 19:52:49 INFO mapred.MapTask: kvstart = 26214396; length = 6553600
19/05/26 19:52:49 INFO mapred.MapTask: Map output collector class = org.apache.hadoop.mapred.MapTask$MapOutputBuffer
19/05/26 19:52:49 INFO mapred.LocalJobRunner: 
19/05/26 19:52:49 INFO mapred.MapTask: Starting flush of map output
19/05/26 19:52:49 INFO mapred.MapTask: Spilling map output
19/05/26 19:52:49 INFO mapred.MapTask: bufstart = 0; bufend = 81; bufvoid = 104857600
19/05/26 19:52:49 INFO mapred.MapTask: kvstart = 26214396(104857584); kvend = 26214368(104857472); length = 29/6553600
19/05/26 19:52:49 INFO mapred.MapTask: Finished spill 0
19/05/26 19:52:49 INFO mapred.Task: Task:attempt_local1405270075_0001_m_000000_0 is done. And is in the process of committing
19/05/26 19:52:49 INFO mapred.LocalJobRunner: map
19/05/26 19:52:49 INFO mapred.Task: Task 'attempt_local1405270075_0001_m_000000_0' done.
19/05/26 19:52:49 INFO mapred.LocalJobRunner: Finishing task: attempt_local1405270075_0001_m_000000_0
19/05/26 19:52:49 INFO mapred.LocalJobRunner: map task executor complete.
19/05/26 19:52:49 INFO mapred.LocalJobRunner: Waiting for reduce tasks
19/05/26 19:52:49 INFO mapred.LocalJobRunner: Starting task: attempt_local1405270075_0001_r_000000_0
19/05/26 19:52:49 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 1
19/05/26 19:52:49 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
19/05/26 19:52:49 INFO mapred.ReduceTask: Using ShuffleConsumerPlugin: org.apache.hadoop.mapreduce.task.reduce.Shuffle@31ef8b56
19/05/26 19:52:49 INFO reduce.MergeManagerImpl: MergerManager: memoryLimit=334338464, maxSingleShuffleLimit=83584616, mergeThreshold=220663392, ioSortFactor=10, memToMemMergeOutputsThreshold=10
19/05/26 19:52:49 INFO reduce.EventFetcher: attempt_local1405270075_0001_r_000000_0 Thread started: EventFetcher for fetching Map Completion Events
19/05/26 19:52:49 INFO reduce.LocalFetcher: localfetcher#1 about to shuffle output of map attempt_local1405270075_0001_m_000000_0 decomp: 64 len: 68 to MEMORY
19/05/26 19:52:49 INFO reduce.InMemoryMapOutput: Read 64 bytes from map-output for attempt_local1405270075_0001_m_000000_0
19/05/26 19:52:49 INFO reduce.MergeManagerImpl: closeInMemoryFile -> map-output of size: 64, inMemoryMapOutputs.size() -> 1, commitMemory -> 0, usedMemory ->64
19/05/26 19:52:49 WARN io.ReadaheadPool: Failed readahead on ifile
EBADF: Bad file descriptor
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posix_fadvise(Native Method)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX.posixFadviseIfPossible(NativeIO.java:267)
	at org.apache.hadoop.io.nativeio.NativeIO$POSIX$CacheManipulator.posixFadviseIfPossible(NativeIO.java:146)
	at org.apache.hadoop.io.ReadaheadPool$ReadaheadRequestImpl.run(ReadaheadPool.java:206)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
19/05/26 19:52:49 INFO reduce.EventFetcher: EventFetcher is interrupted.. Returning
19/05/26 19:52:49 INFO mapred.LocalJobRunner: 1 / 1 copied.
19/05/26 19:52:49 INFO reduce.MergeManagerImpl: finalMerge called with 1 in-memory map-outputs and 0 on-disk map-outputs
19/05/26 19:52:49 INFO mapred.Merger: Merging 1 sorted segments
19/05/26 19:52:49 INFO mapred.Merger: Down to the last merge-pass, with 1 segments left of total size: 57 bytes
19/05/26 19:52:49 INFO reduce.MergeManagerImpl: Merged 1 segments, 64 bytes to disk to satisfy reduce memory limit
19/05/26 19:52:49 INFO reduce.MergeManagerImpl: Merging 1 files, 68 bytes from disk
19/05/26 19:52:49 INFO reduce.MergeManagerImpl: Merging 0 segments, 0 bytes from memory into reduce
19/05/26 19:52:49 INFO mapred.Merger: Merging 1 sorted segments
19/05/26 19:52:49 INFO mapred.Merger: Down to the last merge-pass, with 1 segments left of total size: 57 bytes
19/05/26 19:52:49 INFO mapred.LocalJobRunner: 1 / 1 copied.
19/05/26 19:52:49 INFO Configuration.deprecation: mapred.skip.on is deprecated. Instead, use mapreduce.job.skiprecords
19/05/26 19:52:49 INFO mapred.Task: Task:attempt_local1405270075_0001_r_000000_0 is done. And is in the process of committing
19/05/26 19:52:49 INFO mapred.LocalJobRunner: 1 / 1 copied.
19/05/26 19:52:49 INFO mapred.Task: Task attempt_local1405270075_0001_r_000000_0 is allowed to commit now
19/05/26 19:52:49 INFO output.FileOutputCommitter: Saved output of task 'attempt_local1405270075_0001_r_000000_0' to hdfs://vm017:9000/user/zozo/output-wordcount/_temporary/0/task_local1405270075_0001_r_000000
19/05/26 19:52:49 INFO mapred.LocalJobRunner: reduce > reduce
19/05/26 19:52:49 INFO mapred.Task: Task 'attempt_local1405270075_0001_r_000000_0' done.
19/05/26 19:52:49 INFO mapred.LocalJobRunner: Finishing task: attempt_local1405270075_0001_r_000000_0
19/05/26 19:52:49 INFO mapred.LocalJobRunner: reduce task executor complete.
19/05/26 19:52:49 INFO mapreduce.Job: Job job_local1405270075_0001 running in uber mode : false
19/05/26 19:52:49 INFO mapreduce.Job:  map 100% reduce 100%
19/05/26 19:52:49 INFO mapreduce.Job: Job job_local1405270075_0001 completed successfully
19/05/26 19:52:49 INFO mapreduce.Job: Counters: 35
	File System Counters
		FILE: Number of bytes read=547388
		FILE: Number of bytes written=1149896
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=100
		HDFS: Number of bytes written=42
		HDFS: Number of read operations=13
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=4
	Map-Reduce Framework
		Map input records=6
		Map output records=8
		Map output bytes=81
		Map output materialized bytes=68
		Input split bytes=124
		Combine input records=8
		Combine output records=5
		Reduce input groups=5
		Reduce shuffle bytes=68
		Reduce input records=5
		Reduce output records=5
		Spilled Records=10
		Shuffled Maps =1
		Failed Shuffles=0
		Merged Map outputs=1
		GC time elapsed (ms)=0
		Total committed heap usage (bytes)=545259520
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters 
		Bytes Read=50
	File Output Format Counters 
		Bytes Written=42
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -ls /user/zozo/output-wordcount
Found 2 items
-rw-r--r--   1 zozo supergroup          0 2019-05-26 19:52 /user/zozo/output-wordcount/_SUCCESS
-rw-r--r--   1 zozo supergroup         42 2019-05-26 19:52 /user/zozo/output-wordcount/part-r-00000
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -cat /user/zozo/output-wordcount/part-r-00000
good	1
hadoop	2
mapreduce	1
yarn	2
zozo	2
[zozo@vm017 hadoop-2.7.2]$ 
```

# 二 启动 YARN 并运行 MapReduce 程序

## 2.1 step1 修改配置

- 修改配置 `./etc/hadoop/yarn-env.sh`

```bash
# some Java parameters
# export JAVA_HOME=/home/y/libexec/jdk1.6.0/

# custom
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
```

- 修改配置 `./etc/hadoop/yarn-site.xml`

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
    <value>vm017</value>
    <description>
      The hostname of the RM.
    </description>
  </property>

<configuration>
```

- 修改配置 `./etc/hadoop/mapred-env.sh`

```bash
# export JAVA_HOME=/home/y/libexec/jdk1.6.0/

# custom
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
```

- 修改配置 `./etc/hadoop/mapred-site.xml`

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

## 2.2 step2 启动 YARN (ResourceManager, NodeManager)

- 启动 ResourceManager (守护进程)

```
[zozo@vm017 hadoop-2.7.2]$ sbin/yarn-daemon.sh start resourcemanager
starting resourcemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-resourcemanager-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
32245 org.apache.hadoop.yarn.server.resourcemanager.ResourceManager
32474 sun.tools.jps.Jps -m -l
8975 org.apache.hadoop.hdfs.server.namenode.NameNode
9119 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ 
```

- 启动 NodeManager (守护进程)

```
[zozo@vm017 hadoop-2.7.2]$ sbin/yarn-daemon.sh start nodemanager
starting nodemanager, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/yarn-zozo-nodemanager-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
32245 org.apache.hadoop.yarn.server.resourcemanager.ResourceManager
32521 org.apache.hadoop.yarn.server.nodemanager.NodeManager
32652 sun.tools.jps.Jps -m -l
8975 org.apache.hadoop.hdfs.server.namenode.NameNode
9119 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ 
```

## 2.3 浏览器查看 YARN

YARN 控制台 URL: http://193.112.38.200:8088

以下为初始化首页

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BYARN-1.png?raw=true)

## 2.4 WordCount 案例

```
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -ls /user/zozo/input-wordcount
Found 1 items
-rw-r--r--   1 zozo supergroup         50 2019-05-26 18:30 /user/zozo/input-wordcount/wordcount.input
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar wordcount /user/zozo/input-wordcount /user/zozo/output-wordcount-2
19/05/26 23:04:00 INFO client.RMProxy: Connecting to ResourceManager at vm017/172.16.0.17:8032
19/05/26 23:04:01 INFO input.FileInputFormat: Total input paths to process : 1
19/05/26 23:04:02 INFO mapreduce.JobSubmitter: number of splits:1
19/05/26 23:04:02 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1558881403273_0001
19/05/26 23:04:03 INFO impl.YarnClientImpl: Submitted application application_1558881403273_0001
19/05/26 23:04:03 INFO mapreduce.Job: The url to track the job: http://vm017:8088/proxy/application_1558881403273_0001/
19/05/26 23:04:03 INFO mapreduce.Job: Running job: job_1558881403273_0001
19/05/26 23:04:14 INFO mapreduce.Job: Job job_1558881403273_0001 running in uber mode : false
19/05/26 23:04:14 INFO mapreduce.Job:  map 0% reduce 0%
19/05/26 23:04:20 INFO mapreduce.Job:  map 100% reduce 0%
19/05/26 23:04:25 INFO mapreduce.Job:  map 100% reduce 100%
19/05/26 23:04:26 INFO mapreduce.Job: Job job_1558881403273_0001 completed successfully
19/05/26 23:04:26 INFO mapreduce.Job: Counters: 49
	File System Counters
		FILE: Number of bytes read=68
		FILE: Number of bytes written=235105
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=174
		HDFS: Number of bytes written=42
		HDFS: Number of read operations=6
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=2
	Job Counters 
		Launched map tasks=1
		Launched reduce tasks=1
		Data-local map tasks=1
		Total time spent by all maps in occupied slots (ms)=3349
		Total time spent by all reduces in occupied slots (ms)=2936
		Total time spent by all map tasks (ms)=3349
		Total time spent by all reduce tasks (ms)=2936
		Total vcore-milliseconds taken by all map tasks=3349
		Total vcore-milliseconds taken by all reduce tasks=2936
		Total megabyte-milliseconds taken by all map tasks=3429376
		Total megabyte-milliseconds taken by all reduce tasks=3006464
	Map-Reduce Framework
		Map input records=6
		Map output records=8
		Map output bytes=81
		Map output materialized bytes=68
		Input split bytes=124
		Combine input records=8
		Combine output records=5
		Reduce input groups=5
		Reduce shuffle bytes=68
		Reduce input records=5
		Reduce output records=5
		Spilled Records=10
		Shuffled Maps =1
		Failed Shuffles=0
		Merged Map outputs=1
		GC time elapsed (ms)=338
		CPU time spent (ms)=1500
		Physical memory (bytes) snapshot=427196416
		Virtual memory (bytes) snapshot=4209008640
		Total committed heap usage (bytes)=302514176
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters 
		Bytes Read=50
	File Output Format Counters 
		Bytes Written=42
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -ls /user/zozo/output-wordcount-2
Found 2 items
-rw-r--r--   1 zozo supergroup          0 2019-05-26 23:04 /user/zozo/output-wordcount-2/_SUCCESS
-rw-r--r--   1 zozo supergroup         42 2019-05-26 23:04 /user/zozo/output-wordcount-2/part-r-00000
[zozo@vm017 hadoop-2.7.2]$ bin/hdfs dfs -cat /user/zozo/output-wordcount-2/part-r-00000
good	1
hadoop	2
mapreduce	1
yarn	2
zozo	2
[zozo@vm017 hadoop-2.7.2]$ 
```

在此过程中通过浏览器查看 YARN 的相关信息, 可监控到作业运行中的如下变化:

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BYARN-wordcount1.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BYARN-wordcount2.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BYARN-wordcount3.png?raw=true)

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8BYARN-wordcount4.png?raw=true)

## 2.5 配置历史服务器

- 修改配置 `./etc/hadoop/mapred-site.xml`

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

  <!-- 历史服务器端地址 -->
  <property>
    <name>mapreduce.jobhistory.address</name>
    <!-- value: 0.0.0.0:10020 -->
    <value>vm017:10020</value>
    <description>
      MapReduce JobHistory Server IPC host:port
    </description>
  </property>

  <!-- 历史服务器端 web 端地址 -->
  <property>
    <name>mapreduce.jobhistory.webapp.address</name>
    <!-- value: 0.0.0.0:19888 -->
    <value>vm017:19888</value>
    <description>
      MapReduce JobHistory Server Web UI host:port
    </description>
  </property>

</configuration>
```

- 启动历史服务器

```
[zozo@vm017 hadoop-2.7.2]$ sbin/mr-jobhistory-daemon.sh start historyserver
starting historyserver, logging to /home/zozo/app/hadoop/hadoop-2.7.2/logs/mapred-zozo-historyserver-vm017.out
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
32245 org.apache.hadoop.yarn.server.resourcemanager.ResourceManager
16040 sun.tools.jps.Jps -m -l
15976 org.apache.hadoop.mapreduce.v2.hs.JobHistoryServer
32521 org.apache.hadoop.yarn.server.nodemanager.NodeManager
8975 org.apache.hadoop.hdfs.server.namenode.NameNode
9119 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ 
```

- 浏览器查看

进入 [YARN 首页](http://193.112.38.200:8088), 通过点击对应任务的 `History` 按钮进入

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E5%8E%86%E5%8F%B2%E6%9C%8D%E5%8A%A1%E5%99%A81.png?raw=true)

`Overview`: 预览

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E5%8E%86%E5%8F%B2%E6%9C%8D%E5%8A%A1%E5%99%A82.png?raw=true)

`Counters`: 计数器

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E5%8E%86%E5%8F%B2%E6%9C%8D%E5%8A%A1%E5%99%A83.png?raw=true)

`Configuration`: 配置信息

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E5%8E%86%E5%8F%B2%E6%9C%8D%E5%8A%A1%E5%99%A84.png?raw=true)

`Map tasks`: Map 任务

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E5%8E%86%E5%8F%B2%E6%9C%8D%E5%8A%A1%E5%99%A85.png?raw=true)

`Reduce tasks`: Reduce 任务

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E5%8E%86%E5%8F%B2%E6%9C%8D%E5%8A%A1%E5%99%A86.png?raw=true)

## 2.6 配置日志聚集

- 停止历史服务器

```
[zozo@vm017 hadoop-2.7.2]$ sbin/mr-jobhistory-daemon.sh stop historyserver
stopping historyserver
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
19873 sun.tools.jps.Jps -m -l
32245 org.apache.hadoop.yarn.server.resourcemanager.ResourceManager
32521 org.apache.hadoop.yarn.server.nodemanager.NodeManager
8975 org.apache.hadoop.hdfs.server.namenode.NameNode
9119 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ 
```

- 停止 YARN NodeManager

```
[zozo@vm017 hadoop-2.7.2]$ sbin/yarn-daemon.sh stop nodemanager
stopping nodemanager
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
32245 org.apache.hadoop.yarn.server.resourcemanager.ResourceManager
20014 sun.tools.jps.Jps -m -l
8975 org.apache.hadoop.hdfs.server.namenode.NameNode
9119 org.apache.hadoop.hdfs.server.datanode.DataNode
[zozo@vm017 hadoop-2.7.2]$ 
```

- 停止 YARN ResourceManager

```
[zozo@vm017 hadoop-2.7.2]$ sbin/yarn-daemon.sh stop resourcemanager
stopping resourcemanager
[zozo@vm017 hadoop-2.7.2]$ jps -m -l
8975 org.apache.hadoop.hdfs.server.namenode.NameNode
9119 org.apache.hadoop.hdfs.server.datanode.DataNode
20143 sun.tools.jps.Jps -m -l
[zozo@vm017 hadoop-2.7.2]$
```

- 修改配置 `./etc/hadoop/yarn-site.xml`

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

  <!-- 开启日志聚集功能 -->
  <property>
    <name>yarn.log-aggregation-enable</name>
    <!-- value: false -->
    <value>true</value>
    <description>
      Whether to enable log aggregation. Log aggregation collects each container's logs and moves these logs onto a file-system, for e.g. HDFS, after the application completes. Users can configure the "yarn.nodemanager.remote-app-log-dir" and "yarn.nodemanager.remote-app-log-dir-suffix" properties to determine where these logs are moved to. Users can access the logs via the Application Timeline Server.
    </description>
  </property>

  <!-- 日志保留时间设置 7 天 -->
  <property>
    <name>yarn.log-aggregation.retain-seconds</name>
    <!-- value: -1 -->
    <value>604800</value>
    <description>
      How long to keep aggregation logs before deleting them. -1 disables. Be careful set this too small and you will spam the name node.
    </description>
  </property>

<configuration>
```

- 启动 YARN ResourceManager

```
[zozo@vm017 hadoop-2.7.2]$ sbin/yarn-daemon.sh start resourcemanager
```

- 启动 YARN NodeManager

```
[zozo@vm017 hadoop-2.7.2]$ sbin/yarn-daemon.sh start nodemanager
```

- 启动历史服务器

```
[zozo@vm017 hadoop-2.7.2]$ sbin/mr-jobhistory-daemon.sh start historyserver
```

- 重跑一个 WordCount 案例

```
[zozo@vm017 hadoop-2.7.2]$ bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar wordcount /user/zozo/input-wordcount /user/zozo/output-wordcount-21
```

- 浏览器查看

进入 [YARN 首页](http://193.112.38.200:8088), 通过点击对应任务的 `History` 按钮进入 `Overview`, 然后点击 `Logs` 按钮

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E6%97%A5%E5%BF%971.png?raw=true)

`Logs` 首页, 可再次点击 `here` 进入详情

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E6%97%A5%E5%BF%972.png?raw=true)

`here`

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-Hadoop%E8%BF%90%E8%A1%8C%E6%A8%A1%E5%BC%8F/%E6%B5%8F%E8%A7%88%E5%99%A8%E6%9F%A5%E7%9C%8B%E6%97%A5%E5%BF%973.png?raw=true)

---
