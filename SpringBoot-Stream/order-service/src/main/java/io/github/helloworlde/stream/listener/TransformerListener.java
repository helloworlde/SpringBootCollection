package io.github.helloworlde.stream.listener;

import io.github.helloworlde.stream.binding.TransformerBinding;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Component;

/**
 * @author HelloWood
 */
@Component
public class TransformerListener {

    @Transformer(inputChannel = TransformerBinding.INPUT, outputChannel = TransformerBinding.OUTPUT)
    public Object transform(String message) {
        return message.toUpperCase();
    }

}
