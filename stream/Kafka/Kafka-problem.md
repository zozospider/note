
# cached zkversion not equal to that in zookeeper skip updating isr

- [记Kafka的一个BUG，导致整个集群不能工作](https://qingmu.io/2018/10/21/Kafka-bug-Cached-zkVersion-not-equal-to-that-in-zookeeper-broker-not-recovering/)

开启 JMX 时, 不要在 `bin/kafka-run-class.sh` 中指定 JMX_PORT, 否则调用其他脚本时, 会报端口已存在.
可在启动命令中 `JMX_PORT=9988 bin/kafka-server-start.sh -daemon config/server.properties` 或者 `bin/kafka-server-start.sh` 脚本中指定 JMX_PORT: `JMX_PORT=9988`
- [kafka开启JMX](https://www.jianshu.com/p/de4b4cbb0f3c)
- [kafka开启jmx_port后，报端口被占用](https://blog.csdn.net/weixin_37642251/article/details/90405635)
- [Port already in use](https://stackoverflow.com/questions/52997194/kafka-kubernetes-helm-usr-bin-kafka-avro-console-consumer)
