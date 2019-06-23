package cn.com.hellowood.websocket.controller;

import cn.com.hellowood.websocket.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.LocalDateTime;

/**
 * @author HelloWood
 */
@Slf4j
@Controller
public class MessageHandler {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    //
    // @MessageMapping("/message")
    // @SendTo("/response/message")
    // public Message message(SimpMessageHeaderAccessor accessor, String title) {
    //     log.info("Receive new message, title is :" + title);
    //
    //     return Message.builder()
    //                   .title(title)
    //                   .content(title + " content!")
    //                   .createTime(LocalDateTime.now())
    //                   .build();
    // }


    @MessageMapping("/message")
    @SendToUser("/response/message")
    public Message message(Principal principal, SimpMessageHeaderAccessor accessor, String title) {
        log.info("Receive new message, title is :" + title);

        Message message = Message.builder()
                                 .title(title)
                                 .content(title + " content!")
                                 .createTime(LocalDateTime.now())
                                 .build();
        // simpMessagingTemplate.convertAndSendToUser();
        simpMessagingTemplate.convertAndSendToUser(accessor.getSessionId(), "/response/message", message);
        return message;
    }

    @GetMapping("/sendMessage")
    @ResponseBody
    public void sendMessage(String title, String userId) {
        log.info("Receive new message from REST interface, title is :" + title);

        Message message = Message.builder()
                                 .title(title)
                                 .content(title + " content!")
                                 .createTime(LocalDateTime.now())
                                 .build();

        // simpMessagingTemplate.convertAndSend("/response/message", message);
        simpMessagingTemplate.convertAndSendToUser(userId, "/response/message", message);

    }

}
