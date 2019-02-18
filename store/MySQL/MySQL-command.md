
# 创建数据库
```
create database game_manager default character set utf8 collate utf8_general_ci;
```

# 查看编码
```
mysql> show variables like'%char%';
```

# 导入导出

## 导出表结构和数据

5.6以上会加上gid（用此命令声场的sql导入到新库需要高权限）
```
mysqldump -uterrace -p123456 -h192.168.0.1 -P3306 game_manager > game_manager_20180607_all.sql
```

不需要gid
```
mysqldump -uterrace -p123456 -h192.168.0.1 -P3306 --set-gtid-purged=off game_manager > game_manager_20180607_all.sql
```

## 只导出表结构，不导出数据
```
mysqldump -d -uterrace -p123456 -h192.168.0.1 -P3306 game_manager > game_manager_20180607_table.sql
mysqldump -d -uterrace -p123456 -h192.168.0.1 -P3306 --set-gtid-purged=off game_manager > game_manager_20180607_table.sql
```

## 导出数据，不导出表结构
```
mysqldump -t -uterrace -p123456 -h192.168.0.1 -P3306 game_manager web_menu web_user web_role > game_manager_20180607_data.sql
mysqldump -t -uterrace -p123456 -h192.168.0.1 -P3306 --set-gtid-purged=off game_manager web_menu web_user web_role > game_manager_20180607_data.sql
```

## 导入表结构 / 数据
```
mysql -uterrace -pTz#666 -h192.168.0.1 -P3306 game_manager < game_manager_20180607_all.sql
mysql -uterrace -pTz#666 -h192.168.0.1 -P3306 game_manager < game_manager_20180607_table.sql
mysql -uterrace -pTz#666 -h192.168.0.1 -P3306 game_manager < game_manager_20180607_data.sql
```
