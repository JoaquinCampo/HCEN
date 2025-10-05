# Terraform Infrastructure Overview

This module provisions the AWS resources required to host the Practico application.

## Resources

- **VPC stack** (`vpc.tf`): VPC, public subnet, internet gateway, route table, and security group (`${var.project_name}-sg`) exposing HTTP/SSH.
- **ECR repository** (`ecr.tf`): `aws_ecr_repository.app` is used by the CI pipeline to push the WildFly image. Image scanning is enabled.
- **EC2 instance** (`ec2.tf`): Amazon Linux 2023 host with Docker + Compose preinstalled. User data renders `/opt/practico/docker-compose.yml` referencing the ECR image.

## Inputs

Set values in `terraform.tfvars` (or via CLI/CI variables). For local work you can copy `terraform.auto.tfvars.example` to `terraform.auto.tfvars` and fill in the secrets. Required keys:

- `project_name` – prefix for resource names.
- `region` – AWS region (must match the ECR/EC2 deployment region).
- `public_ssh_key` – SSH public key for instance access.
- `instance_type` – EC2 size (defaults to `t3.small`).
- `db_username` / `db_password` – database credentials (mark as sensitive in Terraform or pass through `TF_VAR_` environment variables).
- `wf_admin_user` / `wf_admin_password` – WildFly management credentials (password should be supplied via CI secrets).

## Outputs

After `terraform apply`, capture:

- `public_ip` / `public_dns` – reach the EC2 host.
- `ecr_repository_url` – image URI for CI/CD (e.g., `123456789012.dkr.ecr.sa-east-1.amazonaws.com/tse-lab-app`). Tag this with the version you push.

## Bootstrap Flow

1. Copy `terraform.auto.tfvars.example` to `terraform.auto.tfvars` and replace placeholder values (this file is ignored by git). Alternatively export the variables as `TF_VAR_*` in your shell/CI.
2. Ensure AWS credentials with permissions for VPC, EC2, IAM key pairs, and ECR are available (`aws configure` or environment variables that Terraform can read).
3. Run `terraform init` then `terraform apply` from the `infra/` directory.
4. The EC2 user data installs Docker + Compose and writes `/opt/practico/docker-compose.yml` using `ecr_repository_url:latest` (services have `restart: unless-stopped` so they survive host restarts once started).
5. Your CI pipeline should:
   - Build the WildFly image.
   - Authenticate to ECR (`aws ecr get-login-password`).
   - Push `${ecr_repository_url}:<tag>`.
   - SSH (or SSM) into the instance to `docker compose pull && docker compose up -d`.

Adjust the security group or parameterize credentials before production use (sensitive values should come from GitLab/GitHub secrets or a local git-ignored tfvars file).
