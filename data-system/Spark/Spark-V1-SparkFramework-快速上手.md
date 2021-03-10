
- [1. IDEA 创建 Spark 项目](#1-idea-创建-spark-项目)
- [2. WordCount](#2-wordcount)
    - [2.1. 案例分析](#21-案例分析)

---

# 1. IDEA 创建 Spark 项目

- 1. 创建 maven 项目

- 2. IDEA 将项目修改成 Scala 项目, 参考 https://github.com/zozospider/note/blob/master/Scala/Scala.md

- 3. pom.xml 添加以下依赖配置:
```xml
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-core_2.12</artifactId>
    <version>3.1.1</version>
</dependency>

<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-streaming_2.12</artifactId>
    <version>3.1.1</version>
</dependency>
```

- 4. 下载当前 Spark 对应的 [winutils](https://github.com/cdarlint/winutils), 并配置 Windows 环境变量 `HADOOP_HOME` 和 `%HADOOP_HOME%\bin` 或在 IDEA 的右上角 -> 项目 -> Edit Configurations -> 添加 Classpath, 然后重启 IDEA, 否则会报如下异常:
```java
Did not find winutils.exe: {}
java.io.FileNotFoundException: java.io.FileNotFoundException: HADOOP_HOME and hadoop.home.dir are unset.
```

---

# 2. WordCount

## 2.1. 案例分析

![image](https://github.com/zozospider/note/blob/master/data-system/Spark/Spark-V1-SparkFramework-快速上手/WordCount案例分析-1.png)

---

![image](https://github.com/zozospider/note/blob/master/data-system/Spark/Spark-V1-SparkFramework-快速上手/WordCount案例分析-2.png)

---
