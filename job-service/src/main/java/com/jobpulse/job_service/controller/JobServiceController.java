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
import java.util.List;

@RestController
@RequestMapping("/v1")
public class JobServiceController {

    private final JobService jobService;

    @Autowired
    public JobServiceController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs")
    public List<JobPost> getJobListings(@AuthenticationPrincipal Jwt jwt) {
        return jobService.getJobListings();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_JOB_POSTER')")
    @PostMapping("/jobs")
    public JobPost createJob(
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