# 1. Etapa de compilación con Maven y Java
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el archivo de dependencias y el código fuente
COPY pom.xml .
COPY src ./src

# Compilamos el ejecutable .jar saltando pruebas
RUN mvn clean package -DskipTests

# 2. Etapa de ejecución (ligera y optimizada)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiamos el archivo compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto por defecto
EXPOSE 8080

# Comando de arranque de Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
