package io.github.helloworlde.prometheus.dao;

import io.github.helloworlde.prometheus.model.Product;
import io.micrometer.core.annotation.Counted;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProductDao {

    @Counted
    @Select("select * from product where id = #{id}")
    Product getProductById(@Param("id") Integer id);

    @Insert("insert into product values(#{id}, #{name}, now())")
    Integer addProduct(Product product);

    @Delete("delete from product where id > 0")
    Integer deleteAll();
}
