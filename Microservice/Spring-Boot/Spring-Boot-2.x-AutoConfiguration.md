# Spring Boot 2.x AutoConfiguration

## Spring 模式注解装配
> [Spring Annotation Programming Model](https://github.com/spring-projects/spring-framework/wiki/Spring-Annotation-Programming-Model#stereotype-annotations)

> * A stereotype annotation is an annotation that is used to declare the role that a component plays within the application. For example, the `@Repository` annotation in the Spring Framework is a marker for any class that fulfills the role or stereotype of a repository (also known as Data Access Object or DAO).
> * `@Component` is a generic stereotype for any Spring-managed component. Any component annotated with `@Component` is a candidate for component scanning. Similarly, any component annotated with an annotation that is itself meta-annotated with `@Component` is also a candidate for component scanning. For example, `@Service` is meta-annotated with `@Component`.

> * 模式注解是一种用于声明在应用中扮演“组件”角色的注解。如Spring Framework中的`@Repository`标注在任何类上，用于扮演仓储角色的模式注解。
> * `@Component`作为一种由Spring容器托管的通用模式组件，任何被`@Component`标准的组件均为组件扫描的候选对象。类似地，凡是被`@Component`元标注（meta-annotated）的注解，如`@Service`，当任何组件标注它时，也被视作组件扫描的候选对象。

### Spring 模式注解装配 -> 举例

| Spring Framework 注解 | 场景说明 | 起始版本 |
| :--- | :--- | :--- |
| `@Component` | 通用组件模式注解 | 2.5 |
| `@Repository` | 数据仓储模式注解 | 2.0 |
| `@Service` | 服务模式注解 | 2.5 |
| `@Controller` | Web控制器模式注解 | 2.5 |
| `@Configuration` | 配置类模式注解 | 3.0 |

### Spring 模式注解装配 -> 装配方式: @ComponentScan & <context:component-scan>
> [Spring Framework 5.1.0 Classpath Scanning and Managed Components](https://docs.spring.io/spring/docs/5.1.0.RELEASE/spring-framework-reference/core.html#beans-classpath-scanning)

> * 要自动检测这些类并注册相应的bean，您需要将 `@ComponentScan` 添加到 `@Configuration` 类
```java
@Configuration
@ComponentScan(basePackages = "org.example")
public class AppConfig  {
    ...
}
```
> 等价xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 激活注解驱动特性 -->
    <context:annotation-config>

    <!-- 找寻被 @Component 或者其派生 Annotation 标记的类（Class），将它们注册为 Spring Bean -->
    <context:component-scan base-package="org.example"/>

</beans>
```

> * 以下示例显示忽略所有@Repository注释并使用“存根”存储库的配置
```java
@Configuration
@ComponentScan(basePackages = "org.example",
        includeFilters = @Filter(type = FilterType.REGEX, pattern = ".*Stub.*Repository"),
        excludeFilters = @Filter(Repository.class))
public class AppConfig {
    ...
}
```
> 等价xml
```xml
<beans>
    <context:component-scan base-package="org.example">
        <context:include-filter type="regex"
                expression=".*Stub.*Repository"/>
        <context:exclude-filter type="annotation"
                expression="org.springframework.stereotype.Repository"/>
    </context:component-scan>
</beans>
```

### Spring 模式注解装配 -> 自定义
> 定义注解 `@FirstLevelRepository` ，从 `@Repository` 派生，其中， `@Repository` 从 `@Component` 派生。
> `@FirstLevelRepository` > `@Repository` > `@Component`
```java
import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

/**
 * 一级 {@link Repository @Repository}
 *
 * @author zoz
 * @since 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repository
public @interface FirstLevelRepository {

    String value() default "";

}
```
> 定义注解 `@SecondLevelRepository` ，从 `@FirstLevelRepository` 派生。
> `@SecondLevelRepository` > `@FirstLevelRepository` > `@Repository` > `@Component`
```java
import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

/**
 * 二级 {@link Repository}
 *
 * @author zoz
 * @since 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FirstLevelRepository
public @interface SecondLevelRepository {

    String value() default "";

}
```
> 定义类引用注解 `@FirstLevelRepository`
```java
package com.imooc.diveinspringboot.configuration.repository;

import com.imooc.diveinspringboot.configuration.annotation.FirstLevelRepository;

@FirstLevelRepository(value = "myFirstLevelRepository")
public class MyFirstLevelRepository {

}
```
> 定义类引用注解 `@SecondLevelRepository`
```java
package com.imooc.diveinspringboot.configuration.repository;

import com.imooc.diveinspringboot.configuration.annotation.SecondLevelRepository;

@SecondLevelRepository(value = "mySecondLevelRepository")
public class MySecondLevelRepository {
    
}

```
> 启动类，通过 `@ComponentScan` 扫描所有引用了注解 `@Component` 或其派生的类，并获取类的实体。
```java
import com.imooc.diveinspringboot.configuration.repository.MyFirstLevelRepository;
import com.imooc.diveinspringboot.configuration.repository.MySecondLevelRepository;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.imooc.diveinspringboot.configuration.repository")
public class RepositoryBootstrap {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(RepositoryBootstrap.class)
                .web(WebApplicationType.NONE)
                .run(args);

        MyFirstLevelRepository myFirstLevelRepository = context.getBean("myFirstLevelRepository", MyFirstLevelRepository.class);
        MySecondLevelRepository mySecondLevelRepository = context.getBean("mySecondLevelRepository", MySecondLevelRepository.class);

        System.out.println("myFirstLevelRepository Bean: " + myFirstLevelRepository);
        System.out.println("mySecondLevelRepository Bean: " + mySecondLevelRepository);

        // 关闭上下文
        context.close();

    }

}
```

## Spring 模式 @Enable 模块装配
> Spring Framework 3.1 开始支持 `@Enable` 模块装配，即将具有相同领域的功能组件集合，组合成一个独立的单元。

### Spring 模式 @Enable 模块装配 -> 举例

| 框架实现 | `@Enable` 注解模块 | 激活模块 |
| :--- | :--- | :--- |
| Spring Framework | `@EnableWebMvc` | Web MVC 模块 |
|  | `@EnableTransactionManagement` | 事务管理模块 |
|  | `@EnableCaching` | Caching 模块 |
|  | `@EnableMBeanExport` | JMX 模块 |
|  | `@EnableAsync` | 异步处理模块 |
|  | `@EnableWebFlux` | Web Flux 模块 |
|  | `@EnableAspectJAutoProxy` | AspectJ 代理模块 |
|  |  |  |
| Spring Boot | `@EnableAutoConfiguration` | 自动装配模块 |
|  | `@EnableManagementContext` | Actuator 模块 |
|  | `@EnableConfigurationProperties` | 配置属性绑定模块 |
|  | `@EnableOAuth2Sso` | OAuth2 单点登录模块 |
| Spring Cloud |  |  |
|  | `@EnableEurekaServer` | Eureka 服务器模块 |
|  | `@EnableConfigServer` | 配置服务器模块 |
|  | `@EnableFeignClients` | Feign 客户端模块 |
|  | `@EnableZuulProxy` | 服务网关 Zuul 模块 |
|  | `@EnableCircuitBreaker` | 服务熔断模块 |

### Spring 模式 @Enable 模块装配 -> 实现方式: 注解驱动(Configuration @since 3.0)
> * Spring Framework `@EnableWebMvc` 导入 `DelegatingWebMvcConfiguration`
```java
package org.springframework.web.servlet.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DelegatingWebMvcConfiguration.class)
public @interface EnableWebMvc {

}
```
> `DelegatingWebMvcConfiguration` 添加 `@Configuration` 注解
```java
@Configuration
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {

    private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();

    @Autowired(required = false)
    public void setConfigurers(List<WebMvcConfigurer> configurers) {
        if (!CollectionUtils.isEmpty(configurers)) {
            this.configurers.addWebMvcConfigurers(configurers);
        }
    }

    @Override
    protected void configurePathMatch(PathMatchConfigurer configurer) {
        this.configurers.configurePathMatch(configurer);
    }

    ...

}
```
> * 自定义 `@EnableHelloConfiguration` 导入 `HelloConfiguration`
```java
import com.imooc.diveinspringboot.configuration.configuration.HelloConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 激活 Hello 模块
 *
 * @author zozo
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HelloConfiguration.class)
public @interface EnableHelloConfiguration {

}
```
> `HelloConfiguration` 添加 `@Configuration` 注解
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Hello 配置
 *
 * @author zozo
 * @since 1.0
 */
@Configuration
public class HelloConfiguration {

    @Bean
    public String hello() { // 方法名即 Bean 名称
        return "Hello Bean";
    }

}
```
> 引用 & 运行结果
```java
import com.imooc.diveinspringboot.configuration.annotation.EnableHelloConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@EnableHelloConfiguration
public class EnableHelloConfigurationBootstrap {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(EnableHelloConfigurationBootstrap.class)
                .web(WebApplicationType.NONE)
                .run(args);

        // 查找 Bean
        String hello = context.getBean("hello", String.class);

        System.out.println("hello Bean: " + hello);

        // 关闭上下文
        context.close();

    }

}
```
```
hello Bean: Hello Bean
```

### Spring 模式 @Enable 模块装配 -> 实现方式: 接口编程(ImportSelector @since 3.1)
> * Spring Framework `@EnableCaching` 导入 `CachingConfigurationSelector`
```java
package org.springframework.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CachingConfigurationSelector.class)
public @interface EnableCaching {

