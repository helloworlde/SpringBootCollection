package cn.com.hellowood.configclient.controller;

import cn.com.hellowood.configclient.model.Product;
import cn.com.hellowood.configclient.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HelloWood
 */
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String root() {
        return "Hello";
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @GetMapping("/product")
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @PostMapping("/product")
    public Product saveProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }
}
