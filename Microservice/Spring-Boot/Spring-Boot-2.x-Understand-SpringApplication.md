# Spring Boot 2.x Understand SpringApplication

## Document & Code
> * [SpringApplication](https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#boot-features-spring-application)
> * [Github: zozospider/note-microservice-spring-boot](https://github.com/zozospider/note-microservice-spring-boot)

## SpingApplication 基本使用 [more](https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#boot-features-spring-application)
> * The `SpringApplication` class provides a convenient way to bootstrap a Spring application that is started from a main() method. In many situations, you can delegate to the static `SpringApplication.run` method, as shown in the following example:

```java
public static void main(String[] args) {
	SpringApplication.run(MySpringConfiguration.class, args);
}
```

## 自定义 SpringApplication [more](https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#boot-features-customizing-spring-application)
> * If the SpringApplication defaults are not to your taste, you can instead create a local instance and customize it. For example, to turn off the banner, you could write:

```java
public static void main(String[] args) {
	SpringApplication app = new SpringApplication(MySpringConfiguration.class);
	app.setBannerMode(Banner.Mode.OFF);
	app.run(args);
}
```

## 流式构建(Fluent Builder API) [more](https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#boot-features-fluent-builder-api)
> * If you need to build an `ApplicationContext` hierarchy (multiple contexts with a parent/child relationship) or if you prefer using a “fluent” builder API, you can use the `SpringApplicationBuilder`.
> * The `SpringApplicationBuilder` lets you chain together multiple method calls and includes `parent` and `child` methods that let you create a hierarchy, as shown in the following example:

```java
new SpringApplicationBuilder()
		.sources(Parent.class)
		.child(Application.class)
		.bannerMode(Banner.Mode.OFF)
		.run(args);
```

## 本地启动测试
> 1. 通过 `SpringApplication.run(Class<?> primarySource, String... args)` 指定本类为 `primarySource` 参数。

```java
package com.zozospider.springapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * {@link SpringApplication} 引导类
 *
 * @author zozo
 * @since 1.0
 */
@SpringBootApplication
public class SpringApplicationBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(SpringApplicationBootstrap.class, args);
    }

}
```

> 2. 通过 `SpringApplication.run(Class<?> primarySource, String... args)` 指定其他类为 `primarySource` 参数 。

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * {@link SpringApplication} 引导类
 *
 * @author zozo
 * @since 1.0
 */
public class SpringApplicationBootstrap2 {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfiguration.class, args);
    }

    @SpringBootApplication
    public static class ApplicationConfiguration {

    }

}
```

> 3. 通过 `new SpringApplication().setSources(Set<String> sources)` 指定其他类为 `Set<String> sources` 参数 & 运行结果。

```java
package com.zozospider.springapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link SpringApplication} 引导类
 *
 * @author zozo
 * @since 1.0
 */
public class SpringApplicationBootstrap3 {

    public static void main(String[] args) {

        // 配置class名称
        Set<String> sources = new HashSet();
        sources.add(ApplicationConfiguration.class.getName());

        SpringApplication springApplication = new SpringApplication();
        springApplication.setSources(sources);
        // 运行并获取上下文
        ConfigurableApplicationContext context = springApplication.run(args);
        System.out.println("Bean: " + context.getBean(ApplicationConfiguration.class));

    }

    @SpringBootApplication
    public static class ApplicationConfiguration {

    }

}
```
```
Bean: com.zozospider.springapplication.SpringApplicationBootstrap3$ApplicationConfiguration$$EnhancerBySpringCGLIB$$b75f6e8c@4d518b32
```

