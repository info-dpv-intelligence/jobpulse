package com.jobpulse.job_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateJobPostCommand(
    @NotNull(message = "Job ID is required")
    @NotBlank(message = "Job ID cannot be empty")
    String jobId,
    
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    String title,
    
    @Size(min = 1, max = 5000, message = "Description must be between 1 and 5000 characters")
    String description,
    
    Boolean isActive
) {

}
