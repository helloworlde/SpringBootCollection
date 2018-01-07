# Spring Boot 使用 Flyway

> [Flyway](https://flywaydb.org) 是一个数据库版本管理工具，用于管理数据库操作脚本

## 添加依赖
```groovy
compile('org.flywaydb:flyway-core')
compile('org.mybatis.spring.boot:mybatis-spring-boot-starter:1.3.1')
runtime('mysql:mysql-connector-java')
//runtime('com.h2database:h2')
```    
## 配置 

 - 配置数据库
 
 ```properties
# H2 数据库
#spring.datasource.url=jdbc:h2:mem:test
#spring.datasource.username=root
#spring.datasource.password=123456
#spring.datasource.driver-class-name=org.h2.Driver

# MySQL 数据库
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/product?useSSL=false
spring.datasource.username=root
spring.datasource.password=123456
```

 - 使用 `V_VARSION__DESCRIPTION.sql` 方式命名脚本
 
```sql
CREATE TABLE product(
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  price DOUBLE NOT NULL DEFAULT 0
);
``` 

 - 将 SQL 脚本放在 `resources/db/migration` 目录下
 
## 使用

此时启动应用，Flyway 将会自动执行脚本进行数据库操作

- 第一次启动时将会看到如下日志

```text
2018-01-07 21:00:14.932  INFO 5041 --- [           main] o.f.core.internal.util.VersionPrinter    : Flyway 3.2.1 by Boxfuse
2018-01-07 21:00:17.015  INFO 5041 --- [           main] o.f.c.i.dbsupport.DbSupportFactory       : Database: jdbc:mysql://localhost:3306/product?useSSL=false (MySQL 5.7)
2018-01-07 21:00:17.253  INFO 5041 --- [           main] o.f.core.internal.command.DbValidate     : Validated 1 migration (execution time 00:00.133s)
2018-01-07 21:00:17.750  INFO 5041 --- [           main] o.f.c.i.metadatatable.MetaDataTableImpl  : Creating Metadata table: `product`.`schema_version`
2018-01-07 21:00:18.437  INFO 5041 --- [           main] o.f.core.internal.command.DbMigrate      : Current version of schema `product`: << Empty Schema >>
2018-01-07 21:00:18.437  INFO 5041 --- [           main] o.f.core.internal.command.DbMigrate      : Migrating schema `product` to version 1.0 - 0001 CREATE PRODUCT
2018-01-07 21:00:19.284  INFO 5041 --- [           main] o.f.core.internal.command.DbMigrate      : Successfully applied 1 migration to schema `product` (execution time 00:01.592s).

```
- 再次启动将会看到 Flyway 校验版本

```text
2018-01-07 21:02:09.195  INFO 5061 --- [           main] o.f.core.internal.util.VersionPrinter    : Flyway 3.2.1 by Boxfuse
2018-01-07 21:02:11.327  INFO 5061 --- [           main] o.f.c.i.dbsupport.DbSupportFactory       : Database: jdbc:mysql://localhost:3306/product?useSSL=false (MySQL 5.7)
2018-01-07 21:02:11.618  INFO 5061 --- [           main] o.f.core.internal.command.DbValidate     : Validated 1 migration (execution time 00:00.202s)
2018-01-07 21:02:12.386  INFO 5061 --- [           main] o.f.core.internal.command.DbMigrate      : Current version of schema `product`: 1.0
2018-01-07 21:02:12.447  INFO 5061 --- [           main] o.f.core.internal.command.DbMigrate      : Schema `product` is up to date. No migration necessary.

```

- 如果不想 Flyway 执行，可以配置不启用 Flyway

```properties
flyway.enabled=false
```

## 注意
> 如果按照 Flyway [官方文档](https://flywaydb.org/documentation/plugins/springboot) 的指导，仅配置 Flyway，应用启动时并不会执行 Flyway 的任何操作，这是因为 [`FlywayAutoConfiguration 类`](https://docs.spring.io/spring-boot/docs/1.4.x/api/org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration.html)在启动时要求有 DataSource 的实例，如果没有配置，就不会执行 Flyway，所以在依赖里添加了 MyBatis(或 JPA) ，使用 MyBatis(或 JPA) 时会自动注入数据源，因此才会执行 Flyway，具体可以参考[https://github.com/spring-projects/spring-boot/issues/8649](https://github.com/spring-projects/spring-boot/issues/8649), [https://stackoverflow.com/questions/43496506/how-to-debug-when-flyway-doesnt-work-on-spring-boot](https://stackoverflow.com/questions/43496506/how-to-debug-when-flyway-doesnt-work-on-spring-boot)