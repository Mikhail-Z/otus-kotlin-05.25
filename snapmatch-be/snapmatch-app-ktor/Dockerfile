# Build stage
FROM gradle:8.10-jdk21 AS build
WORKDIR /app

# 1. Скопируйте только корневые файлы конфигурации
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle

# 2. **Предварительно скачайте зависимости для ВСЕХ проектов**
# Мы используем фиктивный таск 'dependencies' или 'assemble' для этого.
# Если эти файлы не меняются, этот слой будет кэширован Docker'ом.
RUN gradle assemble --no-daemon

# 3. Теперь скопируйте исходный код ваших подпроектов
COPY snapmatch-be ./snapmatch-be
COPY build-logic ./build-logic
COPY specs ./specs

# 4. Соберите приложение (теперь зависимости уже в кэше)
RUN gradle :snapmatch-be:snapmatch-app-ktor:build -x test --no-daemon

# --- Runtime stage (остается без изменений) ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built jar
COPY --from=build /app/snapmatch-be/snapmatch-app-ktor/build/libs/*-all.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
