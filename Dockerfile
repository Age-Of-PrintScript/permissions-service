# ---------- Etapa 1: compilar ----------
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src src

RUN chmod +x gradlew

# El secret inyecta gradle.properties sin dejarlo guardado en las capas de la imagen.
# El cache conserva las dependencias descargadas entre builds.
# bootJar genera el jar ejecutable con todas las dependencias; -x test omite los tests.

RUN --mount=type=secret,id=gradle_properties,target=/root/.gradle/gradle.properties \
    --mount=type=cache,target=/root/.gradle/caches \
    ./gradlew bootJar --no-daemon -x test

# ---------- Etapa 2: ejecutar ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
