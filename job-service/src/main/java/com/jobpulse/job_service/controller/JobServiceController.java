package com.jobpulse.job_service.controller;

import com.jobpulse.job_service.model.JobPost;
import com.jobpulse.job_service.service.JobServiceContract;
import com.jobpulse.job_service.dto.CreateJobPostCommand;
import com.jobpulse.job_service.dto.CreateJobPostRequest;
import com.jobpulse.job_service.dto.UserContext;
import com.jobpulse.job_service.dto.JobListingsResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import com.jobpulse.job_service.dto.CreatedResponse;

import java.util.List;
import java.util.Map;
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

    private final JobServiceContract jobService;

    @Autowired
    public JobServiceController(JobServiceContract jobService) {
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
                schema = @Schema(implementation = JobListingsResponse.class),
                examples = @ExampleObject(
                    value = "{\"jobs\": [{\"id\": \"123\", \"title\": \"Software Engineer\", \"description\": \"Join our team...\"}], \"totalElements\": 1, \"totalPages\": 1}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - JWT token required",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"Unauthorized access\", \"code\": \"UNAUTHORIZED\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"Internal server error\", \"code\": \"INTERNAL_ERROR\"}"
                )
            )
        )
    })
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<?> getJobListings(
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        
        var result = jobService.getJobListings(pageable);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getData());
        } else {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", result.getErrorMessage(),
                "code", result.getErrorCode()
            ));
        }
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
    public ResponseEntity<?> createJob(
            @Parameter(description = "Job posting details", required = true)
            @RequestBody @Valid CreateJobPostRequest createJobPostRequest,
            @AuthenticationPrincipal Jwt jwt) {
        
        UserContext userContext = UserContext.fromJwt(jwt);
        CreateJobPostCommand command = new CreateJobPostCommand();
        command.setTitle(createJobPostRequest.getTitle());
        command.setDescription(createJobPostRequest.getDescription());
        command.setJobPosterId(userContext.getId());

        var result = jobService.createJob(command);
        
        if (result.isSuccess()) {
            return ResponseEntity.status(201).body(result.getData());
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "error", result.getErrorMessage(),
                "code", result.getErrorCode()
            ));
        }
    }
}