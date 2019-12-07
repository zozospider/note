
- [Document & Code](#document--code)
- [一. 零拷贝](#一-零拷贝)
- [二. 预读 & 后写](#二-预读--后写)
- [三. 分区](#三-分区)
    - [3.1 分区的原因](#31-分区的原因)
    - [3.2 文件存储](#32-文件存储)
    - [3.3 分区原则](#33-分区原则)
- [四. 副本](#四-副本)
- [五. 应答机制](#五-应答机制)
- [六. 生产数据](#六-生产数据)
- [七. HW & LEO](#七-hw--leo)
- [八. 消费数据](#八-消费数据)

---

# Document & Code

- page
  - [../Kafka-video](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video.md)

- link
  - [通过零拷贝实现有效数据传输](https://www.ibm.com/developerworks/cn/java/j-zerocopy/)
  - [零拷贝底层实现原理](https://juejin.im/entry/59b740fdf265da06633d02cf)
  - [浅析Linux中的零拷贝技术](https://www.jianshu.com/p/fad3339e3448)
  - [Linux 中的零拷贝技术，第 1 部分](https://www.ibm.com/developerworks/cn/linux/l-cn-zerocopy1/index.html)
  - [Linux 中直接 I/O 机制的介绍](https://www.ibm.com/developerworks/cn/linux/l-cn-directio/)
  - [Kafka相关内容总结（存储和性能）](https://www.w3xue.com/exp/article/20191/16157.html)
  - [Double-ended queue](https://en.wikipedia.org/wiki/Double-ended_queue)
  - [Kafka水位(high watermark)与leader epoch的讨论](https://www.cnblogs.com/huxi2b/p/7453543.html)
  - [深入分析Kafka高可用性](https://zhuanlan.zhihu.com/p/46658003)

---

Kafka 高吞吐原因: 零拷贝, 顺写, 分段 + 索引, 预读, 后写

# 一. 零拷贝

![image](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video-%E7%94%9F%E4%BA%A7%E5%92%8C%E6%B6%88%E8%B4%B9/Kafka%E9%9B%B6%E6%8B%B7%E8%B4%9D.png?raw=true)

零拷贝参考如下资料:
- [通过零拷贝实现有效数据传输](https://www.ibm.com/developerworks/cn/java/j-zerocopy/)
- [零拷贝底层实现原理](https://juejin.im/entry/59b740fdf265da06633d02cf)
- [浅析Linux中的零拷贝技术](https://www.jianshu.com/p/fad3339e3448)
- [Linux 中的零拷贝技术，第 1 部分](https://www.ibm.com/developerworks/cn/linux/l-cn-zerocopy1/index.html)
- [Linux 中直接 I/O 机制的介绍](https://www.ibm.com/developerworks/cn/linux/l-cn-directio/)

# 二. 预读 & 后写

参考如下资料:
- [Kafka相关内容总结（存储和性能）](https://www.w3xue.com/exp/article/20191/16157.html)

- 预读: 读取某一个内容的时候, 把附近的内容也读出来. (因为读取某个内容时, 大部分情况下也会读取附近的内容, 如查看朋友圈预先加载后面几条, 搜索引擎预先加载前几页内容等)
- 后写: 

# 三. 分区

消息发送时都被发送到一个 Topic, 其本质就是一个目录, 而 Topic 是由一些 Partition Logs (分区日志) 组成, 其组织结构如下图:

![image](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video-%E7%94%9F%E4%BA%A7%E5%92%8C%E6%B6%88%E8%B4%B9/log_anatomy.png?raw=true)

每个 Partition 中的消息都是有序的, 生产的消息被不断追加到 Partition log 上, 其中的每一个消息都被赋予了一个唯一的 offset 值.

## 3.1 分区的原因

- 方便在集群中扩展, 每个 Partition 可以通过调整以适应它所在的机器, 而一个 Topic 又可以有多个 Partition, 因此整个集群就可以适应任意大小的数据了.

- 可以提高并发, 因为可以以 Partition 为单位读写.

## 3.2 文件存储 (分段日志 + 索引)

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

无论消息是否被消费, Kafka 都会保留所有消息, 有两种策略删除旧数据:
- 基于时间: log.retention.hours=168
- 基于大小: log.retention.bytes=1073741824

需要注意, 因为 Kafka 读取特定消息的时间复杂度为 O(1), 即与文件大小无关, 所以删除过期文件与提高 Kafka 性能无关.

## 3.3 分区原则

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

# 四. 副本

同一个 Partition 可能会有多个 Replication (对应 server.properties 配置中的 default.replication=N). 在没有 Replication 的情况下, 一旦 Broker 宕机, 其上所有 Partition 的数据都不可被消费, 同时 Producer 也不能将数据存于其上的 Partition. 引入 Replication 之后, 同一个 Partition 可能会有多个 Replication, 而这时需要在这些 Replication 之间选出一个 Leader, Producer 和 Consumer 只与这个 Leader 交互, 其他 Replication 作为 Follower 从 Leader 中复制数据.

# 五. 应答机制

![image](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video-%E7%94%9F%E4%BA%A7%E5%92%8C%E6%B6%88%E8%B4%B9/Kafka-ACK.png?raw=true)

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

# 六. 生产数据

![image](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video-%E7%94%9F%E4%BA%A7%E5%92%8C%E6%B6%88%E8%B4%B9/kafka%E7%94%9F%E4%BA%A7%E6%95%B0%E6%8D%AE.png?raw=true)

如上图, data 将数据发送到双端队列 (参考 [Double-ended queue](https://en.wikipedia.org/wiki/Double-ended_queue)), Sender 从双端队列中取数据 (按数据量和时间批量取数据), 并发送到 Kafka 集群, 如果发送失败, 会将数据放入双端队列尾部 (保证顺序性) 重新发送 (视 ACK 机制而定).

# 七. HW & LEO

- __HW__: High watermark
- __LEO__: Log end offset

参考如下资料:
- [Kafka水位(high watermark)与leader epoch的讨论](https://www.cnblogs.com/huxi2b/p/7453543.html)
- [深入分析Kafka高可用性](https://zhuanlan.zhihu.com/p/46658003)

# 八. 消费数据

![image](https://github.com/zozospider/note/blob/master/stream/Kafka/Kafka-video-%E7%94%9F%E4%BA%A7%E5%92%8C%E6%B6%88%E8%B4%B9/Kafka%E6%B6%88%E8%B4%B9%E6%95%B0%E6%8D%AE.png?raw=true)

如上图所示, Broker 或 Consumer 发生变化时, 可能会触发再平衡 (右下图, 左下图), 再平衡遵守如下规则:
- 一个分区只能被同一个消费者组中的一个消费者消费 / 同一个消费者组中的多个消费者不能消费一个分区, 否则会出现重复消费且无法保证顺序 (右上图).
- 同一个消费者组中的一个消费者可以消费多个分区 (左下图).

# 九. ZooKeeper 存储结构

参考如下资料:
- [apache kafka系列之在zookeeper中存储结构](https://blog.csdn.net/lizhitao/article/details/23744675)
- [跟我学Kafka之zookeeper的存储结构](https://www.jianshu.com/p/3e9cedc8ed03)
- [kafka自学之路--zookeeper中存储结构](https://cloud.tencent.com/developer/article/1018468)

```
[zozo@VM_0_6_centos bin]$ ./zkCli.sh
Connecting to localhost:2181
...
[zk: localhost:2181(CONNECTED) 0] ls /
[cluster, controller_epoch, controller, brokers, zookeeper, admin, isr_change_notification, consumers, log_dir_event_notification, latest_producer_id_block, config]
[zk: localhost:2181(CONNECTED) 1] ls /cluster
[id]
[zk: localhost:2181(CONNECTED) 2] ls /cluster/id
[]
[zk: localhost:2181(CONNECTED) 3] get /cluster/id
{"version":"1","id":"HApw8pLOT3-qhrqtyo5oFQ"}
cZxid = 0x100000018
ctime = Tue Feb 19 21:28:44 CST 2019
mZxid = 0x100000018
mtime = Tue Feb 19 21:28:44 CST 2019
pZxid = 0x100000018
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 45
numChildren = 0
```

如上所示, Kafka 会在 ZooKeeper 上建立多个目录存储元数据. `/cluster/id` 表示当前集群的 ID 为 `HApw8pLOT3-qhrqtyo5oFQ`.

```
[zk: localhost:2181(CONNECTED) 4] ls /controller
[]
[zk: localhost:2181(CONNECTED) 5] get /controller
{"version":1,"brokerid":3,"timestamp":"1550858501807"}
cZxid = 0x10000018f
ctime = Sat Feb 23 02:01:41 CST 2019
mZxid = 0x10000018f
mtime = Sat Feb 23 02:01:41 CST 2019
pZxid = 0x10000018f
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x301a7caf7e70002
dataLength = 54
numChildren = 0
```

如上所示, `/controller` 表示集群控制器, 其值指定了由第 `3` 台机器来控制集群的行为, 如分区处理, 再平衡等.

```
[zk: localhost:2181(CONNECTED) 6] ls /controller_epoch
[]
[zk: localhost:2181(CONNECTED) 7] get /controller_epoch
2
cZxid = 0x10000001a
ctime = Tue Feb 19 21:28:45 CST 2019
mZxid = 0x10000018f
mtime = Sat Feb 23 02:01:41 CST 2019
pZxid = 0x10000001a
cversion = 0
dataVersion = 2
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 1
numChildren = 0
```

如上所示, `/controller_epoch` 表示集群累计进行了 `2` 次 Leader 竞选.

```
[zk: localhost:2181(CONNECTED) 8] ls /brokers
[ids, topics, seqid]
[zk: localhost:2181(CONNECTED) 9] ls /brokers/ids
[1, 2, 3]
[zk: localhost:2181(CONNECTED) 10] ls /brokers/ids/1
[]
[zk: localhost:2181(CONNECTED) 11] get /brokers/ids/1
{"listener_security_protocol_map":{"PLAINTEXT":"PLAINTEXT"},"endpoints":["PLAINTEXT://172.16.0.6:9092"],"jmx_port":-1,"host":"172.16.0.6","timestamp":"1550858517088","port":9092,"version":4}
cZxid = 0x100000195
ctime = Sat Feb 23 02:01:57 CST 2019
mZxid = 0x100000195
mtime = Sat Feb 23 02:01:57 CST 2019
pZxid = 0x100000195
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x106c91921af001a
dataLength = 190
numChildren = 0
```

如上所示, `/brokers/ids` 表示集群一共有 id 为 `1`, `2`, `3` 的三个 broker, 其中, `/brokers/ids/1` 记录了第 `1` 个 broker 的信息.

```
[zk: localhost:2181(CONNECTED) 12] ls /brokers
[ids, topics, seqid]
[zk: localhost:2181(CONNECTED) 13] ls /brokers/topics
[test, first, __consumer_offsets, second]
[zk: localhost:2181(CONNECTED) 14] ls /brokers/topics/first
[partitions]
[zk: localhost:2181(CONNECTED) 15] get /brokers/topics/first
{"version":1,"partitions":{"2":[2,3],"1":[1,2],"0":[3,1]}}
cZxid = 0x100000092
ctime = Wed Feb 20 20:52:42 CST 2019
mZxid = 0x100000092
mtime = Wed Feb 20 20:52:42 CST 2019
pZxid = 0x100000094
cversion = 1
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 58
numChildren = 1
[zk: localhost:2181(CONNECTED) 16] ls /brokers/topics/first/partitions
[0, 1, 2]
[zk: localhost:2181(CONNECTED) 17] ls /brokers/topics/first/partitions/1
[state]
[zk: localhost:2181(CONNECTED) 18] ls /brokers/topics/first/partitions/1/state
[]
[zk: localhost:2181(CONNECTED) 19] get /brokers/topics/first/partitions/1/state
{"controller_epoch":2,"leader":2,"version":1,"leader_epoch":2,"isr":[2,1]}
cZxid = 0x100000099
ctime = Wed Feb 20 20:52:42 CST 2019
mZxid = 0x1000001a8
mtime = Sat Feb 23 02:01:58 CST 2019
pZxid = 0x100000099
cversion = 0
dataVersion = 3
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 74
numChildren = 0
[zk: localhost:2181(CONNECTED) 20] ls /brokers/topics/__consumer_offsets/partitions
[44, 45, 46, 47, 48, 49, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43]
```

如上所示, `/brokers/topics` 记录了当前集群有 `test`, `first`, `__consumer_offsets`, `second` 这 4 个 topic, 其中 `/brokers/topics/first/partitions` 表示名为 `first` 的 partition 有 `0`, `1`, `2` 这 3 个分区. `/brokers/topics/__consumer_offsets/partitions` 表示名为 `__consumer_offsets` 的 partition 有 50 个分区.

```
[zk: localhost:2181(CONNECTED) 23] ls /consumers
[]
```

如上所示, 当消费者连接 ZooKeeper 进行消费时 (新版本已废除), `/consumers` 记录了消费者组和消费者的元数据信息.

查看消费情况请参考 `bin/kafka-consumer-groups.sh` 脚本使用说明: [Kafka-Documentation](http://kafka.apache.org/documentation/#basic_ops_consumer_lag).

