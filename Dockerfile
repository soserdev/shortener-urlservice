# syntax=docker/dockerfile:experimental
FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app
COPY mvnw .
COPY .mvn .mvn
COPY src src
COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 ./mvnw package -DskipTests
RUN java -Djarmode=layertools -jar target/jumper-urlservice-0.0.1-SNAPSHOT.jar extract --destination target/extracted


FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S demo && adduser -S demo -G demo
VOLUME /tmp
USER demo
ARG EXTRACTED=/workspace/app/target/extracted
WORKDIR application
COPY --from=build ${EXTRACTED}/dependencies/ ./
COPY --from=build ${EXTRACTED}/spring-boot-loader/ ./
COPY --from=build ${EXTRACTED}/snapshot-dependencies/ ./
COPY --from=build ${EXTRACTED}/application/ ./
ENTRYPOINT ["java","-noverify","-XX:TieredStopAtLevel=1","-Dspring.main.lazy-initialization=true","org.springframework.boot.loader.launch.JarLauncher"]
