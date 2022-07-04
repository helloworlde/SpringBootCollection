package io.github.helloworlde.grpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GrpcConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcConsumerApplication.class, args);
    }

}