    boolean proxyTargetClass() default false;

    AdviceMode mode() default AdviceMode.PROXY;

    int order() default Ordered.LOWEST_PRECEDENCE;

}
```
> `CachingConfigurationSelector` 继承 `AdviceModeImportSelector` 实现 `ImportSelector` 接口
```java
public class CachingConfigurationSelector extends AdviceModeImportSelector<EnableCaching> {

    private static final String PROXY_JCACHE_CONFIGURATION_CLASS =
            "org.springframework.cache.jcache.config.ProxyJCacheConfiguration";

    private static final String CACHE_ASPECT_CONFIGURATION_CLASS_NAME =
            "org.springframework.cache.aspectj.AspectJCachingConfiguration";

    ...

    @Override
    public String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY:
                return getProxyImports();
            case ASPECTJ:
                return getAspectJImports();
            default:
                return null;
        }
    }

    private String[] getProxyImports() {
        List<String> result = new ArrayList<>(3);
        result.add(AutoProxyRegistrar.class.getName());
        result.add(ProxyCachingConfiguration.class.getName());
        if (jsr107Present && jcacheImplPresent) {
            result.add(PROXY_JCACHE_CONFIGURATION_CLASS);
        }
        return StringUtils.toStringArray(result);
    }

    ...

}
```

> * 自定义 `@EnableHelloImportSelector` 导入 `HelloImportSelector`
```java
import com.imooc.diveinspringboot.configuration.configuration.HelloImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 激活 Hello 模块
 *
 * @author zozo
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HelloImportSelector.class)
public @interface EnableHelloImportSelector {

}
```
> `HelloImportSelector` 实现 `ImportSelector` 接口
```java
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Hello {@link ImportSelector} 实现
 *
 * @author zozo
 * @since 1.0
 */
public class HelloImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{HelloConfiguration.class.getName()};
    }

}
```
> 引用 & 运行结果
```java
import com.imooc.diveinspringboot.configuration.annotation.EnableHelloImportSelector;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@EnableHelloImportSelector
public class EnableHelloImportSelectorBootstrap {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(EnableHelloImportSelectorBootstrap.class)
                .web(WebApplicationType.NONE)
                .run(args);

        // 查找 Bean
        String hello = context.getBean("hello", String.class);

        System.out.println("hello Bean: " + hello);

        // 关闭上下文
        context.close();
    }

}
```
```
hello Bean: Hello Bean
```

## Spring 条件装配
> Spring Framework 3.1 支持，允许在 Bean 装配时增加前置条件判断。

### Spring 条件装配 -> 举例

| Spring 注解 | 场景说明 | 起始版本 |
| :--- | :--- | :--- |
| `@Profile` | 配置条件装配 | 3.1 |
| `@Conditional` | 编程条件装配 | 4.0 |

### Spring 条件装配 -> 实现方式: 配置方式(@Profile)
> * 自定义 `@EnableHelloImportSelector`

### Spring 条件装配 -> 实现方式: 编程方式(@Conditional)


aaa
bbb
