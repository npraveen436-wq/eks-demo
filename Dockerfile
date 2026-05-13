FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S app && adduser -S app -G app
WORKDIR /opt/app

COPY target/eks-demo.jar app.jar
RUN chown -R app:app /opt/app

USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]