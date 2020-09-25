
# 查找删除

```bash
# 删除 file 中匹配 delcontent 的行
sed -i '/delcontent/d' file
sed -i '/$del/d' file
```

```bash
#!/bin/bash

# 删除 file 中包含 delfile 内容的行
darray=($(cat delfile))
dlen=${#darray[*]}
for (( i=0;i<dlen;i++ ))
do
  line="${darray[$i]}"
  echo "${line}"
  # 删除 file 中内容等于 line 的行
  sed -i "/$line/d" file
done
```

```bash
#!/bin/bash

darray=($(cat delfile))
dlen=${#darray[*]}
for (( i=0;i<dlen;i++ ))
do
  line="${darray[$i]}"
  echo "${line}"
  # 切割字符 '_' 前的内容 (切割字符 '_' 后的内容使用 ${line#*_}) [详细见下面的 测试]
  head="${line%_*}"
  echo "${head}"
  # awk 中将外部变量 head 转化成 h 后再使用
  cat file | grep "${line}" | awk -v h=$head -F '|' '$2 != h {print $0}' >> local_file
done

# 测试
line="aa_bb_cc"
# 从左到右第一个 '_' 之后的内容 (*_ 匹配 aa_)
echo ${line#*_} # bb_cc
# 从右到左第一个 '_' 之前的内容 (_* 匹配 _cc)
echo ${line%_*} # aa_bb
# 从右到左第一个 '_bb_cc' 之前的内容 (_${line#*_} 匹配 _bb_cc)
echo ${line%_${line#*_}} # aa
# 从左到右第一个 'aa_bb_' 之后的内容 (${line%_*}_ 匹配 aa_bb_)
echo ${line#${line%_*}_}
```

# 替换

```bash
# sed 替换模式中的特殊字符包括: # ? \ . [ ] ^ $ / 等

# 静态替换
## 无特殊字符: old content -> new content
## 或者: sed -in-place -e "s/old content/new content/g" file
sed -i "s/old content/new content/g" file

## 有特殊字符 (无 `#` 号): old/content -> new/content
## 或者: sed -in-place -e "s#old/content#new/content#g" file
sed -i "s#old/content#new/content#g" file

## 有特殊字符 (无 `?` 号): old/content -> new/content
## 或者: sed -in-place -e "s?old/content?new/content?g" file
sed -i "s?old/content?new/content?g" file

# 变量替换
## 无特殊字符: old content -> new content
old="old content"
new="new content"
# 或者: sed -in-place -e "s/${old}/${new}/g" file
sed -i "s/${old}/${new}/g" file

## 有特殊字符 (无 `#` 号): old/content -> new/content
old="old/content"
new="new/content"
## 或者: sed -in-place -e "s#${old}#${new}#g" file
sed -i "s#${old}#${new}#g" file

## 有特殊字符 (无 `?` 号): old/content -> new/content
old="old/content"
new="new/content"
# 或者: sed -in-place -e "s?${old}?${new}?g" file
sed -i "s?${old}?${new}?g" file
```
