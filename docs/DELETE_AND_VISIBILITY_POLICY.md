# 삭제/조회 정책 초안

> 현 상태(as-is) 기준 정리. 실제 정책과 다르면 이 문서가 업데이트 대상임.

## 1) 용어
- 계정 상태
  - ACTIVE: 정상 사용 가능
  - PENDING: 가입 미완료
  - WITHDRAWN: 탈퇴(소프트 삭제 + 익명화)
- “익명화 표기”: 응답에서 작성자 닉네임을 “알 수 없음”으로 표시

## 2) 삭제 정책 (as-is)
### 2.1 회원 탈퇴 (소프트 삭제)
- 처리: `Member.markAsWithdrawn`
  - nickname: `withdrawn_{id}`로 변경
  - profileImageObjectKey: null
  - styles: clear
  - accountStatus: WITHDRAWN
  - deletedAt: now
- 데이터 유지: 게시글/댓글은 삭제하지 않음

### 2.2 회원 하드 삭제 (local 프로필 전용)
- 엔드포인트: `/api/dev/members/{id}` 
- 처리(DevMemberService):
  - 회원 게시글 전부 삭제
  - 해당 게시글의 댓글/좋아요/태그 매핑 삭제
  - 회원의 댓글/좋아요 삭제
  - 회원 삭제

### 2.3 게시글 삭제 (소유자)
- 처리(PostCommandService):
  - 댓글 삭제 → 좋아요 삭제 → 태그 매핑 삭제 → 게시글 삭제
- post_images는 게시글 삭제 시 함께 제거됨(값 컬렉션)

### 2.4 댓글 삭제 (소유자)
- 처리(CommentCommandService): 댓글 삭제 + 댓글 수 감소

## 3) 조회 정책 (as-is)
### 3.1 회원 프로필 조회 `/api/members/{id}`
- 상태 ACTIVE: 조회 가능
- 상태 PENDING: 404 + 전용 메시지
- 상태 WITHDRAWN: 404 + 전용 메시지

### 3.2 사용자 작성 게시글 목록 `/api/members/{id}/posts`
- ACTIVE만 조회 가능 (WITHDRAWN/PENDING은 404)

### 3.3 게시글 상세 조회 `/api/posts/{id}`
- 게시글 존재하면 조회 가능
- 작성자 상태는 별도 제한 없음
- 작성자가 WITHDRAWN이면 응답 작성자 닉네임은 “알 수 없음”

### 3.4 댓글 목록 조회 `/api/posts/{id}/comments`
- 게시글 존재하면 조회 가능
- 댓글 작성자가 WITHDRAWN이면 응답 작성자 닉네임은 “알 수 없음”

## 4) 응답에서의 작성자 표기 (as-is)
- 게시글/댓글 작성자: WITHDRAWN → “알 수 없음”
- 기타 상태는 원래 닉네임/프로필 반환
