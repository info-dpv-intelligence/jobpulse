package com.jobpulse.job_service.factory;

import com.jobpulse.job_service.service.JobServiceContract;

/**
 * Abstract factory for creating service instances in job-service.
 * Provides a centralized way to create and configure services
 * while maintaining dependency injection principles.
 */
public interface JobServiceFactory {
    
    /**
     * Creates a job service instance.
     *
     * @return configured job service
     */
    JobServiceContract createJobService();
}
