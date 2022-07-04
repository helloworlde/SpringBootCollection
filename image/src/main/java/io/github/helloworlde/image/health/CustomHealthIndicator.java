package io.github.helloworlde.image.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    private boolean health = true;

    @Override
    public Health health() {
        if (health) {
            return Health.up().withDetail("Status", "Health").build();
        }
        return Health.down().withDetail("Status", "Not Health").build();
    }

    public void setHealth(boolean health) {
        this.health = health;
    }
}
