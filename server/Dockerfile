FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} /home/sengstudent/server/server-0.0.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/home/sengstudent/server/server-0.0.jar"]