package io.github.helloworlde.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
public class ConsumerController {

    public static String getHostName() throws UnknownHostException {
        InetAddress netAddress = InetAddress.getLocalHost();
        if (null == netAddress) {
            return null;
        }
        return netAddress.getHostName();
    }

    @GetMapping("/")
    public String root() {
        return "{\"msg\":\"this is API application\"}";
    }

    @GetMapping("/hello")
    public String hello() throws UnknownHostException {
        return "Hostname is " + getHostName();
    }
}
