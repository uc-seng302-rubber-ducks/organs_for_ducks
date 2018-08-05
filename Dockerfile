FROM openjdk:8

WORKDIR /server

RUN apt-get update && apt-get install -y --no-install-recommends openjfx && rm -rf /var/lib/apt/lists/*
RUN apt-get update
RUN apt-get install -y maven

COPY . /server

RUN ["mvn", "install", "-DskipTests"]
RUN ["mvn", "package", "-DskipTests"]

EXPOSE 8080
RUN rename -v -e 's/-\d.\d//' server-*.jar
ENTRYPOINT ["java", "-jar","/server/target/server.jar"]