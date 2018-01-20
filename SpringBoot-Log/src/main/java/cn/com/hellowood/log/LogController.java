package cn.com.hellowood.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController("/")
public class LogController {


    @Autowired
    HttpSession session;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping
    public void generateLog() {
        int i = 0;
        String user = "Hello";
        MDC.put("user", user);

        while (i < 1000) {
            logger.debug("debug log {}", i);
            logger.info("info log {}", i);
            logger.warn("warn log {}", i);
            logger.error("error log {}", i++);
            logger.info("hello world, current User:", user);
        }
    }
}

