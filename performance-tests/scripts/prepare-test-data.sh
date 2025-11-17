#!/bin/bash

# Script to prepare test data for JMeter performance tests
# This script generates CSV files with test data

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
DATA_DIR="$SCRIPT_DIR/../jmeter/data"

echo "Preparing test data for JMeter performance tests..."
echo "Data directory: $DATA_DIR"

# Create data directory if it doesn't exist
mkdir -p "$DATA_DIR"

# Generate users.csv
echo "Generating users.csv..."
cat > "$DATA_DIR/users.csv" << 'EOF'
ci,email,name
12345678,user1@test.com,Usuario Test 1
87654321,user2@test.com,Usuario Test 2
11223344,user3@test.com,Usuario Test 3
44332211,user4@test.com,Usuario Test 4
55667788,user5@test.com,Usuario Test 5
88776655,user6@test.com,Usuario Test 6
99887766,user7@test.com,Usuario Test 7
66778899,user8@test.com,Usuario Test 8
22334455,user9@test.com,Usuario Test 9
55443322,user10@test.com,Usuario Test 10
EOF

# Generate clinics.csv
echo "Generating clinics.csv..."
cat > "$DATA_DIR/clinics.csv" << 'EOF'
name,email,phone
Clinica Test 1,clinic1@test.com,24001234
Clinica Test 2,clinic2@test.com,24001235
Clinica Test 3,clinic3@test.com,24001236
EOF

# Generate health-workers.csv
echo "Generating health-workers.csv..."
cat > "$DATA_DIR/health-workers.csv" << 'EOF'
ci,clinic_name,specialty,email,name
11111111,Clinica Test 1,Cardiology,worker1@test.com,Profesional Test 1
22222222,Clinica Test 1,Neurology,worker2@test.com,Profesional Test 2
33333333,Clinica Test 2,Internal Medicine,worker3@test.com,Profesional Test 3
44444444,Clinica Test 2,Pediatrics,worker4@test.com,Profesional Test 4
55555555,Clinica Test 3,Cardiology,worker5@test.com,Profesional Test 5
EOF

# Generate documents.csv
echo "Generating documents.csv..."
cat > "$DATA_DIR/documents.csv" << 'EOF'
document_id,health_user_ci,clinic_name
doc-001,12345678,Clinica Test 1
doc-002,87654321,Clinica Test 1
doc-003,11223344,Clinica Test 2
doc-004,44332211,Clinica Test 2
doc-005,55667788,Clinica Test 3
EOF

echo "Test data files generated successfully!"
echo ""
echo "Generated files:"
ls -lh "$DATA_DIR"/*.csv
echo ""
echo "Note: These are template files. You may need to populate the database"
echo "      with actual test data before running performance tests."

