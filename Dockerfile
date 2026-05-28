FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S app && adduser -S app -G app
WORKDIR /opt/app

# Datadog Java APM tracer, downloaded at build time.
# 'latest' is convenient for a lab; pin to a version (e.g. .../dd-java-agent/1.42.0/dd-java-agent-1.42.0.jar)
# from Maven Central for reproducible builds in real environments.
ADD https://dtdg.co/latest-java-tracer /opt/app/dd-java-agent.jar

COPY target/eks-demo.jar app.jar
RUN chown -R app:app /opt/app

USER app
EXPOSE 8080

# -javaagent loads the Datadog tracer into the JVM before the app starts.
ENTRYPOINT ["java", "-javaagent:/opt/app/dd-java-agent.jar", "-jar", "/opt/app/app.jar"]
