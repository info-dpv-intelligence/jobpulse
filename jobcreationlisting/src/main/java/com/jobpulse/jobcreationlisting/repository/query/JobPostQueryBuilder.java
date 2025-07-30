package com.jobpulse.jobcreationlisting.repository.query;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.jobpulse.jobcreationlisting.dto.repository.command.GetJobPostsCommand;
import com.jobpulse.jobcreationlisting.model.JobPost;
import com.jobpulse.jobcreationlisting.model.properties.JobPostProperties;

@Component
public class JobPostQueryBuilder {

    private JobPostQuerySpecification jobPostQuerySpecification;
    private JobPostProperties jobPostProperties;

    @Autowired
    public JobPostQueryBuilder(
        JobPostQuerySpecification jobPostQuerySpecification,
        JobPostProperties jobPostProperties
    ) {
        this.jobPostQuerySpecification = jobPostQuerySpecification;
        this.jobPostProperties = jobPostProperties;
    }

    public JobPostQuery build(GetJobPostsCommand command) {

        List<Specification<JobPost>> specifications = new ArrayList<>();

        if (command.getCursorCreatedAt() != null && command.getCursorId() != null) {
            specifications.add(jobPostQuerySpecification.withCursor(
                    command.getCursorCreatedAt(), 
                    jobPostProperties.CREATED_AT,
                    command.getCursorId(),
                    jobPostProperties.ID,
                    command.getSortDirection()
                )
            );
        }
        
        Specification<JobPost> finalSpec = specifications.stream().reduce(Specification::and).orElseGet(Specification::unrestricted);

        // indexed base sort
        Sort sort = Sort.by(
            command.getSortDirection(), 
            jobPostProperties.CREATED_AT, 
            jobPostProperties.ID
        );

        if (command.getSortField() != null && !command.getSortField().equals(jobPostProperties.CREATED_AT)) {

            sort = sort.and(
                Sort.by(command.getSortDirection() != null ? 
                        command.getSortDirection() : 
                        Sort.Direction.DESC, 
                        command.getSortField()
                )
            );
        }

        return new JobPostQuery(
            finalSpec, 
            PageRequest.of(
                0, 
                command.getPageSize(), 
                sort
            )
        );
    }
}
