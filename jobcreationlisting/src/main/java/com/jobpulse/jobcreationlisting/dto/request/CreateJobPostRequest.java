package com.jobpulse.jobcreationlisting.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateJobPostRequest {
    private UUID jobPosterId;
    private String title;
    private JobPostDescriptionRequest jobPostDescription;
    private CompanyDetailsRequest companyDetails;
    private JobPostPreRequisitesRequest jobPostPreRequisites;
}