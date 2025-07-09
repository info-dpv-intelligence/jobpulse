package com.jobpulse.job_service.factory;

import org.springframework.stereotype.Component;

import com.jobpulse.job_service.service.JobServiceContract;
import com.jobpulse.job_service.service.JobService;
import com.jobpulse.job_service.repository.JobPostRepository;

/**
 * Default implementation of JobServiceFactory.
 * Creates service instances using Spring dependency injection.
 */
@Component
public class DefaultJobServiceFactory implements JobServiceFactory {
    
    private final JobPostRepository jobPostRepository;
    
    public DefaultJobServiceFactory(JobPostRepository jobPostRepository) {
        this.jobPostRepository = jobPostRepository;
    }
    
    @Override
    public JobServiceContract createJobService() {
        return new JobService(jobPostRepository);
    }
}
