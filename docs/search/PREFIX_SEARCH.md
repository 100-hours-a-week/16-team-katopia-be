# Prefix 검색 정책 및 와일드카드 처리

## 1) 개요
프로젝트 내 일부 검색(계정/게시글 태그)은 **prefix 기반(`LIKE 'keyword%'`)** 으로 동작한다.  
해당 검색은 빠른 UX를 제공하지만, `_`/`%` 와일드카드 문제와 인덱스 효율을 함께 고려해야 한다.

## 2) 현재 prefix 검색 대상
### 2.1 계정(닉네임) 검색
- 엔드포인트: `/api/search/users`
- 구현 위치: `SearchService#searchUsers`
- 쿼리:
  - `MemberRepository.searchLatestByNickname`
  - `MemberRepository.searchPageAfterByNickname`
- 기준: `nickname LIKE 'query%'`

### 2.2 태그 검색 (게시글 검색 내 분기)
- 엔드포인트: `/api/search/posts`
- 구현 위치: `SearchService#searchPosts`
- 규칙:
  - query가 `#`로 시작하면 태그 prefix 검색
- 쿼리:
  - `PostRepository.searchLatestByTag`
  - `PostRepository.searchPageAfterByTag`
- 기준: `tag.name LIKE 'query%'`

## 3) 와일드카드 문제 및 해결
### 3.1 문제 원인
MySQL `LIKE`에서 `_`는 단일 문자, `%`는 0개 이상의 문자에 매칭되는 **와일드카드**다.  
따라서 사용자가 입력한 검색어에 `_`/`%`가 포함되면 의도치 않게 많은 결과가 매칭된다.

### 3.2 해결 방식
검색어에 포함된 `_`, `%`, `\\` 문자를 **이스케이프**하고,  
JPQL에 `ESCAPE '\\'`를 명시해 문자 그대로 매칭되도록 처리한다.

#### 서비스 전처리
```java
String keyword = LikeEscapeHelper.escape(rawQuery);
```

#### JPQL 예시
```sql
... where m.nickname like concat(:nickname, '%') escape '\\'
```

### 3.3 적용 범위
- 닉네임 prefix 검색
- 태그 prefix 검색
- 본문 prefix 검색(기존 V1 로직)

## 4) 성능 참고
- prefix 검색은 `LIKE 'keyword%'` 형태이므로 인덱스를 탈 수 있다.
- 검색어 길이 제한(예: 2~100)을 유지해 과도한 스캔을 방지한다.

## 5) 운영 체크리스트
- [ ] `_`/`%` 입력 시 예상 결과 여부 확인
- [ ] 인덱스가 실제로 사용되는지 EXPLAIN 확인
- [ ] 검색어 길이 제한 정책 유지 여부 확인

---
이 문서는 prefix 검색 정책과 와일드카드 문제 해결 방식을 기록한 초안이다.
