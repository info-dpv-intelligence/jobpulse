package com.jobpulse.jobcreationlisting.dto.response.view;

import java.util.UUID;

public record JobListingView(
    UUID jobPostId,
    String title,
    String description,
    JobCompanyDetailsView companyDetails,
    String status
    
) {}