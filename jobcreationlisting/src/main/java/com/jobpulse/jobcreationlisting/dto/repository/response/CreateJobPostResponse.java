package com.jobpulse.jobcreationlisting.dto.repository.response;

import lombok.Data;
import java.util.UUID;

@Data
public class CreateJobPostResponse {
    private UUID jobPostId;

    public CreateJobPostResponse(UUID jobPostId) {
        this.jobPostId = jobPostId;
    }
}