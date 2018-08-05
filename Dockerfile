FROM openjdk:8-jdk-alpine
WORKDIR .
EXPOSE 8080
COPY server/target/server-*.jar /home/sengstudent/server
ENTRYPOINT ["java", "-jar","/home/sengstudent/server/server.jar"]