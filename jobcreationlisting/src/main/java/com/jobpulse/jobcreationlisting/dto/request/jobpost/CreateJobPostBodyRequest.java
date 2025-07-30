package com.jobpulse.jobcreationlisting.dto.request.jobpost;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobPostBodyRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Job post description is required")
    private JobPostDescriptionRequest jobPostDescription;

    private CompanyDetailsRequest companyDetails;

    private JobPostPreRequisitesRequest jobPostPreRequisites;
}