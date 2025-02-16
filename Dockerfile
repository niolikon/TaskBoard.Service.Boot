FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/taskboard-service-*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
