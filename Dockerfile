FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew clean build -x test

FROM eclipse-temurin:17-jdk
WORKDIR /app
ARG SERVICE_NAME
# Copy the fat JAR (excludes the -plain.jar file)
COPY --from=build /workspace/${SERVICE_NAME}/build/libs/${SERVICE_NAME}-*-SNAPSHOT.jar app.jar

# Use a non-root user for security (optional but recommended)
RUN useradd -ms /bin/bash appuser

# Create logs directory with proper permissions
RUN mkdir -p /app/logs && chown -R appuser:appuser /app/logs

USER appuser

EXPOSE 8080

# Healthcheck (optional, for orchestrators)
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s CMD curl --fail http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]