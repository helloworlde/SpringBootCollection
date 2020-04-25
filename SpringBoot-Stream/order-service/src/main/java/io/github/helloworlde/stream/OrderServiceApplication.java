package io.github.helloworlde.stream;

import io.github.helloworlde.stream.binding.NotifyBinding;
import io.github.helloworlde.stream.binding.OrderBinding;
import io.github.helloworlde.stream.binding.TransformerBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * @author HelloWood
 */
@SpringBootApplication
@EnableBinding({OrderBinding.class, NotifyBinding.class, TransformerBinding.class})
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
