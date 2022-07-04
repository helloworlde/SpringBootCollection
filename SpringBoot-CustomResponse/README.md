# Spring Boot 中自定义接口返回格式

Spring Boot 项目中提供的 REST 接口往往需要封装状态或者其他附加的内容返回给客户端，如果每个接口都用一些工具类来处理很繁琐，会增加很多重复代码，还有可能漏掉导致客户端无法解析；用切面也可以实现，但是 SpringBoot 提供了处理的接口

### 添加自定义注解

- CustomResponse.java

```java
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomResponse {
}
```

### 消息内容

封装返回内容的实体

- CustomResponseContent.java

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponseContent {

    private Integer code;

    private String status;

    private Object data;

    private String message;
}
```

### 返回内容处理器

用于封装返回内容

- CustomResponseReturnValueHandler.java

```java
public class CustomResponseReturnValueHandler implements HandlerMethodReturnValueHandler, AsyncHandlerMethodReturnValueHandler {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public boolean isAsyncReturnValue(Object o, MethodParameter methodParameter) {
        return supportsReturnType(methodParameter);
    }

    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        return methodParameter.getAnnotatedElement().getAnnotation(CustomResponse.class) != null;
    }

    @Override
    public void handleReturnValue(Object data, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws Exception {
        modelAndViewContainer.setRequestHandled(true);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        String message = "操作成功";
        String status = "success";

        if ((data instanceof Boolean) && !((Boolean) data)) {
            message = "操作失败";
            status = "fail";
        }

        CustomResponseContent responseContent = CustomResponseContent.builder()
                                                                     .code(response.getStatus())
                                                                     .status(status)
                                                                     .message(message)
                                                                     .data(data)
                                                                     .build();

        response.getWriter()
                .write(objectMapper.writeValueAsString(responseContent));

    }
}
```

### 添加配置

将返回值处理器添加到应用中

- CustomWebMvcConfigurer.java

```java
@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(new CustomResponseReturnValueHandler());
    }
}
```

## 测试 

### 添加接口

- ProductController.java

```java
@RestController
public class ProductController {

    private static Map<Long, Product> productMap;

    static {
        productMap = Stream.of(
                Product.builder().id(1L).name("product1").price(1L).build(),
                Product.builder().id(2L).name("product2").price(2L).build(),
                Product.builder().id(3L).name("product3").price(3L).build()
        ).collect(Collectors.toConcurrentMap(Product::getId, p -> p));
    }

    @GetMapping("/boolean")
    @CustomResponse
    public Boolean getBoolean(String booleanValue) {
        return "true".equals(booleanValue);
    }

    @GetMapping("/product/{id}")
    @CustomResponse
    public Product getProduct(@PathVariable Long id) {
        return productMap.get(id);
    }

    @GetMapping("/product")
    public List<Product> getProducts() {
        return new ArrayList<>(productMap.values());
    }
}
```

### 测试 

```bash
# 没有添加自定义返回值注解
curl localhost:8080/product

[{"id":1,"name":"product1","price":1},{"id":2,"name":"product2","price":2},{"id":3,"name":"product3","price":3}]%

# 添加了自定义返回值注解
curl localhost:8080/product/1

{"code":200,"status":"success","data":{"id":1,"name":"product1","price":1},"message":"操作成功"}%

# Boolean 值返回不容的状态
curl 'http://localhost:8080/boolean?value=true'
{"code":200,"status":"success","data":true,"message":"操作成功"}%

curl 'http://localhost:8080/boolean?value=false'
{"code":200,"status":"fail","data":false,"message":"操作失败"}%
```