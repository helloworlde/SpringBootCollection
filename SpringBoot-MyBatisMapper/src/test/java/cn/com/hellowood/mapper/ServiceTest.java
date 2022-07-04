package cn.com.hellowood.mapper;

import cn.com.hellowood.mapper.model.Product;
import cn.com.hellowood.mapper.service.ProductService;
import cn.com.hellowood.mapper.utils.ServiceException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tk.mybatis.mapper.entity.Condition;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ServiceTest extends MapperApplicationTests {

    @Autowired
    ProductService productService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void save() {
        Product product = addProduct("save", 100);
    }

    @Test
    public void saveList() {
        List<Product> productList = new ArrayList<>();
        productList.add(addProduct("saveList1", 200));
        productList.add(addProduct("saveList2", 200));
        Integer record = productService.save(productList);
        assertTrue("record should more than 0 if success", 0 < record);
    }


    @Test
    public void deleteById() {
        Product product = addProduct("deleteById", 300);
        Integer record = productService.deleteById(product.getId());
        assertTrue("record should be 1 if success", 1 == record);
    }


    @Test
    public void deleteByIds() {
        List<Product> productList = new ArrayList<>();
        productList.add(addProduct("deleteByIds", 200));
        productList.add(addProduct("deleteByIds", 200));

        String ids = "";
        for (Product product : productList) {
            ids = "," + product.getId();
        }

        if (ids.length() > 0) {
            ids = ids.replaceFirst(",", "");
        }

        Integer record = productService.deleteByIds(ids);
        assertTrue("record should more than 0 if success", 0 < record);
    }


    @Test
    public void update() {
        Product product = addProduct("update", 300);

        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setName("afterUpdate");
        Integer record = productService.update(newProduct);

        assertTrue("record should be 1 if success", 1 == record);
    }


    @Test
    public void getById() {
        Product product = addProduct("getById", 300);
        Product newProduct = productService.getById(product.getId());
        assertNotNull("product should be not null", newProduct);
        assertEquals("product name should be same", product.getName(), newProduct.getName());
        assertEquals("product price should be same", product.getPrice(), newProduct.getPrice());
    }


    @Test
    public void getByField() throws ServiceException {
        Product product = addProduct("getByField", 300);
        List<Product> productList = productService.getByField("name", "getByField");
        assertNotNull("product list should be not null", productList);
        assertTrue("product list size should be 1", 1 == productList.size());
        assertEquals("product name should be same", product.getName(), productList.get(0).getName());
        assertEquals("product price should be same", product.getPrice(), productList.get(0).getPrice());
    }


    @Test
    public void getByIds() {
        List<Product> productList = new ArrayList<>();
        productList.add(addProduct("getByIds1", 200));
        productList.add(addProduct("getByIds2", 200));

        String ids = "";
        for (Product product : productList) {
            ids += "," + product.getId();
        }

        if (ids.length() > 0) {
            ids = ids.replaceFirst(",", "");
        }

        List<Product> products = productService.getByIds(ids);

        assertNotNull("product list should be not null", products);
        assertTrue("product list size should be 2", 2 == products.size());
        assertEquals("product name should be same", products.get(0).getName(), productList.get(0).getName());
        assertEquals("product price should be same", products.get(0).getPrice(), productList.get(0).getPrice());
    }


    @Test
    public void getByCondition() {
        List<Product> productList = new ArrayList<>();
        productList.add(addProduct("getByCondition", 200));
        productList.add(addProduct("getByCondition", 200));

        Condition condition = new Condition(Product.class, true, true);

        List<Product> products = productService.getByCondition(condition);
        assertNotNull("product list should be not null", products);
        assertTrue("product list size should more than 0", 0 < products.size());
    }


    @Test
    public void getAll() {
        List<Product> productList = new ArrayList<>();
        productList.add(addProduct("getAll", 200));
        productList.add(addProduct("getAll", 200));

        List<Product> products = productService.getAll();
        assertNotNull("product list should be not null", products);
        assertTrue("product list size should more than 0", 0 < products.size());
    }


    @Test
    public void getPage() {
        List<Product> productList = new ArrayList<>();
        productList.add(addProduct("getAll", 200));
        productList.add(addProduct("getAll", 200));

        try {
            MvcResult mvcResult = mockMvc.perform(get("/product?pageNum=1&pageSize=2"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andDo(print())
                    .andReturn();
            String result = mvcResult.getResponse().getContentAsString();
            System.out.println(result);
            assertTrue("there should have success if no error", result.contains("success"));
            assertTrue("there should have page info if success", result.contains("pageNum"));
            assertTrue("there should have page info if success", result.contains("pageSize"));
            assertTrue("there should have page info if success", result.contains("prePage"));
            assertTrue("there should have page info if success", result.contains("nextPage"));
            assertTrue("there should have page info if success", result.contains("isFirstPage"));
            assertTrue("there should have page info if success", result.contains("isLastPage"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Product addProduct(String name, Integer price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(Double.valueOf(price));
        Integer record = productService.save(product);
        assertTrue("Record should be 1 if success", 1 == record);
        return product;
    }
}
