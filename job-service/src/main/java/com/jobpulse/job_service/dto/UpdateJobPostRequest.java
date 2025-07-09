package com.jobpulse.job_service.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateJobPostRequest {
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    @Size(min = 1, max = 5000, message = "Description must be between 1 and 5000 characters")
    private String description;
    
    private Boolean isActive; // TODO: to be replaced
}
