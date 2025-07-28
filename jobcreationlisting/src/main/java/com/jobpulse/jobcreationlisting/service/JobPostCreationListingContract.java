package com.jobpulse.jobcreationlisting.service;

import com.jobpulse.jobcreationlisting.dto.request.CreateJobPostRequest;
import com.jobpulse.jobcreationlisting.dto.response.JobPostCreatedAggregateResponse;
import com.jobpulse.jobcreationlisting.dto.response.ServiceResult;

public interface JobPostCreationListingContract {
    // ServiceResult<JobListingsResponse> getJobListings(GetJobPostsRequest request);
    ServiceResult<JobPostCreatedAggregateResponse> createJobPost(CreateJobPostRequest request);
}
