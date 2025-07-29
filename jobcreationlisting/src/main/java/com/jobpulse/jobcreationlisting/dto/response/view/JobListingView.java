package com.jobpulse.jobcreationlisting.dto.response.view;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.UUID;
import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobListingView {
    private UUID jobPostId;
    private String title;
    private String description;
    private JobCompanyDetailsView companyDetails;
    private String status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}