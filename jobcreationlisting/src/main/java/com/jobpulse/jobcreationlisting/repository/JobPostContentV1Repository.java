package com.jobpulse.jobcreationlisting.repository;

import com.jobpulse.jobcreationlisting.model.JobPostContentV1;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JobPostContentV1Repository extends JpaRepository<JobPostContentV1, UUID> {}