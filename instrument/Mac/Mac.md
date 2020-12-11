
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

alias http_proxy='export http_proxy=socks5://127.0.0.1:1086'
alias https_proxy='export https_proxy=socks5://127.0.0.1:1086'
alias un_http_proxy='unset http_proxy'
alias un_https_proxy='unset https_proxy'
```

## 开启和关闭

```bash
# proxy
## 开启
proxy
## 验证
curl cip.cc
## 关闭
unproxy
## 验证
curl cip.cc

# http_proxy
## 开启
http_proxy
## 验证
curl http://cip.cc
## 关闭
un_http_proxy
## 验证
curl http://cip.cc

# https_proxy
## 开启
https_proxy
## 验证
curl https://cip.cc
## 关闭
un_https_proxy
## 验证
curl https://cip.cc
```

## 注意

### Homebrew use proxy

Homebrew 安装的时候, 开启 proxy 没问题

### pip use proxy

- [Python's requests “Missing dependencies for SOCKS support” when using SOCKS5 from Terminal](https://stackoverflow.com/questions/38794015/pythons-requests-missing-dependencies-for-socks-support-when-using-socks5-fro)

- pip 安装的时候
  - 开启 `all_proxy` 报错
  - 开启 `https_proxy` 报错
  - 开启 `http_proxy` 不会报错

可以尝试以下解决方案:
```bash
# 测试有效
unset all_proxy
# 未测试
pip3 install pysocks
# 未测试
pip install request[socks]
# 未测试
pip install -U requests[socks]

```

# Homebrew

见 `Mac-Homebrew.md`
