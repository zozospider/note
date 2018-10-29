# Spring Boot 2.x Web MVC Rest

- [Document & Code](#document--code)

---

## Document & Code
> * [github.com: zozospider/note/Microservice/Spring-Boot/Spring-Boot-2.x](https://github.com/zozospider/note/blob/master/Microservice/Spring-Boot/Spring-Boot-2.x.md)

> * [github.com: zozospider/note-microservice-spring-boot](https://github.com/zozospider/note-microservice-spring-boot)

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
| :--- | :--- | :--- |
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

