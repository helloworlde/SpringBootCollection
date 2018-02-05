package cn.com.hellowood.mybatisplus.modal;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * Product bean
 *
 * @author HelloWood
 * @date 2017-07-11 11:09
 */
@TableName(value = "product", resultMap = "baseResultMap")
public class Product implements Serializable {
    private static final long serialVersionUID = 1435515995276255188L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField
    private String name;

    @TableField
    private Long price;

    public Product() {
    }

    public Product(Long id, String name, Long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public long getId() {
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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}
