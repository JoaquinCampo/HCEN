#!/bin/bash

# Script to run JMeter performance tests
# Usage: ./run-performance-tests.sh <test-plan-name> [options]
#
# Examples:
#   ./run-performance-tests.sh clinical-history-stability
#   ./run-performance-tests.sh breakpoint-test --users 100 --duration 1800
#   ./run-performance-tests.sh bottleneck-analysis --base-url http://localhost:8080

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
PERF_TESTS_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
JMETER_DIR="$PERF_TESTS_DIR/jmeter"
TEST_PLANS_DIR="$JMETER_DIR/test-plans"
DATA_DIR="$JMETER_DIR/data"
REPORTS_DIR="$JMETER_DIR/reports"
CONFIG_DIR="$JMETER_DIR/config"

# Default values
BASE_URL="${BASE_URL:-http://localhost:8080}"
API_BASE_PATH="${API_BASE_PATH:-/api}"
TEST_DATA_DIR="${TEST_DATA_DIR:-$DATA_DIR}"
REPORT_OUTPUT_DIR="${REPORT_OUTPUT_DIR:-$REPORTS_DIR}"
JMETER_HOME="${JMETER_HOME:-}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to show usage
show_usage() {
    cat << EOF
Usage: $0 <test-plan-name> [options]

Test Plans:
  - clinical-history-stability  : Stability test for clinical history queries
  - breakpoint-test            : Breakpoint test with aggressive ramp-up
  - bottleneck-analysis        : Bottleneck analysis test

Options:
  --base-url URL              Base URL of the application (default: $BASE_URL)
  --api-path PATH             API base path (default: $API_BASE_PATH)
  --data-dir DIR              Test data directory (default: $TEST_DATA_DIR)
  --report-dir DIR            Report output directory (default: $REPORT_OUTPUT_DIR)
  --jmeter-home DIR           JMeter home directory (if not in PATH)
  --users N                   Number of concurrent users (test plan specific)
  --duration SECONDS          Test duration in seconds (test plan specific)
  --ramp-up SECONDS           Ramp-up time in seconds (test plan specific)
  --no-html-report            Skip HTML report generation (view results in GUI instead)
  --open-gui                  Open results file in JMeter GUI after test completes
  --help                      Show this help message

Environment Variables:
  BASE_URL                    Base URL of the application
  API_BASE_PATH               API base path
  TEST_DATA_DIR               Test data directory
  REPORT_OUTPUT_DIR           Report output directory
  JMETER_HOME                 JMeter home directory

Examples:
  $0 clinical-history-stability
  $0 breakpoint-test --base-url http://localhost:8080 --users 200
  $0 bottleneck-analysis --report-dir ./custom-reports
EOF
}

# Parse arguments
TEST_PLAN=""
CUSTOM_USERS=""
CUSTOM_DURATION=""
CUSTOM_RAMPUP=""
GENERATE_HTML_REPORT=true
OPEN_GUI=false

if [ $# -eq 0 ]; then
    print_error "No test plan specified"
    show_usage
    exit 1
fi

TEST_PLAN="$1"
shift

while [[ $# -gt 0 ]]; do
    case $1 in
        --base-url)
            BASE_URL="$2"
            shift 2
            ;;
        --api-path)
            API_BASE_PATH="$2"
            shift 2
            ;;
        --data-dir)
            TEST_DATA_DIR="$2"
            shift 2
            ;;
        --report-dir)
            REPORT_OUTPUT_DIR="$2"
            shift 2
            ;;
        --jmeter-home)
            JMETER_HOME="$2"
            shift 2
            ;;
        --users)
            CUSTOM_USERS="$2"
            shift 2
            ;;
        --duration)
            CUSTOM_DURATION="$2"
            shift 2
            ;;
        --ramp-up)
            CUSTOM_RAMPUP="$2"
            shift 2
            ;;
        --no-html-report)
            GENERATE_HTML_REPORT=false
            shift
            ;;
        --open-gui)
            OPEN_GUI=true
            shift
            ;;
        --help)
            show_usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Validate test plan
