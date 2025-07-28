package com.jobpulse.jobcreationlisting.dto.repository.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobPostCompanyResponse {
    private UUID jobPostCompanyId;
}