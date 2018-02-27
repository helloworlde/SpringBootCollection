package cn.com.hellowood.mapper.controller;

import cn.com.hellowood.mapper.common.CommonResponse;
import cn.com.hellowood.mapper.common.ResponseUtil;
import cn.com.hellowood.mapper.model.Product;
import cn.com.hellowood.mapper.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public CommonResponse getAllProduct() {
        return ResponseUtil.generateResponse(productService.getPage());
    }


    @GetMapping("/{id}")
    public CommonResponse getProduct(@PathVariable("id") Integer productId) {
        return ResponseUtil.generateResponse(productService.getById(productId));
    }

    @PutMapping("/{id}")
    public CommonResponse updateProduct(@PathVariable("id") Integer productId, @RequestBody Product newProduct) {
        newProduct.setId(productId);
        return ResponseUtil.generateResponse(productService.update(newProduct));
    }

    @DeleteMapping("/{id}")
    public CommonResponse deleteProduct(@PathVariable("id") Integer productId) {
        return ResponseUtil.generateResponse(productService.deleteById(productId));
    }

    @PostMapping
    public CommonResponse addProduct(@RequestBody Product newProduct) {
        return ResponseUtil.generateResponse(productService.save(newProduct));
    }
}
