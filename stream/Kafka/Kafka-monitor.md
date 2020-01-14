
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

# 如果只是在单个项目中修改的话,在build.sbt里添加 `resolvers += "aliyun" at "http://maven.aliyun.com/nexus/content/groups/public/"`
```




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

```bash
# 以下是 Mac 操作记录的
yhdeiMac:kafka-manager-2.0.0.2 yh$ ./sbt clean dist
Downloading sbt launcher for 1.2.8:
  From  http://repo.scala-sbt.org/scalasbt/maven-releases/org/scala-sbt/sbt-launch/1.2.8/sbt-launch.jar
    To  /Users/yh/.sbt/launchers/1.2.8/sbt-launch.jar
Getting org.scala-sbt sbt 1.2.8  (this may take some time)...
downloading https://repo1.maven.org/maven2/org/scala-sbt/sbt/1.2.8/sbt-1.2.8.jar ...
	[SUCCESSFUL ] org.scala-sbt#sbt;1.2.8!sbt.jar (1830ms)
downloading https://repo1.maven.org/maven2/org/scala-lang/scala-library/2.12.7/scala-library-2.12.7.jar ...
	[SUCCESSFUL ] org.scala-lang#scala-library;2.12.7!scala-library.jar (24620ms)
downloading https://repo1.maven.org/maven2/org/scala-sbt/main_2.12/1.2.8/main_2.12-1.2.8.jar ...
...

downloading https://repo1.maven.org/maven2/org/scala-sbt/ivy/ivy/2.3.0-sbt-cb9cc189e9f3af519f9f102e6c5d446488ff6832/ivy-2.3.0-sbt-cb9cc189e9f3af519f9f102e6c5d446488ff6832.jar ...
	[SUCCESSFUL ] org.scala-sbt.ivy#ivy;2.3.0-sbt-cb9cc189e9f3af519f9f102e6c5d446488ff6832!ivy.jar (4830ms)
:: retrieving :: org.scala-sbt#boot-app
	confs: [default]
	80 artifacts copied, 0 already retrieved (28561kB/118ms)
Getting Scala 2.12.7 (for sbt)...
:: retrieving :: org.scala-sbt#boot-scala
	confs: [default]
	5 artifacts copied, 0 already retrieved (19715kB/137ms)
[info] Loading settings for project kafka-manager-2-0-0-2-build from plugins.sbt ...
[info] Loading project definition from /Volumes/files/zz/other/kafka-manager-test/kafka-manager-2.0.0.2/project
[info] Updating ProjectRef(uri("file:/Volumes/files/zz/other/kafka-manager-test/kafka-manager-2.0.0.2/project/"), "kafka-manager-2-0-0-2-build")...
[info] downloading https://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/org.foundweekends/sbt-bintray/scala_2.12/sbt_1.0/0.5.4/jars/sbt-bintray.jar ...
[info] downloading https://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/com.typesafe.play/sbt-plugin/scala_2.12/sbt_1.0/2.6.21/jars/sbt-plugin.jar ...
[info] downloading https://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/com.typesafe.sbt/sbt-gzip/scala_2.12/sbt_1.0/1.0.2/jars/sbt-gzip.jar ...
[info] downloading https://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/com.typesafe.sbt/sbt-jshint/scala_2.12/sbt_1.0/1.0.6/jars/sbt-jshint.jar ...
[info] downloading https://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/com.typesafe.sbt/sbt-digest/scala_2.12/sbt_1.0/1.1.4/jars/sbt-digest.jar ...
[info] downloading https://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/com.typesafe.sbt/sbt-less/scala_2.12/sbt_1.0/1.1.2/jars/sbt-less.jar ...
[info] 	[SUCCESSFUL ] org.foundweekends#sbt-bintray;0.5.4!sbt-bintray.jar (3626ms)
[info] downloading https://repo1.maven.org/maven2/net/virtual-void/sbt-dependency-graph_2.12_1.0/0.9.2/sbt-dependency-graph-0.9.2.jar ...
[info] 	[SUCCESSFUL ] com.typesafe.sbt#sbt-gzip;1.0.2!sbt-gzip.jar (4701ms)
[info] 	[SUCCESSFUL ] net.virtual-void#sbt-dependency-graph;0.9.2!sbt-dependency-graph.jar (1484ms)
...

