package com.jobpulse.jobcreationlisting.dto.util.cursor;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CursorV1 {
    private final ZonedDateTime createdAt;
    private final UUID id;
    
    @JsonCreator
    public CursorV1(
        @JsonProperty("createdAt") ZonedDateTime createdAt,
        @JsonProperty("id") UUID id
    ) {
        this.createdAt = createdAt;
        this.id = id;
    }
}
