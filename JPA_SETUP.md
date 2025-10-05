### Step 1: Start PostgreSQL

```bash
# Start PostgreSQL with Docker
docker-compose up -d

# Verify it's running
docker ps
```

### Step 2: Configure WildFly

```bash
# Download PostgreSQL JDBC driver
wget https://jdbc.postgresql.org/download/postgresql-42.7.3.jar -O /tmp/postgresql-42.7.3.jar

Then move the file to wildfly-37.0.0.Final/standalone/deployments/

# Run WildFly configuration
$WILDFLY_HOME/bin/jboss-cli.sh --connect --file=wildfly-postgresql-setup.cli
```