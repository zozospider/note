
- [一 计数器](#一-计数器)
    - [1.1 代码测试](#11-代码测试)

---

# 一 计数器

Hadoop 为每个作业维护若干个内置计数器, 以描述多项指标. 例如, 某些计数器记录已处理的字节数和记录数, 使用户可监控已处理的输入数据量和已产生的输出数据量. 计数结果在程序运行控制台上查看.

计数器 API 如下:

```java
context.getCounter("groupName", "counterName").increment(1);
```

## 1.1 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.counter.CounterDriver1, com.zozospider.hadoop.mapreduce.counter.CounterDriver2)](https://github.com/zozospider/note-hadoop-video1)

---
