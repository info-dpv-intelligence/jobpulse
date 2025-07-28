package com.jobpulse.jobcreationlisting.repository.mapper;

import com.jobpulse.jobcreationlisting.model.JobPost;
import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostCommand;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobPostMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "title", target = "title")
    @Mapping(source = "jobPosterId", target = "jobPosterId")
    @Mapping(source = "jobPostContentId", target = "jobPostContent.jobPostContentId")
    @Mapping(source = "status", target = "status", defaultExpression = "java(com.jobpulse.jobcreationlisting.model.JobPostStatus.DRAFT.toString())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "jobPostContent.createdAt", ignore = true)
    @Mapping(target = "jobPostContent.updatedAt", ignore = true)
    JobPost toEntity(CreateJobPostCommand request);
}
