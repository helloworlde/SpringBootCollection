# ELK 日志收集系统安装配置

## Elasticsearch 

- 下载 [Elasticsearch](https://www.elastic.co/cn/downloads/elasticsearch)

- 解压并运行 `bin/elasticsearch`

- 访问 `http://localhost:9200`, 会看到以下内容

```
{
  "name" : "9KA6kPN",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "BtscSPXeR0manhl2LibpuA",
  "version" : {
    "number" : "6.1.1",
    "build_hash" : "bd92e7f",
    "build_date" : "2017-12-17T20:23:25.338Z",
    "build_snapshot" : false,
    "lucene_version" : "7.1.0",
    "minimum_wire_compatibility_version" : "5.6.0",
    "minimum_index_compatibility_version" : "5.0.0"
  },
  "tagline" : "You Know, for Search"
}
```

- 安装 Head 插件

```
bin/plugin install mobz/elasticsearch-head
```

## Logstash

- 下载 [Logstash](https://www.elastic.co/cn/downloads/logstash)

- 解压

- 添加配置

添加 `log_to_es.conf`

```
input {
  tcp {
    host => "localhost"
    port => 4560
    mode => "server"
    tags => ["applog"]
    codec => json_lines
  }
}
output {
 stdout{codec =>rubydebug}
  elasticsearch {
    action => "index"            #The operation on ES
    hosts  => "localhost:9200"   #ElasticSearch host, can be array.
    index  => "applog"           #The index to write data to.
 }
}
```

这是从本地通过 TCP 读取读取日志并发送到 ElasticSearch

- 启动
```
bin/logstash -f log_to_es.conf
```

## Kibana

- 下载 [Kibana](https://www.elastic.co/cn/downloads/kibana)

- 解压运行 `bin/kibana`

- 访问 `http://localhost:5601`, 可以看到 Kibana 主界面

## 配置应用

- 添加依赖

```groovy
dependencies {
	compile('org.springframework.boot:spring-boot-starter-web')
	compile('org.springframework.boot:spring-boot-starter-logging')
	compile('net.logstash.logback:logstash-logback-encoder:4.11')
	compile('net.logstash.log4j:jsonevent-layout:1.7')
	testCompile('org.springframework.boot:spring-boot-starter-test')
}
```

- 添加 logback 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter" />
    <conversionRule conversionWord="wex" converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter" />
    <conversionRule conversionWord="wEx" converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter" />

    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>127.0.0.1:4560</destination>
        <encoder charset="UTF-8" class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder charset="UTF-8"> <!-- encoder 可以指定字符集，对于中文输出有意义 -->
            <pattern>${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%21.21t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="LOGSTASH"/>
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```
- 在应用中输出日志

```java
    @GetMapping("/")
    public void generateLog() {
        for (int i = 0; i < 20; i++) {
            logger.info("log " + Math.random());
        }
    }
```

- 运行应用并访问