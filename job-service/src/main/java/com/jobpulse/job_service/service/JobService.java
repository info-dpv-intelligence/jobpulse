package com.jobpulse.job_service.service;

import com.jobpulse.job_service.model.JobPost;
import com.jobpulse.job_service.repository.JobPostRepository;

import com.jobpulse.job_service.dto.CreatedResponse;
import com.jobpulse.job_service.dto.DeletedResponse;
import com.jobpulse.job_service.dto.DeleteJobPostCommand;
import com.jobpulse.job_service.dto.CreateJobPostCommand;
import com.jobpulse.job_service.dto.ServiceResult;
import com.jobpulse.job_service.dto.UpdateJobPostCommand;
import com.jobpulse.job_service.dto.JobListingsResponse;
import com.jobpulse.job_service.dto.JobPostUpdatedResponse;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Job service implementation.
 * Handles job posting operations including CRUD operations.
 * This service is created via factory pattern rather than Spring annotation.
 */
public class JobService implements JobServiceContract {

    private final JobPostRepository jobPostRepository;

    public JobService(JobPostRepository jobPostRepository) {
        this.jobPostRepository = jobPostRepository;
    }

    @Override
    public ServiceResult<JobListingsResponse> getJobListings(Pageable pageable) {
        try {
            Page<JobPost> jobPage = jobPostRepository.findAll(pageable);
            // filter by active (exclude soft deleted )and latest version
            JobListingsResponse response = JobListingsResponse.from(jobPage);
            return ServiceResult.success(response);
        } catch (Exception e) {
            return ServiceResult.failure("Failed to retrieve job listings: " + e.getMessage(), "JOB_LISTING_ERROR");
        }
    }

    @Override
    public ServiceResult<CreatedResponse> createJob(CreateJobPostCommand command) {
        try {
            JobPost jobPost = JobPost.create(
                command.getTitle(), 
                command.getDescription(), 
                UUID.fromString(command.getJobPosterId()), 
                true
            );

            JobPost saved = jobPostRepository.save(jobPost);
            // TODO: Introduce event publishing, emit job posted event
            CreatedResponse response = new CreatedResponse(saved.getId().toString());
            return ServiceResult.success(response);
        } catch (Exception e) {
            return ServiceResult.failure("Failed to create job: " + e.getMessage(), "JOB_CREATION_ERROR");
        }
    }

    @Override
    public ServiceResult<JobPostUpdatedResponse> updateJob(UpdateJobPostCommand command) {
        try {
            Optional<JobPost> jobPostOptional = jobPostRepository.findById(UUID.fromString(command.jobId()));
            if (jobPostOptional.isEmpty()) {
                return ServiceResult.failure("Job post not found", "JOB_NOT_FOUND");
            }
            JobPost jobPost = jobPostOptional.get();
            if (command.title() != null && !command.title().trim().isEmpty()) {
                jobPost.setTitle(command.title());
            }
            if (command.description() != null && !command.description().trim().isEmpty()) {
                jobPost.setDescription(command.description());
            }
            jobPost.setActive(true);
            jobPost.setUpdatedAt(LocalDateTime.now());

            JobPost savedJobPost = jobPostRepository.save(jobPost);
            
            // TODO: Introduce event publishing, emit job updated event
            
            JobPostUpdatedResponse response = JobPostUpdatedResponse.success(savedJobPost.getId().toString());
            return ServiceResult.success(response);
            
        } catch (IllegalArgumentException e) {
            return ServiceResult.failure("Invalid job ID format", "INVALID_JOB_ID");
        } catch (Exception e) {
            return ServiceResult.failure("Failed to update job: " + e.getMessage(), "JOB_UPDATE_ERROR");
        }
    }
    
    @Override
    public ServiceResult<DeletedResponse> deleteJob(DeleteJobPostCommand command) {
        try {
            // Find the job by ID from command
            Optional<JobPost> jobPostOptional = jobPostRepository.findById(UUID.fromString(command.jobId()));
            if (jobPostOptional.isEmpty()) {
                return ServiceResult.failure("Job post not found", "JOB_NOT_FOUND");
            }
            
            // Get the job post and perform soft delete
            JobPost jobPost = jobPostOptional.get();
            jobPost.setActive(false); // Soft delete by setting inactive
            jobPost.setUpdatedAt(LocalDateTime.now());
            
            // Save the updated job post
            jobPostRepository.save(jobPost);
            
            // TODO: Introduce event publishing, emit job deleted event
            
            DeletedResponse response = new DeletedResponse();
            return ServiceResult.success(response);
            
        } catch (IllegalArgumentException e) {
            return ServiceResult.failure("Invalid job ID format", "INVALID_JOB_ID");
        } catch (Exception e) {
            return ServiceResult.failure("Failed to delete job: " + e.getMessage(), "JOB_DELETE_ERROR");
        }
    }
}