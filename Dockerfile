# === [1단계: 빌드 단계] =========================================
FROM openjdk:17-slim-buster AS build
WORKDIR /workspace/app

COPY gradlew build.gradle settings.gradle ./
COPY ./gradle ./gradle/

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew :application:clean :application:bootJar

# === [2단계: 런타임 이미지] ======================================
FROM openjdk:17-slim-buster

WORKDIR /app

COPY --from=build /workspace/app/application/build/libs/*.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]