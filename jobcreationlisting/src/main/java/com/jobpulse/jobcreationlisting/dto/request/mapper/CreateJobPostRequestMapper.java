package com.jobpulse.jobcreationlisting.dto.request.mapper;

import java.util.LinkedHashMap;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.jobpulse.jobcreationlisting.dto.request.jobpost.CompanyDetailsRequest;
import com.jobpulse.jobcreationlisting.dto.request.jobpost.CreateJobPostBodyRequest;
import com.jobpulse.jobcreationlisting.dto.request.jobpost.CreateJobPostRequest;
import com.jobpulse.jobcreationlisting.dto.request.jobpost.ExistingCompanyDetailsRequest;
import com.jobpulse.jobcreationlisting.model.CompanyDetails;
import com.jobpulse.jobcreationlisting.dto.request.UserContext;
import com.jobpulse.jobcreationlisting.dto.request.company.NewCompanyDetailsRequest;

@Mapper(componentModel = "spring")
public interface CreateJobPostRequestMapper {
    @Mapping(target = "jobPosterId", source = "userContext.id")
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "jobPostDescription.description", source ="request.jobPostDescription.description")
    @Mapping(target = "companyDetails", expression ="java(mapCompanyDetails(request.getCompanyDetails()))")
    CreateJobPostRequest toJobPostRequest(CreateJobPostBodyRequest request, UserContext userContext);

    default CompanyDetailsRequest mapCompanyDetails(Object companyDetailsObject) {
        if (companyDetailsObject == null) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        if (companyDetailsObject instanceof LinkedHashMap) {
            LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) companyDetailsObject;

            if (map.containsKey("companyDetailsId")) {
                try {
                    ExistingCompanyDetailsRequest source = objectMapper.convertValue(map, ExistingCompanyDetailsRequest.class);
                    return CompanyDetailsRequest.builder()
                        .companyDetailsId(source.getCompanyDetailsId())
                        .build();
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("The company ID provided is malformed.");
                }
            }
            else if (map.containsKey("name")) {
                NewCompanyDetailsRequest source = objectMapper.convertValue(map, NewCompanyDetailsRequest.class);
                return CompanyDetailsRequest.builder()
                    .name(source.getName())
                    .tagline(source.getTagline())
                    .phone(source.getPhone())
                    .build();
            }
        }

        return null;
    }
}
