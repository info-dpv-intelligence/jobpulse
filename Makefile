# JobPulse Unified Makefile
# Simplified and standardized for microservices with integrated observability

# Environment configuration
ENV ?= dev
VALID_ENVS := dev test prod

# Validate environment
ifeq ($(filter $(ENV),$(VALID_ENVS)),)
$(error Invalid environment '$(ENV)'. Valid options are: $(VALID_ENVS))
endif

# Compose file configuration
COMPOSE_BASE := docker-compose.yml
COMPOSE_ENV := docker-compose.$(ENV).yml
COMPOSE_OBS := docker-compose.observability.$(ENV).yml

# Environment-specific configuration
ifeq ($(ENV),prod)
	GRAFANA_PORT := 3002
	PROMETHEUS_PORT := 9092
	JAEGER_PORT := 16688
	GRAFANA_PASSWORD := $${GRAFANA_ADMIN_PASSWORD}
	BASE_URL := 16.171.9.26
else ifeq ($(ENV),test)
	GRAFANA_PORT := 3001
	PROMETHEUS_PORT := 9091
	JAEGER_PORT := 16687
	GRAFANA_PASSWORD := jobpulse-test-456
	BASE_URL := localhost
else
	GRAFANA_PORT := 3000
	PROMETHEUS_PORT := 9090
	JAEGER_PORT := 16686
	GRAFANA_PASSWORD := jobpulse-dev-123
	BASE_URL := localhost
endif

# Service ports (environment-aware)
ifeq ($(ENV),prod)
	AUTH_PORT := 8089
	JOB_PORT := 8084
else
	AUTH_PORT := 8080
	JOB_PORT := 8081
endif

