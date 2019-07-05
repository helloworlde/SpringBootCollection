package io.github.helloworlde.stream.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author HelloWood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotifyMessage {

    private String title;

    private Long userId;

    private LocalDateTime sendTime;
}
