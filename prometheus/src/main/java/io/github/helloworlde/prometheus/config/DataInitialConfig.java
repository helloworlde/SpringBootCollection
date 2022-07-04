package io.github.helloworlde.prometheus.config;

import io.github.helloworlde.prometheus.dao.ProductDao;
import io.github.helloworlde.prometheus.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
public class DataInitialConfig {

    @Autowired
    private ProductDao productDao;

    @PostConstruct
    public void initData() {
        productDao.deleteAll();

        IntStream.range(1, 100)
                 .mapToObj(id -> Product.builder()
                                        .id(id)
                                        .name(UUID.randomUUID().toString())
                                        .build())
                 .forEach(p -> productDao.addProduct(p));
    }
}
