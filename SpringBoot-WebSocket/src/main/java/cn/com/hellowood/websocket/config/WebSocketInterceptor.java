package cn.com.hellowood.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * @author HelloWood
 */
@Slf4j
public class WebSocketInterceptor extends DefaultHandshakeHandler {
    // @Override
    // public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    //     log.info("Before Handshake Interceptor");
    //     log.info("Current sec-websocket-key is: {}", request.getHeaders().get("sec-websocket-key"));
    //     return true;
    // }
    //
    // @Override
    // public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    //     log.info("After Handshake Interceptor");
    // }


    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String userId = UUID.randomUUID().toString();
        log.info("UserId is:{}", userId);

        return new CustomPrinciple(userId);
    }

}
