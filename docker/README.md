# TSE Practico Deployment Guide

This repository packages a Jakarta EE multi-module application (EJB, WAR, EAR, console) that targets WildFly 37 on JDK 21. The container workflow builds a single EAR and deploys it with an auto-configured PostgreSQL datasource.

## Prerequisites

- Docker 24+ with BuildKit enabled (`DOCKER_BUILDKIT=1`) and Compose V2
- Internet access to pull base images and the PostgreSQL JDBC driver

## Quick Start

```bash
docker compose -f docker-compose.local.yml up --build
```

This command compiles the EAR (Maven 3.9/JDK 21), brings up PostgreSQL 16, and starts WildFly 37.0.1.Final. The web module becomes available at:

```
http://localhost:8080/practico/
```

Tear everything down (including the Postgres volume) when you are done:

```bash
docker compose -f docker-compose.local.yml down -v
```

## Project Layout

- `Dockerfile` – multi-stage build (Maven → WildFly). Copies the EAR to `standalone/deployments/app.ear`, installs the PostgreSQL JDBC module, exposes port 8080, and adds a health check.
- `docker/entrypoint.sh` – container entrypoint. Creates the management user if needed, starts WildFly, waits for the management API, runs the CLI script below, and tails the server.
- `docker/configure-datasource.cli` – jboss-cli batch that ensures the PostgreSQL driver is registered and recreates the `PracticoDS` datasource from environment variables.
- `docker-compose.local.yml` – local smoke stack: PostgreSQL 16 + app service on port 8080 with health checks and sensible defaults.

## Runtime Environment Variables

All defaults are defined in the runtime image; override them with `docker compose` or `docker run` as needed.

| Variable            | Default               | Description                                                                   |
| ------------------- | --------------------- | ----------------------------------------------------------------------------- |
| `DB_JNDI`           | `java:/PostgresDS`    | JNDI name bound to the datasource (persistence unit uses `java:/PostgresDS`). |
| `DB_HOST`           | `postgres`            | PostgreSQL host name.                                                         |
| `DB_PORT`           | `5432`                | PostgreSQL port.                                                              |
| `DB_NAME`           | `practico_db`         | Database name.                                                                |
| `DB_USER`           | _required_            | Database user (supply via env/secret before starting).                        |
| `DB_PASSWORD`       | _required_            | Database password (supply via env/secret before starting).                    |
| `WF_ADMIN_USER`     | `admin`               | WildFly management username (created automatically).                          |
| `WF_ADMIN_PASSWORD` | _required_            | WildFly management password (inject via secret/env).                          |
| `WILDFLY_CONFIG`    | `standalone-full.xml` | Configuration profile passed to `standalone.sh`.                              |
| `JPA_DDL` / `SEED`  | `drop-and-create` / `true` | Controls schema generation + seeding defaults.                               |

### Local credentials

1. Create an `.env.local` file alongside `docker-compose.local.yml` with at least:
   ```env
   DB_USER=your_db_user
   DB_PASSWORD=your_db_password
   WF_ADMIN_PASSWORD=your_wildfly_admin_password
   ```
2. Export the same variables in your shell if you plan to use `docker-compose.yml` directly.
3. Never commit `.env.local`; the repo `.gitignore` already covers `*.auto.tfvars` but add the env file to your global/local ignore list as needed.

## Using the Application

1. Start the stack as shown above.
2. Wait for the log line `WildFly is ready; following server process` (the first deployment attempt may log a missing datasource before the CLI reloads).
3. Visit `http://localhost:8080/practico/` to use the JSF frontend.
4. Follow logs while testing (optional):
   ```bash
   docker compose -f docker-compose.local.yml logs -f app
   ```

## Management Interface Notes

- The management endpoint listens on `127.0.0.1:9990` _inside_ the container. It is not published to the host for security reasons.
- To run management commands, exec from inside the container:
  ```bash
  docker compose -f docker-compose.local.yml exec app \
    curl -u "${WF_ADMIN_USER:-admin}:${WF_ADMIN_PASSWORD}" \
    http://127.0.0.1:9990/management
  ```
- If you need host access to the admin console, expose port `9990` explicitly and adjust the bind address, but this is discouraged for shared environments.
