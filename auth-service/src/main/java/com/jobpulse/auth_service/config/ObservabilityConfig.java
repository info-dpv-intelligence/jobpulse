package com.jobpulse.auth_service.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@EnableAspectJAutoProxy
public class ObservabilityConfig {

    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(false);
        loggingFilter.setIncludeHeaders(false);
        loggingFilter.setMaxPayloadLength(1000);
        return loggingFilter;
    }

    // Custom business metrics
    @Bean
    public Counter registrationAttempts(MeterRegistry meterRegistry) {
        return Counter.builder("auth_registration_attempts_total")
                .description("Total number of registration attempts")
                .tag("service", "auth-service")
                .register(meterRegistry);
    }

    @Bean
    public Counter registrationSuccesses(MeterRegistry meterRegistry) {
        return Counter.builder("auth_registration_success_total")
                .description("Total number of successful registrations")
                .tag("service", "auth-service")
                .register(meterRegistry);
    }

    @Bean
    public Counter registrationFailures(MeterRegistry meterRegistry) {
        return Counter.builder("auth_registration_failures_total")
                .description("Total number of failed registrations")
                .tag("service", "auth-service")
                .register(meterRegistry);
    }

    @Bean
    public Counter loginAttempts(MeterRegistry meterRegistry) {
        return Counter.builder("auth_login_attempts_total")
                .description("Total number of login attempts")
                .tag("service", "auth-service")
                .register(meterRegistry);
    }

    @Bean
    public Counter loginSuccesses(MeterRegistry meterRegistry) {
        return Counter.builder("auth_login_success_total")
                .description("Total number of successful logins")
                .tag("service", "auth-service")
                .register(meterRegistry);
    }

    @Bean
    public Counter loginFailures(MeterRegistry meterRegistry) {
        return Counter.builder("auth_login_failures_total")
                .description("Total number of failed logins")
                .tag("service", "auth-service")
                .register(meterRegistry);
    }

    @Bean
    public Timer jwtGenerationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("auth_jwt_generation_duration")
                .description("Time taken to generate JWT tokens")
                .tag("service", "auth-service")
                .register(meterRegistry);
    }

    @Bean
    public Timer jwtValidationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("auth_jwt_validation_duration")
                .description("Time taken to validate JWT tokens")
                .tag("service", "auth-service")
                .register(meterRegistry);
    }
}
