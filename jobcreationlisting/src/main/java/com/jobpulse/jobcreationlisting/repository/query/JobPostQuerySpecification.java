package com.jobpulse.jobcreationlisting.repository.query;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.data.domain.Sort;
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
        String cursorIdFieldName,
        Sort.Direction sortDirection
    ) {
        return (root, query, cb) -> {
            Path<ZonedDateTime> createdAtPath = root.get(cursorCreatedAtFieldName);
            Path<UUID> idPath = root.get(cursorIdFieldName);

            boolean isDesc = sortDirection.equals(Sort.Direction.DESC);
            
            return cb.or(
                isDesc ? cb.lessThan(createdAtPath, cursorCreatedAt) 
                    : cb.greaterThan(createdAtPath, cursorCreatedAt),
                cb.and(
                    cb.equal(createdAtPath, cursorCreatedAt),
                    isDesc ? cb.lessThan(idPath, cursorId) 
                        : cb.greaterThan(idPath, cursorId)
                )
            );
        };
    }

}
