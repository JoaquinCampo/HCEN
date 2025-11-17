#!/bin/bash

# Script to open JMeter results file in GUI
# Usage: ./open-results-gui.sh [result-file.jtl]

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPORTS_DIR="$SCRIPT_DIR/../jmeter/reports"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# Find JMeter
if command -v jmeter &> /dev/null; then
    JMETER_CMD="jmeter"
elif [ -n "$JMETER_HOME" ] && [ -f "$JMETER_HOME/bin/jmeter" ]; then
    JMETER_CMD="$JMETER_HOME/bin/jmeter"
else
    echo "Error: JMeter not found. Please install JMeter or set JMETER_HOME."
    exit 1
fi

# If file provided, use it; otherwise find latest
if [ -n "$1" ]; then
    RESULT_FILE="$1"
    if [ ! -f "$RESULT_FILE" ]; then
        echo "Error: File not found: $RESULT_FILE"
        exit 1
    fi
else
    # Find latest JTL file
    RESULT_FILE=$(ls -t "$REPORTS_DIR"/*.jtl 2>/dev/null | head -1)
    if [ -z "$RESULT_FILE" ]; then
        echo "Error: No result files found in $REPORTS_DIR"
        echo "Run a performance test first or specify a file: $0 <file.jtl>"
        exit 1
    fi
    print_info "Using latest result file: $RESULT_FILE"
fi

print_info "Opening JMeter GUI with results: $RESULT_FILE"
print_info ""
print_info "In JMeter GUI, you can:"
print_info "  - View results in 'View Results Tree' listener"
print_info "  - See statistics in 'Summary Report' listener"
print_info "  - Analyze response times in 'Aggregate Report' listener"
print_info "  - View graphs in 'Response Times Over Time' listener"
print_info ""

"$JMETER_CMD" -g "$RESULT_FILE"

