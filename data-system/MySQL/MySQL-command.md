
# 创建数据库
```sql
-- UTF8
mysql> CREATE DATABASE game_manager DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
-- GBK
mysql> CREATE DATABASE game_manager DEFAULT CHARACTER SET gbk COLLATE gbk_chinese_ci;
```

# 查看状态

## 查看编码
```sql
mysql> show variables like '%char%';
```

## 查看 MySQL 版本
```sql
mysql> select version();
mysql> status;
```

# 导入导出

## 导出表结构和数据

5.6 版本以上会加上 gid (用此命令生成的 SQL 导入到新库需要高权限)
```
mysqldump -uterrace -p123456 -h192.168.0.1 -P3306 game_manager > game_manager_20190218_all.sql
```

设置编码
```
mysqldump -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 game_manager > game_manager_20190218_all.sql
```

不需要 gid
```
mysqldump -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off game_manager > game_manager_20190218_all.sql
```

## 只导出表结构，不导出数据
```
mysqldump -d -uterrace -p123456 -h192.168.0.1 -P3306 game_manager > game_manager_20190218_table.sql
mysqldump -d -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 game_manager > game_manager_20190218_table.sql
mysqldump -d -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off game_manager > game_manager_20190218_table.sql
```

## 导出数据，不导出表结构
```
mysqldump -t -uterrace -p123456 -h192.168.0.1 -P3306 game_manager web_menu web_user web_role > game_manager_20190218_data.sql
mysqldump -t -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 game_manager web_menu web_user web_role > game_manager_20190218_data.sql
mysqldump -t -uterrace -p123456 -h192.168.0.1 -P3306 --default-character-set=utf8 --set-gtid-purged=off game_manager web_menu web_user web_role > game_manager_20190218_data.sql
```

## 导入表结构 / 数据
```
mysql -uterrace -pTz#666 -h192.168.0.1 -P3306 game_manager < game_manager_20190218_all.sql
mysql -uterrace -pTz#666 -h192.168.0.1 -P3306 game_manager < game_manager_20190218_table.sql
mysql -uterrace -pTz#666 -h192.168.0.1 -P3306 game_manager < game_manager_20190218_data.sql
```

# 命令行分页显示结果
```
mysql>pager more;
mysql>show tables;
mysql>show databases;
```

# 更改字段类型
```sql
ALTER TABLE tbl MODIFY COLUMN col LONGTEXT;
```

