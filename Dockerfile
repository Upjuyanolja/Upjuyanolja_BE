FROM openjdk:17-jdk-slim

# Dependencies
RUN apt-get update && apt-get install -y mariadb-client

# Application
ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} app.jar

# Environment variables
ENV USE_PROFILE prod

ENTRYPOINT ["java", "-Dspring.profiles.active=${USE_PROFILE}", "-jar", "app.jar"]
