# SpringBoot logback 输出日志到数据库、Logstash

## 输入日志到数据库

- 创建表

```
DROP TABLE IF EXISTS logging_event;
CREATE TABLE logging_event
(
  timestmp          BIGINT       NOT NULL,
  formatted_message TEXT         NOT NULL,
  logger_name       VARCHAR(254) NOT NULL,
  level_string      VARCHAR(254) NOT NULL,
  thread_name       VARCHAR(254),
  reference_flag    SMALLINT,
  arg0              VARCHAR(254),
  arg1              VARCHAR(254),
  arg2              VARCHAR(254),
  arg3              VARCHAR(254),
  caller_filename   VARCHAR(254) NOT NULL,
  caller_class      VARCHAR(254) NOT NULL,
  caller_method     VARCHAR(254) NOT NULL,
  caller_line       CHAR(4)      NOT NULL,
  event_id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY
);

DROP TABLE IF EXISTS logging_event_property;
CREATE TABLE logging_event_property
(
  event_id     BIGINT       NOT NULL,
  mapped_key   VARCHAR(254) NOT NULL,
  mapped_value TEXT,
  PRIMARY KEY (event_id, mapped_key),
  FOREIGN KEY (event_id) REFERENCES logging_event (event_id)
);

DROP TABLE IF EXISTS logging_event_exception;
CREATE TABLE logging_event_exception
(
  event_id   BIGINT       NOT NULL,
  i          SMALLINT     NOT NULL,
  trace_line VARCHAR(254) NOT NULL,
  PRIMARY KEY (event_id, i),
  FOREIGN KEY (event_id) REFERENCES logging_event (event_id)
);
```
- 配置`logback.xml`
```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!--日志异步到数据库 -->
    <appender name="DB" class="ch.qos.logback.classic.db.DBAppender">
        <!--日志异步到数据库-->
        <connectionSource class="ch.qos.logback.core.db.DriverManagerConnectionSource">
            <driverClass>com.mysql.jdbc.Driver</driverClass>
            <url>jdbc:mysql://ali.hellowood.com.cn:3306/log?useSSL=false</url>
            <user>root</user>
            <password>ihaveapen*^@#</password>
        </connectionSource>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="DB"/>
    </root>
</configuration>
```


## 输入日志到 [`Logstash`](https://www.elastic.co/cn/products/logstash)

- logback.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
  
    <!--日志导出的到 Logstash-->
    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>127.0.0.1:4560</destination>
        <encoder chaset="UTF-8" class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"appname":"SpringBootLog"}</customFields>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="LOGSTASH"/>
    </root>
</configuration>
```
