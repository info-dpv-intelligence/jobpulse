# JobPulse – Job Posting & Application Management Backend

**JobPulse** is a modular backend system built with Java and Spring Boot, designed to demonstrate core backend skills for job posting and application management. It is intended as a demonstration project to showcase usage of the Java ecosystem and modern backend architecture patterns.

## Tech Stack

- Java 17, Spring Boot 3
- Spring Security (JWT-based authentication)
- Kafka (event-driven communication)
- PostgreSQL (persistence)
- Docker, Docker Compose
- JUnit (testing)

## Architecture Overview

JobPulse is structured as a set of microservices:
- **auth-service**: Manages user registration, login, JWT issuance, and refresh token management.
- **job-service**: Handles job post creation and listing (with basic pagination).
- **application-service**: Intended to handle application management via events (early stage).
- **alert-worker**: Placeholder for consuming job/application events (logic not yet fully implemented).

All services are containerized and can be orchestrated with Docker Compose.

## Key Features

- **User Registration & Authentication**: 
  - Register and log in users, with secure password hashing.
  - Issue JWT access tokens and refresh tokens.
  - Role-based access control (roles modeled, limited enforcement).
- **Job Post Management**: 
  - Create, list, and paginate job postings.
  - (CRUD and advanced filtering/search are not fully implemented.)
- **Event-driven Design (Early Stage)**: 
  - Kafka integration is scaffolded for events (job creation, application lifecycle), with basic event publishing.
- **Basic Health Checks**: 
  - `/ping` and `/actuator/health` endpoints for all services.

## What’s Not Yet Implemented

- No production-grade error handling or input validation.
- Application management (applying to jobs, tracking application state) is not complete.
- Alerting/notification worker is a stub.
- No frontend or admin dashboard.
- Minimal API documentation and test coverage (work in progress).

## Quick Start

1. Clone the repository.
2. Ensure Docker, Docker Compose, and `make` are installed.
3. Start the services with:
   ```sh
   make ENV=dev up
   ```
4. Access API endpoints (see below).

## Example API Endpoints

- **Auth Service**
  - `POST /auth/register` – Register a new user.
  - `POST /auth/login` – Log in and receive JWT.
- **Job Service**
  - `GET /v1/jobs` – List all jobs.
  - `POST /v1/jobs` – Create a new job.

## Intended Use

This repository was created as a demonstration project to showcase Java and Spring Boot backend skills and ecosystem usage. It is not intended for production use, but as a reference for code quality, service design, and rapid prototyping.

---

*This README reflects the current state of the codebase and will be updated as more features are added.*
