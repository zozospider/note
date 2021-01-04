# 发布包到 pypi.org

## 一. 注册账号

在 https://pypi.org/ 注册账号 (记住用户名密码), 注册后记得在邮箱中激活

---

## 二. 安装 setuptools wheel twine

```bash
# 安装 setuptools
python -m pip install --user setuptools wheel twine
```

---

## 三. 创建工作空间, package, modules

1. 创建一个 zozopdf 的工作空间, 并创建 zozospider 文件夹作为 package, tests 文件夹用于测试, data 文件夹用于存放数据

2. 在 zozospider 文件夹下创建 __init__.py 文件以表示 zozospider 为 package

3. 在 zozospider 文件夹下创建 pdf2text.py 和 pdf2image.py 文件作为 module

- pdf2text.py:
```python
def convert():
    print("This is zozopdf pdf2text convert()")
```

- pdf2image.py:
```python
def convert():
    print("This is zozopdf pdf2image convert()")
```

---

## 四. 创建 setup.py

在工作空间下创建 setup.py 文件, 内容如下:

```python
# from pathlib import Path
import setuptools

setuptools.setup(
    name="zozopdf",
    version=1.0,
    # can't get content of current path
    # long_description=Path(R".\README.md").read_text(),
    long_description="This is the homepage of our project.",
    # tests and data directory is not packages
    packages=setuptools.find_packages(exclude=["tests", "data"])
)
```

_注_: 无法通过 Path 获取到当前目录下的 README.md 的内容 (先手写一下)

_注_: 如果是更新版本, 需要修改 version, 否则无法上传成功

---

## 五. 创建 README.md

在工作空间下创建 README.md 文件, 内容如下:

```markdown
This is the homepage of our project.
```

---

## 六. 创建 LICENSE

在工作空间下创建 LICENSE 文件, 内容如下:

从网站 [Choose an open source license](https://choosealicense.com/) 中选择 [I care about sharing improvements.](https://choosealicense.com/licenses/gpl-3.0/) 并点击 `Copy license text to clipboard` 拷贝内容, 粘贴到 LICENSE 文件中

---

## 七. 构建

在工作空间下执行以下命令:

```bash
# sdist: source distribution
# bdist_wheel: build distribution
python setup.py sdist bdist_wheel
```

执行后会生成以下几个文件夹:
- build
- dist (包含旧版本)
- zozopdf.egg-info

_注_: 如果是更新版本, 需要修改 setup.py 中的 version, 否则无法上传成功

---

## 八. 上传到 pypi.org

在工作空间下执行以下命令:

```bash
# 上传命令
twine upload dist/*
# 上传命令 (跳过已存在的版本, 不覆盖) (dist 文件夹中会包含旧版本) (推荐)
twine upload --skip-existing dist/*
...
# 提示输入用户名密码
Enter your username: zozospider
Enter your password:
...
  Skipping zozopdf-1.0-py3-none-any.whl because it appears to already exist
Uploading zozopdf-1.1-py3-none-any.whl
...
  Skipping zozopdf-1.0.tar.gz because it appears to already exist
Uploading zozopdf-1.1.tar.gz
...
View at:
https://pypi.org/project/zozopdf/1.1/

# 上传成功后可在 https://pypi.org/ 中通过 `zozopdf` 关键词搜索到结果并打开 https://pypi.org/project/zozopdf/, 也可以通过上传命令行结果中的提示网址直接打开项目主页: https://pypi.org/project/zozopdf/1.1/
```

_注_:

PyPI 不允许重复使用分发文件名 (项目名称 + 版本号 + 分发类型)

这样可以确保给定项目的给定发行版的给定发行版将始终解析为同一文件, 并且项目维护者或恶意方不能在一天之内秘密更改 (只能将其删除), 您需要将版本号更改为以前未上传到 PyPI 的版本号

参考: https://stackoverflow.com/questions/52016336/how-to-upload-new-versions-of-project-to-pypi-with-twine

---

## 九. 下载并使用发布的包

1. 下载 / 更新包:
```bash
# 下载包
# 正常环境 (Windows)
python -m pip install --user zozopdf
python -m pip install --user zozopdf==1.1
# 虚拟环境 (Mac / Windows)
pipenv install zozopdf
pipenv install zozopdf==1.1

# 更新包 (Windows)
python -m pip install --user --upgrade zozopdf
```

2. 使用包:
```python
from zozopdf import pdf2text
from zozopdf import pdf2image

pdf2text.convert()
pdf2image.convert()
```

---

## 十. 重新上传

从第七步开始执行

---
