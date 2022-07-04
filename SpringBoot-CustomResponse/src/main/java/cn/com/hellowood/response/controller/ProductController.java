package cn.com.hellowood.response.controller;

import cn.com.hellowood.response.common.CustomResponse;
import cn.com.hellowood.response.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author HelloWood
 */
@RestController
public class ProductController {

    private static Map<Long, Product> productMap;

    static {
        productMap = Stream.of(
                Product.builder().id(1L).name("product1").price(1L).build(),
                Product.builder().id(2L).name("product2").price(2L).build(),
                Product.builder().id(3L).name("product3").price(3L).build()
        ).collect(Collectors.toConcurrentMap(Product::getId, p -> p));
    }

    @GetMapping("/boolean")
    @CustomResponse
    public Boolean getBoolean(String value) {
        return "true".equals(value);
    }

    @GetMapping("/product/{id}")
    @CustomResponse
    public Product getProduct(@PathVariable Long id) {
        return productMap.get(id);
    }

    @GetMapping("/product")
    public List<Product> getProducts() {
        return new ArrayList<>(productMap.values());
    }
}
