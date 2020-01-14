
开源监控软件

- [Kafka监控工具汇总](https://juejin.im/post/5d5f62085188255d803faebb)

# linkedin: cruise-control & cruise-control-ui

- [linkedin/cruise-control](https://github.com/linkedin/cruise-control)
- [Connection to node -1 could not be established. Broker may not be available. (org.apache.kafka.clients.NetworkClient)](https://stackoverflow.com/questions/56161345/connection-to-node-1-could-not-be-established-broker-may-not-be-available-or)
要在 Kafka KAFKA_HOME/config/server.properties 设置 listeners=PLAINTEXT://:9092
- [Getting "Connection to node -1 could not be established. Broker may not be available." on all brokers](https://github.com/linkedin/cruise-control/issues/143)
要在 cruise-control/config/cruisecontrol.properties 中添加 cruise.control.metrics.reporter.bootstrap.servers=hadoop1:9092,hadoop2:9092,hadoop3:9092

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
