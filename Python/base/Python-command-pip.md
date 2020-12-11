
# 查看模块

- [pip list](https://pip.pypa.io/en/stable/reference/pip_list/)

```bash
# 列出过时的包
python3 -m pip list --user --outdated
python3 -m pip list --outdated

# 列出最新的包
python3 -m pip list --user --uptodate
python3 -m pip list --uptodate
```

# 安装模块

```bash
# 在线安装
python3 -m pip install --user SomeProject
# 离线安装
python3 -m pip install --user "xxx.whl"

# 安装后的包在
ls -l /Users/zoz/Library/Python/
# 所以可以在 ~/.bash_profile 配置如下
# Python 3.9 pip packages path
export PYTHON_PIP_PACKAGES_HOME=/Users/zoz/Library/Python/3.9
export PATH=$PATH:$PYTHON_PIP_PACKAGES_HOME/bin
```

---

# 卸载模块

```bash
# 卸载模块 (要不要加 --user?)
python3 -m pip uninstall xxx
```
