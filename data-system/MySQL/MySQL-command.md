

# MySQL 语法

## 创建数据库
```sql
-- UTF-8 utf8
CREATE DATABASE db_1 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
-- UTF-8 utf8mb4 (MySQL 新版默认字符集, 推荐)
CREATE DATABASE db_1 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
-- GBK
CREATE DATABASE db_1 DEFAULT CHARACTER SET gbk COLLATE gbk_chinese_ci;
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
mysqldump -uzozo -p123456 -h127.0.0.1 -P3306 db_1 > db_1_20190218_all.sql
# (设置编码)
mysqldump -uzozo -p123456 -h127.0.0.1 -P3306 --default-character-set=utf8 db_1 > db_1_20190218_all.sql
# (不需要 gid)
mysqldump -uzozo -p123456 -h127.0.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off db_1 > db_1_20190218_all.sql

# database table
mysqldump -uzozo -p123456 -h127.0.0.1 -P3306 db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_all.sql
mysqldump -uzozo -p123456 -h127.0.0.1 -P3306 --default-character-set=utf8 db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_all.sql
mysqldump -uzozo -p123456 -h127.0.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_all.sql
```

## 只导出表结构，不导出数据
```bash
# database
mysqldump -d -uzozo -p123456 -h127.0.0.1 -P3306 db_1 > db_1_20190218_structure.sql
mysqldump -d -uzozo -p123456 -h127.0.0.1 -P3306 --default-character-set=utf8 db_1 > db_1_20190218_structure.sql
mysqldump -d -uzozo -p123456 -h127.0.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off db_1 > db_1_20190218_structure.sql

# database table
mysqldump -d -uzozo -p123456 -h127.0.0.1 -P3306 db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_structure.sql
mysqldump -d -uzozo -p123456 -h127.0.0.1 -P3306 --default-character-set=utf8 db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_structure.sql
mysqldump -d -uzozo -p123456 -h127.0.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_structure.sql
```

## 只导出数据，不导出表结构
```bash
# database
mysqldump -t -uzozo -p123456 -h127.0.0.1 -P3306 db_1 > db_1_20190218_data.sql
mysqldump -t -uzozo -p123456 -h127.0.0.1 -P3306 --default-character-set=utf8 db_1 > db_1_20190218_data.sql
mysqldump -t -uzozo -p123456 -h127.0.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off db_1 > db_1_20190218_data.sql

# database table
mysqldump -t -uzozo -p123456 -h127.0.0.1 -P3306 db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_data.sql
mysqldump -t -uzozo -p123456 -h127.0.0.1 -P3306 --default-character-set=utf8 db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_data.sql
mysqldump -t -uzozo -p123456 -h127.0.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off db_1 tbl_1 tbl_2 tbl_3 > db_1_tbl_123_20190218_data.sql
```

## 导入表结构 / 数据
```
mysql -uzozo -p123456 -h127.0.0.1 -P3306 db_1 < db_1_20190218_all.sql
mysql -uzozo -p123456 -h127.0.0.1 -P3306 db_1 < db_1_20190218_structure.sql
mysql -uzozo -p123456 -h127.0.0.1 -P3306 db_1 < db_1_20190218_data.sql
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

## 查找表
```sql
select table_schema, table_name from information_schema.tables where table_schema='db_1' and table_name like 'tbl_%';
```

---

# 修改字符集

```sql
-- 查看数据库编码
SHOW variables LIKE '%char%';
SHOW CREATE DATABASE db_name;

-- 查看表编码
SHOW CREATE TABLE tbl_1;

-- 查看字段编码
SHOW FULL COLUMNS FROM tbl_1;
```

```sql
-- 修改数据库字符集
ALTER DATABASE db_1 DEFAULT CHARACTER SET character_name [COLLATE ...];
ALTER DATABASE db_1 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER DATABASE db_1 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 只是修改表的默认字符集
ALTER TABLE tbl_1 DEFAULT CHARACTER SET character_name [COLLATE...];
ALTER TABLE tbl_1 CDEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE tbl_1 CDEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

--- 把表默认的字符集和所有字符列 (CHAR, VARCHAR, TEXT) 改为新的字符集
ALTER TABLE tbl_1 CONVERT TO CHARACTER SET character_name [COLLATE ...]
ALTER TABLE tbl_1 CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE tbl_1 CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 修改字段的字符集 1
ALTER TABLE tbl_name CHANGE c_name c_name CHARACTER SET character_name [COLLATE ...];
ALTER TABLE tbl_1 CHANGE title title VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE tbl_1 CHANGE title title VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 修改字段的字符集 2
ALTER TABLE <表名> MODIFY COLUMN <字段名> <字段类型> CHARACTER SET character_name [COLLATE ...];
ALTER TABLE tbl_1 MODIFY COLUMN column_1 varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE tbl_1 MODIFY COLUMN column_1 varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---
