FROM openjdk:8

WORKDIR /server

RUN apt-get update && apt-get install -y --no-install-recommends openjfx && rm -rf /var/lib/apt/lists/*
RUN apt-get update
RUN apt-get install -y maven

COPY . /server

RUN ["mvn", "install", "-DskipTest"]
RUN ["mvn", "package", "-DskipTest"]

EXPOSE 8080
ENTRYPOINT ["java", "-jar","/server/target/server.jar"]