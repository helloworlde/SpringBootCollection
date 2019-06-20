package cn.com.hellowood.exception.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * Product bean
 *
 * @author HelloWood
 * @date 2018-05-29 11:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    private static final Long serialVersionUID = 1435515995276255188L;


    @NotNull(message = "id 不能为空")
    @Min(value = 1, message = "id 必须大于0")
    @Max(value = 10, message = "id 不能小于 10")
    private Long id;

    @NotNull(message = "名称不能为空")
    @Pattern(regexp = "product[\\w]+", message = "名称必须以 product 开头的")
    private String name;

    @PositiveOrZero(message = "价格必须是正数")
    private Long price;

}