package com.jobpulse.jobcreationlisting.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.UUID;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "description_company_skeleton")
public class DescriptionCompanySkeleton {
    @Id
    @GeneratedValue
    @Column(name = "description_company_skeleton_id", nullable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "job_post_description_id", referencedColumnName = "job_post_description_id", nullable = false)
    private JobPostDescription jobPostDescription;

    @OneToOne
    @JoinColumn(name = "company_details_id", referencedColumnName = "company_details_id", nullable = false)
    private CompanyDetails companyDetails;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}