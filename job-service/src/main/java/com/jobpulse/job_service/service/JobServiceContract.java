package com.jobpulse.job_service.service;

import com.jobpulse.job_service.model.JobPost;
import com.jobpulse.job_service.dto.CreatedResponse;
import com.jobpulse.job_service.dto.CreateJobPostCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for job posting operations.
 * Defines the contract for job management functionality including
 * creating job posts and retrieving job listings with pagination.
 */
public interface JobServiceContract {
    
    /**
     * Retrieves a paginated list of job posts.
     *
     * @param pageable pagination parameters (page number, size, sorting)
     * @return paginated list of job posts
     */
    Page<JobPost> getJobListings(Pageable pageable);
    
    /**
     * Creates a new job posting.
     *
     * @param command the job creation command containing job details
     * @return response containing the created job's ID
     */
    CreatedResponse createJob(CreateJobPostCommand command);
}
