package com.jobpulse.jobcreationlisting.service;

import com.jobpulse.jobcreationlisting.dto.request.jobpost.CreateJobPostRequest;
import com.jobpulse.jobcreationlisting.dto.request.jobpost.GetJobPostsRequest;
import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostCommand;
import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostCompanyDetailsCommand;
import com.jobpulse.jobcreationlisting.dto.request.jobpost.CompanyDetailsRequest;
import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostContentV1Command;
import com.jobpulse.jobcreationlisting.dto.repository.command.GetJobPostsCommand;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostCompanyResponse;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostResponse;
import com.jobpulse.jobcreationlisting.dto.repository.response.OperationResult;
import com.jobpulse.jobcreationlisting.dto.response.JobListingsResponse;
import com.jobpulse.jobcreationlisting.dto.response.JobPostCreatedAggregateResponse;
import com.jobpulse.jobcreationlisting.dto.response.ServiceResult;
import com.jobpulse.jobcreationlisting.dto.response.view.JobListingView;
import com.jobpulse.jobcreationlisting.dto.response.view.JobPostViewMapper;
import com.jobpulse.jobcreationlisting.dto.util.cursor.CursorEncoderDecoderContract;
import com.jobpulse.jobcreationlisting.dto.util.cursor.CursorV1;
import com.jobpulse.jobcreationlisting.model.*;
import com.jobpulse.jobcreationlisting.model.properties.JobPostProperties;
import com.jobpulse.jobcreationlisting.repository.*;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
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
            GetJobPostsCommand.GetJobPostsCommandBuilder getJobPostcommandBuilder = GetJobPostsCommand.builder();
            getJobPostcommandBuilder
                .pageSize(
                    request.getLimit() != null 
                    ? request.getLimit() 
                    : 10
                )
                .sortField(
                    request.getSortField() != null 
                    ? request.getSortField() 
                    : jobPostProperties.CREATED_AT
                );
            if (request.getCursor() != null) {
                CursorV1 cursorV1 = cursorEncoderDecoder.decode(request.getCursor()).getCursor();
                
                getJobPostcommandBuilder
                    .cursorId(cursorV1.getId())
                    .cursorCreatedAt(cursorV1.getCreatedAt());
            }
            GetJobPostsCommand getJobPostcommand = getJobPostcommandBuilder.build();
            
            Page<JobPost> jobPosts = jobPostCreationListingRepositoryImp.getJobPosts(getJobPostcommand).getData();
            
            Page<JobListingView> jobPostListingsViewPage = jobPostViewMapper.toJobListingViewPage(jobPosts);

            String nextCursor = null;
            List<JobPost> content = jobPosts.getContent();
            if (!content.isEmpty() && jobPosts.hasNext()) {
                JobPost lastItem = content.get(content.size() - 1);
                nextCursor = cursorEncoderDecoder.encode(
                    CursorV1.builder()
                        .id(lastItem.getId())
                        .createdAt(lastItem.getCreatedAt())
                        .build()
                );
            }
            JobListingsResponse jobListingsResponse = JobListingsResponse
                .builder()
                .jobPostListings(jobPostListingsViewPage.getContent())
                .hasNext(jobPostListingsViewPage.hasNext())
                .cursor(nextCursor)
                .build();

            return ServiceResult.success(jobListingsResponse);
        } catch (Exception e) {
            return ServiceResult.failure("Failed to retrieve job posts: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ServiceResult<JobPostCreatedAggregateResponse> createJobPost(CreateJobPostRequest request) {
        try {
            // Step 1: Find or create Company Details
            UUID companyDetailsId = findOrCreateCompanyDetails(request.getCompanyDetails());

            // Step 2. Create JobPostContent
            CreateJobPostContentV1Command contentCommand = CreateJobPostContentV1Command.builder()
                .description(request.getJobPostDescription().getDescription())
                .companyDetailsId(companyDetailsId)
                .revisionStatus(RevisionStatus.DRAFT)
                .build();
            UUID jobPostContentId = jobPostCreationListingRepositoryImp.createJobPostContent(contentCommand).getData().getJobPostContentId();

            // Step 3. Create JobPost
            CreateJobPostCommand createJobPostCommand = CreateJobPostCommand.builder()
                .title(request.getTitle())
                .jobPosterId(request.getJobPosterId())
                .jobPostContentId(jobPostContentId)
                .status(JobPostStatus.DRAFT)
                .build();
            OperationResult<CreateJobPostResponse> createJobPostResponse = jobPostCreationListingRepositoryImp.createJobPost(createJobPostCommand);

            // Step 4. Build and return the successful response
            return ServiceResult.success(
                JobPostCreatedAggregateResponse.builder()
                .jobPostId(createJobPostResponse.getData().getJobPostId())
                .jobPostContentId(jobPostContentId)
                .companyDetailsId(companyDetailsId)
                .build()
            );
        } catch (Exception e) {
            // logger
            return ServiceResult.failure("Failed to create job post: " + e.getMessage());
        }
    }

    private UUID findOrCreateCompanyDetails(CompanyDetailsRequest companyDetailsRequest) {
        if (companyDetailsRequest == null) {
            return null;
        }

        if (companyDetailsRequest.getCompanyDetailsId() != null) {
            UUID id = companyDetailsRequest.getCompanyDetailsId();
            return jobPostCreationListingRepositoryImp.findCompanyDetailsById(id)
                    .map(CompanyDetails::getCompanyDetailsId)
                    .orElseThrow(() -> new EntityNotFoundException("Company details not found with id: " + id));
        }

        CreateJobPostCompanyDetailsCommand command = CreateJobPostCompanyDetailsCommand.builder()
                .name(companyDetailsRequest.getName())
                .tagline(companyDetailsRequest.getTagline())
                .phone(companyDetailsRequest.getPhone())
                .build();

        CreateJobPostCompanyResponse response = jobPostCreationListingRepositoryImp.createJobPostCompany(command).getData();
        return response.getJobPostCompanyId();
    }
}