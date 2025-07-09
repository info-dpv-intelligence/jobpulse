package com.jobpulse.job_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.jobpulse.job_service.factory.JobServiceFactory;
import com.jobpulse.job_service.factory.DefaultJobServiceFactory;
import com.jobpulse.job_service.repository.JobPostRepository;

/**
 * Configuration class for service factories.
 * Ensures proper bean registration and dependency injection.
 */
@Configuration
public class ServiceFactoryConfig {

    @Bean
    @Primary
    public JobServiceFactory jobServiceFactory(JobPostRepository jobPostRepository) {
        return new DefaultJobServiceFactory(jobPostRepository);
    }
}
