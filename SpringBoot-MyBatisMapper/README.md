# SpringBoot 使用 MyBatis 通用 Mapper

> [通用 Mapper](https://github.com/abel533/Mapper) 是一个开源的 MyBatis 插件，该插件可以使用通用的 MyBatis方法，减少简单的 CRUD，提高开发效率；推荐搭配分页插件 [PageHelper](https://github.com/pagehelper/Mybatis-PageHelper) 一起使用；通用 Mapper 插件虽然没有 [MyBatis Plus](https://github.com/baomidou/mybatis-plus) 强大，但是满足正常项目开发；

## 添加依赖 
- build.gradle
```gradle
compile('com.github.pagehelper:pagehelper-spring-boot-starter:1.2.3')
compile('tk.mybatis:mapper:3.4.2')
```

## 配置
- application.properties
```
# DataSource
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/product?useSSL=false
spring.datasource.username=root
spring.datasource.password=123456

# MyBatis
mybatis.type-aliases-package=cn.com.hellowood.mybatisplus.dao
mybatis.mapper-locations=mappers/**Mapper.xml

# PageHelper
pagehelper.helper-dialect=mysql
pagehelper.reasonable=true
pagehelper.support-methods-arguments=true
pagehelper.params=count=countSql
pagehelper.row-bounds-with-count=true
pagehelper.offset-as-page-num=true
pagehelper.page-size-zero=true

# Log
logging.level.root=info
logging.level.cn.com.hellowood=trace

spring.profiles.active=dev
```

- MyBatisConfig.java
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import java.util.Properties;

@Configuration
public class MyBatisConfig {

    private final String MAPPER_PACKAGE = "cn.com.hellowood.mapper.dao";
    private final String MAPPER_INTERFACE_REFERENCE = "cn.com.hellowood.mapper.common.CommonMapper";

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        mapperScannerConfigurer.setBasePackage(MAPPER_PACKAGE);

        Properties properties = new Properties();
        properties.setProperty("mappers", MAPPER_INTERFACE_REFERENCE);
        properties.setProperty("notEmpty", "false");
        properties.setProperty("IDENTIFY", "MYSQL");
        mapperScannerConfigurer.setProperties(properties);

        return mapperScannerConfigurer;
    }
}

```

- BaseService.java
```java
package cn.com.hellowood.mapper.service;

import cn.com.hellowood.mapper.common.CommonMapper;
import cn.com.hellowood.mapper.common.CommonService;
import cn.com.hellowood.mapper.utils.ServiceException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * The type Base service.
 *
 * @param <T> the type parameter
 */
public abstract class BaseService<T> implements CommonService<T> {

    /**
     * real class type of current generic
     */
    private Class<T> modelClass;

    @Autowired
    protected CommonMapper<T> commonMapper;

    @Autowired
    HttpServletRequest request;

    /**
     * Instantiates a new Base service.
     */
    public BaseService() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.modelClass = (Class<T>) type.getActualTypeArguments()[0];
    }


    @Override
    @Transactional
    public Integer save(T model) {
        return commonMapper.insertSelective(model);
    }

    @Override
    @Transactional
    public Integer save(List<T> models) {
        return commonMapper.insertList(models);
    }

    @Override
    @Transactional
    public Integer deleteById(Serializable id) {
        return commonMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public Integer deleteByIds(String ids) {
        return commonMapper.deleteByIds(ids);
    }

    @Override
    @Transactional
    public Integer update(T model) {
        return commonMapper.updateByPrimaryKeySelective(model);
    }

    @Override
    public T getById(Serializable id) {
        return commonMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<T> getByField(String fieldName, Object value) throws ServiceException {
        try {
            T model = modelClass.newInstance();
            Field field = modelClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(model, value);
            return commonMapper.select(model);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<T> getByIds(String ids) {
        return commonMapper.selectByIds(ids);
    }

    @Override
    public List<T> getByCondition(Condition condition) {
        return commonMapper.selectByCondition(condition);
    }

    @Override
    public List<T> getAll() {
        PageHelper.startPage(0, 0);
        return commonMapper.selectAll();
    }

    @Override
    public PageInfo<T> getPage() {
        PageHelper.startPage(request);
        List<T> list = commonMapper.selectAll();
        return new PageInfo<T>(list);
    }

}

```
- CommonMapper.java
```java
package cn.com.hellowood.mapper.common;

import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

public interface CommonMapper<T> extends BaseMapper<T>,
        ConditionMapper<T>,
        IdsMapper<T>,
        InsertListMapper<T> {
}
```

- Product.java
```java
package cn.com.hellowood.mapper.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private Double price;

    // get, set ...
}
```

- ProductDao.java
```java
package cn.com.hellowood.mapper.dao;


import cn.com.hellowood.mapper.common.CommonMapper;
import cn.com.hellowood.mapper.model.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductDao extends CommonMapper<Product> {
}

```
- ProductMapper.xml
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="cn.com.hellowood.mapper.mapper.ProductMapper">

    <resultMap id="baseResultMap" type="cn.com.hellowood.mapper.model.Product">
        <id column="id" property="id" javaType="java.lang.Integer" jdbcType="INTEGER"></id>
        <result column="name" property="name" javaType="java.lang.String" jdbcType="VARCHAR"></result>
        <result column="price" property="price" javaType="java.lang.Double" jdbcType="BIGINT"></result>
    </resultMap>

</mapper>
```

## 使用
> 直接调用 Service 的方法就可以实现正常的单表 CRUD，但是对于多表的操作依然需要写在 XML 里


--------------------

### 注意

- 使用 `CodeGenerator` 生成  `Mapper` 接口后需要添加 `@Mapper` 注解，否则会报错，或者在 `Application.java` 添加 `@MapperScan("MAPPER_INTERFACE_PACKAGE")`

- 生成 `Service` 接口后必须有 `Mapper` 接口，因为在 `Service` 中继承了 `BaseService` , 必须要有相应的 `Mapper` 实现其 `CRUD`，如果没有 `Mapper` 接口，会在启动过程中提示无法注入通用` Mapper` 错误