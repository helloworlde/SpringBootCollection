package cn.com.hellowood.mybatisplus;

import cn.com.hellowood.mybatisplus.modal.Product;
import cn.com.hellowood.mybatisplus.service.ProductService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Transactional
public class ProductTest extends SpringBootMyBatisPlusApplicationTests {

    @Autowired
    private ProductService productService;

    @Test
    public void insertByEntity() {
        addProduct("insertByEntity", 100);
    }

    @Test
    public void insertAllColumn() {
        Product product = new Product();
        product.setName("insertAllColumn");
        product.setPrice(200L);
        Integer record = productService.insertAllColumn(product);
        assertTrue("If insert success, should return 1", 1 == record);
    }

    @Test
    public void updateById() {
        // insert product
        Product product = addProduct("beforeUpdateById", 100);

        // update product
        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setName("updatedProduct");
        newProduct.setPrice(200L);
        Integer updateRecord = productService.updateById(newProduct);
        assertTrue("If update success, should return 1", 1 == updateRecord);
    }

    @Test
    public void updateAllColumnById() {
        Product product = addProduct("beforeUpdateAllColumnById", 100);

        // update product
        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setName("updateAllColumnById");
        newProduct.setPrice(200L);
        Integer updateRecord = productService.updateAllColumnById(newProduct);
        assertTrue("If update success, should return 1", 1 == updateRecord);
    }

    @Test
    public void selectById() {
        Product product = addProduct("selectById", 100);

        Product selectProduct = productService.selectById(product.getId());
        assertEquals("The product name should be same", product.getName(), selectProduct.getName());
        assertEquals("The product price should be same", product.getPrice(), selectProduct.getPrice());
    }

    @Test
    public void selectBatchIds() {
        Product product1 = addProduct("batch1", 100);
        Product product2 = addProduct("batch2", 100);

        List<Long> ids = new ArrayList<>();
        ids.add(product1.getId());
        ids.add(product2.getId());

        List<Product> products = productService.selectBatchIds(ids);

        assertEquals("Should have two entity", 2, products.size());
    }

    @Test
    public void selectByMap() {
        Product product1 = addProduct("map", 100);
        Product product2 = addProduct("map", 200);

        Map<String, Object> productMap = new HashMap<>();
        productMap.put("name", "map");

        List<Product> products = productService.selectByMap(productMap);
        assertEquals("Should have two entity", 2, products.size());
        assertEquals("Name should be same", product1.getName(), products.get(0).getName());
        assertEquals("Name should be same", product2.getName(), products.get(1).getName());
    }

    @Test
    public void selectOne() {
        Product product = addProduct("selectOne", 100);

        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct = productService.selectOne(newProduct);

        assertEquals("Name should be same", product.getName(), newProduct.getName());
        assertEquals("Price should be same", product.getPrice(), newProduct.getPrice());
    }

    @Test
    public void selectCount() {
        Product product1 = addProduct("selectCount", 999);
        Product product2 = addProduct("selectCount", 999);

        EntityWrapper<Product> entityWrapper = new EntityWrapper<>();
        entityWrapper.where("name={0}", "selectCount");

        Integer record = productService.selectCount(entityWrapper);
        assertTrue("Should have two entity", 2 == record);
    }

    @Test
    public void selectList() {
        Product product1 = addProduct("selectList", 777);
        Product product2 = addProduct("selectList", 777);

        EntityWrapper<Product> entityWrapper = new EntityWrapper<>();
        entityWrapper.where("name={0}", "selectList")
                .and("price={0}", 777);

        List<Product> products = productService.selectList(entityWrapper);

        assertEquals("Should have two entity", 2, products.size());
        assertEquals("Name should be same", product1.getName(), products.get(0).getName());
        assertEquals("Price should be same", product1.getPrice(), products.get(0).getPrice());
        assertEquals("Name should be same", product2.getName(), products.get(1).getName());
        assertEquals("Price should be same", product2.getPrice(), products.get(1).getPrice());
    }

