
- 下载页面: [Download jq](https://stedolan.github.io/jq/download/)
- 安装包下载地址: [Source tarball for jq 1.6](https://github.com/stedolan/jq/releases/download/jq-1.6/jq-1.6.tar.gz)

```bash
# 解压压缩包
cd /home/zozo/app/jq
tar -zxvf jq-1.6.tar.gz
cd jq-1.6
# 可能没有这个命令
autoreconf -i
# 指定安装目录 (需要先新建 jq16 文件夹)
./configure --prefix=/home/zozo/app/jq16 --disable-maintainer-mode
make
make install
```
