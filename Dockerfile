FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} directory-service.jar
ENTRYPOINT ["java","-jar","/directory-service.jar"]