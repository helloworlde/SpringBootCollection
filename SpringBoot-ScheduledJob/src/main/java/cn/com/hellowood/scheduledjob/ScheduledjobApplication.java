package cn.com.hellowood.scheduledjob;

import org.quartz.DisallowConcurrentExecution;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@DisallowConcurrentExecution
public class ScheduledjobApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduledjobApplication.class, args);
    }
}
