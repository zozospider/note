# 打包

## 生成 jar 包排除文件

```
<build>
    <plugin>
        ...
    <plugin>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-jar-plugin</artifactId>
        <configuration>
            <archive>
                <!-- 配置主程序入口，及classpath -->
                <manifest>
                    <addClasspath>true</addClasspath>
                    <classpathPrefix>lib/</classpathPrefix>
                    <mainClass>com.zhoulz.homework.chaptor02.config.Config</mainClass>
                </manifest>
                <manifestEntries>
                    <!-- 配置jar包资源文件目录 -->
                    <Class-Path>config/</Class-Path>
                </manifestEntries>
            </archive>
            <!-- 将jar包里的所有资源文件排除掉 -->
            <excludes>
                <exclude>**/*.properties</exclude>
            </excludes>
        </configuration>
    </plugin>
    <plugin>
        ...
    <plugin>
<build>
```
