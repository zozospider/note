# Spring Boot 2.x Web MVC Core

- [Document & Code](#document--code)
- [DispatcherServlet](#dispatcherservlet)
- [Spring Framework Web MVC Demo](#spring-framework-web-mvc-demo)
- [Spring Framework Web MVC Process](#spring-framework-web-mvc-process)
  - [Core](#core)
  - [Debugger](#debugger)
- [Spring Framework Web MVC Annotation](#spring-framework-web-mvc-annotation)
  - [Demo](#demo)
  - [View Demo](#view-demo)
- [Spring Framework Web MVC Auto Configuration](#spring-framework-web-mvc-auto-configuration)
  - [Demo-1](#demo-1)

---

## Document & Code
> * [github.com: zozospider/note/Microservice/Spring-Boot/Spring-Boot-2.x](https://github.com/zozospider/note/blob/master/Microservice/Spring-Boot/Spring-Boot-2.x.md)

> * [github.com: zozospider/note-microservice-spring-boot](https://github.com/zozospider/note-microservice-spring-boot)

---

## DispatcherServlet
> **前端总控制器**
> * [DispatcherServlet API](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/DispatcherServlet.html)

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/DispatcherServlet-Servlet.png)
![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/DispatcherServlet-Front-Controller.png)
![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/DispatcherServlet-Spring-Web-MVC.png)

---

## Spring Framework Web MVC Demo
> **Spring Web MVC框架案例**

> 1. 配置 `pom.xml` 。
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

    <artifactId>spring-webmvc</artifactId>
    <packaging>war</packaging>

    <dependencies>

        <!-- Servlet 3.1 API 依赖 -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
        </dependency>

        <!-- Spring Web MVC 依赖 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
        </dependency>

    </dependencies>

    <build>

        <plugins>

            <!-- 使用可执行 Tomcat Maven 插件 -->
            <plugin>
                <groupId>org.apache.tomcat.maven</groupId>
                <artifactId>tomcat7-maven-plugin</artifactId>
                <version>2.1</version>
                <executions>
                    <execution>
                        <id>tomcat-run</id>
                        <goals>
                            <goal>exec-war-only</goal>
                        </goals>
                        <phase>package</phase>
                        <configuration>
                            <!-- ServletContext path -->
                            <path>/</path>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

        </plugins>

    </build>

</project>
```

> 2. 新建 `webapp/WEB-INF/web.xml` 。

```xml
<web-app>

    <servlet>
        <servlet-name>app</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/app-context.xml</param-value>
        </init-param>
    </servlet>

    <servlet-mapping>
        <servlet-name>app</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

</web-app>
```

> 3. 新建 `webapp/WEB-INF/app-context.xml` 。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="com.zozospider.springwebmvc"/>

    <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"/>

    <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"/>

    <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

</beans>
```

> 4. 新建 `HelloController` 。

```java
package com.zozospider.springwebmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Hello {@link Controller}
 *
 * @author zozo
 * @since 1.0
 */
@Controller
public class HelloController {

    @RequestMapping("")
    public String index() {
        return "index";
    }

}
```

5. 新建 `webapp/WEB-INF/jsp/index.jsp` 。

```
Hello
```

6. 编译执行。
> Maven: spring-mvc > Lifecycle > clean > compile > package ，得到 `spring-webmvc/target/spring-webmvc-0.0.1-SNAPSHOT-war-exec.jar` 文件。

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Demo-IDEA.png)

7. 运行。
> 进入 Terminal > 执行 `java -jar` 命令运行 `spring-webmvc-0.0.1-SNAPSHOT-war-exec.jar`

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Demo-IDEA-Terminal.png)

8. 访问。
> 浏览器访问 http://localhost:8080/

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Demo-Chrome-hello.png)

---

## Spring Framework Web MVC Process
> **Spring Web MVC 流程**

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Process-process.png)

### Core
> **Spring Web MVC 流程-核心组件**

| 组件 Bean 类型 | 说明 |
| :--- | :--- |
| `HandlerMapping` | ? |
| `HandlerAdapter` | ? | 
| `HandlerExceptionResolver` | ? |
| `ViewResolver` | ? |
| `LocaleResolver, LocaleContextResolver` | ? |
| `MultipartResolver` | ? |

### Debugger
> **Spring Web MVC 流程-调试**

> 1. 调试准备，IDEA 配置 Remote Debugger。

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Process-IDEA-Remote.png)

> 2. 进入 Terminal > 执行命令 `java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar spring-webmvc-0.0.1-SNAPSHOT-war-exec.jar` 

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Process-IDEA-Terminal.png)

> 3. 启动 Remote Debugger，即可开始调试。

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Process-IDEA-Debugger.png)

> 4. 浏览器访问 http://localhost:8080/ ，核心步骤如下，参考[Spring Framework Web MVC Process](#spring-framework-web-mvc-process)的交互流程图。

> * 经过 `DispatcherServlet` 的方法 `doDispatch` 。

```java
package org.springframework.web.servlet;

...

public class DispatcherServlet extends FrameworkServlet {

    ...

    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpServletRequest processedRequest = request;
        HandlerExecutionChain mappedHandler = null;
        boolean multipartRequestParsed = false;

        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

        ...

    }

    ...

}
```

> * 经过 `DispatcherServlet` 的方法 `getHandler` 。

```java
package org.springframework.web.servlet;

...

public class DispatcherServlet extends FrameworkServlet {

    ...

    @Nullable
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        if (this.handlerMappings != null) {
            for (HandlerMapping hm : this.handlerMappings) {
                if (logger.isTraceEnabled()) {
                    logger.trace(
                            "Testing handler map [" + hm + "] in DispatcherServlet with name '" + getServletName() + "'");
                }
                HandlerExecutionChain handler = hm.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }

    ...

}
```

> * 经过 `DispatcherServlet` 的方法 `getHandlerAdapter` 。

```java
package org.springframework.web.servlet;

...

public class DispatcherServlet extends FrameworkServlet {

    ...

    protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        if (this.handlerAdapters != null) {
            for (HandlerAdapter ha : this.handlerAdapters) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Testing handler adapter [" + ha + "]");
                }
                if (ha.supports(handler)) {
                    return ha;
                }
            }
        }
        throw new ServletException("No adapter for handler [" + handler +
                "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
    }

    ...

}
```

> * 经过 `HelloController` 的方法 `index` 。

```java
package com.zozospider.springwebmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Hello {@link Controller}
 *
 * @author zozo
 * @since 1.0
 */
@Controller
public class HelloController {

    @RequestMapping("")
    public String index() {
        return "index";
    }

}
```

> * 经过 `DispatcherServlet` 的方法 `resolveViewName` 。

```java
package org.springframework.web.servlet;

...

public class DispatcherServlet extends FrameworkServlet {

    ...

    @Nullable
    protected View resolveViewName(String viewName, @Nullable Map<String, Object> model,
            Locale locale, HttpServletRequest request) throws Exception {

        if (this.viewResolvers != null) {
            for (ViewResolver viewResolver : this.viewResolvers) {
                View view = viewResolver.resolveViewName(viewName, locale);
                if (view != null) {
                    return view;
                }
            }
        }
        return null;
    }

    ...

}
```

---

## Spring Framework Web MVC Annotation
> **Spring Web MVC 注解**

> Spring Framework Web MVC 也可采用注解方式实现。

### Demo
> **Spring Web MVC 注解-框架案例**

> 1. 注释 `app-context.xml` 中配置的自动注入。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="com.zozospider.springwebmvc"/>

    <!--<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"/>

    <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"/>

    <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>-->

</beans>
```

> 2. 新建 `WebMvcConfig` 注解 `@Configuration` 和 `@EnableWebMvc`，并指定 `ViewResolver`( `viewResolver()` 逻辑在 Spring 启动时执行)。

```java
package com.zozospider.springwebmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
public class WebMvcConfig {

    /*<bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>*/

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

}
```

> 3. 此时浏览器访问 http://localhost:8080/ 可展示页面。

> 4. 实现 `WebMvcConfigurer` 接口，添加拦截器( `registry.addInterceptor(...)` 逻辑在 Spring 启动时执行，且在 `viewResolver()` 之前)。

```java
package com.zozospider.springwebmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    /*<bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>*/

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                System.out.println("WebMvcConfigurer addInterceptors preHandle 拦截...");
                return true;
            }
        });
    }
}
```

> 5. 再次浏览器访问 http://localhost:8080/ ，核心步骤如下，参考[Spring Framework Web MVC Process](#spring-framework-web-mvc-process)的交互流程图。

> * 经过 `DispatcherServlet` 的方法 `doDispatch` 。
> * 经过 `DispatcherServlet` 的方法 `getHandler` 。
> * 经过 `DispatcherServlet` 的方法 `getHandlerAdapter` 。
> * 经过 `WebMvcConfig` 的方法 `addInterceptors` 监听的方法 `preHandle`。
> * 经过 `HelloController` 的方法 `index` 。
> * 经过 `DispatcherServlet` 的方法 `resolveViewName` 。

### View Demo
> **Spring Web MVC 注解-视图案例**

> 1. `Controller`

```java
package com.zozospider.springwebmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Hello {@link Controller}
 *
 * @author zozo
 * @since 1.0
 */
