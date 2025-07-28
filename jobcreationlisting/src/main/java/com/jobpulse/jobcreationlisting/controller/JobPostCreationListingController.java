package com.jobpulse.jobcreationlisting.controller;

import com.jobpulse.jobcreationlisting.service.JobPostCreationListingContract;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostResponse;
import com.jobpulse.jobcreationlisting.dto.request.CreateJobPostBodyRequest;
import com.jobpulse.jobcreationlisting.dto.request.CreateJobPostRequest;
import com.jobpulse.jobcreationlisting.dto.request.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@Tag(name = "JobPostCreationListingController", description = "Job post creation and listing")
public class JobPostCreationListingController {

    private final JobPostCreationListingContract jobPostCreationListingService;

    @Autowired
    public JobPostCreationListingController(
            JobPostCreationListingContract jobPostCreationListingService
    ) {
        this.jobPostCreationListingService = jobPostCreationListingService;
    }

    @PostMapping("/jobs")
    @Operation(
            summary = "Create a new job posting"
    )
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<?> createJob(
            @RequestBody @Valid CreateJobPostBodyRequest createJobPostBodyRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UserContext userContext = UserContext.fromJwt(jwt);
        CreateJobPostRequest createJobPostRequest = new CreateJobPostRequest();
        createJobPostRequest.setJobPosterId(UUID.fromString(userContext.getId()));
        createJobPostRequest.setTitle(createJobPostBodyRequest.getTitle());
        createJobPostRequest.setJobPostDescription(createJobPostBodyRequest.getJobPostDescription());
        createJobPostRequest.setCompanyDetails(createJobPostBodyRequest.getCompanyDetails());
        createJobPostRequest.setJobPostPreRequisites(createJobPostBodyRequest.getJobPostPreRequisites());
        CreateJobPostResponse createJobPostResponse = jobPostCreationListingService.createJobPost(createJobPostRequest).getData();

        return ResponseEntity.ok(createJobPostResponse.getJobPostId());
    }
}
