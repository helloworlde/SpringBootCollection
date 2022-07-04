package io.github.helloworlde.stream.listener;

import io.github.helloworlde.stream.binding.OrderBinding;
import io.github.helloworlde.stream.vo.OrderVO;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

/**
 * @author HelloWood
 */
@Component
public class TimerAdapter {

    @InboundChannelAdapter(value = OrderBinding.NEW_ORDERS, poller = @Poller(fixedDelay = "10", maxMessagesPerPoll = "1"))
    @Bean
    public MessageSource<OrderVO> timerMessageSource() {

        OrderVO orderVO = OrderVO.builder()
                                 .userId(1L)
                                 .orderId(1L)
                                 .build();

        return () -> new GenericMessage<>(orderVO);
    }
}
