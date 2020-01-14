
开源监控软件

- [Kafka监控工具汇总](https://juejin.im/post/5d5f62085188255d803faebb)

# linkedin: cruise-control & cruise-control-ui

- [linkedin/cruise-control](https://github.com/linkedin/cruise-control)

- [Connection to node -1 could not be established. Broker may not be available. (org.apache.kafka.clients.NetworkClient)](https://stackoverflow.com/questions/56161345/connection-to-node-1-could-not-be-established-broker-may-not-be-available-or)
要在 Kafka 服务器的 `KAFKA_HOME/config/server.properties` 设置 `listeners=PLAINTEXT://:9092`

- [Getting "Connection to node -1 could not be established. Broker may not be available." on all brokers](https://github.com/linkedin/cruise-control/issues/143)
要在 `cruise-control/config/cruisecontrol.properties` 中添加 `cruise.control.metrics.reporter.bootstrap.servers=hadoop1:9092,hadoop2:9092,hadoop3:9092`

- [linkedin/cruise-control-ui](https://github.com/linkedin/cruise-control-ui)

# yahoo: kafka-manager

- [yahoo/kafka-manager](https://github.com/yahoo/kafka-manager)
- [kafka-manager简介和安装](https://www.cnblogs.com/frankdeng/p/9584870.html)
```bash
# yum 安装 sbt (因为 kafka-manager 需要 sbt 编译)
[admin@node21 ~]$ curl https://bintray.com/sbt/rpm/rpm > bintray-sbt-rpm.repo
[admin@node21 ~]$ sudo mv bintray-sbt-rpm.repo /etc/yum.repos.d/
[admin@node21 ~]$ sudo yum install sbt
```

配置 maven 镜像
- [第五章 SBT国内源配置](https://www.jianshu.com/p/a867b2a7c3c8)
- [maven与sbt修改国内镜像](https://www.cnblogs.com/feiyumo/p/9237517.html)
```bash
# 没用
[repositories] 
local 
aliyun: http://maven.aliyun.com/nexus/content/groups/public/
typesafe: http://repo.typesafe.com/typesafe/ivy-releases/, [organization]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext], bootOnly 
sonatype-oss-releases 
maven-central 
sonatype-oss-snapshots

# 方案 二.1 项目 build.sbt 增加以下两行
resolvers += "central" at "http://maven.aliyun.com/nexus/content/groups/public/"
externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)
# 方案 二.2 运行



# 有用
[repositories]
local
aliyun-nexus: http://maven.aliyun.com/nexus/content/groups/public/  
ibiblio-maven: http://maven.ibiblio.org/maven2/
typesafe-ivy: https://dl.bintray.com/typesafe/ivy-releases/, [organization]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]
uk-repository: http://uk.maven.org/maven2/
jboss-repository: http://repository.jboss.org/nexus/content/groups/public/

osc: http://maven.oschina.net/content/groups/public/
typesafe: http://repo.typesafe.com/typesafe/ivy-releases/, [organization]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext], bootOnly
sonatype-oss-releases
maven-central
sonatype-oss-snapshots
```
如果只是在单个项目中修改的话,在build.sbt里添加 `resolvers += "aliyun" at "http://maven.aliyun.com/nexus/content/groups/public/"`