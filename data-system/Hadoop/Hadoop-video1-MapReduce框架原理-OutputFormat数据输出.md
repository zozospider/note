
- [一 概述](#一-概述)
- [二 自定义 OutputFormat](#二-自定义-outputformat)
    - [2.1 代码测试](#21-代码测试)

---

# 一 概述

`OutputFormat` 是 MapReduce 输出的基类, 所有 MapReduce 输出都实现了 `OutputFormat` 接口.

- `TextOutputFormat`: 默认输出格式, 它把每条记录写为文本行, 它的键值可以是任意类型, 因为 `TextOutputFormat` 会调用 `toString()` 方法把它们转换为字符串.

- `SequenceFileOutputFormat`: 将 SequenceFileOutputFormat 输出作为后续 MapReduce 任务的输入是一种好的输出格式, 因为它格式紧凑, 很容易被压缩.

---

# 二 自定义 OutputFormat

步骤:
- 1. 自定义一个类继承 `FileOutputFormat`.
- 2. 改写 `RecordWriter`, 具体改写 `write()` 方法 (输出数据的方法).

## 2.1 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.output.custom.CustomDriver)](https://github.com/zozospider/note-hadoop-video1)

---
