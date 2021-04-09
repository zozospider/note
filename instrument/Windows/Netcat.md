
# Windows 安装 Netcat

- [Windows安装NetCat](https://blog.csdn.net/qq_42881421/article/details/90312940)
- [windows环境下netcat的安装及使用](https://blog.csdn.net/qq_37585545/article/details/82250984)

下载后杀毒软件会提示病毒, 参考:
- https://eternallybored.org/misc/netcat/ 中的 Warning 提示内容
- https://answers.microsoft.com/en-us/protect/forum/all/hacktoolwin32remoteadminmsr-ncatncexe/b2e2a4e0-e5f6-41b6-bfb3-c35bc3f122f5

1. 下载安装包

下载地址 https://eternallybored.org/misc/netcat/

2. 解压到目录: D:\zz\other\software\netcat-win32-1.12

3. cmd 运行命令, 即可向本地的 9999 端口发送数据

```bash
nc -lp 9999
hello word
hello
hello
```
