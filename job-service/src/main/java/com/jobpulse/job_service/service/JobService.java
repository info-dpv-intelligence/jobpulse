package com.jobpulse.job_service.service;

import com.jobpulse.job_service.model.JobPost;
import com.jobpulse.job_service.repository.JobPostRepository;

import com.jobpulse.job_service.dto.CreatedResponse;
import com.jobpulse.job_service.dto.CreateJobPostCommand;
import com.jobpulse.job_service.dto.ServiceResult;
import com.jobpulse.job_service.dto.JobListingsResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class JobService implements JobServiceContract {

    private final JobPostRepository jobPostRepository;

    @Autowired
    public JobService(JobPostRepository jobPostRepository) {
        this.jobPostRepository = jobPostRepository;
    }

    @Override
    public ServiceResult<JobListingsResponse> getJobListings(Pageable pageable) {
        try {
            Page<JobPost> jobPage = jobPostRepository.findAll(pageable);
            JobListingsResponse response = JobListingsResponse.from(jobPage);
            return ServiceResult.success(response);
        } catch (Exception e) {
            return ServiceResult.failure("Failed to retrieve job listings: " + e.getMessage(), "JOB_LISTING_ERROR");
        }
    }

    @Override
    public ServiceResult<CreatedResponse> createJob(CreateJobPostCommand command) {
        try {
            JobPost jobPost = new JobPost();
            jobPost.setTitle(command.getTitle());
            jobPost.setDescription(command.getDescription());
            jobPost.setJobPosterId(UUID.fromString(command.getJobPosterId()));
            jobPost.setCreatedAt(LocalDateTime.now());
            jobPost.setUpdatedAt(LocalDateTime.now());
            jobPost.setActive(true);

            JobPost saved = jobPostRepository.save(jobPost);
            CreatedResponse response = new CreatedResponse(saved.getId().toString());
            return ServiceResult.success(response);
            // emit job posted event
        } catch (Exception e) {
            return ServiceResult.failure("Failed to create job: " + e.getMessage(), "JOB_CREATION_ERROR");
        }
    }
}