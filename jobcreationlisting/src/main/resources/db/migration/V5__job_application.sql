DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'application_status') THEN
        CREATE TYPE application_status AS ENUM ('APPLIED', 'IN_REVIEW', 'REJECTED', 'SHORTLISTED', 'ONBOARDED');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'application_document_type') THEN
        CREATE TYPE application_document_type AS ENUM ('RESUME', 'COVER', 'OTHER');
    END IF;
END
$$;


CREATE TABLE IF NOT EXISTS job_application (
    job_application_id UUID PRIMARY KEY,
    job_post_id UUID NOT NULL,
    job_applicant_id UUID NOT NULL,
    status application_status NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS job_application_file_mapping (
    job_application_document_id UUID PRIMARY KEY,
    job_post_id UUID NOT NULL,
    job_application_file_id UUID NOT NULL,
    job_application_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_job_application_file_mapping_job_post_id_job_application_id
    ON job_application_file_mapping (job_post_id, job_application_id);

CREATE INDEX IF NOT EXISTS idx_job_application_file_mapping_created_at
    ON job_application_file_mapping (created_at);

CREATE TABLE IF NOT EXISTS job_application_file (
    job_application_file_id UUID PRIMARY KEY,
    file_url TEXT NOT NULL,
    document_type application_document_type NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);