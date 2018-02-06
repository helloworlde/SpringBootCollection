package cn.com.hellowood.mapper.service;

import cn.com.hellowood.mapper.dao.ProductDao;
import cn.com.hellowood.mapper.modal.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProductService extends BaseService<Product> {

    @Autowired
    private ProductDao productDao;


}
