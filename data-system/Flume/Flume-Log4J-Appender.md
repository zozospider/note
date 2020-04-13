
---

# 说明

程序可以通过 log4j 将日志发送到 Flume

- [使用Log4j将日志实时写入Flume](https://blog.csdn.net/HG_Harvey/article/details/78357556)

---

# Maven 配置

```xml
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>

<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.7.21</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.apache.flume.flume-ng-clients</groupId>
    <artifactId>flume-ng-log4jappender</artifactId>
    <version>1.8.0</version>
</dependency>
```

# 程序 log4j.properties 配置 - 方式一

以下为 `resources` 资源目录下的 `log4j.properties` 配置:
```properties
log4j.rootLogger=INFO,stdout,flume

log4j.appender.stdout = org.apache.log4j.ConsoleAppender
log4j.appender.stdout.target = System.out
log4j.appender.stdout.layout = org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss,SSS} [%t] [%c] [%p] - %m%n

log4j.appender.flume = org.apache.flume.clients.log4jappender.Log4jAppender
log4j.appender.flume.Hostname = 127.0.0.1
log4j.appender.flume.Port = 4141
log4j.appender.flume.UnsafeMode = true
```

# 程序 log4j.properties 配置 - 方式二

```properties
log4j.rootLogger = INFO,console,infoFile

log4j.logger.flume = INFO,flume

# 输出信息到控制台
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.encoding = UTF-8
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss,SSS} [%t] [%c] [%p] - %m%n

# 输出 INFO 级别以上的日志
log4j.appender.infoFile = org.apache.log4j.DailyRollingFileAppender
log4j.appender.infoFile.File = /tmp/info.log
log4j.appender.infoFile.Appender = true
log4j.appender.infoFile.Threshold = INFO
log4j.appender.infoFile.layout = org.apache.log4j.PatternLayout
log4j.appender.infoFile.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss,SSS} [%t] [%c] [%p] - %m%n

log4j.appender.flume = org.apache.flume.clients.log4jappender.Log4jAppender
log4j.appender.flume.Hostname = 127.0.0.1
log4j.appender.flume.Port = 4141
log4j.appender.flume.UnsafeMode = true
```

---

# 程序逻辑 - 方式一

以下为 `App.class` 程序逻辑:
```java
import org.apache.log4j.Logger;

/**
 * 循环打印值，模仿日志输出
 */
public class App {

    private static final Logger flumeLogger = Logger.getLogger("flume");

    public static void main(String[] args) throws InterruptedException {
        int index = 0;
        while (true) {
            Thread.sleep(1000);
            flumeLogger.info("value is：" + index++);
        }
    }
}
```

---

# 程序逻辑 - 方式二

以下为 `App.class` 程序逻辑:
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 循环打印值，模仿日志输出
 */
public class App {

    private static final Logger flumeLogger = LoggerFactory.getLogger("flume");

    public static void main(String[] args) throws InterruptedException {
        int index = 0;
        while (true) {
            Thread.sleep(1000);
            flumeLogger.info("value is：" + index++);
        }
    }
}
```

---

# Flume 配置

以下为 Flume 的 `flume-conf.properties` 配置:
```properties
a1.sources = s1
a1.sinks = c1 c2
a1.channels = k1 k2

a1.sources.s1.type = avro
a1.sources.s1.bind = 0.0.0.0
a1.sources.s1.port = 4141

a1.channels.c1.type = memory
a1.channels.c2.type = memory

a1.sinks.k1.type = logger
a1.sinks.k2.type = file_roll
a1.sinks.k2.sink.directory = /home/zozo/app/flume/apache-flume-1.8.0-bin-file_roll_dir
a1.sinks.k2.sink.pathManager = ROLLTIME
a1.sinks.k2.sink.pathManager.extension = log
a1.sinks.k2.sink.pathManager.prefix = roll-dir-
a1.sinks.k2.sink.rollInterval = 14100
a1.sinks.k2.sink.batchSize = 100

a1.sources.s1.channels = c1 c2
a1.sinks.k1.channel = c1
a1.sinks.k2.channel = c2
```

---

# 执行程序

- 执行 App.class 后控制台会生成如下日志
```
2017-09-25 10:16:12,862 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:94)] Event: { headers:{flume.client.log4j.timestamp=1508926512143, flume.client.log4j.logger.name=LoggerGenerate, flume.client.log4j.log.level=20000, flume.client.log4j.message.encoding=UTF8} body: 76 61 6C 75 65 20 69 73 EF BC 9A 30             value is...0 }
2017-09-25 10:16:12,863 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:94)] Event: { headers:{flume.client.log4j.timestamp=1508926513197, flume.client.log4j.logger.name=LoggerGenerate, flume.client.log4j.log.level=20000, flume.client.log4j.message.encoding=UTF8} body: 76 61 6C 75 65 20 69 73 EF BC 9A 31             value is...1 }
2017-09-25 10:16:12,864 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:94)] Event: { headers:{flume.client.log4j.timestamp=1508926514203, flume.client.log4j.logger.name=LoggerGenerate, flume.client.log4j.log.level=20000, flume.client.log4j.message.encoding=UTF8} body: 76 61 6C 75 65 20 69 73 EF BC 9A 32             value is...2 }
2017-09-25 10:16:13,082 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:94)] Event: { headers:{flume.client.log4j.timestamp=1508926515210, flume.client.log4j.logger.name=LoggerGenerate, flume.client.log4j.log.level=20000, flume.client.log4j.message.encoding=UTF8} body: 76 61 6C 75 65 20 69 73 EF BC 9A 33             value is...3 }
2017-09-25 10:16:14,092 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:94)] Event: { headers:{flume.client.log4j.timestamp=1508926516221, flume.client.log4j.logger.name=LoggerGenerate, flume.client.log4j.log.level=20000, flume.client.log4j.message.encoding=UTF8} body: 76 61 6C 75 65 20 69 73 EF BC 9A 34             value is...4 }
2017-09-25 10:16:15,103 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:94)] Event: { headers:{flume.client.log4j.timestamp=1508926517228, flume.client.log4j.logger.name=LoggerGenerate, flume.client.log4j.log.level=20000, flume.client.log4j.message.encoding=UTF8} body: 76 61 6C 75 65 20 69 73 EF BC 9A 35             value is...5 }
```

- Flume 也会通过 `a1.sinks.k1.type = logger` 打印出接收数据到 `./logs/flume.log` 中, 另外也会通过 `a1.sinks.k2.type = file_roll` 落地数据到 `/home/zozo/app/flume/apache-flume-1.8.0-bin-file_roll_dir` 中.

---
