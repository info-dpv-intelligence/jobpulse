package com.jobpulse.auth_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.jobpulse.auth_service.repository",
    enableDefaultTransactions = true
)
public class JpaConfig {
    // Spring Data will automatically configure event publishing for repositories
    // when @EnableJpaRepositories is present and domain event methods are annotated
    // with @DomainEvents and @AfterDomainEventPublication
}
