package cn.com.hellwood.session.controller;

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
