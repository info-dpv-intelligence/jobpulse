# JobPulse – Job Management Backend

A microservices-based job posting and application management system built with Java 17 and Spring Boot 3. Demonstrates modern backend architecture, event-driven design, and observability patterns.

## 🚀 Tech Stack

**Backend:** Java 17, Spring Boot 3, Spring Security (JWT)  
**Messaging:** Apache Kafka  
**Database:** PostgreSQL  
**Observability:** Micrometer, Prometheus, Grafana, Jaeger  
**Infrastructure:** Docker, Docker Compose  
**Testing:** JUnit 5  

## 🏗️ Architecture

**Microservices:**
- `auth-service` (8080) - JWT authentication & user management
- `job-service` (8081) - Boilerplate only; no architectural patterns or business logic applied yet. Contains a basic pagination request. Needs architecture review and implementation.
- `application-service` (8082) - Application management *(API in progress)*
- `alert-worker` (8083) - Event processing *(implementation pending)*

**Infrastructure:**
- PostgreSQL databases per service
- Kafka for async event communication
- Full observability stack (Prometheus, Grafana, Jaeger)
- Comprehensive metrics collection with custom business metrics

## 📐 Architecture Documentation

Detailed architecture diagrams and design documents are available in the `docs/` folder:

**Service-Specific Architecture:**
- [Auth Service Architecture](docs/auth-service/auth-service.puml) - Service structure and components
- [Registration & Login Flow](docs/auth-service/registration-login.puml) - Authentication workflow diagrams
- [Job Service Design](docs/job-service/design.puml) - Job management service architecture


## ✅ Implemented Features

**Core Functionality:**
- User registration/login with JWT tokens
- Job posting creation and listing with pagination *(job-service endpoints are placeholders; implementation pending)*
- Role-based access control

**Event-Driven Architecture:**
- Kafka integration for service communication
- Event publishing for job lifecycle

**Observability & Monitoring:**
- Prometheus metrics collection with custom business metrics
- Grafana dashboards for visualization
- Jaeger distributed tracing
- Health checks (`/actuator/health`) 
- Real-time monitoring scripts
- Custom endpoint metrics with AOP

**API Documentation:**
- OpenAPI/Swagger for auth-service
- Comprehensive endpoint documentation

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

   # Health check
   curl http://localhost:8081/actuator/health
   ```

## 📋 API Endpoints

**Auth Service (8080):**
```
POST /api/auth/register  # User registration
POST /api/auth/login     # Login with JWT response
GET  /actuator/prometheus # Prometheus metrics
```

**Job Service (8081):**
```
GET  /api/v1/jobs        # List jobs (paginated)
GET  /actuator/prometheus # Prometheus metrics
```

**API Documentation:** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

## 📊 Monitoring & Observability

**Metrics Collection:**
- Business metrics: *(planned for future implementation)*

**Monitoring Stack:**
- **Prometheus:** [http://localhost:9090](http://localhost:9090) - Metrics collection
- **Grafana:** [http://localhost:3000](http://localhost:3000) - Dashboards (admin/your_password)
- **Jaeger:** [http://localhost:16686](http://localhost:16686) - Distributed tracing

**Real-time Monitoring:**
```bash
# Live metrics dashboard
./scripts/monitor.sh

# Validate observability setup
./scripts/validate-observability.sh

# Generate load for testing
./scripts/load-test.sh
```

## 🚧 To Be Implemented

- [ ] **Job Service Review:** The current job-service is a boilerplate; no architectural patterns or business logic have been applied yet. A design review and proper implementation are required.
- [ ] Job Create/Get Endpoints: The job creation and retrieval endpoints are placeholders and need to be implemented according to the decided design and best practices.
- [ ] Job update/delete operations
- [ ] Advanced job search and filtering
- [ ] Application submission API (application-service)
- [ ] Application state tracking and file uploads
- [ ] Alert worker event processing
- [ ] Enhanced input validation and error handling
- [ ] Comprehensive integration test suite


*Note: This is a demonstration project.*
*Note: Only the auth-service is suitable for demo purposes. The job-service is a boilerplate and not production-ready.*
