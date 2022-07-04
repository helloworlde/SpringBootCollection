package io.github.helloworlde.image.controller;

import io.github.helloworlde.image.health.CustomHealthIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomController {


    @Autowired
    private CustomHealthIndicator customHealthIndicator;

    @RequestMapping("/")
    public String root() {
        return "hello.html";
    }

    @GetMapping("/switchHealth")
    public void switchHealth(boolean health) {
        customHealthIndicator.setHealth(health);
    }
}
