FROM openjdk:8-jdk-alpine
WORKDIR /home/sengstudent/
EXPOSE 8080
COPY server/target/server-*.jar server/
ENTRYPOINT ["java", "-jar","/home/sengstudent/server/server.jar"]