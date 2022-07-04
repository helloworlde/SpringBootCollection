# SpringBoot 中使用 Quartz 定时任务

> 在 SpringBoot 中通过 `@Schedule`注解就可以简单的实现定时任务，但是这种方式够灵活，如果想要修改执行状态就必须修改代码；另外一种实现方式是通过 [`Quartz`](http://www.quartz-scheduler.org/) 实现任务调度

## 通过 SpringBoot 实现简单任务

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import static cn.com.hellowood.scheduledjob.utils.ApplicationUtils.currentDateTime;

@Component
public class StaticJob {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static long SECOND = 1000;


    /**
     * fixedDelay: 固定延迟时间执行
     */
    @Scheduled(fixedDelay = 10 * SECOND)
    public void fixedDelayJob() {
        logger.info("{}\tfixedDelay", currentDateTime());
    }

    /**
     * fixedRate: 固定间隔时间执行
     */
    @Scheduled(fixedRate = 10 * SECOND)
    public void fixedRate() {
        logger.info("{}\tfixedRate", currentDateTime());
    }

    /**
     * cron: 通过 Cron 表达式控制执行
     */
    @Scheduled(cron = "*/10 * * * * *")
    public void cron() {
        logger.info("{}\tcron", currentDateTime());
    }
}
```

----------------------

## SpringBoot 集成 Quartz 实现可控的任务

> 在 SpringBoot 的2.0 以上版本直接集成了 Quartz；
> 本文通过 RESTful 接口 来实现，实现的大概思路是通过实现 `org.quartz.Job`接口，在其`execute()`中实现自己的逻辑；通过调用 `org.quartz.Scheduler`的接口实现任务的创建，运行，暂停，更新，删除功能，核心的代码在 `cn.com.hellowood.scheduledjob.job.ScheduleUtil`里

### 添加依赖

- build.gradle

```gradle
buildscript {
    ext {
        springBootVersion = '2.0.0.RC1'
    }
    repositories {
        maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
        mavenCentral()
        maven { url "https://repo.spring.io/snapshot" }
        maven { url "https://repo.spring.io/milestone" }
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
    }
}

apply plugin: 'java'
apply plugin: 'eclipse'
apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'

repositories {
    maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
    mavenCentral()
    maven { url "https://repo.spring.io/snapshot" }
    maven { url "https://repo.spring.io/milestone" }
}


dependencies {
    compile('org.springframework.boot:spring-boot-starter-quartz')
    compile('org.springframework.boot:spring-boot-starter-web')
    compile('org.mybatis.spring.boot:mybatis-spring-boot-starter:1.3.1')
    runtime('mysql:mysql-connector-java')
    runtime('com.h2database:h2')
    testCompile('org.springframework.boot:spring-boot-starter-test')
}

```

### 添加配置

- application.properties

```
# DataSource
#spring.datasource.driver-class-name=com.mysql.jdbc.Driver
#spring.datasource.url=jdbc:mysql://localhost:3306/quartz?useSSL=false
#spring.datasource.username=root
#spring.datasource.password=123456
#spring.datasource.platform=mysql

spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:test
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.platform=h2

spring.datasource.initialization-mode=embedded
spring.datasource.schema=classpath:schema/schema-${spring.datasource.platform}.sql

spring.quartz.jdbc.initialize-schema=embedded
spring.quartz.job-store-type=jdbc

# MyBatis
mybatis.type-aliases-package=cn.com.hellowood.scheduledjob.dao
mybatis.mapper-locations=mappers/**Mapper.xml

# Log
logging.level.root=info
logging.level.cn.com.hellowood=trace

spring.profiles.active=dev
```

- 如果使用 MySQL 的数据库还需要创建 Quartz 相关的表，创建的脚本是 `schema/schema-mysql.sql`，其他的数据库脚本在 `org/quartz/impl/dbcjobstore/tables_@@platform@@.sql` 下

### 实现

- ScheduleConfig.java 

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;


@Configuration
public class ScheduleConfig {

    @Autowired
    private ScheduleJobFactory jobFactory;
    
    /**
     * To Configuration Quartz , not necessary, if not config this, will use default
     *
     * @param dataSource
     * @return
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(@Qualifier("dataSource") DataSource dataSource) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setSchedulerName("TASK_EXECUTOR");
        schedulerFactoryBean.setStartupDelay(10);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContextKey");
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setDataSource(dataSource);
        // 将 Job 交由 Spring 进行管理
        schedulerFactoryBean.setJobFactory(jobFactory);
        return schedulerFactoryBean;
    }
}
```

- ScheduleJobFactory.java
```java
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

@Component
public class ScheduleJobFactory extends AdaptableJobFactory {

    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;

    /**
     * 将 JobFactory 注入 Spring 容器中，用于依赖管理，否则会导致 Job 中注入的 Bean 为 null
     */
    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        autowireCapableBeanFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}
