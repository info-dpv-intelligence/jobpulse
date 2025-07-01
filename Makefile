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

.PHONY: up down build logs restart clean

up:
	docker-compose $(COMPOSE_FILES) up -d
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
# Example: make ENV=prod up