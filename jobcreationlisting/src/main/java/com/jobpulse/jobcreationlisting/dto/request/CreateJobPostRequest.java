package com.jobpulse.jobcreationlisting.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Data
@NoArgsConstructor
@Setter
public class CreateJobPostRequest {
    private UUID jobPosterId;
    private String title;
    private JobPostDescriptionRequest jobPostDescription;
    private CompanyDetailsRequest companyDetails;
    private JobPostPreRequisitesRequest jobPostPreRequisites;
}