FROM openjdk:11-jdk-slim AS builder
WORKDIR /app

COPY gradlew /app
COPY gradle /app/gradle

COPY build.gradle settings.gradle gradle.properties /app/
RUN ./gradlew build || return 0

COPY . .
RUN ./gradlew build
RUN mv /app/build/libs/backend-1.0-SNAPSHOT.jar /app/build/libs/backend.jar

FROM openjdk:11-jdk-slim as runner

WORKDIR /app

COPY --from=builder /app/build/libs/backend.jar .
COPY --from=builder /app/build/resources/main/. .

EXPOSE 8080
RUN ["chmod", "+x", "backend.jar"]
CMD ["java","-jar", "backend.jar"]