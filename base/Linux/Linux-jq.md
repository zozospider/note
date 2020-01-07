
- [jq 一个强悍的json格式化查看工具](https://sq.163yun.com/blog/article/197406979988197376)
- [Linux Shell格式化Json](https://www.jianshu.com/p/fbe712c19ea2)

Linux 上格式化 JSON 字符串, 使用方式
```bash
echo '{"uid":100120,"token":"1fa9fb8004b04f66b7da57393641eddc"}' | jq .
```

- 下载页面: [Download jq](https://stedolan.github.io/jq/download/)
- 安装包下载地址: [Source tarball for jq 1.6](https://github.com/stedolan/jq/releases/download/jq-1.6/jq-1.6.tar.gz)
- 安装包下载地址: [Source tarball for jq 1.5](https://github.com/stedolan/jq/releases/download/jq-1.5/jq-1.5.tar.gz)
- 安装包下载地址: [Source tarball for jq 1.6](https://github.com/stedolan/jq/releases/download/jq-1.4/jq-1.4.tar.gz)

```bash
# 解压压缩包
cd /home/zozo/app/jq
tar -zxvf jq-1.6.tar.gz
cd jq-1.6
# 可能没有这个命令, 需要安装较高版本 (不同 jq 版本对 autoreconf 版本要求不一样, 如果 autoreconf 版本较低, 可以尝试较低版本的 jq)
autoreconf -i
# 指定安装目录 (需要先新建 jq16 文件夹)
./configure --prefix=/home/zozo/app/jq16 --disable-maintainer-mode
make
make install
```
