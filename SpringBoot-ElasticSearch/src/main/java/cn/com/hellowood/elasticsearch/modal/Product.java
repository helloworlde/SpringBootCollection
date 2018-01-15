package cn.com.hellowood.elasticsearch.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

@Document(indexName = "elasticsearch", type = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1435515995276255188L;

    @Id
    private Long id;

    private String name;

    private Integer price;

    public Product() {
    }

    public Product(Long id, String name, Integer price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
