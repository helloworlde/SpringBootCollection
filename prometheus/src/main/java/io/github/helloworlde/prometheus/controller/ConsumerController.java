package io.github.helloworlde.prometheus.controller;

import io.github.helloworlde.prometheus.config.PrometheusMetrics;
import io.github.helloworlde.prometheus.dao.ProductDao;
import io.github.helloworlde.prometheus.service.CustomService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
@Slf4j
public class ConsumerController {

    private static final Random random = new Random();

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CustomService customService;

    @GetMapping("/hello")
    public Object hello(String name) {
        Map<String, Object> result = new HashMap<String, Object>() {{
            put("date", Instant.now().toString());
            put("name", name);
        }};
        return result;
    }


    /**
     * 时间和数量计数器
     */
    @Timed
    @Counted
    @GetMapping("/timed")
    public Object timed() throws InterruptedException {
        return customService.timed(UUID.randomUUID().toString());
    }

    @Timed
    @GetMapping("/db")
    public Object db() {
        int id = random.nextInt(100);
        if (id == 99) {
            throw new RuntimeException("Demo exception");
        }
        return productDao.getProductById(id);
    }

    /**
     * 自定义监控指标
     */
    @PrometheusMetrics
    @GetMapping("/order")
    public Object order(String area, Integer money) throws InterruptedException {
        log.info("下单地区:{}, 金额:{}", area, money);
        Thread.sleep(300);
        return "success";
    }
}
