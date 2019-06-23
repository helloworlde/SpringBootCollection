package cn.com.hellowood.websocket.controller;

import cn.com.hellowood.websocket.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * @author HelloWood
 */
@Slf4j
@Controller
public class MessageHandler {

    @MessageMapping("/message")
    @SendTo("/response/message")
    public Message message(String title) {
        log.info("Receive new message, title is :" + title);

        return Message.builder()
                      .title(title)
                      .content(title + " content!")
                      .createTime(LocalDateTime.now())
                      .build();
    }

}
