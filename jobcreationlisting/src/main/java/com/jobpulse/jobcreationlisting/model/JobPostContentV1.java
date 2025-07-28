package com.jobpulse.jobcreationlisting.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.UUID;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "job_post_content_v1")
public class JobPostContentV1 {
    @Id
    @GeneratedValue
    @Column(name = "job_post_content_id", nullable = false)
    private UUID jobPostContentId;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToOne
    @JoinColumn(name = "company_details_id", referencedColumnName = "company_details_id", nullable = true)
    private CompanyDetails companyDetails;

    @Column(name = "revision_status", nullable = false)
    private String revisionStatus;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}