package cn.com.hellowood.scheduledjob.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import static cn.com.hellowood.scheduledjob.utils.ApplicationUtils.currentDateTime;

//@Component
public class StaticJob {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static long SECOND = 1000;


    /**
     * fixedDelay: Fixed wait time
     * This is fixed delay, will execute per 10 seconds
     */
    @Scheduled(fixedDelay = 10 * SECOND)
    public void fixedDelayJob() {
        logger.info("{}\tfixedDelay", currentDateTime());
    }

    /**
     * fixedRate: Fixed interval time, will execute per 10 seconds
     */
    @Scheduled(fixedRate = 10 * SECOND)
    public void fixedRate() {
        logger.info("{}\tfixedRate", currentDateTime());
    }

    /**
     * cron: execute by cron expression
     */
    @Scheduled(cron = "*/10 * * * * *")
    public void cron() {
        logger.info("{}\tcron", currentDateTime());
    }
}
