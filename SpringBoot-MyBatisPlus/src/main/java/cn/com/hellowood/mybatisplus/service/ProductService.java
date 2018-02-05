package cn.com.hellowood.mybatisplus.service;

import cn.com.hellowood.mybatisplus.dao.ProductDao;
import cn.com.hellowood.mybatisplus.modal.Product;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    public Integer insertAllColumn(Product product) {
        return productDao.insertAllColumn(product);
    }

    public Integer insertByEntity(Product newProduct) {
        return productDao.insert(newProduct);
    }

    public Integer deleteById(Long id) {
        return productDao.deleteById(id);
    }

    public Integer deleteByMap(Map<String, Object> productMap) {
        return productDao.deleteByMap(productMap);
    }

    public Integer deleteByEntity(Product product) {
        return productDao.delete(new EntityWrapper<>(product));
    }

    public Integer deleteBatchIds(Collection<? extends Serializable> ids) {
        return productDao.deleteBatchIds(ids);
    }

    public Integer updateById(Product product) {
        return productDao.updateById(product);
    }

    public Integer updateAllColumnById(Product product) {
        return productDao.updateAllColumnById(product);
    }

    public Product selectById(Long id) {
        return productDao.selectById(id);
    }

    public List<Product> selectBatchIds(Collection<? extends Serializable> ids) {
        return productDao.selectBatchIds(ids);
    }

    public List<Product> selectByMap(Map<String, Object> productMap) {
        return productDao.selectByMap(productMap);
    }

    public Product selectOne(Product product) {
        return productDao.selectOne(product);
    }

    public Integer selectCount(Wrapper<Product> wrapper) {
        return productDao.selectCount(wrapper);
    }

    public List<Product> selectList(Wrapper<Product> wrapper) {
        return productDao.selectList(wrapper);
    }

    public List<Map<String, Object>> selectMaps(Wrapper<Product> wrapper) {
        return productDao.selectMaps(wrapper);
    }

    public List<Object> selectObjs(Wrapper<Product> wrapper) {
        return productDao.selectObjs(wrapper);
    }

    public Page<Product> selectPage(Page page, Wrapper<Product> wrapper) {
        List<Product> products = productDao.selectPage(page, wrapper);
        page.setRecords(products);
        return page;
    }

    public Page selectMapsPage(Page page, Wrapper<Product> wrapper) {
        List<Map<String, Object>> products = productDao.selectMapsPage(page, wrapper);
        page.setRecords(products);
        return page;
    }

    public List<Product> getAllProduct() {
        return productDao.getAllProduct();
    }
}