@Controller
public class HelloController {

    /**
     * Request 入口
     * @param host 请求参数（可选项）
     * @param model
     * @return
     */
    @RequestMapping("")
    public String index(@RequestHeader("Host") String host,
                        Model model) {
        System.out.println("Hello RequestHeader Host: " + host);
        // 与 @ModelAttribute(name) 等同
        model.addAttribute("msg", "Hello msg");
        return "index";
    }

    /**
     * @ModelAttribute(name) 等同于 model.addAttribute(attributeName, attributeValue);
     * @return attributeValue
     */
    @ModelAttribute("message")
    public String message() {
        return "Hello message";
    }

    /**
     * @ModelAttribute(name) 等同于 model.addAttribute(attributeName, attributeValue);
     * @param acceptLanguage 请求参数（可选项）
     * @return attributeValue
     */
    @ModelAttribute("acceptLanguageReturn")
    public String acceptLanguageReturn(@RequestHeader("Accept-Language") String acceptLanguage) {
        System.out.println("Hello RequestHeader Accept-Language: " + acceptLanguage);
        return acceptLanguage;
    }

}
```

> 2. `jsp`

```jsp
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0">
    <body>
        <msg>${msg}</msg>
        <message>${message}</message>
        <acceptLanguageReturn>${acceptLanguageReturn}</acceptLanguageReturn>
    </body>
