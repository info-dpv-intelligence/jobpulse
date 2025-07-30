package com.jobpulse.jobcreationlisting.dto.request.jobpost;

import lombok.Data;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetJobPostsRequest {
    @Valid
    private String sortBy = "createdAt";
    @Valid
    private String sortDirection = "DESC";
}