    @Test
    public void selectMaps() {
        Product product1 = addProduct("selectMaps", 888);
        Product product2 = addProduct("selectMaps", 888);

        EntityWrapper<Product> entityWrapper = new EntityWrapper<>();
        entityWrapper.where("name={0}", "selectMaps")
                .and("price={0}", 888);

        List<Map<String, Object>> products = productService.selectMaps(entityWrapper);

        assertEquals("Should have two entity", 2, products.size());
        assertEquals("Name should be same", product1.getName(), ((Product) products.get(0)).getName());
        assertEquals("Price should be same", product1.getPrice(), ((Product) products.get(0)).getPrice());
        assertEquals("Name should be same", product2.getName(), ((Product) products.get(1)).getName());
        assertEquals("Price should be same", product2.getPrice(), ((Product) products.get(1)).getPrice());
    }

    @Test
    public void selectObjs() {
        Product product1 = addProduct("selectObjs", 999);
        Product product2 = addProduct("selectObjs", 999);

        EntityWrapper<Product> entityWrapper = new EntityWrapper<>();
        entityWrapper.where("name={0}", "selectObjs")
                .and("price={0}", 999)
                .setSqlSelect("id, name, price");

        List<Object> products = productService.selectObjs(entityWrapper);

        assertEquals("Should have two entity", 2, products.size());
        assertEquals("Name should be same", product1.getName(), ((Product) products.get(0)).getName());
        assertEquals("Price should be same", product1.getPrice(), ((Product) products.get(0)).getPrice());
        assertEquals("Name should be same", product2.getName(), ((Product) products.get(1)).getName());
        assertEquals("Price should be same", product2.getPrice(), ((Product) products.get(1)).getPrice());
    }

    @Test
    public void selectPage() {
        Product product1 = addProduct("selectPage", 1000);
        Product product2 = addProduct("selectPage", 1000);

        EntityWrapper<Product> entityWrapper = new EntityWrapper<>();
        entityWrapper.where("name={0}", "selectPage")
                .and("price={0}", 1000);

        Page<Product> page = new Page<>(0, 2);
        page = productService.selectPage(page, entityWrapper);

        assertEquals("Should have two entity", 2, page.getSize());
    }

    @Test
    public void selectMapsPage() {
        Product product1 = addProduct("selectMapsPage", 1100);
        Product product2 = addProduct("selectMapsPage", 1100);

        EntityWrapper<Product> entityWrapper = new EntityWrapper<>();
        entityWrapper.where("name={0}", "selectMapsPage")
                .and("price={0}", 1100);

        Page<Product> page = new Page<>(0, 2);
        page = productService.selectMapsPage(page, entityWrapper);

        assertEquals("Should have two entity", 2, page.getSize());
    }

    @Test
    public void deleteByMap() {
        Product product = addProduct("deleteByMap", 1200);

        Map<String, Object> productMap = new HashMap<>();
        productMap.put("id", product.getId());
        productMap.put("name", product.getName());

        Integer record = productService.deleteByMap(productMap);

        assertTrue("Should deleted 1 row", 1 == record);
    }

    @Test
    public void deleteByEntity() {
        Product product = addProduct("deleteByEntity", 1300);

        Integer record = productService.deleteByEntity(product);

        assertTrue("Should deleted 1 row", 1 == record);
    }

    @Test
    public void deleteBatchIds() {
        Product product1 = addProduct("deleteBatchIds1", 1400);
        Product product2 = addProduct("deleteBatchIds2", 1400);

        List<Long> ids = new ArrayList<>();
        ids.add(product1.getId());
        ids.add(product2.getId());

        Integer record = productService.deleteBatchIds(ids);

        assertTrue("Should deleted 2 row", 2 == record);
    }


    private Product addProduct(String name, long price) {
        // insert product
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        Integer insertRecord = productService.insertByEntity(product);
        assertTrue("If insert success, should return 1", 1 == insertRecord);
        return product;
    }

}
