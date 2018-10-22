# Spring Boot 2.x Web MVC Core

- [DispatcherServlet](#dispatcherservlet)
- [Spring Framework Web MVC Demo](#spring-framework-web-mvc-demo)
- [Spring Framework Web MVC Process](#spring-framework-web-mvc-process)

## DispatcherServlet
> **前端总控制器**
> * [DispatcherServlet API](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/DispatcherServlet.html)

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/DispatcherServlet-Servlet.png)
![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/DispatcherServlet-Front-Controller.png)
![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/DispatcherServlet-Spring-Web-MVC.png)

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
> 浏览器访问 `http://localhost:8080/`

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Demo-Chrome-hello.png)

## Spring Framework Web MVC Process
> **Spring Web MVC 流程**

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Process-process.png)

### Core

| 组件 Bean 类型 | 说明 |
| :--- | :--- |
| `HandlerMapping` | ? |
| `HandlerAdapter` | ? | 
| `HandlerExceptionResolver` | ? |
| `ViewResolver` | ? |
| `LocaleResolver, LocaleContextResolver` | ? |
| `MultipartResolver` | ? |

### Debugger

> 1. 调试准备，IDEA 配置 Remote Debugger。

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Process-IDEA-Remote.png)

> 2. 进入 Terminal > 执行命令 `java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar spring-webmvc-0.0.1-SNAPSHOT-war-exec.jar` 

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Process-IDEA-Terminal.png)

> 3. 启动 Remote Debugger，即可开始调试。

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Core/Spring-Framework-Web-MVC-Process-IDEA-Debugger.png)

> 4. 访问 http://localhost:8080/ ，核心步骤如下，参考[Spring Framework Web MVC Process](#spring-framework-web-mvc-process)的交互流程图。

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

