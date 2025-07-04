package com.jobpulse.job_service.dto;

import com.jobpulse.job_service.model.JobPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

/**
 * Response DTO for job listings with pagination information.
 */
@Getter
@AllArgsConstructor
public class JobListingsResponse {
    private final Page<JobPost> jobs;
    private final long totalElements;
    private final int totalPages;
    private final int currentPage;
    private final int pageSize;
    
    public static JobListingsResponse from(Page<JobPost> jobPage) {
        return new JobListingsResponse(
            jobPage,
            jobPage.getTotalElements(),
            jobPage.getTotalPages(),
            jobPage.getNumber(),
            jobPage.getSize()
        );
    }
}
