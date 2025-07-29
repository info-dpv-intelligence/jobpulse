package com.jobpulse.jobcreationlisting.dto.response.view;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobCompanyDetailsView {
    private UUID companyDetailsId;
    private String name;
    private String tagline;
    private String phone;
}