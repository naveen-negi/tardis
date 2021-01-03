FROM openjdk:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/tardis-0.0.1-SNAPSHOT-standalone.jar /tardis/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/tardis/app.jar"]
