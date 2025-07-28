package com.jobpulse.jobcreationlisting.service;

import com.jobpulse.jobcreationlisting.dto.request.CreateJobPostRequest;
import com.jobpulse.jobcreationlisting.dto.request.CompanyDetailsRequest;
import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostCommand;
import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostCompanyDetailsCommand;
import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostContentV1Command;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostCompanyResponse;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostResponse;
import com.jobpulse.jobcreationlisting.dto.repository.response.OperationResult;
import com.jobpulse.jobcreationlisting.dto.response.JobPostCreatedAggregateResponse;
import com.jobpulse.jobcreationlisting.dto.response.ServiceResult;
import com.jobpulse.jobcreationlisting.model.*;
import com.jobpulse.jobcreationlisting.repository.*;

import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobPostCreationListingService implements JobPostCreationListingContract {

    private final JobPostCreationAndListingRepository jobPostCreationListingRepositoryImp;


    @Autowired
    public JobPostCreationListingService(
        JobPostCreationAndListingRepository jobPostCreationListingRepositoryImp
    ) {
        this.jobPostCreationListingRepositoryImp = jobPostCreationListingRepositoryImp;
    }

    @Override
    @Transactional
    public ServiceResult<JobPostCreatedAggregateResponse> createJobPost(CreateJobPostRequest request) {
        // 1. Create JobPostCompanyDetails
        UUID companyDetailsId = null;
        CompanyDetailsRequest cdReq = request.getCompanyDetails();

        companyDetailsId = Optional.ofNullable(cdReq.getCompanyDetailsId())
            .map(id -> {
                        boolean exists = jobPostCreationListingRepositoryImp
                        .findCompanyDetailsById(id)
                        .isPresent();
                    if (!exists) {
                        throw new EntityNotFoundException("Company details not found");
                    }

                return id;
            })
            .orElseGet(() -> {
                try {
                    CreateJobPostCompanyResponse createJobPostCompanyResponse = jobPostCreationListingRepositoryImp.createJobPostCompany(
                        CreateJobPostCompanyDetailsCommand.builder()
                            .name(cdReq.getName())
                            .tagline(cdReq.getTagline())
                            .phone(cdReq.getPhone())
                            .build()
                        ).getData();
                    return createJobPostCompanyResponse.getJobPostCompanyId();
                } catch (Exception e) {
                    //logger
                    throw new RuntimeException(e);
                }
            });

        // 2. Create JobPostContent
        UUID jobPostContentId = null;
        CreateJobPostContentV1Command contentCommand = CreateJobPostContentV1Command.builder()
                .description(request.getJobPostDescription().getDescription())
                .companyDetailsId(companyDetailsId)
                .revisionStatus(RevisionStatus.DRAFT)
                .build();
        try {
            jobPostContentId = jobPostCreationListingRepositoryImp.createJobPostContent(contentCommand).getData().getJobPostContentId();
        } catch (Exception e) {
            //
        }

        // 3. Create JobPostCommand
        CreateJobPostCommand createJobPostCommand = CreateJobPostCommand.builder()
                .title(request.getTitle())
                .jobPosterId(request.getJobPosterId())
                .jobPostContentId(jobPostContentId)
                .status(JobPostStatus.DRAFT)
                .build();

        try {
            OperationResult<CreateJobPostResponse> createJobPostResponse = jobPostCreationListingRepositoryImp.createJobPost(createJobPostCommand);

            return ServiceResult.success(
                JobPostCreatedAggregateResponse.builder()
                .jobPostId(createJobPostResponse.getData().getJobPostId())
                .jobPostContentId(jobPostContentId)
                .companyDetailsId(companyDetailsId)
                .build()
            );
        } catch (Exception e) {
            // introduce logger
            return ServiceResult.failure(e.getMessage());
        }
    }
}