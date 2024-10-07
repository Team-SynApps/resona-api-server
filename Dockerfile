# OpenJDK 17.0.1의 slim 버전을 기반 이미지로 사용합니다.
FROM openjdk:17.0.1-jdk-slim

# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 현재 디렉토리의 gradlew, build.gradle, settings.gradle 파일을 /app 디렉토리에 복사합니다.
# 현재 디렉토리의 gradle 폴더를 /app 디렉토리에 복사합니다.
# 현재 디렉토리의 src 폴더를 /app 디렉토리에 복사합니다.
COPY gradlew build.gradle settings.gradle ./
COPY ./gradle ./gradle/
COPY ./src ./src/

# gradlew를 사용하여 bootJar 작업을 실행하여 Spring Boot JAR 파일을 생성합니다.
RUN ./gradlew bootJar
#RUN chmod +x /app/build/libs/ReFit-0.0.1-SNAPSHOT.jar

# 컨테이너가 8080 포트에서 통신하도록 설정합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 app.jar를 실행합니다.
CMD ["java", "-jar", "/app/build/libs/api-0.0.1-SNAPSHOT.jar"]