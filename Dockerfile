FROM eclipse-temurin:23-jre-alpine
WORKDIR /opt/app
COPY build/libs/springapp-1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
