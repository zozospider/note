
# Postman 发送 POST, JSON 格式

1. 选择 __POST__ 发送方式, 并填写 URL
2. __Body__ 选择 `raw`, 再选择 `JSON(application/json)` (此时 __Headers__ 中会自动添加 `Content-Type:application/json`)
3. 填写需要发送的 JSON 文本
4. 发送

---

# Postman 发送 POST, JSON, GZIP 格式

前提: HTTP 服务端需要配置为接收 gzip 压缩格式

1. 选择 __POST__ 发送方式, 并填写 URL
2. __Body__ 选择 `binary`
3. __Headers__ 中添加 `Content-Type:application/json` 和 `Accept-Encoding:gzip`
4. 新建一个 JSON 文件, 填写需要发送的 JSON 文本, 并使用 gzip 压缩该文本文件, 如通过 `gzip json.txt` 命令得到 `json.txt.gz`
5. __Body__ 的 `binary` 方式选择本地 `json.txt.gz` 压缩文件
6. 发送

---

# Linux 发送 POST, JSON 格式

1. 在 Linux 命令行执行以下命令:

```bash
# 假设 json_content = [{"headers": {"h1": "h1_c", "h2": "h2_c"}, "body": "body_content"}, {"headers": {"h1": "h1_c", "h2": "h2_c"}, "body": "body_content"}]
# 如需添加其他 Headers 属性, 新增 -H 参数即可
curl -X POST -H 'Content-Type:application/json' -d 'json_content' http://hsotname:port
```

---

# Linux 发送 POST, JSON, GZIP 格式

1. 新建一个 JSON 文件, 填写需要发送的 JSON 文本 json_content, 并使用 gzip 压缩该文本文件, 如通过 `gzip json.txt` 命令得到 `json.txt.gz`

2. 在 Linux 命令行执行以下命令:

```bash
# 如需添加其他 Headers 属性, 新增 -H 参数即可
curl -X POST -H 'Content-Type:application/json' POST -H 'Accept-Encoding:gzip' --data-binary @json.txt.gz http://hsotname:port
```

---
