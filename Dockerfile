FROM maven:3.9-eclipse-temurin-21 AS build
MAINTAINER Andrea Keane <a.living.keane@gmail.com>

WORKDIR /app

COPY README.md pom.xml ./
COPY src src

RUN mvn package


FROM eclipse-temurin:21-jre AS app

WORKDIR /app

COPY --from=build /app/src/main/resources/ /app/src/main/resources/
COPY --from=build /app/target/*.jar ./app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]