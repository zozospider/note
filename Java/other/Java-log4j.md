
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

<!-- 通过 log4j 发送到 Flume -->
<dependency>
    <groupId>org.apache.flume.flume-ng-clients</groupId>
    <artifactId>flume-ng-log4jappender</artifactId>
    <version>1.8.0</version>
</dependency>
```

---

# log4j.properties

```properties
log4j.rootLogger = INFO,console,infoFile

# 通过字符串 `stringT`, `flume` 识别 Logger 对象
log4j.logger.stringT = INFO,stringT
log4j.logger.flume = INFO,flume

# 通过类名 `com.zozospider.test.Clazz` 识别 Logger 对象
log4j.logger.com.zozospider.test.Clazz = INFO,Clazz

# 指定 `flume` 不受全局管理 (即不会打印到 rootLogger 中)
log4j.additivity.flume = false
# log4j.additivity.stringT = false

# 输出信息到控制台
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.encoding = UTF-8
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss,SSS} [%t] [%c] [%p] - %m%n

# 输出 INFO 级别以上的日志
log4j.appender.infoFile = org.apache.log4j.DailyRollingFileAppender
log4j.appender.infoFile.File = /tmp/info.log
# log4j.appender.infoFile.File = D://tmp//info.log
log4j.appender.infoFile.Appender = true
log4j.appender.infoFile.Threshold = INFO
log4j.appender.infoFile.layout = org.apache.log4j.PatternLayout
log4j.appender.infoFile.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss,SSS} [%t] [%c] [%p] - %m%n

log4j.appender.stringT = org.apache.log4j.DailyRollingFileAppender
log4j.appender.stringT.File = /tmp/stringT.log
# log4j.appender.stringT.File = D://tmp//stringT.log
log4j.appender.stringT.Appender = true
log4j.appender.stringT.Threshold = INFO
log4j.appender.stringT.layout = org.apache.log4j.PatternLayout
log4j.appender.stringT.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss,SSS} [%t] [%c] [%p] - %m%n

log4j.appender.Clazz = org.apache.log4j.DailyRollingFileAppender
log4j.appender.Clazz.File = /tmp/Clazz.log
# log4j.appender.Clazz.File = D://tmp//Clazz.log
log4j.appender.Clazz.Appender = true
log4j.appender.Clazz.Threshold = INFO
log4j.appender.Clazz.layout = org.apache.log4j.PatternLayout
log4j.appender.Clazz.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss,SSS} [%t] [%c] [%p] - %m%n

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

    private static final Logger stringTLogger = Logger.getLogger("stringT");
    private static final Logger flumeLogger = Logger.getLogger("flume");
    private static final Logger clazzLogger = Logger.getLogger(Clazz.class);

    public static void main(String[] args) {
        stringTLogger.info("I am stringT");
        flumeLogger.info("I am flume");
        clazzLogger.info("I am Clazz");
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

    private static final Logger stringTLogger = LoggerFactory.getLogger("stringT");
    private static final Logger flumeLogger = LoggerFactory.getLogger("flume");
    private static final Logger clazzLogger = LoggerFactory.getLogger(Clazz.class);

    public static void main(String[] args) {
        stringTLogger.info("I am stringT");
        flumeLogger.info("I am flume");
        clazzLogger.info("I am Clazz");
    }
}
```

---
