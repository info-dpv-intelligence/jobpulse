package com.jobpulse.job_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "application_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationFiles {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "job_application_id", nullable = false)
    private UUID jobApplicationId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private ApplicationFileType fileType;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;
}