
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

* `[ -n "$1" ]`: 如果第一个参数存在则为空

# Sell sleep

```
sleep 1    睡眠1秒
sleep 1s   睡眠1秒
sleep 1m   睡眠1分
sleep 1h   睡眠1小时
```

# Shell $ 变量

- `$$`: Shell 本身的 PID
- `$!`: Shell 最后运行的后台 Process 的 PID
- `$?`: 最后运行的命令的结束代码 (返回值)
- `$-`: 使用 Set 命令设定的 Flag 一览
- `$*`: 所有参数列表, 如 `$*` 用 `"` 括起来的情况, 以 `"$1 $2 ... $n"` 的形式输出所有参数
- `$@`: 所有参数列表, 如 `$@` 用 `"` 括起来的情况, 以 `"$1" "$2" ... "$n"` 的形式输出所有参数
- `$#`: 添加到 Shell 的参数个数
- `$0`: Shell 本身文件名
- `$1 ~ $n`: 添加到 Shell 的各参数值, $1 是第 1 个参数, $2 是第 2 个参数 ...

params.sh 示例
```bash
#!/bin/bash
printf "The complete list is %s\n" "$$"
printf "The complete list is %s\n" "$!"
printf "The complete list is %s\n" "$?"
printf "The complete list is %s\n" "$*"
printf "The complete list is %s\n" "$@"
printf "The complete list is %s\n" "$#"
printf "The complete list is %s\n" "$0"
printf "The complete list is %s\n" "$1"
printf "The complete list is %s\n" "$2
```

脚本执行结果
```
[Aric@localhost ~]$ bash params.sh 123456 QQ
The complete list is 24249
The complete list is
The complete list is 0
The complete list is 123456 QQ
The complete list is 123456
The complete list is QQ
The complete list is 2
The complete list is params.sh
The complete list is 123456
The complete list is QQ
```

