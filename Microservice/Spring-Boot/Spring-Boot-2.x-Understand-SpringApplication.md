# Spring Boot 2.x Understand SpringApplication

- [Document & Code](#document--code)
- [Sping Application](#sping-application)
- [Customizing Spring Application](#customizing-spring-application)
- [Fluent Builder API](#fluent-builder-api)
- [Local Bootstrap Test](#local-bootstrap-test)
- [Deduce Web Application Type](#deduce-web-application-type)
- [Deduce Main Application Class](#deduce-main-application-class)
- [Load Application Context Initializer](#load-application-context-initializer)
  - [Spring Boot Example](#spring-boot-example)
  - [Customizing](#customizing)
- [Application Listeners](#application-listeners)
  - [Spring Boot Example](#spring-boot-example-1)
  - [Customizing](#customizing-1)
- [Spring Application Run Listeners](#spring-application-run-listeners)
  - [Spring Framework Example](#spring-framework-example)
  - [Spring Boot Run Listeners Example](#spring-boot-run-listeners-example)
  - [Spring Boot Run Listeners Customizing](#spring-boot-run-listeners-customizing)
- [ConfigurableApplicationContext](#configurableapplicationcontext)

---

## Document & Code
> * [github.com: zozospider/note/Microservice/Spring-Boot/Spring-Boot-2.x](https://github.com/zozospider/note/blob/master/Microservice/Spring-Boot/Spring-Boot-2.x.md)

> * [github.com: zozospider/note-microservice-spring-boot](https://github.com/zozospider/note-microservice-spring-boot)

> * [docs.spring.io: SpringApplication](https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#boot-features-spring-application)

---

## Sping Application
> **SpingApplication 基本使用**
> * [docs.spring.io](https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#boot-features-spring-application)

> * The `SpringApplication` class provides a convenient way to bootstrap a Spring application that is started from a main() method. In many situations, you can delegate to the static `SpringApplication.run` method, as shown in the following example:

```java
public static void main(String[] args) {
	SpringApplication.run(MySpringConfiguration.class, args);
}
```

---

## Customizing Spring Application
> **自定义 SpringApplication**
> * [docs.spring.io](https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#boot-features-customizing-spring-application)

> * If the SpringApplication defaults are not to your taste, you can instead create a local instance and customize it. For example, to turn off the banner, you could write:

```java
public static void main(String[] args) {
	SpringApplication app = new SpringApplication(MySpringConfiguration.class);
	app.setBannerMode(Banner.Mode.OFF);
	app.run(args);
}
```

---

## Fluent Builder API
> **流式构建**
> * [docs.spring.io](https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#boot-features-fluent-builder-api)

> * If you need to build an `ApplicationContext` hierarchy (multiple contexts with a parent/child relationship) or if you prefer using a “fluent” builder API, you can use the `SpringApplicationBuilder`.
> * The `SpringApplicationBuilder` lets you chain together multiple method calls and includes `parent` and `child` methods that let you create a hierarchy, as shown in the following example:

```java
new SpringApplicationBuilder()
		.sources(Parent.class)
		.child(Application.class)
		.bannerMode(Banner.Mode.OFF)
		.run(args);
```

---

## Local Bootstrap Test
> **本地启动测试**

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

---

## Deduce Web Application Type
> **推断 Web 应用类型**

> `SpringApplication` 通过判断 `ClassPath` 中是否存在相关实现类来推断 Web 应用的类型。类型如下：
> 1. Web Reactive: `WebApplicationType.REACTIVE`
> 2. Web Servlet: `WebApplicationType.SERVLET`
> 3. 非 Web: `WebApplicationType.NONE`

```java
package org.springframework.boot;

...

public class SpringApplication {

	...

	// 初始化 SpringApplication
	public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
		this.resourceLoader = resourceLoader;
		Assert.notNull(primarySources, "PrimarySources must not be null");
		this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
		// 推断 Web 应用类型
		this.webApplicationType = deduceWebApplicationType();
		// 加载应用上下文初始器 ApplicationContextInitializer
		setInitializers((Collection) getSpringFactoriesInstances(
				ApplicationContextInitializer.class));
		// 加载应用事件监听器 ApplicationListener
		setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
		// 推断引导类(Main Class)
		this.mainApplicationClass = deduceMainApplicationClass();
	}

	...

	// 推断 Web 应用类型
	private WebApplicationType deduceWebApplicationType() {
		// 如果 org.springframework.web.reactive.DispatcherHandler 存在
		// 且 org.springframework.web.servlet.DispatcherServlet 不存在
		// 且 org.glassfish.jersey.server.ResourceConfig 不存在
		// 则为 Web Reactive 类型
		if (ClassUtils.isPresent(REACTIVE_WEB_ENVIRONMENT_CLASS, null)
				&& !ClassUtils.isPresent(MVC_WEB_ENVIRONMENT_CLASS, null)
				&& !ClassUtils.isPresent(JERSEY_WEB_ENVIRONMENT_CLASS, null)) {
			return WebApplicationType.REACTIVE;
		}
		// 如果 javax.servlet.Servlet 不存在
		// 且 org.springframework.web.context.ConfigurableWebApplicationContext 不存在
		// 则为非 Web 类型
		for (String className : WEB_ENVIRONMENT_CLASSES) {
			if (!ClassUtils.isPresent(className, null)) {
				return WebApplicationType.NONE;
			}
		}
		// 以上两个条件都不满足
		// 则为 Web Servlet 类型
		return WebApplicationType.SERVLET;
	}

	...

}
```

---

## Deduce Main Application Class
> **推断引导类(Main Class)**

> * 根据 Main 线程执行堆栈判断实际的引导类（通过异常堆栈信息判断是否存在 `main` 方法）。

```java
package org.springframework.boot;

...

public class SpringApplication {

	...

	// 初始化 SpringApplication
	public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
		this.resourceLoader = resourceLoader;
		Assert.notNull(primarySources, "PrimarySources must not be null");
		this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
		// 推断 Web 应用类型
		this.webApplicationType = deduceWebApplicationType();
		// 加载应用上下文初始器 ApplicationContextInitializer
		setInitializers((Collection) getSpringFactoriesInstances(
				ApplicationContextInitializer.class));
		// 加载应用事件监听器 ApplicationListener
		setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
		// 推断引导类(Main Class)
		this.mainApplicationClass = deduceMainApplicationClass();
	}

	...

	// 推断引导类(Main Class)
	private Class<?> deduceMainApplicationClass() {
		try {
			StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				if ("main".equals(stackTraceElement.getMethodName())) {
					return Class.forName(stackTraceElement.getClassName());
				}
			}
		}
		catch (ClassNotFoundException ex) {
			// Swallow and continue
		}
		return null;
	}

	...

}
```

---

## Load Application Context Initializer
> **加载应用上下文初始器 `ApplicationContextInitializer`**

> * 利用 Spring 工厂加载机制，实例化 `ApplicationContextInitializer` 的具体实现类，并对具体实现类的执行顺序进行排序。

### Spring Boot Example
> **Spring Boot 举例**

> 1. `spring-boot-autoconfigure-2.0.5.RELEASE.jar!\META-INF\spring.factories` 文件中配置 `ApplicationContextInitializer` 的具体实现类。

```properties
# Initializers
org.springframework.context.ApplicationContextInitializer=\
org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer,\
org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener
```

> 2. 利用 Spring 工厂加载 `SpringFactoriesLoader`，实例化 `ApplicationContextInitializer` 的具体实现类。

```java
package org.springframework.boot;

...

public class SpringApplication {

	...

	// 初始化 SpringApplication
	public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
		this.resourceLoader = resourceLoader;
		Assert.notNull(primarySources, "PrimarySources must not be null");
		this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
		// 推断 Web 应用类型
		this.webApplicationType = deduceWebApplicationType();
		// 加载应用上下文初始器 ApplicationContextInitializer
		setInitializers((Collection) getSpringFactoriesInstances(
				ApplicationContextInitializer.class));
		// 加载应用事件监听器 ApplicationListener
		setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
		// 推断引导类(Main Class)
		this.mainApplicationClass = deduceMainApplicationClass();
	}

	...

	private <T> Collection<T> getSpringFactoriesInstances(Class<T> type) {
		return getSpringFactoriesInstances(type, new Class<?>[] {});
	}

	private <T> Collection<T> getSpringFactoriesInstances(Class<T> type,
			Class<?>[] parameterTypes, Object... args) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// Use names and ensure unique to protect against duplicates
		Set<String> names = new LinkedHashSet<>(
				SpringFactoriesLoader.loadFactoryNames(type, classLoader));
		List<T> instances = createSpringFactoriesInstances(type, parameterTypes,
				classLoader, args, names);
		AnnotationAwareOrderComparator.sort(instances);
		return instances;
	}

	...

}
```
```java
package org.springframework.core.io.support;

...

public abstract class SpringFactoriesLoader {

	// The location to look for factories（查找本地的 factories 文件）
	// <p>Can be present in multiple JAR files.（包括 JAR 文件里的）
	public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";

	...

	// 加载 factories 文件配置的具体实现类
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
		// 对具体的实现类进行排序
		AnnotationAwareOrderComparator.sort(result);
		return result;
	}

	...

}
```

> 3. `AnnotationAwareOrderComparator` 对具体的实现类进行排序。判断方式如下：
> 31. 具体实现类是否实现 `Ordered` 接口
> 32. 具体实现类是否注解 `@Order`
> 如果存在则排序生效，否则按照默认规则排序。

```java
package org.springframework.core.annotation;

...

public class AnnotationAwareOrderComparator extends OrderComparator {

	...

	@Override
	@Nullable
	protected Integer findOrder(Object obj) {
		// Check for regular Ordered interface
		Integer order = super.findOrder(obj);
		if (order != null) {
			return order;
		}

		// Check for @Order and @Priority on various kinds of elements
		if (obj instanceof Class) {
			return OrderUtils.getOrder((Class<?>) obj);
		}
		else if (obj instanceof Method) {
			Order ann = AnnotationUtils.findAnnotation((Method) obj, Order.class);
			if (ann != null) {
				return ann.value();
			}
		}
		else if (obj instanceof AnnotatedElement) {
			Order ann = AnnotationUtils.getAnnotation((AnnotatedElement) obj, Order.class);
			if (ann != null) {
				return ann.value();
			}
		}
		else {
			order = OrderUtils.getOrder(obj.getClass());
			if (order == null && obj instanceof DecoratingProxy) {
				order = OrderUtils.getOrder(((DecoratingProxy) obj).getDecoratedClass());
			}
		}

		return order;
	}

	...

}
```

### Customizing
> **自定义**

> 1. 新建 `resources\META-INF\spring.factories` ，指定 `ApplicationContextInitializer` 实现的具体类。

```properties
# Initializers (copy from spring-boot-autoconfigure-2.0.5.RELEASE.jar!/META-INF/spring.factories)
org.springframework.context.ApplicationContextInitializer=\
com.zozospider.springapplication.context.FirstApplicationContextInitializer,\
com.zozospider.springapplication.context.SecondApplicationContextInitializer
```

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Understand-SpringApplication/springapplication-springfactories.png)

> 2. 创建 `FirstApplicationContextInitializer` ，实现 `ApplicationContextInitializer` 接口，并注解 `Order` 进行排序，指定为高级别。

```java
package com.zozospider.springapplication.context;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * FirstApplicationContextInitializer
 *
 * @param <C>
 * @author zozo
 * @since 1.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE) // 进行排序，并指定为高级别
public class FirstApplicationContextInitializer<C extends ConfigurableApplicationContext>
        implements ApplicationContextInitializer<C> {

    @Override
    public void initialize(C applicationContext) {
        System.out.println("First ApplicationContextInitializer: " + applicationContext.getId());
    }

}
```

> 3. 创建 `SecondApplicationContextInitializer` ，实现 `ApplicationContextInitializer` 接口和 `Ordered` 接口，并重写 `Ordered` 接口的 `getOrder()` 方法，指定为低级别。

```java
package com.zozospider.springapplication.context;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * SecondApplicationContextInitializer
 *
 * @author zozo
 * @since 1.0
 */
public class SecondApplicationContextInitializer implements ApplicationContextInitializer, Ordered {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.out.println("Second ApplicationContextInitializer: " + applicationContext.getId());
    }

    /**
     * 实现 Ordered 接口，进行排序，并指定为低级别
     *
     * @return
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
```

> 4. 运行 `SpringApplicationBootstrap` & 运行结果（ web 和非 web 应用都可以）。

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
```
First ApplicationContextInitializer: org.springframework.context.annotation.AnnotationConfigApplicationContext@473b46c3
Second ApplicationContextInitializer: application
```

---

## Application Listeners
> **加载应用事件监听器 `ApplicationListener`**

> * 利用 Spring 工厂加载机制，实例化 `ApplicationListener` 的具体实现类，并对具体实现类的执行顺序进行排序。

### Spring Boot Example
> **Spring Boot 举例**
> 1. `spring-boot-autoconfigure-2.0.5.RELEASE.jar!\META-INF\spring.factories` 和 `spring-boot-2.0.5.RELEASE.jar!/META-INF/spring.factories` 文件中配置 `ApplicationListener` 的具体实现类。

```properties
# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.boot.autoconfigure.BackgroundPreinitializer
```
```properties
# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.boot.ClearCachesApplicationListener,\
org.springframework.boot.builder.ParentContextCloserApplicationListener,\
org.springframework.boot.context.FileEncodingApplicationListener,\
org.springframework.boot.context.config.AnsiOutputApplicationListener,\
org.springframework.boot.context.config.ConfigFileApplicationListener,\
org.springframework.boot.context.config.DelegatingApplicationListener,\
org.springframework.boot.context.logging.ClasspathLoggingApplicationListener,\
org.springframework.boot.context.logging.LoggingApplicationListener,\
org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener
```

> 2. 利用 Spring 工厂加载 `SpringFactoriesLoader`，实例化 `ApplicationListener` 的具体实现类。

```java
package org.springframework.boot;

...

public class SpringApplication {

	...

	// 初始化 SpringApplication
	public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
		this.resourceLoader = resourceLoader;
		Assert.notNull(primarySources, "PrimarySources must not be null");
		this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
		// 推断 Web 应用类型
		this.webApplicationType = deduceWebApplicationType();
		// 加载应用上下文初始器 ApplicationContextInitializer
		setInitializers((Collection) getSpringFactoriesInstances(
				ApplicationContextInitializer.class));
		// 加载应用事件监听器 ApplicationListener
		setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
		// 推断引导类(Main Class)
		this.mainApplicationClass = deduceMainApplicationClass();
	}

	...

	private <T> Collection<T> getSpringFactoriesInstances(Class<T> type) {
		return getSpringFactoriesInstances(type, new Class<?>[] {});
	}

	private <T> Collection<T> getSpringFactoriesInstances(Class<T> type,
			Class<?>[] parameterTypes, Object... args) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// Use names and ensure unique to protect against duplicates
		Set<String> names = new LinkedHashSet<>(
				SpringFactoriesLoader.loadFactoryNames(type, classLoader));
		List<T> instances = createSpringFactoriesInstances(type, parameterTypes,
				classLoader, args, names);
		AnnotationAwareOrderComparator.sort(instances);
		return instances;
	}

	...

}
```
```java
package org.springframework.core.io.support;

...

public abstract class SpringFactoriesLoader {

	// The location to look for factories（查找本地的 factories 文件）
	// <p>Can be present in multiple JAR files.（包括 JAR 文件里的）
	public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";

	...

	// 加载 factories 文件配置的具体实现类
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
		// 对具体的实现类进行排序
		AnnotationAwareOrderComparator.sort(result);
		return result;
	}

	...

}
```

> 3. `AnnotationAwareOrderComparator` 对具体的实现类进行排序。判断方式如下：
> 31. 具体实现类是否实现 `Ordered` 接口
> 32. 具体实现类是否注解 `@Order`
> 如果存在则排序生效，否则按照默认规则排序。

```java
package org.springframework.core.annotation;

...

public class AnnotationAwareOrderComparator extends OrderComparator {

	...

	@Override
	@Nullable
	protected Integer findOrder(Object obj) {
		// Check for regular Ordered interface
		Integer order = super.findOrder(obj);
		if (order != null) {
			return order;
		}

		// Check for @Order and @Priority on various kinds of elements
		if (obj instanceof Class) {
			return OrderUtils.getOrder((Class<?>) obj);
		}
		else if (obj instanceof Method) {
			Order ann = AnnotationUtils.findAnnotation((Method) obj, Order.class);
			if (ann != null) {
				return ann.value();
			}
		}
		else if (obj instanceof AnnotatedElement) {
			Order ann = AnnotationUtils.getAnnotation((AnnotatedElement) obj, Order.class);
			if (ann != null) {
				return ann.value();
			}
		}
		else {
			order = OrderUtils.getOrder(obj.getClass());
			if (order == null && obj instanceof DecoratingProxy) {
				order = OrderUtils.getOrder(((DecoratingProxy) obj).getDecoratedClass());
			}
		}

		return order;
	}

	...

}
```

> 4. `ApplicationListener` 接口，定义实现类需要实现的方法。

```java
package org.springframework.context;

...

@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {

	/**
	 * Handle an application event.
	 * @param event the event to respond to
	 */
	void onApplicationEvent(E event);

}
```

> 5. `BackgroundPreinitializer` 和 `ConfigFileApplicationListener` 等具体实现类实现 `ApplicationListener` 接口，并指定排序。

```java
package org.springframework.boot.autoconfigure;

...

@Order(LoggingApplicationListener.DEFAULT_ORDER + 1)
public class BackgroundPreinitializer
		implements ApplicationListener<SpringApplicationEvent> {

	@Override
	public void onApplicationEvent(SpringApplicationEvent event) {
		if (event instanceof ApplicationStartingEvent
				&& preinitializationStarted.compareAndSet(false, true)) {
			performPreinitialization();
		}
		if ((event instanceof ApplicationReadyEvent
				|| event instanceof ApplicationFailedEvent)
				&& preinitializationStarted.get()) {
			try {
				preinitializationComplete.await();
			}
			catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	...

}
```
```java
package org.springframework.boot.context.config;

...

public class ConfigFileApplicationListener
		implements EnvironmentPostProcessor, SmartApplicationListener, Ordered {

	...

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ApplicationEnvironmentPreparedEvent) {
			onApplicationEnvironmentPreparedEvent(
					(ApplicationEnvironmentPreparedEvent) event);
		}
		if (event instanceof ApplicationPreparedEvent) {
			onApplicationPreparedEvent(event);
		}
	}

	...

}
```

### Customizing
> **自定义**

> 1. 新建 `resources\META-INF\spring.factories` ，指定 `ApplicationListener` 实现的具体类。

```properties
# Application Listeners (copy from spring-boot-autoconfigure-2.0.5.RELEASE.jar!/META-INF/spring.factories and spring-boot-2.0.5.RELEASE.jar!/META-INF/spring.factories)
org.springframework.context.ApplicationListener=\
com.zozospider.springapplication.listener.FirstApplicationListener,\
com.zozospider.springapplication.listener.SecondApplicationListener,\
com.zozospider.springapplication.listener.BeforeConfigFileApplicationListener,\
com.zozospider.springapplication.listener.AfterConfigFileApplicationListener
```

> 2. 新建 `resources/application.properties`，配置 `name` key 和对应的 value。
```properties
name = zozospider
```

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Understand-SpringApplication/springapplication-springfactories2.png)

> 3. 创建 `FirstApplicationListener` ，实现 `ApplicationListener<ContextRefreshedEvent>` 接口，并注解 `Order` 进行排序，指定为高级别。

```java
package com.zozospider.springapplication.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * First {@link ApplicationListener} 监听 {@link ContextRefreshedEvent}
 *
 * @author zozo
 * @since 1.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE) // 进行排序，并指定为高级别
public class FirstApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("First ApplicationListener: " + event.getApplicationContext().getId()
                + ", timestamp: " + event.getTimestamp());
    }

}
```

> 4. 创建 `SecondApplicationListener` ，实现 `ApplicationListener<ContextRefreshedEvent>` 接口和 `Ordered` 接口，并重写 `Ordered` 接口的 `getOrder()` 方法，指定为低级别。

```java
package com.zozospider.springapplication.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

/**
 * Second {@link ApplicationListener} 监听 {@link ContextRefreshedEvent}
 *
 * @author zozo
 * @since 1.0
 */
public class SecondApplicationListener implements ApplicationListener<ContextRefreshedEvent>, Ordered {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("Second ApplicationListener: " + event.getApplicationContext().getId()
                + ", timestamp: " + event.getTimestamp());
    }

    /**
     * 实现 Ordered 接口，进行排序，并指定为低级别
     *
     * @return
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
```

> 5. 创建 `BeforeConfigFileApplicationListener` ，实现 `ApplicationListener<ContextRefreshedEvent>` 接口和 `Ordered` 接口，并重写 `Ordered` 接口的 `getOrder()` 方法，指定为比 `ConfigFileApplicationListener` 优先级更高。

```java
package com.zozospider.springapplication.listener;

import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Before {@link ConfigFileApplicationListener} 实现
 *
 * @zozo
 * @since 1.0
 */
public class BeforeConfigFileApplicationListener implements SmartApplicationListener, Ordered {

    /**
     * 参考 ConfigFileApplicationListener 实现
     * @param eventType
     * @return
     */
    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationEnvironmentPreparedEvent.class.isAssignableFrom(eventType)
                || ApplicationPreparedEvent.class.isAssignableFrom(eventType);
    }

    /**
     * 参考 ConfigFileApplicationListener 实现
     * @param aClass
     * @return
     */
    @Override
    public boolean supportsSourceType(Class<?> aClass) {
        return true;
    }

    /**
     * 参考 ConfigFileApplicationListener 实现，并试图获取 application.properties 中配置的 name 对应的值。
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            ApplicationEnvironmentPreparedEvent preparedEvent = (ApplicationEnvironmentPreparedEvent) event;
            ConfigurableEnvironment environment = preparedEvent.getEnvironment();
            String name = environment.getProperty("name");
            System.out.println("Before environment getProperty name: " + name);
        }
        if (event instanceof ApplicationPreparedEvent) {

        }
    }

    /**
     * 比 ConfigFileApplicationListener 优先级更高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return ConfigFileApplicationListener.DEFAULT_ORDER - 1;
    }

}
```

> 6. 创建 `AfterConfigFileApplicationListener` ，实现 `ApplicationListener<ContextRefreshedEvent>` 接口和 `Ordered` 接口，并重写 `Ordered` 接口的 `getOrder()` 方法，指定为比 `ConfigFileApplicationListener` 优先级更低。

```java
package com.zozospider.springapplication.listener;

import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * After {@link ConfigFileApplicationListener} 实现
 *
 * @zozo
 * @since 1.0
 */
public class AfterConfigFileApplicationListener implements SmartApplicationListener, Ordered {

    /**
     * 参考 ConfigFileApplicationListener 实现
     * @param eventType
     * @return
     */
    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationEnvironmentPreparedEvent.class.isAssignableFrom(eventType)
                || ApplicationPreparedEvent.class.isAssignableFrom(eventType);
    }

    /**
     * 参考 ConfigFileApplicationListener 实现
     * @param aClass
     * @return
     */
    @Override
    public boolean supportsSourceType(Class<?> aClass) {
        return true;
    }

    /**
     * 参考 ConfigFileApplicationListener 实现，并试图获取 application.properties 中配置的 name 对应的值。
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            ApplicationEnvironmentPreparedEvent preparedEvent = (ApplicationEnvironmentPreparedEvent) event;
            ConfigurableEnvironment environment = preparedEvent.getEnvironment();
            String name = environment.getProperty("name");
            System.out.println("After environment getProperty name: " + name);
        }
        if (event instanceof ApplicationPreparedEvent) {

        }
    }

    /**
     * 比 ConfigFileApplicationListener 优先级更低
     *
     * @return
     */
    @Override
    public int getOrder() {
        return ConfigFileApplicationListener.DEFAULT_ORDER + 1;
    }

}
```

> 7. 运行 `SpringApplicationBootstrap` & 运行结果（ web 和非 web 应用都可以）。

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
```
Before environment getProperty name: null
After environment getProperty name: zozospider

(以下两行为 Load Application Context Initializer 自定义运行结果)
First ApplicationContextInitializer: org.springframework.context.annotation.AnnotationConfigApplicationContext@75c072cb
Second ApplicationContextInitializer: application

First ApplicationListener: application, timestamp: 1539954515281
Second ApplicationListener: application, timestamp: 1539954515281
```

---

## Spring Application Run Listeners
> **SpringApplication 运行监听器**

### Spring Framework Example
> **Spring 框架运行监听器举例**

```java
package com.zozospider.springapplication;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Spring Framework 事件引导类
 *
 * @author zozo
 * @since 1.0
 */
public class SpringFrameworkEventBootstrap {

    public static void main(String[] args) {

        // 创建上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // 注册应用事件监听器
        context.addApplicationListener(event -> {
            System.out.println("监听到事件: " + event);
        });

        // 启动上下文
        context.refresh();

        context.publishEvent("Hello Event 1");
        context.publishEvent("Hello Event 2");
        context.publishEvent(new ApplicationEvent("Hello Application Event") {

        });

        // 关闭上下文
        context.close();

    }

}
```
```
监听到事件: org.springframework.context.event.ContextRefreshedEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@5f5a92bb: startup date [Sat Oct 20 14:26:38 CST 2018]; root of context hierarchy]
监听到事件: org.springframework.context.PayloadApplicationEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@5f5a92bb: startup date [Sat Oct 20 14:26:38 CST 2018]; root of context hierarchy]
监听到事件: org.springframework.context.PayloadApplicationEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@5f5a92bb: startup date [Sat Oct 20 14:26:38 CST 2018]; root of context hierarchy]
监听到事件: com.zozospider.springapplication.SpringFrameworkEventBootstrap$1[source=Hello Application Event]
监听到事件: org.springframework.context.event.ContextClosedEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@5f5a92bb: startup date [Sat Oct 20 14:26:38 CST 2018]; root of context hierarchy]
```

### Spring Boot Run Listeners Example
> **Spring Boot 运行监听器举例**

> 1. `spring-boot-2.0.5.RELEASE.jar!/META-INF/spring.factories` 文件中配置 `SpringApplicationRunListener` 的具体实现类。

```properties
# Run Listeners
org.springframework.boot.SpringApplicationRunListener=\
org.springframework.boot.context.event.EventPublishingRunListener
```

> 2. 利用 Spring 工厂加载 `SpringFactoriesLoader`，实例化 `EventPublishingRunListener` 的具体实现类。

> * `SpringApplication` 运行时调用 `SpringApplicationRunListeners` 。

```java
package org.springframework.boot;

...

public class SpringApplication {

	...

	/**
	 * Run the Spring application, creating and refreshing a new
	 * {@link ApplicationContext}.
	 * @param args the application arguments (usually passed from a Java main method)
	 * @return a running {@link ApplicationContext}
	 */
	public ConfigurableApplicationContext run(String... args) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		ConfigurableApplicationContext context = null;
		Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
		configureHeadlessProperty();
		SpringApplicationRunListeners listeners = getRunListeners(args);
		listeners.starting();
		
		...

	}

	private SpringApplicationRunListeners getRunListeners(String[] args) {
		Class<?>[] types = new Class<?>[] { SpringApplication.class, String[].class };
		return new SpringApplicationRunListeners(logger, getSpringFactoriesInstances(
				SpringApplicationRunListener.class, types, this, args));
	}

	...

}
```
> * `SpringApplicationRunListeners` 装载所有实例化的 `SpringApplicationRunListener` ，并统一调用。
```java
package org.springframework.boot;

...

class SpringApplicationRunListeners {
	
	...

	private final List<SpringApplicationRunListener> listeners;

	SpringApplicationRunListeners(Log log,
			Collection<? extends SpringApplicationRunListener> listeners) {
		this.log = log;
		this.listeners = new ArrayList<>(listeners);
	}

	public void starting() {
		for (SpringApplicationRunListener listener : this.listeners) {
			listener.starting();
		}
	}

	public void environmentPrepared(ConfigurableEnvironment environment) {
		for (SpringApplicationRunListener listener : this.listeners) {
			listener.environmentPrepared(environment);
		}
	}

	...

}
```
> * `SpringApplicationRunListener` 接口，定义实现类需要实现的方法。
```java
public interface SpringApplicationRunListener {

	// Called immediately when the run method has first started. Can be used for very early initialization.
	// Spring 应用刚启动
	void starting();

	// Called once the environment has been prepared, but before the {@link ApplicationContext} has been created.
	// ConfigurableEnvironment 准备妥当，允许将其调整。
	void environmentPrepared(ConfigurableEnvironment environment);

	// Called once the {@link ApplicationContext} has been created and prepared, but before sources have been loaded.
	// ConfigurableApplicationContext 准备妥当，允许将其调整
	void contextPrepared(ConfigurableApplicationContext context);

	// Called once the application context has been loaded but before it has been refreshed.
	// ConfigurableApplicationContext 已装载，但仍未启动。
	void contextLoaded(ConfigurableApplicationContext context);

	// The context has been refreshed and the application has started but {@link CommandLineRunner CommandLineRunners} and {@link  ApplicationRunner ApplicationRunners} have not been called.
	// ConfigurableApplicationContext 已启动，此时 Spring Bean 已初始化完成。
	void started(ConfigurableApplicationContext context);

	// Called immediately before the run method finishes, when the application context has been refreshed and all {@link CommandLineRunner CommandLineRunners} and {@link ApplicationRunner ApplicationRunners} have been called.
	// Spring 应用正在运行。
	void running(ConfigurableApplicationContext context);

	// Called when a failure occurs when running the application.
	// Spring 应用运行失败。
	void failed(ConfigurableApplicationContext context, Throwable exception);

}
```
> * `EventPublishingRunListener` 实现 `SpringApplicationRunListener` 接口。
```java
package org.springframework.boot.context.event;

...

public class EventPublishingRunListener implements SpringApplicationRunListener, Ordered {

	...

	@Override
	public void starting() {
		this.initialMulticaster.multicastEvent(
				new ApplicationStartingEvent(this.application, this.args));
	}

	@Override
	public void environmentPrepared(ConfigurableEnvironment environment) {
		this.initialMulticaster.multicastEvent(new ApplicationEnvironmentPreparedEvent(
				this.application, this.args, environment));
	}

	...

}
```

### Spring Boot Run Listeners Customizing
> **Spring Boot 运行监听器自定义**

> 1. 新建 `resources\META-INF\spring.factories` ，指定 `SpringApplicationRunListener` 实现的具体类。

```properties
# Run Listeners (copy from spring-boot-2.0.5.RELEASE.jar!/META-INF/spring.factories)
org.springframework.boot.SpringApplicationRunListener=\
com.zozospider.springapplication.run.HelloRunListener
```

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Understand-SpringApplication/springapplication-springfactories3.png)

> 2. 创建 `HelloRunListener` ，实现 `SpringApplicationRunListener` 接口。

```java
package com.zozospider.springapplication.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Hello {@link SpringApplicationRunListener}
 *
 * @author zozo
 * @since 1.0
 */
public class HelloRunListener implements SpringApplicationRunListener {

    public HelloRunListener(SpringApplication application, String[] args) {

    }

    @Override
    public void starting() {
        System.out.println("HelloRunListener starting()...");
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {

    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {

    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {

    }

    @Override
    public void started(ConfigurableApplicationContext context) {

    }

    @Override
    public void running(ConfigurableApplicationContext context) {

    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {

    }

}
```
> 3. 运行 `SpringApplicationBootstrap` & 运行结果（ web 和非 web 应用都可以）。

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
```
HelloRunListener starting()...

(以上四行为 Load Application Context Initializer 和 Application Listeners 自定义运行结果)
First ApplicationContextInitializer: org.springframework.context.annotation.AnnotationConfigApplicationContext@21507a04
Second ApplicationContextInitializer: application
First ApplicationListener: application, timestamp: 1540020038631
Second ApplicationListener: application, timestamp: 1540020038631
```

---

## ConfigurableApplicationContext
> **创建 Spring 应用上下文**

> 根据准备阶段推断出的 Web 应用类型，创建对应的 `ConfigurableApplicationContext` 实例。
> * Web Reactive: `AnnotationConfigReactiveWebServerApplicationContext`
> * Web Servlet: `AnnotationConfigServletWebServerApplicationContext`
> * 非 Web: `AnnotationConfigApplicationContext`

> 根据准备阶段推断出的 Web 应用类型，创建对应的 `ConfigurableEnvironment` 实例。
> * Web Reactive: `StandardEnvironment`
> * Web Servlet: `StandardServletEnvironment`
> * 非 Web: `StandardEnvironment`

> `SpringApplication` 运行时，根据 Web 应用类型，创建对应的 `ApplicationContext`

```java
package org.springframework.boot;

...

public class SpringApplication {

	...

	public ConfigurableApplicationContext run(String... args) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		ConfigurableApplicationContext context = null;
		Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
		configureHeadlessProperty();
		SpringApplicationRunListeners listeners = getRunListeners(args);
		listeners.starting();
		try {
			ApplicationArguments applicationArguments = new DefaultApplicationArguments(
					args);
			// 根据 Web 应用类型，创建对应的 `ConfigurableEnvironment` 的实例
			ConfigurableEnvironment environment = prepareEnvironment(listeners,
					applicationArguments);
			configureIgnoreBeanInfo(environment);
			Banner printedBanner = printBanner(environment);
			// 根据 Web 应用类型，创建对应的 `ConfigurableApplicationContext` 的实例
			context = createApplicationContext();
			exceptionReporters = getSpringFactoriesInstances(
					SpringBootExceptionReporter.class,
					new Class[] { ConfigurableApplicationContext.class }, context);
			prepareContext(context, environment, listeners, applicationArguments,
					printedBanner);
			refreshContext(context);
			afterRefresh(context, applicationArguments);
			stopWatch.stop();
			if (this.logStartupInfo) {
				new StartupInfoLogger(this.mainApplicationClass)
						.logStarted(getApplicationLog(), stopWatch);
			}
			listeners.started(context);
			callRunners(context, applicationArguments);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, exceptionReporters, listeners);
			throw new IllegalStateException(ex);
		}

		try {
			listeners.running(context);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, exceptionReporters, null);
			throw new IllegalStateException(ex);
		}
		return context;
	}

	...

	// 根据 Web 应用类型，创建对应的 `ConfigurableEnvironment` 的实例
	private ConfigurableEnvironment prepareEnvironment(
			SpringApplicationRunListeners listeners,
			ApplicationArguments applicationArguments) {
		// Create and configure the environment
		ConfigurableEnvironment environment = getOrCreateEnvironment();
		configureEnvironment(environment, applicationArguments.getSourceArgs());
		listeners.environmentPrepared(environment);
		bindToSpringApplication(environment);
		if (!this.isCustomEnvironment) {
			environment = new EnvironmentConverter(getClassLoader())
					.convertEnvironmentIfNecessary(environment, deduceEnvironmentClass());
		}
		ConfigurationPropertySources.attach(environment);
		return environment;
	}

	...

	private ConfigurableEnvironment getOrCreateEnvironment() {
		if (this.environment != null) {
			return this.environment;
		}
		switch (this.webApplicationType) {
		case SERVLET:
			return new StandardServletEnvironment();
		case REACTIVE:
			return new StandardReactiveWebEnvironment();
		default:
			return new StandardEnvironment();
		}
	}

	...

	// 根据 Web 应用类型，创建对应的 `ConfigurableApplicationContext` 的实例
	protected ConfigurableApplicationContext createApplicationContext() {
		Class<?> contextClass = this.applicationContextClass;
		if (contextClass == null) {
			try {
				switch (this.webApplicationType) {
				// org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext
				case SERVLET:
					contextClass = Class.forName(DEFAULT_WEB_CONTEXT_CLASS);
					break;
				// org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext
				case REACTIVE:
					contextClass = Class.forName(DEFAULT_REACTIVE_WEB_CONTEXT_CLASS);
					break;
				// org.springframework.context.annotation.AnnotationConfigApplicationContext
				default:
					contextClass = Class.forName(DEFAULT_CONTEXT_CLASS);
				}
			}
			catch (ClassNotFoundException ex) {
				throw new IllegalStateException(
						"Unable create a default ApplicationContext, "
								+ "please specify an ApplicationContextClass",
						ex);
			}
		}
		return (ConfigurableApplicationContext) BeanUtils.instantiateClass(contextClass);
	}

	...

}
```

> 测试

```java
package com.zozospider.springapplication;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringApplicationContextBootstrap {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(SpringApplicationContextBootstrap.class)
                .web(WebApplicationType.NONE)
                .run(args);

        System.out.println("ConfigurableApplicationContext: " + context.getClass().getName());
        System.out.println("ConfigurableApplicationContext getEnvironment: "+ context.getEnvironment().getClass().getName());

        // 关闭上下文
        context.close();

    }

}
```
```
// 以下为 .web(WebApplicationType.NONE)
ConfigurableApplicationContext: org.springframework.context.annotation.AnnotationConfigApplicationContext
ConfigurableApplicationContext getEnvironment: org.springframework.core.env.StandardEnvironment
```

---
