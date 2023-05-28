FROM openjdk:8

ARG JAR_FILE=target/CloudGateway-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} cloudgateway.jar

ENTRYPOINT ["java", "-jar", "/cloudgateway.jar"]

EXPOSE 9090