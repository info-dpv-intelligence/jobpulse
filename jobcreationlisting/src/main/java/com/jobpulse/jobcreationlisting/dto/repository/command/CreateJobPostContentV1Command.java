package com.jobpulse.jobcreationlisting.dto.repository.command;

import com.jobpulse.jobcreationlisting.model.RevisionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobPostContentV1Command {
    private String description;
    private UUID companyDetailsId;
    private RevisionStatus revisionStatus;
}

