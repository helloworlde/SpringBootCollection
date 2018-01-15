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
