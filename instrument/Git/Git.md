
# Documentation

* [大白话解释 Git 和 GitHub](http://blog.jobbole.com/111187/)
* [GitHub](https://github.com/)
* [搬进 GitHub](http://gitbeijing.com/)
* [Markdown TOC generate](https://magnetikonline.github.io/markdown-toc-generate/)
* [GitHub Wiki TOC generator](https://ecotrust-canada.github.io/markdown-toc/)

---

# GitHub

## GitHub Desktop

如果 GitHub Desktop Clone 很慢或失败，需要修改 Git 的代理。

如需取消，输入以下命令（windows 10 测试有效）
```
git config --global --unset http.proxy
git config --global --unset https.proxy
```

然后输入以下命令设置局部代理（推荐此方案：windows 10 测试有效）
```
git config --global http.https://github.com.proxy socks5://127.0.0.1:1080
git config --global https.https://github.com.proxy socks5://127.0.0.1:1080
```

输入以下命令可设置全局代理（不推荐此方案：windows 10 测试无效）
```
git config --global http.https://github.com.proxy https://127.0.0.1:1080
git config --global https.https://github.com.proxy https://127.0.0.1:1080
```

参考: [git clone一个github上的仓库，太慢，经常连接失败，但是github官网流畅访问，为什么？](https://www.zhihu.com/question/27159393) 汪小九答案最后的命令。

