# SpringBoot 在启动时执行数据库脚本

> SpringBoot 在启动的过程中执行数据库的初始化总共有三种方式：
> - 通过 SQL 脚本执行初始化
> - 通过 [Flyway](https://flywaydb.org)  脚本工具执行初始化
> - 通过 [Liquibase](http://www.liquibase.org/) 脚本工具执行初始化

## 通过 SQL 脚本

### 添加脚本

- 在 `resources`目录下添加 `schema.sql`和 `data.sql` 脚本
`schema.sql`用于建立数据库，表等操作；`data.sql`用于向表中插入初始化数据

### 配置 

- 在 `application.properties`中添加如下配置

```
# 应用启动时执行脚本
spring.datasource.initialize=true
# 当脚本出错后继续执行(避免再次启动时因SQL脚本错误而停止启动)
spring.datasource.continue-on-error=true
# 自定义 schema.sql 脚本位置
#spring.datasource.schema=db
# 自定义 data.sql 脚本位置
#spring.datasource.data=db
```

-----------------------------

## 通过 [Flyway](https://flywaydb.org)  执行

### 添加依赖
```groovy
compile('org.flywaydb:flyway-core')
compile('org.mybatis.spring.boot:mybatis-spring-boot-starter:1.3.1')
runtime('mysql:mysql-connector-java')
//runtime('com.h2database:h2')
```    
### 配置 

 - 配置数据库
```
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

- 将 SQL 脚本放在 `resources/db/migration` 目录下，启动应用即可
- 如果不想 Flyway 执行，可以配置不启用 Flyway

```
flyway.enabled=false
```


-----------------------------

## 通过 [Liquibase](http://www.liquibase.org/) 执行
- 添加依赖 

```gradle
compile('org.liquibase:liquibase-core')
```

- 创建 `db/changelog/db.changelog-master.yaml`
- 添加配置

```
databaseChangeLog:
  - changeSet:
      id: 1
      author: marceloverdijk
      changes:
        - createTable:
            tableName: person
            columns:
              - column:
                  name: id
                  type: int
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: first_name
                  type: varchar(255)
                  constraints:
                    nullable: false
              - column:
                  name: last_name
                  type: varchar(255)
                  constraints:
                    nullable: false
  - changeSet:
      id: 2
      author: marceloverdijk
      changes:
        - insert:
            tableName: person
            columns:
              - column:
                  name: first_name
                  value: Marcel
              - column:
                  name: last_name
                  value: Overdijk
```

- 启动项目就会看到数据库迁移开始执行 