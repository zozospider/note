


---

# Document & Code

- [../Kafka-video](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video.md)

---

# 一. 说明

高级 API 优点:
- 不需要去管理 offset, 系统通过 ZooKeeper 自行管理.
- 不需要管理分区, 副本等情况, 系统自行管理.
- 消费者断线会自动根据上一次记录在 ZooKeeper 中的 offset 去接着获取数据 (默认设置 1 分钟更新一次 ZooKeeper 的 offset).
- 可以使用 group 来区分对同一个 topic 的不同程序访问分离开来 (不同 group 记录不同的 offset, 这样不同程序获取同一个 topic 才不会因为 offset 互相影响).

高级 API 缺点:
- 不能自行控制 offset.
- 不能细化控制分区, 副本, ZooKeeper 等.

低级 API 优点:
- 能够让开发者自己控制 offset.
- 自行控制连接分区, 对分区自定义负载均衡.
- 对 ZooKeeper 的依赖性降低 (如: offset 不一定非要由 ZooKeeper 存储, 自行存储 offset 即可, 比如存储在 内存 / Redis / 文件 中).

低级 API 缺点:
- 太复杂, 需要自行控制 offset, 连接哪个分区, 找到分区 Leader 等.

---


