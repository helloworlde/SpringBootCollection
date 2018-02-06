package cn.com.hellowood.mybatis.dao;


import cn.com.hellowood.mybatis.modal.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * Product mapper for operate data of products table
 *
 * @author HelloWood
 * @date 2017-07-11 10:54
 */

@Mapper
public interface ProductDao {

    List<Product> getByPageHelper();

    List<Product> getByRowBounds(RowBounds rowBounds);

    List<Product> getByInterfaceArgs(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);

    List<Product> getByModalArgs(Product product);
}
