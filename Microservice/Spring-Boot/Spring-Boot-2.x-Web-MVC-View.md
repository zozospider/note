# Spring Boot 2.x Web MVC View

- [Document & Code](#document--code)
- [Thymeleaf](#thymeleaf)
  - [Local Demo](#local-demo)
  - [Runnable Demo](#runnable-demo)
  - [Together With JSP](#together-with-jsp)

---

## Document & Code
> * [github.com: zozospider/note/Microservice/Spring-Boot/Spring-Boot-2.x](https://github.com/zozospider/note/blob/master/Microservice/Spring-Boot/Spring-Boot-2.x.md)

> * [github.com: zozospider/note-microservice-spring-boot](https://github.com/zozospider/note-microservice-spring-boot)

> * [Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)

---

## Thymeleaf

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-View/Thymeleaf-process.png)

### Local Demo
> **本地测试案例**

> 新建 `spring-boot-view` 项目，工程结构如下：

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-View/Thymeleaf-Local-Demo-IDEA.png)

> 1. 新建 `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>note-microservice-spring-boot</artifactId>
        <groupId>com.zozospider</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-view</artifactId>

    <dependencies>

        <!-- Web 模块 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Thymeleaf 模板 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>


    </dependencies>

</project>
```

> 2. 新建 `templates\thymeleaf\hello-thymeleaf.html`

```xml
<p th:text="${message}">none</p>
```

> 3. 新建 `ThymeleafTemplateEngine.java`

```java
package com.zozospider.springbootview.template.engine;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ThymeleafTemplateEngine {

    public static void main(String[] args) throws IOException {

        // 构建引擎
        SpringTemplateEngine engin = new SpringTemplateEngine();

        // 创造渲染上下文，设置 message
        Context context = new Context();
        context.setVariable("message", "Hello, Thymeleaf");

        // 从 classpath 读取内容
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:/templates/thymeleaf/hello-thymeleaf.html");

        // 通过文件流获取模板内容
        File file = resource.getFile();
        FileInputStream inputStream = new FileInputStream(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, outputStream);
        inputStream.close();
        String text = outputStream.toString("UTF-8");
        System.out.println("thymeleaf text: " + text);

        // 渲染处理
        String result = engin.process(text, context);
        System.out.println("thymeleaf result: " + result);
    }

}
```

4. 运行结果如下:

```
thymeleaf text: <p th:text="${message}">none</p>

...

thymeleaf result: <p>Hello, Thymeleaf</p>
```

### Runnable Demo
> **完整运行案例**

> 在上文基础上继续开发，工程结构如下：

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-View/Thymeleaf-Runnable-Demo-IDEA.png)

> 1. 新建 `application.properties`

```properties
# Thymeleaf 配置
spring.thymeleaf.prefix = classpath:/templates/thymeleaf/
spring.thymeleaf.suffix = .html

# 取消缓存
spring.thymeleaf.cache = false
```

> 2. 新建 `SpringBootViewBootstrap.java`

```java
package com.zozospider.springbootview.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 视图 引导类
 *
 * @author zozo
 * @since 1.0
 */
@SpringBootApplication(scanBasePackages = "com.zozospider.springbootview")
public class SpringBootViewBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootViewBootstrap.class, args);
    }

}
```

> 3. 新建 `HelloController.java`

```java
package com.zozospider.springbootview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello() {
        return "hello-thymeleaf"; // View 逻辑名称
    }

    @ModelAttribute("message")
    public String message() {
        return "I am message";
    }

}
```

> 4. 新建 `templates\thymeleaf\hello-thymeleaf.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <body>
        <p th:text="${message}">none</p>
        2018
    </body>
</html>
```

> 5. 浏览器访问 http://localhost:8080/hello

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-View/Thymeleaf-Runnable-Demo-Chrome-hello.png)

### Together With JSP
> **与JSP共存**

> 1. 
