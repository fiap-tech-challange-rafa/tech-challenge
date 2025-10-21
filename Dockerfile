# Etapa 1: Build da aplicação (opcional, se for usar jar já buildado)
# FROM maven:3.9-eclipse-temurin-17 AS build
# WORKDIR /app
# COPY . .
# RUN mvn clean package -DskipTests

# Etapa 2: Executar o JAR
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copia o JAR gerado para dentro do container
COPY target/oficina-api.jar app.jar

# Expõe a porta do Spring Boot
EXPOSE 8080

# Comando de execução
ENTRYPOINT ["java", "-jar", "app.jar"]
