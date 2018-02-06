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
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BaseService<T> implements CommonService<T> {

    private Class<T> modelClass;

    @Autowired
    protected CommonMapper<T> commonMapper;

    @Autowired
    HttpServletRequest request;

    public BaseService() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.modelClass = (Class<T>) type.getActualTypeArguments()[0];
    }


    @Override
    public Integer save(T model) {
        return commonMapper.insertSelective(model);
    }

    @Override
    public Integer save(List<T> models) {
        return commonMapper.insertList(models);
    }

    @Override
    public Integer deleteById(Serializable id) {
        return commonMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Integer deleteByIds(String ids) {
        return commonMapper.deleteByIds(ids);
    }

    @Override
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
package cn.com.hellowood.mapper.modal;

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
import cn.com.hellowood.mapper.modal.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductDao extends CommonMapper<Product> {
}

```
- ProductMapper.xml
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="cn.com.hellowood.mapper.dao.ProductDao">

    <resultMap id="baseResultMap" type="cn.com.hellowood.mapper.modal.Product">
        <id column="id" property="id" javaType="java.lang.Integer" jdbcType="INTEGER"></id>
        <result column="name" property="name" javaType="java.lang.String" jdbcType="VARCHAR"></result>
        <result column="price" property="price" javaType="java.lang.Double" jdbcType="BIGINT"></result>
    </resultMap>

</mapper>
```

## 使用
> 直接调用 Service 的方法就可以实现正常的单表 CRUD，但是对于多表的操作依然需要写在 XML 里


--------------------------

## MyBatis Plus 和 通用 Mapper+PageHelper 比较
> MyBatis Plus 在功能上更加强大，支持单表的 CRUD，分页，单表多条件查询，还支持乐观锁，热更新，条件构造等等功能，对于单表几乎不需要写 SQL 语句就可以实现几乎所有的操作；但是其分页相对比较弱，需要传入指定对象，并且不能直接返回分页的信息；
> 通用 Mapper 相对比较轻量，也可是实现单表的 CRUD 操作；配合 PageHelper 实现分页可以满足正常的使用，可以直接通过 PageInfo 对象返回分页信息；
> MyBatis Plus 和 PageHelper 又似乎有些多余，但是却是目前最好的方式，既可以实现单表的条件构造查询，又可以较好的实现分页，但是有两种分页方式一起存在又会显得多余