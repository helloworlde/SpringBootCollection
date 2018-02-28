# SpringBoot 使用 Spring Session 实现 Session 共享

## 通过 Redis 共享 

### 配置
- 配置并启动 Redis
- 添加依赖

```gradle
dependencies {
	compile('org.springframework.session:spring-session')
	compile('org.springframework.boot:spring-boot-starter-data-redis')
	compile('org.springframework.boot:spring-boot-starter-web')
	testCompile('org.springframework.boot:spring-boot-starter-test')
}
```

- 添加配置

```
spring.session.store-type=redis
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.database=0
spring.redis.password=123456
```

### 使用

- 启用 Redis Session 

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
public class SessionApplication {

	public static void main(String[] args) {
		SpringApplication.run(SessionApplication.class, args);
	}
}
```

`@EnableRedisHttpSession` 也可以不写，在配置文件里配置  `spring.session.store-type=redis` 即可

- 添加 Controller 

```java

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class SessionController {

    @RequestMapping("/")
    @ResponseBody
    public String root(HttpSession session) {
        return session.getId();
    }

}
```

访问 `http://localhost:8080`, 此时会显示 `Session ID`, 重新启动应用，再次访问 `http://localhost:8080`，此时 `Session ID` 应该和第一次访问一致，说明 `Session` 已被正确共享