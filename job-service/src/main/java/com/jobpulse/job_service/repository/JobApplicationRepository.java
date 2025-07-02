package com.jobpulse.job_service.repository;

import com.jobpulse.job_service.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobPost, UUID> {
    // Add custom query methods if needed
}