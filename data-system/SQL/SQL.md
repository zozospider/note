
# 函数

```sql
-- 连接多个字符串
CONCAT(str1, str2, str3 ...)
-- a&b&c
SELECT CONCAT('a', '&', 'b', '&', 'c');

-- 截取字符串
SUBSTRING_INDEX(str, delim, count)
-- 从左到右的第 2 个 & 前的字符串
-- a&b
SELECT SUBSTRING_INDEX('a&b&c', '&', 2);
-- 从右到左的第 2 个 & 后的字符串
-- b&c
SELECT SUBSTRING_INDEX('a&b&c', '&', -2);

-- 替换
REPLACE(str, from_str, to_str);
-- 将 a&b&c 替换成 a, b, c
-- a, b, c
SELECT REPLACE('a&b&c', '&', ', ');
```

