package cn.com.hellowood.exception.controller;

import cn.com.hellowood.exception.common.CustomResponse;
import cn.com.hellowood.exception.model.Product;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HelloWood
 */
@RestController
public class ExceptionController {

    @GetMapping("/null")
    @CustomResponse
    public Object getProduct() {
        throw new NullPointerException();
    }

    @GetMapping("/arg")
    public Object getProducts() {
        throw new IllegalArgumentException();
    }

    @PostMapping("/product")
    @CustomResponse
    public Product product(@RequestBody @Validated Product product) {
        return product;
    }
}
