package cn.com.hellowood.mapper.dao;


import cn.com.hellowood.mapper.common.CommonMapper;
import cn.com.hellowood.mapper.modal.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * Product mapper for operate data of products table
 *
 * @author HelloWood
 * @date 2017-07-11 10:54
 */
@Mapper
public interface ProductDao extends CommonMapper<Product> {
}
