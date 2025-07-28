package com.jobpulse.jobcreationlisting.repository;

import com.jobpulse.jobcreationlisting.dto.repository.response.CreateJobPostResponse;
import com.jobpulse.jobcreationlisting.dto.repository.response.OperationResult;
import com.jobpulse.jobcreationlisting.model.JobPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JobPostCreationAndListingRepositoryImpl implements JobPostCreationAndListingRepository {

    // private final JobPostRepository jobPostRepository;

    // @Autowired
    // public JobPostCreationAndListingRepositoryImpl(JobPostRepository jobPostRepository) {
    //     this.jobPostRepository = jobPostRepository;
    // }

    // @Override
    // public OperationResult<CreateJobPostResponse> createJobPost(CreateJobPostCommand command) {
    //     JobPost jobPost = new JobPost();
    //     jobPost.setId(UUID.randomUUID());
    //     jobPost.setTitle(command.getTitle());
    //     jobPost.setJobPosterId(command.getJobPosterId());
    //     jobPost.setJobPostContentId(command.getJobPostContentId());
    //     jobPost.setStatus(command.getStatus());
    //     jobPost.setCreatedAt(ZonedDateTime.now());
    //     jobPost.setUpdatedAt(ZonedDateTime.now());

    //     jobPostRepository.save(jobPost);

    //     CreateJobPostResponse response = new CreateJobPostResponse(jobPost.getId());
    //     return OperationResult.success(response);
    // }

    // @Override    
    // OperationResult<CreateJobPostCompanyResponse> createJobPostCompany(CreateJobPostCompanyCommand command) {
        
    // }
    // @Override
    // OperationResult<CreateJobPostContentResponse> createJobPostContent(CreateJobPostContentCommand command) {
    // }
}