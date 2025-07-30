package com.jobpulse.jobcreationlisting.dto.repository.command;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class GetJobPostsCommand {
    private final Integer pageSize;
    private final String cursorId;
    private final ZonedDateTime cursorCreatedAt;
    private final String sortField;
    private final String sortDirection;
}
