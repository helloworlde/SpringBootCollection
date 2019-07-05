package io.github.helloworlde.stream.listener;

import io.github.helloworlde.stream.binding.OrderBinding;
import io.github.helloworlde.stream.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

/**
 * @author HelloWood
 */
@Component
@Slf4j
public class WaiterListener {

    @StreamListener(OrderBinding.NEW_ORDERS)
    @SendTo(OrderBinding.FINISHED_ORDERS)
    public OrderVO newOrders(OrderVO orderVO) {
        log.info("收到用户:{} 的新订单:{}", orderVO.getUserId(), orderVO.getOrderId());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            log.error("制作失败", e);
        }

        return orderVO;
    }
}
