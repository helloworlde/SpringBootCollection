package io.github.helloworlde.stream.controller;

import io.github.helloworlde.stream.binding.OrderBinding;
import io.github.helloworlde.stream.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author HelloWood
 */
@RestController
public class OrderController {

    @Autowired
    private OrderBinding orderBinding;

    @Autowired
    @Qualifier(OrderBinding.FINISHED_ORDERS)
    private MessageChannel messageChannel;

    @GetMapping("/newOrder")
    public Object sendMessage(Long orderId, Long userId) {
        OrderVO orderVO = OrderVO.builder()
                                 .orderId(orderId)
                                 .userId(userId)
                                 .build();

        boolean result = orderBinding.newOrders().send(MessageBuilder.withPayload(orderVO).build());

        return new HashMap<String, String>() {{
            put("message", result ? "下单成功" : "下单失败");
        }};
    }
}
