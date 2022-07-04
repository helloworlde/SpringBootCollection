package cn.com.hellowood.configclient.service.impl;

import cn.com.hellowood.configclient.dao.ProductDao;
import cn.com.hellowood.configclient.model.Product;
import cn.com.hellowood.configclient.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HelloWood
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Override
    public Product getProduct(Long id) {
        return productDao.getOne(id);
    }

    @Override
    public List<Product> getProducts() {
        return productDao.findAll();
    }

    @Override
    public Product saveProduct(Product product) {
        return productDao.save(product);
    }
}
