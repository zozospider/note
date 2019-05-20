
# Documentation

* [mac激活](https://www.jianshu.com/p/3c87487e7121)
* [IntelliJ-IDEA 提交代码到 Github](https://github.com/FatliTalk/blog/issues/11)
* [IntelliJ-IDEA 提交代码到 Github-2](https://blog.csdn.net/rongxiang111/article/details/78120126)
* [git上传本地Intellij idea 项目到github](https://blog.csdn.net/u010237107/article/details/50910879)

---

# Open

1. 打开: File > Settings > Appearance & Behavior > System Settings

2. 去掉勾选: Startup/Shutdown > Reopen last project on startup

3. 界面主题字体修改: File > Settings > Appearance & Behavior > Appearance > 勾选 Use custom font > 修改 Size

4. 编辑区字体修改: File > Settings > Editor > Font > 修改 Size

# Keymap Refrence

Help > Keymap Refrence

# 无法下载源码

在项目根目录下手动执行如下命令：
```
mvn dependency:resolve -Dclassifier=sources
```

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

# IDEA 提交项目到 GitHub

1. CVS - import into Version Control - Share Project on GitHub
2. CVS - import into Version Control - Create Git Repository
3. 右键 - Git - add
4. 右键 - Git - Commit Directory
5. 右键 - Git - Repository - Push

# Tomcat 配置

- 正常流程: https://www.jianshu.com/p/6a462984ba99

- 无 artifacts 问题: https://blog.csdn.net/zsy3313422/article/details/52583091

- javax.servlet 包不存在的问题: https://blog.csdn.net/qq_41283865/article/details/81865806

