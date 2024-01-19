FROM openjdk:21
ADD PROTOCOL-0.2.0-a..jar PROTOCOL-0.1.jar

ENTRYPOINT ["java", "-jar", "PROTOCOL-0.1.jar"]