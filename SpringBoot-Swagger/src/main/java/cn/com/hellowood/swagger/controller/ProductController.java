package cn.com.hellowood.swagger.controller;

import cn.com.hellowood.swagger.common.CommonResponse;
import cn.com.hellowood.swagger.common.ResponseUtil;
import cn.com.hellowood.swagger.modal.Product;
import cn.com.hellowood.swagger.service.ProductService;
import cn.com.hellowood.swagger.utils.ServiceException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Product controller
 *
 * @author HelloWood
 * @date 2017-07-11 11:38
 */

@Api(value = "商品信息", tags = {"商品信息API"}, description = "商品信息API接口")
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Get all product
     *
     * @return
     * @throws ServiceException
     */
    @ApiOperation(value = "查询所有商品", notes = "查询所有的商品信息", httpMethod = "GET")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功", response = CommonResponse.class),
            @ApiResponse(code = 400, message = "请求参数错误", response = CommonResponse.class),
            @ApiResponse(code = 401, message = "未授权的访问", response = CommonResponse.class),
            @ApiResponse(code = 403, message = "拒绝访问", response = CommonResponse.class),
            @ApiResponse(code = 404, message = "资源不存在", response = CommonResponse.class),
            @ApiResponse(code = 500, message = "服务器内部错误", response = CommonResponse.class)
    })
    @GetMapping
    public CommonResponse getAllProduct() {
        return ResponseUtil.generateResponse(productService.getAllProduct());
    }

    /**
     * Get product by id
     *
     * @param productId
     * @return
     * @throws ServiceException
     */
    @ApiOperation(value = "通过 ID 查询商品信息", notes = "通过 ID 查询商品信息", httpMethod = "GET", response = CommonResponse.class)
    @ApiImplicitParam(name = "id", value = " Product ID", required = true, paramType = "path", dataType = "Long")
    @GetMapping("/{id}")
    public CommonResponse getProduct(@PathVariable("id") Long productId) throws ServiceException {
        return ResponseUtil.generateResponse(productService.select(productId));
    }

    /**
     * Update product by id
     *
     * @param productId
     * @param newProduct
     * @return
     * @throws ServiceException
     */
    @ApiOperation(value = "更新商品信息", notes = "通过 ID 更新商品信息", httpMethod = "PUT", response = CommonResponse.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商品 ID", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "newProduct", value = "商品信息对象", required = true, dataType = "Product", dataTypeClass = Product.class)
    })
    @PutMapping("/{id}")
    public CommonResponse updateProduct(@PathVariable("id") Long productId, @RequestBody Product newProduct) throws ServiceException {
        return ResponseUtil.generateResponse(productService.update(productId, newProduct));
    }

    /**
     * Delete product by id
     *
     * @param productId
     * @return
     * @throws ServiceException
     */
    @ApiOperation(value = "删除商品信息", notes = "通过 ID 删除商品信息", httpMethod = "DELETE", response = CommonResponse.class)
    @ApiImplicitParam(name = "id", value = "商品 ID", required = true, paramType = "path", dataType = "Long")
    @DeleteMapping("/{id}")
    public CommonResponse deleteProduct(@PathVariable("id") long productId) throws ServiceException {
        return ResponseUtil.generateResponse(productService.delete(productId));
    }

    /**
     * Save product
     *
     * @param newProduct
     * @return
     * @throws ServiceException
     */
    @ApiOperation(value = "更新商品信息", notes = "通过 ID 更新商品信息", httpMethod = "POST", response = CommonResponse.class)
    @ApiImplicitParam(name = "newProduct", value = "商品信息对象", required = true, dataType = "Product", dataTypeClass = Product.class)
    @PostMapping
    public CommonResponse addProduct(@RequestBody Product newProduct) throws ServiceException {
        return ResponseUtil.generateResponse(productService.add(newProduct));
    }
}
