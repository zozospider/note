
# 分区

```sql
-- 查询所有分区
SHOW PARTITIONS tbl;

-- 条件查询分区
-- SHOW PARTITIONS tbl PARTITION(partition_desc);
SHOW PARTITIONS tbl_1 PARTITION(pdate='20191023');

-- 查看该分区的详细信息
DESC FORMATTED tbl_1 PARTITION(month='2015-01', day='2015-01-25');

-- 添加分区
ALTER TABLE tbl_1 ADD PARTITION(pdate='20191023', ptype='t1') LOCATION '/db_1/tbl_1/20191023/t1';
```

# SQL

```sql
-- 如果为 NULL 则转换
-- wow
SELECT NVL(NULL, 'wow');

-- 切割字符串
-- c
SELECT SPLIT('a&b&c', '&')[2];

```

# 导出数据到本地

```
hive -e "select * from db_1.tbl_1 where pdate=20200101";" > TBL_20200101.txt
```
