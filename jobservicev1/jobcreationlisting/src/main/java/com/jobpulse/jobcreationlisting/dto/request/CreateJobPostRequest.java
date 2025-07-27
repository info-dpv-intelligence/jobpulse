package com.jobpulse.jobcreationlisting.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CreateJobPostRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Job post description is required")
    private JobPostDescriptionRequest jobPostDescription;

    private CompanyDetailsRequest companyDetails;

    private JobPostPreRequisitesRequest jobPostPreRequisites;
}