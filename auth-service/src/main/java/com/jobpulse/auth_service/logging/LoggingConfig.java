package com.jobpulse.auth_service.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @Value("${spring.application.name:auth-service}")
    private String serviceName;

    @Bean
    public LoggerFactory loggerFactory() {
        return new LoggerFactory() {
            @Override
            public StructuredLogger getLogger(Class<?> clazz) {
                return new DefaultStructuredLogger(clazz, serviceName);
            }

            @Override
            public StructuredLogger getLogger(String name) {
                return new DefaultStructuredLogger(
                    org.slf4j.LoggerFactory.getLogger(name).getClass(), 
                    serviceName
                );
            }
        };
    }

    public interface LoggerFactory {
        StructuredLogger getLogger(Class<?> clazz);
        StructuredLogger getLogger(String name);
    }
}
