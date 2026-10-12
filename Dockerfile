FROM ubuntu:latest

# El comando copy copia archivos desde el host y los copia dentro de la imagen.
# Necesita dos partes: COPY [que archivo dentro de tu computadora] [que directorio dentro de la imagen]
# Si lo copio a ".", lo estoy copiando a el root de la imagen

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src src

# Primero le inyecto las variables de entorio desde afuera sin que queden guardadas en las capas de ejecucion de docker
# Luego las cacheo
# Despues, corro los tests. el bootjar es la manera de correr todas las depenencias de sprinboot con gradle

RUN --mount=typesecret, id=gradle_properties, target=/root/.gradle/gradle.properties \
    --mount=type=cache, target=/root/.gradle/caches \
    ./gradlew bootjar -no--daemon -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

LABEL authors="ignaciogarcia"

ENTRYPOINT ["top", "-b"]
