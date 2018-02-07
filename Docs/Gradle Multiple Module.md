# Gradle 使用-多项目构建

## 配置
> 如该项目有四个模块，分别是`Controller`, `Service`, `Dao`,`Model`，根目录仅为父级目录，不存在任何代码；子目录为各个相应的模块或单独的项目

- 项目根目录下添加 `build.gradle`
```gradle
// 子模块通用配置
subprojects {
    apply plugin: 'java'
    apply plugin: 'idea'
    apply plugin: 'eclipse'

    group = 'cn.com.hellowood'
    version = '0.0.2-SNAPSHOT'

    sourceCompatibility = 1.8

    // java编译的时候缺省状态下会因为中文字符而失败
    [compileJava, compileTestJava, javadoc]*.options*.encoding = 'UTF-8'

    repositories {
        mavenLocal()
        maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
        mavenCentral()
        jcenter()
    }

    dependencies {
        testCompile 'junit:junit:4.12'
    }
}

```

- 项目根目录下添加 `settings.gradle`
```gradle
rootProject.name = 'SpringBoot'

// 子模块
include 'Controller'
include 'Service'
include 'Dao'
include 'Model'
```

- 子目录 `build.gradle`(其他模块类似)
```gradle
buildscript {
	ext {
		springBootVersion = '1.5.9.RELEASE'
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

archivesBaseName = 'Controller'

repositories {
	mavenCentral()
}


dependencies {
	compile('org.mybatis.spring.boot:mybatis-spring-boot-starter:1.3.1')
	compile('org.springframework.boot:spring-boot-starter-web')
	compile('io.springfox:springfox-swagger2:2.7.0')
	compile('io.springfox:springfox-swagger-ui:2.7.0')
	runtime('mysql:mysql-connector-java')
	runtime('com.h2database:h2')
	testCompile('org.springframework.boot:spring-boot-starter-test')
	testCompile('org.springframework.restdocs:spring-restdocs-mockmvc')
}

```

## 编译和使用 

- 在根目录下执行`gradle init`
- 在根目录下执行`gradle build`
- 如果需要单独编译某个模块执行`gradle Controller:build`


## 依赖其他项目
- 在项目的build.gradle 文件的依赖中添加其他项目依赖

```
dependencies {
	compile project(':model')
}
```