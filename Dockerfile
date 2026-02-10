# Build stage
FROM amazoncorretto:21-slim AS builder
WORKDIR /build

# 의존성 복사(캐싱)
COPY gradle/wrapper gradle/wrapper
COPY gradlew .
COPY build.gradle settings.gradle ./

# 2. 의존성 다운로드(캐싱) - build.gradle 변경시에만 재빌드
RUN ./gradlew dependencies --no-daemon

# 3. 소스 코드 복사 및 빌드
COPY src src

# 4. 빌드 - JAR를 레이어로 분리
RUN ./gradlew bootJar -x test --no-daemon && \
    java -Djarmode=layertools -jar build/libs/*.jar extract

# Runtime stage
FROM amazoncorretto:21-slim
WORKDIR /app

# 1. 보안: root 사용자 대신 전용 유저 생성
RUN addgroup --system spring && adduser --system --group spring
USER spring:spring

# JAR 레이어 복사
COPY jar-layers/dependencies/ .
COPY jar-layers/spring-boot-loader/ ./
COPY jar-layers/snapshot-dependencies/ ./
COPY jar-layers/application/ ./

# 3. 포트 노출
EXPOSE 8080

# 4. 실행
ENTRYPOINT ["java", "-jar", "app.jar"]