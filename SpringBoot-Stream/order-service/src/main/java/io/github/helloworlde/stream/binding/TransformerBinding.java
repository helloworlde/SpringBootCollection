package io.github.helloworlde.stream.binding;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

/**
 * @author Admin
 */
@Component
public interface TransformerBinding {

    String INPUT = "input";

    String OUTPUT = "output";

    @Input
    MessageChannel input();

    @Output
    MessageChannel output();

}
