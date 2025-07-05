#!/bin/bash

# JobPulse Load Testing Script for Observability
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

AUTH_SERVICE_URL="http://localhost:8080"
JOB_SERVICE_URL="http://localhost:8081"

echo -e "${BLUE}🧪 JobPulse Load Testing Script${NC}"
echo -e "${YELLOW}This script will generate test traffic to observe metrics${NC}"
echo ""

# Function to check if services are running
check_services() {
    echo -e "${YELLOW}Checking if services are running...${NC}"
    
    if ! curl -f -s "$AUTH_SERVICE_URL/actuator/health" > /dev/null; then
        echo -e "${RED}❌ Auth service is not running at $AUTH_SERVICE_URL${NC}"
        echo "Start it with: ./gradlew bootRun (in auth-service directory)"
        exit 1
    fi
    
    if ! curl -f -s "$JOB_SERVICE_URL/actuator/health" > /dev/null; then
        echo -e "${RED}❌ Job service is not running at $JOB_SERVICE_URL${NC}"
        echo "Start it with: ./gradlew bootRun (in job-service directory)"
        exit 1
    fi
    
    echo -e "${GREEN}✅ All services are running${NC}"
}

# Function to register a test user
register_user() {
    local email="test$1@jobpulse.com"
    local password="TestPassword123!"
    local role="JOB_APPLICANT"
    
    curl -s -X POST "$AUTH_SERVICE_URL/api/auth/register" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$email\",\"password\":\"$password\",\"role\":\"$role\"}" \
        > /dev/null 2>&1 || true
}

# Function to login and get JWT token
login_user() {
    local email="test$1@jobpulse.com"
    local password="TestPassword123!"
    
    local response=$(curl -s -X POST "$AUTH_SERVICE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$email\",\"password\":\"$password\"}")
    
    echo "$response" | jq -r '.accessToken' 2>/dev/null || echo ""
}

# Function to simulate auth service load
test_auth_service() {
    echo -e "${BLUE}Testing Auth Service...${NC}"
    
    # Registration load test
    echo "Simulating user registrations..."
    for i in $(seq 1 20); do
        register_user $i &
        if [ $((i % 5)) -eq 0 ]; then
            wait
            echo -n "."
        fi
    done
    wait
    echo " ✅"
    
    # Login load test
    echo "Simulating user logins..."
    for i in $(seq 1 15); do
        login_user $i > /dev/null &
        if [ $((i % 5)) -eq 0 ]; then
            wait
            echo -n "."
        fi
    done
    wait
    echo " ✅"
    
    # Failed login attempts (to test error metrics)
    echo "Simulating failed login attempts..."
    for i in $(seq 1 5); do
        curl -s -X POST "$AUTH_SERVICE_URL/api/auth/login" \
            -H "Content-Type: application/json" \
            -d "{\"email\":\"nonexistent@test.com\",\"password\":\"wrongpassword\"}" \
            > /dev/null 2>&1 &
    done
    wait
    echo " ✅"
}

# Function to simulate job service load
test_job_service() {
    echo -e "${BLUE}Testing Job Service...${NC}"
    
    # Get a valid JWT token for authenticated requests
    local token=$(login_user 1)
    
    if [ -z "$token" ] || [ "$token" = "null" ]; then
        echo -e "${YELLOW}⚠️  No valid token, testing public endpoints only${NC}"
        token=""
    fi
    
    # Job search simulation
    echo "Simulating job searches..."
    local search_terms=("developer" "engineer" "manager" "analyst" "designer")
    
    for term in "${search_terms[@]}"; do
        for i in $(seq 1 10); do
            if [ -n "$token" ]; then
                curl -s -X GET "$JOB_SERVICE_URL/api/jobs/search?query=$term" \
                    -H "Authorization: Bearer $token" > /dev/null &
            else
                curl -s -X GET "$JOB_SERVICE_URL/api/jobs/search?query=$term" > /dev/null &
            fi
        done
        wait
        echo -n "."
    done
    echo " ✅"
    
    # Health check calls
    echo "Testing health endpoints..."
    for i in $(seq 1 20); do
        curl -s "$JOB_SERVICE_URL/actuator/health" > /dev/null &
        if [ $((i % 5)) -eq 0 ]; then
            wait
            echo -n "."
        fi
    done
    wait
    echo " ✅"
}

