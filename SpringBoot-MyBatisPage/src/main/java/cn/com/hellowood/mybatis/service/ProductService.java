package cn.com.hellowood.mybatis.service;

import cn.com.hellowood.mybatis.dao.ProductDao;
import cn.com.hellowood.mybatis.modal.Product;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


/**
 * Product service for handler logic of product operation
 *
 * @author HelloWood
 * @date 2017 -07-11 11:58
 */
@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    HttpServletRequest request;

    public PageInfo<Product> getByPageHelper() {
        PageHelper.startPage(request);
        return new PageInfo<>(productDao.getByPageHelper());
    }

    public PageInfo<Product> getByRowBounds(Integer pageNum, Integer pageSize) {
        return new PageInfo<>(productDao.getByRowBounds(new RowBounds(pageNum, pageSize)));
    }

    public PageInfo<Product> getByInterfaceArgs(Integer pageNum, Integer pageSize) {
        return new PageInfo<>(productDao.getByInterfaceArgs(pageNum, pageSize));
    }

    public PageInfo<Product> getByModalArgs(Product product) {
        return new PageInfo<>(productDao.getByModalArgs(product));
    }
}
