package cn.com.hellowood.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author HelloWood
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String title;

    private String content;

    private LocalDateTime createTime;

}
