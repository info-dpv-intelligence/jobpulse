package com.jobpulse.jobcreationlisting.repository;

import com.jobpulse.jobcreationlisting.model.DescriptionOnlySkeleton;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescriptionOnlySkeletonRepository extends JpaRepository<DescriptionOnlySkeleton, UUID> {
}