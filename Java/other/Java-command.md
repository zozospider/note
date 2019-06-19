
# Java 执行 jar 包

## Maven 打包, 指定 Main-Class

如需指定 jar 包的 Main-Class, 可在 maven 的 pom.xml 中设置:
```xml
        <plugins>
          <plugin>  
              <artifactId>maven-assembly-plugin</artifactId>  
              <configuration>  
                  <!-- 这部分可有可无, 加上的话则直接生成可运行 jar 包 -->
                  <archive>
                      <manifest>
                          <mainClass>${exec.mainClass}</mainClass>
                      </manifest>
                  </archive>
                  <descriptorRefs>  
                      <descriptorRef>jar-with-dependencies</descriptorRef>  
                  </descriptorRefs>  
             </configuration>
        </plugin>
```

这样打包后, 在 jar 包的 `./META-INF/MANIFEST.MF` 文件中会有如下内容:
```
Manifest-Version: 1.0
Archiver-Version: Plexus Archiver
Created-By: Apache Maven
Built-By: zozo
Build-Jdk: 1.8.0_40
Main-Class: App
```

## 执行 jar 包

### 指定 Main-Class, 不带参数

执行命令如下:
```
java -cp test.jar com.example.Main
```

### 指定 Main-Class, 带参数

执行命令如下:
```
java -cp test.jar com.example.Main 1000 arg2str
```

### 不指定 Main-Class, 不带参数

执行命令如下:
```
java -jar test.jar
```

### 不指定 Main-Class, 带参数

执行命令如下:
```
java -jar test.jar 1000 arg2str
```

程序去参数逻辑如下:
```java
public class Main {
    public static void main(String[] args) {
        Integer arg1 = Integer.valueOf(args[0]);
        String arg2 = args[1];
    }
}
```

### 不指定 Main-Class, 带指定参数名

格式如下:
```
java [-options] -jar jarfile [args...]
```

执行命令如下:
```
java -Darg1=1000 -Darg2="arg2 strA strB" -jar test.jar
```

程序取参数逻辑如下:
```java
public class Main {
    public static void main(String[] args) {
        Integer arg1 = Integer.valueOf(System.getProperty("arg1", "10"));
        String arg2 = System.getProperty("arg2", "arg2 default value");
    }
}
```
