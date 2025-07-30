package com.jobpulse.jobcreationlisting.repository.query;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.jobpulse.jobcreationlisting.model.JobPost;

import jakarta.persistence.criteria.Path;

@Component
public class JobPostQuerySpecification {
    public Specification<JobPost> withCursor(
        ZonedDateTime cursorCreatedAt, 
        String cursorCreatedAtFieldName, 
        UUID cursorId,
        String cursorIdFieldName
    ) {
        return (root, query, cb) -> {
            Path<ZonedDateTime> createdAtPath = root.get(cursorCreatedAtFieldName);
            Path<UUID> idPath = root.get(cursorIdFieldName);

            return cb.or(
                cb.lessThan(createdAtPath, cursorCreatedAt),
                cb.and(
                    cb.equal(createdAtPath, cursorCreatedAt),
                    cb.lessThan(idPath, cursorId))
            );
        };
    }
}
