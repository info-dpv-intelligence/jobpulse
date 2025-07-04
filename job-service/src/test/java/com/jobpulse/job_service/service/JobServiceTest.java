package com.jobpulse.job_service.service;

import com.jobpulse.job_service.dto.*;
import com.jobpulse.job_service.model.JobPost;
import com.jobpulse.job_service.repository.JobPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class JobServiceTest {

    @Mock
    private JobPostRepository jobPostRepository;

    @InjectMocks
    private JobService jobService;

    private CreateJobPostCommand validCreateCommand;
    private JobPost mockJobPost;
    private Pageable mockPageable;

    @BeforeEach
    void setUp() {
        validCreateCommand = new CreateJobPostCommand();
        validCreateCommand.setTitle("Software Engineer");
        validCreateCommand.setDescription("Exciting opportunity for a software engineer");
        validCreateCommand.setJobPosterId(UUID.randomUUID().toString());

        mockJobPost = new JobPost();
        mockJobPost.setId(UUID.randomUUID());
        mockJobPost.setTitle("Software Engineer");
        mockJobPost.setDescription("Exciting opportunity for a software engineer");
        mockJobPost.setJobPosterId(UUID.randomUUID());
        mockJobPost.setCreatedAt(LocalDateTime.now());
        mockJobPost.setUpdatedAt(LocalDateTime.now());
        mockJobPost.setActive(true);

        mockPageable = PageRequest.of(0, 10);
    }

    @Test
    void getJobListings_WithValidPageable_ShouldReturnSuccess() {
        // Arrange
        List<JobPost> jobPosts = List.of(mockJobPost);
        Page<JobPost> jobPage = new PageImpl<>(jobPosts, mockPageable, 1);
        when(jobPostRepository.findAll(mockPageable)).thenReturn(jobPage);

        // Act
        ServiceResult<JobListingsResponse> result = jobService.getJobListings(mockPageable);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getTotalElements());
        assertEquals(1, result.getData().getTotalPages());
        assertEquals(0, result.getData().getCurrentPage());
        assertEquals(10, result.getData().getPageSize());
        assertNotNull(result.getData().getJobs());
        verify(jobPostRepository).findAll(mockPageable);
    }

    @Test
    void getJobListings_WithRepositoryException_ShouldReturnFailure() {
        // Arrange
        when(jobPostRepository.findAll(mockPageable)).thenThrow(new RuntimeException("Database error"));

        // Act
        ServiceResult<JobListingsResponse> result = jobService.getJobListings(mockPageable);

        // Assert
        assertTrue(result.isFailure());
        assertTrue(result.getErrorMessage().contains("Failed to retrieve job listings"));
        assertEquals("JOB_LISTING_ERROR", result.getErrorCode());
    }

    @Test
    void createJob_WithValidCommand_ShouldReturnSuccess() {
        // Arrange
        when(jobPostRepository.save(any(JobPost.class))).thenReturn(mockJobPost);

        // Act
        ServiceResult<CreatedResponse> result = jobService.createJob(validCreateCommand);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(mockJobPost.getId().toString(), result.getData().getId());
        verify(jobPostRepository).save(any(JobPost.class));
    }

    @Test
    void createJob_WithRepositoryException_ShouldReturnFailure() {
        // Arrange
        when(jobPostRepository.save(any(JobPost.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ServiceResult<CreatedResponse> result = jobService.createJob(validCreateCommand);

        // Assert
        assertTrue(result.isFailure());
        assertTrue(result.getErrorMessage().contains("Failed to create job"));
        assertEquals("JOB_CREATION_ERROR", result.getErrorCode());
    }

    @Test
    void createJob_ShouldSetCorrectJobPostProperties() {
        // Arrange
        when(jobPostRepository.save(any(JobPost.class))).thenAnswer(invocation -> {
            JobPost savedJobPost = invocation.getArgument(0);
            savedJobPost.setId(UUID.randomUUID());
            return savedJobPost;
        });

        // Act
        ServiceResult<CreatedResponse> result = jobService.createJob(validCreateCommand);

        // Assert
        assertTrue(result.isSuccess());
        verify(jobPostRepository).save(argThat(jobPost -> 
            jobPost.getTitle().equals(validCreateCommand.getTitle()) &&
            jobPost.getDescription().equals(validCreateCommand.getDescription()) &&
            jobPost.getJobPosterId().toString().equals(validCreateCommand.getJobPosterId()) &&
            jobPost.isActive() &&
            jobPost.getCreatedAt() != null &&
            jobPost.getUpdatedAt() != null
        ));
    }
}
