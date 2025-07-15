# === [1단계: 빌드 단계] =========================================
FROM openjdk:17-slim-buster AS build

WORKDIR /workspace/app

COPY gradlew build.gradle settings.gradle ./
COPY ./gradle ./gradle/
COPY ./src ./src/

RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -PexcludeSecrets=true

# === [2단계: 런타임 이미지] ======================================
FROM openjdk:17-slim-buster

WORKDIR /app

# Oracle ADB SSL 인증서 복사
COPY ./adb-ca-cert.crt.pem /app/adb-ca-cert.crt.pem

# 인증서를 truststore에 등록
RUN keytool -import -noprompt \
    -alias oracleadb \
    -keystore /usr/local/openjdk-17/lib/security/cacerts \
    -file /app/adb-ca-cert.crt.pem \
    -storepass changeit

# Spring Boot JAR 복사
COPY --from=build /workspace/app/build/libs/*.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]