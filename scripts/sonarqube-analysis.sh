#!/bin/bash
set -e

# SonarQube Analysis Script
# Usage: 
#   ./scripts/sonarqube-analysis.sh           # Uses .env (local)
#   ./scripts/sonarqube-analysis.sh prod      # Uses .env.production
#   ./scripts/sonarqube-analysis.sh production # Uses .env.production

echo "=== Starting SonarQube Analysis ==="

# Determine which env file to use
ENV_FILE=".env"
if [ "$1" = "prod" ] || [ "$1" = "production" ]; then
    ENV_FILE=".env.production"
    echo "Running in PRODUCTION mode"
fi

# Load environment variables if not already set
if [ -z "$SONAR_HOST_URL" ] && [ -f "$ENV_FILE" ]; then
    echo "Loading environment variables from $ENV_FILE..."
    set -a
    source "$ENV_FILE"
    set +a
fi

echo "Using SONAR_HOST_URL: $SONAR_HOST_URL"
echo "Using SONAR_PROJECT_KEY: $SONAR_PROJECT_KEY"

# Step 1: Clean and build
echo "Step 1: Building project..."
mvn clean verify -DskipTests=false

# Step 2: Run SonarQube analysis
echo "Step 2: Running SonarQube analysis..."
if [ -z "$SONAR_TOKEN" ]; then
    echo "ERROR: SONAR_TOKEN environment variable is not set"
    exit 1
fi

if [ -z "$SONAR_HOST_URL" ]; then
    echo "WARNING: SONAR_HOST_URL not set, using default http://localhost:9000"
    SONAR_HOST_URL="http://localhost:9000"
fi

sonar-scanner \
    -Dsonar.token="$SONAR_TOKEN" \
    -Dsonar.host.url="$SONAR_HOST_URL"

echo "=== SonarQube Analysis Complete ==="
echo "View results at: $SONAR_HOST_URL/dashboard?id=$SONAR_PROJECT_KEY"
