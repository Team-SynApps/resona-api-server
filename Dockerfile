# 빌드 스테이지
FROM eclipse-temurin:17-jdk as build

WORKDIR /workspace/app

# 먼저 gradle 파일들만 복사하여 종속성을 다운로드합니다.
# 이렇게 하면 소스 코드가 변경되어도 종속성 레이어를 재사용할 수 있습니다.
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 종속성을 다운로드합니다.
RUN ./gradlew dependencies

# 소스 코드를 복사합니다.
COPY src src

# 애플리케이션을 빌드합니다.
RUN ./gradlew bootJar
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

# 런타임 스테이지
FROM eclipse-temurin:17-jre

VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency

# 애플리케이션 파일을 복사합니다.
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# 8080 포트를 노출합니다.
EXPOSE 8080

# 애플리케이션을 실행합니다.
ENTRYPOINT ["java","-cp","app:app/lib/*","synapps.resona.api.ResonaAPIServer"]