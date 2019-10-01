


---

# 一. NameNode 和 SecondaryNameNode 工作机制

NameNode 的元数据是如何存储的?

- 如果元数据存储在 NameNode 节点的磁盘上, 因为经常需要随机访问磁盘, 效率过低. 因此需要存储在内存中.
- 如果只存在内存中, 会有元数据丢失的风险. 因此产生磁盘备份 Fsimage.
- 如果内存中的元数据更新时, 同时更新 Fsimage, 就会导致效率过低, 如果不更新, 就会有一致性问题. 因此引入 Edits (只追加, 效率高), 每当有元数据需要更新时, 将更新操作追加到 Edits 中并更新内存. 这样就可以通过合并 Fsimage 和 Edits 还原完整的元数据.
- 如果长时间添加数据到 Edits 中会导致数据过大, 恢复时间过长. 因此需要定期合并 Fsimage 和 Edits.
- 如果定期合并 Fsimage 和 Edits 由 NameNode 节点完成, 效率过低. 因此引入 SecondaryNameNode 用于合并 Fsimage 和 Edits.

![image](https://github.com/zozospider/note/blob/master/data-system/Hadoop/Hadoop-video1-NameNode%E5%92%8CSecondaryNameNode/NameNode%E5%B7%A5%E4%BD%9C%E6%9C%BA%E5%88%B6.png?raw=true)

---

# 二. Fsimage 和 Edis 解析

---

# 三. CheckPoint 时间设置

---

# 四. NameNode 故障处理

---

# 五. 集群安全模式

---

# 六. NameNode 多目录设置

---
