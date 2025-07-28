package com.jobpulse.jobcreationlisting.repository.mapper;

import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostCompanyDetailsCommand;
import com.jobpulse.jobcreationlisting.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyDetailsMapper {
    @Mapping(target = "companyDetailsId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CompanyDetails toEntity(CreateJobPostCompanyDetailsCommand command);
}
