# 검색 도메인 테크 스펙 (Search)

## 배경 (Background)

- 프로젝트 목표: 사용자/게시글 검색을 안정적으로 제공한다.
- 문제 정의:
  - LIKE 검색은 `_`/`%` 와일드카드 이슈가 있다.
  - 게시글 본문 검색은 대량 데이터에서 성능 저하가 발생한다.

## 목표가 아닌 것 (Non-goals)

- 추천/랭킹 기반 검색
- 고급 필터(키/몸무게/성별 필터)

## 설계 및 기술 자료 (Architecture and Technical Documentation)

### 핵심 정책

- 사용자 검색
  - 닉네임 prefix 검색
  - LIKE 와일드카드 이스케이프 처리
  - 커서 기반 페이징
- 게시글 검색
  - MySQL FULLTEXT 사용
  - ngram 파서 적용 여지(부분 검색 대응)
  - query 길이 제한 적용
  - limit 기반 반환(커서 없음)
- 인증
  - 검색 API는 로그인 사용자만 허용

### 인덱스

- members.nickname prefix 검색 인덱스
- posts.content FULLTEXT 인덱스
  - ngram 파서 적용 시 부분 검색 정합성 향상, 인덱스 크기/쓰기 비용 증가

## API 명세 (API Specifications)

- 사용자 검색: `GET /api/search/users?query=&size=&after=`
- 게시글 검색: `GET /api/search/posts?query=&size=`

## 트랜잭션/정합성 정책

- 검색은 읽기 전용 트랜잭션

## 장애/예외 처리

- 검색어 길이 오류는 400
- size 형식 오류는 400

## 확장 방향 (Extension)

- ngram FULLTEXT 전환: 부분 검색 품질 향상, 인덱스 용량/쓰기 비용 증가를 감안해 트래픽·데이터 규모 기준으로 전환
- relevance 튜닝: 가중치/필드별 boost, 최신성 보정 등으로 결과 품질 개선
- 사전/동의어 처리: 브랜드/줄임말 매핑으로 검색 회수율 개선
- 캐시 전략: 인기 검색어/상위 결과 캐시로 지연 시간 완화
- 비동기 색인 분리: 검색 전용 스토어(ES/OpenSearch) 도입 검토
