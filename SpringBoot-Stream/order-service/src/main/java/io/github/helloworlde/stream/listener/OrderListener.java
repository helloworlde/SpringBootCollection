package io.github.helloworlde.stream.listener;

import io.github.helloworlde.stream.binding.NotifyBinding;
import io.github.helloworlde.stream.binding.OrderBinding;
import io.github.helloworlde.stream.entity.NotifyMessage;
import io.github.helloworlde.stream.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author HelloWood
 */
@Component
@Slf4j
public class OrderListener {

    @StreamListener(OrderBinding.FINISHED_ORDERS)
    @SendTo(NotifyBinding.SEND_NOTIFY)
    public NotifyMessage finishedOrdersHandler(OrderVO orderVO) {
        log.info("用户:{} 的订单:{} 已完成", orderVO.getUserId(), orderVO.getOrderId());
        return NotifyMessage.builder()
                            .title("订单已完成")
                            .userId(orderVO.getUserId())
                            .sendTime(LocalDateTime.now())
                            .build();
    }

}
