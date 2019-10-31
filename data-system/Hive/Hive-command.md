
# 分区

```sql
-- 查询所有分区
SHOW PARTITIONS tbl;

-- 条件查询分区
-- SHOW PARTITIONS tbl PARTITION(partition_desc);
SHOW PARTITIONS tbl PARTITION(pdate=20191023);

-- 查看该分区的详细信息
DESC FORMATTED tbl PARTITION(month='2015-01', day='2015-01-25');
```
