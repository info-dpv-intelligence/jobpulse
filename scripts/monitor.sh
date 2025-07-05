#!/bin/bash

# JobPulse Real-time Monitoring Script
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

PROMETHEUS_URL="http://localhost:9090"
AUTH_SERVICE_URL="http://localhost:8080"
JOB_SERVICE_URL="http://localhost:8081"

# Function to get metric value from Prometheus
get_metric() {
    local query=$1
    curl -s "$PROMETHEUS_URL/api/v1/query?query=$query" | \
        jq -r '.data.result[0].value[1] // "0"' 2>/dev/null || echo "0"
}

# Function to get service health
get_service_health() {
    local url=$1
    if curl -f -s "$url/actuator/health" | jq -r '.status' 2>/dev/null | grep -q "UP"; then
        echo "UP"
    else
        echo "DOWN"
    fi
}

# Function to display real-time metrics
display_metrics() {
    clear
    
    echo -e "${BLUE}📊 JobPulse Real-time Observability Dashboard${NC}"
    echo -e "${CYAN}$(date)${NC}"
    echo "=================================================="
    echo ""
    
    # Service Health Status
    echo -e "${YELLOW}🏥 SERVICE HEALTH${NC}"
    local auth_health=$(get_service_health "$AUTH_SERVICE_URL")
    local job_health=$(get_service_health "$JOB_SERVICE_URL")
    
    if [ "$auth_health" = "UP" ]; then
        echo -e "🔐 Auth Service: ${GREEN}$auth_health${NC}"
    else
        echo -e "🔐 Auth Service: ${RED}$auth_health${NC}"
    fi
    
    if [ "$job_health" = "UP" ]; then
        echo -e "💼 Job Service: ${GREEN}$job_health${NC}"
    else
        echo -e "💼 Job Service: ${RED}$job_health${NC}"
    fi
    echo ""
    
    # Request Rates
    echo -e "${YELLOW}📈 REQUEST RATES (per second)${NC}"
    local auth_rps=$(get_metric 'sum(rate(http_server_requests_seconds_count{application="auth-service"}[1m]))')
    local job_rps=$(get_metric 'sum(rate(http_server_requests_seconds_count{application="job-service"}[1m]))')
    
    printf "🔐 Auth Service: %6.2f RPS\n" "$auth_rps"
    printf "💼 Job Service: %6.2f RPS\n" "$job_rps"
    echo ""
    
    # Response Times (95th percentile)
    echo -e "${YELLOW}⏱️  RESPONSE TIMES (95th percentile, ms)${NC}"
    local auth_p95=$(get_metric 'histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{application="auth-service"}[5m])) by (le)) * 1000')
    local job_p95=$(get_metric 'histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{application="job-service"}[5m])) by (le)) * 1000')
    
    printf "🔐 Auth Service: %6.2f ms\n" "$auth_p95"
    printf "💼 Job Service: %6.2f ms\n" "$job_p95"
    echo ""
    
    # Error Rates
    echo -e "${YELLOW}🚨 ERROR RATES${NC}"
    local auth_errors=$(get_metric 'sum(rate(http_server_requests_seconds_count{application="auth-service",status=~"4..|5.."}[5m]))')
    local job_errors=$(get_metric 'sum(rate(http_server_requests_seconds_count{application="job-service",status=~"4..|5.."}[5m]))')
    
    if (( $(echo "$auth_errors > 0" | bc -l) )); then
        printf "🔐 Auth Service: ${RED}%.2f errors/sec${NC}\n" "$auth_errors"
    else
        printf "🔐 Auth Service: ${GREEN}%.2f errors/sec${NC}\n" "$auth_errors"
    fi
    
    if (( $(echo "$job_errors > 0" | bc -l) )); then
        printf "💼 Job Service: ${RED}%.2f errors/sec${NC}\n" "$job_errors"
    else
        printf "💼 Job Service: ${GREEN}%.2f errors/sec${NC}\n" "$job_errors"
    fi
    echo ""
    
    # Business Metrics
    echo -e "${YELLOW}📊 BUSINESS METRICS${NC}"
    local registrations=$(get_metric 'auth_registration_attempts_total')
    local logins=$(get_metric 'auth_login_attempts_total')
    local searches=$(get_metric 'job_searches_total')
    
    printf "👤 Total Registrations: %.0f\n" "$registrations"
    printf "🔑 Total Logins: %.0f\n" "$logins"
    printf "🔍 Total Job Searches: %.0f\n" "$searches"
    echo ""
    
    # JVM Memory Usage
    echo -e "${YELLOW}💾 JVM MEMORY USAGE (MB)${NC}"
    local auth_memory=$(get_metric 'jvm_memory_used_bytes{application="auth-service",area="heap"} / 1024 / 1024')
    local job_memory=$(get_metric 'jvm_memory_used_bytes{application="job-service",area="heap"} / 1024 / 1024')
    
    printf "🔐 Auth Service Heap: %6.2f MB\n" "$auth_memory"
    printf "💼 Job Service Heap: %6.2f MB\n" "$job_memory"
    echo ""
    
    # Database Connections
    echo -e "${YELLOW}🗄️  DATABASE CONNECTIONS${NC}"
    local auth_db_active=$(get_metric 'hikaricp_connections_active{application="auth-service"}')
    local job_db_active=$(get_metric 'hikaricp_connections_active{application="job-service"}')
    
    printf "🔐 Auth Service Active: %.0f\n" "$auth_db_active"
    printf "💼 Job Service Active: %.0f\n" "$job_db_active"
    echo ""
    
    # Top Endpoints by Request Count
    echo -e "${YELLOW}🔥 TOP ENDPOINTS (last 5 minutes)${NC}"
    echo "Loading endpoint metrics..."
    echo ""
    
    # System Resources
    echo -e "${YELLOW}🖥️  SYSTEM RESOURCES${NC}"
    local cpu_usage=$(get_metric 'system_cpu_usage * 100')
    local process_cpu=$(get_metric 'process_cpu_usage * 100')
    
    printf "System CPU: %6.2f%%\n" "$cpu_usage"
    printf "Process CPU: %6.2f%%\n" "$process_cpu"
    echo ""
    
    echo -e "${CYAN}Press Ctrl+C to exit | Refreshing every 5 seconds${NC}"
    echo "=================================================="
}

