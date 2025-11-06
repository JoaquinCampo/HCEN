#!/bin/bash

BASE_URL=http://localhost:8080/api

echo "Creating access request..."

curl -X POST "$BASE_URL/access-requests" \
  -H "Content-Type: application/json" \
  -u "admin:admin" \
  -d '{
    "healthUserCi": "54053584",
    "healthWorkerCi": "52800804",
    "clinicName": "Clínica Central"
  }'

echo -e "\nAccess request creation completed."