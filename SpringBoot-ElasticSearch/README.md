# Spring Boot 使用 ElasticSearch

> [ElasticSearch](https://www.elastic.co/cn/products/elasticsearch) 是一个开源的分布式搜索引擎，用于存储数据

> 使用 SpringBoot 和 ElasticSearch 集成，实现最简单的增删改查功能

## 添加依赖
```groovy
dependencies {
	compile('org.springframework.boot:spring-boot-starter-data-elasticsearch')
	compile('org.springframework.boot:spring-boot-starter-web')
	compile('net.java.dev.jna:jna')
	testCompile('org.springframework.boot:spring-boot-starter-test')
}
```
不添加 `'net.java.dev.jna:jna'` 依赖会提示 `java.lang.ClassNotFoundException: com.sun.jna.Native` 错误

## 添加配置
```properties
spring.data.elasticsearch.repositories.enabled=true
#spring.data.elasticsearch.cluster-nodes=localhost:9300
```
`spring.data.elasticsearch.cluster-nodes=localhost:9300` 只有当使用外部集群时配置，只使用本机时会提示错误

## Modal
```java
package cn.com.hellowood.elasticsearch.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

@Document(indexName = "elasticsearch", type = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1435515995276255188L;

    @Id
    private Long id;

    private String name;

    private Integer price;

    // ...
}

```

## 实现 Repository 接口
```java
package cn.com.hellowood.elasticsearch.repository;

import cn.com.hellowood.elasticsearch.modal.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

public interface ProductRepository extends ElasticsearchRepository<Product, Long> {

    List<Product> findById(Long id);

    Page<Product> findByName(String name, Pageable pageable);

    Page<Product> findByNameLike(String name, Pageable pageable);

    Page<Product> findByNameNot(String name, Pageable pageable);

    Page<Product> findByPriceBetween(Long priceFrom, Long priceTo, Pageable pageable);
}

```

- 方法命名使用关键词来命名，用法和 JPA 类似

## 添加逻辑

- ProductController.java
```java
package cn.com.hellowood.elasticsearch.controller;

import cn.com.hellowood.elasticsearch.modal.Product;
import cn.com.hellowood.elasticsearch.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ProductService productService;

    @PostMapping
    public Product save(@RequestBody Product product) {
        return productService.save(product);
    }

    @GetMapping("/find/id")
    public List<Product> findById(@RequestParam Long id) {
        return productService.findById(id);
    }

    @GetMapping("/find/name")
    public List<Product> findByName(@RequestParam String name) {
        return productService.findByName(name);
    }

    @GetMapping("/find/name/like")
    public List<Product> findByNameLike(@RequestParam String name) {
        return productService.findByNameLike(name);
    }

    @GetMapping("/find/name/notLike")
    public List<Product> findByNameNot(@RequestParam String name) {
        return productService.findByNameNot(name);
    }

    @GetMapping("/find/price/between")
    public List<Product> findByPriceBetween(@RequestParam Long priceFrom, @RequestParam Long priceTo) {
        return productService.findByPriceBetween(priceFrom, priceTo);
    }
}

```
- ProductServiceImpl.java
```java
package cn.com.hellowood.elasticsearch.service.impl;

import cn.com.hellowood.elasticsearch.modal.Product;
import cn.com.hellowood.elasticsearch.repository.ProductRepository;
import cn.com.hellowood.elasticsearch.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductServiceImpl implements ProductService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final Integer pageNum = 0;

    private static final Integer pageSize = 10;

    Pageable pageable = new PageRequest(pageNum, pageSize);

    @Autowired
    ProductRepository productRepository;

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> findByName(String name) {
        return productRepository.findByName(name, pageable).getContent();
    }

    @Override
    public List<Product> findByNameLike(String name) {
        return productRepository.findByNameLike(name, pageable).getContent();
    }

    @Override
    public List<Product> findByNameNot(String name) {
        return productRepository.findByNameNot(name, pageable).getContent();
    }

    @Override
    public List<Product> findByPriceBetween(Long priceFrom, Long priceTo) {
        return productRepository.findByPriceBetween(priceFrom, priceTo, pageable).getContent();
    }
}

```

## 插入数据
分别插入以下数据：

```json
{
    "id":"1",
    "name":"手机",
    "price":"500"
}
```

```json
{
    "id":"2",
    "name":"小米手机",
    "price":"600"
}
```

```json
{
    "id":"3",
    "name":"小米笔记本",
    "price":"700"
}
```

```json
{
    "id":"4",
    "name":"苹果手机",
    "price":"800"
}
```

```json
{
    "id":"5",
    "name":"苹果笔记本",
    "price":"900"
}
```

## 调用接口

使用 Postman 分别调用各个接口：

- 保存 `/product`
```
POST /product
```

```
{
    "id":"1",
    "name":"手机",
    "price":"500"
}
```


- 通过 id 查找 `/product/find/id`

```
GET /product/find/id?id=4

```

```
[
    {
        "id": 4,
        "name": "苹果手机",
        "price": 800
    }
]
```

- 通过名称查找 `/product/find/name`

```
GET /product/find/name?name=手机
```

```
[
    {
        "id": 1,
        "name": "手机",
        "price": 500
    },
    {
        "id": 2,
        "name": "小米手机",
        "price": 600
    },
    {
        "id": 4,
        "name": "苹果手机",
        "price": 800
    }
]
```

- 通过名称模糊查询 `/product/find/name/like`

```
GET /product/find/name/like?name=苹果
```

``` 
[
    {
        "id": 4,
        "name": "苹果手机",
        "price": 800
    },
    {
        "id": 5,
        "name": "苹果笔记本",
        "price": 900
    }
]
```

- 通过不包含该名称 `/product/find/name/notLike`

``` 
GET /product/find/name/notLike?name=手机
```

``` 
[
    {
        "id": 5,
        "name": "苹果笔记本",
        "price": 900
    },
    {
        "id": 3,
        "name": "小米笔记本",
        "price": 700
    }
]
```

- 通过范围查找 `/product/find/price/between`

``` 
GET /product/find/price/between?priceFrom=600&priceTo=700
```

``` 
[
    {
        "id": 2,
        "name": "小米手机",
        "price": 600
    },
    {
        "id": 3,
        "name": "小米笔记本",
        "price": 700
    }
]
```
