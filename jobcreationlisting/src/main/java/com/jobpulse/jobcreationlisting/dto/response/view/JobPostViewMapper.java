package com.jobpulse.jobcreationlisting.dto.response.view;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.jobpulse.jobcreationlisting.model.CompanyDetails;
import com.jobpulse.jobcreationlisting.model.JobPost;

@Mapper(componentModel = "spring")
public interface JobPostViewMapper {
    
    @Mapping(source = "id", target = "jobPostId")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "jobPostContent.description", target = "description")
    @Mapping(source = "jobPostContent.companyDetails", target = "companyDetails")
    @Mapping(source = "status", target = "status")
    JobListingView toJobListingView(JobPost jobPost);
    
    @Mapping(source = "companyDetailsId", target = "companyDetailsId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "tagline", target = "tagline")
    @Mapping(source = "phone", target = "phone")
    JobCompanyDetailsView toJobCompanyDetailsView(CompanyDetails companyDetails);
    
    default Page<JobListingView> toJobListingViewPage(Page<JobPost> jobPosts) {
        return jobPosts.map(this::toJobListingView);
    }
}