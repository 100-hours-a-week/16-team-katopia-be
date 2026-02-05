# 게시글 본문 FULLTEXT 도입

## 1) 현재 구조
- 계정 검색: 닉네임 prefix 검색(`LIKE 'keyword%'`)
- 태그 검색: 태그명 prefix 검색(`LIKE 'keyword%'`)
- 본문 검색: FULLTEXT 기반 검색(`MATCH ... AGAINST`)

## 2) FULLTEXT 동작 개요
- DB가 본문을 **토큰화**하고 **역색인**을 생성한다.
- 검색은 `MATCH(content) AGAINST(:query)`로 수행한다.
- 기본 도입은 **natural language mode**이며, 정렬은 `score DESC` → `created_at DESC` → `id DESC`.

## 2.1 현재 상태/제약 사항 (2026-02-03 기준)
- **ngram 파서 적용**으로 부분 검색을 지원한다.
- FULLTEXT 관련 설정값(`innodb_ft_min_token_size`, stopword 등)은 **기본값 유지**다.
- 기본 `ngram_token_size=2`를 전제로 하며, 최소 검색어 길이(2자) 정책과 일치한다.

## 2.2 적용 사유 (설정/옵션 선택 근거)
- **ngram 파서 + ngram_token_size=2**: 한글 부분 검색 요구가 명확했고, 최소 검색어 길이가 2자이므로 기본값(2)이 정책과 일치함.
- **NATURAL LANGUAGE MODE**: 기본 관련도 기반 정렬을 활용하고, 초기 도입 리스크를 최소화하기 위함.
- **정렬 기준**: `score DESC → created_at DESC → id DESC`로 결정적 정렬을 보장.
- **설정값 미변경**: 운영 영향(재기동/전체 인덱스 재생성)을 최소화하고, 초기 도입 안정성 확보가 우선.

## 3) API 변경 요약
- 게시글 검색은 `/api/search/posts`에서 FULLTEXT를 사용한다.
- 파라미터
  - `query`: 검색어 (2~200자)
  - `size`: 페이지 크기
  - `after`: 커서(현재는 사용하지 않음, 향후 개선 포인트)

## 4) DB 변경 (수동 DDL)
MySQL 8.0/8.4 기준 InnoDB FULLTEXT 사용.
```sql
ALTER TABLE posts
  DROP INDEX ft_posts_content;

ALTER TABLE posts
  ADD FULLTEXT INDEX ft_posts_content (content) WITH PARSER ngram;
```

### 확인 방법
```sql
SHOW INDEX FROM posts;
SHOW VARIABLES LIKE 'ngram_token_size';
EXPLAIN
SELECT p.*
FROM posts p
JOIN members m ON m.id = p.member_id
WHERE m.account_status = 'ACTIVE'
  AND MATCH(p.content) AGAINST('검색어' IN NATURAL LANGUAGE MODE);
```

### 롤백
```sql
ALTER TABLE posts
  DROP INDEX ft_posts_content;
```

## 5) 운영 체크 지표
- 검색 응답시간(p95/p99)
- DB CPU/IO
- FULLTEXT 인덱스 크기
- 게시글 작성/수정 지연 증가 여부

## 6) 개선 포인트(향후)
- **ngram 파서 고도화**: token size/분석기 변경 필요성 판단
- **토큰 길이 제한**: `innodb_ft_min_token_size` / `ngram_token_size` 조정 필요성 판단
- **stopword 영향 분석**: 검색 누락 케이스 수집
- **랭킹 튜닝**: score vs 최신성 가중치 조정
- **커서 페이징**: score+id 기반 커서 설계
- **오타/초성 요구 대응**: 전처리 컬럼 또는 검색 엔진 도입
- **커밋 직후 검색 반영성**: 트랜잭션 커밋 이후 검색 반영 지연 모니터링

## 6.1 개선 우선순위 및 근거
1) **token size/stopword 튜닝**
   - 부분 검색/검색 누락 문제를 가장 직접적으로 개선
   - 운영 데이터 기반으로 효과 측정이 쉬움
2) **랭킹/정렬 튜닝**
   - 성능보다 결과 품질 개선 목적
3) **커서 페이징 고도화**
   - 대규모 트래픽/깊은 탐색이 필요해지는 시점에 효과 큼
4) **오타/초성 대응 및 검색 엔진 도입**
   - 정확도/유연성은 높지만 운영 복잡도와 비용이 큼

## 6.2 ngram 파서 운영 체크
### 운영 설정 참고
- `ngram_token_size` 기본값은 **2** (2글자 단위 토큰)
- 변경 시 인덱스 재생성이 필요
- 확인:
```sql
SHOW VARIABLES LIKE 'ngram_token_size';
```

## 7) 롤백/비활성화 전략
- 인덱스 제거 시 FULLTEXT 검색 결과가 비활성화됨
- 필요 시 `LIKE` 검색으로 롤백하는 기능 플래그/설정 도입을 고려
