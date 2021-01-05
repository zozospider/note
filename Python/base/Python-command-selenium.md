
- [Selenium with Python](https://selenium-python.readthedocs.io/)

# 使用

## 一. 安装包

```bash
# 安装包 (Windows)
python -m pip install --user selenium
```

---

## 二. 安装浏览器驱动

驱动可用于自动执行浏览器行为, 安装步骤如下:

1. 在 https://pypi.org/ 搜索 selenium 关键词: https://pypi.org/search/?q=selenium

2. 找到 driver, 选择 Chrome driver: https://sites.google.com/a/chromium.org/chromedriver/downloads -> 选择和当前 Chrome 版本匹配的包, 如 _Chrome 87_ 对应: _If you are using Chrome version 87, please download ChromeDriver 87.0.4280.88_ -> 下载 zip 包

3. 解压 zip 包, 并且将解压出来的可执行文件添加到 PATH (否则 selenium 程序会报错), 添加到 PATH 以后要重启 Terminal & VSCode 才能执行 selenium 程序

---

## 三. 编码

通过以下代码可以自动打开浏览器:

```python
from selenium import webdriver

browser = webdriver.Chrome()
browser.get("https://github.com")
```

详细代码见: `xxx\code_with_mosh_python_getting_started\Popular Python Packages\Browser Automation.py`

---
