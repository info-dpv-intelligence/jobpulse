package com.jobpulse.jobcreationlisting.repository;

import com.jobpulse.jobcreationlisting.model.JobPostContent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostContentRepository extends JpaRepository<JobPostContent, UUID> {
}