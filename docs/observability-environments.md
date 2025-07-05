# JobPulse Observability Environment Separation Guide

This guide explains how observability is configured and separated across different environments in the JobPulse platform.

## Environment Overview

JobPulse observability supports three distinct environments, each with specific configurations:

| Environment | Purpose | Monitoring Level | Data Retention | Ports |
|-------------|---------|------------------|----------------|-------|
| **Development** | Local development and debugging | Verbose | 7 days | 9090, 3000, 16686 |
| **Test** | Automated testing and CI/CD | Balanced | 3 days | 9091, 3001, 16687 |
| **Production** | Live production monitoring | Optimized | 30 days | 9092, 3002, 16688 |

## Quick Start by Environment

### Development Environment
```bash
# Start development observability
make observability-dev

# Start full dev environment (services + observability)
make dev-full

# Set application observability level
export OBSERVABILITY_ENV=dev
```

**Access URLs:**
- Grafana: http://localhost:3000 (admin/jobpulse-dev-123)
- Prometheus: http://localhost:9090
- Jaeger: http://localhost:16686

### Test Environment
```bash
# Start test observability
make observability-test

# Start full test environment
make test-full

# Set application observability level
export OBSERVABILITY_ENV=test
```

**Access URLs:**
- Grafana: http://localhost:3001 (admin/jobpulse-test-456)
- Prometheus: http://localhost:9091
- Jaeger: http://localhost:16687

### Production Environment
```bash
# Start production observability (requires env vars)
make observability-prod

# Set application observability level
export OBSERVABILITY_ENV=prod
```

**Access URLs:**
- Grafana: http://localhost:3002 (admin/[configured-password])
- Prometheus: http://localhost:9092
- Jaeger: http://localhost:16688

## Environment-Specific Configurations

### Development Configuration

**Characteristics:**
- Full metrics exposure for debugging
- Detailed logging and tracing
- All actuator endpoints enabled
- High-frequency scraping (5s intervals)
- Verbose debug information

**Key Settings:**
```properties
# Enhanced observability for development
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
logging.level.com.jobpulse=DEBUG
management.tracing.sampling.probability=1.0
```

### Test Configuration

**Characteristics:**
- Balanced metrics collection
- Test-specific health checks
- Automated testing support
- Moderate scraping intervals (10s)
- Load generation included

**Key Settings:**
```properties
# Test-specific observability settings
management.endpoints.web.exposure.include=health,metrics,prometheus,info
management.endpoint.health.show-details=when-authorized
management.tracing.sampling.probability=0.5
```

### Production Configuration

**Characteristics:**
- Security-hardened endpoints
- Optimized performance settings
- Extended data retention
- Lower sampling rates
- External monitoring support

**Key Settings:**
```properties
# Production-hardened observability settings
management.endpoints.web.exposure.include=health,metrics,prometheus,info
management.endpoint.health.show-details=never
management.server.port=9090
management.tracing.sampling.probability=0.1
```

## Environment Variables

### Required Environment Variables

| Variable | Description | Dev | Test | Prod |
|----------|-------------|-----|------|------|
| `OBSERVABILITY_ENV` | Selects observability config | `dev` | `test` | `prod` |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `dev` | `test` | `prod` |
| `ENV` | Deployment environment | `dev` | `test` | `prod` |

### Production-Specific Variables

```bash
# Required for production
export GRAFANA_ADMIN_PASSWORD=secure-prod-password
export POSTGRES_EXPORTER_DSN=postgresql://user:pass@host:5432/db
export KAFKA_BROKERS=kafka1:9092,kafka2:9092
export ELASTICSEARCH_URL=http://elasticsearch:9200
export AWS_REGION=eu-west-1
```

## Directory Structure

```
observability/
├── environments/
│   ├── dev/
│   │   ├── prometheus.yml              # Dev Prometheus config
│   │   ├── application-observability.properties
│   │   └── dashboards/                 # Dev-specific dashboards
│   ├── test/
│   │   ├── prometheus.yml              # Test Prometheus config
│   │   ├── application-observability.properties
│   │   └── dashboards/                 # Test-specific dashboards
│   └── prod/
│       ├── prometheus.yml              # Production Prometheus config
│       ├── application-observability.properties
│       ├── blackbox.yml               # External monitoring
│       └── dashboards/                # Production dashboards
├── grafana/
│   ├── dashboards/                     # Shared dashboards
│   └── datasources/                    # Shared data sources
└── alerts/                             # Environment-specific alerts
```

## Service Configuration Loading

Services automatically load environment-specific configurations:

```properties
# In application.properties
spring.config.import=optional:classpath:observability/environments/${OBSERVABILITY_ENV:dev}/application-observability.properties
```

This allows runtime selection of observability configuration without code changes.

## Network Isolation

Each environment uses separate Docker networks:

- **Development**: `jobpulse-observability-dev`
- **Test**: `jobpulse-observability-test`
- **Production**: `jobpulse-observability-prod`

## Security Considerations

### Development
- All endpoints exposed for debugging
- Default passwords acceptable
- Debug logging enabled

### Test
- Limited endpoint exposure
- Automated testing credentials
- Moderate logging levels

### Production
- Minimal endpoint exposure
- Strong authentication required
- Audit logging only
- Network security required
- SSL/TLS mandatory

## Metrics Collection Differences

### Development
```yaml
scrape_interval: 5s
evaluation_interval: 5s
# High-frequency collection for real-time development feedback
```

### Test
```yaml
scrape_interval: 10s
evaluation_interval: 10s
# Balanced collection for testing scenarios
```

### Production
```yaml
scrape_interval: 30s
evaluation_interval: 30s
# Optimized collection for production efficiency
```

## Alerting Configuration

Environment-specific alert rules:

- **Dev**: Basic alerts for learning and testing
- **Test**: Validation alerts for CI/CD integration
- **Prod**: Critical business and SLA alerts

## Data Retention Policies

| Environment | Prometheus | Grafana | Jaeger |
|-------------|------------|---------|--------|
| Development | 7 days | 30 days | Memory only |
| Test | 3 days | 14 days | Memory only |
| Production | 30 days | 90 days | Elasticsearch |

## Best Practices

### Environment Switching
```bash
# Always set the observability environment
export OBSERVABILITY_ENV=test

# Start the corresponding observability stack
make observability-test

# Start services with matching environment
make up ENV=test
```

### Development Workflow
1. Start dev observability: `make observability-dev`
2. Develop and test locally
3. Switch to test environment for integration testing
4. Deploy to production with proper security configurations

### Testing Workflow
1. Start test observability: `make observability-test`
2. Run automated tests
3. Validate metrics and traces
4. Clean up test environment

### Production Deployment
1. Configure all required environment variables
2. Set up secure passwords and certificates
3. Configure network security
4. Start production observability
5. Monitor deployment health

## Troubleshooting

### Wrong Environment Running
```bash
# Check what's running
docker ps | grep jobpulse

# Stop specific environment
make observability-down ENV=test

# Stop all environments
make observability-down-all
```

### Configuration Not Loading
```bash
# Verify environment variable
echo $OBSERVABILITY_ENV

# Check if config file exists
ls -la auth-service/src/main/resources/observability/environments/$OBSERVABILITY_ENV/
```

### Port Conflicts
Each environment uses different ports to avoid conflicts:
- Dev: 9090, 3000, 16686
- Test: 9091, 3001, 16687
- Prod: 9092, 3002, 16688

This allows running multiple environments simultaneously for testing and validation.
