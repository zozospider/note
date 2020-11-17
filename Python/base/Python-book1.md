
- [Head First Python（中文版）](https://book.douban.com/subject/10561367/)

# Python Flask 启动 Debug 模式

```bash
export FLASK_DEBUG=1
python3 xxx.py
```

# 第七章 使用数据库

```sql
CREATE DATABASE vsearchlogDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE vsearchlogDB;
CREATE TABLE log (
    id int auto_increment primary key,
    ts timestamp default current_timestamp,
    phrase varchar(128) not null,
    letters varchar(32) not null,
    ip varchar(16) not null,
    browser_string varchar(256) not null,
    results varchar(64) not null
);
DESC log;
```
