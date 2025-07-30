package com.jobpulse.jobcreationlisting.service;

import com.jobpulse.jobcreationlisting.dto.request.jobpost.CreateJobPostRequest;
import com.jobpulse.jobcreationlisting.dto.request.jobpost.GetJobPostsRequest;
import com.jobpulse.jobcreationlisting.dto.response.JobListingsResponse;
import com.jobpulse.jobcreationlisting.dto.response.JobPostCreatedAggregateResponse;
import com.jobpulse.jobcreationlisting.dto.response.ServiceResult;

public interface JobPostCreationListingContract {
    ServiceResult<JobListingsResponse> getJobPosts(GetJobPostsRequest request);
    ServiceResult<JobPostCreatedAggregateResponse> createJobPost(CreateJobPostRequest request);
}
