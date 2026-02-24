# 댓글(Comment)

| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-COMMENT-S-01 | 댓글 작성 성공 | 인증된 사용자, 게시글 존재 | 댓글 작성 | 201 응답 |
| TC-COMMENT-S-02 | 댓글 목록 조회(커서) | 댓글 존재 | size/after로 조회 | 최신순 목록 + nextCursor |
| TC-COMMENT-S-03 | 댓글 수정 성공 | 작성자 본인 | 수정 요청 | 200 응답 |
| TC-COMMENT-S-04 | 댓글 삭제 성공 | 작성자 본인 | 삭제 요청 | 204 응답 |
| TC-COMMENT-F-01 | 댓글 작성 실패(본문 없음) | 인증된 사용자 | 빈 본문 | 400 오류 반환 |
| TC-COMMENT-F-02 | 댓글 작성 실패(본문 200자 초과) | 인증된 사용자 | 긴 본문 | 400 오류 반환 |
| TC-COMMENT-F-03 | 댓글 수정 실패(타인) | 작성자 아님 | 수정 요청 | 403 오류 반환 |
| TC-COMMENT-F-04 | 댓글 삭제 실패(타인) | 작성자 아님 | 삭제 요청 | 403 오류 반환 |
| TC-COMMENT-F-05 | 댓글 수정 실패(댓글 없음) | 댓글 미존재 | 수정 요청 | 404 오류 반환 |
| TC-COMMENT-F-06 | 댓글 삭제 실패(댓글 없음) | 댓글 미존재 | 삭제 요청 | 404 오류 반환 |

## 댓글 커맨드(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-COMMENT-CMD-S-01 | 댓글 생성 성공(연관 엔티티/카운트 증가) | 게시글/작성자 존재 | create 호출 | 저장 + count 증가 |
| TC-COMMENT-CMD-S-02 | 댓글 수정 성공(본문 변경) | 댓글/작성자 존재 | update 호출 | content 변경 |
| TC-COMMENT-CMD-S-03 | 댓글 알림 생성 | 타인 게시글 | create 호출 | 알림 생성 요청 |
| TC-COMMENT-CMD-F-01 | 댓글 작성 실패(연관관계 오류) | postId/memberId FK 오류 | create 호출 | COMMON-E-008 반환 |
| TC-COMMENT-CMD-F-02 | 댓글 수정 실패(댓글 없음) | commentId 미존재 | update 호출 | COMMENT-E-003 반환 |
| TC-COMMENT-CMD-F-03 | 댓글 수정 실패(작성자 아님) | 타인 댓글 | update 호출 | AUTH-E-014 반환 |
| TC-COMMENT-CMD-F-04 | 댓글 삭제 실패(작성자 아님) | 타인 댓글 | delete 호출 | AUTH-E-014 반환 |
| TC-COMMENT-CMD-F-05 | 댓글 삭제 실패(댓글 없음) | commentId 미존재 | delete 호출 | COMMENT-E-003 반환 |

## 댓글 작성자 표시(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-COMMENT-AUTHOR-S-01 | 탈퇴 회원 닉네임 표시 | WITHDRAWN 회원 | CommentAuthorResponse.of | 닉네임=탈퇴 회원 |
| TC-COMMENT-AUTHOR-S-02 | 활성 회원 닉네임 표시 | ACTIVE 회원 | CommentAuthorResponse.of | 닉네임=원본 |

## 댓글 조회(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-COMMENT-QUERY-S-01 | 댓글 목록: 최신 목록과 다음 커서 생성 | 댓글 2개 | list 호출 | nextCursor 생성 |
| TC-COMMENT-QUERY-S-02 | 댓글 목록: 커서 이후 페이지 조회 | after 존재 | list 호출 | after 기반 조회 |
| TC-COMMENT-QUERY-F-01 | 댓글 목록 실패(게시글 없음) | postId 미존재 | list 호출 | POST-E-002 반환 |

## 댓글 본문 유효성(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-COMMENT-VAL-S-01 | 본문 유효성 성공 | content=유효문자열 | 검증 수행 | 오류 없음 |
| TC-COMMENT-VAL-F-01 | 본문 누락(null) | content=null | 검증 수행 | COMMENT-E-001 반환 |
| TC-COMMENT-VAL-F-02 | 본문 누락(빈 문자열) | content="" | 검증 수행 | COMMENT-E-001 반환 |
| TC-COMMENT-VAL-F-03 | 본문 공백만 | content="   " | 검증 수행 | COMMENT-E-001 반환 |
| TC-COMMENT-VAL-F-04 | 본문 길이 초과 | content=201자 | 검증 수행 | COMMENT-E-002 반환 |
