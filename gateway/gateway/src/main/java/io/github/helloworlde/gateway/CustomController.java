package io.github.helloworlde.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/services")
    public Object services() {
        return discoveryClient.getServices();
    }

    @GetMapping("/instance")
    public Object instance(String instanceId) {
        return discoveryClient.getInstances(instanceId);
    }
}
