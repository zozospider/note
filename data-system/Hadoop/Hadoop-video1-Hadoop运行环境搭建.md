
# 下载解压

Apache 版本官网和下载地址如下:
- 官网地址: http://hadoop.apache.org/
- 下载地址: https://archive.apache.org/dist/hadoop/common/

下载后解压到指定路径, 并配置环境变量:
```bash
# set hadoop
export HADOOP_HOME=/home/zozo/app/hadoop/hadoop-2.7.2
export PATH=$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$PATH

# set java
export JAVA_HOME=/home/zozo/app/java/jdk1.8.0_192
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
```

---

# 目录结构

- __bin__: 命令 (重要)
- __sbin__: Hadoop 启动停止等命令, 大部分命令都会使用 (重要)
- __etc__: 配置文件, 需修改 (重要)
- __lib__: 本地库
- __libexec__: 本地库
- __include__: 其他语言如 C 语言库
- __share__: 说明文档, 案例

---
