FROM openjdk:11-jre-slim
VOLUME /tmp
# EXPOSE 8082
RUN mkdir -p /app/
RUN mkdir -p /app/logs/
ADD target/*.jar /app/app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
