


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



## 2.2 Map 阶段



## 2.3 Reduce 阶段



## 2.4 IO 传输



## 2.5 数据倾斜



## 2.6 常用的调优参数



---

# 三 HDFS 小文件优化方法

---
