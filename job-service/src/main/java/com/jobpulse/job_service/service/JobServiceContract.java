package com.jobpulse.job_service.service;

import com.jobpulse.job_service.dto.ServiceResult;
import com.jobpulse.job_service.dto.UpdateJobPostCommand;
import com.jobpulse.job_service.dto.DeleteJobPostCommand;
import com.jobpulse.job_service.dto.CreatedResponse;
import com.jobpulse.job_service.dto.DeletedResponse;
import com.jobpulse.job_service.dto.CreateJobPostCommand;
import com.jobpulse.job_service.dto.JobListingsResponse;
import com.jobpulse.job_service.dto.JobPostUpdatedResponse;

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
     * @return service result with paginated job listings or error details
     */
    ServiceResult<JobListingsResponse> getJobListings(Pageable pageable);
    
    /**
     * Creates a new job posting.
     *
     * @param command the job creation command containing job details
     * @return service result with created job response or error details
     */
    ServiceResult<CreatedResponse> createJob(CreateJobPostCommand command);

    ServiceResult<JobPostUpdatedResponse> updateJob(UpdateJobPostCommand command);

    ServiceResult<DeletedResponse> deleteJob(DeleteJobPostCommand command);
}
