
# 准备安装包

1. 下载安装包:
- https://flink.apache.org/downloads.html#all-stable-releases
- https://archive.apache.org/dist/flink/flink-1.12.2/

2. 如需添加 Hadoop 依赖, 将下载后的依赖 jar 包放入 Flink 安装目录的 ./lib 下:
- https://flink.apache.org/downloads.html#additional-components

3. 安装 scala 并配置环境变量 SCALA_HOME

```bash
# set scala
export SCALA_HOME=/home/zozo/app/scala/scala-2.12.13
export PATH=$SCALA_HOME/bin:$PATH
```

# Local 模式

1. 直接解压缩官网下载的压缩包

## 启动

2. 执行以下命令启动:
```bash
bin/start-cluster.sh
```

```bash
[zozo@vm03 flink-1.12.2-local]$ bin/start-cluster.sh
Starting cluster.
# 启动 jobmanager
Starting standalonesession daemon on host vm03.
# 启动 taskmanager
Starting taskexecutor daemon on host vm03.
[zozo@vm03 flink-1.12.2-local]$ 
[zozo@vm03 flink-1.12.2-local]$ jps
# jobmanager
24514 StandaloneSessionClusterEntrypoint
# taskmanager
24783 TaskManagerRunner
[zozo@vm03 flink-1.12.2-local]$
```

## 监控页面

浏览器访问 http://vm03:8081 或 http://111.230.233.137:8081/

## 提交 Job

1. 打包编写的程序 jar 包:

Maven -> Lifecycle -> compile & package, 打包后的 jar 包在 target/xxx-SNAPSHOT.jar

2. 上传 Jar:

- a. 浏览器访问http://111.230.233.137:8081/
- b. Submit New Job
- c. Uploaded Jars -> Add New -> 上传 Jar

3. 提交 Job:

- a. 点击需要提交的 jar 包
- b. 填写参数:
  - Entry Class - 程序入口类, 如: `com.zozospider.flink.wordcount.StreamWordCount`
  - Program Arguments - 程序参数, 如: `--hostname localhost --port 7777`
  - Parallelism - 并行度, 如: `3`
  - Savepoint Path - 保存点, 手动存盘路径
- c. 点击 Show Plan 按钮, 查看 Plan Visualization
- d. 点击 Submit 按钮, 此时会出现 Job 运行界面
- e. 如需取消 Job, 点击 Cancel Job 按钮

---

# Standalone 模式

---

# Yarn 模式

---

# Kubernetes 模式

---
