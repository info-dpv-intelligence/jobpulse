package com.jobpulse.jobcreationlisting.repository.query;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.jobpulse.jobcreationlisting.dto.repository.command.GetJobPostsCommand;
import com.jobpulse.jobcreationlisting.model.JobPost;

@Component
public class JobPostQueryBuilder {

    private JobPostQuerySpecification jobPostQuerySpecification;

    @Autowired
    public JobPostQueryBuilder(
        JobPostQuerySpecification jobPostQuerySpecification
    ) {
        this.jobPostQuerySpecification = new JobPostQuerySpecification();
    }

    public JobPostQuery build(GetJobPostsCommand command) {

        List<Specification<JobPost>> specifications = new ArrayList<>();

        if (command.getCursorCreatedAt() != null && command.getCursorId() != null) {
            specifications.add(jobPostQuerySpecification.withCursor(command.getCursorCreatedAt(), command.getCursorId()));
        }
        
        Specification<JobPost>finalSpec = specifications.stream().reduce(Specification::and).orElseGet(Specification::unrestricted);

        // indexed base sort
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt", "id");

        if (command.getSortField() != null && !command.getSortField().equals("createdAt")) {

            sort = sort.and(
                Sort.by(command.getSortDirection() != null ? 
                        command.getSortDirection() : 
                        Sort.Direction.DESC, 
                        command.getSortField()
                )
            );
        }

        return new JobPostQuery(finalSpec, PageRequest.of(0, command.getPageSize() + 1, sort));
    }
}