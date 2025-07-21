#!/bin/bash

# JobPulse Observability Stack Startup Script
set -e

echo "🚀 Starting JobPulse Observability Stack..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to check if a service is ready
check_service() {
    local service_name=$1
    local url=$2
    local max_attempts=30
    local attempt=1

    echo -e "${YELLOW}Waiting for $service_name to be ready...${NC}"
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f -s "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}✅ $service_name is ready!${NC}"
            return 0
        fi
        
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    echo -e "${RED}❌ $service_name failed to start within timeout${NC}"
    return 1
}

# Create the jobpulse network if it doesn't exist
docker network create jobpulse-network 2>/dev/null || true

# Start the observability stack
echo -e "${BLUE}Starting observability services...${NC}"
docker-compose -f docker-compose.observability.yml up -d

# Wait for services to be ready
echo -e "${YELLOW}Checking service health...${NC}"

check_service "Prometheus" "http://localhost:9090/-/ready"
check_service "Grafana" "http://localhost:3000/api/health"
check_service "Jaeger" "http://localhost:16686/"
    echo "✅ Prometheus is healthy"
else
    echo "❌ Prometheus is not responding"
fi

# Check Grafana
if curl -s http://localhost:3000/api/health > /dev/null; then
    echo "✅ Grafana is healthy"
else
    echo "❌ Grafana is not responding"
fi

# Check Jaeger
if curl -s http://localhost:16686/api/services > /dev/null; then
    echo "✅ Jaeger is healthy"
else
    echo "❌ Jaeger is not responding"
fi

echo ""
echo "🎉 Observability stack is ready!"
echo ""
echo "📊 Access your dashboards:"
echo "   Prometheus: http://localhost:9090"
echo "   Grafana: http://localhost:3000 (admin/\$GRAFANA_ADMIN_PASSWORD or check .env file)"
echo "   Jaeger: http://localhost:16686"
echo ""
echo "📈 To see metrics, make sure your services are running:"
echo "   docker-compose up -d"
echo ""
echo "🔧 To view logs:"
echo "   docker-compose -f docker-compose.observability.yml logs -f"
