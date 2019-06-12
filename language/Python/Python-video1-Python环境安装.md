
- [Document & Code](#document--code)
- [一. 下载 Python 安装包](#一-下载-python-安装包)
- [二. 安装 Python](#二-安装-python)
- [三. IDLE 与第一段 Python 代码](#三-idle-与第一段-python-代码)

---

# Document & Code

- [../Python-video1](https://github.com/zozospider/note/blob/master/language/Python/Python-video1.md)

---

# 一. 下载 Python 安装包

Python 2.x 与 Python 3.x 版本差异较大, 不完全兼容.

- 官网: https://www.python.org/

- 文档: https://docs.python.org/3.6/

- 下载页: https://www.python.org/downloads/release/python-362/

- 下载地址
  - Mac OS X: https://www.python.org/ftp/python/3.6.2/python-3.6.2-macosx10.6.pkg
  - Windows: https://www.python.org/ftp/python/3.6.2/python-3.6.2-amd64.exe

---

# 二. 安装 Python

- Mac OS X

注: Mac OS X 自带了 Python 2.7.10

```
➜  ~ python --version
Python 2.7.10
➜  ~
```

Mac OS X 下载 pkg 包后直接按照界面安装即可.

完成后可以检查版本
```
➜  ~ python3 --version
Python 3.6.2
➜  ~
```

- Windows

Windows 下载 exe 包后直接按照界面安装即可. (加入 Path, 全部勾选)

---

# 三. IDLE 与第一段 Python 代码

打开 IDLE

Mac OS X 可通过 `IDLE` -> `Preferences` -> `Size` 调整字体大小: 22

- 运行 `Hello World`, 不建议加 `;`:
```python
>>> print('Hello, World')
Hello, World
>>> print('Hello, World');
Hello, World
>>> 
```

---
