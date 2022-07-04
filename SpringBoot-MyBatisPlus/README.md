# SpringBoot 使用 MyBatis Plus

> [MyBatis Plus](https://github.com/baomidou/mybatis-plus) 是 MyBatis 的工具包，用于处理通用的 MyBatis 增删改查、分页等操作

## 使用 

### 添加依赖
- build.gradle

```gradle
compile('com.baomidou:mybatis-plus-boot-starter:2.1.9')
```

### 配置 
- application.properties

```
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/product?useSSL=false
spring.datasource.username=root
spring.datasource.password=123456

mybatis.type-aliases-package=cn.com.hellowood.mybatisplus.dao
mybatis.mapper-locations=mappers/**Mapper.xml

logging.level.root=info
logging.level.cn.com.hellowood=trace

spring.profiles.active=dev

mybatis-plus.type-aliases-package=cn.com.hellowood.mybatisplus.dao
mybatis-plus.mapper-locations=mappers/**Mapper.xml
# 主键类型 0:数据库ID自增 1:用户输入ID 2:全局唯一ID(数字类型唯一ID) 3:全局唯一ID(UUID)
mybatis-plus.global-config.id-type=0
# 字段策略 0:忽略判断 1:非NULL判断 2:非空判断
mybatis-plus.global-config.field-strategy=1
# 驼峰下划线转换
mybatis-plus.global-config.db-column-underline=true
# 刷新mapper
mybatis-plus.global-config.refresh-mapper=true
# 逻辑删除配置
mybatis-plus.global-config.logic-delete-value=-1
mybatis-plus.global-config.logic-not-delete-value=0
# 将下划线转为驼峰命名
mybatis-plus.configuration.map-underscore-to-camel-case=true
mybatis-plus.configuration.cache-enabled=true
```

- MyBatisPlusConfig.java

```java
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.plugins.PerformanceInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class MyBatisPlusConfig {

    @Bean
    @Profile({"dev", "test"})
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        performanceInterceptor.setMaxTime(1000);
        return performanceInterceptor;
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setLocalPage(true);
        return paginationInterceptor;
    }
}

```

### 使用

#### 修改 Modal
- Product.java
```
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

@TableName(value = "product", resultMap = "baseResultMap")
public class Product implements Serializable {
    private static final long serialVersionUID = 1435515995276255188L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField
    private String name;

    @TableField
    private Long price;

    public Product() {
    }

    public Product(Long id, String name, Long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // get, set ...
}

```

#### 修改 Dao 层接口

```java
import cn.com.hellowood.mybatisplus.modal.Product;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductDao extends BaseMapper<Product> {

}
```
#### 修改 XML
 
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="cn.com.hellowood.mybatisplus.dao.ProductDao">

    <resultMap id="baseResultMap" type="cn.com.hellowood.mybatisplus.modal.Product">
        <id column="id" property="id" javaType="java.lang.Long" jdbcType="INTEGER"></id>
        <result column="name" property="name" javaType="java.lang.String" jdbcType="VARCHAR"></result>
        <result column="price" property="price" javaType="java.lang.Long" jdbcType="BIGINT"></result>
    </resultMap>
</mapper>

```


#### 通用方法
- `Integer insert(T entity)`
- `Integer insertAllColumn(T entity)`
- `Integer deleteById(Serializable id)`
- `Integer deleteByMap(@Param("cm") Map<String, Object> columnMap)`
- `Integer delete(@Param("ew") Wrapper<T> wrapper)`
- `Integer deleteBatchIds(@Param("coll") Collection<? extends Serializable> idList)`
- `Integer updateById(@Param("et") T entity)`
- `Integer updateAllColumnById(@Param("et") T entity)`
- `Integer update(@Param("et") T entity, @Param("ew") Wrapper<T> wrapper)`
- `T selectById(Serializable id)`
- `List<T> selectBatchIds(@Param("coll") Collection<? extends Serializable> idList)`
- `List<T> selectByMap(@Param("cm") Map<String, Object> columnMap)`
- `T selectOne(@Param("ew") T entity)`
- `Integer selectCount(@Param("ew") Wrapper<T> wrapper)`
- `List<T> selectList(@Param("ew") Wrapper<T> wrapper)`
- `List<Map<String, Object>> selectMaps(@Param("ew") Wrapper<T> wrapper)`
- `List<Object> selectObjs(@Param("ew") Wrapper<T> wrapper)`
- `List<T> selectPage(RowBounds rowBounds, @Param("ew") Wrapper<T> wrapper)`
- `List<Map<String, Object>> selectMapsPage(RowBounds rowBounds, @Param("ew") Wrapper<T> wrapper)`

------------------------

### 条件构造器
> [http://baomidou.oschina.io/mybatis-plus-doc/#/wrapper](http://baomidou.oschina.io/mybatis-plus-doc/#/wrapper)

### 分页查询
```java
Page page = new Page(pageNum, pageSize);
EntityWrapper<Product> wrapper = new EntityWrapper<>();
List<Product> products = productDao.selectPage(page, wrapper);
page.setRecords(products);
```
这种分页方式并不是好用，每次都需要 new Page 对象，而且 Page 对象里没有任何分页的信息；推荐结合 [`PageHelper`](https://github.com/pagehelper/Mybatis-PageHelper)使用