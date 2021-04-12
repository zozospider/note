
- [1. 准备安装包](#1-准备安装包)
- [2. Local 模式](#2-local-模式)
    - [2.1. 配置](#21-配置)
    - [2.2. 启动](#22-启动)
    - [2.3. 停止](#23-停止)
    - [2.4. 监控页面](#24-监控页面)
    - [2.5. 提交 Job - 控制台操作方式](#25-提交-job---控制台操作方式)
    - [2.6. 提交 Job - 命令行方式](#26-提交-job---命令行方式)
- [3. Standalone 模式](#3-standalone-模式)
- [4. Yarn 模式](#4-yarn-模式)
- [5. Kubernetes 模式](#5-kubernetes-模式)

# 1. 准备安装包

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

# 2. Local 模式

## 2.1. 配置

1. 直接解压缩官网下载的压缩包

2. 修改 conf/flink-conf.yaml 中的配置项:

```properties
# 默认为 1, 推荐为当前机器的 CPU 核数
# 如果该参数配置小于提交的 Job 运行需要的 Slot 数, 则 Job 会一直等待直到 Slot 足够位置, 这样会导致 Job 运行超时失败
# 此处配置为 4 用于测试
taskmanager.numberOfTaskSlots: 4
```

## 2.2. 启动

执行以下命令启动集群:
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

## 2.3. 停止

执行以下命令启动集群:
```bash
bin/stop-cluster.sh
```

```bash
[zozo@vm03 flink-1.12.2-local]$ bin/stop-cluster.sh
# taskmanager
Stopping taskexecutor daemon (pid: 24783) on host vm03.
# jobmanager
Stopping standalonesession daemon (pid: 24514) on host vm03.
```

## 2.4. 监控页面

浏览器访问 http://vm03:8081 或 http://111.230.233.137:8081/

## 2.5. 提交 Job - 控制台操作方式

1. 打包编写的程序 jar 包:

Maven -> Lifecycle -> compile & package, 打包后的 jar 包在 target/flink-v1-1.0-SNAPSHOT.jar

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
- d. 点击 Submit 按钮, 此时会出现 Job 运行界面, 此时如果没有错误, 提交数据到 Job 的数据接收端即可看到相关指标的动态显示

4. 查看 Job:

Jobs -> Running Jobs / Completed Jobs

5. 取消 Job:

如 Job 报错, 超时, 或需停止当前正在运行的 Job, 在 Job 运行页面点击 Cancel Job 按钮

## 2.6. 提交 Job - 命令行方式

1. 打包编写的程序 jar 包:

Maven -> Lifecycle -> compile & package, 打包后的 jar 包在 target/flink-v1-1.0-SNAPSHOT.jar

2. 上传 Jar:

上传到安装包指定位置, 如: examples/test-flink-v1/flink-v1-1.0-SNAPSHOT.jar

3. 提交 Job:

```bash
# -c 为程序入口类
# -p 为并行度
# --hostname localhost --port 7777 为程序参数
# 执行提交后按 Ctrl + C 不会取消 Job
bin/flink run -c com.zozospider.flink.wordcount.StreamWordCount -p 3 examples/test-flink-v1/flink-v1-1.0-SNAPSHOT.jar --hostname localhost --port 7777
```

```bash
[zozo@vm03 flink-1.12.2-local]$ bin/flink run -c com.zozospider.flink.wordcount.StreamWordCount -p 2 examples/test-flink-v1/flink-v1-1.0-SNAPSHOT.jar --hostname localhost --port 7777
Job has been submitted with JobID 9f5a9586865b32bae609f88e5ce59d13
^C[zozo@vm03 flink-1.12.2-local]$
```

4. 查看 Job:

```bash
# 查看正在运行的 Job
bin/flink list
# 查看所有 Job
bin/flink list -a
```

```bash
[zozo@vm03 flink-1.12.2-local]$ bin/flink list
Waiting for response...
------------------ Running/Restarting Jobs -------------------
12.04.2021 16:03:19 : 9f5a9586865b32bae609f88e5ce59d13 : Flink Streaming Job (RUNNING)
--------------------------------------------------------------
No scheduled jobs.
[zozo@vm03 flink-1.12.2-local]$
[zozo@vm03 flink-1.12.2-local]$ bin/flink list -a
Waiting for response...
------------------ Running/Restarting Jobs -------------------
12.04.2021 16:03:19 : 9f5a9586865b32bae609f88e5ce59d13 : Flink Streaming Job (RUNNING)
--------------------------------------------------------------
No scheduled jobs.
---------------------- Terminated Jobs -----------------------
12.04.2021 15:34:02 : 4cec4004a96c6a425b5a9aa040cddc59 : Flink Streaming Job (FAILED)
12.04.2021 15:36:08 : 467dcd7cb87f5841b441157f5b04fafb : Flink Streaming Job (CANCELED)
12.04.2021 15:55:39 : 6f2e2d8e460b9e5706e3c056599d436d : Flink Streaming Job (CANCELED)
--------------------------------------------------------------
[zozo@vm03 flink-1.12.2-local]$
```

5. 取消 Job:

```bash
bin/flink cancel JOBID
```

```bash
[zozo@vm03 flink-1.12.2-local]$ bin/flink cancel 9f5a9586865b32bae609f88e5ce59d13
Cancelling job 9f5a9586865b32bae609f88e5ce59d13.
Cancelled job 9f5a9586865b32bae609f88e5ce59d13.
```

---

# 3. Standalone 模式

TODO

---

# 4. Yarn 模式

TODO

---

# 5. Kubernetes 模式

TODO

---
