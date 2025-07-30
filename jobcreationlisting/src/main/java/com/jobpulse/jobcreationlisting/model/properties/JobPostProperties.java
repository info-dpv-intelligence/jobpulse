package com.jobpulse.jobcreationlisting.model.properties;

import org.springframework.stereotype.Component;
import com.jobpulse.jobcreationlisting.model.JobPost_;

@Component
public final class JobPostProperties {
    
    public final String CREATED_AT = JobPost_.createdAt.getName();
    public final String ID = JobPost_.id.getName();
    //TODO: move to a separate configuration
    public final Integer DEFAULT_PAGE_SIZE = 10;
}