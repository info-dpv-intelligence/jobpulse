package com.jobpulse.auth_service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jobpulse.auth_service.factory.ServiceFactory;
import com.jobpulse.auth_service.service.module.password.PasswordServiceContract;
import com.jobpulse.auth_service.service.module.password.BCryptPasswordService;
import com.jobpulse.auth_service.service.module.jwt.JwtServiceContract;

import static org.mockito.Mockito.mock;

/**
 * Test configuration for auth-service integration tests.
 * Provides mock beans for external dependencies when running tests.
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public KafkaTemplate<String, Object> mockKafkaTemplate() {
        return mock(KafkaTemplate.class);
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public ServiceFactory testServiceFactory() {
        return new ServiceFactory() {
            @Override
            public PasswordServiceContract createPasswordService() {
                return new BCryptPasswordService();
            }

            @Override
            public JwtServiceContract createJwtService() {
                return mock(JwtServiceContract.class);
            }
        };
    }
}
