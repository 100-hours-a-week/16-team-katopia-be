
# FitCheck – Local & Deployment Guide

## 1. Health Check (Actuator)

### 기본 헬스체크 API
- **GET /actuator/health**
- **GET /actuator/metrics**

본 프로젝트는 Spring Boot Actuator를 사용하며, 다음 설정을 전제로 합니다.

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, metrics, prometheus
  endpoint:
    health:
      show-details: when_authorized
      roles: ADMIN
```

- 인증되지 않은 사용자는 `{"status":"UP"}` 수준의 요약 정보만 확인할 수 있습니다.
- ADMIN 권한이 있는 사용자는 DB, Redis 등 세부 컴포넌트 상태를 확인할 수 있습니다.
- 운영 환경에서는 로드밸런서(ALB/NLB) 헬스체크 용도로 `/actuator/health`만 외부에 노출하는 구성을 권장합니다.
- `/actuator/prometheus`가 기본 노출되므로 모니터링 스택과 연계 시 추가 설정 없이 사용할 수 있습니다.

---

## 2. Local Development Guide

### 2.1 필수 요구사항
- Docker Desktop 실행
- Java 21+
- Gradle

### 2.2 로컬 인프라 실행 (Docker)
로컬에서는 MySQL / Redis / RabbitMQ를 Docker로 실행합니다.

```bash
docker compose up -d
```

기본 포트/자격 구성 (docker-compose.yml 기준):
- MySQL: `localhost:3307` → 컨테이너 3306 / root:admin / DB `fitcheck`
- Redis: `localhost:6379` (비밀번호 없음)
- RabbitMQ: `localhost:5672` / guest:guest
- RabbitMQ Management UI: `http://localhost:15672`

Spring Boot 3.5 + `spring-boot-docker-compose` 의존성으로 `./gradlew bootRun` 시 자동으로 compose 서비스를 띄웁니다. 수동 제어가 필요하면 `spring.docker.compose.enabled=false` VM 옵션을 지정하세요.

### 2.3 로컬 애플리케이션 프로필 요약
- 기본 프로필: `local`
- 그룹 로딩: `local -> (local, local-secret)`
- 핵심 속성 발췌:
  ```yaml
  spring:
    datasource:
      url: jdbc:mysql://localhost:3307/fitcheck?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Seoul
      username: ${DB_USERNAME:root}
      password: ${DB_PASSWORD:admin}
    jpa:
      open-in-view: false
      hibernate:
        ddl-auto: update
    data:
      redis:
        host: ${REDIS_HOST:localhost}
        port: ${REDIS_PORT:6379}
        client-type: lettuce
    rabbitmq:
      host: ${RABBIT_HOST:localhost}
      port: ${RABBIT_PORT:5672}
      username: ${RABBIT_USERNAME:guest}
      password: ${RABBIT_PASSWORD:guest}
    docker:
      compose:
        enabled: true
        file: docker-compose.yml
        lifecycle-management: start_and_stop
  server:
    error:
      include-message: always
  ```

`spring.data.redis`는 인증 없이 접속하도록 구성되어 있으므로 docker-compose 기본 Redis 설정과 바로 맞물립니다. 비밀번호/ACL을 적용할 경우 `application-local.yml` 또는 환경변수에서 직접 추가해야 합니다.

---

## 3. Local Secret Configuration (필수)

본 프로젝트는 **시크릿 파일을 Git에 커밋하지 않습니다.**  
로컬 개발자는 아래 중 하나의 방식을 선택해야 합니다.

### 옵션 A. application-local-secret.yml (권장)

경로:
```
src/main/resources/application-local-secret.yml
```

```yaml
spring:
  config:
    activate:
      on-profile: local-secret

  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: ${KAKAO_CLIENT_ID}
            client-secret: ${KAKAO_CLIENT_SECRET}
            client-authentication-method: client_secret_post
            authorization-grant-type: authorization_code
            redirect-uri: ${KAKAO_REDIRECT_URI:http://localhost:8080/login/oauth2/code/kakao}
            scope:
              - account_email
        provider:
          kakao:
            authorization-uri: https://kauth.kakao.com/oauth/authorize
            token-uri: https://kauth.kakao.com/oauth/token
            user-info-uri: https://kapi.kakao.com/v2/user/me
            user-name-attribute: id

app:
  jwt:
    access-token-secret: ${JWT_ACCESS_TOKEN_SECRET}
    refresh-token-secret: ${JWT_REFRESH_TOKEN_SECRET}
```

> 주의: 해당 파일은 반드시 `.gitignore`에 포함되어야 합니다.

---

### 옵션 B. 환경 변수(.env 또는 IDE Run Config)

```text
JWT_ACCESS_TOKEN_SECRET=...
JWT_REFRESH_TOKEN_SECRET=...

KAKAO_CLIENT_ID=...
KAKAO_CLIENT_SECRET=...
KAKAO_REDIRECT_URI=http://localhost:8080/login/oauth2/code/kakao

# (선택) 로컬 기본값을 바꿔야 할 때만 지정
# DB_USERNAME=root
# DB_PASSWORD=admin
# REDIS_HOST=localhost
# REDIS_PORT=6379
```

---

## 4. Production / AWS Deployment Guide

### 4.1 운영 환경 필수 시크릿 목록

#### 인증 / 보안
- JWT_ACCESS_TOKEN_SECRET
- JWT_REFRESH_TOKEN_SECRET

#### Database (RDS)
- DB_URL
- DB_USERNAME
- DB_PASSWORD

#### Redis (ElastiCache 또는 자체 Redis)
- REDIS_HOST
- REDIS_PORT (기본 6379)
- REDIS_PASSWORD (requirepass/ACL을 사용할 때만)

#### Kakao OAuth
- KAKAO_CLIENT_ID
- KAKAO_CLIENT_SECRET
- KAKAO_REDIRECT_URI (운영 도메인 기준)

> 주의: `application-prod.yml`에는 RabbitMQ 설정이 포함되어 있지 않습니다. 운영 환경에서 메시지 브로커가 필요하다면 별도 프로필 혹은 환경변수로 `spring.rabbitmq.*`을 추가해야 합니다.

---

### 4.2 시크릿 관리 권장 방식 (AWS)

- **AWS Secrets Manager** (권장)
- AWS SSM Parameter Store
- ECS/EKS 환경변수 주입
- ❌ 시크릿 파일을 컨테이너 이미지에 포함하지 말 것

---

## 5. 운영 보안 가이드

- Swagger UI는 운영 환경에서 비활성화 권장
- Actuator Endpoint는 내부망 또는 관리자 권한으로만 접근 허용
- JWT Secret은 반드시 32바이트 이상 랜덤 문자열 사용

---

## 6. Swagger(OpenAPI) 사용 안내

- 개발/스테이징 환경에서만 Swagger UI 활성화 권장
- 로컬 기본 경로: `http://localhost:8080/swagger-ui/index.html`
- 운영에서는 `springdoc.swagger-ui.enabled=false` 설정 권장

---

본 문서는 FitCheck 프로젝트의 내부 개발 및 운영 기준 문서입니다.
