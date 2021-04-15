
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

<!-- https://mvnrepository.com/artifact/org.apache.flink/flink-connector-elasticsearch-base -->
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-connector-elasticsearch-base_2.12</artifactId>
    <version>1.12.2</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.apache.flink/flink-connector-jdbc -->
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-connector-jdbc_2.12</artifactId>
    <version>1.12.2</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.apache.bahir/flink-connector-redis -->
<!-- 目前还没有 scala 2.12 的版本, 影响就是引用的某些 Flink 的类会出现两个相同的, 一个是在 2.12 的包里, 另一个是在 2.11 的包里, 所以不推荐使用 -->
<dependency>
    <groupId>org.apache.bahir</groupId>
    <artifactId>flink-connector-redis_2.11</artifactId>
    <version>1.0</version>
</dependency>
```
