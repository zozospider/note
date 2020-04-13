
# HTTP 发送普通文本格式请求

## 第一步 生成文件

新建文本文件 `t.txt`:
```json
[{"headers":{"versions":"zyy"}, "body":"2020-03-04 12:12:12|test_hunter3_client_4|80164|999|heihei|aaavvv"}]

[{"headers":{"versions":"zyy"}, "body":"2020-03-04 12:12:12|test_hunter3_client_4|80164|999|heihei|aaavvv"}, {"headers":{"versions":"zyy"}, "body":"2020-03-04 12:12:12|test_hunter3_client_4|80164|999|heihei|aaavvv"}]
```

## 第二步 发送

### 方式一 (命令行指定文件)
```bash
# TODO (不知道怎么指定文件)
```

### 方式二 (命令行)
```bash
curl -X POST -H 'Content-Type:application/json' -H 'charset=UTF-8' -d '[{"headers":{"versions":"zyy"}, "body":"2020-03-04 12:12:12|test_hunter3_client_4|80164|999|heihei|aaavvv"}]' http://127.0.0.1:4141
```

### 方式三 (postman)
- 发送方式: `POST` `http://127.0.0.1:4141`
- Headers: KEY -> `Content-Type`, VALUE -> `application/json`
- Body: `raw` -> `JSON(application/json)` -> `[{"headers":{"versions":"zyy"}, "body":"2020-03-04 12:12:12|test_hunter3_client_4|80164|999|heihei|aaavvv"}, {"headers":{"versions":"zyy"}, "body":"2020-03-04 12:12:12|test_hunter3_client_4|80164|999|heihei|aaavvv"}]`
- Send

---

# HTTP 发送压缩格式请求

## 第一步 生成压缩文件

新建文本文件 `t.txt`:
```json
[{"headers":{"versions":"zyy"}, "body":"2020-03-04 12:12:12|test_hunter3_client_4|80164|999|heihei|aaavvv"}]
```

压缩成 `t.txt.gz`:
```bash
gzip t.txt
```

## 第二步 发送

### 方式一 (命令行指定压缩文件)
```bash
curl -X POST -H 'Content-Type:application/json' -H 'Accept-Encoding:gzip' -H 'charset=UTF-8' --data-binary @t.txt.gz http://127.0.0.1:4141
```

### 方式二 (postman)
- 发送方式: `POST` `http://127.0.0.1:4141`
- Headers: KEY -> `Content-Type`, VALUE -> `application/json`
- Body: `binary` -> t.txt.gz
- Send

---
