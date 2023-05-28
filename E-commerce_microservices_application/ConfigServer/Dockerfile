FROM openjdk:8

ARG JAR_FILE=target/ConfigServer-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} configserver.jar

ENTRYPOINT ["java", "-jar", "/configserver.jar"]

EXPOSE 9296