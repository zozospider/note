
- 查找某个文件某个字段是否等于某个值, 打印整行内容:
```bash
awk -F '|' '$10 == "value" {print $0}' file
awk -F '|' '$10 == "value" {print $0}' file >> result_file
cat file | awk -F '|' '$10 == "value" {print $0}'
cat file | awk -F '|' '$10 == "value" {print $0}' >> result_file
```

- 查找某个文件某个字段总和, 打印总和:
```bash
awk -F '|' '{sum += $5};END {print sum}' file
```

- 查找某个文件某个字段, 去重打印
```bash
cat file | awk -F '|' '{print $6}' | sort | uniq >> result_file
```
