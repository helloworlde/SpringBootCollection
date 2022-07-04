package io.github.helloworlde.prometheus.service;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

@Service
public class CustomService {

    @Counted
    @Timed
    public Object timed(String id) throws InterruptedException {
        Thread.sleep(RandomUtils.nextInt(100, 2000));
        return id;
    }
}
