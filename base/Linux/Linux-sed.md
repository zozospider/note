
查找删除:

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
  # echo "${line}"
  sed -i "/$line/d" file
done
```
