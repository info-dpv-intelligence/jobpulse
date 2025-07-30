package com.jobpulse.jobcreationlisting.dto.request.jobpost;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetJobPostsRequest {
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}