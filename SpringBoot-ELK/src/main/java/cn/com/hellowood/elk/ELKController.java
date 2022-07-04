package cn.com.hellowood.elk;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ELKController {

    private final Logger logger = Logger.getLogger(getClass());

    @GetMapping("/")
    public void generateLog() {
        for (int i = 0; i < 20; i++) {
            logger.info("log " + Math.random());
        }
    }
}
