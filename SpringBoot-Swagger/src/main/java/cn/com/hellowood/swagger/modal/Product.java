package cn.com.hellowood.swagger.modal;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Product bean
 *
 * @author HelloWood
 * @date 2017-07-11 11:09
 */
@Api(value = "Product", description = "商品类")
public class Product implements Serializable {
    private static final long serialVersionUID = 1435515995276255188L;

    @ApiModelProperty(notes = "主键")
    private long id;

    @ApiModelProperty(notes = "名称")
    private String name;

    @ApiModelProperty(notes = "价格")
    private long price;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
