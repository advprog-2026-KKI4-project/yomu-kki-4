FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-javaagent:/otel/opentelemetry-javaagent.jar"
ENTRYPOINT ["java", "-jar", "app.jar"]
