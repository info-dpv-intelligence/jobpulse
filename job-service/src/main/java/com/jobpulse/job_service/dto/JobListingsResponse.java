package com.jobpulse.job_service.dto;

import com.jobpulse.job_service.model.JobPost;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

/**
 * Response DTO for job listings with pagination information.
 */
@Schema(description = "Paginated response containing job listings")
@Getter
@AllArgsConstructor
public class JobListingsResponse {
    
    @Schema(description = "Page containing job postings")
    private final Page<JobPost> jobs;
    
    @Schema(description = "Total number of job postings available", example = "150")
    private final long totalElements;
    
    @Schema(description = "Total number of pages", example = "15")
    private final int totalPages;
    
    @Schema(description = "Current page number (0-based)", example = "0")
    private final int currentPage;
    
    @Schema(description = "Number of items per page", example = "10")
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
