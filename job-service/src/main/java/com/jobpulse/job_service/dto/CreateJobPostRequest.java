package com.jobpulse.job_service.dto;

import lombok.Data;

@Data
public class CreateJobPostRequest {
    private String title;
    private String description;
}