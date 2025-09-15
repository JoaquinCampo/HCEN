# syntax=docker/dockerfile:1

# ------------------------------------------------------------
# Build stage: compile the multi-module Maven project (EAR)
# ------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy POMs first to leverage layer caching of dependencies
COPY pom.xml ./
COPY ejb/pom.xml ejb/pom.xml
COPY web/pom.xml web/pom.xml
COPY ear/pom.xml ear/pom.xml
COPY console/pom.xml console/pom.xml

# Pre-fetch dependencies for faster repeated builds
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp -Dmaven.compiler.release=21 -DskipTests dependency:go-offline

# Copy the full source and build
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp -Dmaven.compiler.release=21 -DskipTests clean package

# ------------------------------------------------------------
# Runtime stage: run on WildFly (EE 10, JDK 21)
# ------------------------------------------------------------
FROM quay.io/wildfly/wildfly:37.0.1.Final-jdk21

# Prepare writable directories for arbitrary UID in OpenShift
USER root
RUN mkdir -p "$JBOSS_HOME/standalone/deployments" \
    && chown -R 1001:0 "$JBOSS_HOME" \
    && chmod -R g+rwX "$JBOSS_HOME"
USER 1001

# Deploy the built EAR
COPY --from=build /workspace/ear/target/practico.ear "$JBOSS_HOME/standalone/deployments/practico.ear"
RUN /bin/sh -c 'touch "$JBOSS_HOME/standalone/deployments/practico.ear.dodeploy"'

# The image exposes 8080 by default; re-declare for clarity
EXPOSE 8080

# Ensure the server binds to all interfaces
ENV JAVA_OPTS="-Djboss.bind.address=0.0.0.0 -Djboss.bind.address.management=0.0.0.0"

# Entrypoint/CMD provided by the base image (standalone.sh)


