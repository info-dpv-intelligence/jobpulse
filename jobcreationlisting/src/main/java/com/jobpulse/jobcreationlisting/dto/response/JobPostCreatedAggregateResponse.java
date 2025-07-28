package com.jobpulse.jobcreationlisting.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostCreatedAggregateResponse {
    private UUID jobPostId;
    private UUID jobPostContentId;
    private UUID companyDetailsId;
}
