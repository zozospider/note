
# 查看文档

```bash
# 查看 math 的文档 (Mac)
python3 -m pydoc math
pydoc3 math

# 查看 math / zozopdf.pdf2text 的文档 (Windows)
# space: 查看下一页
# q: 退出
python -m pydoc math
python -m pydoc zozopdf.pdf2text
```

# 将文档转换为 HTML

```bash
# 将文档转换为 HTML (Windows)
# 执行命令后会在当前目录下生成一个 math.html 和 zozopdf.pdf2text.html 的文件, 可使用浏览器打开查看
python -m pydoc -w math
python -m pydoc -w zozopdf.pdf2text
```

# 在本地启动一个 Web 服务器, 用于查看 Python 文档

```bash
# 请先退出外网

# 在本地启动一个 Web 服务器 (Windows)
xxx\Python Package Index>python -m pydoc -p 1234
Server ready at http://localhost:1234/
Server commands: [b]rowser, [q]uit
server>
# 退出
server>q
Server stopped
xxx\Python Package Index>

# 使用以下地址浏览, 可以看到本地的所有 Python 包的文档
http://localhost:1234/
```
