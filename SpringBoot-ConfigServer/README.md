# SpringBoot 使用 Spring Cloud Config

> [Spring Cloud Config ](https://cloud.spring.io/spring-cloud-config/) 适用于微服务，各个服务在启动时从配置中心获取配置；集中管理程序的配置

> Spring Cloud Config 推荐使用 Git 管理配置文件，这样可以更好的管理配置文件的变更；同时也使用本地文件

## 配置 Spring Cloud Config 应用

#### 创建  Spring Cloud Config 应用

#### 添加依赖

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

 - db-dev.properties

```properties
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/dev?useSSL=false
spring.datasource.username=root
spring.datasource.password=123456
```

 - db-test.properties

```properties
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/test?useSSL=false
spring.datasource.username=root
spring.datasource.password=123456
```
 - db-prod.properties

```properties
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/prod?useSSL=false
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