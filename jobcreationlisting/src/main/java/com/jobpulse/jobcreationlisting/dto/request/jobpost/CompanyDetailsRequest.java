package com.jobpulse.jobcreationlisting.dto.request.jobpost;

import lombok.Data;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class CompanyDetailsRequest {
    private UUID companyDetailsId;

    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must be at most 255 characters")
    private String name;

    @Size(max = 255, message = "Tagline must be at most 255 characters")
    private String tagline;

    @Size(max = 20, message = "Phone number must be at most 20 characters")
    private String phone;
}