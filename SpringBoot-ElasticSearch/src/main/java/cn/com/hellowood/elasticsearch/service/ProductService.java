package cn.com.hellowood.elasticsearch.service;

import cn.com.hellowood.elasticsearch.modal.Product;

import java.util.List;


public interface ProductService {

    Product save(Product product);

    List<Product> findById(Long id);

    List<Product> findByName(String name);

    List<Product> findByNameLike(String name);

    List<Product> findByNameNot(String name);

    List<Product> findByPriceBetween(Long priceFrom, Long priceTo);
}
