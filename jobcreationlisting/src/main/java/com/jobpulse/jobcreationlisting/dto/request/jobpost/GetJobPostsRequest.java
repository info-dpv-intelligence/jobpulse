package com.jobpulse.jobcreationlisting.dto.request.jobpost;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetJobPostsRequest {
    private String cursor;
    private Integer limit;
    private String sortField = "createdAt";
    private String sortDirection = "DESC";
}