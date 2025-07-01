# JobPulse – Job Posting & Application Management Backend

## 🚀 Mission Statement
**JobPulse** is a modular, event-driven backend platform that allows companies to post job listings and manage applications, while users can discover and apply for jobs. The system supports role-based authentication, lifecycle management for job applications, real-time job alerts, and Kafka-based event processing – designed to demonstrate scalable Java backend architecture, concurrency, and production readiness.

## 📌 Problem Statement
Companies need scalable systems to handle job listings, applicant tracking, and communication workflows efficiently. JobPulse addresses this by providing a clean, backend-first platform with async notifications, lifecycle state tracking, and extensible service boundaries.

## 🧠 Tech Stack
- Java 17, Spring Boot 3
- Spring Security (JWT Auth)
- Kafka (event-based communication)
- PostgreSQL + Redis (persistence + caching)
- Docker + Docker Compose
- JUnit + Testcontainers
- GitHub Actions (CI/CD)

## 📦 Core Modules
- `auth-service`: Handles login/signup and role-based access.
- `job-service`: Manages job creation, listing, and Kafka event production.
- `application-service`: Tracks application state and publishes Kafka events.
- `alert-worker`: Listens for job events and sends simulated user alerts.