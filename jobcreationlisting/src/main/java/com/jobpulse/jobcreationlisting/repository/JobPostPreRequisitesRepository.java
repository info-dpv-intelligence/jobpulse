package com.jobpulse.jobcreationlisting.repository;

import com.jobpulse.jobcreationlisting.model.JobPostPreRequisites;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostPreRequisitesRepository extends JpaRepository<JobPostPreRequisites, UUID> {
}