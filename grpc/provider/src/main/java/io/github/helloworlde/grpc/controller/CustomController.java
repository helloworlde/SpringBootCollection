package io.github.helloworlde.grpc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomController {


    @GetMapping("/hello")
    public Map<String, Object> hello() {
        return new HashMap() {{
            put("message", "message");
            put("time", LocalDateTime.now());
        }};
    }


    // @GetMapping("/getUserInfo")
    // public Object getUserInfo(String name) {
    //
    //     UserInfoRequest request = UserInfoRequest.newBuilder()
    //                                              .setName(name)
    //                                              .build();
    //     return userInfoGrpc.getUserInfo(request);
    // }

}
