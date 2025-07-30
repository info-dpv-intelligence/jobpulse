package com.jobpulse.jobcreationlisting.dto.util.cursor;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CursorV1 {
    private final ZonedDateTime createdAt;
    private final UUID id;
}
