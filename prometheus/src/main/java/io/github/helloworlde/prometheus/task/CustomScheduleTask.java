package io.github.helloworlde.prometheus.task;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class CustomScheduleTask {

    private static final Random random = new Random();

    @Counted(value = "custom_task_count", extraTags = {"name", "自定义定时任务"}, description = "自定义定时任务监控")
    @Scheduled(fixedDelay = 5000)
    @Timed(value = "custom_task_time", extraTags = {"name", "自定义定时任务"}, description = "自定义定时任务监控")
    public void customSchedule() throws InterruptedException {
        Thread.sleep(random.nextInt(5000));
        log.info("定时任务执行完成");
    }
}
