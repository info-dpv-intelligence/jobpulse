package com.jobpulse.job_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobPostUpdatedResponse {
    private String id;
    private String message;
    
    public static JobPostUpdatedResponse success(String jobId) {
        return new JobPostUpdatedResponse(jobId, "Job post updated successfully");
    }
}
