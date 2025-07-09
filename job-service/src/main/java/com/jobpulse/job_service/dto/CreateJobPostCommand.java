package com.jobpulse.job_service.dto;

import lombok.Data;

@Data
public class CreateJobPostCommand {
    private String title;
    private String description;
    private String jobPosterId;
    private Boolean isActive;
}
