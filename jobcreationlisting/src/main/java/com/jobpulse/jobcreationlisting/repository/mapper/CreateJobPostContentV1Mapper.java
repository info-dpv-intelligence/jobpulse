package com.jobpulse.jobcreationlisting.repository.mapper;

import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostContentV1Command;
import com.jobpulse.jobcreationlisting.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreateJobPostContentV1Mapper {
    @Mapping(target = "jobPostContentId", ignore = true)
    @Mapping(source = "description", target = "description")
    @Mapping(source = "companyDetailsId", target = "companyDetails.companyDetailsId")
    @Mapping(target = "revisionStatus", source = "revisionStatus", defaultExpression = "java(com.jobpulse.jobcreationlisting.model.RevisionStatus.DRAFT.toString())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    JobPostContentV1 toEntity(CreateJobPostContentV1Command command);
}
