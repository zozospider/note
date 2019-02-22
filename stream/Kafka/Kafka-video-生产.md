


---

# Document & Code

- page
  - [../Kafka-video](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video.md)

- link
  - [零拷贝底层实现原理](https://juejin.im/entry/59b740fdf265da06633d02cf)
  - [浅析Linux中的零拷贝技术](https://www.jianshu.com/p/fad3339e3448)
  - [Linux 中的零拷贝技术，第 1 部分](https://www.ibm.com/developerworks/cn/linux/l-cn-zerocopy1/index.html)
  - [Linux 中直接 I/O 机制的介绍](https://www.ibm.com/developerworks/cn/linux/l-cn-directio/)
  - [Kafka相关内容总结（存储和性能）](https://www.w3xue.com/exp/article/20191/16157.html)

---

# 零拷贝

![image](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video-%E7%94%9F%E4%BA%A7/Kafka%E9%9B%B6%E6%8B%B7%E8%B4%9D.png?raw=true)

// TODO

# 预读 & 后写

// TODO

# 文件存储

每个 Partition 中的消息都是有序的, 生产的消息被不断追加到 Partition log 上, 每个消息都被赋予了一个唯一的 offset.

名为 `first-0` 的文件夹存储格式如下, 其中 *.log 文件存储实际数据, *.index 存储数据索引:
```
00000000000000000000.log
00000000000000000000.index
00000000000000001000.log
00000000000000001000.index
00000000000000002000.log
00000000000000003000.index
```

假设 `00000000000000001000.log` 存储内容为:
```
1001abc1002wedohave1003nonono1004great
```

则 `00000000000000001000.index` 存储 offset 对应的位置, 如需找到 offset 为 1003 的数据, 则从 `00000000000000001000.log` 文件的 19 位置开始:
```
1001 > 0
1002 > 7
1003 > 19
1004 > 29
```

# 分区原则

- 指定了 partition, 则直接使用.
- 未指定 partition 但指定了 key, 通过 key 进行 hash 算出 partion.
- partition 和 key 都未指定, 使用轮询选出一个 partition.


