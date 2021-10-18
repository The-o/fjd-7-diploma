FROM gradle:jdk11 AS build

WORKDIR /build

COPY build.gradle settings.gradle ./
COPY src/main src/main
RUN gradle --no-daemon bootJar


FROM openjdk:11-jre-slim

ARG PROJECT=fjd-7-diploma
ARG VERSION=0.1.0
WORKDIR /app
COPY --from=build /build/build/libs/${PROJECT}-${VERSION}.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]