```

- ScheduleJob.java

```java
public class ScheduleJob implements Serializable {

    private static final Long serialVersionUID = 1435515995276255188L;

    private Long id;

    private String className;

    private String cronExpression;

    private String jobName;

    private String jobGroup;

    private String triggerName;

    private String triggerGroup;

    private Boolean pause;

    private Boolean enable;

    private String description;

    private Date createTime;

    private Date lastUpdateTime;

	// get, set ...
```

- ScheduleUtil.java

```java

import cn.com.hellowood.scheduledjob.model.ScheduleJob;
import cn.com.hellowood.scheduledjob.utils.ServiceException;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ScheduleUtil {

    private final static Logger logger = LoggerFactory.getLogger(ScheduleUtil.class);

    /**
     * 获取 Trigger Key
     *
     * @param scheduleJob
     * @return
     */
    public static TriggerKey getTriggerKey(ScheduleJob scheduleJob) {
        return TriggerKey.triggerKey(scheduleJob.getTriggerName(), scheduleJob.getTriggerGroup());
    }

    /**
     * 获取 Job Key
     *
     * @param scheduleJob
     * @return
     */
    public static JobKey getJobKey(ScheduleJob scheduleJob) {
        return JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
    }

    /**
     * 获取 Cron Trigger
     *
     * @param scheduler
     * @param scheduleJob
     * @return
     * @throws ServiceException
     */
    public static CronTrigger getCronTrigger(Scheduler scheduler, ScheduleJob scheduleJob) throws ServiceException {
        try {
            return (CronTrigger) scheduler.getTrigger(getTriggerKey(scheduleJob));
        } catch (SchedulerException e) {
            throw new ServiceException("Get Cron trigger failed", e);
        }
    }

    /**
     * 创建任务
     *
     * @param scheduler
     * @param scheduleJob
     * @throws ServiceException
     */
    public static void createScheduleJob(Scheduler scheduler, ScheduleJob scheduleJob) throws ServiceException {
        
        validateCronExpression(scheduleJob);
    
        try {
            // 要执行的 Job 的类
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(scheduleJob.getClassName()).newInstance().getClass();

            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(scheduleJob.getJobName(), scheduleJob.getJobGroup())
                    .withDescription(scheduleJob.getDescription())
                    .build();

            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression())
                    .withMisfireHandlingInstructionDoNothing();

            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(scheduleJob.getTriggerName(), scheduleJob.getTriggerGroup())
                    .withDescription(scheduleJob.getDescription())
                    .withSchedule(scheduleBuilder)
                    .startNow()
                    .build();

            scheduler.scheduleJob(jobDetail, cronTrigger);

            logger.info("Create schedule job {}-{} success", scheduleJob.getJobGroup(), scheduleJob.getJobName());

            if (scheduleJob.isPause()) {
                pauseJob(scheduler, scheduleJob);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Execute schedule job failed");
            throw new ServiceException("Execute schedule job failed", e);
        }
    }


    /**
     * 更新任务
     *
     * @param scheduler
     * @param scheduleJob
     * @throws ServiceException
     */
    public static void updateScheduleJob(Scheduler scheduler, ScheduleJob scheduleJob) throws ServiceException {
    
        validateCronExpression(scheduleJob);

        try {

            TriggerKey triggerKey = getTriggerKey(scheduleJob);

            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression())
                    .withMisfireHandlingInstructionDoNothing();

            CronTrigger cronTrigger = getCronTrigger(scheduler, scheduleJob);

            cronTrigger = cronTrigger.getTriggerBuilder()
                    .withIdentity(triggerKey)
                    .withDescription(scheduleJob.getDescription())
                    .withSchedule(cronScheduleBuilder).build();

            scheduler.rescheduleJob(triggerKey, cronTrigger);

            logger.info("Update schedule job {}-{} success", scheduleJob.getJobGroup(), scheduleJob.getJobName());

            if (scheduleJob.isPause()) {
                pauseJob(scheduler, scheduleJob);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("Update schedule job failed");
            throw new ServiceException("Update schedule job failed", e);
        }
    }

    /**
     * 执行任务
     *
     * @param scheduler
     * @param scheduleJob
     * @throws ServiceException
     */
    public static void run(Scheduler scheduler, ScheduleJob scheduleJob) throws ServiceException {
        try {
            scheduler.triggerJob(getJobKey(scheduleJob));
            logger.info("Run schedule job {}-{} success", scheduleJob.getJobGroup(), scheduleJob.getJobName());
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("Run schedule job failed");
            throw new ServiceException("Run schedule job failed", e);
        }
    }

    /**
     * 暂停任务
     *
     * @param scheduler
     * @param scheduleJob
     */
    public static void pauseJob(Scheduler scheduler, ScheduleJob scheduleJob) throws ServiceException {
        try {
            scheduler.pauseJob(getJobKey(scheduleJob));
            logger.info("Pause schedule job {}-{} success", scheduleJob.getJobGroup(), scheduleJob.getJobName());
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("Pause schedule job failed");
            throw new ServiceException("Pause job failed", e);
        }
    }

    /**
     * 继续执行任务
     *
     * @param scheduler
     * @param scheduleJob
     * @throws ServiceException
     */
    public static void resumeJob(Scheduler scheduler, ScheduleJob scheduleJob) throws ServiceException {
        try {
            scheduler.resumeJob(getJobKey(scheduleJob));
            logger.info("Resume schedule job {}-{} success", scheduleJob.getJobGroup(), scheduleJob.getJobName());
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("Resume schedule job failed");
            throw new ServiceException("Resume job failed", e);
        }
    }

    /**
     * 删除任务
     *
     * @param scheduler
     * @param scheduleJob
     * @throws ServiceException
     */
    public static void deleteJob(Scheduler scheduler, ScheduleJob scheduleJob) throws ServiceException {
        try {
            scheduler.deleteJob(getJobKey(scheduleJob));
            logger.info("Delete schedule job {}-{} success", scheduleJob.getJobGroup(), scheduleJob.getJobName());
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("Delete schedule job failed");
            throw new ServiceException("Delete job failed", e);
        }
    }
    
    /**
     * 校验 Cron 表达式
     *
     * @param scheduleJob
     * @throws ServiceException
     */
    public static void validateCronExpression(ScheduleJob scheduleJob) throws ServiceException {
        if (!CronExpression.isValidExpression(scheduleJob.getCronExpression())) {
            throw new ServiceException(String.format("Job %s expression %s is not correct!", scheduleJob.getClassName(), scheduleJob.getCronExpression()));
        }
    }
}
```

- JobService.java

```java

import cn.com.hellowood.scheduledjob.dao.JobDao;
import cn.com.hellowood.scheduledjob.job.ScheduleUtil;
import cn.com.hellowood.scheduledjob.model.ScheduleJob;
import cn.com.hellowood.scheduledjob.utils.ServiceException;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class JobService {

    @Autowired
    private JobDao jobDao;

    @Autowired
    private Scheduler scheduler;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public List<ScheduleJob> getAllEnableJob() {
        return jobDao.getAllEnableJob();
    }

    public ScheduleJob select(Long jobId) throws ServiceException {
        ScheduleJob scheduleJob = jobDao.select(jobId);
        if (scheduleJob == null) {
            throw new ServiceException("ScheduleJob:" + jobId + " not found");
        }
        return scheduleJob;
    }

    @Transactional(rollbackFor = DataAccessException.class)
    public ScheduleJob update(Long jobId, ScheduleJob scheduleJob) throws ServiceException {

        if (jobDao.update(scheduleJob) <= 0) {
            throw new ServiceException("Update product:" + jobId + "failed");
        }

        ScheduleUtil.updateScheduleJob(scheduler, scheduleJob);

        return scheduleJob;
    }

    @Transactional(rollbackFor = DataAccessException.class)
    public boolean add(ScheduleJob scheduleJob) throws ServiceException {
        Integer num = jobDao.insert(scheduleJob);
        if (num <= 0) {
            throw new ServiceException("Add product failed");
        }

        ScheduleUtil.createScheduleJob(scheduler, scheduleJob);

        return true;
    }

    @Transactional(rollbackFor = DataAccessException.class)
    public boolean delete(Long jobId) throws ServiceException {
        ScheduleJob scheduleJob = select(jobId);

        Integer num = jobDao.delete(jobId);
        if (num <= 0) {
            throw new ServiceException("Delete product:" + jobId + "failed");
        }

        ScheduleUtil.deleteJob(scheduler, scheduleJob);

        return true;
    }

    public List<ScheduleJob> getAllJob() {
        return jobDao.getAllJob();
    }

    public boolean resume(Long jobId) throws ServiceException {
        ScheduleJob scheduleJob = updateScheduleJobStatus(jobId, false);
        ScheduleUtil.resumeJob(scheduler, scheduleJob);
        return true;
    }

    public boolean pause(Long jobId) throws ServiceException {
        ScheduleJob scheduleJob = updateScheduleJobStatus(jobId, true);
        ScheduleUtil.pauseJob(scheduler, scheduleJob);
        return true;
    }

    public boolean run(Long jobId) throws ServiceException {
        ScheduleJob scheduleJob = updateScheduleJobStatus(jobId, false);
        ScheduleUtil.run(scheduler, scheduleJob);
        return true;
    }


    private ScheduleJob updateScheduleJobStatus(Long jobId, Boolean isPause) throws ServiceException {
        ScheduleJob scheduleJob = select(jobId);
        scheduleJob.setPause(isPause);
        update(scheduleJob.getId(), scheduleJob);
        return scheduleJob;
    }
}
```

- JobController.java

```java
import cn.com.hellowood.scheduledjob.common.CommonResponse;
import cn.com.hellowood.scheduledjob.common.ResponseUtil;
import cn.com.hellowood.scheduledjob.model.ScheduleJob;
import cn.com.hellowood.scheduledjob.service.JobService;
import cn.com.hellowood.scheduledjob.utils.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping
    public CommonResponse getAllJob() {
        return ResponseUtil.generateResponse(jobService.getAllJob());
    }

