DROP TABLE IF EXISTS schedule_job;

CREATE TABLE schedule_job (
  id               INT       AUTO_INCREMENT PRIMARY KEY ,
  class_name       VARCHAR(200),
  cron_expression  VARCHAR(50),
  job_name         VARCHAR(100),
  job_group        VARCHAR(100),
  trigger_name     VARCHAR(100),
  trigger_group    VARCHAR(100),
  pause            BOOLEAN   DEFAULT FALSE,
  enable           BOOLEAN   DEFAULT TRUE,
  description      VARCHAR(500),
  create_time      TIMESTAMP DEFAULT current_timestamp,
  last_update_time TIMESTAMP DEFAULT current_timestamp
);

INSERT INTO schedule_job (id, class_name, cron_expression, job_name, job_group, trigger_name, trigger_group, pause, enable, description, create_time, last_update_time)
VALUES (1, 'cn.com.hellowood.scheduledjob.job.TestJob', '*/10 * * * * ?', 'testJob', 'TEST_GROUP', 'TEST_TRIGGER',
           'TEST_GROUP', 0, 1, 'test Job for SpringBoot', '2018-02-12 14:14:03', '2018-02-12 15:23:24');