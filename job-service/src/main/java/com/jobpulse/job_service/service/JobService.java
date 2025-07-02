package com.jobpulse.job_service.service;

import com.jobpulse.job_service.model.JobPost;
import com.jobpulse.job_service.repository.JobPostRepository;

import com.jobpulse.job_service.dto.CreateJobPostCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jobpulse.job_service.dto.CreateJobPostRequest;
import java.time.LocalDateTime;
import java.util.UUID;

import java.util.List;

@Service
public class JobService {

    private final JobPostRepository jobPostRepository;

    @Autowired
    public JobService(JobPostRepository jobPostRepository) {
        this.jobPostRepository = jobPostRepository;
    }

    public List<JobPost> getJobListings() {
        return jobPostRepository.findAll();
    }

    public JobPost createJob(CreateJobPostCommand command) {
        JobPost jobPost = new JobPost();
        jobPost.setTitle(command.getTitle());
        jobPost.setDescription(command.getDescription());
        jobPost.setJobPosterId(UUID.fromString(command.getJobPosterId()));
        jobPost.setCreatedAt(LocalDateTime.now());
        jobPost.setUpdatedAt(LocalDateTime.now());
        jobPost.setActive(true);

        return jobPostRepository.save(jobPost);
    }
}