</jsp:root>
```

> 3. 浏览器访问 http://localhost:8080/ ，

```
Hello RequestHeader Accept-Language: zh-CN,zh;q=0.9
Hello RequestHeader Host: localhost:8080
```

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Annotation-Chrome-hello.png)

> 4. `WorldController`

```java
package com.zozospider.springwebmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * World {@link Controller}
 *
 * @author zozo
 * @since 1.0
 */
@Controller
public class WorldController {

    /**
     * Request 入口
     * @param host 请求参数（可选项）
     * @param model
     * @return
     */
    @RequestMapping("/world")
    public String world(@RequestHeader("Host") String host,
                        Model model) {
        System.out.println("World RequestHeader Host: " + host);
        // 与 @ModelAttribute(name) 等同
        model.addAttribute("msg", "World msg");
        return "world";
    }

}
```

> 5. `WorldControllerAdvice`

```java
package com.zozospider.springwebmvc.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * {@link WorldController} 通知
 *
 * @author zozo
 * @since 1.0
 */
@ControllerAdvice(assignableTypes = WorldController.class)
public class WorldControllerAdvice {

    /**
     * @ModelAttribute(name) 等同于 model.addAttribute(attributeName, attributeValue);
     * @return attributeValue
     */
    @ModelAttribute("message")
    public String message() {
        return "World message";
    }

