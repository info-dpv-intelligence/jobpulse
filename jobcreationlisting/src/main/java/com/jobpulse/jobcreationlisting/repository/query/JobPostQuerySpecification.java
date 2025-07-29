package com.jobpulse.jobcreationlisting.repository.query;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.jobpulse.jobcreationlisting.model.JobPost;

import jakarta.persistence.criteria.Path;

public class JobPostQuerySpecification {
    public Specification<JobPost> withCursor(ZonedDateTime cursorCreatedAt, UUID cursorId) {
        return (root, query, cb) -> {
            Path<ZonedDateTime> createdAtPath = root.get("createdAt");
            Path<UUID> idPath = root.get("id");

            return cb.or(
                cb.lessThan(createdAtPath, cursorCreatedAt),
                cb.and(
                    cb.equal(createdAtPath, cursorCreatedAt),
                    cb.lessThan(idPath, cursorId))
            );
        };
    }
}
