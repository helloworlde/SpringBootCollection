# Spring Boot  中使用 WebSocket 

> WebSocket 是一种长连接技术，可以实现服务端和客户端的双向通信，服务端可以主动推送信息给客户端

## 构建应用 

### 添加依赖

- build.gradle 

```gradle
dependencies {
    compile("org.springframework.boot:spring-boot-starter-websocket")
    compile("org.webjars:webjars-locator-core")
    compile("org.webjars:sockjs-client:1.0.2")
    compile("org.webjars:stomp-websocket:2.3.3")
    compile("org.webjars:bootstrap:3.3.7")
    compile("org.webjars:jquery:3.1.0")

    testCompile("org.springframework.boot:spring-boot-starter-test")
}
```

### 配置 

- WebSocketConfig.java

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/socket").withSockJS();
    }
}
```

其中`/topic` 是用于推送给客户端的消息路径前缀；`/app`是用于请求服务端的消息路径前缀， `/socket`用于客户端建立连接

SockJS用于提供浏览器兼容性，当浏览器不支持 WebSocket 时，就会尝试降级为HTTP流或者长轮询的方式以实现和 WebSocket 相同的效果，参考 [4.3. SockJS Fallback](https://docs.spring.io/spring/docs/5.1.8.RELEASE/spring-framework-reference/web.html#websocket-fallback)


### 群发消息

群发消息可以将消息发送给所有订阅了该消息的客户端，可以通过 `@SendTo`或`org.springframework.messaging.simp.SimpMessagingTemplate#convertAndSend`发送

#### 服务端

- 通过注解实现

```java
    @MessageMapping("/message/broadcast")
    @SendTo("/response/message")
    public Message broadcastMessage(String title) {
        log.info("Receive new broadcast message from socket, title is :" + title);

        return Message.builder()
                      .title(title)
                      .content("Socket Broadcast:" + title + " content!")
                      .createTime(LocalDateTime.now())
                      .build();
    }
```

- 通过 REST接口调用方法实现

```java
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/message/broadcast")
    @ResponseBody
    public void sendBroadcastMessage(String title) {
        log.info("Receive new broadcast message from REST interface, title is :" + title);

        Message message = Message.builder()
                                 .title(title)
                                 .content("REST Broadcast:" + title + " content!")
                                 .createTime(LocalDateTime.now())
                                 .build();

        simpMessagingTemplate.convertAndSend("/response/message", message);
    }
```

#### 客户端

```javascript
function connect() {
    var socket = new SockJS('/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/response/message', function (message) {
            console.log("Receive message from server:" + message);
        });
    });
}

function sendMessage() {
    stompClient.send('/request/message/broadcast', {}, "Message");
}
```

#### 测试

- 启动应用，用两个不同的浏览器访问 [localhost:8080](localhost:8080)
- 广播消息建立连接，并发送消息，此时可以看到两个浏览器都收到了刚才发送的消息
- 通过 REST 接口：

```bash
curl 'localhost:8080/message/broadcast?title=hello'
```


### 发送给指定客户端

群发消息可以将消息发送给所有订阅了该消息的客户端，可以通过 `@SendToUser`或`org.springframework.messaging.simp.SimpMessagingTemplate#convertAndSendToUser`发送

#### 服务端

发送给指定客户端，要求客户端有指定的用户名，通过继承`org.springframework.web.socket.server.support.DefaultHandshakeHandler`实现

- CustomPrinciple.java

```java
@AllArgsConstructor
public class CustomPrinciple implements Principal {

    private String name;

    @Override
    public String getName() {
        return this.name;
    }
}
```

- CustomHandshakeHandler.java

```java
@Slf4j
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String userId = UUID.randomUUID().toString();
        log.info("Current username is: {}", userId);
        return new CustomPrinciple(userId);
    }
}
```

- WebSocketConfig.java

```java
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/socket")
                .setHandshakeHandler(new CustomHandshakeHandler())
                .withSockJS();

    }
```

- 通过注解实现

```java
    @MessageMapping("/message/specify")
    @SendToUser("/response/message")
    public Message speicifyMessage(String title) {
        log.info("Receive new specify message from socket, title is :" + title);

        return Message.builder()
                      .title(title)
                      .content("Socket Specify:" + title + " content!")
                      .createTime(LocalDateTime.now())
                      .build();
    }
```

- 通过调用方法实现

```java
    @GetMapping("/message/specify")
    @ResponseBody
    public void sendSpecifyUserMessage(String title, String username) {
        log.info("Receive new specify message from REST interface, title is :" + title);

        Message message = Message.builder()
                                 .title(title)
                                 .content("REST Specify:" + title + " content!")
                                 .createTime(LocalDateTime.now())
                                 .build();

        simpMessagingTemplate.convertAndSendToUser(username, "/response/message", message);
    }
``` 


#### 客户端

```javascript
function connect() {
    var socket = new SockJS('/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/user/response/message', function (message) {
            console.log("Receive message from server:" + message);
        });
    });
}

function sendMessage() {
    stompClient.send('/request/message/specify', {}, "Message");
}
```


需要注意的是，发送给指定用户的消息订阅时需要添加额外的 `/user`前缀

#### 测试

- 启动应用，用两个不同的浏览器访问 [localhost:8080](localhost:8080)
- 指定消息建立连接，并发送消息，此时只有发送消息的那个浏览器才能收到消息，另一个没有收到
- 通过 REST 接口：
建立连接后可以在控制台看到相应的 username 

```bash
curl 'localhost:8080/message/specify?title=hello&username=ff3cb2ca-9579-46fb-973b-b1bd6420f610'
```
此时相应的客户端会收到发送的消息，而另一个没有

-------------

### 参考文档 

- [Using WebSocket to build an interactive web application](https://spring.io/guides/gs/messaging-stomp-websocket/)
- [Spring Boot + WebSockets + Angular 5](https://medium.com/oril/spring-boot-websockets-angular-5-f2f4b1c14cee)
- [Spring MVC 3.2 Preview: Techniques for Real-time Updates](https://spring.io/blog/2012/05/08/spring-mvc-3-2-preview-techniques-for-real-time-updates/)