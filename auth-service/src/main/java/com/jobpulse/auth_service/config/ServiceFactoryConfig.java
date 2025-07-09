package com.jobpulse.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.jobpulse.auth_service.factory.ServiceFactory;
import com.jobpulse.auth_service.factory.DefaultServiceFactory;
import com.jobpulse.auth_service.service.module.jwt.factory.JwtServiceFactory;

/**
 * Configuration class for service factories.
 * Ensures proper bean registration and dependency injection.
 */
@Configuration
public class ServiceFactoryConfig {

    @Bean
    @Primary
    public ServiceFactory serviceFactory(JwtServiceFactory jwtServiceFactory) {
        return new DefaultServiceFactory(jwtServiceFactory);
    }
}
