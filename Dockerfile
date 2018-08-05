FROM openjdk:8-jdk-alpine
WORKDIR /home/gitlab-runner/15f00793/0/seng302-2018/team-100
EXPOSE 8080
COPY server/target/server-*.jar /home/sengstudent/server
ENTRYPOINT ["java", "-jar","/home/sengstudent/server/server.jar"]