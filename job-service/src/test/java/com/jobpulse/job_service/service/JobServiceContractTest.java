package com.jobpulse.job_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.jobpulse.job_service.dto.*;
import com.jobpulse.job_service.model.JobPost;
import com.jobpulse.job_service.repository.JobPostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Contract tests for JobService focusing on business operations.
 */
@ExtendWith(MockitoExtension.class)
class JobServiceContractTest {

    @Mock private JobPostRepository jobPostRepository;
    private JobService jobService;

    @BeforeEach
    void setUp() {
        jobService = new JobService(jobPostRepository);
    }

    @Test
    void getJobListings_ValidPagination_ReturnsPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        JobPost jobPost = createTestJobPost();
        Page<JobPost> page = new PageImpl<>(List.of(jobPost), pageable, 1);

        when(jobPostRepository.findAll(pageable)).thenReturn(page);

        ServiceResult<JobListingsResponse> result = jobService.getJobListings(pageable);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        verify(jobPostRepository).findAll(pageable);
    }

    @Test
    void getJobListings_EmptyResults_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<JobPost> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(jobPostRepository.findAll(pageable)).thenReturn(emptyPage);

        ServiceResult<JobListingsResponse> result = jobService.getJobListings(pageable);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        verify(jobPostRepository).findAll(pageable);
    }

    @Test
    void getJobListings_RepositoryException_ReturnsFailure() {
        Pageable pageable = PageRequest.of(0, 10);
        when(jobPostRepository.findAll(pageable)).thenThrow(new RuntimeException("Database error"));

        ServiceResult<JobListingsResponse> result = jobService.getJobListings(pageable);

        assertFalse(result.isSuccess());
        assertEquals("JOB_LISTING_ERROR", result.getErrorCode());
        assertTrue(result.getErrorMessage().contains("Failed to retrieve job listings"));
    }

    @Test
    void createJob_ValidInput_ReturnsJobId() {
        CreateJobPostCommand command = new CreateJobPostCommand();
        command.setTitle("Software Engineer");
        command.setDescription("Great opportunity");
        command.setJobPosterId(UUID.randomUUID().toString());

        JobPost savedJob = createTestJobPost();
        when(jobPostRepository.save(any(JobPost.class))).thenReturn(savedJob);

        ServiceResult<CreatedResponse> result = jobService.createJob(command);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertNotNull(result.getData().getId());
        verify(jobPostRepository).save(any(JobPost.class));
    }

    @Test
    void createJob_RepositoryException_ReturnsFailure() {
        CreateJobPostCommand command = new CreateJobPostCommand();
        command.setTitle("Software Engineer");
        command.setDescription("Great opportunity");
        command.setJobPosterId(UUID.randomUUID().toString());

        when(jobPostRepository.save(any(JobPost.class))).thenThrow(new RuntimeException("Database error"));

        ServiceResult<CreatedResponse> result = jobService.createJob(command);

        assertFalse(result.isSuccess());
        assertEquals("JOB_CREATION_ERROR", result.getErrorCode());
        assertTrue(result.getErrorMessage().contains("Failed to create job"));
    }

    @Test
    void updateJob_ValidInput_ReturnsUpdatedJob() {
        UUID jobId = UUID.randomUUID();
        UpdateJobPostCommand command = new UpdateJobPostCommand(
            jobId.toString(),
            "Updated Title",
            "Updated Description",
            true
        );

        JobPost existingJob = createTestJobPost();
        existingJob.setId(jobId);
        JobPost updatedJob = createTestJobPost();
        updatedJob.setId(jobId);
        updatedJob.setTitle("Updated Title");

        when(jobPostRepository.findById(jobId)).thenReturn(Optional.of(existingJob));
        when(jobPostRepository.save(any(JobPost.class))).thenReturn(updatedJob);

        ServiceResult<JobPostUpdatedResponse> result = jobService.updateJob(command);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        verify(jobPostRepository).findById(jobId);
        verify(jobPostRepository).save(any(JobPost.class));
    }

    @Test
    void updateJob_NonExistentJob_ReturnsFailure() {
        UUID jobId = UUID.randomUUID();
        UpdateJobPostCommand command = new UpdateJobPostCommand(
            jobId.toString(),
            "Updated Title",
            "Updated Description",
            true
        );

        when(jobPostRepository.findById(jobId)).thenReturn(Optional.empty());

        ServiceResult<JobPostUpdatedResponse> result = jobService.updateJob(command);

        assertFalse(result.isSuccess());
        assertEquals("JOB_NOT_FOUND", result.getErrorCode());
        assertEquals("Job post not found", result.getErrorMessage());
        verify(jobPostRepository, never()).save(any(JobPost.class));
    }

    @Test
    void updateJob_InvalidJobId_ReturnsFailure() {
        UpdateJobPostCommand command = new UpdateJobPostCommand(
            "invalid-id",
            "Updated Title",
            "Updated Description",
            true
        );

        ServiceResult<JobPostUpdatedResponse> result = jobService.updateJob(command);

        assertFalse(result.isSuccess());
        assertEquals("INVALID_JOB_ID", result.getErrorCode());
        assertEquals("Invalid job ID format", result.getErrorMessage());
        verify(jobPostRepository, never()).save(any(JobPost.class));
    }

    @Test
    void deleteJob_ValidJobId_SoftDeletesJob() {
        UUID jobId = UUID.randomUUID();
        DeleteJobPostCommand command = new DeleteJobPostCommand(jobId.toString());

        JobPost existingJob = createTestJobPost();
        existingJob.setId(jobId);

        when(jobPostRepository.findById(jobId)).thenReturn(Optional.of(existingJob));
        when(jobPostRepository.save(any(JobPost.class))).thenReturn(existingJob);

        ServiceResult<DeletedResponse> result = jobService.deleteJob(command);

        assertTrue(result.isSuccess());
        verify(jobPostRepository).findById(jobId);
        verify(jobPostRepository).save(argThat(job -> !job.isActive()));
    }

    @Test
    void deleteJob_NonExistentJob_ReturnsFailure() {
        UUID jobId = UUID.randomUUID();
        DeleteJobPostCommand command = new DeleteJobPostCommand(jobId.toString());

        when(jobPostRepository.findById(jobId)).thenReturn(Optional.empty());

        ServiceResult<DeletedResponse> result = jobService.deleteJob(command);

        assertFalse(result.isSuccess());
        assertEquals("JOB_NOT_FOUND", result.getErrorCode());
        assertEquals("Job post not found", result.getErrorMessage());
        verify(jobPostRepository, never()).save(any(JobPost.class));
    }

    @Test
    void deleteJob_InvalidJobId_ReturnsFailure() {
        DeleteJobPostCommand command = new DeleteJobPostCommand("invalid-id");

        ServiceResult<DeletedResponse> result = jobService.deleteJob(command);

        assertFalse(result.isSuccess());
        assertEquals("INVALID_JOB_ID", result.getErrorCode());
        assertEquals("Invalid job ID format", result.getErrorMessage());
        verify(jobPostRepository, never()).save(any(JobPost.class));
    }

    @Test
    void deleteJob_RepositoryException_ReturnsFailure() {
        UUID jobId = UUID.randomUUID();
        DeleteJobPostCommand command = new DeleteJobPostCommand(jobId.toString());

        JobPost existingJob = createTestJobPost();
        existingJob.setId(jobId);

        when(jobPostRepository.findById(jobId)).thenReturn(Optional.of(existingJob));
        when(jobPostRepository.save(any(JobPost.class))).thenThrow(new RuntimeException("Database error"));

        ServiceResult<DeletedResponse> result = jobService.deleteJob(command);

        assertFalse(result.isSuccess());
        assertEquals("JOB_DELETE_ERROR", result.getErrorCode());
        assertTrue(result.getErrorMessage().contains("Failed to delete job"));
    }

    private JobPost createTestJobPost() {
        JobPost jobPost = new JobPost();
        jobPost.setId(UUID.randomUUID());
        jobPost.setTitle("Test Job");
        jobPost.setDescription("Test Description");
        jobPost.setJobPosterId(UUID.randomUUID());
        jobPost.setActive(true);
        jobPost.setCreatedAt(LocalDateTime.now());
        jobPost.setUpdatedAt(LocalDateTime.now());
        return jobPost;
    }
}
