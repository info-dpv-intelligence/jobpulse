package com.jobpulse.jobcreationlisting.dto.repository.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.jobpulse.jobcreationlisting.model.JobPostStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobPostCommand {
    private String title;
    private UUID jobPosterId;
    private UUID jobPostContentId;
    private JobPostStatus status;
}