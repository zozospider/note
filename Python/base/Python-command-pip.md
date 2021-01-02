
# 查看模块

- [pip list](https://pip.pypa.io/en/stable/reference/pip_list/)

- tips: 如果下载超时, 可尝试断开外网

```bash
# 查看 pip 版本 (Windows)
python -m pip --version
pip --version
```

```bash
# 列出所有包 (Windows)
python -m pip list
python -m pip list --user

# 列出过时的包 (Mac)
python3 -m pip list --user --outdated
python3 -m pip list --outdated

# 列出最新的包 (Mac)
python3 -m pip list --user --uptodate
python3 -m pip list --uptodate
```

# 安装模块

```bash
# 安装 (Mac)
python3 -m pip install --user SomeProject
# 安装 (Windows)
python -m pip install --user SomeProject

# 安装指定版本的包 (会先卸载已有版本, 再安装指定版本) (Windows)
python -m pip install --user SomeProject==x.x.x
# 安装指定版本的最高版本的包 (Windows)
python -m pip install --user SomeProject==2.9.*
python -m pip install --user SomeProject~=2.9.0
python -m pip install --user SomeProject==2.*

# 离线安装 (Mac)
python3 -m pip install --user "xxx.whl"

# 加 --user 安装后的包在以下位置 (Mac):
ls -l /Users/zoz/Library/Python/
# 可以在 ~/.bash_profile 配置如下内容 (Mac):
# Python 3.9 pip packages path
export PYTHON_PIP_PACKAGES_HOME=/Users/zoz/Library/Python/3.9
export PATH=$PATH:$PYTHON_PIP_PACKAGES_HOME/bin

# 不加 --user 安装后的包在以下位置 (Windows):
dir C:\Users\thisi\AppData\Local\Programs\Python\Python39\Lib\site-packages
# 加 --user 安装后的包在以下位置 (Windows):
dir C:\Users\thisi\AppData\Roaming\Python\Python39\site-packages
```

---

# 卸载模块

```bash
# 卸载模块 (Mac) (好像不要加 --user?)
python3 -m pip uninstall SomeProject
# 卸载模块 (Windows) (不要加 --user)
python -m pip uninstall SomeProject
```
