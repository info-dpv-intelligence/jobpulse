package com.jobpulse.job_service.service;

import com.jobpulse.job_service.model.JobPost;
import com.jobpulse.job_service.repository.JobPostRepository;

import com.jobpulse.job_service.dto.CreatedResponse;

import com.jobpulse.job_service.dto.CreateJobPostCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jobpulse.job_service.dto.CreateJobPostRequest;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;

@Service
public class JobService implements JobServiceContract {

    private final JobPostRepository jobPostRepository;

    @Autowired
    public JobService(JobPostRepository jobPostRepository) {
        this.jobPostRepository = jobPostRepository;
    }

    @Override
    public Page<JobPost> getJobListings(Pageable pageable) {
       return jobPostRepository.findAll(pageable);
    }

    @Override
    public CreatedResponse createJob(CreateJobPostCommand command) {
        JobPost jobPost = new JobPost();
        jobPost.setTitle(command.getTitle());
        jobPost.setDescription(command.getDescription());
        jobPost.setJobPosterId(UUID.fromString(command.getJobPosterId()));
        jobPost.setCreatedAt(LocalDateTime.now());
        jobPost.setUpdatedAt(LocalDateTime.now());
        jobPost.setActive(true);

        JobPost saved = jobPostRepository.save(jobPost);
        return new CreatedResponse(saved.getId().toString());
        // emit job posted event
    }
}