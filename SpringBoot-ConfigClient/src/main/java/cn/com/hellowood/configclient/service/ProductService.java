package cn.com.hellowood.configclient.service;

import cn.com.hellowood.configclient.model.Product;

import java.util.List;

/**
 * @author HelloWood
 */
public interface ProductService {
    /**
     * Get product by Id
     *
     * @param id
     * @return
     */
    Product getProduct(Long id);

    /**
     * Get all product
     *
     * @return
     */
    List<Product> getProducts();

    /**
     * Save product
     *
     * @param product
     * @return
     */
    Product saveProduct(Product product);
}
