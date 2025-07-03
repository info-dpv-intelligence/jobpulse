# JobPulse – Job Posting & Application Management Backend

JobPulse is a modular backend system built with Java and Spring Boot, designed as a demonstration of core backend skills for job posting and application management. It showcases modern backend architecture patterns and Java ecosystem usage.

---

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Security (JWT-based authentication)
- Kafka (event-driven communication)
- PostgreSQL (persistence)
- Docker, Docker Compose
- JUnit (testing)

---

## Architecture Overview

JobPulse is structured as a set of microservices:

- **auth-service:** Manages user registration, login, JWT issuance, and refresh token management.
- **job-service:** Handles job post creation and listing (with basic pagination). OpenAPI spec available (see below).
- **application-service:** Intended to handle application management via events (early stage; API not yet exposed).
- **alert-worker:** Placeholder for consuming job/application events (logic not yet implemented).

All services are containerized and orchestrated with Docker Compose.

---

## Key Features

### User Registration & Authentication

- Register and log in users, with secure password hashing.
- Issue JWT access and refresh tokens.
- Role-based access control (roles modeled, limited enforcement).

### Job Post Management

- Create, list, and paginate job postings.
- OpenAPI spec for job-service available (see below).
- **Note:** CRUD (update, delete) and advanced filtering/search are not yet implemented.

### Event-driven Design (Early Stage)

- Kafka integration scaffolded for events (job creation, application lifecycle).
- Basic event publishing implemented.
- Alerting/notification worker is a stub (no real event processing yet).

### Health Checks

- `/ping` and `/actuator/health` endpoints for all services.

---

## What’s Not Yet Implemented

- No production-grade error handling or input validation (basic error handling only).
- Application management (applying to jobs, tracking application state) is not complete; no public API yet.
- Alerting/notification worker is a placeholder.
- No frontend or admin dashboard.
- Minimal API documentation and test coverage (work in progress).

---

## Quick Start

1. **Clone the repository.**
2. Ensure Docker, Docker Compose, and `make` are installed.
3. Start the services with:
   ```sh
   make ENV=dev up
   ```
4. Access API endpoints (see below).

---

## Example API Endpoints

### Auth Service

- `POST /auth/register` – Register a new user.
- `POST /auth/login` – Log in and receive JWT.

### Job Service

- `GET /v1/jobs` – List all jobs (paged).
- `POST /v1/jobs` – Create a new job.

### OpenAPI Documentation

- **Job Service OpenAPI Spec:**  
  Once job-service is running, visit:  
  [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)  
  or  
  [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

---

## Intended Use

This repository was created as a demonstration project to showcase Java and Spring Boot backend skills and ecosystem usage. It is not intended for production use, but as a reference for code quality, service design, and rapid prototyping.

---

## Planned Features

- Full CRUD support for job postings (update, delete)
- Advanced filtering and search for job listings
- Public API for job application submission and state tracking
- Application file upload/download endpoints
- Alert/notification worker for event-driven notifications
- Improved error handling and input validation
- Expanded test coverage (unit and integration)
- API documentation via Swagger/OpenAPI UI for all services

---
