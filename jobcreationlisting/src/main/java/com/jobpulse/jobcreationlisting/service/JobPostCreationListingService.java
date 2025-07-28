package com.jobpulse.jobcreationlisting.service;

import com.jobpulse.jobcreationlisting.dto.request.CreateJobPostRequest;
import com.jobpulse.jobcreationlisting.dto.request.CompanyDetailsRequest;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostResponse;
import com.jobpulse.jobcreationlisting.dto.response.ServiceResult;
import com.jobpulse.jobcreationlisting.model.*;
import com.jobpulse.jobcreationlisting.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class JobPostCreationListingService implements JobPostCreationListingContract {

    private final CompanyDetailsRepository companyDetailsRepository;
    private final JobPostContentV1Repository jobPostContentV1Repository;
    private final JobPostRepository jobPostRepository;

    @Autowired
    public JobPostCreationListingService(
            CompanyDetailsRepository companyDetailsRepository,
            JobPostContentV1Repository jobPostContentV1Repository,
            JobPostRepository jobPostRepository
    ) {
        this.companyDetailsRepository = companyDetailsRepository;
        this.jobPostContentV1Repository = jobPostContentV1Repository;
        this.jobPostRepository = jobPostRepository;
    }

    @Override
    public ServiceResult<CreateJobPostResponse> createJobPost(CreateJobPostRequest request) {
        ZonedDateTime now = ZonedDateTime.now();

        // 1. Handle optional company details
        CompanyDetails companyDetails = null;
        if (request.getCompanyDetails() != null) {
            CompanyDetailsRequest cdReq = request.getCompanyDetails();
            companyDetails = new CompanyDetails();
            companyDetails.setName(cdReq.getName());
            companyDetails.setTagline(cdReq.getTagline());
            companyDetails.setPhone(cdReq.getPhone());
            companyDetails.setCreatedAt(now);
            companyDetails.setUpdatedAt(now);
            companyDetails = companyDetailsRepository.save(companyDetails);
        }

        // 2. Create JobPostContentV1
        JobPostContentV1 content = new JobPostContentV1();
        content.setDescription(request.getJobPostDescription().getDescription());
        content.setCompanyDetails(companyDetails);
        content.setRevisionStatus(RevisionStatus.DRAFT.toString()); // or ACTIVE, as needed
        content.setCreatedAt(now);
        content.setUpdatedAt(now);
        content = jobPostContentV1Repository.save(content);

        // 3. Create JobPost
        JobPost jobPost = new JobPost();
        jobPost.setTitle(request.getTitle());
        jobPost.setJobPosterId(request.getJobPosterId());
        jobPost.setJobPostContent(content);
        jobPost.setStatus(JobPostStatus.DRAFT.toString()); // or ACTIVE, as needed
        jobPost.setCreatedAt(now);
        jobPost.setUpdatedAt(now);
        jobPost = jobPostRepository.save(jobPost);

        // 4. Build response
        CreateJobPostResponse response = new CreateJobPostResponse(jobPost.getId());
        return ServiceResult.success(response);
    }
}