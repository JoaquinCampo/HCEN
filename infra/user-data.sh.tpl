#!/usr/bin/env bash
set -euxo pipefail

# Update & install Docker + Compose on Ubuntu
export DEBIAN_FRONTEND=noninteractive
sudo apt-get update -y
sudo apt-get install -y docker.io docker-compose-plugin
sudo systemctl enable --now docker
sudo usermod -aG docker ubuntu || true

# App directory
sudo mkdir -p /opt/practico
sudo chown ubuntu:ubuntu /opt/practico

# Compose file pointing to the application image hosted in Amazon ECR
cat >/opt/practico/docker-compose.yml <<'YAML'
services:
  app:
    image: ${app_image_uri}:latest
    ports: ["80:8080"]
    env_file: .env
    depends_on: [postgres]
    restart: unless-stopped

  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: practico_db
      POSTGRES_USER: "${db_username}"
      POSTGRES_PASSWORD: "${db_password}"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U $${POSTGRES_USER} -d $${POSTGRES_DB}"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

volumes:
  postgres-data:
YAML

# Default envs (the app reads these)
cat >/opt/practico/.env <<'ENV'
DB_HOST=postgres
DB_PORT=5432
DB_NAME=practico_db
DB_USER="${db_username}"
DB_PASSWORD="${db_password}"
JPA_DDL=drop-and-create
SEED=true
WF_ADMIN_USER="${wf_admin_user}"
WF_ADMIN_PASSWORD="${wf_admin_password}"
ENV
