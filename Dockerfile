FROM gradle:7-jdk11-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM openjdk:11-jre-slim

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/1.0.jar /app/application.jar

COPY --from=build /home/gradle/src/config.json /app/

COPY --from=build /home/gradle/src/.env /app/

WORKDIR /app

ENTRYPOINT ["java", "-jar", "application.jar"]