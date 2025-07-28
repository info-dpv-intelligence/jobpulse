package com.jobpulse.jobcreationlisting.repository;

import com.jobpulse.jobcreationlisting.model.JobPostDescription;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostDescriptionRepository extends JpaRepository<JobPostDescription, UUID> {
}