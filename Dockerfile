# ==============================
# Stage 1: Build the application
# ==============================
FROM eclipse-temurin:22-jdk-alpine AS builder
WORKDIR /app

# 1. Copy Maven wrapper and configuration files first
# This allows Docker to cache the dependencies layer if pom.xml hasn't changed
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

# Download all dependencies offline (improves caching compared to simple 'resolve')
RUN ./mvnw dependency:go-offline

# 2. Copy source code and build the application
COPY src ./src
RUN ./mvnw clean package -DskipTests \
    -Dmaven.compiler.debug=false \
    -Dmaven.compiler.debuglevel=none

# 3. Extract Layers (Spring Boot Layered Jar feature)
# This separates dependencies from application code for optimized Docker caching
RUN java -Djarmode=layertools -jar target/*.jar extract

# ==============================
# Stage 2: Runtime Environment
# ==============================
FROM eclipse-temurin:22-jre-alpine

# Create a non-root user for security purposes
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

# 4. Copy extracted layers from the builder stage
# The order matters: dependencies are copied first because they change less frequently
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

# Create logs directory and set permissions
RUN mkdir -p /app/logs && chown -R spring:spring /app

# Switch to the non-root user
USER spring

# Expose the application port
EXPOSE 4000
ENV SERVER_PORT=4000

# JVM Configuration
# -XX:MaxRAMPercentage=75.0: Adapts heap size to 75% of the container's available memory
# -XX:+UseContainerSupport: Ensures JVM recognizes container resource limits
ENV JVM_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dspring.profiles.active=prod"

# Application Health Check
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Start the application using JarLauncher
# JarLauncher is optimized for handling layered jars (starts faster than standard java -jar)
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]