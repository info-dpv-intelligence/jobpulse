package com.jobpulse.auth_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.jobpulse.auth_service.repository",
    enableDefaultTransactions = true
)
public class JpaConfig {
}