# Colors for output
GREEN := \033[0;32m
YELLOW := \033[1;33m
BLUE := \033[0;34m
CYAN := \033[0;36m
NC := \033[0m

.PHONY: help up down build logs logs-observability restart restart-observability clean health observability full-up full-down status urls api-docs test config

# Default target
help:
	@echo "$(BLUE)JobPulse Makefile - Environment: $(CYAN)$(ENV)$(NC)"
	@echo ""
	@echo "$(YELLOW)Primary Commands:$(NC)"
	@echo "  make full-up        - Start complete environment (services + observability)"
	@echo "  make full-down      - Stop complete environment"
	@echo "  make up             - Start application services only"
	@echo "  make down           - Stop application services only"
	@echo "  make observability  - Start observability stack only"
	@echo ""
	@echo "$(YELLOW)Utility Commands:$(NC)"
	@echo "  make build          - Build all images"
	@echo "  make restart        - Restart services"
	@echo "  make logs           - Show service logs"
	@echo "  make clean          - Clean up containers and volumes"
	@echo "  make health         - Check service health"
	@echo "  make status         - Show environment status"
	@echo "  make urls           - Show important URLs"
	@echo "  make api-docs       - Validate API documentation"
	@echo "  make test           - Run observability validation tests"
	@echo "  make config         - Show current compose file configuration"
	@echo "  make logs-observability - Show observability service logs"
	@echo ""
	@echo "$(YELLOW)Environment Selection:$(NC)"
	@echo "  make ENV=dev [command]   - Development environment (default)"
	@echo "  make ENV=test [command]  - Test environment"
	@echo "  make ENV=prod [command]  - Production environment"
	@echo ""
	@echo "$(YELLOW)Current Configuration:$(NC)"
	@echo "  Environment: $(CYAN)$(ENV)$(NC)"
	@echo "  Grafana:     http://$(BASE_URL):$(GRAFANA_PORT)"
	@echo "  Prometheus:  http://$(BASE_URL):$(PROMETHEUS_PORT)"
	@echo "  Jaeger:      http://$(BASE_URL):$(JAEGER_PORT)"

# Application services
up:
	@echo "$(BLUE)🚀 Starting $(ENV) application services...$(NC)"
	@docker-compose -f $(COMPOSE_BASE) -f $(COMPOSE_ENV) up -d
	@echo "$(GREEN)✅ Application services started$(NC)"

down:
	@echo "$(BLUE)🛑 Stopping $(ENV) application services...$(NC)"
	@docker-compose -f $(COMPOSE_BASE) -f $(COMPOSE_ENV) down
	@echo "$(GREEN)✅ Application services stopped$(NC)"

# Observability stack
observability:
	@echo "$(BLUE)📊 Starting $(ENV) observability stack...$(NC)"
	@docker-compose -f $(COMPOSE_OBS) up -d
	@echo "$(YELLOW)⏳ Waiting for services to initialize...$(NC)"
	@sleep 15
	@echo "$(GREEN)✅ Observability stack started$(NC)"

observability-down:
	@echo "$(BLUE)🛑 Stopping $(ENV) observability stack...$(NC)"
	@docker-compose -f $(COMPOSE_OBS) down
	@echo "$(GREEN)✅ Observability stack stopped$(NC)"

# Combined operations
full-up: observability
	@sleep 10
	@make up ENV=$(ENV)
	@echo "$(GREEN)🎉 Complete $(ENV) environment is ready!$(NC)"
	@make urls ENV=$(ENV)

full-down:
	@make down ENV=$(ENV)
	@make observability-down ENV=$(ENV)
	@echo "$(GREEN)✅ Complete $(ENV) environment stopped$(NC)"

# Utility commands
build:
	@echo "$(BLUE)🔨 Building $(ENV) images...$(NC)"
	@docker-compose -f $(COMPOSE_BASE) -f $(COMPOSE_ENV) build

restart:
	@echo "$(BLUE)🔄 Restarting $(ENV) services...$(NC)"
	@docker-compose -f $(COMPOSE_BASE) -f $(COMPOSE_ENV) restart

restart-observability:
	@echo "$(BLUE)🔄 Restarting $(ENV) observability services...$(NC)"
	@docker-compose -f $(COMPOSE_OBS) restart
	@sleep 10
	@echo "$(GREEN)✅ Observability services restarted$(NC)"

logs:
	@echo "$(BLUE)📋 Showing $(ENV) service logs...$(NC)"
	@docker-compose -f $(COMPOSE_BASE) -f $(COMPOSE_ENV) logs -f

logs-observability:
	@echo "$(BLUE)📋 Showing $(ENV) observability logs...$(NC)"
	@docker-compose -f $(COMPOSE_OBS) logs -f

clean:
	@echo "$(BLUE)🧹 Cleaning $(ENV) environment...$(NC)"
	@docker-compose -f $(COMPOSE_BASE) -f $(COMPOSE_ENV) down -v --remove-orphans
	@docker-compose -f $(COMPOSE_OBS) down -v --remove-orphans 2>/dev/null || true
	@echo "$(GREEN)✅ Environment cleaned$(NC)"

# Status and health checks
status:
	@echo "$(BLUE)📊 $(ENV) Environment Status:$(NC)"
	@echo ""
	@echo "$(YELLOW)Application Services:$(NC)"
	@docker-compose -f $(COMPOSE_BASE) -f $(COMPOSE_ENV) ps --format table 2>/dev/null || echo "Not running"
	@echo ""
	@echo "$(YELLOW)Observability Services:$(NC)"
	@docker-compose -f $(COMPOSE_OBS) ps --format table 2>/dev/null || echo "Not running"

health:
	@echo "$(BLUE)🏥 Checking $(ENV) service health...$(NC)"
	@curl -f -s http://$(BASE_URL):$(AUTH_PORT)/actuator/health | jq '.status' 2>/dev/null \
		&& echo "$(GREEN)✅ Auth Service healthy$(NC)" \
		|| echo "$(YELLOW)⚠️  Auth Service not ready$(NC)"
	@curl -f -s http://$(BASE_URL):$(JOB_PORT)/actuator/health | jq '.status' 2>/dev/null \
		&& echo "$(GREEN)✅ Job Service healthy$(NC)" \
		|| echo "$(YELLOW)⚠️  Job Service not ready$(NC)"

urls:
	@echo "$(BLUE)🔗 $(ENV) Environment URLs:$(NC)"
	@echo ""
	@echo "$(YELLOW)🔍 Observability:$(NC)"
	@echo "  📊 Grafana:     http://$(BASE_URL):$(GRAFANA_PORT) (admin/$(GRAFANA_PASSWORD))"
	@echo "      - Overview Dashboard: /d/jobpulse-overview"
	@echo "      - Auth Service: /d/auth-service-dashboard"  
	@echo "      - Job Service: /d/job-service-dashboard"
	@echo "      - Infrastructure: /d/infrastructure-dashboard"
	@echo "  📈 Prometheus:  http://$(BASE_URL):$(PROMETHEUS_PORT)"
	@echo "  🔍 Jaeger:      http://$(BASE_URL):$(JAEGER_PORT)"
	@echo ""
	@echo "$(YELLOW)📋 API Documentation:$(NC)"
	@echo "  🔐 Auth Service: http://$(BASE_URL):$(AUTH_PORT)/swagger-ui.html"
	@echo "  💼 Job Service:  http://$(BASE_URL):$(JOB_PORT)/swagger-ui.html"
	@echo ""
	@echo "$(YELLOW)🛠️  Development Tools:$(NC)"
	@echo "  🔧 Kafka UI:    http://$(BASE_URL):8082"
	@echo "  🗄️  PgAdmin:     http://$(BASE_URL):5050"

api-docs:
	@echo "$(BLUE)🔍 Validating $(ENV) API documentation...$(NC)"
	@curl -f -s http://$(BASE_URL):$(AUTH_PORT)/v3/api-docs | jq '.info.title' 2>/dev/null \
		&& echo "$(GREEN)✅ Auth Service API docs available$(NC)" \
		|| echo "$(YELLOW)⚠️  Auth Service API docs not ready$(NC)"
	@curl -f -s http://$(BASE_URL):$(JOB_PORT)/v3/api-docs | jq '.info.title' 2>/dev/null \
		&& echo "$(GREEN)✅ Job Service API docs available$(NC)" \
		|| echo "$(YELLOW)⚠️  Job Service API docs not ready$(NC)"

test:
	@echo "$(BLUE)🧪 Running $(ENV) observability validation...$(NC)"
	@if [ -f ./scripts/validate-observability.sh ]; then \
		chmod +x ./scripts/validate-observability.sh; \
		./scripts/validate-observability.sh $(ENV); \
	else \
		echo "$(YELLOW)⚠️  Validation script not found, skipping tests$(NC)"; \
	fi

config:
	@echo "$(BLUE)⚙️  $(ENV) Environment Configuration:$(NC)"
	@echo ""
	@echo "$(YELLOW)Compose Files:$(NC)"
	@echo "  Base:          $(COMPOSE_BASE)"
	@echo "  Environment:   $(COMPOSE_ENV)"
	@echo "  Observability: $(COMPOSE_OBS)"
	@echo ""
	@echo "$(YELLOW)Port Configuration:$(NC)"
	@echo "  Grafana:       $(GRAFANA_PORT)"
	@echo "  Prometheus:    $(PROMETHEUS_PORT)"
	@echo "  Jaeger:        $(JAEGER_PORT)"
	@echo "  Auth Service:  $(AUTH_PORT)"
	@echo "  Job Service:   $(JOB_PORT)"

# Legacy targets for backward compatibility
dev-full: ENV=dev
dev-full: full-up

test-full: ENV=test
test-full: full-up

prod-deploy: ENV=prod
prod-deploy: full-up