# TSE Practico Deployment

This repository contains a Jakarta EE application packaged as a WildFly 37 container. It ships with Terraform infrastructure (AWS EC2 + ECR) and a GitHub Actions workflow that builds, pushes and redeploys the container on pushes to `main`.

## Local Development
- Build/test: `docker compose -f docker-compose.local.yml up --build`
- Provide secrets by creating `.env` (gitignored) with `DB_USER`, `DB_PASSWORD`, and `WF_ADMIN_PASSWORD` before running the compose stack.
- Tear down: `docker compose -f docker-compose.local.yml down -v`

## AWS Infrastructure
Infrastructure lives in `infra/`:
1. Copy `infra/terraform.auto.tfvars.example` to `infra/terraform.auto.tfvars` and fill in real values (AWS region, SSH public key, DB credentials, WildFly admin credentials). The file stays local.
2. Authenticate with AWS (`aws configure` or env vars) and run:
   ```bash
   cd infra
   terraform init
   terraform apply
   ```
   Terraform creates a VPC, security group, public subnet, EC2 instance (Amazon Linux 2023 with Docker + Compose), and an Amazon ECR repository. Outputs include the public IP/DNS of the instance and the ECR repository URL.

## CI/CD Pipeline
GitHub Actions workflow: `.github/workflows/deploy.yml`. It triggers on pushes to `main` and performs:
1. Build the Docker image from the repo.
2. Authenticate against ECR (`aws ecr get-login-password`).
3. Push `${ecr_repository_url}:<tag>` (commit SHA + latest).
4. SSH into the EC2 host and run `docker compose pull && docker compose up -d`.

### Required GitHub Secrets
Set these repository secrets before pushing to `main`:
- `AWS_ACCESS_KEY_ID` – IAM user/role credential with ECR, EC2, and VPC permissions.
- `AWS_SECRET_ACCESS_KEY` – matching secret key.
- `EC2_HOST` – public DNS or IP of the provisioned EC2 instance (from Terraform output).
- `EC2_SSH_KEY` – the private SSH key matching the public key provided to Terraform (PEM format). The workflow uses it to SSH into the instance.

Optional: if you prefer a different AWS region/account, update `AWS_REGION` and `ECR_REPOSITORY` in the workflow env section.

## Manual Deployment Check
- Build and push locally:
  ```bash
  aws ecr get-login-password --region sa-east-1 | docker login --username AWS --password-stdin <account>.dkr.ecr.sa-east-1.amazonaws.com
  docker build -t <account>.dkr.ecr.sa-east-1.amazonaws.com/tse-lab-app:latest .
  docker push <account>.dkr.ecr.sa-east-1.amazonaws.com/tse-lab-app:latest
  ```
- SSH to EC2 (`ssh -i <private-key> ec2-user@<public-ip>`), then:
  ```bash
  cd /opt/practico
  sudo docker compose pull
  sudo docker compose up -d
  ```
- Access the app at `http://<public-ip>/practico/`.

## Repository Structure
- `Dockerfile`, `docker/`, `docker-compose.local.yml` – container build/run logic.
- `infra/` – Terraform definitions for AWS resources.
- `.github/workflows/deploy.yml` – CI/CD pipeline.

## Notes
- Database and WildFly credentials should be managed via Terraform variables and never committed to the repo.
- The security group currently opens SSH (22) and HTTP (80) to the world for demo purposes. Lock it down before production use.
- Consider moving Terraform state to a remote backend (S3 + DynamoDB) if multiple users or automation will manage the infrastructure.
