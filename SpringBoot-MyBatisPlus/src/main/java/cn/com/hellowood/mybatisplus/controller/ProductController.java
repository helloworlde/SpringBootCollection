package cn.com.hellowood.mybatisplus.controller;

import cn.com.hellowood.mybatisplus.common.CommonResponse;
import cn.com.hellowood.mybatisplus.common.ResponseUtil;
import cn.com.hellowood.mybatisplus.modal.Product;
import cn.com.hellowood.mybatisplus.service.ProductService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public CommonResponse getAllProduct() {
        return ResponseUtil.generateResponse(productService.getAllProduct());
    }

    @GetMapping("/page")
    public CommonResponse getAllProductPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        Page page = new Page(pageNum, pageSize);
        return ResponseUtil.generateResponse(productService.selectPage(page, new EntityWrapper<>()));
    }

    @GetMapping("/{id}")
    public CommonResponse getProduct(@PathVariable("id") Long productId) {
        return ResponseUtil.generateResponse(productService.selectById(productId));
    }

    @PutMapping("/{id}")
    public CommonResponse updateProduct(@PathVariable("id") Long productId, @RequestBody Product newProduct) {
        newProduct.setId(productId);
        return ResponseUtil.generateResponse(productService.updateById(newProduct));
    }

    @DeleteMapping("/{id}")
    public CommonResponse deleteProduct(@PathVariable("id") Long productId) {
        return ResponseUtil.generateResponse(productService.deleteById(productId));
    }

    @PostMapping
    public CommonResponse addProduct(@RequestBody Product newProduct) {
        return ResponseUtil.generateResponse(productService.insertByEntity(newProduct));
    }
}
