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
import com.jobpulse.jobcreationlisting.exception.CompanyNotFoundException;
import com.jobpulse.jobcreationlisting.model.*;
import com.jobpulse.jobcreationlisting.model.properties.JobPostProperties;
import com.jobpulse.jobcreationlisting.repository.*;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<JobListingsResponse> getJobPosts(GetJobPostsRequest request) {
            GetJobPostsCommand getJobPostcommand = buildGetJobPostsCommand(request);
            Slice<JobPost> jobPosts = jobPostCreationListingRepositoryImp.getJobPosts(getJobPostcommand).getData();
            Slice<JobListingView> jobPostListingsViewPage = jobPostViewMapper.toJobListingViewPage(jobPosts);

            List<JobPost> content = jobPosts.getContent();
            String nextCursor = computeNextCursor(jobPosts, content);


            return ServiceResult.success(
                JobListingsResponse
                .builder()
                    .jobPostListings(jobPostListingsViewPage.getContent())
                    .hasNext(jobPostListingsViewPage.hasNext())
                    .cursor(nextCursor)
                .build()
            );
    }

    @Override
    @Transactional
    public ServiceResult<JobPostCreatedAggregateResponse> createJobPost(CreateJobPostRequest request) {
        UUID companyDetailsId = findOrCreateCompanyDetails(request.getCompanyDetails());
        UUID jobPostContentId = createJobPostContent(request, companyDetailsId);;
        OperationResult<CreateJobPostResponse> createJobPostResponse = createJobPost(request, jobPostContentId);

        return ServiceResult.success(
            buildCreatedJobAggregateResponse(companyDetailsId, jobPostContentId, createJobPostResponse)
        );
    }

    private String computeNextCursor(Slice<JobPost> jobPosts, List<JobPost> content) {
        if (content.isEmpty() || !jobPosts.hasNext()) {
            return null;
        }
        JobPost lastItem = content.get(content.size() - 1);
        return cursorEncoderDecoder.encode(
            CursorV1.builder()
                .id(lastItem.getId())
                .createdAt(lastItem.getCreatedAt())
                .build()
        );
    }

    private JobPostCreatedAggregateResponse buildCreatedJobAggregateResponse(UUID companyDetailsId, UUID jobPostContentId,
            OperationResult<CreateJobPostResponse> createJobPostResponse) {
        return JobPostCreatedAggregateResponse.builder()
        .jobPostId(createJobPostResponse.getData().getJobPostId())
        .jobPostContentId(jobPostContentId)
        .companyDetailsId(companyDetailsId)
        .build();
    }

    private GetJobPostsCommand buildGetJobPostsCommand(GetJobPostsRequest request) {
        GetJobPostsCommand.GetJobPostsCommandBuilder getJobPostcommandBuilder = GetJobPostsCommand.builder();

        getJobPostcommandBuilder
            .pageSize(
                request.getLimit() != null 
                ? request.getLimit() 
                : jobPostProperties.DEFAULT_PAGE_SIZE
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

        if (!request.getSortDirection().equals(Sort.Direction.DESC.toString())) {
            getJobPostcommandBuilder.sortDirection(Sort.Direction.fromString(request.getSortDirection()));
        }

        return getJobPostcommandBuilder.build();
    }

    private OperationResult<CreateJobPostResponse> createJobPost(CreateJobPostRequest request, UUID jobPostContentId) {
        return jobPostCreationListingRepositoryImp.createJobPost(
            CreateJobPostCommand
                .builder()
                    .title(request.getTitle())
                    .jobPosterId(request.getJobPosterId())
                    .jobPostContentId(jobPostContentId)
                    .status(JobPostStatus.DRAFT)
                .build()
            );
    }

    private UUID createJobPostContent(CreateJobPostRequest request, UUID companyDetailsId) {
        return jobPostCreationListingRepositoryImp.createJobPostContent(
            CreateJobPostContentV1Command.builder()
            .description(request.getJobPostDescription().getDescription())
            .companyDetailsId(companyDetailsId)
            .revisionStatus(RevisionStatus.DRAFT)
            .build()
        ).getData().getJobPostContentId();
    }

    private UUID findOrCreateCompanyDetails(CompanyDetailsRequest companyDetailsRequest) {
        if (companyDetailsRequest == null) {
            return null;
        }

        if (companyDetailsRequest.getCompanyDetailsId() != null) {
            UUID id = companyDetailsRequest.getCompanyDetailsId();
            return jobPostCreationListingRepositoryImp.findCompanyDetailsById(id)
                    .map(CompanyDetails::getCompanyDetailsId)
                    .orElseThrow(() -> new CompanyNotFoundException());
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