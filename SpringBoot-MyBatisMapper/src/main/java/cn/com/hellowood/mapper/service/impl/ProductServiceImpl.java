package cn.com.hellowood.mapper.service.impl;

import cn.com.hellowood.mapper.mapper.ProductMapper;
import cn.com.hellowood.mapper.model.Product;
import cn.com.hellowood.mapper.service.BaseService;
import cn.com.hellowood.mapper.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProductServiceImpl extends BaseService<Product> implements ProductService{

    @Autowired
    private ProductMapper productDao;

}
