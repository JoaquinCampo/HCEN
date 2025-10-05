#!/usr/bin/env bash

# This entrypoint configures WildFly on startup before handing control back to the server.
# Recognised environment variables:
#   DB_JNDI (default java:/PostgresDS)      - JNDI name bound to the datasource
#   DB_HOST (default postgres)              - PostgreSQL host
#   DB_PORT (default 5432)                  - PostgreSQL port
#   DB_NAME (default practico_db)           - PostgreSQL database name
#   DB_USER (required)                      - PostgreSQL username
#   DB_PASSWORD (required)                  - PostgreSQL password
#   WF_ADMIN_USER (default admin)           - WildFly management user used by the CLI
#   WF_ADMIN_PASSWORD (required)               - WildFly management password
#   WILDFLY_CONFIG (default standalone-full.xml) - Server configuration file
#   JPA_DDL / SEED - forwarded to the application as-is (no special handling here)

set -euo pipefail

DB_JNDI=${DB_JNDI:-java:/PostgresDS}
DB_HOST=${DB_HOST:-postgres}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-practico_db}
DB_USER=${DB_USER:?DB_USER is required}
DB_PASSWORD=${DB_PASSWORD:?DB_PASSWORD is required}
WF_ADMIN_USER=${WF_ADMIN_USER:-admin}
WF_ADMIN_PASSWORD=${WF_ADMIN_PASSWORD:?WF_ADMIN_PASSWORD is required}
WILDFLY_CONFIG=${WILDFLY_CONFIG:-standalone-full.xml}

MGMT_USERS_FILE="$JBOSS_HOME/standalone/configuration/mgmt-users.properties"

ensure_mgmt_user() {
  if [[ -f "$MGMT_USERS_FILE" ]] && grep -q "^${WF_ADMIN_USER}=" "$MGMT_USERS_FILE"; then
    return
  fi
  echo "Creating WildFly management user '${WF_ADMIN_USER}'"
  "$JBOSS_HOME/bin/add-user.sh" --silent --user "$WF_ADMIN_USER" --password "$WF_ADMIN_PASSWORD"
}

start_server() {
  echo "Starting WildFly using $WILDFLY_CONFIG"
  "$JBOSS_HOME/bin/standalone.sh" -c "$WILDFLY_CONFIG" -b 0.0.0.0 -bmanagement 127.0.0.1 "$@" &
  SERVER_PID=$!
  export SERVER_PID
}

wait_for_management() {
  local attempt=0
  echo "Waiting for WildFly management interface on 127.0.0.1:9990"
  until curl -s -u "${WF_ADMIN_USER}:${WF_ADMIN_PASSWORD}" -o /dev/null "http://127.0.0.1:9990/management"; do
    attempt=$((attempt + 1))
    if (( attempt > 60 )); then
      echo "Management interface did not become ready in time" >&2
      return 1
    fi
    sleep 2
  done
  echo "Management interface is up"
}

configure_datasource() {
  echo "Configuring PostgreSQL datasource via CLI"
  # Export variables so the CLI script uses the current values
  export DB_JNDI DB_HOST DB_PORT DB_NAME DB_USER DB_PASSWORD
  "$JBOSS_HOME/bin/jboss-cli.sh" \
    --connect \
    --controller=127.0.0.1:9990 \
    --user="$WF_ADMIN_USER" \
    --password="$WF_ADMIN_PASSWORD" \
    --file=/opt/app/configure-datasource.cli
  echo "Datasource configuration applied"
}

shutdown_server() {
  if [[ -n "${SERVER_PID:-}" ]]; then
    echo "Shutting down WildFly (PID $SERVER_PID)"
    kill -TERM "$SERVER_PID" 2>/dev/null || true
    wait "$SERVER_PID" 2>/dev/null || true
  fi
}

trap shutdown_server INT TERM

ensure_mgmt_user
start_server "$@"
wait_for_management
configure_datasource

# Wait for reload triggered by the CLI
wait_for_management

echo "WildFly is ready; following server process"
wait "$SERVER_PID"
