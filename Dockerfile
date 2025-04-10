
# Versão default de Java, caso não especificada
ARG JAVA_VERSION=24

# Etapa 1: Build
FROM eclipse-temurin:${JAVA_VERSION}-jdk AS build

WORKDIR /app

COPY . .

RUN ./mvnw package

# Etapa 2: Execução
FROM eclipse-temurin:${JAVA_VERSION}-jre
WORKDIR /app

COPY --from=build /app/target/tcc-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
