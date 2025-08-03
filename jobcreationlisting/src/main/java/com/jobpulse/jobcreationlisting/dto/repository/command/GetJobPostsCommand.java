package com.jobpulse.jobcreationlisting.dto.repository.command;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class GetJobPostsCommand {
    private final ZonedDateTime cursorCreatedAt;
    private final UUID cursorId;
    private final int pageSize;
    private final String sortField;
    @Builder.Default
    private final Sort.Direction sortDirection = Sort.Direction.DESC;
}
