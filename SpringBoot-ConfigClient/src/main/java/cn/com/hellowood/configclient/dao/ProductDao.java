package cn.com.hellowood.configclient.dao;

import cn.com.hellowood.configclient.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author HelloWood
 */
@Repository
public interface ProductDao extends JpaRepository<Product, Long> {
}