    @GetMapping("/{id}")
    public CommonResponse getJob(@PathVariable("id") Long jobId) throws ServiceException {
        return ResponseUtil.generateResponse(jobService.select(jobId));
    }

    @PutMapping("/update/{id}")
    public CommonResponse updateJob(@PathVariable("id") Long jobId, @RequestBody ScheduleJob newScheduleJob) throws ServiceException {
        return ResponseUtil.generateResponse(jobService.update(jobId, newScheduleJob));
    }

    @DeleteMapping("/delete/{id}")
    public CommonResponse deleteJob(@PathVariable("id") Long jobId) throws ServiceException {
        return ResponseUtil.generateResponse(jobService.delete(jobId));
    }

    @PostMapping("/save")
    public CommonResponse saveJob(@RequestBody ScheduleJob newScheduleJob) throws ServiceException {
        return ResponseUtil.generateResponse(jobService.add(newScheduleJob));
    }


    @PutMapping("/run/{id}")
    public CommonResponse runJob(@PathVariable("id") Long jobId) throws ServiceException {
        return ResponseUtil.generateResponse(jobService.run(jobId));
    }


    @PutMapping("/pause/{id}")
    public CommonResponse pauseJob(@PathVariable("id") Long jobId) throws ServiceException {
        return ResponseUtil.generateResponse(jobService.pause(jobId));
    }

