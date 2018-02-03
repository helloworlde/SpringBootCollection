# SpringBoot 使用 MyBatis 分页插件

>  [MyBatis PageHelper](https://github.com/pagehelper/Mybatis-PageHelper) 是一个 MyBatis 分页插件，能够比较方便的实现 MyBatis 的分页

## 添加依赖

- build.gradle 

```gradle
compile('com.github.pagehelper:pagehelper-spring-boot-starter:1.2.3')
```

## 添加配置 
- application.properties

```
pagehelper.helper-dialect=mysql
pagehelper.reasonable=true
pagehelper.support-methods-arguments=true
pagehelper.params=count=countSql
pagehelper.row-bounds-with-count=true
pagehelper.offset-as-page-num=true
pagehelper.page-size-zero=true

```
- `pagehelper.helper-dialect` : 指定分页插件使用哪种语言
- `pagehelper.offset-as-page-num`: 默认为 `false`, 该参数对使用`RowBounds`作为分页参数时有效，当为`true`时，会将`RowBounds`的`offset`参数当成`pageNum`使用
- `pagehelper.row-bounds-with-count`: 默认为`false`，该参数对使用`RowBounds`作为分页参数时有效，当该参数值为`true`时，使用`RowBounds`分页会进行`count`查询
- `pagehelper.page-size-zero`: 默认为`false`,当该参数为`true`时，如果`pageSize=0`或者`RowBounds.limit=0`就会查询所有结果
- `pagehelper.reasonable`: 分页合理化参数，默认为`false`，当该值为`true`，`pageNum<=0`默认查询第一页，`pageNum>pages`时会查询最后一页，`false`时直接根据参数进行查询
- `pagehelper.params`: 为了支持`startPage(Object params)`方法，增加该参数来配置参数映射，用于从对象中根据属性名取值，可以配置`pageNum,pageSize,pageSizeZero, reasonable`, 不配置映射是使用默认值， 默认值为`pageNum=pageNum;pageSize=pageSize;count=countSql;reasonable=reasonable;pageSizeZero=pageSizeZero`
- `pagehelper.support-methods-arguments`: 支持通过 `Mapper`接口参数来传递分页参数，默认为`false`, 分页插件会从查询方法的参数中根据`params`配置的字段中取值，查找到合适的就进行分页
- `pagehelper.auto-runtime-dialect`: 默认为`false`, 为`true`时允许在运行时根据多数据源自动识别对应的方言进行分页
- `pagehelper.close-conn`: 默认为`true`, 当使用运行是动态数据源或者没有设置`helperDialect`属性自动获取数据库类型时，会自动获取一个数据库连接，通过该属性来设置是否关闭获取的这个连接，默认为`true`关闭，`false`不会自动关闭


## 使用
- Product.java
```java
public class Product implements Serializable {

    private long id;

    private String name;

    private long price;

    private Integer pageNum;

    private Integer pageSize;

    // ...
}
```
- ProductMapper.xml

```xml
<resultMap id="baseResultMap" type="cn.com.hellowood.mybatis.modal.Product">
    <id column="id" property="id" javaType="java.lang.Long" jdbcType="INTEGER"></id>
    <result column="name" property="name" javaType="java.lang.String" jdbcType="VARCHAR"></result>
    <result column="price" property="price" javaType="java.lang.Long" jdbcType="BIGINT"></result>
</resultMap>

```


### 使用`PageHelper.startPage()`或 `PageHelper.offsetPage()`
 
```java
public PageInfo<Product> getByPageHelper() {
    PageHelper.startPage(request);
    // 或
    // PageHelper.startPage(0,10);
    // PageHelper.offsetPage(0,10);
    return new PageInfo<>(productDao.getByPageHelper());
}
```

```xml
<select id="getByPageHelper" resultMap="baseResultMap">
    SELECT *
    FROM product
</select>
```

### 使用`RowBounds`传递分页参数
```java
public PageInfo<Product> getByRowBounds(Integer pageNum, Integer pageSize) {
    return new PageInfo<>(productDao.getByRowBounds(new RowBounds(pageNum, pageSize)));
}
```
```xml
<select id="getByRowBounds" resultMap="baseResultMap">
    SELECT *
    FROM product
</select>
```

### Dao 接口直接传递分页参数
```java
public PageInfo<Product> getByInterfaceArgs(Integer pageNum, Integer pageSize) {
    return new PageInfo<>(productDao.getByInterfaceArgs(pageNum, pageSize));
}
```
```xml
<select id="getByInterfaceArgs" resultMap="baseResultMap">
    SELECT *
    FROM product
</select>
```

### 通过`Modal`传递分页参数
- 需要 `Product`中的`pageSize`和`pageNum`都有效
```java
public PageInfo<Product> getByModalArgs(Product product) {
    return new PageInfo<>(productDao.getByModalArgs(product));
}
```

```xml
<select id="getByModalArgs" resultMap="baseResultMap" parameterType="cn.com.hellowood.mybatis.modal.Product">
    SELECT *
    FROM product
</select>
```