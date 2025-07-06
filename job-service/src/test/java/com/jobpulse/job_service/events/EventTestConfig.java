package com.jobpulse.job_service.events;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
@ComponentScan(basePackages = "com.jobpulse.job_service.events")
public class EventTestConfig {
}
