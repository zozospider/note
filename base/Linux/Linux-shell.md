
- [source](#source)
- [sleep](#sleep)
- [变量与判断](#变量与判断)
    - [变量赋值注意](#变量赋值注意)
    - [数组](#数组)
    - [if 语法](#if-语法)
    - [变量类型](#变量类型)
    - [变量比较](#变量比较)
        - [整数比较](#整数比较)
        - [字符串比较](#字符串比较)
    - [并且 / 或者 / 非](#并且--或者--非)
    - [参数变量](#参数变量)
- [循环](#循环)
    - [数字性循环](#数字性循环)
    - [字符性循环](#字符性循环)
    - [路径查找](#路径查找)
    - [日期循环](#日期循环)
- [字符串处理](#字符串处理)
    - [字符串截取](#字符串截取)
- [日期](#日期)
    - [获取日期](#获取日期)

---

# source

Shell 中调用 `source file` 命令, 需要注意 file 文件的编码, 可通过 `enca file` 查看, 此外 file 中的环境变量命名不能有 `.` 符号出现。

---

# sleep

```bash
sleep 1    # 睡眠 1 秒
sleep 1s   # 睡眠 1 秒
sleep 1m   # 睡眠 1 分
sleep 1h   # 睡眠 1 小时
```

---

# 变量与判断

## 变量赋值注意

```bash
# 注意: 使用管道循环赋值无效
# 以下为无效赋值, 最终结果: x: xx
x="xx"
echo "abc" | while read line
do
  x=$line
done
echo x: $x

# 以下为有效赋值, 最终结果: y: abc
y="yy"
for line in echo "abc"
do
  y=$line
done
echo y: $y
```

## 数组

```bash
# 添加元素
arr=()
for i in `seq 1 10`
do
  arr[${#arr[*]}]=$i
done

arr_0=${arr[0]}
arr_1=${arr[1]}
echo arr_0: $arr_0, arr_1: $arr_1

for i in ${arr[*]}
do
  echo $i
done
```

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

以下两种方式等价:

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

### 布尔比较

```bash
#!/bin/sh
b=true
if [ "$b" == true ]; then
  echo "b is true"
fi
```

## 并且 / 或者 / 非

- 并且

```bash
# 推荐
if [ bool1 -a bool2 ]; then
  echo "bool1 and bool2"
fi
```

```bash
if [ bool1 && bool2 ]; then
  echo "bool1 and bool2"
fi
```

- 或者

```bash
# 推荐
if [ bool1 -o bool2 ]; then
  echo "bool1 or bool2"
fi
```

```bash
if [ bool1 || bool2 ]; then
  echo "bool1 or bool2"
fi
```

- 非

```bash
if [ ! bool ]; then
 echo "not bool"
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

# 循环

- [Linux下Shell的for循环语句](https://www.cnblogs.com/EasonJim/p/8315939.html)

## 数字性循环

```bash
#!/bin/bash
for((i=1;i<=10;i++));
do
echo $(expr $i \* 3 + 1);
done
```

```bash
#!/bin/bash

for i in $(seq 1 10)
do
echo $(expr $i \* 3 + 1);
done
```

```bash
#!/bin/bash

for i in {1..10}
do
echo $(expr $i \* 3 + 1);
done
```

```bash
#!/bin/bash

awk 'BEGIN{for(i=1; i<=10; i++) print i}'
```

## 字符性循环

```bash
#!/bin/bash

for i in `ls`;
do
echo $i is file name\! ;
done
```

```bash
#!/bin/bash

for i in $* ;
do
echo $i is input chart\! ;
done
```

```bash
#!/bin/bash

for i in f1 f2 f3 ;
do
echo $i is appoint ;
done
```

```bash
#!/bin/bash

list="rootfs usr data data2"
for i in $list;
do
echo $i is appoint ;
done
```

## 路径查找

```bash
#!/bin/bash

for file in /proc/*;
do
echo $file is file path \! ;
done
```

```bash
#!/bin/bash

for file in $(ls *.sh)
do
echo $file is file path \! ;
done
```

## 循环文件内容

```bash
# 1,2,3
# ONE,TWO,THREE
cat file | while read line
do
  # a,b,c
  echo $line
  # 按 `,` 切割每一行内容
  IFS=","
  arr=($line)

  a=${arr[0]}
  b=${arr[1]}
  c=${arr[2]}

  # a: 1, b: 2, c: 3
  echo "a: $a, b: $b, c: $c"
done
```

## 日期循环

- 方式一

```bash
#! /bin/bash

start='2020-02-20'
end='2020-04-11'

# 注: 日期范围是 [start, end)
# 注: while 里面的左右条件一定要用 "" 引起来, ' 不行
while [ "${start}" != "${end}" ]
do
  echo ${start}
  # 可转换成 %Y%m%d 格式使用
  date=`date -d "${start}" +%Y%m%d`
  echo ${date}
  # 日期自增
  start=`date -d "1 day ${start}" +%Y-%m-%d`
done
```

- 方式二 (更灵活)

```bash
#! /bin/bash

start=`date -d "2020-02-20" +%Y%m%d`
end=`date -d "2020-04-10" +%Y%m%d`

# 注: 日期范围是 [start, end]
while [ ${start} -le ${end} ]
do
  echo ${start}
  # 可转换成 %Y-%m-%d 格式使用
  date=`date -d "${start}" +%Y-%m-%d`
  echo ${date}
  # 日期自增
  start=`date -d "1 day ${start}" +%Y%m%d`
done
```

---


# 字符串处理

## 字符串截取

- [Shell字符串截取（非常详细）](http://c.biancheng.net/view/1120.html)

截取规则如下:

| 序号 | 格式 | 说明 |
| :--- | :--- | :--- |
| 1 | `${string: start :length}` | 从 string 字符串的左边第 start 个字符开始, 向右截取 length 个字符. |
| 2 | `${string: start}` | 从 string 字符串的左边第 start 个字符开始截取, 直到最后. |
| 3 | `${string: 0-start :length}` | 从 string 字符串的右边第 start 个字符开始, 向右截取 length 个字符. |
| 4 | `${string: 0-start}` | 从 string 字符串的右边第 start 个字符开始截取, 直到最后. |
| 5 | `${string#*chars}` | string 字符串从左到右第一次出现 chars 的位置开始, 截取 chars 右边的所有字符. |
| 6 | `${string##*chars}` | string 字符串从左到右最后一次出现 chars 的位置开始, 截取 chars 右边的所有字符. |
| 7 | `${string%chars*}` | string 字符串从右到左第一次出现 chars 的位置开始, 截取 chars 左边的所有字符. |
| 8 | `${string%%chars*}` | string 字符串从右到左最后一次出现 chars 的位置开始, 截取 chars 左边的所有字符. |

5, 6, 7, 8 示例如下:
```bash
#!/bin/bash
url="http://c.biancheng.net/index.html"
echo ${url#*/}    # 结果为 /c.biancheng.net/index.html
echo ${url##*/}   # 结果为 index.html
str="---aa+++aa@@@"
echo ${str#*aa}   # 结果为 +++aa@@@
echo ${str##*aa}  # 结果为 @@@

#!/bin/bash
url="http://c.biancheng.net/index.html"
echo ${url%/*}    # 结果为 http://c.biancheng.net
echo ${url%%/*}   # 结果为 http:
str="---aa+++aa@@@"
echo ${str%aa*}   # 结果为 ---aa+++
echo ${str%%aa*}  # 结果为 ---
```

---

# 日期处理

## 获取日期

```bash
# 获取当前日期
date +%Y%m%d
date +%Y-%m-%d
date +%Y_%m_%d
date +%Y%m%d%H%M%S
date +%Y-%m-%d-%H-%M-%S
date +%Y_%m_%d_%H_%M_%S
date +%Y-%m-%d_%H_%M_%S
date +%Y-%m-%d_%H:%M:%S
date +"%Y-%m-%d %H:%M:%S"
DATE=$(date +"%Y-%m-%d %H:%M:%S")

# 以下时间单位加或者不加 `s` 都可以, 如 `2 day` 或 `2 days` 都可以
# 2 秒前
date +"%Y-%m-%d %H:%M:%S" --date="2 seconds ago"
# 2 分钟前
date +"%Y-%m-%d %H:%M:%S" --date="2 minutes ago"
# 2 小时前
date +"%Y-%m-%d %H:%M:%S" --date="2 hours ago"
# 2 天前
date +"%Y-%m-%d %H:%M:%S" --date="2 days ago"
# 2 周前
date +"%Y-%m-%d %H:%M:%S" --date="2 weeks ago"
# 2 个月前
date +"%Y-%m-%d %H:%M:%S" --date="2 months ago"
# 2 年前
date +"%Y-%m-%d %H:%M:%S" --date="2 years ago"
DATE=$(date +"%Y-%m-%d %H:%M:%S" --date="2 years ago")

# 2 秒后
date +"%Y-%m-%d %H:%M:%S" --date="2 seconds"
# 2 分钟后
date +"%Y-%m-%d %H:%M:%S" --date="2 minutes"
# 2 小时后
date +"%Y-%m-%d %H:%M:%S" --date="2 hours"
# 2 天后
date +"%Y-%m-%d %H:%M:%S" --date="2 days"
# 2 周后
date +"%Y-%m-%d %H:%M:%S" --date="2 weeks"
# 2 个月后
date +"%Y-%m-%d %H:%M:%S" --date="2 months"
# 2 年后
date +"%Y-%m-%d %H:%M:%S" --date="2 years"
DATE=$(date +"%Y-%m-%d %H:%M:%S" --date="2 years")

# 某个时间参数的 2 天前
# 注意: 传入的 ${DATE} 格式必须为 `%Y%m%d %H:%M:%S`, 转化后的格式随意
DATE=$(date +"%Y%m%d %H:%M:%S")
DATE_TMP=$(date +"%Y-%m-%d_%H-%M-%S" --date="${DATE} 2 days ago")
```

---
