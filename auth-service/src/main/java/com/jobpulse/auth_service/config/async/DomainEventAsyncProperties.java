package com.jobpulse.auth_service.config.async;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "async.domain-event")
public record DomainEventAsyncProperties(
    int corePoolSize,
    int maxPoolSize,
    int queueCapacity,
    String threadNamePrefix
) {

}
