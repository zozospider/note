
# IDEA 查看源码

- [Library sources not found for scala-library in Intellij](https://blog.csdn.net/mengxpfighting/article/details/79889326)
- [Library sources not found for scala-library in Intellij](https://stackoverflow.com/questions/28445260/library-sources-not-found-for-scala-library-in-intellij)

1. 打开 [下载页面](https://www.scala-lang.org/download/2.12.11.html) 下载 Sources `scala-sources-2.12.11.tar.gz` 并解压, 改文件夹名称 (注意解压后的文件夹名称为 `scala-2.12.11`, 需要修改为 `scala-sources-2.12.11`)
2. 拷贝到 `/Users/zoz/zz/app/scala/scala-sources-2.12.11` 目录
3. `File` > `Project structure` -> `Global libraries` -> `scala-sdk-2.12.11` -> `Standard library` -> `Sources` (如果没有则跳过) -> 最下面的左边的 `+` 按钮 -> 选中 `/Users/zoz/zz/app/scala/scala-sources-2.12.11/src` 目录 `open`
