package com.jobpulse.auth_service.config.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private DomainEventAsyncProperties domainEventAsyncProperties;

    @Autowired
    public AsyncConfig(DomainEventAsyncProperties domainEventAsyncProperties) {
        this.domainEventAsyncProperties = domainEventAsyncProperties;
    }

    @Bean(name = "domainEventExecutor")
    public Executor domainEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(domainEventAsyncProperties.corePoolSize());
        executor.setMaxPoolSize(domainEventAsyncProperties.maxPoolSize());
        executor.setQueueCapacity(domainEventAsyncProperties.queueCapacity());
        executor.setThreadNamePrefix(domainEventAsyncProperties.threadNamePrefix());
        executor.initialize();

        return executor;
    }
}
