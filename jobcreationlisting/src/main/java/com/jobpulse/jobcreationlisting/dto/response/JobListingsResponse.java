package com.jobpulse.jobcreationlisting.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.util.List;

import com.jobpulse.jobcreationlisting.dto.response.view.JobListingView;

@Data
@Builder
@AllArgsConstructor
public class JobListingsResponse {
    private List<JobListingView> jobPostListings;
    private boolean hasNext;
}