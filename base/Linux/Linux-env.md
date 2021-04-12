
# Java

Linux 安装 Java 步骤如下：

1. 下载 JDK: [Oracle JDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

2. 上传至指定路径，解压

3. 配置环境变量 ~/.bash_profile

在文件内添加以下内容：
```
# set java
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
```

4. 使环境变量生效

重新登录客户端或者执行以下命令：
```
source ~/.bash_profile
```

# 安装 tree

在 root 用户下运行如下命令:
```
yum -y install tree
```

# 安装 netcat

在 root 用户下运行如下命令:
```
yum install nc
```

# Python


