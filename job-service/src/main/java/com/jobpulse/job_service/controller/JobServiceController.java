package com.jobpulse.job_service.controller;

import com.jobpulse.job_service.model.JobPost;
import com.jobpulse.job_service.service.JobService;

import com.jobpulse.job_service.dto.CreateJobPostCommand;
import com.jobpulse.job_service.dto.CreateJobPostRequest;
import com.jobpulse.job_service.dto.UserContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import com.jobpulse.job_service.dto.CreatedResponse;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/v1")
@Tag(name = "Job Management", description = "Job posting and listing operations")
public class JobServiceController {

    private final JobService jobService;

    @Autowired
    public JobServiceController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs")
    @Operation(
        summary = "Get job listings",
        description = "Retrieve paginated list of job postings"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved job listings",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - JWT token required"
        )
    })
    @SecurityRequirement(name = "bearer-jwt")
    public Page<JobPost> getJobListings(
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        return jobService.getJobListings(pageable);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_JOB_POSTER')")
    @PostMapping("/jobs")
    @Operation(
        summary = "Create new job posting",
        description = "Create a new job posting (requires ADMIN or JOB_POSTER role)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Job created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreatedResponse.class),
                examples = @ExampleObject(
                    value = "{\"id\": \"123\", \"message\": \"Job created successfully\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid job posting data"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - JWT token required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - insufficient permissions"
        )
    })
    @SecurityRequirement(name = "bearer-jwt")
    public CreatedResponse createJob(
            @Parameter(description = "Job posting details", required = true)
            @RequestBody @Valid CreateJobPostRequest createJobPostRequest,
            @AuthenticationPrincipal Jwt jwt) {
        UserContext userContext = UserContext.fromJwt(jwt);
        CreateJobPostCommand command = new CreateJobPostCommand();
        command.setTitle(createJobPostRequest.getTitle());
        command.setDescription(createJobPostRequest.getDescription());
        command.setJobPosterId(userContext.getId());

        return jobService.createJob(command);
    }
}