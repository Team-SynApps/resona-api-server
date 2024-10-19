# OpenJDK 17.0.1의 slim 버전을 기반으로 빌드 스테이지를 설정합니다.
FROM openjdk:17.0.1-jdk-slim as build

# 작업 디렉토리를 /workspace/app으로 설정합니다.
WORKDIR /workspace/app

# gradlew 및 build 관련 파일들을 복사합니다.
COPY gradlew build.gradle settings.gradle ./
COPY ./gradle ./gradle/

# 소스 코드를 복사합니다.
COPY ./src ./src/

# gradlew를 사용하여 bootJar 작업을 실행하여 Spring Boot JAR 파일을 생성합니다.
RUN ./gradlew bootJar

# 런타임용 이미지로 slim 버전을 다시 사용합니다.
FROM openjdk:17.0.1-jdk-slim

# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일을 복사합니다.
COPY --from=build /workspace/app/build/libs/*.jar /app/app.jar

# 컨테이너가 8080 포트에서 통신하도록 설정합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 app.jar를 실행합니다.
CMD ["java", "-jar", "/app/app.jar"]
