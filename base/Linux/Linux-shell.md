
# Sell 中 source 命令

Shell 中调用 `source file` 命令，需要注意 file 文件的编码，可通过 `enca file` 查看，此外 file 中的环境变量命名不能有 `.` 符号出现。

---

# Sell sleep

```bash
sleep 1    # 睡眠 1 秒
sleep 1s   # 睡眠 1 秒
sleep 1m   # 睡眠 1 分
sleep 1h   # 睡眠 1 小时
```

---

# Shell 变量与判断

## if 语法

```bash
if [ -x FILE ]; then
  echo "true"
fi

if [ ! -x FILE ]; then
  echo "false"
fi

if [ -x FILE ]; then
  echo "true"
else
  echo "false"
fi
```

以上 Shell 中的 `if` 用于判断条件是否成立，条件有以下多种表现方式：
- `[ -a FILE ]`: 如果 FILE 存在则为真。
- `[ -e FILE ]`: 如果 FILE 存在则为真。
- `[ -f FILE ]`: 如果 FILE 存在且是一个普通文件则为真。
- `[ -d FILE ]`: 如果 FILE 存在且是一个目录则为真。
- `[ -s FILE ]`: 如果 FILE 存在且大小不为 0 则为真。
- `[ -r FILE ]`: 如果 FILE 存在且是可读的则为真。
- `[ -x FILE ]`: 如果 FILE 存在且是可执行的则为真。

- `[ -z STRING ]`: "STRING" 的长度为零则为真。
- `[ STRING1 == STRING2 ]`: 如果 2 个字符串相同。
- `[ STRING1 != STRING2 ]`: 如果字符串不相等则为真。

- `[ -n "$1" ]`: 如果第一个参数存在则为空

## 变量类型

- `$$`: Shell 本身的 PID
- `$!`: Shell 最后运行的后台 Process 的 PID
- `$?`: 最后运行的命令的结束代码 (返回值)
- `$-`: 使用 Set 命令设定的 Flag 一览
- `$*`: 所有参数列表, 如 `$*` 用 `"` 括起来的情况, 以 `"$1 $2 ... $n"` 的形式输出所有参数
- `$@`: 所有参数列表, 如 `$@` 用 `"` 括起来的情况, 以 `"$1" "$2" ... "$n"` 的形式输出所有参数
- `$#`: 添加到 Shell 的参数个数
- `$0`: Shell 本身文件名
- `$1 ~ $n`: 添加到 Shell 的各参数值, `$1` 是第 1 个参数, `$2` 是第 2 个参数 ...

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

## 变量比较
```bash
#!/bin/bash
# 如果传入的参数个数小于 2, 则退出
if [ $# -lt 2 ]; then
echo "Need two or more parameters!"
exit 1;
fi
```

### 整数比较
- `-eq`: 等于
  - `if [ "$a" -eq "$b" ]`

- `-ne`: 不等于
  - `if [ "$a" -ne "$b" ]`

- `-gt`: 大于
  - `if [ "$a" -gt "$b" ]`

- `-ge`: 大于等于
  - `if [ "$a" -ge "$b" ]`

- `-lt`: 小于
  - `if [ "$a" -lt "$b" ]`

- `-le`: 小于等于
  - `if [ "$a" -le "$b" ]`

- `<`: 小于, 在双括号中使用
  - `(("$a" < "$b"))`

- `<=`: 小于等于, 在双括号中使用
  - `(("$a" <= "$b"))`

- `>`: 大于, 在双括号中使用
  - `(("$a" > "$b"))`

- `>=`: 大于等于, 在双括号中使用
  - `(("$a" >= "$b"))`

### 字符串比较
- `=`: 等于
  - `if [ "$a" = "$b" ]`

- `==`: 等于
  - `if [ "$a" == "$b" ]`

注: `==` 比较操作符在双中括号对和单中括号对中的行为是不同的:
```bash
[[ $a == z* ]]   # 如果 $a 以 "z" 开头 (模式匹配) 那么结果将为真
[[ $a == "z*" ]]  # 如果 $a 与 z* 相等 (就是字面意思完全一样), 那么结果为真.
[ $a == z* ]      # 文件扩展匹配 (file globbing) 和单词分割有效.
[ "$a" == "z*" ]  # 如果 $a 与 z* 相等 (就是字面意思完全一样), 那么结果为真.
```

- `!=`: 不等号, 这个操作符将在 [[ ... ]] 结构中使用模式匹配.
  - `if [ "$a" != "$b" ]`

- `<`: 小于, 按照 ASCII 字符进行排序, 注意 `<` 使用在 [ ] 结构中的时候需要被转义
  - `if [[ "$a" < "$b" ]]`
  - `if [ "$a" \< "$b" ]`

- `>`: 大于, 按照 ASCII 字符进行排序, 注意 `>` 使用在 [ ] 结构中的时候需要被转义
  - `if [[ "$a" > "$b" ]]`
  - `if [ "$a" \> "$b" ]`

- `-z`: 字符串为 `null` , 意思就是字符串长度为零

- `-n`: 字符串不为 `null`

注: 当 `-n` 使用在中括号中进行条件测试的时候, 必须要把字符串用双引号引用起来，这才是安全的行为. 如果采用了未引用的字符串来使用 `! -z`, 甚至是在条件测试中括号中只使用未引用的字符串的话, 一般也是可以工作的, 然而, 这是一种不安全的习惯. 习惯于使用引用的测试字符串才是正路.

```bash
#!/bin/sh
a=1
# if [ "$a" -eq "$1" ]; then  # ok
if [ "$1" -eq "$a" ] # ok
# if[ "$1" -eq "$a" ]  # not ok
then
  echo "11 var1 == a"
fi

if [ "$1" == "$a" ]; then # ok
# if [ $1 == $a ]; then # can do but not a good style
# if [ $1==$a ]; then # not ok
  echo "22 var1 == a"
fi
```

## 参数变量

- [shell中脚本参数传递的两种方式](https://blog.csdn.net/sinat_36521655/article/details/79296181)
- [Shell 脚本传参方法总结](https://www.jianshu.com/p/d3cd36c97abc)
- [Shell脚本怎么通过 参数名 参数值 传参](https://segmentfault.com/q/1010000000126260)
- [linux shell脚本通过参数名传递参数值](https://www.bbsmax.com/A/Gkz1Xm7gdR/)

以下为通过 getopts 方式获取 demo:
```bash
while getopts ":a:b:c:" opt
do
  case $opt in
    a)
      echo "参数 a 的值 $OPTARG"
      param_a=$OPTARG
      ;;
    b)
      echo "参数 b 的值 $OPTARG"
      param_b=$OPTARG
      ;;
    c)
      echo "参数 c 的值 $OPTARG"
      param_c=$OPTARG
      ;;
    ?)
      echo "未知参数"
      # exit 1
      ;;
  esac
done

if [ ! $param_a ]; then
echo "-a can not be empty!"
exit 1;
fi

if [ ! $param_b ]; then
echo "use default param_b: DEFAULT_B"
fi

echo "param_a: $param_a"
echo "param_b: $param_b"
echo "param_c: $param_c"
```

执行脚本:
```
sh t.sh -a aaa -b bbb -c ccc
```


---

