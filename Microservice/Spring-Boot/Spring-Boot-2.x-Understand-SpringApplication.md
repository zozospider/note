# Spring Boot 2.x Understand SpringApplication

## Document
> [SpringApplication](https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#boot-features-spring-application)

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

## 测试
