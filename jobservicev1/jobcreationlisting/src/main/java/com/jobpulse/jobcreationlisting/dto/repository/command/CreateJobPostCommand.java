package com.jobpulse.jobcreationlisting.dto.repository.command;

import lombok.Data;
import com.jobpulse.jobcreationlisting.model.JobPostStatus;

@Data
public class CreateJobPostCommand {
    private String title;
    private String jobPosterId;
    private String jobPostContentId;
    private JobPostStatus status;
}