FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
# Устанавливаем curl для healthcheck
RUN apk add --no-cache curl
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]