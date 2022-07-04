package cn.com.hellowood.mybatisplus.dao;


import cn.com.hellowood.mybatisplus.modal.Product;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Product mapper for operate data of products table
 *
 * @author HelloWood
 * @date 2017-07-11 10:54
 */

@Mapper
public interface ProductDao extends BaseMapper<Product> {
    List<Product> getAllProduct();
}
