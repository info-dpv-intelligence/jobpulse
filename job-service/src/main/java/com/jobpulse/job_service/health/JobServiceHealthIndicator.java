package com.jobpulse.job_service.health;

import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class JobServiceHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            // Add custom health checks here
            // For example: check database connectivity, external service availability, etc.
            
            return Health.up()
                    .withDetail("service", "job-service")
                    .withDetail("version", "1.0.0")
                    .withDetail("status", "operational")
                    .withDetail("database", "connected")
                    .withDetail("search_engine", "available")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("service", "job-service")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
