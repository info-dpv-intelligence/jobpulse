DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'job_post_status') THEN
        CREATE TYPE job_post_status AS ENUM ('DRAFT', 'ACTIVE');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'revision_status') THEN
        CREATE TYPE revision_status AS ENUM ('DRAFT', 'ACTIVE');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'skeleton_type') THEN
        CREATE TYPE skeleton_type AS ENUM (
            'DESCRIPTION_ONLY_SKELETON',
            'DESCRIPTION_COMPANY_SKELETON',
            'DESCRIPTION_PREREQUISITES_SKELETON',
            'DESCRIPTION_COMPANY_PREREQUISITES_SKELETON'
        );
    END IF;
END
$$;

CREATE TABLE IF NOT EXISTS job_post (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    job_poster_id UUID NOT NULL,
    job_post_content_id UUID NOT NULL,
    status job_post_status NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_post_content_id) REFERENCES job_post_content(job_post_content_id)
);

CREATE TABLE IF NOT EXISTS job_post_content (
    job_post_content_id UUID PRIMARY KEY,
    job_post_skeleton_id UUID NOT NULL,
    revision_status revision_status NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_post_skeleton_id) REFERENCES job_post_skeleton(job_post_skeleton_id)
);

CREATE TABLE IF NOT EXISTS job_post_skeleton (
    job_post_skeleton_id UUID PRIMARY KEY,
    skeleton_type skeleton_type NOT NULL,
    status revision_status NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS description_only_skeleton (
    description_only_skeleton_id UUID PRIMARY KEY,
    job_post_description_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_post_description_id) REFERENCES job_post_description(job_post_description_id)
);

CREATE TABLE IF NOT EXISTS description_company_skeleton (
    description_company_skeleton_id UUID PRIMARY KEY,
    job_post_description_id UUID NOT NULL,
    company_details_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_post_description_id) REFERENCES job_post_description(job_post_description_id),
    FOREIGN KEY (company_details_id) REFERENCES company_details(company_details_id)
);

CREATE TABLE IF NOT EXISTS description_prerequisites_skeleton (
    description_prerequisites_skeleton_id UUID PRIMARY KEY,
    job_post_description_id UUID NOT NULL,
    job_post_prerequisites_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_post_description_id) REFERENCES job_post_description(job_post_description_id),
    FOREIGN KEY (job_post_prerequisites_id) REFERENCES job_post_prerequisites(job_post_prerequisites_id)
);


CREATE TABLE IF NOT EXISTS description_company_prerequisites_skeleton (
    description_company_prerequisites_skeleton_id UUID PRIMARY KEY,
    job_post_description_id UUID NOT NULL,
    company_details_id UUID NOT NULL,
    job_post_prerequisites_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_post_description_id) REFERENCES job_post_description(job_post_description_id),
    FOREIGN KEY (company_details_id) REFERENCES company_details(company_details_id),
    FOREIGN KEY (job_post_prerequisites_id) REFERENCES job_post_prerequisites(job_post_prerequisites_id)
);

CREATE TABLE IF NOT EXISTS job_post_description (
    job_post_description_id UUID PRIMARY KEY,
    description TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS company_details (
    company_details_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tagline VARCHAR(255),
    phone VARCHAR(32),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS job_post_prerequisites (
    job_post_prerequisites_id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);