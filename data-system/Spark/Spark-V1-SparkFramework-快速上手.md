
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

- 5. 在项目的 `resources` 目录下新建 `log4j.properties` 文件, 修改日志级别为 `INFO` / `WARN` / `ERROR` 即可控制输出的日志:
```properties
log4j.rootCategory=WARN, console
log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.console.target=System.err
log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=%d{yy/MM/dd HH:mm:ss} %p %c{1}: %m%n

# Set the default spark-shell log level to ERROR. When running the spark-shell, the
# log level for this class is used to overwrite the root logger's log level, so that
# the user can have different defaults for the shell and regular Spark apps.
log4j.logger.org.apache.spark.repl.Main=ERROR

# Settings to quiet third party logs that are too verbose
log4j.logger.org.spark_project.jetty=ERROR
log4j.logger.org.spark_project.jetty.util.component.AbstractLifeCycle=ERROR
log4j.logger.org.apache.spark.repl.SparkIMain$exprTyper=ERROR
log4j.logger.org.apache.spark.repl.SparkILoop$SparkILoopInterpreter=ERROR
log4j.logger.org.apache.parquet=ERROR
log4j.logger.parquet=ERROR

# SPARK-9183: Settings to avoid annoying messages when looking up nonexistent UDFs in SparkSQL with Hive support
log4j.logger.org.apache.hadoop.hive.metastore.RetryingHMSHandler=FATAL
log4j.logger.org.apache.hadoop.hive.ql.exec.FunctionRegistry=ERROR
```

---

# 2. WordCount

## 2.1. 案例分析

![image](https://github.com/zozospider/note/blob/master/data-system/Spark/Spark-V1-SparkFramework-快速上手/WordCount案例分析-1.png)

---

![image](https://github.com/zozospider/note/blob/master/data-system/Spark/Spark-V1-SparkFramework-快速上手/WordCount案例分析-2.png)

---
