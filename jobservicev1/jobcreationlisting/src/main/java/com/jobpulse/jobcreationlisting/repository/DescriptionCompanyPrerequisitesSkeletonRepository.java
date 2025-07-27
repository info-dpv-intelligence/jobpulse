package com.jobpulse.jobcreationlisting.repository;

import com.jobpulse.jobcreationlisting.model.DescriptionCompanyPrerequisitesSkeleton;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescriptionCompanyPrerequisitesSkeletonRepository extends JpaRepository<DescriptionCompanyPrerequisitesSkeleton, UUID> {
}