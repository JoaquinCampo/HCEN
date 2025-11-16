#!/bin/bash
set -e

# SonarQube Analysis Script for CI/CD
# This script runs tests, generates coverage reports, and uploads to SonarQube

echo "=== Starting SonarQube Analysis ==="

# Load environment variables from .env if it exists (for local development)
if [ -f .env ]; then
    echo "Loading environment variables from .env..."
    set -a
    source .env
    set +a
fi

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

# Use standalone scanner (faster and more reliable)
sonar-scanner \
    -Dsonar.token="$SONAR_TOKEN" \
    -Dsonar.host.url="$SONAR_HOST_URL"

echo "=== SonarQube Analysis Complete ==="
echo "View results at: $SONAR_HOST_URL/dashboard?id=grupo12:practico"
