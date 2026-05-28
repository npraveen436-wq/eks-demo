FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S app && adduser -S app -G app
WORKDIR /opt/app

# Datadog Java APM tracer, downloaded at build time.
# 'latest' is convenient for a lab; pin a version from Maven Central for reproducible builds.
ADD https://dtdg.co/latest-java-tracer /opt/app/dd-java-agent.jar

# Spring Boot executable ("fat") jar, still named eks-demo.jar via <finalName>
COPY target/eks-demo.jar app.jar
RUN chown -R app:app /opt/app

USER app
EXPOSE 8080

# -javaagent loads the Datadog tracer; it auto-instruments Spring MVC / embedded Tomcat.
ENTRYPOINT ["java", "-javaagent:/opt/app/dd-java-agent.jar", "-jar", "/opt/app/app.jar"]
