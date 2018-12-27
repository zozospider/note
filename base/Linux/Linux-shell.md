
# Sell 中 source 命令

Shell 中调用 `source file` 命令，需要注意 file 文件的编码，可通过 `enca file` 查看，此外 file 中的环境变量命名不能有 `.` 符号出现。

# Shell 中 if 判断语法

```
if [ -x FILE ]; then
  echo "true"
fi
if [ ! -x FILE ]; then
  echo "false"
fi
```

以上 Shell 中的 if 用于判断条件是否成立，条件有以下多种表现方式：

* `[ -a FILE ]`: 如果 FILE 存在则为真。
* `[ -e FILE ]`: 如果 FILE 存在则为真。
* `[ -f FILE ]`: 如果 FILE 存在且是一个普通文件则为真。
* `[ -d FILE ]`: 如果 FILE 存在且是一个目录则为真。
* `[ -s FILE ]`: 如果 FILE 存在且大小不为 0 则为真。
* `[ -r FILE ]`: 如果 FILE 存在且是可读的则为真。
* `[ -x FILE ]`: 如果 FILE 存在且是可执行的则为真。

* `[ -z STRING ]`: "STRING" 的长度为零则为真。
* `[ STRING1 == STRING2 ]`: 如果 2 个字符串相同。
* `[ STRING1 != STRING2 ]`: 如果字符串不相等则为真。
