package io.github.helloworlde.stream.listener;

import io.github.helloworlde.stream.binding.NotifyBinding;
import io.github.helloworlde.stream.entity.NotifyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * @author HelloWood
 */
@Component
@Slf4j
public class NotifyListener {

    @StreamListener(NotifyBinding.SEND_NOTIFY)
    public void sendNotifyHandler(NotifyMessage notifyMessage) {
        log.info("发送标题为 :{} 的消息给用户:{}", notifyMessage.getTitle(), notifyMessage.getUserId());
    }

}
