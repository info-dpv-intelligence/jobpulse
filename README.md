# JobPulse – Job Posting & Application Management Backend

## 🎯 Live Demo
**JobPulse** is currently hosted on AWS EC2 for demonstration purposes. This live deployment showcases a production-ready distributed system built with Java and Spring Boot.

### 🌐 Live Services
- **Auth Service**: [http://16.171.9.26:8089](http://16.171.9.26:8089)
- **Job Service**: [http://16.171.9.26:8084](http://16.171.9.26:8084)
- **Kafka UI**: [http://16.171.9.26:8082](http://16.171.9.26:8082)
- **PgAdmin**: [http://16.171.9.26:5050](http://16.171.9.26:5050)

### 🔗 Quick API Tests
```bash
# Test Registration
curl -X POST http://16.171.9.26:8089/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "demo", "email": "demo@example.com", "password": "password123"}'

# Test Authentication
curl -X POST http://16.171.9.26:8089/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "demo", "password": "password123"}'

# Health Checks
curl http://16.171.9.26:8089/actuator/health
curl http://16.171.9.26:8084/actuator/health
```

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

## 🛠️ Local Development Setup

### Quick Start
1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd jobpulse
   ```

2. **Set up environment variables**
   ```bash
   cp .env.template .env
   # Edit .env with your preferred values
   ```

3. **Start the development environment**
   ```bash
   # Using Make (recommended)
   make ENV=dev up
   
   # Or using Docker Compose directly
   docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
   ```

4. **Verify services are running**
   ```bash
   # Check container status
   docker ps
   
   # Test health endpoints
   curl http://localhost:8080/actuator/health  # Auth Service
   curl http://localhost:8081/actuator/health  # Job Service
   ```

### Development URLs
- **Auth Service**: [http://localhost:8080](http://localhost:8080)
- **Job Service**: [http://localhost:8081](http://localhost:8081)
- **Kafka UI**: [http://localhost:8082](http://localhost:8082)
- **PgAdmin**: [http://localhost:5050](http://localhost:5050)
- **PostgreSQL** (Auth): `localhost:5435`
- **PostgreSQL** (Job): `localhost:5436`
- **Kafka**: `localhost:9092`
- **Zookeeper**: `localhost:2181`
