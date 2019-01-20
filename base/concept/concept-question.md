
# 你知道有哪些方式可以实现锁？

# 说明 Java 中 equals() 和 hashcode() 方法的区别。

# Java 中 HashMap 如何确定索引位置?

# Java 中 HashMap 扩容为什么一定要是原有的 2 倍或 2^n?

# 什么是假死和脑裂，如何避免？

如果 Server1 为 Master 状态，但是由于负载过高（GC 占用时间长或 CPU 负载高）或网络闪断，无法对外提供服务，此时注册中心（如 ZooKeeper）认为其会话失效，释放节点，将 Server2 切换为 Master 状态，但是 Server1 依然认为自己是 Master，这种情况下称 Server1 为 `假死`，所产生的两个 Master 的情况称为 `脑裂`。

如何避免呢？以下是注册中心为 ZooKeeper 的解决方案:

- 1. 通过 ZooKeeper 的 ACL 权限控制，某个 Server 创建的节点必须携带 ZooKeeper 的 ACL 信息，以防止其他 Server 更新。

比如 Server1 为 Master 状态，出现假死后，ZooKeeper 将其移除，此时 Server2 创建节点，并切换为 Master。Server1 恢复后依然认为自己是 Master 并试图更新 ZooKeeper 数据，但是失败了，于是就将自己切换为 Slave 状态。

- 2. 通过延长 Master 节点假死保护期。

比如 Server1 为 Master 状态，出现假死后，ZooKeeper 将其移除，但是 Server2 收到节点释放通知后，会延迟一段时间（如 5 秒）再抢占 Master 节点，但是在此期间 Server1 不需要等待延迟，可以直接获得 Master 状态。这样可以避免因 Master 瞬间失效导致的资源消耗。

# 如何实现一个高并发统计功能？

# 进程和线程的通信有什么区别?

线程与进程之间通信非常消耗资源, 如 JDBC 需要及时释放资源. 线程之间通信可以基于内存, 效率是进程的 1000 倍以上.

# 什么是双端队列, 有什么用处?