    @PutMapping("/resume/{id}")
    public CommonResponse resumeJob(@PathVariable("id") Long jobId) throws ServiceException {
        return ResponseUtil.generateResponse(jobService.resume(jobId));
    }
}

```

- ApplicationListener.java

```java
import cn.com.hellowood.scheduledjob.job.ScheduleUtil;
import cn.com.hellowood.scheduledjob.model.ScheduleJob;
import cn.com.hellowood.scheduledjob.service.JobService;
import cn.com.hellowood.scheduledjob.utils.ServiceException;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationListener implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JobService jobService;

    @Autowired
    private Scheduler scheduler;

    @Override
    public void run(String... args) throws Exception {

        // 应用启动之后执行所有可执行的的任务
        List<ScheduleJob> scheduleJobList = jobService.getAllEnableJob();
        for (ScheduleJob scheduleJob : scheduleJobList) {
            try {
                CronTrigger cronTrigger = ScheduleUtil.getCronTrigger(scheduler, scheduleJob);
                if (cronTrigger == null) {
                    ScheduleUtil.createScheduleJob(scheduler, scheduleJob);
                } else {
                    ScheduleUtil.updateScheduleJob(scheduler, scheduleJob);
                }
                logger.info("Startup {}-{} success", scheduleJob.getJobGroup(), scheduleJob.getJobName());
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
    }
}
```

### 使用

- 添加任务 

```
POST /job/save HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cache-Control: no-cache
Postman-Token: c919bb29-40ab-45c8-2328-edd13c396d82

{
	"className":"cn.com.hellowood.scheduledjob.job.TestJob",
	"cronExpression":"*/10 * * * * ?",
	"jobName":"TestJob",
	"jobGroup":"TEST_GROUP",
	"triggerName":"TEST_TRIGGER",
	"triggerGroup":"TEST_GROUP",
	"pause": "false",
	"enable": "true",
	"description":"Test Job for SpringBoot"
}
```

- 暂停任务

```
GET /job/pause/1 HTTP/1.1
Host: localhost:8080
Cache-Control: no-cache
Postman-Token: a16d0256-0b2c-d2c4-6e96-21902c3ee88d
```

- 继续执行任务

```
GET /job/resume/1 HTTP/1.1
Host: localhost:8080
Cache-Control: no-cache
Postman-Token: e9c93950-e073-26de-9a75-1e281a23a719
```

- 更新任务

```
PUT /job/update/1 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cache-Control: no-cache
Postman-Token: 5aad8357-522e-9adb-7452-ed84c2383418

{
	"id":1,
	"className":"cn.com.hellowood.scheduledjob.job.TestJob",
	"cronExpression":"*/5 * * * * ?",
	"jobName":"testJob",
	"jobGroup":"TEST_GROUP",
	"triggerName":"TEST_TRIGGER",
	"triggerGroup":"TEST_GROUP",
	"pause": "false",
	"enable": "true",
	"description":"test Job for SpringBoot"
}
```

- 删除任务

```
DELETE /job/delete/1 HTTP/1.1
Host: localhost:8080
Cache-Control: no-cache
Postman-Token: a1331b4c-1a6a-5499-4106-50880e63e725

```

