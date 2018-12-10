# Shell 中 if 判断语法

```
if [ -x FILE ]; then
  echo "true"
fi
```

* `[ -e FILE ]`: 如果 FILE 存在则为真。
* `[ -x FILE ]`: 如果 FILE 存在且是可执行的则为真。
* `[ -z STRING ]`: "STRING" 的长度为零则为真。

