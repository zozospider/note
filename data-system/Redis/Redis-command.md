
- out
  - [在shell中，读取redis值方式](https://blog.csdn.net/shuishen520/article/details/89465612)
  - [Redis 教程](https://www.runoob.com/redis/redis-tutorial.html)
  - [《吐血整理》Redis 性能优化的 13 条军规！史上最全](https://juejin.im/post/5e79702af265da570c75580a)

---

# 连接 Redis

```bash
REDIS_HOME/src/redis-cli
REDIS_HOME/src/redis-cli -a password
REDIS_HOME/src/redis-cli -h 127.0.0.1 -p 6379 -a password
```

---

# 通用命令

```bash
# 查询当前数据库版本信息
INFO
# 选择其他数据库 (num = 0-15)
SELECT num
# 清空当前数据库
FLUSHDB
# 清空所有数据库
FLUSHDBALL
# 查询已有的 key
KEYS *
KEYS key*
# 查询某个 key 是否存在
EXISTS key
# 查询某个 key 的类型
TYPE key
# 查询 key 的过期时间 / 有效时间
TTL key
# 删除 key
DEL key
```

---

# string 字符串类型

```bash
# 查询 key 对应的 value
GET key
# 设置 key 对应的 value
SET key value
# 设置多个 key, value
MSET key1 value1 key2 value2
# 查询多个 key 对应的 value
MGET key1 key2 key3
```

---

# hash 类型

```bash
# 查询 key 的所有 kv 对
HGETALL key
# 查询 key 的某个 kv 对
HGET key k
# 查询 key 的所有 k
HKEYS key
# 查询 key 的所有 v
HVALS key
# 查询 key 的 kv 对个数
HLEN key
# 将单个 kv 对设置到 key 中
HSET key k v
# 将多个 kv 对设置到 key 中
HMSET key k1 v1 k2 v2
# 删除 key 中某个 kv 对
HDEL key k
```

---
