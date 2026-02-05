# Datasource Proxy 도입

## 목적
- 요청 단위로 SQL 실행 횟수/패턴을 요약해 로그 가독성을 개선한다.
- N+1, 슬로우 쿼리 후보를 빠르게 식별한다.

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

## 주의사항
- Hibernate `show_sql` 로그와 중복될 수 있으므로 필요 시 비활성화 권장.
- 프록시 활성화 시 로그량이 늘어나므로 운영 환경은 기본 비활성 권장.
