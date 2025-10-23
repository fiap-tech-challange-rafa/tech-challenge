# Etapa 1: construir o projeto
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: criar a imagem final
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Define o profile ativo (pode ser sobrescrito no docker-compose)
ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8080

# Comando de inicialização
ENTRYPOINT ["java","-jar","app.jar"]
