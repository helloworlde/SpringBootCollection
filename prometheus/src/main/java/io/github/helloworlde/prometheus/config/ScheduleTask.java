package io.github.helloworlde.prometheus.config;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class ScheduleTask {

    Random random = new Random();

    @Scheduled(fixedDelay = 5000)
    @Timed(value = "custom_task_time", extraTags = {"name"}, description = "自定义定时任务")
    public void customSchedule() throws InterruptedException {
        Thread.sleep(random.nextInt(5000));
        log.info("定时任务执行完成");
    }
}
