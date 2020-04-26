package io.github.helloworlde.grpc.controller;

// import io.github.helloworlde.grpc.feign.ProviderClient;

import io.github.helloworlde.grpc.proto.UserInfoRequest;
import io.github.helloworlde.grpc.proto.UserInfoResponse;
import io.github.helloworlde.grpc.proto.UserInfoServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ConsumerController {

    // @Autowired
    // private ProviderClient providerClient;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    // @GetMapping("/hello/consumer")
    // public Map<String, Object> hello(String message) {
    //     return providerClient.hello();
    // }

    @GetMapping("/service")
    public Object service() {
        return discoveryClient.getServices();
    }

    @GetMapping("/client")
    public Object client(String serviceId) {
        return loadBalancerClient.choose(serviceId).getUri().toString();
    }

    @GrpcClient("grpc-provider")
    private UserInfoServiceGrpc.UserInfoServiceBlockingStub userInfoStub;

    @GetMapping("/getUserInfo")
    public Object getUserInfo(String name) {
        UserInfoRequest request = UserInfoRequest.newBuilder()
                                                 .setName(name)
                                                 .build();
        UserInfoResponse response = userInfoStub.getUserInfo(request);
        return response;
    }
}
