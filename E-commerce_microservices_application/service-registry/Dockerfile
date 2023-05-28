FROM openjdk:8

ARG JAR_FILE=target/service-registry-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} serviceregistry.jar

ENTRYPOINT ["java", "-jar", "/serviceregistry.jar"]

EXPOSE 8761