# Function to run continuous monitoring
monitor() {
    echo -e "${BLUE}Starting real-time monitoring...${NC}"
    echo "Make sure Prometheus is running at $PROMETHEUS_URL"
    echo ""
    
    # Check if Prometheus is accessible
    if ! curl -f -s "$PROMETHEUS_URL/-/ready" > /dev/null; then
        echo -e "${RED}❌ Prometheus is not accessible at $PROMETHEUS_URL${NC}"
        echo "Start observability stack with: ./start-observability.sh"
        exit 1
    fi
    
    # Trap Ctrl+C to clean exit
    trap 'echo -e "\n${GREEN}Monitoring stopped.${NC}"; exit 0' INT
    
    while true; do
        display_metrics
        sleep 5
    done
}

# Function to show single snapshot
snapshot() {
    echo -e "${BLUE}📸 JobPulse Metrics Snapshot${NC}"
    echo ""
    
    # Check if Prometheus is accessible
    if ! curl -f -s "$PROMETHEUS_URL/-/ready" > /dev/null; then
        echo -e "${RED}❌ Prometheus is not accessible at $PROMETHEUS_URL${NC}"
        exit 1
    fi
    
    display_metrics
}

# Function to export metrics to file
export_metrics() {
    local filename="jobpulse-metrics-$(date +%Y%m%d-%H%M%S).txt"
    
    echo "Exporting metrics snapshot to $filename..."
    
    {
        echo "JobPulse Metrics Export"
        echo "Generated: $(date)"
        echo "=========================="
        echo ""
        
        # Service Health
        echo "SERVICE HEALTH:"
        echo "Auth Service: $(get_service_health "$AUTH_SERVICE_URL")"
        echo "Job Service: $(get_service_health "$JOB_SERVICE_URL")"
        echo ""
        
        # Request Rates
        echo "REQUEST RATES (RPS):"
        echo "Auth Service: $(get_metric 'sum(rate(http_server_requests_seconds_count{application="auth-service"}[1m]))')"
        echo "Job Service: $(get_metric 'sum(rate(http_server_requests_seconds_count{application="job-service"}[1m]))')"
        echo ""
        
        # Business Metrics
        echo "BUSINESS METRICS:"
        echo "Total Registrations: $(get_metric 'auth_registration_attempts_total')"
        echo "Total Logins: $(get_metric 'auth_login_attempts_total')"
        echo "Total Job Searches: $(get_metric 'job_searches_total')"
        echo ""
        
    } > "$filename"
    
    echo -e "${GREEN}✅ Metrics exported to $filename${NC}"
}

# Main function
main() {
    case "${1:-monitor}" in
        "monitor"|"")
            monitor
            ;;
        "snapshot")
            snapshot
            ;;
        "export")
            export_metrics
            ;;
        "help")
            echo -e "${BLUE}JobPulse Monitoring Script${NC}"
            echo ""
            echo "Usage: $0 [command]"
            echo ""
            echo "Commands:"
            echo "  monitor   - Start real-time monitoring dashboard (default)"
            echo "  snapshot  - Show single metrics snapshot"
            echo "  export    - Export metrics to file"
            echo "  help      - Show this help"
            echo ""
            echo "Examples:"
            echo "  $0                 # Start monitoring"
            echo "  $0 snapshot        # One-time snapshot"
            echo "  $0 export          # Export to file"
            ;;
        *)
            echo -e "${RED}Unknown command: $1${NC}"
            echo "Use '$0 help' for usage information"
            exit 1
            ;;
    esac
}

# Check dependencies
if ! command -v jq &> /dev/null; then
    echo -e "${RED}❌ jq is required but not installed. Please install jq first.${NC}"
    exit 1
fi

if ! command -v bc &> /dev/null; then
    echo -e "${RED}❌ bc is required but not installed. Please install bc first.${NC}"
    exit 1
fi

# Run main function
main "$@"
