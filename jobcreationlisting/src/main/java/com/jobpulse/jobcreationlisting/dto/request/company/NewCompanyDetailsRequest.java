package com.jobpulse.jobcreationlisting.dto.request.company;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Details for a new company, requiring a name and other fields.")
public class NewCompanyDetailsRequest {
    @NotBlank(message = "Company name is required for new companies")
    @Size(max = 255, message = "Company name must be at most 255 characters")
    private String name;

    @Size(max = 255, message = "Tagline must be at most 255 characters")
    private String tagline;

    @Size(max = 20, message = "Phone number must be at most 20 characters")
    private String phone;
}