package com.jobpulse.jobcreationlisting.service;

import com.jobpulse.jobcreationlisting.dto.request.CreateJobPostRequest;
import com.jobpulse.jobcreationlisting.dto.response.ServiceResult;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostResponse;

public interface JobPostCreationListingContract {
    // ServiceResult<JobListingsResponse> getJobListings(GetJobPostsRequest request);
    ServiceResult<CreateJobPostResponse> createJobPost(CreateJobPostRequest request);
}
