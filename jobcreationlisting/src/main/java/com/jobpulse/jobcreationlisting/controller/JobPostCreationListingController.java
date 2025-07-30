package com.jobpulse.jobcreationlisting.controller;

import com.jobpulse.jobcreationlisting.service.JobPostCreationListingContract;
import com.jobpulse.jobcreationlisting.dto.request.jobpost.CreateJobPostBodyRequest;
import com.jobpulse.jobcreationlisting.dto.request.jobpost.CreateJobPostRequest;
import com.jobpulse.jobcreationlisting.dto.request.jobpost.GetJobPostsRequest;
import com.jobpulse.jobcreationlisting.dto.request.mapper.CreateJobPostRequestMapper;
import com.jobpulse.jobcreationlisting.dto.request.UserContext;
import com.jobpulse.jobcreationlisting.dto.response.JobListingsResponse;
import com.jobpulse.jobcreationlisting.dto.response.JobPostCreatedAggregateResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@Tag(name = "JobPostCreationListingController", description = "Job post creation and listing")
public class JobPostCreationListingController {

    private final JobPostCreationListingContract jobPostCreationListingService;
    private final CreateJobPostRequestMapper createJobPostRequestMapper;


    @Autowired
    public JobPostCreationListingController(
            JobPostCreationListingContract jobPostCreationListingService,
            CreateJobPostRequestMapper createJobPostRequestMapper
    ) {
        this.jobPostCreationListingService = jobPostCreationListingService;
        this.createJobPostRequestMapper = createJobPostRequestMapper;
    }

    @GetMapping("/jobs")
    @Operation(
            summary = "Get all job postings"
    )
    public ResponseEntity<JobListingsResponse> getJobPosts(
        @Valid @ModelAttribute GetJobPostsRequest getJobPostsRequest
    ) {
        return ResponseEntity.ok(
            jobPostCreationListingService.getJobPosts(
                getJobPostsRequest
            ).getData()
        );
    }

    @PostMapping("/jobs")
    @Operation(
            summary = "Create a new job posting"
    )
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<JobPostCreatedAggregateResponse> createJob(
            @RequestBody @Valid CreateJobPostBodyRequest createJobPostBodyRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UserContext userContext = UserContext.fromJwt(jwt);
        CreateJobPostRequest createJobPostRequest = createJobPostRequestMapper.toJobPostRequest(
            createJobPostBodyRequest, 
            userContext
        );
            
        JobPostCreatedAggregateResponse jobPostCreatedAggregateResponse = (
            jobPostCreationListingService
                .createJobPost(createJobPostRequest)
                .getData()
        );

        return ResponseEntity.ok(jobPostCreatedAggregateResponse);

    }
}