# Function to test metrics endpoints
test_metrics_endpoints() {
    echo -e "${BLUE}Testing Metrics Endpoints...${NC}"
    
    echo "Checking Prometheus metrics endpoints..."
    
    # Auth service metrics
    if curl -s "$AUTH_SERVICE_URL/actuator/prometheus" | grep -q "http_server_requests"; then
        echo -e "${GREEN}✅ Auth service metrics available${NC}"
    else
        echo -e "${RED}❌ Auth service metrics not available${NC}"
    fi
    
    # Job service metrics
    if curl -s "$JOB_SERVICE_URL/actuator/prometheus" | grep -q "http_server_requests"; then
        echo -e "${GREEN}✅ Job service metrics available${NC}"
    else
        echo -e "${RED}❌ Job service metrics not available${NC}"
    fi
}

# Function to generate continuous load
continuous_load() {
    local duration=${1:-60}
    echo -e "${BLUE}Running continuous load for $duration seconds...${NC}"
    
    local end_time=$(($(date +%s) + duration))
    
    while [ $(date +%s) -lt $end_time ]; do
        # Random auth operations
        register_user $RANDOM &
        login_user $(( (RANDOM % 10) + 1 )) > /dev/null &
        
        # Random job searches
        local terms=("java" "python" "javascript" "react" "spring")
        local term=${terms[$((RANDOM % ${#terms[@]}))]}
        curl -s "$JOB_SERVICE_URL/api/jobs/search?query=$term" > /dev/null &
        
        # Health checks
        curl -s "$AUTH_SERVICE_URL/actuator/health" > /dev/null &
        curl -s "$JOB_SERVICE_URL/actuator/health" > /dev/null &
        
        sleep 1
    done
    
    wait
    echo -e "${GREEN}✅ Continuous load test completed${NC}"
}

# Main execution
main() {
    check_services
    
    echo ""
    echo -e "${YELLOW}Choose test type:${NC}"
    echo "1. Quick test (30 seconds)"
    echo "2. Standard test (2 minutes)"
    echo "3. Extended test (5 minutes)"
    echo "4. Custom duration"
    echo "5. One-time load burst"
    
    read -p "Enter choice (1-5): " choice
    
    case $choice in
        1)
            echo -e "${BLUE}Running quick test...${NC}"
            test_auth_service
            test_job_service
            test_metrics_endpoints
            continuous_load 30
            ;;
        2)
            echo -e "${BLUE}Running standard test...${NC}"
            test_auth_service
            test_job_service
            test_metrics_endpoints
            continuous_load 120
            ;;
        3)
            echo -e "${BLUE}Running extended test...${NC}"
            test_auth_service
            test_job_service
            test_metrics_endpoints
            continuous_load 300
            ;;
        4)
            read -p "Enter duration in seconds: " duration
            test_auth_service
            test_job_service
            test_metrics_endpoints
            continuous_load $duration
            ;;
        5)
            echo -e "${BLUE}Running load burst...${NC}"
            test_auth_service
            test_job_service
            test_metrics_endpoints
            ;;
        *)
            echo -e "${RED}Invalid choice${NC}"
            exit 1
            ;;
    esac
    
    echo ""
    echo -e "${GREEN}🎉 Load testing completed!${NC}"
    echo ""
    echo -e "${BLUE}View your metrics at:${NC}"
    echo "📊 Grafana: http://localhost:3000"
    echo "📈 Prometheus: http://localhost:9090"
    echo "🔍 Jaeger: http://localhost:16686"
    echo ""
    echo -e "${YELLOW}Key metrics to check:${NC}"
    echo "• Request rate (RPS)"
    echo "• Response time percentiles"
    echo "• Error rates"
    echo "• Custom business metrics"
    echo "• JVM memory usage"
    echo "• Database connection pools"
}

# Run main function
main
