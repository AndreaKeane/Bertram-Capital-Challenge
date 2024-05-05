FROM eclipse-temurin
MAINTAINER Andrea Keane <a.living.keane@gmail.com>

WORKDIR /src
COPY ./target/demo-0.0.1-SNAPSHOT.jar /src
COPY /src/main/resources/demo_tab.json src/main/resources/demo_tab.json

EXPOSE 8080

CMD ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]