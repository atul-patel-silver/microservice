#Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim

#Information around who maintains the image
MAINTAINER atul.com

# Add the application's jar to the image
COPY target/Account-0.0.1-SNAPSHOT.jar Account-0.0.1-SNAPSHOT.jar

# execute the application
ENTRYPOINT ["java", "-jar", "Account-0.0.1-SNAPSHOT.jar"]

RUN apt-get update && apt-get install -y postgresql-client