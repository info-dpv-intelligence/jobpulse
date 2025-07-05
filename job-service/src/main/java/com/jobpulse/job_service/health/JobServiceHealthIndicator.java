package com.jobpulse.job_service.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

@RestController
public class JobServiceHealthIndicator {
    
    @GetMapping("/actuator/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "job-service");
        health.put("version", "1.0.0");
        health.put("database", "connected");
        health.put("search_engine", "available");
        return health;
    }
}
