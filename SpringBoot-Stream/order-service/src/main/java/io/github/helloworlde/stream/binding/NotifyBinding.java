package io.github.helloworlde.stream.binding;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

/**
 * @author HelloWood
 */
public interface NotifyBinding {

    String SEND_NOTIFY = "sendNotify";

    /**
     * 发送消息
     *
     * @return
     */
    @Input
    MessageChannel sendNotify();
}
