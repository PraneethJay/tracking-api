FROM openjdk:17
WORKDIR /app
COPY target/tracking-number-api.jar tracking-number-api.jar
ENTRYPOINT ["java", "-jar", "tracking-number-api.jar"]
