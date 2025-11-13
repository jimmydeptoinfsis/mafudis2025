# Etapa 1: Compilación
# Utiliza una imagen de Maven con Java 17
FROM maven:3.8.3-openjdk-17 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de configuración de Maven
COPY pom.xml .

# Copia el código fuente
COPY src ./src

# Compila la aplicación y genera el archivo JAR
RUN mvn clean package -DskipTests

# Etapa 2: Construcción de la imagen final
# Utiliza una imagen ligera de OpenJDK con Java 17
FROM openjdk:17-jre-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR desde la etapa de compilación
COPY --from=build /app/target/smdfdis-0.0.1-SNAPSHOT.jar /app/app.jar

# Expone el puerto por defecto de Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
