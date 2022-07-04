package cn.com.hellowood.websocket.controller;

import cn.com.hellowood.websocket.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

/**
 * @author HelloWood
 */
@Slf4j
@Controller
public class MessageHandler {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/message/broadcast")
    @SendTo("/response/message")
    public Message broadcastMessage(String title) {
        log.info("Receive new broadcast message from socket, title is :" + title);

        return Message.builder()
                      .title(title)
                      .content("Socket Broadcast:" + title + " content!")
                      .createTime(LocalDateTime.now())
                      .build();
    }


    @MessageMapping("/message/specify")
    @SendToUser("/response/message")
    public Message speicifyMessage(String title) {
        log.info("Receive new specify message from socket, title is :" + title);

        return Message.builder()
                      .title(title)
                      .content("Socket Specify:" + title + " content!")
                      .createTime(LocalDateTime.now())
                      .build();
    }

    @GetMapping("/message/broadcast")
    @ResponseBody
    public void sendBroadcastMessage(String title) {
        log.info("Receive new broadcast message from REST interface, title is :" + title);

        Message message = Message.builder()
                                 .title(title)
                                 .content("REST Broadcast:" + title + " content!")
                                 .createTime(LocalDateTime.now())
                                 .build();

        simpMessagingTemplate.convertAndSend("/response/message", message);
    }


    @GetMapping("/message/specify")
    @ResponseBody
    public void sendSpecifyUserMessage(String title, String username) {
        log.info("Receive new specify message from REST interface, title is :" + title);

        Message message = Message.builder()
                                 .title(title)
                                 .content("REST Specify:" + title + " content!")
                                 .createTime(LocalDateTime.now())
                                 .build();

        simpMessagingTemplate.convertAndSendToUser(username, "/response/message", message);
    }

}
