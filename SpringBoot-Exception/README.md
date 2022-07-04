# Spring Boot 中自定义异常处理

> Spring Boot 中提供了默认的异常处理，但是对于应用来说，这些信息并不应该直接返回或者不够明确，需要结合自己的情况进行定制
> 自定义处理异常有两种方式:
> - `org.springframework.web.servlet.HandlerExceptionResolver#resolveException`方法
> - `org.springframework.web.bind.annotation.RestControllerAdvice`或`org.springframework.web.bind.annotation.ControllerAdvice`和`org.springframework.web.bind.annotation.ExceptionHandler`注解来实现

> 当两种方式都实现时，`HandlerExceptionResolver`要先于`ControllerAdvice`执行

## 使用 HandlerExceptionResolver 处理异常

- CustomExceptionHandlerResolver.java

```java
public class CustomExceptionHandlerResolver implements HandlerExceptionResolver {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

        String message = "服务器错误";

        if (o instanceof HandlerMethod) {
            if (e instanceof IllegalArgumentException) {
                message = "参数错误";
            } else if (e instanceof SecurityException) {
                message = "不允许访问";
            } else if (e instanceof NullPointerException) {
                message = "空指针异常";
            }
        } else if (e instanceof NoHandlerFoundException) {
            message = "未找到相应资源";
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            message = "请求类型不支持";
        }

        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        try {
            httpServletResponse.getWriter()
                               .write(
                                       OBJECT_MAPPER.writeValueAsString(
                                               CustomResponseContent.builder()
                                                                    .code(500)
                                                                    .status("fail")
                                                                    .message(message)
                                                                    .build()
                                       )
                               );
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return new ModelAndView();
    }
}
```

还需要将该配置添加到应用中 

- CustomWebMvcConfigurer.java

```java
@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new CustomExceptionHandlerResolver());
    }
}
```

### 测试

- 添加接口

```java
    @GetMapping("/null")
    @CustomResponse
    public Object getProduct() {
        throw new NullPointerException();
    }

    @GetMapping("/arg")
    public Object getProducts() {
        throw new IllegalArgumentException();
    }
```

- 调用接口

```
curl localhost:8080/null
{"code":500,"status":"fail","data":null,"message":"空指针异常"}%

curl localhost:8080/arg
{"code":500,"status":"fail","data":null,"message":"参数错误"}%
```


## 使用 RestControllerAdvice/ControllerAdvice 和 ExceptionHandler 处理异常

- CustomControllerExceptionResolver.java

```java
@RestControllerAdvice
@ControllerAdvice
public class CustomControllerExceptionResolver {

    @ExceptionHandler({NullPointerException.class})
    @ResponseBody
    public ResponseEntity<?> handlerNullPointerException(HttpServletRequest request, Throwable throwable) {
        CustomResponseContent responseContent = CustomResponseContent.builder()
                                                                     .code(500)
                                                                     .status("fail")
                                                                     .message("空指针异常")
                                                                     .build();
        return new ResponseEntity<>(responseContent, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseBody
    public ResponseEntity<?> handlerIllegalArgumentException(HttpServletRequest request, Throwable throwable) {
        CustomResponseContent responseContent = CustomResponseContent.builder()
                                                                     .code(400)
                                                                     .status("fail")
                                                                     .message("参数错误")
                                                                     .build();
        return new ResponseEntity<>(responseContent, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<?> handlerMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                               .getAllErrors()
                               .stream()
                               .map(f -> ((FieldError) f).getField() + ":" + f.getDefaultMessage())
                               .collect(Collectors.joining(";"));

        CustomResponseContent responseContent = CustomResponseContent.builder()
                                                                     .code(400)
                                                                     .status("fail")
                                                                     .message(errorMessage)
                                                                     .build();
        return new ResponseEntity<>(responseContent, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseEntity<?> handlerException(HttpServletRequest request, Throwable throwable) {
        CustomResponseContent responseContent = CustomResponseContent.builder()
                                                                     .code(500)
                                                                     .status("fail")
                                                                     .message(throwable.getMessage())
                                                                     .build();
        return new ResponseEntity<>(responseContent, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

### 测试 

- 添加接口

```java
    @GetMapping("/null")
    @CustomResponse
    public Object getProduct() {
        throw new NullPointerException();
    }

    @GetMapping("/arg")
    public Object getProducts() {
        throw new IllegalArgumentException();
    }

    @PostMapping("/product")
    @CustomResponse
    public Product product(@RequestBody @Validated Product product) {
        return product;
    }
```

- 添加接口参数校验

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    private static final Long serialVersionUID = 1435515995276255188L;


    @NotNull(message = "id 不能为空")
    @Min(value = 1, message = "id 必须大于0")
    @Max(value = 10, message = "id 不能小于 10")
    private Long id;

    @NotNull(message = "名称不能为空")
    @Pattern(regexp = "product[\\w]+", message = "名称必须以 product 开头")
    private String name;

    @PositiveOrZero(message = "价格必须是正数")
    private Long price;

}
```

- 测试

```bash
curl localhost:8080/null
{"code":500,"status":"fail","data":null,"message":"空指针异常"}%

curl localhost:8080/arg
{"code":400,"status":"fail","data":null,"message":"参数错误"}%


curl -X POST \
  http://localhost:8080/product \
  -H 'Content-Type: application/json' \
  -d '{
    "id": 1
}'
{"code":400,"status":"fail","data":null,"message":"name:名称不能为空"}%


curl -X POST \
  http://localhost:8080/product \
  -H 'Content-Type: application/json' \
  -d '{
    "id": 0,
    "name": 1,
    "price": -100
}'
{"code":400,"status":"fail","data":null,"message":"id:id 必须大于0;price:价格必须是正数;name:名称必须以 product 开头"}%
```

## 403/404... 等错误

添加了上述的异常处理后，还有一部分异常无法处理，如404 错误，这是因为这些错误的异常处理并不经过异常处理器，而是被转发到 `/error`的路径下，默认由`org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController`处理，所以，继承`org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController`并添加相应实现即可自定义处理该异常

### 返回 JSON 数据

- CustomNoHandlerExceptionResolver.java 

```java
@Controller
@RequestMapping("/error")
public class CustomNoHandlerExceptionResolver extends AbstractErrorController {

    public CustomNoHandlerExceptionResolver(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @Override
    public String getErrorPath() {
        return null;
    }

    @RequestMapping
    @ResponseBody
    public ResponseEntity<?> error(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> originRequestUri = Optional.ofNullable((String) request.getAttribute("javax.servlet.error.request_uri"));
        String reasonPhrase = HttpStatus.valueOf(response.getStatus()).getReasonPhrase();

        CustomResponseContent responseContent = CustomResponseContent.builder()
                                                                     .code(response.getStatus())
                                                                     .status("fail")
                                                                     .message(originRequestUri.orElse("/error") + ":" + reasonPhrase)
                                                                     .build();

        return new ResponseEntity<>(responseContent, HttpStatus.valueOf(response.getStatus()));
    }
}
```

- 测试

```bash
curl localhost:8080/404
{"code":404,"status":"fail","data":null,"message":"/404:Not Found"}%
```

### 返回错误页面

当通过页面访问时，我们希望返回一个页面，可以通过指定 `produces="text/html"`处理来自网页的请求

在`resources/public/error`目录下添加相应的文件，如 `404.html`等，根据不同的错误返回相应的页面

```java
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView pageError(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView(String.format("error/%d.html", response.getStatus()));
    }

```