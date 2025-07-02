FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew clean build -x test

# Final stage: use build arg to select which service JAR to copy
FROM eclipse-temurin:17-jdk
WORKDIR /app
ARG SERVICE_NAME
COPY --from=build /workspace/${SERVICE_NAME}/build/libs/${SERVICE_NAME}-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]