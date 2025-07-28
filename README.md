# JobPulse – Job Management Backend

A microservices-based job posting and application management system built with Java 17 and Spring Boot 3. Demonstrates modern backend architecture, event-driven design, and observability patterns.

## 🚀 Tech Stack

**Backend:** Java 17, Spring Boot 3, Spring Security (JWT)  
**Messaging:** Apache Kafka  
**Database:** PostgreSQL  
**Observability:** Micrometer, Prometheus, Grafana, Jaeger  
**Infrastructure:** Docker, Docker Compose  
**Testing:** JUnit 5  

## Architecture

**Microservices:**
- `auth-service`:
   _Handles user registration, login, and JWT token issuance with embedded roles. Each microservice enforces role-based access control by validating roles from the token._
- `job-service`:
   - **`job-creation-listing`**:
      _Handles job listings, job creation. Responsible for exposing job opportunities to users._
   - **`job-poster`**
      _Manages job posts from the perspective of the poster. Enables reviewing, updating, and managing applications submitted for owned job posts._
   - **`job-seeker`**
      _Manages job applications (includes updating submitted documents, status)._
- `alert-worker`:
   _Handles alerts and notifications (pending)_

## Architecture Documentation
**ERD:**
- [Job Post](docs/job-service/database-design/job_post.puml)
- [Job Application](docs/job-service/database-design/job_application.puml)

**Service-Specific Architecture:**
- [Auth Service Architecture](docs/auth-service/auth-service.puml) - Service structure and components
- Job Service:
   - [Job Creation and Listing](docs/job-service/job-creation-listing/design.puml) - Job post creation and listing
   - [Job Poster](docs/job-service/job-poster/design.puml) - Manage job posts and review applications
   - [Job Seeker](docs/job-service/job-seeker/design.puml) - Manage applications

## Features
   - **auth-service**: (Implemented: ✅)
     - User registration and login with JWT tokens
     - Role-based access control (RBAC)
   - **job-service**: (In progress 🚧)
      - **job-creation-listing**:
         - Retrieve available job postings. (Issue link: https://github.com/info-dpv-intelligence/jobpulse/issues/9)
         - Create a new job post in the system. (Implemented: ✅)
      - **job-poster**:
         - List all job posts created by the current poster.
         — Modify the status or visibility of a job post.
         - Retrieve all applications submitted for a specific job post.
      - **job-seeker**:
         - List all applied job applicants.
         - Submit a job application.
         - Edit or update documents (resume, cover letter) attached to an application.
         - Change the status of an application (withdraw, update progress, etc.).

**Observability & Monitoring:**
- Grafana dashboards for visualization
- Jaeger distributed tracing (_configuration review pending ⏳_)
- Health checks (`/actuator/health`) 

**API Documentation:**
- OpenAPI/Swagger

## 🔧 Quick Start

### Environment Setup
1. **Copy environment template:**
   ```bash
   cp .env.example .env
   ```

2. **Configure credentials in `.env`:**
   ```bash
   # Required: Set your Grafana password
   GRAFANA_PASSWORD=your_secure_password_here
   
   # Optional: Override default URLs if needed
   # GRAFANA_URL=http://localhost:3000
   ```

3. **Start services:**
   ```bash
   # Start all services
   make ENV=dev up

   # Start observability stack
   ./start-observability.sh
   ```

## 📊 Monitoring & Observability

**Metrics Collection:**
- Business metrics: *(planned for future implementation)*

**Monitoring Stack:**
- **Prometheus:** [http://localhost:9090](http://localhost:9090) - Metrics collection
- **Grafana:** [http://localhost:3000](http://localhost:3000) - Dashboard
- **Jaeger:** [http://localhost:16686](http://localhost:16686) - Distributed tracing (__configuration pending_)
