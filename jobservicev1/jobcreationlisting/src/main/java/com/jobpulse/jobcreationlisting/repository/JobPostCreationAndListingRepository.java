package com.jobpulse.jobcreationlisting.repository;

import com.jobpulse.jobcreationlisting.dto.command.CreateJobPostCommand;
import com.jobpulse.jobcreationlisting.dto.response.CreateJobPostResponse;
import com.jobpulse.jobcreationlisting.dto.response.OperationResult;

public interface JobPostCreationAndListingRepository {
    OperationResult<CreateJobPostResponse> createJobPost(CreateJobPostCommand command);
}