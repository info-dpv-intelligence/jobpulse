package com.jobpulse.job_service.controller;

import com.jobpulse.job_service.model.JobPost;
import com.jobpulse.job_service.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class JobServiceController {

    private final JobService jobService;

    @Autowired
    public JobServiceController(JobService jobService) {
        this.jobService = jobService;
    }

    // @PreAuthorize("hasAuthority('ROLE_JOB_POSTER') or hasAuthority('ROLE_JOB_POSTER')")
    @GetMapping("/jobs")
    public List<JobPost> getJobListings() {
        return jobService.getJobListings();
    }
}