#!/bin/bash

BASE_URL=http://localhost:8080/api

echo "Creating health user..."

curl -X POST "$BASE_URL/health-users" \
  -H "Content-Type: application/json" \
  -u "admin:admin" \
  -d '{
    "ci": "54053584",
    "firstName": "Francisco",
    "lastName": "Simonelli",
    "gender": "MALE",
    "email": "francisco.simonelli@example.com",
    "phone": "+59899123456",
    "address": "Calle 18 de Julio 1234, Montevideo",
    "dateOfBirth": "1990-05-15",
    "clinicNames": ["Clínica Central"]
  }'

echo -e "\nHealth user creation completed."