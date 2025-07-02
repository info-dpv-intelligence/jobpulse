CREATE TABLE job_post (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    job_poster_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TYPE application_status AS ENUM ('APPLIED', 'REVIEWED', 'REJECTED');

CREATE TABLE job_application (
    id UUID PRIMARY KEY,
    job_post_id UUID NOT NULL REFERENCES job_post(id),
    job_applicant_id UUID NOT NULL,
    status application_status NOT NULL,
    applied_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TYPE application_file_type AS ENUM ('RESUME', 'COVER', 'OTHER');

CREATE TABLE application_files (
    id UUID PRIMARY KEY,
    job_application_id UUID NOT NULL REFERENCES job_application(id),
    file_name VARCHAR(255) NOT NULL,
    file_type application_file_type NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW()
);