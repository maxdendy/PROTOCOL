FROM openjdk:21
MAINTAINER mxdndy
COPY PROTOCOL-1.0-jar-with-dependencies.jar ./PROTOCOL.jar
CMD ["java", "-jar", "PROTOCOL.jar"]