FROM openjdk:8

WORKDIR /server

RUN apt-get update
RUN apt-get install -y maven

COPY . /server

RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify", "-DskipTest"]
RUN ["mvn", "package", "-DskipTest"]

EXPOSE 8080
ENTRYPOINT ["java", "-jar","/server/target/server.jar"]