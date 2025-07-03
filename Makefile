# Compose files
COMPOSE_BASE=docker-compose.yml
COMPOSE_DEV=docker-compose.dev.yml
COMPOSE_TEST=docker-compose.test.yml
COMPOSE_PROD=docker-compose.prod.yml

# Default environment
ENV ?= dev

ifeq ($(ENV),prod)
	COMPOSE_FILES=-f $(COMPOSE_BASE) -f $(COMPOSE_PROD)
else ifeq ($(ENV),test)
	COMPOSE_FILES=-f $(COMPOSE_BASE) -f $(COMPOSE_TEST)
else
	COMPOSE_FILES=-f $(COMPOSE_BASE) -f $(COMPOSE_DEV)
endif

# Targets

.PHONY: up down build logs restart clean swagger-urls api-docs

up:
	docker-compose $(COMPOSE_FILES) up -d
	@echo "Services are starting..."
	@echo "Waiting for services to be ready..."
	@sleep 30
	@make swagger-urls
down:
	docker-compose $(COMPOSE_FILES) down
build:
	docker-compose $(COMPOSE_FILES) build
logs:
	docker-compose $(COMPOSE_FILES) logs -f
restart:
	docker-compose $(COMPOSE_FILES) restart
clean:
	docker-compose $(COMPOSE_FILES) down -v --remove-orphans

# Run a specific service (e.g., make service SERVICE=auth-service)
service:
	docker-compose $(COMPOSE_FILES) up --build $(SERVICE)

# Show OpenAPI/Swagger URLs based on environment
swagger-urls:
ifeq ($(ENV),prod)
	@echo "🚀 Production OpenAPI Documentation URLs:"
	@echo "📊 Auth Service Swagger UI: http://16.171.9.26:8089/swagger-ui.html"
	@echo "📊 Job Service Swagger UI: http://16.171.9.26:8084/swagger-ui.html"
	@echo "📋 Auth Service OpenAPI JSON: http://16.171.9.26:8089/v3/api-docs"
	@echo "📋 Job Service OpenAPI JSON: http://16.171.9.26:8084/v3/api-docs"
	@echo "🔧 Kafka UI: http://16.171.9.26:8082"
	@echo "🗄️  PgAdmin: http://16.171.9.26:5050"
else
	@echo "🛠️  Development OpenAPI Documentation URLs:"
	@echo "📊 Auth Service Swagger UI: http://localhost:8080/swagger-ui.html"
	@echo "📊 Job Service Swagger UI: http://localhost:8081/swagger-ui.html"
	@echo "📋 Auth Service OpenAPI JSON: http://localhost:8080/v3/api-docs"
	@echo "📋 Job Service OpenAPI JSON: http://localhost:8081/v3/api-docs"
	@echo "🔧 Kafka UI: http://localhost:8082"
	@echo "🗄️  PgAdmin: http://localhost:5050"
endif

# Quick API documentation check
api-docs:
	@echo "🔍 Checking API Documentation Endpoints..."
ifeq ($(ENV),prod)
	@curl -s http://16.171.9.26:8089/v3/api-docs | jq '.info.title' 2>/dev/null || echo "❌ Auth Service API docs not ready"
	@curl -s http://16.171.9.26:8084/v3/api-docs | jq '.info.title' 2>/dev/null || echo "❌ Job Service API docs not ready"
else
	@curl -s http://localhost:8080/v3/api-docs | jq '.info.title' 2>/dev/null || echo "❌ Auth Service API docs not ready"
	@curl -s http://localhost:8081/v3/api-docs | jq '.info.title' 2>/dev/null || echo "❌ Job Service API docs not ready"
endif

# Health check all services
health:
	@echo "🏥 Checking Service Health..."
ifeq ($(ENV),prod)
	@curl -s http://16.171.9.26:8089/actuator/health | jq '.status' 2>/dev/null || echo "❌ Auth Service not healthy"
	@curl -s http://16.171.9.26:8084/actuator/health | jq '.status' 2>/dev/null || echo "❌ Job Service not healthy"
else
	@curl -s http://localhost:8080/actuator/health | jq '.status' 2>/dev/null || echo "❌ Auth Service not healthy"
	@curl -s http://localhost:8081/actuator/health | jq '.status' 2>/dev/null || echo "❌ Job Service not healthy"
endif

# Example: make ENV=prod up