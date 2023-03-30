
# Documentation

- [mac激活](https://www.jianshu.com/p/3c87487e7121)

---

# Open

1. 打开: File > Settings > Appearance & Behavior > System Settings

2. 去掉勾选: Startup/Shutdown > Reopen last project on startup

3. 界面主题字体修改: File > Settings > Appearance & Behavior > Appearance > 勾选 Use custom font > 修改 Size

4. 编辑区字体修改: File > Settings > Editor > Font > 修改 Size

# Keymap Refrence

Help > Keymap Refrence

# 快捷键

## 代码提示快捷键 (Windows)

### 方法参数提示

`Ctrl+P`

### 代码建议

__tips__: 如果不想改快捷键, 可以尝试使用 `ctrl + alt + space` / `ctrl + shift + space`

Windows 上代码提示默认的 `ctrl + space` 快捷键被切换输入法占用, 需要修改为 `alt + /`

- [Intellij IDEA 代码提示快捷键修改（智能感知）](https://blog.csdn.net/leo3070/article/details/77712522)

## 自动创建变量

变量后写上 .var 默认创建变量名 (也可通过 `Ctrl + Alt + V` 自动生成变量)

- [IntelliJ IDEA自动补全变量名称和属性名称的快捷键](https://blog.csdn.net/cats_miao/article/details/81511812)

另外, 在 __Scala__ 中如果需要自动勾选 Specify type (加上变量类型)

- [idea中Scala自动勾选Specify type](https://blog.csdn.net/chinese_coder/article/details/112172647)

# 无法下载源码

在项目根目录下手动执行如下命令：
```
mvn dependency:resolve -Dclassifier=sources
```
或者尝试更改 Maven 版本试试


# maven 无法导入, 没反应

如果是导入旧版本的包没有任何反应还报错, 但是在 https://mvnrepository.com/ 中又能搜到此版本, 可能是国内镜像未收集此版本, 尝试将 idea 配置的 MAVEN_HOME/conf/setting.xml 中的镜像去掉, 用默认的 MAVEN 主机地址重新导入

# 文件编码

默认采用系统默认编码，修改方式为：

File > Settings > Editor > File Encodings

# Git 忽略 .idea 文件

加入 `.gitignore` 文件
```
/target/
!.mvn/wrapper/maven-wrapper.jar

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### NetBeans ###
/nbproject/private/
/build/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
```

如果 .idea 已经被 Git 跟踪，之后再加入 .gitignore 后是没有作用的，此时需要清空 .idea 的 Git 缓存，在项目更目录下输入如下命令：
```
bogon:note-distributed-zookeeper-video zoz$ git rm -r --cached .idea
rm '.idea/uiDesigner.xml'
bogon:note-distributed-zookeeper-video zoz$ 
```

# IDEA 删除项目

- [IntelliJ IDEA 如何彻底删除项目](https://blog.csdn.net/WJJPro/article/details/78449532)

# IDEA 提交忽略文件

- [intellij idea 忽略文件不提交](https://blog.csdn.net/wangjun5159/article/details/74932433)

# IDEA 提交项目到 GitHub

- [IntelliJ-IDEA 提交代码到 Github](https://github.com/FatliTalk/blog/issues/11)
- [IntelliJ-IDEA 提交代码到 Github-2](https://blog.csdn.net/rongxiang111/article/details/78120126)
- [git 上传本地 Intellij idea 项目到 github](https://blog.csdn.net/u010237107/article/details/50910879)


1. CVS - import into Version Control - Share Project on GitHub
2. CVS - import into Version Control - Create Git Repository (如果已经创建会有提示, 就不用了)
3. 右键 - Git - add (第一次尝试可能没反应, 就不用管了)
4. 右键 - Git - Commit Directory (第一次尝试可能没反应, 就不用管了)
5. 右键 - Git - Repository - Push (第一次尝试可能没反应, 就不用管了)
6. 正常流程: 修改内容 - 本地 Git commit - 项目右键 - Git - Repository - push

如果有必要, 可在 GitHub 官网上添加 README.md
```markdown
# note-data-structures-video1

- [note: zozoSpider/note/Mathematics/data-structures/data-structures-video1](https://github.com/zozospider/note/blob/master/Mathematics/data-structures/data-structures-video1.md)
```

如果使用 Mac 操作的时候存在如下 Apple 的提示, 需要打开 terminal, 输入 `sudo xcodebuild -license` 命令, 然后多次按空格查看协议内容, 然后输入 `agree` 同意协议. terminal 操作完成后需要完全退出 IDEA & terminal 软件后再打开:

- [Agreeing to the Xcode/iOS license requires admin privileges, please re-run as root via sudo](https://stackoverflow.com/questions/26485555/agreeing-to-the-xcode-ios-license-requires-admin-privileges-please-re-run-as-ro?rq=1)

- 提示如下:
```
23:47	Accept XCode/iOS License to Run Git: Run “sudo xcodebuild -license” and retry (admin rights required)
23:51	Git Init Failed
			Сannot Run Git: 
			Agreeing to the Xcode/iOS license requires admin privileges, please run “sudo xcodebuild -license” and then retry this command.
```

- terminal 操作记录如下:
```
spiderxmac:note-hadoop-video1 zoz$ sudo xcodebuild -license
Password:

You have not agreed to the Xcode license agreements. You must agree to both license agreements below in order to use Xcode.

Hit the Enter key to view the license agreements at '/Applications/Xcode.app/Contents/Resources/English.lproj/License.rtf'

Xcode and Apple SDKs Agreement

“Apple” means Apple Inc., a California corporation with its principal place of business at One Infinite Loop, Cupertino, California 95014, U.S.A.

...

By typing 'agree' you are agreeing to the terms of the software license agreements. Type 'print' to print them or anything else to cancel, [agree, print, cancel] agree

You can view the license agreements in Xcode's About Box, or at /Applications/Xcode.app/Contents/Resources/English.lproj/License.rtf

spiderxmac:note-hadoop-video1 zoz$ 
```

---

# 将目录设置为模块

- [idea将目录设置为模块](https://blog.csdn.net/m0_37659871/article/details/80669604)

右键项目 -> `Open Module Settings` -> `+` -> `Modules` -> `Import Module` -> 然后选择 `Maven` (如果是 Maven 项目), 然后一路 next 到 Finish

# Tomcat 配置 (设置自动更新资源, 默认首页打开 URL)

- 正常流程: https://www.jianshu.com/p/6a462984ba99

- 无 artifacts 问题: https://blog.csdn.net/zsy3313422/article/details/52583091

- javax.servlet 包不存在的问题: https://blog.csdn.net/qq_41283865/article/details/81865806

# Tomcat 乱码

- https://blog.csdn.net/weixin_36210698/article/details/79557695
- https://www.cnblogs.com/vhua/p/idea_1.html

---

# Debug 模式多线程调试

所有断点都必须是 Thread 类型 (包括 main 线程, 否则会很奇怪)

- [Idea Intellij多线程调试](https://blog.csdn.net/fuzzytalker/article/details/50925218)
- [IntelliJ IDEA多线程调试](https://my.oschina.net/athantang/blog/888720)
- [IntelliJ IDEA 进行多线程调试](https://www.cnblogs.com/756623607-zhang/p/7090928.html)
- [IDEA 多线程Debug (所有断点都必须是 Thread 类型)](https://blog.csdn.net/u011781521/article/details/79251819)

---

# 本地运行 main() 方法带参数

1. 配置:

Run -> Edit Configurations -> Program arguments -> 填写参数如: `--hostname localhost --port 7777` -> OK

2. 运行

Run - Run xxx
