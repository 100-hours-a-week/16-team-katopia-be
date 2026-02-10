# Build stage - cicd 과정에서 build

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
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]