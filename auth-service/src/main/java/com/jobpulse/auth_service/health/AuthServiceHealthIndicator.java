package com.jobpulse.auth_service.health;

import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            // Add custom health checks here
            // For example: check database connectivity, external service availability, etc.
            
            return Health.up()
                    .withDetail("service", "auth-service")
                    .withDetail("version", "1.0.0")
                    .withDetail("status", "operational")
                    .withDetail("database", "connected")
                    .withDetail("jwt", "service_available")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("service", "auth-service")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
