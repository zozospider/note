# Spring Boot 2.x Auto-configuration

- [Document & Code](#document--code)
- [Spring Annotation Programming Model](#spring-annotation-programming-model)
  - [Example](#example)
  - [Classpath Scanning and Managed Components](#classpath-scanning-and-managed-components)
  - [Customizing](#customizing)
- [The @Enable Annotation](#the-enable-annotation)
  - [Example](#example-1)
  - [Annotation](#annotation)
    - [Spring Framework Example](#spring-framework-example)
    - [Customizing](#customizing-1)
  - [Programming ImportSelector](#programming-importselector)
    - [Spring Framework Example](#spring-framework-example-1)
    - [Customizing](#customizing-2)
- [Conditionally Configuration](#conditionally-configuration)
  - [Example](#example-2)
  - [@Profile annotation](#profile-annotation)
  - [@Conditional annotation](#conditional-annotation)
- [Auto-configuration](#auto-configuration)
  - [Realize by](#realize-by)
  - [Example](#example-3)
  - [Customizing](#customizing-3)

## Document & Code
> * [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/)
> * [Github: zozospider/note-microservice-spring-boot](https://github.com/zozospider/note-microservice-spring-boot)

---

## Spring Annotation Programming Model
> **Spring 模式注解装配**
> * [Spring Annotation Programming Model](https://github.com/spring-projects/spring-framework/wiki/Spring-Annotation-Programming-Model#stereotype-annotations)

> * A stereotype annotation is an annotation that is used to declare the role that a component plays within the application. For example, the `@Repository` annotation in the Spring Framework is a marker for any class that fulfills the role or stereotype of a repository (also known as Data Access Object or DAO).
> * `@Component` is a generic stereotype for any Spring-managed component. Any component annotated with `@Component` is a candidate for component scanning. Similarly, any component annotated with an annotation that is itself meta-annotated with `@Component` is also a candidate for component scanning. For example, `@Service` is meta-annotated with `@Component`.

> * 模式注解是一种用于声明在应用中扮演“组件”角色的注解。如Spring Framework中的`@Repository`标注在任何类上，用于扮演仓储角色的模式注解。
> * `@Component`作为一种由Spring容器托管的通用模式组件，任何被`@Component`标准的组件均为组件扫描的候选对象。类似地，凡是被`@Component`元标注（meta-annotated）的注解，如`@Service`，当任何组件标注它时，也被视作组件扫描的候选对象。

### Example
> **举例**

| Spring Framework 注解 | 场景说明 | 起始版本 |
| :--- | :--- | :--- |
| `@Component` | 通用组件模式注解 | 2.5 |
| `@Repository` | 数据仓储模式注解 | 2.0 |
| `@Service` | 服务模式注解 | 2.5 |
| `@Controller` | Web控制器模式注解 | 2.5 |
| `@Configuration` | 配置类模式注解 | 3.0 |

### Classpath Scanning and Managed Components
> **Spring 模式注解装配 -> 装配方式: @ComponentScan & <context:component-scan>**
> * [Spring Framework 5.1.0 Classpath Scanning and Managed Components](https://docs.spring.io/spring/docs/5.1.0.RELEASE/spring-framework-reference/core.html#beans-classpath-scanning)

> * 要自动检测这些类并注册相应的bean，您需要将 `@ComponentScan` 添加到 `@Configuration` 类。

```java
@Configuration
@ComponentScan(basePackages = "org.example")
public class AppConfig  {
    ...
}
```

> 等价xml。

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

> * 以下示例显示忽略所有@Repository注释并使用“存根”存储库的配置。

```java
@Configuration
@ComponentScan(basePackages = "org.example",
        includeFilters = @Filter(type = FilterType.REGEX, pattern = ".*Stub.*Repository"),
        excludeFilters = @Filter(Repository.class))
public class AppConfig {
    ...
}
```

> 等价xml。

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

### Customizing
> **Spring 模式注解装配 -> 自定义**

> 1. 定义注解 `@FirstLevelRepository` ，从 `@Repository` 派生，其中， `@Repository` 从 `@Component` 派生。
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

> 2. 定义注解 `@SecondLevelRepository` ，从 `@FirstLevelRepository` 派生。
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

> 3. 定义类引用注解 `@FirstLevelRepository` 。

```java
package com.imooc.diveinspringboot.configuration.repository;

import com.imooc.diveinspringboot.configuration.annotation.FirstLevelRepository;

@FirstLevelRepository(value = "myFirstLevelRepository")
public class MyFirstLevelRepository {

}
```

> 4. 定义类引用注解 `@SecondLevelRepository` 。

```java
package com.imooc.diveinspringboot.configuration.repository;

import com.imooc.diveinspringboot.configuration.annotation.SecondLevelRepository;

@SecondLevelRepository(value = "mySecondLevelRepository")
public class MySecondLevelRepository {
    
}

```

> 5. 启动类 & 运行结果
> 通过 `@ComponentScan` 扫描所有引用了注解 `@Component` 或其派生的类，并获取类的实体。

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
```
myFirstLevelRepository Bean: com.imooc.diveinspringboot.configuration.repository.MyFirstLevelRepository@4d02f94e
mySecondLevelRepository Bean: com.imooc.diveinspringboot.configuration.repository.MySecondLevelRepository@2b48a640
```

---

## The @Enable Annotation
> **Spring 模式 @Enable 模块装配**

> Spring Framework 3.1 开始支持 `@Enable` 模块装配，即将具有相同领域的功能组件集合，组合成一个独立的单元。

### Example
> **Spring 模式 @Enable 模块装配 -> 举例**

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

### Annotation
> **Spring 模式 @Enable 模块装配 -> 实现方式: 注解驱动(Configuration @since 3.0)**

#### Spring Framework Example
> **Spring Framework 实现**

> 1. `@EnableWebMvc` 导入 `DelegatingWebMvcConfiguration` 。

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

> 2. `DelegatingWebMvcConfiguration` 添加 `@Configuration` 注解。

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

#### Customizing
> **自定义**

> 1. `@EnableHelloConfiguration` 导入 `HelloConfiguration` 。

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

> 2. `HelloConfiguration` 添加 `@Configuration` 注解。

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

> 3. 引用 & 运行结果。

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

### Programming ImportSelector
> **Spring 模式 @Enable 模块装配 -> 实现方式: 接口编程(ImportSelector @since 3.1)**

#### Spring Framework Example
> **Spring Framework 实现**

> 1. `@EnableCaching` 导入 `CachingConfigurationSelector` 。

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

> 2. `CachingConfigurationSelector` 继承 `AdviceModeImportSelector` 实现 `ImportSelector` 接口。

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

#### Customizing
> **自定义实现**

> 1. `@EnableHelloImportSelector` 导入 `HelloImportSelector` 。

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

> 2. `HelloImportSelector` 实现 `ImportSelector` 接口。

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

> 3. 引用 & 运行结果。

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

---

## Conditionally Configuration
> **Spring 条件装配**

> Spring Framework 3.1 支持，允许在 Bean 装配时增加前置条件判断。

### Example
> **Spring 条件装配 -> 举例**

| Spring 注解 | 场景说明 | 起始版本 |
| :--- | :--- | :--- |
| `@Profile` | 配置条件装配 | 3.1 |
| `@Conditional` | 编程条件装配 | 4.0 |

### @Profile annotation
> **Spring 条件装配 -> 实现方式: 配置方式(@Profile)**

> * 自定义 `@Profile` 实现

> 1. 定义计算服务接口

```java
/**
 * 计算服务
 *
 * @author zozo
 * @since 1.0
 */
public interface CalculateService {

    /**
     * 求多个整数的和
     * @param values 多个整数
     * @return 累加值
     */
    Integer sum(Integer... values);

}
```

> 2. 定义接口实现（Java7）

```java
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Java 7 for 循环实现 {@link CalculateService}
 *
 * @author zozo
 * @since 1.0
 */
@Profile("Java7")
@Service
public class Java7CalculateServiceImpl implements CalculateService {

    @Override
    public Integer sum(Integer... values) {
        System.out.println("Java 7 for 循环实现求和");
        int sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return sum;
    }

}
```

> 3. 定义接口实现（Java8）

```java
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * Java 8 Lambda 实现 {@link CalculateService}
 *
 * @author zozo
 * @since 1.0
 */
@Profile("Java8")
@Service
public class Java8CalculateServiceImpl implements CalculateService {

    @Override
    public Integer sum(Integer... values) {
        System.out.println("Java 8 Lambda 实现求和");
        int sum = Stream.of(values).reduce(0, Integer::sum);
        return sum;
    }

}
```

> 4. 引用 & 结果
> 指定 `profiles` 参数为 `Java7` 或 `Java8`，装配对应的 `Bean` 。

```java
import com.imooc.diveinspringboot.configuration.service.CalculateService;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * {@link CalculateService} 引导类
 *
 * @author zozo
 * @since 1.0
 */
@SpringBootApplication(scanBasePackages = "com.imooc.diveinspringboot.configuration.service")
public class CalculateServiceBootstrap {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(CalculateServiceBootstrap.class)
                .web(WebApplicationType.NONE)
                .profiles("Java8") // 配置方式条件装配
                .run(args);

        // 查找 Bean
        CalculateService calculateService = context.getBean(CalculateService.class);

        System.out.println("calculateService sum(1...10): " +
                calculateService.sum(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        // 关闭上下文
        context.close();

    }

}
```
```
Java 7 for 循环实现求和
calculateService sum(1...10): 55
Java 8 Lambda 实现求和
calculateService sum(1...10): 55
```

### @Conditional annotation
> **Spring 条件装配 -> 实现方式: 编程方式(@Conditional)**

> * 自定义 `@Conditional` 实现

> 1. 定义注解 `@ConditionalOnSystemProperty`，指定 `@Conditional` 为 `OnSystemPropertyCondition.class` 。

```java
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * Java 系统属性 条件判断
 *
 * @author zozo
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnSystemPropertyCondition.class)
public @interface ConditionalOnSystemProperty {

    /**
     * Java 系统属性名
     *
     * @return
     */
    String name();

    /**
     * Java 系统属性值
     *
     * @return
     */
    String value();

}
```

> 2. `OnSystemPropertyCondition` 实现 `Condition`，重写 `matches` 方法。

```java
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * 系统属性 条件判断
 *
 * @author zozo
 * @since 1.0
 */
public class OnSystemPropertyCondition implements Condition {

    /**
     * 是否匹配，匹配 True 才满足装配的条件
     *
     * @param context  Spring 上下文
     * @param metadata 元信息，包含 ConditionalOnSystemProperty name 和 value 值
     * @return 是否匹配
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        // 获取 ConditionalOnSystemProperty 的元信息
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnSystemProperty.class.getName());

        // 注解条件 name
        String propertyName = String.valueOf(attributes.get("name"));
        // 注解条件 value
        String propertyValue = String.valueOf(attributes.get("value"));

        System.out.println("propertyName: " + propertyName + ", propertyValue: " + propertyValue);

        // 系统自带 value
        String javaPropertyValue = System.getProperty(propertyName);

        System.out.println("javaPropertyValue: " + javaPropertyValue);

        // 注解条件 name 对应的 value 和 系统自带 name 对应 value 是否相等
        return javaPropertyValue.equals(propertyValue);

    }

}
```

> 3. 引导 & 运行结果。
> 只有 `OnSystemPropertyCondition` 中的 `matches` 方法返回 `true` （即注解的 `propertyValue` 等于系统的 `javaPropertyValue`）时，才可成功装配 `Hello Bean(String.class)`。

```java
import com.imooc.diveinspringboot.configuration.condition.ConditionalOnSystemProperty;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

public class ConditionalOnSystemPropertyBootstrap {

    @Bean
    @ConditionalOnSystemProperty(name = "user.name", value = "Administrator")
    public String hello() {
        return "Hello Bean";
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(ConditionalOnSystemPropertyBootstrap.class)
                .web(WebApplicationType.NONE)
                .run(args);

        // 查找 Bean（ConditionalOnSystemProperty条件满足才可用）
        String hello = context.getBean("hello", String.class);

        System.out.println("hello Bean: " + hello);

        // 关闭上下文
        context.close();
    }

}
```
```
propertyName: user.name, propertyValue: Administrator
javaPropertyValue: Administrator
hello Bean: Hello Bean
```

---

## Auto-configuration
> **Spring Boot 自动装配**

> Spring Boot 基于约定大于配置原则，实现 Spring 组件自动装配目的。

### Realize by
> **Spring Boot 自动装配 -> 底层装配技术**

> * Spring 模式注解装配
> * Spring @Enable 模块装配
> * Spring 条件装配
> * Spring 工厂加载机制（`SpringFactoriesLoader` & `spring.factories`）

### Example
> **Spring Boot 自动装配 -> 举例（SpringFactoriesLoader）**

> * Spring Boot 实现

> 1. `org.springframework.core.io.support.SpringFactoriesLoader`

```java
public abstract class SpringFactoriesLoader {

    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";

    public static <T> List<T> loadFactories(Class<T> factoryClass, @Nullable ClassLoader classLoader) {
        Assert.notNull(factoryClass, "'factoryClass' must not be null");
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = SpringFactoriesLoader.class.getClassLoader();
        }
        List<String> factoryNames = loadFactoryNames(factoryClass, classLoaderToUse);
        if (logger.isTraceEnabled()) {
            logger.trace("Loaded [" + factoryClass.getName() + "] names: " + factoryNames);
        }
        List<T> result = new ArrayList<>(factoryNames.size());
        for (String factoryName : factoryNames) {
            result.add(instantiateFactory(factoryName, factoryClass, classLoaderToUse));
        }
        AnnotationAwareOrderComparator.sort(result);
        return result;
    }

    ...

}
```

> 2. `spring-boot-autoconfigure-2.0.5.RELEASE.jar!/META-INF/spring.factories`

```properties
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
...
org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration,\
org.springframework.boot.autoconfigure.cloud.CloudAutoConfiguration,\
...
org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration,\
...
org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration,\
...
org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration,\
...
org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,\
...
org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,\
...
org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration,\
...
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration,\
...
org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration,\
...
org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration,\
...
org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration,\
org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration,\
...
org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration,\
...
org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration,\
...
org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration,\
...
```

### Customizing
> **Spring Boot 自动装配 -> 自定义实现**

> * 自定义实现，步骤如下：
> 1. 激活自动装配 `@EnableAutoConfiguration`
> 2. 实现自动装配 `xxxAutoConfiguration`
> 3. 配置自动装配 `META-INF/spring.factories`

> 1. 激活自动装配，注解 `@EnableAutoConfiguration` 。

```java
import com.imooc.diveinspringboot.configuration.annotation.EnableHelloConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * {@link EnableAutoConfiguration} 引导类
 *
 * @author zozo
 * @since 1.0
 */
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

> 2. 实现自动装配 `HelloAutoConfiguration` 。
> 通过 Spring 模式注解，Spring @Enable 模块装配，Spring 条件装配实现（相关实现类见上文）。

```java

import com.imooc.diveinspringboot.configuration.annotation.EnableHelloImportSelector;
import com.imooc.diveinspringboot.configuration.condition.ConditionalOnSystemProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Hello 自动装配
 *
 * @author zozo
 * @since 1.0
 */
@Configuration // Spring 模式注解
@EnableHelloImportSelector // Spring @Enable 模块装配
@ConditionalOnSystemProperty(name = "user.name", value = "Administrator") // Spring 条件装配
public class HelloAutoConfiguration {

}
```

> 3. 配置自动装配 `~\dive-in-spring-boot\src\main\resources\META-INF\spring.factories` 。

```properties
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.imooc.diveinspringboot.configuration.configuration.HelloAutoConfiguration
```

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-AutoConfiguration/autoconfiguration-springfactories.png)

> 4. 运行结果。

```
propertyName: user.name, propertyValue: Administrator
javaPropertyValue: Administrator
propertyName: user.name, propertyValue: Administrator
javaPropertyValue: Administrator
hello Bean: Hello Bean
```
