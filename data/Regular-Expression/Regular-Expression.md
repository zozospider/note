
# Document & Code

- [正则表达式 - 教程](http://www.runoob.com/regexp/regexp-tutorial.html)
- [正则表达式](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Guide/Regular_Expressions)
- [正则表达式在线测试](http://tool.chinaz.com/regex)

---

# 案例

以下正则表达式匹配 `PlayerLogin|abc||why do|xxx` 日志所有内容:
```
^(PlayerLogin|MoneyFlow){1}\|.*
```

以下正则表达式匹配 `PlayerLogin|abc||why do|xxx` 日志中的 `PlayerLogin` 字段:
```
(\w+)\|
```

以下正则表达式匹配 `1991-10-29 10:29:30|core_coin|dfbcaaaa|||df dffa|aa||||||bb|fddb| dddd||axy woshi` 日志所有内容:
```
(\d\d\d\d-\d\d-\d\d)\s(\d\d:\d\d:\d\d)\|(core_coin|core_pay)\|.*
(\d+-\d+-\d+)\s(\d+:\d+:\d+)\|(core_coin|core_pay)\|.*
(\d+-\d+-\d+)\s(\d+:\d+:\d+)\|(\w+|\w+-\w+)\|.*
(\d+-\d+-\d+)\s(\d+:\d+:\d+)\|(\w+)\|.*
```

以下正则表达式匹配 `1991-10-29 10:29:30|core_coin|dfbcaaaa|||df dffa|aa||||||bb|fddb| dddd||axy woshi` 日志中的 `core_coin` 字段:
```
\|(\w+)\|
```
