package com.jobpulse.job_service.repository;

import com.jobpulse.job_service.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, UUID> {
    Page<JobPost> findAll(Pageable pageable);
}