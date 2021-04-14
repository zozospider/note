
# 1. IDEA 创建 Flink 项目

- 1. 创建 maven 项目

- 2. pom.xml 添加以下依赖配置:
```xml
<!-- https://mvnrepository.com/artifact/org.apache.flink/flink-java -->
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-java</artifactId>
    <version>1.12.2</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.apache.flink/flink-streaming-java -->
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-streaming-java_2.12</artifactId>
    <version>1.12.2</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.apache.flink/flink-clients -->
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-clients_2.12</artifactId>
    <version>1.12.2</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.apache.flink/flink-connector-kafka -->
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-connector-kafka_2.12</artifactId>
    <version>1.12.2</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.apache.bahir/flink-connector-redis -->
<!-- 目前还没有 scala 2.12 的版本, 不过影响不大, 将就用 -->
<dependency>
    <groupId>org.apache.bahir</groupId>
    <artifactId>flink-connector-redis_2.11</artifactId>
    <version>1.0</version>
</dependency>
```
