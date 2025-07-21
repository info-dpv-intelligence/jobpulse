#!/bin/bash

# JobPulse Observability Validation Script
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Load environment variables
if [ -f "$(dirname "$0")/../.env" ]; then
    source "$(dirname "$0")/../.env"
fi

# Default credentials (should be overridden by environment variables)
GRAFANA_USER=${GRAFANA_USER:-admin}
GRAFANA_PASSWORD=${GRAFANA_PASSWORD:-}

# Check if required credentials are set
if [ -z "$GRAFANA_PASSWORD" ]; then
    echo -e "${RED}❌ GRAFANA_PASSWORD environment variable is required${NC}"
    echo "Please set GRAFANA_PASSWORD in your .env file or export it:"
    echo "export GRAFANA_PASSWORD=your_password"
    exit 1
fi

# Environment detection
ENV=${1:-dev}
case $ENV in
    prod)
        PROMETHEUS_URL="http://localhost:9092"
        GRAFANA_URL="http://localhost:3002"
        JAEGER_URL="http://localhost:16688"
        AUTH_SERVICE_URL="http://16.171.9.26:8089"
        JOB_SERVICE_URL="http://16.171.9.26:8084"
        ;;
    test)
        PROMETHEUS_URL="http://localhost:9091"
        GRAFANA_URL="http://localhost:3001"
        JAEGER_URL="http://localhost:16687"
        AUTH_SERVICE_URL="http://localhost:8080"
        JOB_SERVICE_URL="http://localhost:8081"
        ;;
    *)
        PROMETHEUS_URL="http://localhost:9090"
        GRAFANA_URL="http://localhost:3000"
        JAEGER_URL="http://localhost:16686"
        AUTH_SERVICE_URL="http://localhost:8080"
        JOB_SERVICE_URL="http://localhost:8081"
        ;;
esac

echo -e "${BLUE}🔍 JobPulse Observability Validation - Environment: $ENV${NC}"
echo ""

# Function to check if a URL is accessible
check_url() {
    local name=$1
    local url=$2
    local expected_content=$3
    
    if curl -f -s "$url" > /dev/null 2>&1; then
        if [ -n "$expected_content" ]; then
            if curl -s "$url" | grep -q "$expected_content"; then
                echo -e "${GREEN}✅ $name is accessible and working${NC}"
                return 0
            else
                echo -e "${YELLOW}⚠️  $name is accessible but missing expected content${NC}"
                return 1
            fi
        else
            echo -e "${GREEN}✅ $name is accessible${NC}"
            return 0
        fi
    else
        echo -e "${RED}❌ $name is not accessible at $url${NC}"
        return 1
    fi
}

# Function to check Prometheus metrics
check_prometheus_metrics() {
    echo -e "${BLUE}Checking Prometheus metrics...${NC}"
    
    local metrics=(
        "up"
        "http_server_requests_seconds_count"
        "http_server_requests_seconds_sum"
        "jvm_memory_used_bytes"
        "auth_registration_attempts_total"
        "auth_login_attempts_total"
        "job_searches_total"
    )
    
    for metric in "${metrics[@]}"; do
        if curl -s "$PROMETHEUS_URL/api/v1/query?query=$metric" | jq -r '.data.result | length' | grep -q "^[1-9]"; then
            echo -e "${GREEN}✅ Metric '$metric' has data${NC}"
        else
            echo -e "${YELLOW}⚠️  Metric '$metric' has no data (expected for new setup)${NC}"
        fi
    done
}

# Function to check Grafana dashboards
check_grafana_dashboard() {
    echo -e "${BLUE}Checking Grafana dashboard...${NC}"
    
    # Check if dashboard exists
    local dashboard_response=$(curl -s -u "$GRAFANA_USER:$GRAFANA_PASSWORD" "$GRAFANA_URL/api/dashboards/uid/jobpulse-overview")
    
    if echo "$dashboard_response" | jq -r '.dashboard.title' | grep -q "JobPulse"; then
        echo -e "${GREEN}✅ JobPulse dashboard is available${NC}"
    else
        echo -e "${RED}❌ JobPulse dashboard not found${NC}"
        return 1
    fi
    
    # Check data sources
    local datasources_response=$(curl -s -u "$GRAFANA_USER:$GRAFANA_PASSWORD" "$GRAFANA_URL/api/datasources")
    
    if echo "$datasources_response" | jq -r '.[].name' | grep -q "Prometheus"; then
        echo -e "${GREEN}✅ Prometheus data source configured${NC}"
    else
        echo -e "${RED}❌ Prometheus data source not configured${NC}"
    fi
    
    if echo "$datasources_response" | jq -r '.[].name' | grep -q "Jaeger"; then
        echo -e "${GREEN}✅ Jaeger data source configured${NC}"
    else
        echo -e "${YELLOW}⚠️  Jaeger data source not configured${NC}"
    fi
}

# Function to check service health and metrics endpoints
check_service_endpoints() {
    echo -e "${BLUE}Checking service endpoints...${NC}"
    
    # Auth Service
    check_url "Auth Service Health" "$AUTH_SERVICE_URL/actuator/health" "UP"
    check_url "Auth Service Metrics" "$AUTH_SERVICE_URL/actuator/prometheus" "http_server_requests"
    check_url "Auth Service Info" "$AUTH_SERVICE_URL/actuator/info"
    
    # Job Service
    check_url "Job Service Health" "$JOB_SERVICE_URL/actuator/health" "UP"
    check_url "Job Service Metrics" "$JOB_SERVICE_URL/actuator/prometheus" "http_server_requests"
    check_url "Job Service Info" "$JOB_SERVICE_URL/actuator/info"
}

