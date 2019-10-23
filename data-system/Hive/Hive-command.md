
# 分区

```sql
-- 查询所有分区
SHOW PARTITIONS tbl;

-- 条件查询分区
-- SHOW PARTITIONS tbl PARTITION(partition_desc);
SHOW PARTITIONS tbl PARTITION(pdate=20191023);
```
