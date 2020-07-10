
- 查找某个文件某个字段是否等于某个值, 打印整行内容:
```bash
# 等于
awk -F '|' '$10 == "value" {print $0}' file
# 不等于
awk -F '|' '$10 != "value" {print $0}' file
awk -F '|' '$10 == "value" {print $0}' file >> result_file
cat file | awk -F '|' '$10 == "value" {print $0}'
cat file | awk -F '|' '$10 == "value" {print $0}' >> result_file
# 条件
cat file | awk -F '|' '{if ($10 > 300) {print $0}}' >> result_file
cat file | awk -F '|' '{if ($10 > 300 && $10 < 500) {print $0}}' >> result_file
# 字符串模糊匹配
cat file | awk -F '|' '{if ($10 ~ /word/) {print $0}}' >> result_file
## 反向
cat file | awk -F '|' '{if ($10 !~ /word/) {print $0}}' >> result_file
cat file | awk -F '|' '{if (!($10 !~ /word/)) {print $0}}' >> result_file
## 大小写
cat file | awk -F '|' '{if ($10 ~ /[zZ]word/) {print $0}}' >> result_file
## 多条件
cat file | awk -F '|' '{if ($10 ~ /[zZ]word/ OR $10 ~ /[zZ]another/) {print $0}}' >> result_file
cat file | awk -F '|' '{if ($10 ~ /[zZ]word/ OR $10 == "value") {print $0}}' >> result_file
### 反向多条件
cat file | awk -F '|' '{if (!($10 ~ /[zZ]word/ OR $10 == "value")) {print $0}}' >> result_file

```

- 查找某个文件某个字段总和, 打印总和:
```bash
awk -F '|' '{sum += $5};END {print sum}' file
```

- 查找某个文件某个字段, 去重打印
```bash
cat file | awk -F '|' '{print $6}' | sort | uniq >> result_file
```

- 查找某个文件字段数少于某个值的行
```bash
cat file | awk -F '|' 'NF<6' | more
```