    /**
     * @ModelAttribute(name) 等同于 model.addAttribute(attributeName, attributeValue);
     * @param acceptLanguage 请求参数（可选项）
     * @return attributeValue
     */
    @ModelAttribute("acceptLanguageReturn")
    public String acceptLanguageReturn(@RequestHeader("Accept-Language") String acceptLanguage) {
        System.out.println("World RequestHeader Accept-Language: " + acceptLanguage);
        return acceptLanguage;
    }

}
```

> 6. 浏览器访问 http://localhost:8080/world

```
World RequestHeader Accept-Language: zh-CN,zh;q=0.9
World RequestHeader Host: localhost:8080
```

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Annotation-Chrome-world.png)

> 7. `ExceptionController`

```java
package com.zozospider.springwebmvc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * {@link ExceptionHandler} demo
 *
 * @author zozo
 * @since 1.0
 */
@Controller
public class ExceptionController {

    @RequestMapping("/ex")
    public String ex(@RequestParam int name,
                        Model model) {
        System.out.println("ex");
        return "ex";
    }

    /**
     * 发生异常时处理
     * @param throwable 异常类型
     * @return 返回实体对象
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> onException(Throwable throwable) {
        System.out.println("onException: " + throwable.getMessage());
        return ResponseEntity.ok("ex error: " + throwable.getMessage());
    }

}
```

> 8. 浏览器访问 http://localhost:8080/ex

```
onException: Required int parameter 'name' is not present
```

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Annotation-Chrome-ex.png)

## Spring Framework Web MVC Auto Configuration
> **Spring Web Mvc 自动装配**

### Demo-1
> **实现自动装配案例**

> 工程整体结构如下:

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Auto-Configuration-IDEA.png)

> 1. 注释 `web.xml`

```xml
<web-app>

    <!--<servlet>
        <servlet-name>app</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/app-context.xml</param-value>
        </init-param>
    </servlet>

    <servlet-mapping>
        <servlet-name>app</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>-->

</web-app>
```

2. 注释 `app-context.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!--<context:component-scan base-package="com.zozospider.springwebmvc"/>-->

    <!--<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"/>

    <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"/>

    <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>-->

</beans>
```

> 3. 新建 `DispatcherServletConfiguration`

```java
package com.zozospider.springwebmvc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * {@link DispatcherServlet} 配置类
 *
 * @author zozo
 * @since 1.0
 */
@ComponentScan(basePackages = "com.zozospider.springwebmvc")
public class DispatcherServletConfiguration {

    /*<context:component-scan base-package="com.zozospider.springwebmvc"/>*/

}
```

> 4. 新建 `WebMvcConfig` （上文已配置[Demo](#demo)）

```java
package com.zozospider.springwebmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    /*<bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>*/

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                System.out.println("WebMvcConfigurer addInterceptors preHandle 拦截...");
                return true;
            }
        });
    }
}
```

> 5. 新建 `DefaultAnnotationConfigDispatcherServletInitializer`

```java
package com.zozospider.springwebmvc.servlet.support;

import com.zozospider.springwebmvc.config.DispatcherServletConfiguration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Spring Web MVC 自动装配 默认实现
 *
 * @author zozo
 * @since 1.0
 */
public class DefaultAnnotationConfigDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    // web.xml
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[0];
    }

    // DispatcherServlet配置类（com.zozospider.springwebmvc.config.DispatcherServletConfiguration）
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{DispatcherServletConfiguration.class};
    }

    /*<servlet-mapping>
        <servlet-name>app</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>*/
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

}
```

> 6. 编译打包并启动项目，浏览器访问 http://localhost:8080/

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Auto-Configuration-Chrome-hello.png)

