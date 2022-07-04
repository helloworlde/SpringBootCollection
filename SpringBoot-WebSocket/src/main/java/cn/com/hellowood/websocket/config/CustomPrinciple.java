package cn.com.hellowood.websocket.config;

import lombok.AllArgsConstructor;

import java.security.Principal;

/**
 * @author HelloWood
 */
@AllArgsConstructor
public class CustomPrinciple implements Principal {

    private String name;

    @Override
    public String getName() {
        return this.name;
    }

}
