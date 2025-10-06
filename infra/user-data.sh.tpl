#!/usr/bin/env bash
set -euxo pipefail

# Update & install Docker + Compose + Cron on Amazon Linux 2023
sudo dnf update -y
sudo dnf install -y docker cronie
sudo systemctl enable --now docker
sudo systemctl enable --now crond
sudo usermod -aG docker ec2-user || true

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# App directory
sudo mkdir -p /opt/practico
sudo chown ec2-user:ec2-user /opt/practico

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

# Create auto-update script that pulls latest image from ECR
cat >/opt/practico/update.sh <<'SCRIPT'
#!/bin/bash
set -e
cd /opt/practico

# Log to file
exec >> /var/log/docker-update.log 2>&1
echo "=== $(date) - Checking for updates ==="

# Login to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${app_image_uri%%/*}

# Pull latest image
docker-compose pull app

# Check if image changed
if docker-compose up -d app 2>&1 | grep -q "Recreating\|Starting"; then
  echo "Updated to new image"
  docker image prune -f
else
  echo "Already up to date"
fi
SCRIPT

chmod +x /opt/practico/update.sh
chown ec2-user:ec2-user /opt/practico/update.sh

# Add cron job to check for updates every 5 minutes
(crontab -u ec2-user -l 2>/dev/null || true; echo "*/5 * * * * /opt/practico/update.sh") | crontab -u ec2-user -

# Create log file with proper permissions
touch /var/log/docker-update.log
chown ec2-user:ec2-user /var/log/docker-update.log
