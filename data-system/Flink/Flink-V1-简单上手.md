
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
```
