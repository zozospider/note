


---

# 一 MapReduce 跑的慢的原因

MapReduce 程序效率的瓶颈在于:

- 计算机性能:
  - CPU
  - 内存
  - 磁盘健康
  - 网络

- IO 操作: 
  - 数据倾斜
  - Map 和 Reduce 数设置不合理
  - Map 运行时间太长, 导致 Reduce 等待过久
  - 小文件过多
  - 大量的不可分块的超大文件
  - Spill 次数过多
  - Merge 次数过多

---

# 二 MapReduce 优化方法

MapReduce 优化方法主要从 6 个方面考虑:
- 数据输入
- Map 阶段
- Reduce 阶段
- IO 传输
- 数据倾斜
- 常用的调优参数

## 2.1 数据输入

- __合并小文件__: 在执行 MapReduce 任务前将小文件进行合并, 大量的小文件会产生大量的 Map 任务, 增大 Map 任务装载次数, 而任务的装载比较耗时, 从而导致 MapReduce 运行较慢.

- 采用 CombineTextInputFormat 来作为输入, 解决输入端大量小文件场景.

## 2.2 Map 阶段

- 减少 Spill (溢写) 次数: 通过调整 `io.sort.mb` 及 `sort.spill.percent` 参数值, 增大触发 Spill 的内存上限, 减少 Spill 次数, 从而减少磁盘 IO.

- 减少 Merge (合并) 次数: 通过调整 `io.sort.factor` 参数, 增大 Merge 的文件数目, 减少 Merge 次数, 从而缩短 MapReduce 处理时间.

- 在 Map 之后, 不影响业务逻辑的前提下, 先进行 Combine (Reducer) 处理, 减少 IO.

## 2.3 Reduce 阶段

- 合理设置 Map 和 Reduce 数: 两个都不能设置太少, 也不能设置太多. 太少会导致 Task 等待, 延长处理时间. 太多会导致 Map, Reduce 任务间竞争资源, 造成处理超时等错误.

- 设置 Map, Reduce 共存: 调整 `slowstart.completedmaps` 参数, 使 Map 运行到一定程度后, Reduce 也开始运行, 减少 Reduce 的等待时间.

- 规避使用 Reduce: 因为 Reduce 在用于连接数据集的时候会产生大量的网络消耗.

- 合理设置 Reduce 端的 Buffer: 默认情况下, 数据达到一个阈值的时候, Buffer 中的数据就会写入磁盘, 然后 Reduce 会从磁盘中获得所有的数据. 也就是说, Buffer 和 Reduce 是没有直接关联的, 中间多次写磁盘, 读磁盘的过程. 既然有这个弊端, 那么就可以通过参数来配置, 使得 Buffer 中的一部分数据可以直接输送到 Reduce, 从而减少 IO 开销. `mapred.job.reduce.input.buffer.percent` 默认为 0.0, 当该值大于 0 的时候, 会保存指定比例的内存读 Buffer 中的数据直接拿给 Reduce 使用. 这样一来, 设置 Buffer 需要内存, 读取数据需要内存, Reduce 计算也需要内存, 所以要根据作业的运行情况进行调整.

## 2.4 IO 传输

- 采用数据压缩的方式: 减少网络 IO 的时间, 可安装 Snappy / LZO 压缩编码器.

- 使用 SequenceFile 二进制文件.

## 2.5 数据倾斜



## 2.6 常用的调优参数



---

# 三 HDFS 小文件优化方法

---
