# Shell 中 if 判断语法

```
if [ -x FILE ]; then
  echo "true"
fi
```

以上 Shell 中的 if 用于判断条件是否成立，条件有以下多种表现方式：

* `[ -e FILE ]`: 如果 FILE 存在则为真。
* `[ -x FILE ]`: 如果 FILE 存在且是可执行的则为真。
* `[ -z STRING ]`: "STRING" 的长度为零则为真。

