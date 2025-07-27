package com.jobpulse.jobcreationlisting.repository;

import com.jobpulse.jobcreationlisting.model.JobPostSkeleton;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostSkeletonRepository extends JpaRepository<JobPostSkeleton, UUID> {
}