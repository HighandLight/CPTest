FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY build/libs/CPTest-0.0.1-SNAPSHOT.jar app.jar

HEALTHCHECK --interval=10s --timeout=3s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]