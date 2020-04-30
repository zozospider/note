
# 命令行提示工具

- [denisidoro/navi](https://github.com/denisidoro/navi)
- [命令行忘性大？这个开源备忘工具一次解决你的所有烦恼](https://zhuanlan.zhihu.com/p/83584149)

# Mac 安装

1. 安装:
```bash
# Using Homebrew or Linuxbrew
brew install denisidoro/tools/navi
```

2. 导入:
```bash
# a1. 输入命令并选择某个仓库 (如 denisidoro/cheats)
navi repo browse
# a2. 直接选择某个仓库
navi repo add https://github.com/denisidoro/cheats

# b. 选择当前仓库中, 需要导入的命令并回车, 如:
os/osx.cheat
code/git.cheat
misc/crontab.cheat
```

3. 使用:
```bash
# 输入 navi 回车:
navi

# 然后输入想要执行的命令, 此时界面会提示, 如:
# 最左边为命令模块, 中间为介绍, 最右边为备选命令, 最上面为详细说明
# crontab, schedule    Edit cron job    crontab -e
# crontab, schedule    List cron jobs   crontab -l
crontab

# 选中需要执行的命令回车, 即可执行当前命令
```
