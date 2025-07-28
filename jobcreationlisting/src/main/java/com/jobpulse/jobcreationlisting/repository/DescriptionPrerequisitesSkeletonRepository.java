package com.jobpulse.jobcreationlisting.repository;

import com.jobpulse.jobcreationlisting.model.DescriptionPrerequisitesSkeleton;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescriptionPrerequisitesSkeletonRepository extends JpaRepository<DescriptionPrerequisitesSkeleton, UUID> {
}