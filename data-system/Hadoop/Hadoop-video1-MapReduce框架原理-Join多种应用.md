


---

# 一 Reduce Join

Map 端的主要工作: 为来自不同表或文件的 key / value 对打标签以区别不同来源的记录. 然后用连接字段作为 key, 其余部分和新加的标志作为 value, 最后进行输出.

Reduce 端的主要工作: 在 Reduce 端以连接字段作为 key 的分组已经完成, 我们只需要在每一个分组当中将那些来源于不同文件的记录 (在 Map 阶段已经打标签), 最后进行合并即可.

## 1.1 代码测试

参考以下项目:

- code
  - [zozospider/note-hadoop-video1 (com.zozospider.hadoop.mapreduce.join.reducejoin.ReduceJoinDriver)](https://github.com/zozospider/note-hadoop-video1)

---

# 二 Map Join

## 2.1 代码测试

---
