
- [yelp](https://www.yelp.com/)
- [yelp developers](https://www.yelp.com/developers/)
- [Discover the Power of Yelp Fusion](https://www.yelp.com/fusion)
- [Endpoint documentation](https://www.yelp.com/developers/documentation/v3)

# 使用 Yelp 接口

参考:
- [Get started with the Yelp Fusion API](https://www.yelp.com/developers/documentation/v3/get_started)
- [Authentication](https://www.yelp.com/developers/documentation/v3/authentication)

## 一. 注册账号

在 https://www.yelp.com/ 注册账号, (记住用户名密码), 注册后记得在邮箱中激活

---

## 二. 创建 App

通过 [Create App](https://www.yelp.com/developers/v3/manage_app) 创建 App:
- App Name: ZozoYelpPy
- Industry: Business
- Contact Email: _qq mail_
- Description: This is ZozoYelpPy

然后记下生成的 Client ID 和 API Key

_注_: 接口调用次数有上限 (Daily API limit: 5000)

---

## 三. 调用接口

调用 `/businesses/search` 接口:
- [/businesses/search](https://www.yelp.com/developers/documentation/v3/business_search)

```python
import requests

# https://www.yelp.com/developers/documentation/v3/business_search
url = "https://api.yelp.com/v3/businesses/search"
# your API Key
api_key = "xxx"
headers = {
    "Authorization": "Bearer " + api_key
}
params = {
    "term": "Barber",
    "location": "NYC"
}
# 查询 NYC 的理发师
response = requests.get(url, headers=headers, params=params)
print("response:", response)
```

详细代码见: `xxx\code_with_mosh_python_getting_started\Popular Python Packages\Yelp API.py`
