package cn.com.hellowood.response.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Long id;

    private String name;

    private Long price;

}
