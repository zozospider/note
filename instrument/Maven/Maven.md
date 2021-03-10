
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

## scope

```xml
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-core_2.12</artifactId>
    <version>3.1.1</version>
    <!-- provided 意味着打包的时候可以不用包进去 (假定目标的容器已经提供了这个 jar 包) -->
    <scope>provided</scope>
</dependency>
```

## 生成 jar 包排除文件

```xml
<build>
    <plugin>
        ...
    <plugin>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-jar-plugin</artifactId>
        <configuration>
            <archive>
                <!-- 配置主程序入口, 及 classpath -->
                <manifest>
                    <addClasspath>true</addClasspath>
                    <classpathPrefix>lib/</classpathPrefix>
                    <mainClass>com.zhoulz.homework.chaptor02.config.Config</mainClass>
                </manifest>
                <manifestEntries>
                    <!-- 配置 jar 包资源文件目录 -->
                    <Class-Path>config/</Class-Path>
                </manifestEntries>
            </archive>
            <!-- 将 jar 包里的所有资源文件排除掉 -->
            <excludes>
                <!-- exclude 的目录不是 src 下面的, 是以编译结果 classes 为根目录计算的 -->
                <exclude>**/*.properties</exclude>
                <exclude>*.yml</exclude>
            </excludes>
        </configuration>
    </plugin>
    <plugin>
        ...
    <plugin>
<build>
```

## 导出依赖的 jar 包

pom.xml 所在目录执行以下命令:

```
mvn dependency:copy-dependencies
```

导出到自定义目录中:

```
mvn dependency:copy-dependencies -DoutputDirectory=lib
```

设置依赖级别 (通常使用 compile 级别):

```
mvn dependency:copy-dependencies -DoutputDirectory=lib -DincludeScope=compile
```

## 打出的包包含所有依赖的 jar

- 1. 在 `pom.xml` 中添加 `maven-assembly-plugin` 插件
```xml
<plugins>
  <plugin>  
    <artifactId>maven-assembly-plugin</artifactId>  
    <configuration>  
      <!-- 这部分可有可无, 加上的话则直接生成可运行 jar 包 -->
      <!--
      <archive>
        <manifest>
          <mainClass>${exec.mainClass}</mainClass>
        </manifest>
      </archive>
      -->
      <descriptorRefs>  
        <descriptorRef>jar-with-dependencies</descriptorRef>  
      </descriptorRefs>  
    </configuration>
  </plugin>
</plugins>
```

- 2. 在 `pom.xml` 同级目录下打开命令行执行如下命令:

```
mvn assembly:assembly
```

## 包含本地 jar 包