TEST_PLAN_FILE="$TEST_PLANS_DIR/${TEST_PLAN}.jmx"
if [ ! -f "$TEST_PLAN_FILE" ]; then
    print_error "Test plan not found: $TEST_PLAN_FILE"
    print_info "Available test plans:"
    ls -1 "$TEST_PLANS_DIR"/*.jmx 2>/dev/null | xargs -n1 basename | sed 's/\.jmx$//' || echo "  (none found)"
    exit 1
fi

# Find JMeter
if [ -z "$JMETER_HOME" ]; then
    if command -v jmeter &> /dev/null; then
        JMETER_CMD="jmeter"
    else
        print_error "JMeter not found in PATH. Please install JMeter or set JMETER_HOME environment variable."
        print_info "Download JMeter from: https://jmeter.apache.org/download_jmeter.cgi"
        exit 1
    fi
else
    if [ ! -f "$JMETER_HOME/bin/jmeter" ]; then
        print_error "JMeter not found at: $JMETER_HOME/bin/jmeter"
        exit 1
    fi
    JMETER_CMD="$JMETER_HOME/bin/jmeter"
fi

# Create reports directory
mkdir -p "$REPORT_OUTPUT_DIR"

# Generate timestamp for report
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
REPORT_NAME="${TEST_PLAN}_${TIMESTAMP}"
RESULT_FILE="$REPORT_OUTPUT_DIR/${REPORT_NAME}.jtl"
HTML_REPORT_DIR="$REPORT_OUTPUT_DIR/${REPORT_NAME}_html"

print_info "Starting performance test: $TEST_PLAN"
print_info "Base URL: $BASE_URL"
print_info "API Path: $API_BASE_PATH"
print_info "Test Data Dir: $TEST_DATA_DIR"
print_info "Result file: $RESULT_FILE"
if [ "$GENERATE_HTML_REPORT" = true ]; then
    print_info "HTML report will be generated at: $HTML_REPORT_DIR"
else
    print_info "HTML report generation disabled (use --open-gui to view results)"
fi

# Build JMeter command
JMETER_ARGS=(
    -n
    -t "$TEST_PLAN_FILE"
    -l "$RESULT_FILE"
    -Jbase.url="$BASE_URL"
    -Japi.base.path="$API_BASE_PATH"
    -Jtest.data.dir="$TEST_DATA_DIR"
    -Jreport.output.dir="$REPORT_OUTPUT_DIR"
)

# Add HTML report generation if requested
if [ "$GENERATE_HTML_REPORT" = true ]; then
    JMETER_ARGS+=(-e -o "$HTML_REPORT_DIR")
fi

# Add custom properties if specified
if [ -n "$CUSTOM_USERS" ]; then
    JMETER_ARGS+=(-Jthreads.count="$CUSTOM_USERS")
fi

if [ -n "$CUSTOM_DURATION" ]; then
    JMETER_ARGS+=(-Jduration.seconds="$CUSTOM_DURATION")
fi

if [ -n "$CUSTOM_RAMPUP" ]; then
    JMETER_ARGS+=(-Jramp.up.seconds="$CUSTOM_RAMPUP")
fi

# Load custom properties if exists
# Use -q (user properties) instead of -p (system properties) to avoid replacing JMeter's default properties
if [ -f "$CONFIG_DIR/user.properties" ]; then
    JMETER_ARGS+=(-q "$CONFIG_DIR/user.properties")
fi

# Note: We don't use -p for jmeter.properties as it replaces ALL properties
# Instead, we add specific properties via -J flags or include them in user.properties

# Run JMeter
print_info "Executing JMeter..."
print_info "Command: $JMETER_CMD ${JMETER_ARGS[*]}"

if "$JMETER_CMD" "${JMETER_ARGS[@]}"; then
    print_info "Performance test completed successfully!"
    print_info "Results saved to: $RESULT_FILE"
    
    if [ "$GENERATE_HTML_REPORT" = true ] && [ -d "$HTML_REPORT_DIR" ]; then
        print_info "HTML report available at: $HTML_REPORT_DIR/index.html"
    fi
    
    # Print summary if jtl file exists
    if [ -f "$RESULT_FILE" ]; then
        print_info ""
        print_info "Test Summary:"
        print_info "  Result file: $RESULT_FILE"
        if [ "$GENERATE_HTML_REPORT" = true ] && [ -d "$HTML_REPORT_DIR" ]; then
            print_info "  HTML report: $HTML_REPORT_DIR/index.html"
            print_info ""
            print_info "To view the HTML report, open: file://$HTML_REPORT_DIR/index.html"
        fi
        print_info ""
        print_info "To view results in JMeter GUI:"
        print_info "  jmeter -g $RESULT_FILE"
        print_info ""
        print_info "Or open JMeter GUI and load the result file:"
        print_info "  jmeter"
        print_info "  Then: File -> Load -> Select: $RESULT_FILE"
    fi
    
    # Open GUI if requested
    if [ "$OPEN_GUI" = true ]; then
        print_info ""
        print_info "Opening results in JMeter GUI..."
        "$JMETER_CMD" -g "$RESULT_FILE" &
    fi
else
    print_error "Performance test failed!"
    exit 1
fi

