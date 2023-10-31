FROM openjdk:17-jdk-alpine
WORKDIR /springbootserver
EXPOSE 80
COPY ./target/*.jar springbootserver.jar
CMD ["java", "-jar", "./springbootserver.jar"]