- [Maven如何把本地依赖的jar包打包进无依赖的可执行jar包](https://fooyou.github.io/document/2016/01/15/maven-assemble-local-jar.html)

- `pom.xml` 配置引入本地 jar 包依赖如下:

```xml
<properties>
  <project.basedir>/tmp</project.basedir>
  <!-- Windows 格式 <project.basedir>D:/tmp</project.basedir> -->
</properties>

<dependency>
  <groupId>com.zozospider</groupId>
  <!-- 多个本地依赖的 artifactId 不能相同 -->
  <artifactId>test1</artifactId>
  <version>1.0</version>
  <scope>system</scope>
  <systemPath>${project.basedir}/lib/test1.jar</systemPath>
</dependency>
```

- `pom.xml` 配置打包插件 `maven-assembly-plugin` 如下:

```xml
<plugins>
  <plugin>  
    <artifactId>maven-assembly-plugin</artifactId>  
    <configuration>
      <descriptors>
        <descriptor>assembly.xml</descriptor>
      </descriptors>
      <!-- 这部分可有可无, 加上的话则直接生成可运行 jar 包 -->
      <!--
      <archive>
        <manifest>
          <mainClass>${exec.mainClass}</mainClass>
        </manifest>
      </archive>
      -->
      <!--
      <descriptorRefs>  
        <descriptorRef>jar-with-dependencies</descriptorRef>  
      </descriptorRefs>
      -->
    </configuration>
  </plugin>
</plugins>
```

- 其中 `assembly.xml` 内容如下:

```xml
<assembly xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.0" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.0 http://maven.apache.org/xsd/assembly-1.1.0.xsd">
  <!-- TODO: a jarjar format would be better -->
  <id>jar-with-dependencies</id>
  <formats>
    <format>jar</format>
  </formats>
  <includeBaseDirectory>false</includeBaseDirectory>
  <dependencySets>
    <dependencySet>
      <outputDirectory>/</outputDirectory>
      <useProjectArtifact>true</useProjectArtifact>
      <unpack>true</unpack>
      <scope>system</scope>
    </dependencySet>
  </dependencySets>
</assembly>
```

## 上传本地 jar 包到私服

- [Maven：利用mvn deploy命令将jar包上传到nexus服务器](https://my.oschina.net/u/566545/blog/371514)  (已测试有效)
- [上传jar包到nexus私服](https://my.oschina.net/lujianing/blog/297128)  (可参考)
- [mvn命令上传本地jar包到远程maven私服Mac](http://leoray.leanote.com/post/mac_upload_local_jar_to_private_maven)

以下为具体步骤:

### 1. 搭建nexus服务器

假设搭建成功后, 路径为 `http://localhost:8081/nexus/`.

### 2. Maven：利用mvn deploy命令将jar包上传到nexus服务器

- 2.1 修改 `$MAVEN_HOME/setting.xml`，在 `<profiles></profiles>` 标签内增加以下内容:
```xml
<profile>
    <id>DEV</id>
    <repositories>
        <repository>
            <id>nexus</id>
            <name>local_repositories</name>
            <url>http://localhost:8081/nexus/content/groups/public/</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
</profile>
```

- 2.2 修改 `$MAVEN_HOME/setting.xml`，在 `<servers></servers>`标签内增加以下内容:
```xml
<server>
   <id>releases</id> <!-- nexus仓库的ID，比如这里选的是releases仓库 -->
   <username>deployment</username> <!-- 这里使用的是nexus的帐号：deployment，可以在nexus服务器管理 -->
   <password>deployment123</password> <!-- deployment帐号默认密码： deployment123 -->
</server>
```

- 2.3 修改项目下的 `pom.xml`，在 `<project></project>` 标签内增加以下内容:
```xml
<distributionManagement>
    <snapshotRepository>
        <id>snapshots</id>
        <name>Nexus Snapshot Repository</name>
        <url>http://localhost:8081/nexus/content/repositories/snapshots</url>
    </snapshotRepository>

    <repository>
        <id>releases</id>
        <name>Nexus Release Repository</name>
        <url>http://localhost:8081/nexus/content/repositories/releases</url>
    </repository>
</distributionManagement>
```

- 2.4 访问 `http://localhost:8081/nexus/`，将 releases 仓库的 Deployment Policy 的只修改为 Allow Redeploy, 如下图:

![image](https://raw.githubusercontent.com/zozospider/note/master/instrument/Maven/Maven/Nexus-Allow-Redeploy.jpg)

- 2.5 在项目下执行 mvn deploy 命令:
```
mvn deploy:deploy-file -DgroupId=app.xxx -DartifactId=xxx -Dversion=1.0 -Dpackaging=jar -Dfile=D:\java\picture_server\target\xxx-1.0-SNAPSHOT.jar -Durl=http://localhost:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
```

- 2.6 执行成功后, 访问 `http://localhost:8081/nexus/content/repositories/releases/` 找到刚才上传成功的jar包.

- 2.7 修改其他项目 `pom.xml`, 在 `<dependencies></dependencies>` 标签内增加以下内容可以导入该依赖 jar 包:
```xml
<dependency>
    <groupId>app.xxx</groupId>
    <artifactId>xxx</artifactId>
    <version>1.0</version>
</dependency>
```

