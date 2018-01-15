package cn.com.hellowood.elasticsearch.repository;

import cn.com.hellowood.elasticsearch.modal.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductRepository extends ElasticsearchRepository<Product, Long> {

    List<Product> findById(Long id);

    Page<Product> findByName(String name, Pageable pageable);

    Page<Product> findByNameLike(String name, Pageable pageable);

    Page<Product> findByNameNot(String name, Pageable pageable);

    Page<Product> findByPriceBetween(Long priceFrom, Long priceTo, Pageable pageable);
}
