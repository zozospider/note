
- [1. 开发环境](#1-开发环境)
- [2. 本地环境](#2-本地环境)
    - [2.1. 安装](#21-安装)
    - [2.2. 启动](#22-启动)
    - [2.3. WordCount 测试](#23-wordcount-测试)
    - [2.4. 监控页面](#24-监控页面)
    - [2.5. 提交应用](#25-提交应用)
- [3. 独立部署环境](#3-独立部署环境)
- [4. YARN 环境](#4-yarn-环境)
- [5. Windows 环境](#5-windows-环境)

---

# 1. 开发环境

IDEA 开发中直接运行代码的环境

---

# 2. 本地环境

## 2.1. 安装

1. 安装 scala 并配置环境变量 SCALA_HOME

```bash
# set scala
export SCALA_HOME=/home/zozo/app/scala/scala-2.12.13
export PATH=$SCALA_HOME/bin:$PATH
```

2. 安装 Spark: 直接解压缩官网下载的压缩包

## 2.2. 启动

1. 执行以下命令启动 Spark 交互窗口, 如能正常执行 Scala 命令则启动成功:

```bash
bin/spark-shell
```

```bash
[zozo@vm017 spark-3.1.1-bin-hadoop3.2-local]$ bin/spark-shell
21/03/11 22:20:56 WARN NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Using Spark's default log4j profile: org/apache/spark/log4j-defaults.properties
Setting default log level to "WARN".
To adjust logging level use sc.setLogLevel(newLevel). For SparkR, use setLogLevel(newLevel).
Spark context Web UI available at http://vm017:4040
Spark context available as 'sc' (master = local[*], app id = local-1615472464495).
Spark session available as 'spark'.
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 3.1.1
      /_/

Using Scala version 2.12.10 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_192)
Type in expressions to have them evaluated.
Type :help for more information.

scala> val i = 1
i: Int = 1

scala>
```

## 2.3. WordCount 测试

在 data 目录下创建 `data/test-wordcount/1.txt` 和 `data/test-wordcount/2.txt` 测试文件, 并在 Spark 交互窗口执行以下命令:

```bash
scala> sc.textFile("data/test-wordcount").flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).collect
res0: Array[(String, Int)] = Array((Spark,1), (World,2), (Hello,3))
```

## 2.4. 监控页面

保持 spark-shell 启动并提交测试 Job, 然后浏览器访问 http://vm017:4040 或 http://193.112.38.200:4040/ 可看到 Job 相关信息

## 2.5. 提交应用

通过以下命令提交测试应用 (计算 Pi 的值), 在控制台打印的结果中可以看到 Pi 的值

```bash
bin/spark-submit \
--class org.apache.spark.examples.SparkPi \
--master local[2] \
./examples/jars/spark-examples_2.12-3.1.1.jar \
10
```

```bash
[zozo@vm017 spark-3.1.1-bin-hadoop3.2-local]$ bin/spark-submit \
> --class org.apache.spark.examples.SparkPi \
> --master local[2] \
> ./examples/jars/spark-examples_2.12-3.1.1.jar \
> 10
...
21/03/11 22:49:46 INFO SparkContext: Running Spark version 3.1.1
...
memory -> name: memory, amount: 1024, script: , vendor: , offHeap -> name: offHeap, amount: 0, script: , vendor: ), task resources: Map(cpus -> name: cpus, amount: 1.0)
...
Pi is roughly 3.143311143311143
...
[zozo@vm017 spark-3.1.1-bin-hadoop3.2-local]$
```

---

# 3. 独立部署环境

---

# 4. YARN 环境

---

# 5. Windows 环境

---
