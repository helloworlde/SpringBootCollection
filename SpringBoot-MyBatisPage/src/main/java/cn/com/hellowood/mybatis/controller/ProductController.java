package cn.com.hellowood.mybatis.controller;

import cn.com.hellowood.mybatis.common.CommonResponse;
import cn.com.hellowood.mybatis.common.ResponseUtil;
import cn.com.hellowood.mybatis.modal.Product;
import cn.com.hellowood.mybatis.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/pageHelper")
    public CommonResponse getByPageHelper() {
        return ResponseUtil.generateResponse(productService.getByPageHelper());
    }

    @GetMapping("/rowBounds")
    public CommonResponse getByRowBounds(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return ResponseUtil.generateResponse(productService.getByRowBounds(pageNum, pageSize));
    }

    @GetMapping("/interfaceArgs")
    public CommonResponse getByInterfaceArgs(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return ResponseUtil.generateResponse(productService.getByInterfaceArgs(pageNum, pageSize));
    }

    @GetMapping("/modalArgs")
    public CommonResponse getByModalArgs(Product product) {
        return ResponseUtil.generateResponse(productService.getByModalArgs(product));
    }
}
