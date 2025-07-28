package com.jobpulse.jobcreationlisting.repository;

import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostCommand;
import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostCompanyDetailsCommand;
import com.jobpulse.jobcreationlisting.dto.repository.command.CreateJobPostContentV1Command;
import com.jobpulse.jobcreationlisting.repository.mapper.CompanyDetailsMapper;
import com.jobpulse.jobcreationlisting.repository.mapper.CreateJobPostContentV1Mapper;
import com.jobpulse.jobcreationlisting.repository.mapper.JobPostMapper;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostCompanyResponse;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostContentResponse;
import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostResponse;
import com.jobpulse.jobcreationlisting.dto.repository.response.OperationResult;
import com.jobpulse.jobcreationlisting.model.CompanyDetails;
import com.jobpulse.jobcreationlisting.model.JobPost;
import com.jobpulse.jobcreationlisting.model.JobPostContentV1;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JobPostCreationAndListingRepositoryImpl implements JobPostCreationAndListingRepository {

    private final JobPostRepository jobPostRepository;

    private final JobPostCompanyDetailsRepository jobPostCompanyDetailsRepository;

    private final JobPostContentV1Repository jobPostContentV1Repository;

    private final CompanyDetailsMapper companyDetailsMapper;

    private final CreateJobPostContentV1Mapper createJobPostContentV1Mapper;

    private final JobPostMapper jobPostMapper;


    @Autowired
    public JobPostCreationAndListingRepositoryImpl(
        JobPostRepository jobPostRepository,
        JobPostCompanyDetailsRepository jobPostCompanyDetailsRepository,
        JobPostContentV1Repository jobPostContentV1Repository,
        CompanyDetailsMapper companyDetailsMapper,
        CreateJobPostContentV1Mapper createJobPostContentV1Mapper,
        JobPostMapper jobPostMapper
    ) {
        this.jobPostRepository = jobPostRepository;
        this.jobPostCompanyDetailsRepository = jobPostCompanyDetailsRepository;
        this.jobPostContentV1Repository = jobPostContentV1Repository;
        this.companyDetailsMapper = companyDetailsMapper;
        this.createJobPostContentV1Mapper = createJobPostContentV1Mapper;
        this.jobPostMapper = jobPostMapper;
    }

    @Override
    public OperationResult<CreateJobPostCompanyResponse> createJobPostCompany(CreateJobPostCompanyDetailsCommand command) {
        CompanyDetails companyDetails = jobPostCompanyDetailsRepository.save(
            companyDetailsMapper.toEntity(command)
        );

        return OperationResult.success(
            CreateJobPostCompanyResponse.builder()
                .jobPostCompanyId(companyDetails.getCompanyDetailsId())
                .build()
            );
    }

    @Override
    public OperationResult<CreateJobPostContentResponse> createJobPostContent(CreateJobPostContentV1Command command) {
        JobPostContentV1 jobPostContentV1 = createJobPostContentV1Mapper.toEntity(command);
        if (command.getCompanyDetailsId() != null) {
            findCompanyDetailsById(command.getCompanyDetailsId())
            .ifPresent(cd -> {
                jobPostContentV1.setCompanyDetails(cd);
            });
        }
        JobPostContentV1 saved = jobPostContentV1Repository.save(jobPostContentV1);

        return OperationResult.success(
            CreateJobPostContentResponse.builder()
                .jobPostContentId(saved.getJobPostContentId())
                .build()
        );
    }

    @Override
    public OperationResult<CreateJobPostResponse> createJobPost(CreateJobPostCommand command) {
        JobPost jobPost = jobPostMapper.toEntity(command);
        jobPostContentV1Repository.findById(command.getJobPostContentId())
        .ifPresent(jpc ->{
            jobPost.setJobPostContent(jpc);
        });

        JobPost jobPostSaved = jobPostRepository.save(jobPost);

        return OperationResult.success(
            CreateJobPostResponse.builder()
                .jobPostId(jobPostSaved.getId())
                .build()
        );
    }

    public Optional<CompanyDetails> findCompanyDetailsById(UUID id) {
        return jobPostCompanyDetailsRepository.findById(id);
    }
}