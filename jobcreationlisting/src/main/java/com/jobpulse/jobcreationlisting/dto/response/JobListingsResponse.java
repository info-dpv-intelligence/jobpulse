package com.jobpulse.jobcreationlisting.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobListingsResponse {
    private List<JobListingView> content;
    private boolean hasNext;
}