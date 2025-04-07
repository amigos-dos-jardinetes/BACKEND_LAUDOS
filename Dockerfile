# Etapa 1: Build
FROM eclipse-temurin:22-jdk AS build
WORKDIR /app

COPY . .

RUN ./mvnw package

# Etapa 2: Execução
FROM eclipse-temurin:22-jre
WORKDIR /app

COPY --from=build /app/target/tcc-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
