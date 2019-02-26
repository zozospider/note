


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
  - [Double-ended queue](https://en.wikipedia.org/wiki/Double-ended_queue)

---

# 零拷贝

![image](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video-%E7%94%9F%E4%BA%A7/Kafka%E9%9B%B6%E6%8B%B7%E8%B4%9D.png?raw=true)

// TODO

# 预读 & 后写

// TODO

# 分区

消息发送时都被发送到一个 Topic, 其本质就是一个目录, 而 Topic 是由一些 Partition Logs (分区日志) 组成, 其组织结构如下图:

![image](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video-%E7%94%9F%E4%BA%A7/log_anatomy.png?raw=true)

每个 Partition 中的消息都是有序的, 生产的消息被不断追加到 Partition log 上, 其中的每一个消息都被赋予了一个唯一的 offset 值.

## 分区的原因

- 方便在集群中扩展, 每个 Partition 可以通过调整以适应它所在的机器, 而一个 Topic 又可以有多个 Partition, 因此整个集群就可以适应任意大小的数据了.

- 可以提高并发, 因为可以以 Partition 为单位读写.

## 文件存储

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

Kafka 实际存储数据格式封装在 `Message.scala` 中:
```scala
/**
 * A message. The format of an N byte message is the following:
 *
 * If magic byte is 0
 *
 * 1. 1 byte "magic" identifier to allow format changes
 *
 * 2. 4 byte CRC32 of the payload
 *
 * 3. N - 5 byte payload
 *
 * If magic byte is 1
 *
 * 1. 1 byte "magic" identifier to allow format changes
 *
 * 2. 1 byte "attributes" identifier to allow annotations on the message independent of the version (e.g. compression enabled, type of codec used)
 *
 * 3. 4 byte CRC32 of the payload
 *
 * 4. N - 6 byte payload
 * 
 */
class Message(val buffer: ByteBuffer) {
  
  import kafka.message.Message._

  ...
}
```

## 分区原则

- 指定了 partition, 则直接使用.
- 未指定 partition 但指定了 key, 通过 key 进行 hash 算出 partion.
- partition 和 key 都未指定, 使用轮询选出一个 partition.

以下为在未指定 partition 的情况下的默认分区实现算法 (会判断是否指定了 key):
```java
/**
 * The default partitioning strategy:
 * <ul>
 * <li>If a partition is specified in the record, use it
 * <li>If no partition is specified but a key is present choose a partition based on a hash of the key
 * <li>If no partition or key is present choose a partition in a round-robin fashion
 */
public class DefaultPartitioner implements Partitioner {

    private final ConcurrentMap<String, AtomicInteger> topicCounterMap = new ConcurrentHashMap<>();

    public void configure(Map<String, ?> configs) {}

    /**
     * Compute the partition for the given record.
     *
     * @param topic The topic name
     * @param key The key to partition on (or null if no key)
     * @param keyBytes serialized key to partition on (or null if no key)
     * @param value The value to partition on or null
     * @param valueBytes serialized value to partition on or null
     * @param cluster The current cluster metadata
     */
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        if (keyBytes == null) {
            int nextValue = nextValue(topic);
            List<PartitionInfo> availablePartitions = cluster.availablePartitionsForTopic(topic);
            if (availablePartitions.size() > 0) {
                int part = Utils.toPositive(nextValue) % availablePartitions.size();
                return availablePartitions.get(part).partition();
            } else {
                // no partitions are available, give a non-available partition
                return Utils.toPositive(nextValue) % numPartitions;
            }
        } else {
            // hash the keyBytes to choose a partition
            return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
        }
    }

    private int nextValue(String topic) {
        AtomicInteger counter = topicCounterMap.get(topic);
        if (null == counter) {
            counter = new AtomicInteger(ThreadLocalRandom.current().nextInt());
            AtomicInteger currentCounter = topicCounterMap.putIfAbsent(topic, counter);
            if (currentCounter != null) {
                counter = currentCounter;
            }
        }
        return counter.getAndIncrement();
    }

    public void close() {}

}


public final class Utils {

    /**
     * A cheap way to deterministically convert a number to a positive value. When the input is
     * positive, the original value is returned. When the input number is negative, the returned
     * positive value is the original value bit AND against 0x7fffffff which is not its absolutely
     * value.
     *
     * Note: changing this method in the future will possibly cause partition selection not to be
     * compatible with the existing messages already placed on a partition since it is used
     * in producer's {@link org.apache.kafka.clients.producer.internals.DefaultPartitioner}
     *
     * @param number a given number
     * @return a positive number.
     */
    public static int toPositive(int number) {
        // 0x7fffffff = int 最大值 - 1
        return number & 0x7fffffff;
    }

    /**
     * Generates 32 bit murmur2 hash from byte array
     * @param data byte array to hash
     * @return 32 bit hash of the given array
     */
    public static int murmur2(final byte[] data) {
        int length = data.length;
        int seed = 0x9747b28c;
        // 'm' and 'r' are mixing constants generated offline.
        // They're not really 'magic', they just happen to work well.
        final int m = 0x5bd1e995;
        final int r = 24;

        // Initialize the hash to a random value
        int h = seed ^ length;
        int length4 = length / 4;

        for (int i = 0; i < length4; i++) {
            final int i4 = i * 4;
            int k = (data[i4 + 0] & 0xff) + ((data[i4 + 1] & 0xff) << 8) + ((data[i4 + 2] & 0xff) << 16) + ((data[i4 + 3] & 0xff) << 24);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }

        // Handle the last few bytes of the input array
        switch (length % 4) {
            case 3:
                h ^= (data[(length & ~3) + 2] & 0xff) << 16;
            case 2:
                h ^= (data[(length & ~3) + 1] & 0xff) << 8;
            case 1:
                h ^= data[length & ~3] & 0xff;
                h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }
}
```

# 副本

同一个 Partition 可能会有多个 Replication (对应 server.properties 配置中的 default.replication=N). 在没有 Replication 的情况下, 一旦 Broker 宕机, 其上所有 Partition 的数据都不可被消费, 同时 Producer 也不能将数据存于其上的 Partition. 引入 Replication 之后, 同一个 Partition 可能会有多个 Replication, 而这时需要在这些 Replication 之间选出一个 Leader, Producer 和 Consumer 只与这个 Leader 交互, 其他 Replication 作为 Follower 从 Leader 中复制数据.

# 应答机制

![image](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video-%E7%94%9F%E4%BA%A7/Kafka-ACK.png?raw=true)

如上图所示, 有如下步骤:
- a. Producer 连接 Kafka 集群后获取相关 Partition 对应的 Leader 节点.
- b. Producer 将消息发送给该 Leader.
- c. Leader 将消息写入本地 Log.
- d. Followers 从 Leader pull 消息, 写入本地 log 后向 Leader 发送 ACK.
- e. Leader 收到所有 ISR 中的 Replication 的 ACK 后, 增加 HW (High Watermark, 最后 commit 的 offset) 并向 Producer 发送 ACK.

Kafka 生成数据时的应答机制 (ACK) 有如下取值:
- 取值为 `0`: 生产者发送数据后, 不关心数据是否到达 Kafka, 直接发送下一条, 这种方式效率高但数据丢失可能性大.
- 取值为 `1` (默认): 生产者发送数据后, 需要等待 Leader 应答, 如果应答成功, 则发送下一条, 这种方式在 Leader 宕机时但 Follower 还未同步数据的时候会存在数据丢失.
- 取值为 `-1` / `all`: 生产者发送数据后, 需要等待所有副本 (Leader + Follower) 应答, 这种方式最安全但效率最低.

# 生产数据

![image](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video-%E7%94%9F%E4%BA%A7/kafka%E7%94%9F%E4%BA%A7%E6%95%B0%E6%8D%AE.png?raw=true)

如上图, data 将数据发送到双端队列 (参考 [Double-ended queue](https://en.wikipedia.org/wiki/Double-ended_queue)), Sender 从双端队列中取数据 (按数据量和时间批量取数据), 并发送到 Kafka 集群, 如果发送失败, 会将数据放入双端队列尾部 (保证顺序性) 重新发送 (视 ACK 机制而定).

# HW & LEO

- __HW__: High watermark
- __LEO__: Log end offset

参考资料如下:
- [Kafka水位(high watermark)与leader epoch的讨论](https://www.cnblogs.com/huxi2b/p/7453543.html)
- [深入分析Kafka高可用性](https://zhuanlan.zhihu.com/p/46658003)
