


---

# DataFrame

## DataFrame 简单演示

1. 在 data 目录下创建 `data/test-sparksql/user.json` 文件:

```json
{"username": "zhangsan", "age": 30}
{"username": "lisi", "age": 20}
{"username": "wangwu", "age": 40}
```

2. 执行以下命令启动 Spark 交互窗口, 进行 DataFrame 的简单演示:

```bash
bin/spark-shell
```

```bash
Using Spark's default log4j profile: org/apache/spark/log4j-defaults.properties
Setting default log level to "WARN".
To adjust logging level use sc.setLogLevel(newLevel). For SparkR, use setLogLevel(newLevel).
Spark context Web UI available at http://DESKTOP-073FH55:4040
Spark context available as 'sc' (master = local[*], app id = local-1617784617874).
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

scala> sc
res0: org.apache.spark.SparkContext = org.apache.spark.SparkContext@76296c86

scala> spark
res1: org.apache.spark.sql.SparkSession = org.apache.spark.sql.SparkSession@2f96e49f

scala> spark.read.
csv   format   jdbc   json   load   option   options   orc   parquet   schema   table   text   textFile

scala> spark.read.json("../data/test-sparksql/user.json")
res3: org.apache.spark.sql.DataFrame = [age: bigint, username: string]

# 获取转换后的 DataFrame 结果对象
scala> val df = spark.read.json("../data/test-sparksql/user.json")
df: org.apache.spark.sql.DataFrame = [age: bigint, username: string]

# 查看 DataFrame 对象信息
scala> df.show
+---+--------+
|age|username|
+---+--------+
| 30|zhangsan|
| 20|    lisi|
| 40|  wangwu|
+---+--------+

# 当前 Session 范围有效的表
# 通过 DataFrame 对象, 创建临时视图 (表), 表名为 user
# scala> df.createOrReplaceTempView("user")
scala> df.createTempView("user")

# 对 user 表执行 SQL 查询
scala> spark.sql("select * from user").show
+---+--------+
|age|username|
+---+--------+
| 30|zhangsan|
| 20|    lisi|
| 40|  wangwu|
+---+--------+

# 对 user 表执行 SQL 查询
scala> spark.sql("select age from user").show
+---+
|age|
+---+
| 30|
| 20|
| 40|
+---+

# 对 user 表执行 SQL 查询
scala> spark.sql("select avg(age) from user").show
+--------+
|avg(age)|
+--------+
|    30.0|
+--------+

scala>

# ------

# 全局表
# 创建全局表
scala> df.createOrReplaceGlobalTempView("emp")
21/04/07 17:06:05 WARN ObjectStore: Failed to get database global_temp, returning NoSuchObjectException

# 新的 Session 中也有效
scala> spark.newSession.sql("select * from global_temp.emp").show
+---+--------+
|age|username|
+---+--------+
| 30|zhangsan|
| 20|    lisi|
| 40|  wangwu|
+---+--------+

scala>

# ------

# DSL 语法

# 查看 DataFrame 的 Schema 信息
scala> df.printSchema
root
 |-- age: long (nullable = true)
 |-- username: string (nullable = true)

# 只查看 "age" 列数据
scala> df.select("age").show
+---+
|age|
+---+
| 30|
| 20|
| 40|
+---+

# 查看 "username" 列数据以及 "age+1" 数据
scala> df.select($"age" + 1).show
scala> df.select('age + 1).show
scala> df.select($"username", $"age" + 1).show
scala> df.select('username, 'age + 1).show

# 查看 "age" 大于 20 的数据
scala> df.filter('age > 20).show

# 按照 "age" 分组, 查看数据条数
scala> df.groupBy('age).count.show
```

## RDD 和 DataFrame 转换

```bash
# RDD 转换成 DataFrame
scala> val rdd = sc.makeRDD(List(1, 2, 3, 4))
rdd: org.apache.spark.rdd.RDD[Int] = ParallelCollectionRDD[82] at makeRDD at <console>:24

scala> val df = rdd.toDF("id")
df: org.apache.spark.sql.DataFrame = [id: int]

scala> df.show
+---+
| id|
+---+
|  1|
|  2|
|  3|
|  4|
+---+

# ------

# DataFrame 转换成 RDD
scala> df.rdd
res29: org.apache.spark.rdd.RDD[org.apache.spark.sql.Row] = MapPartitionsRDD[90] at rdd at <console>:26

scala> val rdd2 = df.rdd
rdd2: org.apache.spark.rdd.RDD[org.apache.spark.sql.Row] = MapPartitionsRDD[90] at rdd at <console>:26

scala> rdd2.collect.foreach(println)
[1]
[2]
[3]
[4]

scala>
```

