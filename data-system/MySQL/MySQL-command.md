
# MySQL 语法

## 创建数据库
```sql
-- UTF8
mysql> CREATE DATABASE db_1 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
-- GBK
mysql> CREATE DATABASE db_1 DEFAULT CHARACTER SET gbk COLLATE gbk_chinese_ci;
```

## 更改字段类型
```sql
ALTER TABLE tbl_1 MODIFY COLUMN col_1 LONGTEXT;
```

---

# 导入导出

## 导出表结构和数据

注: 5.6 版本以上会加上 gid (用此命令生成的 SQL 导入到新库需要高权限)

```bash
# database
mysqldump -uterrace -p123456 -h192.168.0.1 -P3306 db_1 > db_1_20190218_all.sql
mysqldump -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 db_1 > db_1_20190218_all.sql
mysqldump -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off db_1 > db_1_20190218_all.sql

# database table
mysqldump -uterrace -p123456 -h192.168.0.1 -P3306 db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_all.sql
mysqldump -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_all.sql
mysqldump -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_all.sql
```

## 只导出表结构，不导出数据
```bash
# database
mysqldump -d -uterrace -p123456 -h192.168.0.1 -P3306 db_1 > db_1_20190218_structure.sql
mysqldump -d -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 db_1 > db_1_20190218_structure.sql
mysqldump -d -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off db_1 > db_1_20190218_structure.sql

# database table
mysqldump -d -uterrace -p123456 -h192.168.0.1 -P3306 db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_structure.sql
mysqldump -d -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_structure.sql
mysqldump -d -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_structure.sql
```

## 只导出数据，不导出表结构
```bash
# database
mysqldump -d -uterrace -p123456 -h192.168.0.1 -P3306 db_1 > db_1_20190218_data.sql
mysqldump -d -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 db_1 > db_1_20190218_data.sql
mysqldump -d -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off db_1 > db_1_20190218_data.sql

# database table
mysqldump -t -uterrace -p123456 -h192.168.0.1 -P3306 db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_data.sql
mysqldump -t -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_data.sql
mysqldump -t -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_data.sql
```

## 导入表结构 / 数据
```
mysql -uterrace -p123456 -h192.168.0.1 -P3306 db_1 < db_1_20190218_all.sql
mysql -uterrace -p123456 -h192.168.0.1 -P3306 db_1 < db_1_20190218_structure.sql
mysql -uterrace -p123456 -h192.168.0.1 -P3306 db_1 < db_1_20190218_data.sql
```

---

# 其他

## 查看编码
```sql
mysql> show variables like '%char%';
```

## 查看 MySQL 版本
```sql
mysql> select version();
mysql> status;
```

## 命令行分页显示结果
```sql
mysql>pager more;
mysql>show tables;
mysql>show databases;
```

---
