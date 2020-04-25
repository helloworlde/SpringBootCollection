package io.github.helloworlde.stream.binding;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author HelloWood
 */
public interface OrderBinding {

    String NEW_ORDERS = "newOrders";
    String FINISHED_ORDERS = "finishedOrders";

    /**
     * 下单消息
     *
     * @return
     */
    @Input
    SubscribableChannel newOrders();

    /**
     * 订单完成消息
     *
     * @return
     */
    @Output
    MessageChannel finishedOrders();
}
