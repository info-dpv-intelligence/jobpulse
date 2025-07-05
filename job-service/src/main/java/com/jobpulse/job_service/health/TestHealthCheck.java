package com.jobpulse.job_service.health;

import org.springframework.boot.actuator.health.Health;

public class TestHealthCheck {
    public void test() {
        Health health = Health.up().build();
        System.out.println(health);
    }
}
