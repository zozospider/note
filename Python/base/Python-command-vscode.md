
# Terminal

打开 Terminal 方式:

1. View -> Terminal
2. 快捷键: Ctrl + `

# Python 插件

Open Python File -> Extensions -> Python (Microsoft) -> Install

## Linter

安装好 Python 插件后, 右下角会出现 Linter pylint is not installed 提示, 先选择左下角的 Python 版本, 再点击 Install, 此时 Linter 就会帮助提示语法错误.

可以通过: Open Python File -> View -> Command Palette -> Select Linter -> Pylint (默认, 推荐) 选择 Linter 的类型

# Fomatting code 插件

- [Python PEP](https://www.python.org/dev/peps/)
- [Python PEP 8](https://www.python.org/dev/peps/pep-0008/)

Open Python File -> View -> Command Palette -> Format Document -> Formatter autopep8 is not installed. Install? 提示 -> Yes

使用 Fomatting code 方式:

1. 手动格式化: Open Python File -> View -> Command Palette -> Format Document
2. 手动格式化快捷键: Open Python File -> Shift + Alt + F
3. 自动格式化: File -> Preferences -> Settings -> Format On Save -> 勾选, 以后保存 Python 文件后就会自动格式化

# 自动运行 Python 插件

此插件可以通过快捷键运行 Python, 而不用每次打开 Terminal, 然后执行 Python app.py

Open Python File -> Extensions -> Code Runner (Jun Han) -> Install

可以通过快捷键 Ctrl + Alt + N 运行当前 Python 文件

## For Mac

此插件调用的是 python -u 命令, 在 Windows 上没问题, 但是 Mac 中的 python 命令一般是调用 python 2, 要使用 python 3, 需要执行以下操作:

File -> Preferences -> Settings -> 