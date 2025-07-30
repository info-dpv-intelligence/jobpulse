package com.jobpulse.jobcreationlisting.service;

import com.jobpulse.jobcreationlisting.dto.request.jobpost.CreateJobPostRequest;
import com.jobpulse.jobcreationlisting.dto.request.jobpost.GetJobPostsRequest;
import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostCommand;
import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostCompanyDetailsCommand;
import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostContentV1Command;
import com.jobpulse.jobcreationlisting.dto.repository.command.GetJobPostsCommand;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostCompanyResponse;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostResponse;
import com.jobpulse.jobcreationlisting.dto.repository.response.OperationResult;
import com.jobpulse.jobcreationlisting.dto.response.JobListingsResponse;
import com.jobpulse.jobcreationlisting.dto.response.JobPostCreatedAggregateResponse;
import com.jobpulse.jobcreationlisting.dto.response.ServiceResult;
import com.jobpulse.jobcreationlisting.dto.response.view.JobCompanyDetailsView;
import com.jobpulse.jobcreationlisting.dto.response.view.JobListingView;
import com.jobpulse.jobcreationlisting.dto.util.cursor.CursorEncoderDecoderContract;
import com.jobpulse.jobcreationlisting.dto.util.cursor.CursorV1;
import com.jobpulse.jobcreationlisting.model.*;
import com.jobpulse.jobcreationlisting.model.properties.JobPostProperties;
import com.jobpulse.jobcreationlisting.repository.*;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobPostCreationListingService implements JobPostCreationListingContract {

    private final JobPostCreationAndListingRepository jobPostCreationListingRepositoryImp;
    private final CursorEncoderDecoderContract<CursorV1> cursorEncoderDecoder;
    private final JobPostProperties jobPostProperties;
    private final JobPostViewMapper jobPostViewMapper;

    @Autowired
    public JobPostCreationListingService(
        JobPostCreationAndListingRepository jobPostCreationListingRepositoryImp,
        CursorEncoderDecoderContract<CursorV1> cursorEncoderDecoder,
        JobPostProperties jobPostProperties,
        JobPostViewMapper jobPostViewMapper
    ) {
        this.jobPostCreationListingRepositoryImp = jobPostCreationListingRepositoryImp;
        this.cursorEncoderDecoder = cursorEncoderDecoder;
        this.jobPostProperties = jobPostProperties;
        this.jobPostViewMapper = jobPostViewMapper;
    }

    public ServiceResult<JobListingsResponse> getJobPosts(GetJobPostsRequest request) {
        try {
            GetJobPostsCommand command;

            //1. Decode cursor if present
            if (request.getCursor() != null) {
                CursorV1 cursorV1 = cursorEncoderDecoder.decode(request.getCursor()).getCursor();
                command = GetJobPostsCommand
                    .builder()
                    .cursorId(cursorV1.getId())
                    .cursorCreatedAt(cursorV1.getCreatedAt())
                    .build();
            } else {
                command = GetJobPostsCommand
                    .builder()
                    .sortField(request.getSortField() != null ? request.getSortField() : jobPostProperties.CREATED_AT)
                    .pageSize(request.getLimit() != null ? request.getLimit() : 10)
                    .build();
            }
            // encode cursor
            Page<JobPost> jobPosts = jobPostCreationListingRepositoryImp.getJobPosts(command).getData();
            Page<JobListingView> jobPostListingsViewPage = jobPostViewMapper.toJobListingViewPage(jobPosts);

            JobListingsResponse jobListingsResponse = JobListingsResponse
                .builder()
                .jobPostListings(jobPostListingsViewPage.getContent())
                .hasNext(jobPostListingsViewPage.hasNext())
                .build();

            return ServiceResult.success(jobListingsResponse);
        } catch (Exception e) {
            //
        }
        return ServiceResult.failure("Not implemented");
    }

    @Override
    @Transactional
    public ServiceResult<JobPostCreatedAggregateResponse> createJobPost(CreateJobPostRequest request) {
        // 1. Create JobPostCompanyDetails
        UUID companyDetailsId = Optional.ofNullable(request.getCompanyDetails())
            .map(cd -> Optional.ofNullable(cd.getCompanyDetailsId())
            .map(id -> {
                        boolean exists = jobPostCreationListingRepositoryImp
                        .findCompanyDetailsById(id)
                        .isPresent();
                    if (!exists) {
                        // logger.error("Company details not found for id: {}", id);
                        throw new EntityNotFoundException("Company details not found");
                    }
                    return id;
                })
                .orElseGet(() -> {
                    try {
                        CreateJobPostCompanyResponse createJobPostCompanyResponse = jobPostCreationListingRepositoryImp.createJobPostCompany(
                            CreateJobPostCompanyDetailsCommand.builder()
                                .name(cd.getName())
                                .tagline(cd.getTagline())
                                .phone(cd.getPhone())
                                .build()
                            ).getData();
                        return createJobPostCompanyResponse.getJobPostCompanyId();
                    } catch (Exception e) {
                        // logger.error("Error creating company details", e);
                        throw new RuntimeException(e);
                    }
                })
            )
            .orElse(null);

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
            // logger.error("Error creating job post content", e);
            throw new RuntimeException(e);
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
            // logger
            return ServiceResult.failure(e.getMessage());
        }
    }
}