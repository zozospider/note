# 创建虚拟环境, 方式一 (Not recommended)

1. 在需要创建虚拟环境的目录下执行以下命令:
```bash
# 在当前目录下 Python Package Index 创建一个虚拟环境, 该环境的文件夹名称为 env (Mac)
python3 -m venv env
# 在当前目录 Python Package Index 下创建一个虚拟环境, 该环境的文件夹名称为 env (Windows)
xxx\Python Package Index>python -m venv env
```

2. 激活:
```bash
# 执行激活命令以进入虚拟环境 (Mac)
source env/bin/activate

# 激活并进入虚拟环境执行: activate.bat (Windows)
# 在虚拟环境中即可安装已有 requests 包 (2.25 版) 的其他版本 (如 2.10 版), 安装的 requests 包在 Python Package Index\env\Lib\site-packages 文件夹中 (Windows)
# 退出虚拟环境执行: deactivate (Windows)
xxx\Python Package Index>env\Scripts\activate.bat
(env) xxx\Python Package Index>python -m pip install requests==2.10.*
(env) xxx\Python Package Index>python -m pip list
Package    Version
---------- -------
pip        20.2.3
requests   2.10.0
setuptools 49.2.1
(env) xxx\Python Package Index>deactivate
xxx\Python Package Index>
```

---

# 创建虚拟环境, 方式二 (Recommended)

1. 安装 pipenv:
```bash
# 安装 pipenv (Mac)
python3 -m pip install --user pipenv

# 安装 pipenv (Windows)
# 安装后的 pipenv 命令在 C:\Users\thisi\AppData\Roaming\Python\Python39\Scripts 中, 可以加入 Path 中
python -m pip install --user pipenv
```

2. 在虚拟环境中安装 requests:
```bash
# 在虚拟环境中安装 requests (Mac)
pipenv install requests

# 在虚拟环境中安装 requests (Windows)
# 创建的虚拟环境在 C:\Users\thisi\.virtualenvs\Python_Package_Index-yN2Nq_6L 文件夹中, requests 包也在其中
# 虚拟环境中安装 requests 后会在 Python Package Index 目录下创建两个文件: Pipfile 和 Pipfile.lock
xxx\Python Package Index>C:\Users\thisi\AppData\Roaming\Python\Python39\Scripts\pipenv install requests
xxx\Python Package Index>C:\Users\thisi\AppData\Roaming\Python\Python39\Scripts\pipenv --venv
C:\Users\thisi\.virtualenvs\Python_Package_Index-yN2Nq_6L
```

3. 进入虚拟环境:
```bash
# 激活并进入虚拟环境执行: pipenv shell (Mac)
pipenv shell

# 激活并进入虚拟环境执行: pipenv shell (Windows)
# 在虚拟环境中运行 Python 文件可使用虚拟环境安装的包 (外部环境无法使用虚拟环境安装的包)
# 退出虚拟环境执行: exit (Windows)
xxx\Python Package Index>C:\Users\thisi\AppData\Roaming\Python\Python39\Scripts\pipenv shell
Launching subshell in virtual environment...
(Python_Package_Index-yN2Nq_6L) xxx\Python Package Index>python Pip.py
<Response [200]>
(Python_Package_Index-yN2Nq_6L) xxx\Python Package Index>exit
xxx\Python Package Index>
```

---

# VSCode 切换 Python 环境 (配置虚拟环境的 Python)

如果需要 VSCode 切换到当前的虚拟环境编程 (Code Runner, Linter, Fomatting code), 请参考:
- Python-command-vscode.md
  - VSCode 切换 Python 环境 (配置虚拟环境的 Python)