---

# DataSet

```bash
scala> case class Person(name: String, age: Long)
defined class Person

scala> val list = List(Person("zhangsan", 30), Person("lisi", 40))
list: List[Person] = List(Person(zhangsan,30), Person(lisi,40))

scala> val ds = list.toDS
ds: org.apache.spark.sql.Dataset[Person] = [name: string, age: bigint]

scala> ds.show
+--------+---+
|    name|age|
+--------+---+
|zhangsan| 30|
|    lisi| 40|
+--------+---+

scala>

# ------

# DataFrame 转换为 DataSet
scala> ds.show
+---+--------+
|age|username|
+---+--------+
| 30|zhangsan|
| 20|    lisi|
| 40|  wangwu|
+---+--------+

scala> df.printSchema
root
 |-- age: long (nullable = true)
 |-- username: string (nullable = true)

scala> case class Emp(age: Long, username: String)
defined class Emp

scala> val ds = df.as[Emp]
ds: org.apache.spark.sql.Dataset[Emp] = [age: bigint, username: string]

# ------

# DataSet 转换为 DataFrame
scala> val df = ds.toDF
df: org.apache.spark.sql.DataFrame = [age: bigint, username: string]

scala> df.show
+---+--------+
|age|username|
+---+--------+
| 30|zhangsan|
| 20|    lisi|
| 40|  wangwu|
+---+--------+

# ------

# RDD 转换为 DataSet
scala> val rdd = sc.makeRDD(List(Emp(30, "zhangsan"), Emp(40, "lisi")))
rdd: org.apache.spark.rdd.RDD[Emp] = ParallelCollectionRDD[0] at makeRDD at <console>:26

scala> val ds = rdd.toDS
ds: org.apache.spark.sql.Dataset[Emp] = [age: bigint, username: string]

scala> ds.show
+---+--------+
|age|username|
+---+--------+
| 30|zhangsan|
| 40|    lisi|
+---+--------+

# ------

# DataSet 转换为 RDD
scala> val rdd2 = ds.rdd
rdd2: org.apache.spark.rdd.RDD[Emp] = MapPartitionsRDD[6] at rdd at <console>:25

scala> rdd2.collect.foreach(println)
Emp(30,zhangsan)
Emp(40,lisi)

scala>
```

---

# 数据读取与保存

## load

```bash
# 读取 .parquet 格式
scala> val df = spark.read.load("../examples/src/main/resources/users.parquet")
df: org.apache.spark.sql.DataFrame = [name: string, favorite_color: string ... 1 more field]

scala> df.show
+------+--------------+----------------+
|  name|favorite_color|favorite_numbers|
+------+--------------+----------------+
|Alyssa|          null|  [3, 9, 15, 20]|
|   Ben|           red|              []|
+------+--------------+----------------+

# 保存 .parquet 格式
scala> df.write.save("../data/test-sparksql/users.parquet.output")

# ------

# 读取 json 格式
# scala> val df = spark.read.json("../data/test-sparksql/user.json")
scala> val df = spark.read.format("json").load("../data/test-sparksql/user.json")
df: org.apache.spark.sql.DataFrame = [age: bigint, username: string]

scala> df.show
+---+--------+
|age|username|
+---+--------+
| 30|zhangsan|
| 20|    lisi|
| 40|  wangwu|
+---+--------+

scala> spark.sql("select * from json.`../data/test-sparksql/user.json`").show
+---+--------+
|age|username|
+---+--------+
| 30|zhangsan|
| 20|    lisi|
| 40|  wangwu|
+---+--------+

# 保存 json 文件
scala> df.write.format("json").save("../data/test-sparksql/user.json.output")
# 保存多个 json 文件到同一个目录下
df.write.format("json").mode("append").save("../data/test-sparksql/user.json.output")
# 清空原来目录下的内容, 生成一个新的 json 文件
scala> df.write.format("json").mode("overwrite").save("../data/test-sparksql/user.json.output")
# 如果目录存在则忽略, 不存在则生成一个新的 json 文件
scala> df.write.format("json").mode("ignore").save("../data/test-sparksql/user.json.output")

# ------

# 读取 csv
scala> val df = spark.read.format("csv").option("sep", ";").option("inferSchema", "true").option("header", "true").load("../examples/src/main/resources/people.csv")
df: org.apache.spark.sql.DataFrame = [name: string, age: int ... 1 more field]

scala> df.show
+-----+---+---------+
| name|age|      job|
+-----+---+---------+
|Jorge| 30|Developer|
|  Bob| 32|Developer|
+-----+---+---------+
```

---
