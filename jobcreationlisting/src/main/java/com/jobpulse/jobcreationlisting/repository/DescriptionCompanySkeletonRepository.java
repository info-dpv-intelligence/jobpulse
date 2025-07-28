package com.jobpulse.jobcreationlisting.repository;

import com.jobpulse.jobcreationlisting.model.DescriptionCompanySkeleton;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescriptionCompanySkeletonRepository extends JpaRepository<DescriptionCompanySkeleton, UUID> {
}