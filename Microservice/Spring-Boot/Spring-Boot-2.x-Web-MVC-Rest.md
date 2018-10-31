# Spring Boot 2.x Web MVC Rest

- [Document & Code](#document--code)

---

## Document & Code
> * [../Spring-Boot-2.x](https://github.com/zozospider/note/blob/master/Microservice/Spring-Boot/Spring-Boot-2.x.md)

> * [code: zozospider/note-microservice-spring-boot](https://github.com/zozospider/note-microservice-spring-boot)

---

## Rest Introduction
> **Rest 简介**

> REST = RESTful = Representational State Transfer, is one way of providing interoperability between computer systems on the Internet.

> * 架构约束
> 1. 统一接口（Uniform interface）
> 2. C/S 架构（Client-Server）
> 3. 无状态（Stateless）
> 4. 可缓存（Cacheable）
> 5. 分层系统（Layered System）
> 6. 按需代码（Code on demand）

### Uniform interface
> **统一接口**

> 1. 资源识别（Identification of resources）
> * URI (Uniform Resource Identifier)

> 2. 资源操作（Manipulation of resouce through representations）
> * HTTP verbs: GET, PUT, POST, DELETE

> 3. 自描述消息（Self-descriptive messages）
> * Content-Type
> * MIME-Type
> * Media Type: application/javascript, text/html

> 4. 超媒体（HATEOAS）
> * Hypermedia As The Engine Of Application State

## Spring Framework Web MVC Rest
> **Spring Framework Web MVC Rest 支持**

| 范围 | 注解 | 说明 | Spring Framework 版本 |
| :--- | :--- | :--- | :--- |
| 定义 | `@Controller` | 应用控制器注解声明，Spring 模式注解 | 2.5 + |
|  | `@RestController` | 等效于 `@Controller` + `@ResponseBody` | 4.0 + |
|  |  |  |  |
| 映射 | `@RequestMapping` | 应用控制器映射注解声明 | 2.5 + |
|  | `@GetMapping` | GET 方法映射，等效于 `@RequestMapping(method = RequestMethod.GET)` | 4.3 + |
|  | `@PostMapping` | POST 方法映射，等效于 `@RequestMapping(method = RequestMethod.POST)` | 4.3 + |
|  | `@PutMapping` | PUT 方法映射，等效于 `@RequestMapping(method = RequestMethod.PUT)` | 4.3 + |
|  | `@DeleteMapping` | DELETE 方法映射，等效于 `@RequestMapping(method = RequestMethod.DELETE)` | 4.3 + |
|  | `@GetMapping` | GET 方法映射，等效于 `@RequestMapping(method = RequestMethod.GET)` | 4.3 + |
|  | `@PatchMapping` | PATCH 方法映射，等效于 `@RequestMapping(method = RequestMethod.PATCH)` | 4.3 + |
|  |  |  |  |
| 请求 | `@RequestParam` | 获取请求参数 | 2.5 + |
|  | `@RequestHeader` | 获取请求头 | 3.0 + |
|  | `@CookieValue` | 获取Cookie值 | 3.0 + |
|  | `@RequestBody` | 获取完整请求主体内容 | 3.0 + |
|  | `@PathVariable` | 获取请求路径变量 | 3.0 + |
|  | `RequestEntity` | 获取请求内容（包括请求主体和请求头） | 4.1 + |
|  |  |  |  |
| 响应 | `@ResponseBody` | 响应主题注解声明 | 2.5 + |
|  | `ResponseEntity` | 响应内容（包括响应主体和响应头） | 3.0.2 + |
|  | `ResponseCookie` | 响应 Cookie 内容 | 5.0 + |
|  |  |  |  |
| 拦截 | `@RestControllerAdvice` | `@RestController` 注解切面通知 |  4.3 + |
|  | HandlerInterceptor | 处理方法拦截器 | 1.0 |
|  |  |  |  |
| 跨域 | `@CrossOrigin` | 资源跨域声明注解 | 4.2 + |
|  | `CorsFilter` | 资源跨域拦截器 | 4.2 + |
|  | `WebMvcConfigurer#addCorsMappings` | 注册资源跨域信息 | 4.2 + |

### process
> **Spring Framework Web MVC Rest 处理流程**

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Web-MVC-Rest/Spring-Framework-Web-MVC-Rest-process.png)

> 新建一个 `HelloRestController` 的接口

```java
package com.zozospider.springbootrest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello {@link RestController} 实现
 *
 * @author zozo
 * @since 1.0
 */
@RestController
public class HelloRestController {

    @GetMapping(value = "hello")
    public String hello(@RequestParam(required = false) String msg) {
        System.out.println("msg: " + msg);
        return "hello: " + msg;
    }

}
```

> 访问 http://localhost:8080/hello?msg=abc ，具体流程如下：

> 1. `DispatcherServlet` 类的 `initHandlerMappings(ApplicationContext context)` 方法。

```java
package org.springframework.web.servlet;

...

public class DispatcherServlet extends FrameworkServlet {

    ...

    private void initHandlerMappings(ApplicationContext context) {
		this.handlerMappings = null;

		if (this.detectAllHandlerMappings) {
			// Find all HandlerMappings in the ApplicationContext, including ancestor contexts.
			Map<String, HandlerMapping> matchingBeans =
					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerMappings = new ArrayList<>(matchingBeans.values());
				// We keep HandlerMappings in sorted order.
				AnnotationAwareOrderComparator.sort(this.handlerMappings);
			}
		}
		else {
			try {
				HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
				this.handlerMappings = Collections.singletonList(hm);
			}
			catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerMapping later.
			}
		}

		// Ensure we have at least one HandlerMapping, by registering
		// a default HandlerMapping if no other mappings are found.
		if (this.handlerMappings == null) {
			this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
			if (logger.isDebugEnabled()) {
				logger.debug("No HandlerMappings found in servlet '" + getServletName() + "': using default");
			}
		}
	}

    ...

}
```

> 2. `DispatcherServlet` 类的 `doDispatch(HttpServletRequest request, HttpServletResponse response)` 方法。



DispatcherServlet 类
    1. initHandlerMappings
    2. doDispatch
        3. 调用 getHandler 方法
        4. 调用 getHandlerAdapter 方法
        5. 执行 handle 方法
            AbstractHandlerMethodAdapter 类
                    handle
                        6. 执行 handleInternal 方法
                            RequestMappingHandlerAdapter 类
                                7. 执行 handleInternal 方法
                                8. 执行 invokeHandlerMethod 方法
                                    ServletInvocableHandlerMethod 类
                                        执行 invokeAndHandle 方法
                                            InvocableHandlerMethod 类
                                                执行 invokeForRequest 方法
                                                    执行 getMethodArgumentValues 方法
                                                        执行 resolveProvidedArgument 方法
                                                        执行 supportsParameter 方法
                                                            HandlerMethodArgumentResolverComposite 类
                                                                执行 supportsParameter 方法
                                                                    执行 getArgumentResolver 方法
                                                        执行 resolveArgument 方法
                                                            HandlerMethodArgumentResolverComposite 类
                                                                执行 resolveArgument 方法
                                                                    执行 resolveArgument 方法
                                                                        AbstractNamedValueMethodArgumentResolver 类
                                                                            执行  resolveArgument 方法
                                                执行 doInvoke 方法
                                                    执行 invoke 方法
                                                        Method 类
                                                            9. 执行 invoke 方法（类，方法参数带上）
                                                                HelloRestController 类
                                                                    执行 hello 方法
                                        执行 handleReturnValue 方法
                                            执行 handleReturnValue 方法
                                                HandlerMethodReturnValueHandlerComposite 类
                                                    执行 handleReturnValue 方法
                                                        执行 selectHandler 方法
