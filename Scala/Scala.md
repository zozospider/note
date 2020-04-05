
# Scala 安装

1. 打开 [下载页面](https://www.scala-lang.org/download/2.12.11.html) 下载 `scala-2.12.11.tgz - Mac OS X, Unix, Cygwin - 19.83M` 并解压到 `/Users/zoz/zz/app/scala/scala-2.12.11` 目录

2. 添加环境遍历, 修改 `~/.bash_profile` (修改后执行 `source ~/.bash_profile` 生效):
```bash
# java
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home
export PATH=$PATH:$JAVA_HOME/bin

# scala
export SCALA_HOME=/Users/zoz/zz/app/scala/scala-2.12.11
export PATH=$PATH:$SCALA_HOME/bin
```

3. 验证:
```bash
spiderxmac:~ zoz$ source ~/.bash_profile
spiderxmac:~ zoz$ scala
Welcome to Scala 2.12.11 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_181).
Type in expressions for evaluation. Or try :help.

scala> val i = 1
i: Int = 1

scala> 
```

---

# IDEA 添加 Scala 插件

`Prefrences` > `Plugins` > `Browse repositories` > 搜索 `Scala`, 找到 `LANGUAGES Scala` 插件 install > 重启 IDEA

# IDEA 将普通项目修改成 Scala 项目 (否则无法新建 Scala 文件)

1. 选中项目名称右键 > `Add Frameworks Support` > `Scala`, 如果没有则点击 `Create` 并选中 `/Users/zoz/zz/app/scala/scala-2.12.11` > `OK`

2. `src/main` 目录下新建 `scala` 文件夹, 并右键 `Mark Directory as` > `Sources Root`

---

# IDEA 查看源码

- [Library sources not found for scala-library in Intellij](https://blog.csdn.net/mengxpfighting/article/details/79889326)
- [Library sources not found for scala-library in Intellij](https://stackoverflow.com/questions/28445260/library-sources-not-found-for-scala-library-in-intellij)

1. 打开 [下载页面](https://www.scala-lang.org/download/2.12.11.html) 下载 Sources `scala-sources-2.12.11.tar.gz` 并解压, 改文件夹名称 (注意解压后的文件夹名称为 `scala-2.12.11`, 需要修改为 `scala-sources-2.12.11`)

2. 拷贝到 `/Users/zoz/zz/app/scala/scala-sources-2.12.11` 目录

3. `File` > `Project structure` -> `Global libraries` -> `scala-sdk-2.12.11` -> `Standard library` -> `Sources` (如果没有则跳过) -> 最下面的左边的 `+` 按钮 -> 选中 `/Users/zoz/zz/app/scala/scala-sources-2.12.11/src` 目录 `open`

---
