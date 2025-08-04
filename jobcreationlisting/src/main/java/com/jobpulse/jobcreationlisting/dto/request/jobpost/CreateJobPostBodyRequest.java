package com.jobpulse.jobcreationlisting.dto.request.jobpost;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

import com.jobpulse.jobcreationlisting.dto.request.company.NewCompanyDetailsRequest;

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

    @Schema(
        description = "Company details. Either provide an existing company ID or the details for a new company.",
        oneOf = {NewCompanyDetailsRequest.class, ExistingCompanyDetailsRequest.class}
    )
    private Object companyDetails;

    private JobPostPreRequisitesRequest jobPostPreRequisites;
}