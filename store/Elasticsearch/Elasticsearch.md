
# 最佳实践

## 集群规划

[阿里云 Elasticsearch 规格容量评估](https://help.aliyun.com/document_detail/72660.html)

# 升级

- [Upgrade Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/setup-upgrade.html)
- [upgrading logstash](https://www.elastic.co/guide/en/logstash/current/upgrading-logstash.html)

__滚动升级__ 步骤:

1. Logstash
- 1.1. 停止 Logstash 6.5.4.
- 1.2. 如果为 persistent queue, 在 `logstash.yml` 文件配置 `queue.drain: true`.
- 1.3. 重启 Logstash 6.5.4 使配置生效.
- 1.4. 停止 Logstash 6.5.4, 并等待 queue 变空.
- 1.5. 部署 Logstash 6.7.0, 拷贝 Logstash 6.5.4 配置到 6.7.0.

2. Elasticsearch
- 2.1. 禁用分配.
```
PUT _cluster/settings
{
  "persistent": {
    "cluster.routing.allocation.enable": "none"
  }
}
```
- 2.2. 停止一个节点上的 Elasticsearch 6.5.4.
- 2.3. 在该节点上部署 Elasticsearch 6.7.0, 拷贝 Elasticsearch 6.5.4 配置到 6.7.0 (包括 data 目录).
- 2.4. 启动该节点上的 Elasticsearch 6.7.0
- 2.5. 启用分配.
```
PUT _cluster/settings
{
  "persistent": {
    "cluster.routing.allocation.enable": null
  }
}
```
- 2.6. 检查状态, 等待状态变为 `green`.
- 2.7. 重复 2.1 ~ 2.6 步骤完成其他节点升级.

3. 启动 Logstash 6.7.0.

4. 重新部署 Kibana 6.7.0.
