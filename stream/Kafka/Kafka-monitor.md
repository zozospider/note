
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







---


# kafka-manager 编译终极方案


如果以上都没有用, 建议在 Mac 上连接外网, 然后安装 Homebrew, Java1.8, sbt 进行编译
- [【工具】Homebrew的安装及使用](https://www.jianshu.com/p/4e80b42823d5)
- [在 macOS 上安装 sbt](https://www.scala-sbt.org/1.x/docs/zh-cn/Installing-sbt-on-Mac.html)

通过 ./sbt clean dist 时如果报错 slf4j 下载不了, 可以通过删除 `~/.sbt/*`, `~/.ivy2/cache`, `~/.ivy2/jars`, `~/.m2/repository/` 这些目录重新执行.
- [Dependency issues when using --packages option with spark #244 DerekHanqingWang commented on 26 Nov 2017](https://github.com/databricks/spark-redshift/issues/244)

注意: `[info] Packaging /home/soft/kafka-manager-2.0.0.2/target/scala-2.12/kafka-manager_2.12-2.0.0.2-sources.jar` 会很慢, 耐心等 10-20 分钟.
`Warning: node.js detection failed, sbt will use the Rhino based Trireme JavaScript engine instead to run JavaScript assets compilation, which in some cases may be orders of magnitude slower than using node.js.` 会很慢, 耐心等 10 分钟.

执行过程如: [Centos7編譯安裝kafka-manager-2.0.0.2](https://www.twblogs.net/a/5e0447bfbd9eee310da0ea53) 所记录的.
```
[root@localhost kafka-manager-2.0.0.2]# ./sbt clean dist
Downloading sbt launcher for 1.2.8:
  From  http://repo.scala-sbt.org/scalasbt/maven-releases/org/scala-sbt/sbt-launch/1.2.8/sbt-launch.jar
    To  /root/.sbt/launchers/1.2.8/sbt-launch.jar
Getting org.scala-sbt sbt 1.2.8  (this may take some time)...
:: retrieving :: org.scala-sbt#boot-app
	confs: [default]
	79 artifacts copied, 0 already retrieved (28496kB/1360ms)
Getting Scala 2.12.7 (for sbt)...
:: retrieving :: org.scala-sbt#boot-scala
	confs: [default]
	5 artifacts copied, 0 already retrieved (19715kB/347ms)
[info] Loading settings for project kafka-manager-2-0-0-2-build from plugins.sbt ...
[info] Loading project definition from /home/soft/kafka-manager-2.0.0.2/project
[info] Updating ProjectRef(uri("file:/home/soft/kafka-manager-2.0.0.2/project/"), "kafka-manager-2-0-0-2-build")...
[info] Done updating.
[warn] There may be incompatibilities among your library dependencies; run 'evicted' to see detailed eviction warnings.
[info] Loading settings for project root from build.sbt ...
[info] Set current project to kafka-manager (in build file:/home/soft/kafka-manager-2.0.0.2/)
[success] Total time: 0 s, completed 2019-12-25 12:27:23
[info] Packaging /home/soft/kafka-manager-2.0.0.2/target/scala-2.12/kafka-manager_2.12-2.0.0.2-sources.jar ...
[info] Done packaging.
Warning: node.js detection failed, sbt will use the Rhino based Trireme JavaScript engine instead to run JavaScript assets compilation, which in some cases may be orders of magnitude slower than using node.js.
[info] Updating ...
[info] downloading http://maven.aliyun.com/nexus/content/groups/public/org/scala-lang/modules/scala-parser-combinators_2.12/1.0.7/scala-parser-combinators_2.12-1.0.7.jar ...
[info] 	[SUCCESSFUL ] org.scala-lang.modules#scala-parser-combinators_2.12;1.0.7!scala-parser-combinators_2.12.jar(bundle) (2108ms)
[info] Done updating.
[warn] There may be incompatibilities among your library dependencies; run 'evicted' to see detailed eviction warnings.
[info] Wrote /home/soft/kafka-manager-2.0.0.2/target/scala-2.12/kafka-manager_2.12-2.0.0.2.pom
[info] Main Scala API documentation to /home/soft/kafka-manager-2.0.0.2/target/scala-2.12/api...
[info] Non-compiled module 'compiler-bridge_2.12' for Scala 2.12.8. Compiling...
[info]   Compilation completed in 38.745s.
model contains 604 documentable templates
[info] Main Scala API documentation successful.
[info] Compiling 131 Scala sources and 2 Java sources to /home/soft/kafka-manager-2.0.0.2/target/scala-2.12/classes ...
[info] Done compiling.
[info] Packaging /home/soft/kafka-manager-2.0.0.2/target/scala-2.12/kafka-manager_2.12-2.0.0.2-javadoc.jar ...
[info] Done packaging.
[info] LESS compiling on 1 source(s)
[info] Packaging /home/soft/kafka-manager-2.0.0.2/target/scala-2.12/kafka-manager_2.12-2.0.0.2.jar ...
[info] Done packaging.
[info] Packaging /home/soft/kafka-manager-2.0.0.2/target/scala-2.12/kafka-manager_2.12-2.0.0.2-web-assets.jar ...
[info] Done packaging.
[info] Packaging /home/soft/kafka-manager-2.0.0.2/target/scala-2.12/kafka-manager_2.12-2.0.0.2-sans-externalized.jar ...
[info] Done packaging.
[success] All package validations passed
[info] Your package is ready in /home/soft/kafka-manager-2.0.0.2/target/universal/kafka-manager-2.0.0.2.zip
[success] Total time: 355 s, completed 2019-12-25 12:33:19
```
