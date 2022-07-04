# SpringBoot 中日志使用

## 配置日志输出到文件

```
# 输出到指定文件
logging.file=./logs/application.log

# 输出到指定目录下（会写入到 spring.log 中）
logging.path=./logs
```
当同时存在 `logging.path` 和 `logging.file` 时 `logging.path` 无效

## 日志中添加信息

向日志中添加自定义信息可以使用 `MDC`类来实现
- 配置文件
```
logging.pattern.level=user:%X{user} %5p
```

- 类：
	- `org.apache.log4j.MDC`
	- `org.jboss.logging.MDC `
	- `org.slf4j.MDC`
```
MDC.put("user", "Hello");
```

- 输出结果
```
2018-01-18 23:55:10.836 user:Hello  INFO 7011 --- [nio-8080-exec-1] cn.com.hellowood.log.LogController       : log 2
```

--------------------------

## logback 配置

### 标签属性

- `<configuration>`
	- `scan`: 当属性设置为 `true` 时， 配置文件如果发生变化会被重新加载，默认为`true`
	- `scanPeriod`: 设置监测配置文件是否修改的时间间隔，默认单位是毫秒，默认时间间隔1分钟
	- `debug`：当属性设置为`true`时，将打印`logback`内部的日志信息，默认为 `false`
- `<root>`：用来指定最基础的日志输出级别
    - `level`：设置日志打印级别，大小写不敏感，可以是`TRACE,DEBUG,INFO,ERROR,ALL,OFF`，默认`DEBUG`

```
<root level="debug">
  <appender-ref ref="console" />
  <appender-ref ref="file" />
</root>
```

- `<contextName>`：上下文名称，默认为`default`，用于区分应用程序，一般改为项目名称，可以通过 `%contextName` 添加到日志中

- `<property>`：用来定义变量值的标签
  - `name`：变量的名称
  - `value`：变量的值
 定义的变量可以通过`${}`使用

- `<appender>`：用来格式化日志输出节点
  - `name`：`appender` 的名称
  - `class`：用来指定输出策略，通用的有控制台输出和文件输出
  - 子标签 `<layout>`和`<encoder>`作用一样，但是`<layout>`仅仅是将 `event` 事件变成字符串，而 `<encoder>`将 `event`事件变成 `byte`数组并输出到文件中

  - `<logger>`：指定某个包或类的日志属性
    - `name`：类或包名
    - `level`：日志级别
    - `addtivity`：日志是否向上级传递打印，默认是 `true`
    - 子节点`<appender-ref>`：用于指定输出到那个 `appender`，用`ref`属性指定
```
<configuration>
    ...
    
    <!--类名或包名 -->
    <logger name="cn.com.hellowood" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
    </logger>
</configuration>
```
此时会打印在 `CONSOLE`上，如果将`additivity`改为`true`，则`CONSOLE`和`root`各会打印一次

- `<springProfile>`：设置不同环境下启用不同的规则
  -  `name` ：环境的名称，可以是`test`, `dev`, `prod`, `staging`等；和 `application-${env}.properties`一直即可

```
<!-- 环境用逗号隔开 -->
<springProfile name="test,dev">
    <logger name="cn.com.hellowood" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
    </logger>
</springProfile>
```

- `<springProperty>` ：通过 Spring 的 `Enviroment`获取属性，用于从`application.properties`读取参数，作用和 `<property>`一样
  - `scope`：读取属性的范围，一般为`context`
  - `name`：属性名称
  - `source`：`application.properties`文件里的配置名称，必须是串的形式（`app.property`）
  - `defaultValue`：默认值


-------------

## 配置文件

- 常用配置

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    
    <springProperty name="LOG_PATH" value="SpringBoot-Log/out/logs"/>
    <contextName>SpringBootLog</contextName>
    <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter" />
    <conversionRule conversionWord="wex" converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter" />
    <conversionRule conversionWord="wEx" converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter" />
    <property name="CONSOLE_LOG_PATTERN" value="${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%21.21t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"/>
    <property name="FILE_LOG_PATTERN" value="${FILE_LOG_PATTERN:-%d{yyyy-MM-dd HH:mm:ss.SSS} ${LOG_LEVEL_PATTERN:-%5p} ${PID:- } --- [%21.21t] %-40.40logger{39} : %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"/>

    
    <!--输出到控制台 ConsoleAppender-->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %contextName [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

	<!-- 输出到文件 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文件的路径及文件名 -->
        <file>${LOG_PATH}/SpringBootLog.log</file>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
             <!--归档的日志文件的路径，%d{yyyy-MM-dd}指定日期格式，%i指定索引 -->
            <fileNamePattern>${LOG_PATH}/SpringBootLog-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <!-- 若超过10M，日志文件会以索引0开始 -->
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文件保留天数-->
            <MaxHistory>30</MaxHistory>
        </rollingPolicy>
        <!-- 追加方式记录日志 -->
        <append>true</append>
        <!-- 日志文件的格式 -->
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>${FILE_LOG_PATTERN}</pattern>
            <charset>utf-8</charset>
        </encoder>
        <!--设置日志输出级别-->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>
	<!-- 指定包的日志级别 -->
    <logger name="org.springframework" level="WARN" />

    <!--指定最基础的日志输出级别-->
    <root level="INFO">
        <!--appender将会添加到这个logger-->
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>

</configuration>
```

---------------------

## Pattern 属性

- `%p`: 输出日志信息优先级，即`DEBUG`，`INFO`，`WARN`，`ERROR`，`FATAL`,
 
- `%d`: 输出日志时间点的日期或时间，默认格式为`ISO8601`，也可以在其后指定格式，比如：`%d{yyyy-MM-dd HH:mm:ss.SSS}`，输出类似：`2018-01-19 14:47:03.735`
 
- `%r`: 输出自应用启动到输出该log信息耗费的毫秒数
 
- `%c`: 输出日志信息所属的类目，通常就是所在类的全名
 
- `%t`: 输出产生该日志事件的线程名
 
- `%l`: 输出日志事件的发生位置，相当于`%C.%M(%F:%L)`的组合,包括类目名、发生的线程，以及在代码中的行数
 
- `%x`: 输出和当前线程相关联的NDC(嵌套诊断环境),尤其用到像java servlets这样的多客户多线程的应用中
 
- `%%`: 输出一个`%`字符, `%F`: 输出日志消息产生时所在的文件名称；`%L`: 输出代码中的行号；`%m`: 输出代码中指定的消息,产生的日志具体信息
 
- `%n`: 输出一个回车换行符，Windows平台为`\r\n`，Unix平台为`\n`输出日志信息换行

- 可以在`%`与模式字符之间加上修饰符来控制其最小宽度、最大宽度、和文本的对齐方式。如：
 
   - `%20c`：指定输出`category`的名称，最小的宽度是20，如果category的名称小于20的话，默认的情况下右对齐。
 
   - `%-20c`:指定输出`category`的名称，最小的宽度是20，如果category的名称小于20的话，`-`号指定左对齐。
 
   - `%.30c`:指定输出`category`的名称，最大的宽度是30，如果`category`的名称大于30的话，就会将左边多出的字符截掉，但小于30的话也不会有空格。
 
   - `%20.30c`:如果`category`的名称小于20就补空格，并且右对齐，如果其名称长于30字符，就从左边交远销出的字符截掉。

--------------------------

- [日志输出到数据库或 Logstash](./LogToLogstahAndDB.md)