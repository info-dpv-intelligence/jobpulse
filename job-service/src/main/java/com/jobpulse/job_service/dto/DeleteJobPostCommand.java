package com.jobpulse.job_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeleteJobPostCommand(
    @NotNull(message = "Job ID is required")
    @NotBlank(message = "Job ID cannot be empty")
    String jobId
) {
}
