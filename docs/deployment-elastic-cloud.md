# Elastic Cloud Deployment Runbook

This guide captures the steps we followed to move the application into the Elastic Cloud Docker host (`node5899-hcengrupo12`) and work around its restrictions.

## 1. Build & Push the WildFly Image Locally

- Ensure Docker Buildx is available (Docker Desktop ships with it).
- Create/activate a dedicated builder with multi-arch support:
  ```
  docker buildx create --name hcen-amd64 --use --driver docker-container
  docker buildx inspect --bootstrap
  ```
- Build the WildFly image for `linux/amd64` and push directly to ECR:
  ```
  docker buildx build \
    --platform linux/amd64 \
    -t 879753557246.dkr.ecr.us-east-2.amazonaws.com/tse/hcen:latest \
    --push .
  ```
- Optional: verify the manifest lists `linux/amd64` so the Elastic node can pull it:
  ```
  docker buildx imagetools inspect 879753557246.dkr.ecr.us-east-2.amazonaws.com/tse/hcen:latest
  ```

## 2. Update Environment Secrets

- Confirm `.env` contains the database and app credentials that Compose expects. If the file is missing on the server (rsync often skips dotfiles), recreate it from `.env.example`.

## 3. Prepare the Elastic Cloud Host

- Log in to the host via SSH.
- Authenticate Docker against ECR using an AWS profile with push/pull permissions:
  ```
  aws ecr get-login-password --region us-east-2 \
    | docker login --username AWS --password-stdin 879753557246.dkr.ecr.us-east-2.amazonaws.com
  ```
- Pull the latest image:
  ```
  docker compose pull
  ```

## 4. Start the Stack

- Bring the services up:
  ```
  docker compose up -d
  ```
- The compose file now references the prebuilt image (`image: 879753557246.dkr.ecr.us-east-2.amazonaws.com/tse/hcen:latest`) and sets
  ```
  security_opt:
    - seccomp=unconfined
  ```
  on Postgres so `initdb` can spawn helper processes under the Elastic kernel profile.

## 5. Monitor Health

- Tail logs during startup:
  ```
  docker compose logs -f postgres
  docker compose logs -f app
  ```
- Once Postgres reports `database system is ready to accept connections`, the app container exposes WildFly on port `8080`.

## 6. Handy Commands

- Stop and remove the stack:
  ```
  docker compose down
  ```
- Refresh the app image after a new push:
  ```
  docker compose pull app && docker compose up -d app
  ```
