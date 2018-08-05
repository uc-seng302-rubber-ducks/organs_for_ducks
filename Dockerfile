FROM openjdk:8-jdk-alpine
EXPOSE 8080
COPY /serve/target/server-*.jar /home/sengstudent/server
ENTRYPOINT ["java", "-jar","/home/sengstudent/server/server.jar"]