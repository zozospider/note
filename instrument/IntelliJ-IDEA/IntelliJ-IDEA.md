# IntelliJ IDEA

## Documentation
> * [mac激活](https://www.jianshu.com/p/3c87487e7121)
> * [提交代码到Github](https://github.com/FatliTalk/blog/issues/11)
> * [提交代码到Github-2](https://blog.csdn.net/rongxiang111/article/details/78120126)

## Open
> 1. 打开: File > Settings > Appearance & Behavior > System Settings
> 2. 去掉勾选: Startup/Shutdown > Reopen last project on startup

## Keymap Refrence
> Help > Keymap Refrence

## 无法下载源码

在项目根目录下手动执行如下命令：
```
mvn dependency:resolve -Dclassifier=sources
```

## Git 忽略 .idea 文件

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
