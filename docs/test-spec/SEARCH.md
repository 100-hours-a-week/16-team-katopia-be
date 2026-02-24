# 검색(Search)

| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-SEARCH-S-01 | 사용자 검색 성공(query prefix) | 인증된 사용자, ACTIVE 사용자 존재 | /api/search/users?query=... | 최신순 사용자 목록 + nextCursor |
| TC-SEARCH-S-02 | 게시글 검색 성공(본문) | 본문에 매칭되는 글 존재 | /api/search/posts?query=... | 본문 매칭 결과 + nextCursor |
| TC-SEARCH-S-03 | 게시글 검색 성공(태그) | 태그에 매칭되는 글 존재 | /api/search/posts?query=#tag | 태그 매칭 결과 + nextCursor |
| TC-SEARCH-S-04 | 사용자 검색 시 팔로잉 여부 포함 | 요청자/팔로우 관계 | searchUsers 호출 | isFollowing 포함 |
| TC-SEARCH-F-01 | 사용자 검색 실패(검색어 길이) | 검색어 길이<2 or >100 | 검색 요청 | 400 오류 반환 |
| TC-SEARCH-F-02 | 사용자 검색 실패(인증 없음) | 인증 없음 | 검색 요청 | 401 오류 반환 |
| TC-SEARCH-F-03 | 게시글 검색 실패(검색어 길이) | 검색어 길이<2 or >100 | 검색 요청 | 400 오류 반환 |
| TC-SEARCH-F-04 | 게시글 검색 실패(인증 없음) | 인증 없음 | 검색 요청 | 401 오류 반환 |

## 검색어 검증(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-SEARCH-VAL-F-01 | 검색어 누락 | query=null/blank | requireQuery 호출 | COMMON-E-001 반환 |
| TC-SEARCH-VAL-F-02 | 검색어 길이 위반 | 길이<2 or >100 | requireQuery 호출 | COMMON-E-006 반환 |

## 검색 페이징 검증(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-SEARCH-PAGE-F-01 | 커서 디코딩 실패 | after 형식 오류 | searchUsers/searchPosts 호출 | 400 오류 반환 |
| TC-SEARCH-PAGE-F-02 | 페이지 사이즈 형식 오류 | sizeValue 비숫자 | searchUsers/searchPosts 호출 | 400 오류 반환 |

## 커서 페이징 유틸(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
