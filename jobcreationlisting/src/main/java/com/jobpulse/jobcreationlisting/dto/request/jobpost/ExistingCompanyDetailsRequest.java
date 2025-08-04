package com.jobpulse.jobcreationlisting.dto.request.jobpost;

import lombok.Data;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Details for an existing company, requiring only the ID.")
public class ExistingCompanyDetailsRequest {
    @NotNull(message = "Company ID is required for existing companies")
    private UUID companyDetailsId;
}