[info] 	[SUCCESSFUL ] commons-logging#commons-logging;1.1.1!commons-logging.jar (1459ms)
[info] 	[SUCCESSFUL ] com.fasterxml.jackson.core#jackson-databind;2.9.0!jackson-databind.jar(bundle) (6286ms)
[info] 	[SUCCESSFUL ] org.eclipse.jgit#org.eclipse.jgit;3.7.0.201502260915-r!org.eclipse.jgit.jar (7762ms)
[info] downloading https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.11.1/log4j-core-2.11.1-tests.jar ...
[info] 	[SUCCESSFUL ] org.apache.logging.log4j#log4j-core;2.11.1!log4j-core.jar(test-jar) (7294ms)
[info] Done updating.
[warn] There may be incompatibilities among your library dependencies; run 'evicted' to see detailed eviction warnings.
[info] Loading settings for project root from build.sbt ...
[info] Set current project to kafka-manager (in build file:/Volumes/files/zz/other/kafka-manager-test/kafka-manager-2.0.0.2/)
[success] Total time: 0 s, completed 2020-1-13 23:00:28
[info] Packaging /Volumes/files/zz/other/kafka-manager-test/kafka-manager-2.0.0.2/target/scala-2.12/kafka-manager_2.12-2.0.0.2-sources.jar ...
[info] Done packaging.
[info] Updating ...
Warning: node.js detection failed, sbt will use the Rhino based Trireme JavaScript engine instead to run JavaScript assets compilation, which in some cases may be orders of magnitude slower than using node.js.
[info] downloading https://repo1.maven.org/maven2/com/typesafe/play/filters-helpers_2.12/2.6.21/filters-helpers_2.12-2.6.21.jar ...
[info] downloading https://repo1.maven.org/maven2/com/typesafe/play/play-logback_2.12/2.6.21/play-logback_2.12-2.6.21.jar ...
[info] downloading https://repo1.maven.org/maven2/com/typesafe/play/play-akka-http-server_2.12/2.6.21/play-akka-http-server_2.12-2.6.21.jar ...
[info] downloading https://repo1.maven.org/maven2/com/typesafe/play/play-server_2.12/2.6.21/play-server_2.12-2.6.21.jar ...
[info] downloading https://repo1.maven.org/maven2/com/typesafe/akka/akka-actor_2.12/2.5.19/akka-actor_2.12-2.5.19.jar ...
[info] downloading https://repo1.maven.org/maven2/org/scala-lang/scala-library/2.12.8/scala-library-2.12.8.jar ...
[info] 	[SUCCESSFUL ] com.typesafe.play#play-logback_2.12;2.6.21!play-logback_2.12.jar (656ms)
[info] downloading https://repo1.maven.org/maven2/com/typesafe/akka/akka-slf4j_2.12/2.5.19/akka-slf4j_2.12-2.5.19.jar ...
[info] 	[SUCCESSFUL ] com.typesafe.akka#akka-slf4j_2.12;2.5.19!akka-slf4j_2.12.jar (814ms)
[info] downloading https://repo1.maven.org/maven2/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar ...
[info] 	[SUCCESSFUL ] com.typesafe.play#play-akka-http-server_2.12;2.6.21!play-akka-http-server_2.12.jar (1518ms)
[info] downloading https://repo1.maven.org/maven2/org/webjars/webjars-play_2.12/2.6.3/webjars-play_2.12-2.6.3.jar ...
...

