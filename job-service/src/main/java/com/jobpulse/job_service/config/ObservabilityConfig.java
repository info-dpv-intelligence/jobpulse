package com.jobpulse.job_service.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
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

    // Custom business metrics for job service
    @Bean
    public Counter jobCreations(MeterRegistry meterRegistry) {
        return Counter.builder("job_creations_total")
                .description("Total number of job postings created")
                .tag("service", "job-service")
                .register(meterRegistry);
    }

    @Bean
    public Counter jobSearches(MeterRegistry meterRegistry) {
        return Counter.builder("job_searches_total")
                .description("Total number of job searches performed")
                .tag("service", "job-service")
                .register(meterRegistry);
    }

    @Bean
    public Counter jobApplications(MeterRegistry meterRegistry) {
        return Counter.builder("job_applications_total")
                .description("Total number of job applications submitted")
                .tag("service", "job-service")
                .register(meterRegistry);
    }

    @Bean
    public Timer jobSearchTimer(MeterRegistry meterRegistry) {
        return Timer.builder("job_search_duration")
                .description("Time taken to perform job searches")
                .tag("service", "job-service")
                .register(meterRegistry);
    }

    @Bean
    public Timer jobCreationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("job_creation_duration")
                .description("Time taken to create job postings")
                .tag("service", "job-service")
                .register(meterRegistry);
    }
}
