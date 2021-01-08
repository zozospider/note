
- [Anaconda](https://www.anaconda.com/)
- [Jupyter](https://jupyter.org/)

# 安装

在 https://www.anaconda.com/products/individual 下载 Individual Edition (推荐外网更快), 并安装 (安装时选择推荐的默认安装选项即可)

相关命令在以下路径中 (可以加入 PATH, 但是要考虑和 PATH 中已有的 Python 冲突):
- C:\Users\thisi\anaconda3
- C:\Users\thisi\anaconda3\Scripts

---

# 启动

选择 `windows - 开始 - Anaconda3 (64-bit) - Jupyter Notebook (anaconda3)`, 此时会自动启动一个命令行窗口, 并默认打开 http://localhost:8888/tree

## 其他盘启动

默认打开的是 C 盘, 如果需要进入其他盘, 执行以下操作:

选择 `windows - 开始 - Anaconda3 (64-bit) - Anaconda Prompt (anaconda3)`, 此时会自动启动一个命令行窗口, 执行以下命令

```bash
# 方式一
(base) C:\Users\thisi>jupyter notebook --notebook-dir=D:\zz\code\anaconda

# 方式二
# 进入 D 盘目录
(base) C:\Users\thisi>D:
(base) D:\>cd D:\zz\code\anaconda
# 在此处启动 Jupyter Notebook
(base) D:\zz\code\anaconda>jupyter notebook
```

---

# 创建 Notebook

选择需要创建 Notebook 的文件夹, 然后点击右上角的 New -> Python3, 此时会打开一个新的标签页, 将顶部的 `Untitled` 改名成 `HelloWorld` 后, 会在当前文件夹中创建一个 `HelloWorld.ipynb` 文件和 `.ipynb_checkpoints` 文件夹, `HelloWorld.ipynb` 文件即为 Jupyter Notebook 源文件

---
