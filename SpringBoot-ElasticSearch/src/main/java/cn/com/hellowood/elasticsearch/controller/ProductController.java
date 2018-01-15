package cn.com.hellowood.elasticsearch.controller;

import cn.com.hellowood.elasticsearch.modal.Product;
import cn.com.hellowood.elasticsearch.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ProductService productService;

    @PostMapping
    public Product save(@RequestBody Product product) {
        return productService.save(product);
    }

    @GetMapping("/find/id")
    public List<Product> findById(@RequestParam Long id) {
        return productService.findById(id);
    }

    @GetMapping("/find/name")
    public List<Product> findByName(@RequestParam String name) {
        return productService.findByName(name);
    }

    @GetMapping("/find/name/like")
    public List<Product> findByNameLike(@RequestParam String name) {
        return productService.findByNameLike(name);
    }

    @GetMapping("/find/name/notLike")
    public List<Product> findByNameNot(@RequestParam String name) {
        return productService.findByNameNot(name);
    }

    @GetMapping("/find/price/between")
    public List<Product> findByPriceBetween(@RequestParam Long priceFrom, @RequestParam Long priceTo) {
        return productService.findByPriceBetween(priceFrom, priceTo);
    }
}
