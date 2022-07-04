# Spring Boot 配置 HTTPS 访问

## 生成证书

- 创建新证书

```
keytool -genkeypair -alias hellowood -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore hellowood.p12 -validity 3650
输入密钥库口令:
再次输入新口令:
您的名字与姓氏是什么?
  [Unknown]:  HellWood
您的组织单位名称是什么?
  [Unknown]:  HelloWood
您的组织名称是什么?
  [Unknown]:  HelloWood
您所在的城市或区域名称是什么?
  [Unknown]:  BJ
您所在的省/市/自治区名称是什么?
  [Unknown]:  BJ
该单位的双字母国家/地区代码是什么?
  [Unknown]:  CN
CN=HellWood, OU=HelloWood, O=HelloWood, L=BJ, ST=BJ, C=CN是否正确?
  [否]:  y
```

这样就生成了 `hellowood.p12`这个证书

- 如果已经有一个证书，可以将该证书转换为 PKCS格式

```
keytool -import -alias hellowood -file hellowood.crt -keystore hellowood.p12
// 或
keytool -importkeystore -srckeystore hellowood.jks -destkeystore hellowood.p12 -deststoretype pkcs12
```

## 配置 HTTPS

- 将 `hellowood.p12` 添加到 `resources/`下
- application.properties

```properties
# SSL config
server.ssl.key-store-type=PKCS12
server.ssl.key-store=classpath:hellowood.p12
server.ssl.key-store-password=123456
server.ssl.key-alias=hellowood
server.ssl.enabled=true
```

- 添加接口

```java
@RestController
public class BaseController {

    @GetMapping("/")
    public String root() {
        return "HelloWood";
    }
}
```

- 启动应用，会看到日志中有提示应用使用 HTTPS 启动

```
2018-12-31 21:54:07.940  INFO 24001 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (https) with context path ''
```

- 访问 `http://localhost:8080`，会提示需要使用 HTTPS 进行访问
 
```bash
curl http://localhost:8080/
Bad Request
This combination of host and port requires TLS.
```

- 访问 `https://localhost:8080`，会成功返回 HelloWood

```bash
 curl --insecure https://localhost:8080
 HelloWood%
```

## 重定向 HTTP 到 HTTPS

- 修改端口，添加 HTTP 端口 (application.properties)

```dsconfig
server.port=8443
server.port.http=8080
```

- 添加重定向配置`ConnectorConfig.java`

```java
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectorConfig {

    @Value("${server.port.http}")
    private int serverPortHttp;

    @Value("${server.port}")
    private int serverPortHttps;

    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection securityCollection = new SecurityCollection();
                securityCollection.addPattern("/*");
                securityConstraint.addCollection(securityCollection);
                context.addConstraint(securityConstraint);
            }
        };
        factory.addAdditionalTomcatConnectors(redirectConnector());
        return factory;
    }

    private Connector redirectConnector() {
        Connector connector = new Connector(Http11NioProtocol.class.getName());
        connector.setScheme("http");
        connector.setPort(serverPortHttp);
        connector.setSecure(false);
        connector.setRedirectPort(serverPortHttps);
        return connector;
    }
}
```

- 再次启动应用，看到日志中有 HTTP 和 HTTPS 的端口信息

```verilog
2018-12-31 22:17:47.113  INFO 24612 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8443 (https) 8080 (http) with context path ''
```

- 访问 `http://localhost:8080`，将会被重定向到`https://localhost:8443`

```bash
curl -v http://localhost:8080
* Rebuilt URL to: http://localhost:8080/
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET / HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 302
< Cache-Control: private
< Expires: Thu, 01 Jan 1970 00:00:00 GMT
< Location: https://localhost:8443/
< Content-Length: 0
< Date: Mon, 31 Dec 2018 14:31:44 GMT
<
* Connection #0 to host localhost left intact
```
- 访问 `https://localhost:8443`正常返回`HelloWood`
```
curl https://localhost:8443 --insecure
HelloWood%
```