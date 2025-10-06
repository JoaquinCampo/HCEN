# syntax=docker/dockerfile:1.4

## ------------------------------------------------------------
## Build stage: compile the multi-module Maven project (EAR)
## ------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy POMs first to leverage layer caching of dependencies
COPY pom.xml ./
COPY ejb/pom.xml ejb/pom.xml
COPY web/pom.xml web/pom.xml
COPY ear/pom.xml ear/pom.xml
COPY console/pom.xml console/pom.xml

# Warm the Maven repository cache
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp -DskipTests dependency:go-offline

# Copy the full source tree and build the EAR
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp -DskipTests clean package

## ------------------------------------------------------------
## Runtime stage: WildFly 31 (JDK 17)
## ------------------------------------------------------------
FROM quay.io/wildfly/wildfly:31.0.1.Final-jdk17 AS runtime

USER root

# Install PostgreSQL JDBC driver as a WildFly module
ARG POSTGRESQL_JDBC_VERSION=42.7.3
ENV POSTGRESQL_JDBC_VERSION=${POSTGRESQL_JDBC_VERSION}
RUN curl -fsSL "https://jdbc.postgresql.org/download/postgresql-${POSTGRESQL_JDBC_VERSION}.jar" -o /tmp/postgresql.jar \
    && mkdir -p "$JBOSS_HOME/modules/system/layers/base/org/postgresql/main" \
    && mv /tmp/postgresql.jar "$JBOSS_HOME/modules/system/layers/base/org/postgresql/main/postgresql.jar"

RUN cat <<'MODULE' > "$JBOSS_HOME/modules/system/layers/base/org/postgresql/main/module.xml"
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="org.postgresql">
    <resources>
        <resource-root path="postgresql.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
    </dependencies>
</module>
MODULE

# Prepare helper scripts
RUN mkdir -p /opt/app
COPY docker/configure-datasource.cli /opt/app/configure-datasource.cli
COPY docker/entrypoint.sh /opt/app/entrypoint.sh
RUN chmod +x /opt/app/entrypoint.sh

# Deploy the EAR artifact built in the first stage
COPY --from=build /workspace/ear/target/practico.ear "$JBOSS_HOME/standalone/deployments/app.ear"

# Drop privileges back to the default WildFly user (uid 1000)
USER 1000

# Expose HTTP port only
EXPOSE 8080

# Document runtime-overridable datasource and management defaults (no secrets baked in)
ENV DB_JNDI=java:/PostgresDS \
    DB_HOST=postgres \
    DB_PORT=5432 \
    DB_NAME=practico_db \
    DB_USER= \
    DB_PASSWORD= \
    WF_ADMIN_USER=admin \
    WF_ADMIN_PASSWORD= \
    APP_USER=admin \
    APP_PASSWORD=admin \
    WILDFLY_CONFIG=standalone-full.xml

# Health check against the application HTTP endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
    CMD curl -fsS http://127.0.0.1:8080/ || exit 1

ENTRYPOINT ["/opt/app/entrypoint.sh"]
