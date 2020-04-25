package io.github.helloworlde.stream.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HelloWood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderVO {
    private Long userId;

    private Long orderId;
}
