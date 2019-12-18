
# Flume KafkaSink & KafkaSource 记录

- 环境:
  - Flume (KafkaSink) -> Kafka -> Flume (KafkaSource)

- 测试:
  - Flume (KafkaSink) 默认创建的 topic 为 1 个 partition, 1 个 replication (推荐手动创建 topic, 如 3 个 partition, 2 个 replication)
  - Flume (KafkaSource) 增加到 1 个 (flumeA) 时, 所有 partition 都由它消费: flumeA -> p0 & p1 & p2.
  - Flume (KafkaSource) 增加到 2 个 (flumeA, flumeB) 时, 会重写平衡消费者组: flumeA -> p0 & p1; flumeB -> p2.
  - Flume (KafkaSource) 增加到 3 个 (flumeA, flumeB, flumeC) 时, 会重新平衡消费者组: flumeA -> p1; flumeB -> p2; flumeC -> p0.
  - Flume (KafkaSource) 增加到 4 个 (flumeA, flumeB, flumeC, flumeD) 时, 会重新平衡消费者组: flumeA -> p1; flumeB -> p2; flumeC -> p0; flumeD 不消费.
  - Flume (KafkaSource) 减少到 3 个 (flumeA, , flumeC, flumeD) 时, 会重新平衡消费者组: flumeA -> p1; flumeB 挂了; flumeC -> p0; flumeD -> p2.
  - Flume (KafkaSource) 减少到 2 个 (flumeA, , flumeC, ) 时, 会重新平衡消费者组: flumeA -> p2; flumeB 挂了; flumeC -> p1 & p0; flumeD 挂了.

- 结论:
  - 1 个消费者可以消费多个分区, 1 个分区最多被 1 个消费者消费.
  - 同一个组的消费者 新增/减少 时, 都会重新平衡消费者组.

# Flume KafkaChannel 记录

- 环境:
  - Flume (KafkaChannel)

- 测试:
  - Flume (KafkaChannel) 默认创建的 topic 为 1 个 partition, 1 个 replication.
    - Sink 为 1 个时, 可正常消费, k1 -> p0.
    - Sink 为 2 个时, 不能正常消费.
  - 手动创建 KafkaChannel 对应的 topic, 如: 3 个 partition, 2 个 replication.
    - Sink 为 1 个时, k1 -> p0 & p1 & p2.
    - Sink 为 2 个时, k1 -> p2; k2 -> p0 & p1.
    - Sink 为 3 个时, 每个 sink 消费 1 个 partition.
    - Sink 为 4 个时, k1, k2, k3 消费 3 个 partition; k2 不消费.

- 结论:
  - 多个 sink 不能消费只有 1 个分区的 KafkaChannel.
