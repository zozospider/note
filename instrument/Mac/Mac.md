
# 快捷键

| 描述 | 快捷键 |
| :--- | :--- |
| 整屏截图（保存到图片） | command + shift + 3 |
| 整屏截图（保存到剪切板） | control + command + shift + 3 |
| 部分截图（保存到图片） | command + shift + 4 |
| 部分截图（保存到剪切板） | control + command + shift + 4 |

---

# Safari

## 快捷键

| 描述 | 快捷键 |
| :--- | :--- |
| 新建标签页 | command + T |
| 关闭当前标签页 | command + W |

---

# Terminal 配置代理

- [macOS终端命令行配置网络代理](https://blog.csdn.net/talkxin/article/details/97887121)
- [MAC下终端走代理的几种方法](https://blog.csdn.net/yyws2039725/article/details/90675347)
- [Mac OSX终端走shadowsocks代理](https://github.com/mrdulin/blog/issues/18)

## 配置

- `~/.bash_profile` 添加以下内容:
```bash
# 127.0.0.1:1086 端口取决于本地代理服务器的地址和端口, 可通过 Shadowsocks 的 Advance Preference 进行查看
# proxy list
alias proxy='export all_proxy=socks5://127.0.0.1:1086'
alias unproxy='unset all_proxy'
```

## 开启和关闭

```bash
# 开启
proxy
# 验证
curl cip.cc
# 关闭
unproxy
# 验证
curl cip.cc
```

# Homebrew

见 `Mac-Homebrew.md`
