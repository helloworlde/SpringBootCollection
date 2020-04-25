package io.github.helloworlde.stream;

import io.github.helloworlde.stream.binding.OrderBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * @author HelloWood
 */
@SpringBootApplication
@EnableBinding({OrderBinding.class})
public class WaiterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaiterServiceApplication.class, args);
    }

}
