
启动 agent 时, 加上监控参数
```
./bin/flume-ng agent -n a1 -c conf -f conf/flume-test.properties -Dflume.root.logger=INFO,DAILY -Dflume.log.dir=/work/app/flume/logs -Dflume.log.file=flume-test.log -DappFlag=flume-test -Dflume.monitoring.type=http -Dflume.monitoring.port=1100 &
```

查看监控数据, 通过 `curl http://127.0.0.1:1100/metrics` 得到以下数据:
```json
{
  "SOURCE.s1": {
    "StopTime": "0",                       // source 组件停止时间
    "AppendBatchReceivedCount": "0",       // source 端刚刚追加的批量的数量, 比如一批 100, 该度量为 2, 就是 source 端收到了 200 个events
    "EventReceivedCount": "4913189",       // source 端累计收到的 event 数量
    "AppendBatchAcceptedCount": "0",       // source 端追加到 channel 的数量
    "Type": "SOURCE",
    "EventAcceptedCount": "4912519",       // source 端累计成功放入 channel 的 event 数量
    "AppendReceivedCount": "0",            // source 端刚刚追加的且目前已收到的 event 数量
    "StartTime": "1496839492821",          // source 组件启动的时间
    "AppendAcceptedCount": "0",            // source 端刚刚追加放入 channel 的 event 数量
    "OpenConnectionCount": "0"             // 当前有效的连接数
  },
  "CHANNEL.c1": {
    "StopTime": "0",                       // channel 组件停止时间
    "EventPutSuccessCount": "4912519",     // 成功放入 channel 的 event 数量
    "ChannelCapacity": "200000",           // channel 容量
    "ChannelFillPercentage": "5.0E-4",     // channel 使用比例
    "Type": "CHANNEL",
    "ChannelSize": "1",                    // 目前在 channel 中的event数量
    "EventTakeSuccessCount": "4912589",    // 从 channel 中成功取出 event 的数量
    "EventTakeAttemptCount": "4912661",    // 正在从 channel 中取 event 的数量
    "StartTime": "1496839492318",          // channel 组件启动时间
    "EventPutAttemptCount": "4912519"      // 正在放进 channel 的 event 数量
  },
  "SINK.k1": {
    "StopTime": "0",                       // sink 组件停止时间
    "KafkaEventSendTimer": "403088",       // 从 channel 批量取 event, 并成功发送到 kafka 的耗时, 单位: 毫微秒
    "EventDrainSuccessCount": "4912589",   // sink 成功发送出的 event 数量
    "RollbackCount": "0",                  // 失败回滚的 event 数量
    "Type": "SINK",
    "ConnectionCreatedCount": "0",         // 连接被创建的数量
    "BatchCompleteCount": "0",             // 成功完成输出的批量事件个数
    "BatchEmptyCount": "62",               // 批量取空的数量
    "EventDrainAttemptCount": "0",         // 试图从 channel 消耗的事件数量
    "StartTime": "1496839492829",          // sink 组件开始时间
    "BatchUnderflowCount": "8",            // 正处于批量处理的 batch 数
    "ConnectionFailedCount": "0",          // 连接失败数
    "ConnectionClosedCount": "0"           // 连接关闭数
  }
}
```
