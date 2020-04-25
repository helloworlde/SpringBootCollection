package io.github.helloworlde.consul.controller;

import io.github.helloworlde.consul.feign.ProviderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ConsumerController {

    @Autowired
    private ProviderClient providerClient;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @GetMapping("/hello/consumer")
    public Map<String, Object> hello(String message) {
        return providerClient.hello();
    }

    @GetMapping("/service")
    public Object service() {
        return discoveryClient.getServices();
    }

    @GetMapping("/client")
    public Object client(String serviceId) {
        return loadBalancerClient.choose(serviceId).getUri().toString();
    }
}
