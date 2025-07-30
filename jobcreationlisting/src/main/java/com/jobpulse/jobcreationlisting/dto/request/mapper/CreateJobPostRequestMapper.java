package com.jobpulse.jobcreationlisting.dto.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jobpulse.jobcreationlisting.dto.request.jobpost.CreateJobPostBodyRequest;
import com.jobpulse.jobcreationlisting.dto.request.jobpost.CreateJobPostRequest;
import com.jobpulse.jobcreationlisting.dto.request.UserContext;

@Mapper(componentModel = "spring")
public interface CreateJobPostRequestMapper {
    @Mapping(target = "jobPosterId", source = "userContext.id")
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "jobPostDescription.description", source ="request.jobPostDescription.description")
    @Mapping(target = "companyDetails.companyDetailsId", source ="request.companyDetails.companyDetailsId")
    CreateJobPostRequest toJobPostRequest(CreateJobPostBodyRequest request, UserContext userContext);
}
