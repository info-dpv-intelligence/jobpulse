package com.jobpulse.jobcreationlisting.repository;

import java.util.Optional;
import java.util.UUID;

import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostCommand;
import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostCompanyDetailsCommand;
import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostContentV1Command;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostCompanyResponse;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostContentResponse;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostResponse;
import com.jobpulse.jobcreationlisting.dto.repository.response.OperationResult;
import com.jobpulse.jobcreationlisting.model.CompanyDetails;

public interface JobPostCreationAndListingRepository {
    OperationResult<CreateJobPostCompanyResponse> createJobPostCompany(CreateJobPostCompanyDetailsCommand command);
    OperationResult<CreateJobPostContentResponse> createJobPostContent(CreateJobPostContentV1Command command);
    OperationResult<CreateJobPostResponse> createJobPost(CreateJobPostCommand command);
    Optional<CompanyDetails> findCompanyDetailsById(UUID id);
}