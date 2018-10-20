# Spring Boot 2.x Understand SpringApplication

- [Document & Code](#document--code)
- [Sping Application](#sping-application)
- [Customizing Spring Application](#customizing-spring-application)
- [Fluent Builder API](#fluent-builder-api)
- [Local Bootstrap Test](#local-bootstrap-test)
- [Deduce Web Application Type](#deduce-web-application-type)
- [Deduce Main Application Class](#deduce-main-application-class)
- [Load Application Context Initializer](#load-application-context-initializer)

---

## Document & Code
> * [docs.spring.io: SpringApplication](https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#boot-features-spring-application)
> * [github.com: zozospider/note-microservice-spring-boot](https://github.com/zozospider/note-microservice-spring-boot)

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
# Initializers
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

## Application Listeners
> **加载应用事件监听器 `ApplicationListener`**

> * 利用 Spring 工厂加载机制，实例化 `ApplicationListener` 的具体实现类，并对具体实现类的执行顺序进行排序。

### Spring Boot Example
> **Spring Boot 举例**
> 1. `spring-boot-autoconfigure-2.0.5.RELEASE.jar!\META-INF\spring.factories` 文件中配置 `ApplicationListener` 的具体实现类。

```properties
# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.boot.autoconfigure.BackgroundPreinitializer
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

### Customizing
> **自定义**

> 1. 新建 `resources\META-INF\spring.factories` ，指定 `ApplicationListener` 实现的具体类。

```properties
# Application Listeners
org.springframework.context.ApplicationListener=\
com.zozospider.springapplication.listener.FirstApplicationListener,\
com.zozospider.springapplication.listener.SecondApplicationListener
```

![image](https://raw.githubusercontent.com/zozospider/note/master/Microservice/Spring-Boot/Spring-Boot-2.x-Understand-SpringApplication/springapplication-springfactories2.png)

> 2. 创建 `FirstApplicationListener` ，实现 `ApplicationListener<ContextRefreshedEvent>` 接口，并注解 `Order` 进行排序，指定为高级别。

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

> 3. 创建 `SecondApplicationListener` ，实现 `ApplicationListener<ContextRefreshedEvent>` 接口和 `Ordered` 接口，并重写 `Ordered` 接口的 `getOrder()` 方法，指定为低级别。

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
First ApplicationContextInitializer: org.springframework.context.annotation.AnnotationConfigApplicationContext@75c072cb
Second ApplicationContextInitializer: application
(以上两行为 Load Application Context Initializer 自定义运行结果)

First ApplicationListener: application, timestamp: 1539954515281
Second ApplicationListener: application, timestamp: 1539954515281
```



