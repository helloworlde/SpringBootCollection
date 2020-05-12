package io.github.helloworlde.prometheus.controller;

import io.github.helloworlde.prometheus.config.PrometheusMetrics;
import io.github.helloworlde.prometheus.dao.ProductDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@Slf4j
public class ConsumerController {

    private static final Random random = new Random();

    @Autowired
    private ProductDao productDao;

    @PrometheusMetrics(name = "hello")
    @GetMapping("/hello")
    public Object hello(String name) {
        Map<String, Object> result = new HashMap<String, Object>() {{
            put("date", Instant.now().toString());
            put("name", name);
        }};
        return result;
    }

    @GetMapping("/db")
    public Object db() {
        Integer id = random.nextInt(100);
        return productDao.getProductById(id);
    }

    @PrometheusMetrics
    @GetMapping("/order")
    public Object order(String area, Integer money) throws InterruptedException {
        log.info("下单地区:{}, 金额:{}", area, money);
        Thread.sleep(300);
        return "success";
    }
}
