FROM maven:3.6.3-jdk-11-openj9 AS MAVEN3

ARG SPRING_PROFILE

COPY . /tmp/

WORKDIR /tmp

RUN mvn package -Pdev -DskipTests

FROM openjdk:11-jre-slim

COPY --from=MAVEN3 /tmp/checkout-app/target/checkout-app-1.0.0-SNAPSHOT-exec.jar /usr/src/checkout-api/

WORKDIR /usr/src/checkout-api

CMD ["java","-Dspring.profiles.active=${SPRING_PROFILE}", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-Dhibernate.types.print.banner=false", "-jar", "checkout-app-1.0.0-SNAPSHOT-exec.jar"]