[info] 	[SUCCESSFUL ] com.typesafe.play#play-docs_2.12;2.6.21!play-docs_2.12.jar (38859ms)
[info] 	[SUCCESSFUL ] org.scala-lang#scala-compiler;2.12.8!scala-compiler.jar (46979ms)
[info] 	[SUCCESSFUL ] com.typesafe.play#play-omnidoc_2.12;2.6.21!play-omnidoc_2.12.jar (56852ms)
[info] Done updating.
[warn] There may be incompatibilities among your library dependencies; run 'evicted' to see detailed eviction warnings.
[info] Wrote /Volumes/files/zz/other/kafka-manager-test/kafka-manager-2.0.0.2/target/scala-2.12/kafka-manager_2.12-2.0.0.2.pom
[info] Main Scala API documentation to /Volumes/files/zz/other/kafka-manager-test/kafka-manager-2.0.0.2/target/scala-2.12/api...
[info] LESS compiling on 1 source(s)
[info] Compiling 131 Scala sources and 2 Java sources to /Volumes/files/zz/other/kafka-manager-test/kafka-manager-2.0.0.2/target/scala-2.12/classes ...
[info] Non-compiled module 'compiler-bridge_2.12' for Scala 2.12.8. Compiling...
[info] Packaging /Volumes/files/zz/other/kafka-manager-test/kafka-manager-2.0.0.2/target/scala-2.12/kafka-manager_2.12-2.0.0.2-web-assets.jar ...
[info] Done packaging.
[info]   Compilation completed in 10.386s.
model contains 604 documentable templates
[info] Main Scala API documentation successful.
[info] Packaging /Volumes/files/zz/other/kafka-manager-test/kafka-manager-2.0.0.2/target/scala-2.12/kafka-manager_2.12-2.0.0.2-javadoc.jar ...
[info] Done compiling.
[info] Done packaging.
[info] Packaging /Volumes/files/zz/other/kafka-manager-test/kafka-manager-2.0.0.2/target/scala-2.12/kafka-manager_2.12-2.0.0.2.jar ...
[info] Done packaging.
[info] Packaging /Volumes/files/zz/other/kafka-manager-test/kafka-manager-2.0.0.2/target/scala-2.12/kafka-manager_2.12-2.0.0.2-sans-externalized.jar ...
[info] Done packaging.
[success] All package validations passed
[info] Your package is ready in /Volumes/files/zz/other/kafka-manager-test/kafka-manager-2.0.0.2/target/universal/kafka-manager-2.0.0.2.zip
[success] Total time: 474 s, completed 2020-1-13 23:08:22
yhdeiMac:kafka-manager-2.0.0.2 yh$ 
...

yhdeiMac:kafka-manager-2.0.0.2 yh$ pwd
/Volumes/files/zz/other/kafka-manager-test/kafka-manager-2.0.0.2
yhdeiMac:kafka-manager-2.0.0.2 yh$ ls -l
total 112
-rw-r--r--@ 1 yh  admin  11307  4 11  2019 LICENSE
-rw-r--r--@ 1 yh  admin   8686  4 11  2019 README.md
drwxr-xr-x@ 9 yh  admin    306  4 11  2019 app
-rw-r--r--@ 1 yh  admin   4242  4 11  2019 build.sbt
drwxr-xr-x@ 7 yh  admin    238  4 11  2019 conf
drwxr-xr-x@ 9 yh  admin    306  4 11  2019 img
drwxr-xr-x@ 6 yh  admin    204  1 13 22:46 project
drwxr-xr-x@ 5 yh  admin    170  4 11  2019 public
-rwxr-xr-x@ 1 yh  admin  21353  4 11  2019 sbt
drwxr-xr-x@ 4 yh  admin    136  4 11  2019 src
drwxr-xr-x  6 yh  admin    204  1 13 23:08 target
drwxr-xr-x@ 5 yh  admin    170  4 11  2019 test
yhdeiMac:kafka-manager-2.0.0.2 yh$ ls -l target/universal/
total 188896
-rw-r--r--  1 yh  admin  96714523  1 13 23:08 kafka-manager-2.0.0.2.zip
drwxr-xr-x  3 yh  admin       102  1 13 23:08 scripts
yhdeiMac:kafka-manager-2.0.0.2 yh$ 
```

连接失败报错时, 需要注意 kafka 服务器 JMX 监听 IP 是否为默认的 127.0.0.1, 需要在 Kafka 启动的时候指定 KAFKA_JMX_OPTS: -Djava.rmi.server.hostname=vm017
- [Failed to get broker metrics for BrokerIdentity #214  -  captainbupt commented on 24 Mar 2016](https://github.com/yahoo/kafka-manager/issues/214)
