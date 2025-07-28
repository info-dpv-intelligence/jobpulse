package com.jobpulse.jobcreationlisting.repository;

import com.jobpulse.jobcreationlisting.model.CompanyDetails;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostCompanyDetailsRepository extends JpaRepository<CompanyDetails, UUID> {}