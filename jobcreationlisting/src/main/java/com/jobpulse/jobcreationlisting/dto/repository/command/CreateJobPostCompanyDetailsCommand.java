package com.jobpulse.jobcreationlisting.dto.repository.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

import org.springframework.lang.Nullable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobPostCompanyDetailsCommand {
    @Nullable
    private UUID companyDetailsId;
    private String name;
    private String tagline;
    private String phone;
}