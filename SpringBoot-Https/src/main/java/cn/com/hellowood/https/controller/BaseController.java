package cn.com.hellowood.https.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HelloWood
 * @date 2018-12-31 21:50
 */
@RestController
public class BaseController {

    @GetMapping("/")
    public String root() {
        return "HelloWood";
    }
}
