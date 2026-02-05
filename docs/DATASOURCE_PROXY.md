# Datasource Proxy 도입

## 목적
- 요청 단위로 SQL 실행 횟수/패턴을 요약해 로그 가독성을 개선한다.
- N+1, 슬로우 쿼리 후보를 빠르게 식별한다.

## 원리
- Datasource Proxy는 실제 `DataSource`를 프록시로 감싸 JDBC 호출을 가로챈다.
- 각 SQL 실행 정보를 수집해 집계(쿼리 수/타입/시간)를 만든다.
- 집계 정보는 요청 단위로 모아 로그로 출력할 수 있다.

## 적용 개요
- 라이브러리: `net.ttddyy:datasource-proxy`
- 설정: `app.datasource-proxy.enabled=true` 일 때만 활성화

## 활성화 프로필
- local/dev: 활성화
- prod: 비활성화

## 로그 예시
### 집계 로그
```
[REQ] POST /api/auth/tokens(0.360s, sql=0.014s, sqlPct=3.9%%): total=4 select=2 insert=1 update=1 delete=0 other=0
```
### 쿼리 로그
```
[Query](0.006s) select ... from ... where ...
```

## 적용 옵션
| 옵션 | 설명 | 기본/적용 |
| --- | --- | --- |
| `app.datasource-proxy.enabled` | 프록시 활성/비활성 스위치 | local/dev: true, prod: false |
| `app.datasource-proxy.log-queries` | SQL 상세 로그 출력 여부 | local/dev: true, prod: false |
| `app.datasource-proxy.ansi` | SQL 키워드 ANSI 색상 적용 여부 | local/dev: true, prod: false |
| `logging.level.net.ttddyy.dsproxy.listener.logging` | datasource-proxy 로그 출력 레벨 | info |

## 프로젝트 적용 현황
- DataSource 프록시 래핑: `BeanPostProcessor`로 기존 `DataSource`를 후처리로 감싸 적용.
- 요청 단위 요약 로그: `QueryCountHolder`를 요청 필터에서 집계/로그 출력.
- 상세 SQL 로그: `QueryLogEntryCreator`를 통해 쿼리 포맷/ANSI 강조 적용.
- 요약 로그 포맷: `[REQ] {METHOD} {PATH}({요청시간}s, sql={SQL합}s, sqlPct={비율}%): ...`
- 상세 로그 포맷: `[Query]({SQL시간}s) {SQL...}`

## 왜 Hibernate 기본 로그 / P6Spy 대신 Datasource Proxy인가
- Hibernate `show_sql`:
  - SQL 문자열 출력 중심, 요청 단위 집계/요약이 없음.
  - 트래픽 증가 시 로그 가독성이 급격히 떨어짐.
- P6Spy:
  - SQL 상세 출력은 용이하지만 요청 단위 요약/집계는 기본 제공이 약함.
  - 요청-요약을 만들려면 추가 구현이 필요.
- Datasource Proxy:
  - 요청 단위 집계(쿼리 수/타입/시간) 확보가 쉬움.
  - 상세 로그와 요약 로그를 함께 운영할 수 있음.

## 현재 사용 방식
- local/dev에서 상세 SQL + ANSI 강조 + 요청 요약 로그를 함께 출력.
- prod에서는 비활성화하여 로그량/오버헤드를 제한.

## 도입 타당성
- 요청 단위 SQL 집계가 필요하고 운영 가독성이 중요한 상황에 적합.
- SQL 실행 시간과 요청 시간을 함께 기록해 병목 위치 판단이 가능.
- 추후 APM/Tracing 도입 전 단계로도 유효.

## 주의사항
- Hibernate `show_sql` 로그와 중복될 수 있으므로 필요 시 비활성화 권장.
- 프록시 활성화 시 로그량이 늘어나므로 운영 환경은 기본 비활성 권장.
