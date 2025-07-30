package com.jobpulse.jobcreationlisting.dto.response.view;

import java.util.UUID;

public record JobCompanyDetailsView(
    UUID companyDetailsId,
    String name,
    String tagline,
    String phone
) {}