
- 查找某个文件某个字段是否等于某个值, 打印整行内容:
```bash
awk -F '|' '$10 == "value" {print $0}' file
```

