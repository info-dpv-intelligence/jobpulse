package com.jobpulse.jobcreationlisting.repository.query;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.jobpulse.jobcreationlisting.model.JobPost;

import lombok.Value;

@Value
public class JobPostQuery {
    Specification<JobPost> specification;
    PageRequest pageRequest;
}
