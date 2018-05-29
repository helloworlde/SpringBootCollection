# SpringBoot 使用 Spring Cloud Config

> [Spring Cloud Config ](https://cloud.spring.io/spring-cloud-config/) 适用于微服务，各个服务在启动时从配置中心获取配置；集中管理程序的配置

> Spring Cloud Config 推荐使用 Git 管理配置文件，这样可以更好的管理配置文件的变更；同时也使用本地文件

## 配置 Spring Cloud Config 应用

#### 创建  Spring Cloud Config 应用
#### 添加依赖

```gradle
buildscript {
    ext {
        springBootVersion = '2.0.2.RELEASE'
    }
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
    }
}

apply plugin: 'java'
apply plugin: 'eclipse'
apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'

group = 'cn.com.hellowood'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = 1.8

repositories {
    mavenCentral()
    maven { url "https://repo.spring.io/snapshot" }
    maven { url "https://repo.spring.io/milestone" }
}


ext {
    springCloudVersion = 'Finchley.BUILD-SNAPSHOT'
}

dependencies {
    compile('org.springframework.cloud:spring-cloud-config-server')
    testCompile('org.springframework.boot:spring-boot-starter-test')
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}

```

#### 添加配置文件 
配置文件可以使用本地文件或者 Git 仓库的配置文件；使用本地文件的限制比较多，推荐使用  Git 仓库，该仓库可以是本地的或者远程的仓库

 - mysqldb/mysqldb-dev.properties

```properties
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/dev?useSSL=false
spring.datasource.username=root
spring.datasource.password=123456
```

 - mysqldb/mysqldb-test.properties

```properties
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/test?useSSL=false
spring.datasource.username=root
spring.datasource.password=123456
```

 - mysqldb/mysqldb-prod.properties

```properties
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/prod?useSSL=false
spring.datasource.username=root
spring.datasource.password=123456
```

- h2db/h2db-dev.properties

```properties
spring.datasource.initialization-mode=embedded
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:dev
spring.datasource.username=root
spring.datasource.password=123456
```

- h2db/h2db-test.properties

```properties
spring.datasource.initialization-mode=embedded
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:test
spring.datasource.username=root
spring.datasource.password=123456
```

- h2db/h2db-prod.properties

```properties
spring.datasource.initialization-mode=embedded
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:prod
spring.datasource.username=root
spring.datasource.password=123456
```

#### 修改配置
- application.properties

```properties
server.port=8888
spring.cloud.config.server.git.search-paths=/*
spring.cloud.config.server.git.default-label=master
spring.cloud.config.server.git.uri=https://github.com/helloworlde/Configuration.git
#spring.cloud.config.server.git.username=
#spring.cloud.config.server.git.password=
spring.cloud.config.server.git.basedir=./config-repo
```

> `search-paths` 可以忽略配置文件的`label`，其他应用使用时可以直接引用，避免多个`label`导致访问冲突
`label`可以使用不同的分支，名称即为分支名称
`basedir`指向当前项目文件夹，用于保存从仓库复制的配置文件，默认指向服务 `/tmp`目录，该目录可能会被系统清理掉
如果需要登录仓库可以使用用户名和密码，或者配置 ssh 登录
#### 启用 Spring Cloud Config
在应用启动类添加 `@EnableConfigServer`

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class ConfigserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigserverApplication.class, args);
    }
}
```

## 使用 Spring Cloud Config 

#### 创建应用

#### 添加依赖

- build.gradle

```groovy
buildscript {
    ext {
        springBootVersion = '2.0.2.RELEASE'
    }
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
    }
}

apply plugin: 'java'
apply plugin: 'eclipse'
apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'

archivesBaseName = 'ConfigClient'

repositories {
    mavenCentral()
    maven { url "https://repo.spring.io/snapshot" }
    maven { url "https://repo.spring.io/milestone" }
}


ext {
    springCloudVersion = 'Finchley.BUILD-SNAPSHOT'
}

dependencies {
    compile('org.springframework.boot:spring-boot-starter-web')
    compile('org.springframework.cloud:spring-cloud-starter-config')
    compile('org.springframework.boot:spring-boot-starter-data-jpa')
    runtime('mysql:mysql-connector-java')
    runtime('com.h2database:h2')
    testCompile('org.springframework.boot:spring-boot-starter-test')
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}

```
#### 修改配置文件

- 添加 `bootsrap.properties`

```properties
spring.cloud.config.uri=${cloud.config.url:http://localhost:8888}
```

> 配置中心地址通过`cloud.config.url`参数获取，当该参数不存在时从`http://localhost:8080`获取

- 修改 `application.properties`

```properties
server.port=8080
spring.profiles.active=dev
spring.cloud.config.profile=${spring.profiles.active:dev}
spring.cloud.config.name=h2db
spring.jpa.show-sql=true
```

> 使用的 Spring Cloud Config 的环境和应用使用的环境一致，`:dev`表示当没有设置时默认使用 `dev`环境
> `spring.cloud.config.name=`后面添加需要获取的配置，在这里可以是`h2db`或`mysqldb`和其他添加在`Git`配置仓库中的配置前缀

#### 配置接口

- ProductController

```java
import cn.com.hellowood.configclient.model.Product;
import cn.com.hellowood.configclient.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HelloWood
 */
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String root() {
        return "Hello";
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @GetMapping("/product")
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @PostMapping("/product")
    public Product saveProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }
}
```

- 省略`ProductService`, `ProductServiceImpl`, `ProductDao`等类

#### 测试

启动`ConfigServer`
配置`spring.profiles.active=dev`, `spring.cloud.config.name=h2db`, 启动应用，此时可以从启动日志看到加载了`h2db-dev`相关的配置；改为`spring.profiles.active= test`, `spring.cloud.config.name=mysqldb`，可以从启动日志看到加载了`mysqldb-test`的相关配置，说明配置中心配置成功