# Function to test custom metrics
test_custom_metrics() {
    echo -e "${BLUE}Testing custom business metrics...${NC}"
    
    # Generate some test traffic
    echo "Generating test traffic..."
    
    # Register a test user
    curl -s -X POST "$AUTH_SERVICE_URL/api/auth/register" \
        -H "Content-Type: application/json" \
        -d '{"email":"metrics.test@jobpulse.com","password":"TestPassword123!","role":"JOB_APPLICANT"}' \
        > /dev/null 2>&1 || true
    
    # Login attempt
    curl -s -X POST "$AUTH_SERVICE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"email":"metrics.test@jobpulse.com","password":"TestPassword123!"}' \
        > /dev/null 2>&1 || true
    
    # Search jobs
    curl -s "$JOB_SERVICE_URL/api/jobs/search?query=test" > /dev/null 2>&1 || true
    
    sleep 5
    
    # Check if custom metrics are being updated
    local auth_metrics=$(curl -s "$AUTH_SERVICE_URL/actuator/prometheus" | grep -E "(auth_registration|auth_login)_attempts_total")
    if [ -n "$auth_metrics" ]; then
        echo -e "${GREEN}✅ Custom auth metrics are available${NC}"
        echo "$auth_metrics"
    else
        echo -e "${YELLOW}⚠️  Custom auth metrics not found${NC}"
    fi
    
    local job_metrics=$(curl -s "$JOB_SERVICE_URL/actuator/prometheus" | grep -E "job_searches_total")
    if [ -n "$job_metrics" ]; then
        echo -e "${GREEN}✅ Custom job metrics are available${NC}"
        echo "$job_metrics"
    else
        echo -e "${YELLOW}⚠️  Custom job metrics not found${NC}"
    fi
}

# Function to check trace data in Jaeger
check_jaeger_traces() {
    echo -e "${BLUE}Checking Jaeger traces...${NC}"
    
    # Check if Jaeger API is working
    if curl -s "$JAEGER_URL/api/services" | jq -r '.data[]' | grep -q "auth-service\|job-service"; then
        echo -e "${GREEN}✅ Jaeger has trace data from services${NC}"
    else
        echo -e "${YELLOW}⚠️  No trace data found in Jaeger (expected for new setup)${NC}"
    fi
}

# Function to generate a comprehensive test report
generate_report() {
    echo ""
    echo -e "${BLUE}📋 OBSERVABILITY TEST REPORT${NC}"
    echo "======================================"
    
    local timestamp=$(date)
    echo "Test Date: $timestamp"
    echo ""
    
    echo -e "${YELLOW}Service Availability:${NC}"
    check_url "Prometheus" "$PROMETHEUS_URL/-/ready" > /dev/null && echo "✅ Prometheus" || echo "❌ Prometheus"
    check_url "Grafana" "$GRAFANA_URL/api/health" > /dev/null && echo "✅ Grafana" || echo "❌ Grafana"
    check_url "Jaeger" "$JAEGER_URL/" > /dev/null && echo "✅ Jaeger" || echo "❌ Jaeger"
    check_url "Auth Service" "$AUTH_SERVICE_URL/actuator/health" > /dev/null && echo "✅ Auth Service" || echo "❌ Auth Service"
    check_url "Job Service" "$JOB_SERVICE_URL/actuator/health" > /dev/null && echo "✅ Job Service" || echo "❌ Job Service"
    
    echo ""
    echo -e "${YELLOW}Quick Access URLs:${NC}"
    echo "📊 Grafana Dashboard: $GRAFANA_URL (${GRAFANA_USER}/***)"
    echo "📈 Prometheus: $PROMETHEUS_URL"
    echo "🔍 Jaeger Tracing: $JAEGER_URL"
    echo ""
    echo -e "${YELLOW}Service Metrics:${NC}"
    echo "🔐 Auth Service: $AUTH_SERVICE_URL/actuator/prometheus"
    echo "💼 Job Service: $JOB_SERVICE_URL/actuator/prometheus"
    echo ""
    echo -e "${YELLOW}Next Steps:${NC}"
    echo "1. Run load tests: ./scripts/load-test.sh"
    echo "2. Check metrics in Grafana dashboard"
    echo "3. Monitor custom business metrics"
    echo "4. Review trace data in Jaeger"
}

# Main execution
main() {
    echo "Starting observability validation..."
    echo ""
    
    # Core infrastructure checks
    check_url "Prometheus" "$PROMETHEUS_URL/-/ready"
    check_url "Grafana" "$GRAFANA_URL/api/health"
    check_url "Jaeger" "$JAEGER_URL/"
    
    echo ""
    
    # Service checks
    check_service_endpoints
    
    echo ""
    
    # Prometheus metrics check
    check_prometheus_metrics
    
    echo ""
    
    # Grafana dashboard check
    check_grafana_dashboard
    
    echo ""
    
    # Custom metrics test
    test_custom_metrics
    
    echo ""
    
    # Jaeger traces check
    check_jaeger_traces
    
    echo ""
    
    # Generate final report
    generate_report
}

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    echo -e "${RED}❌ jq is required but not installed. Please install jq first.${NC}"
    echo "macOS: brew install jq"
    echo "Ubuntu: sudo apt-get install jq"
    exit 1
fi

# Run main function
main
