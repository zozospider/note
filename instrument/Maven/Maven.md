
# 安装

## 本地仓库

```xml
<localRepository>D:\zz\app\maven\apache-maven-3.6.0-repo</localRepository>
```

## 代理

```xml
  <mirrors>
     ...
     <mirror>  
      <id>alimaven</id>  
      <name>aliyun maven</name>  
      <url>http://maven.aliyun.com/nexus/content/groups/public/</url>  
      <mirrorOf>central</mirrorOf>          
    </mirror>
  </mirrors>
```

## 环境变量
```
MAVEN_HOME
D:\zz\app\maven\apache-maven-3.6.0

PATH
%MAVEN_HOME%\bin
```

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

## 导出依赖的 jar 包

pom.xml 所在目录执行以下命令：
```
mvn dependency:copy-dependencies
```

导出到自定义目录中：
```
mvn dependency:copy-dependencies -DoutputDirectory=lib
```

设置依赖级别（通常使用 compile 级别）：
```
mvn dependency:copy-dependencies -DoutputDirectory=lib -DincludeScope=compile
```

## 打出的包包含所有依赖的 jar

- 1. 在pom.xml中添加maven-assembly-plugin插件
```xml
        <plugins>
          <plugin>  
              <artifactId>maven-assembly-plugin</artifactId>  
              <configuration>  
                  <!--这部分可有可无,加上的话则直接生成可运行jar包-->
                  <!--<archive>-->
                      <!--<manifest>-->
                          <!--<mainClass>${exec.mainClass}</mainClass>-->
                      <!--</manifest>-->
                  <!--</archive>-->
                  <descriptorRefs>  
                      <descriptorRef>jar-with-dependencies</descriptorRef>  
                  </descriptorRefs>  
             </configuration>
        </plugin>
```

- 2. 在pom.xml同级目录下打开命令行执行如下命令:
```
mvn assembly:assembly
```

## 上传本地 jar 包到私服

[mvn命令上传本地jar包到远程maven私服Mac](http://leoray.leanote.com/post/mac_upload_local_jar_to_private_maven)

