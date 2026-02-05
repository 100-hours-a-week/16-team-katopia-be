# API Reference

## 성공 코드 목록

| Code | HTTP | Message |
| --- | --- | --- |
| COMMON-S-001 | 200 | 검색을 완료했습니다. |
| COMMON-S-002 | 200 | 업로드 URL 발급을 완료했습니다. |
| AUTH-S-000 | 200 | 신규 회원입니다. 추가 정보 입력이 필요합니다. |
| AUTH-S-001 | 201 | 회원 가입이 완료되었습니다. |
| AUTH-S-002 | 204 | 회원 탈퇴가 완료되었습니다. |
| AUTH-S-010 | 201 | 로그인이 완료되었습니다. |
| AUTH-S-011 | 200 | 로그아웃이 완료되었습니다. |
| AUTH-S-020 | 200 | 엑세스 토큰이 갱신되었습니다. |
| MEMBER-S-000 | 200 | 닉네임 중복 여부를 조회했습니다. |
| MEMBER-S-003 | 200 | 회원 정보가 수정되었습니다. |
| MEMBER-S-004 | 200 | 프로필 조회가 성공적으로 완료되었습니다. |
| MEMBER-S-005 | 204 | 회원이 삭제되었습니다. |
| POST-S-001 | 201 | 게시글이 등록되었습니다. |
| POST-S-002 | 200 | 게시글 목록 조회를 완료되었습니다. |
| POST-S-003 | 200 | 게시글 상세 조회를 완료되었습니다. |
| POST-S-004 | 200 | 게시글이 수정되었습니다. |
| POST-S-005 | 204 | 게시글이 삭제되었습니다. |
| COMMENT-S-001 | 201 | 댓글이 등록되었습니다. |
| COMMENT-S-002 | 200 | 댓글 목록 조회를 완료되었습니다. |
| COMMENT-S-003 | 200 | 댓글이 수정되었습니다. |
| COMMENT-S-004 | 204 | 댓글이 삭제되었습니다. |
| POST-LIKE-S-001 | 201 | 해당 게시글에 좋아요를 남겼습니다. |
| POST-LIKE-S-002 | 204 | 게시글 좋아요가 취소되었습니다. |

## 오류 코드 목록

### Common
| Code | HTTP | Message | 비고 |
| --- | --- | --- | --- |
| COMMON-E-001 | 400 | %s을 입력해주세요. | 필수값 누락(필드명 포함) |
| COMMON-E-002 | 400 | 유효하지 않은 식별자 형식입니다. | cursor/ID 파싱 실패 포함 |
| COMMON-E-003 | 400 | 페이지 크기(size)는 숫자 형식이어야 합니다. | size 파싱 실패 |
| COMMON-E-004 | 400 | 검색어는 최소 2자, 최대 100자 이내 이어야 합니다. | 검색어 길이 오류 |
| COMMON-E-005 | 404 | 요청한 API 경로를 찾을 수 없습니다. | NoHandlerFound |
| COMMON-E-006 | 405 | 지원하지 않는 HTTP 메서드입니다. | Method Not Allowed |
| COMMON-E-007 | 400 | 유효하지 않은 요청입니다. | 본문 파싱 실패 포함 |
| COMMON-E-999 | 500 | 시스템 오류로 작업을 수행하지 못했습니다. 잠시 후 다시 시도해주세요. | 예외 기본 |

### Auth
| Code | HTTP | Message |
| --- | --- | --- |
| AUTH-E-000 | 401 | 임시 인증 정보가 유효하지 않습니다. 다시 로그인해주세요. |
| AUTH-E-001 | 400 | 임시 인증 정보로는 처리할 수 없는 요청입니다. |
| AUTH-E-002 | 400 | 임시 인증 정보가 존재하지 않습니다. |
| AUTH-E-010 | 401 | 인증 정보가 존재하지 않습니다. |
| AUTH-E-011 | 401 | 인증 정보가 유효하지 않습니다. 다시 로그인해주세요. |
| AUTH-E-012 | 401 | 인증 쿠키가 존재하지 않습니다. |
| AUTH-E-013 | 401 | 인증 정보가 유효하지 않습니다. 다시 로그인해주세요. |
| AUTH-E-014 | 403 | 올바르지 않은 접근입니다. |
| AUTH-E-020 | 409 | 이미 가입된 계정입니다. 로그인을 시도해주세요. |
| AUTH-E-021 | 403 | 탈퇴한 계정입니다. 14일 이후 재가입 가능합니다. |
| AUTH-E-900 | 401 | 지원하지 않는 인증 정보입니다. 다시 로그인해주세요. |

### Member
| Code | HTTP | Message |
| --- | --- | --- |
| MEMBER-E-001 | 400 | 닉네임은 최초 2자, 최대 20자까지 입력 가능합니다. |
| MEMBER-E-002 | 400 | .(온점), _(아래 밑줄) 외의 특수문자는 포함할 수 없습니다. |
| MEMBER-E-003 | 400 | 닉네임에 공백을 사용할 수 없습니다. |
| MEMBER-E-004 | 409 | 이미 사용중인 닉네임입니다. |
| MEMBER-E-010 | 400 | 유효하지 않은 이메일 형식입니다. |
| MEMBER-E-020 | 400 | 성별 값은 'M' 또는 'F'여야 합니다. |
| MEMBER-E-021 | 400 | 키는 숫자(정수)로 입력해야 합니다. |
| MEMBER-E-022 | 400 | 키는 50 이상 300 이하로 입력해야 합니다. |
| MEMBER-E-023 | 400 | 몸무게는 숫자(정수)로 입력해야 합니다. |
| MEMBER-E-024 | 400 | 몸무게는 20 이상 500 이하로 입력해야 합니다. |
| MEMBER-E-030 | 400 | 유효하지 않은 스타일 타입입니다. 허용된 스타일 목록을 확인해주세요. |
| MEMBER-E-031 | 400 | 스타일은 최대 2개까지 선택 가능합니다. |
| MEMBER-E-040 | 400 | 알림 설정 값은 true 또는 false여야 합니다. |
| MEMBER-E-050 | 404 | 사용자를 찾을 수 없습니다. |
| MEMBER-E-051 | 404 | 회원가입이 완료되지 않은 사용자입니다. |
| MEMBER-E-052 | 404 | 탈퇴 후 재가입 유예 기간 중입니다. 14일 후에 다시 시도해주세요. |

### Post
| Code | HTTP | Message |
| --- | --- | --- |
| POST-E-000 | 400 | 본문 내용은 필수 입력 항목입니다. |
| POST-E-001 | 400 | 본문 내용은 최대 200자 입니다. |
| POST-E-002 | 404 | 게시글을 찾을 수 없습니다. |
| POST-E-010 | 400 | 이미지는 최소 1장 이상, 최대 3장까지 등록 가능합니다. |
| POST-E-020 | 400 | 태그는 최소 1자, 최대 20자 등록 가능합니다. |
| POST-E-021 | 400 | 태그는 최대 10개까지 등록 가능합니다. |

### Comment
| Code | HTTP | Message |
| --- | --- | --- |
| COMMENT-E-001 | 400 | 본문 내용은 필수 입력 항목입니다. |
| COMMENT-E-002 | 400 | 본문 내용은 최대 200자 입니다. |
| COMMENT-E-003 | 404 | 댓글을 찾을 수 없습니다. |

### Post Like
| Code | HTTP | Message |
| --- | --- | --- |
| POST-LIKE-E-001 | 409 | 이미 좋아요를 누른 게시글입니다. |
| POST-LIKE-E-002 | 404 | 취소할 좋아요 기록이 없습니다. |

## 회원

<table>
  <tr>
    <td align="center">설명</td>
    <td align="center">요청</td>
    <td align="center">응답</td>
    <td align="center">오류</td>
  </tr>
  <tr>
    <td>

회원가입 API

</td>
<td>

```json
Cookie: registrationToken={REG}

{
  "nickname": "dev_user",
  "gender": "M",
  "profileImageObjectKey": "profile/1/1-uuid.png",
  "height": "175",
  "weight": "70",
  "enableRealtimeNotification": true,
  "style": [
    "MINIMAL",
    "CASUAL"
  ]
}
```

</td>
<td>

```json
{
  "success": true,
  "code": "AUTH-S-001",
  "message": "회원 가입이 완료되었습니다.",
  "data": {
    "id": 1,
    "status": "ACTIVE",
    "accessToken": "at_dummy_string"
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      AUTH-E-000 <br/>
      AUTH-E-002 <br/>
      AUTH-E-020 <br/>
      AUTH-E-021 <br/>
      MEMBER-E-001 <br/>
      MEMBER-E-002 <br/>
      MEMBER-E-003 <br/>
      MEMBER-E-004 <br/>
      MEMBER-E-020 <br/>
      MEMBER-E-021 <br/>
      MEMBER-E-022 <br/>
      MEMBER-E-023 <br/>
      MEMBER-E-024 <br/>
      MEMBER-E-030 <br/>
      MEMBER-E-031 <br/>
      MEMBER-E-040 <br/>
    </td>
  </tr>
  <tr>
    <td>

닉네임 유효성/중복 확인 API

</td>
<td>

```json
GET /api/members/check?nickname=dev_user
```

</td>
<td>

```json
{
  "success": true,
  "code": "MEMBER-S-000",
  "message": "닉네임 중복 여부를 조회했습니다.",
  "data": {
    "isAvailable": true
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      COMMON-E-001 <br/>
      MEMBER-E-001 <br/>
      MEMBER-E-002 <br/>
      MEMBER-E-003 <br/>
    </td>
  </tr>
  <tr>
    <td>

사용자 정보 조회 API

</td>
<td>

```json
GET /api/members/1
```

</td>
<td>

```json
{
  "success": true,
  "code": "MEMBER-S-004",
  "message": "프로필 조회가 성공적으로 완료되었습니다.",
  "data": {
    "id": 1,
    "profile": {
      "nickname": "dev_user",
      "profileImageObjectKey": "profile/1/1-uuid.png",
      "gender": "M",
      "height": 175,
      "weight": 70,
      "style": [
        "MINIMAL",
        "CASUAL"
      ]
    },
    "aggregate": null,
    "updatedAt": "2026-01-01T00:00:00"
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      COMMON-E-002 <br/>
      MEMBER-E-050 <br/>
      MEMBER-E-051 <br/>
      MEMBER-E-052 <br/>
    </td>
  </tr>
  <tr>
    <td>

사용자 게시글 조회 API

</td>
<td>

```json
GET /api/members/1/posts?size=20&after=2026-01-01T00:00:00|1
```

</td>
<td>

```json
{
  "success": true,
  "code": "POST-S-002",
  "message": "게시글 목록 조회를 완료되었습니다.",
  "data": {
    "posts": [
      {
        "id": 1,
        "imageObjectKey": "post/1/1-uuid.png",
        "createdAt": "2026-01-01T00:00:00"
      }
    ],
    "nextCursor": "2026-01-01T00:00:00|1"
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      COMMON-E-002 <br/>
      COMMON-E-003 <br/>
      MEMBER-E-050 <br/>
    </td>
  </tr>
  <tr>
    <td>

내 정보 조회 API

</td>
<td>

```json
GET /api/members/me
Authorization: Bearer {AT}
```

</td>
<td>

```json
{
  "success": true,
  "code": "MEMBER-S-004",
  "message": "프로필 조회가 성공적으로 완료되었습니다.",
  "data": {
    "id": 1,
    "nickname": "dev_user",
    "profileImageObjectKey": "profile/1/1-uuid.png",
    "enableRealtimeNotification": true,
    "email": "user@example.com",
    "profile": {
      "nickname": "dev_user",
      "profileImageObjectKey": "profile/1/1-uuid.png",
      "gender": "M",
      "height": 175,
      "weight": 70,
      "style": [
        "MINIMAL",
        "CASUAL"
      ]
    },
    "updatedAt": "2026-01-01T00:00:00"
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      AUTH-E-010 <br/>
      AUTH-E-011 <br/>
      AUTH-E-014 <br/>
    </td>
  </tr>
  <tr>
    <td>

내 정보 수정 API

</td>
<td>

```json
PATCH /api/members
Authorization: Bearer {AT}

{
  "nickname": "new_name",
  "profileImageObjectKey": "profile/1/1-uuid.png",
  "gender": "F",
  "height": "165",
  "weight": "55",
  "enableRealtimeNotification": false,
  "style": [
    "MINIMAL"
  ]
}
```

</td>
<td>

```json
{
  "success": true,
  "code": "MEMBER-S-003",
  "message": "회원 정보가 수정되었습니다.",
  "data": {
    "id": 1,
    "nickname": "dev_user",
    "profileImageObjectKey": "profile/1/1-uuid.png",
    "enableRealtimeNotification": true,
    "email": "user@example.com",
    "profile": {
      "nickname": "new_name",
      "profileImageObjectKey": "profile/1/1-uuid.png",
      "gender": "F",
      "height": 165,
      "weight": 55,
      "style": [
        "MINIMAL"
      ]
    },
    "updatedAt": "2026-01-01T00:00:00"
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      AUTH-E-010 <br/>
      AUTH-E-011 <br/>
      MEMBER-E-001 <br/>
      MEMBER-E-002 <br/>
      MEMBER-E-003 <br/>
      MEMBER-E-004 <br/>
      MEMBER-E-020 <br/>
      MEMBER-E-021 <br/>
      MEMBER-E-022 <br/>
      MEMBER-E-023 <br/>
      MEMBER-E-024 <br/>
      MEMBER-E-030 <br/>
      MEMBER-E-031 <br/>
      MEMBER-E-040 <br/>
      MEMBER-E-052 <br/>
    </td>
  </tr>
  <tr>
    <td>

회원 탈퇴 API

</td>
<td>

```json
DELETE /api/members
Authorization: Bearer {AT}
```

</td>
<td>

```json
204 No Content
```

</td>
<td>
      AUTH-E-010 <br/>
      AUTH-E-011 <br/>
      MEMBER-E-052 <br/>
    </td>
  </tr>
</table>

## 인증

<table>
  <tr>
    <td align="center">설명</td>
    <td align="center">요청</td>
    <td align="center">응답</td>
    <td align="center">오류</td>
  </tr>
  <tr>
    <td>

토큰 재발급 API

</td>
<td>

```json
POST /api/auth/tokens
Cookie: refreshToken={RT}
```

</td>
<td>

```json
{
  "success": true,
  "code": "AUTH-S-020",
  "message": "엑세스 토큰이 갱신되었습니다.",
  "data": {
    "accessToken": "at_dummy_string"
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      AUTH-E-012 <br/>
      AUTH-E-013 <br/>
    </td>
  </tr>
  <tr>
    <td>

로그아웃 API

</td>
<td>

```json
DELETE /api/auth/tokens
Cookie: refreshToken={RT}
```

</td>
<td>

```json
{
  "success": true,
  "code": "AUTH-S-011",
  "message": "로그아웃이 완료되었습니다.",
  "data": null,
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      -
    </td>
  </tr>
</table>

## 게시글

<table>
  <tr>
    <td align="center">설명</td>
    <td align="center">요청</td>
    <td align="center">응답</td>
    <td align="center">오류</td>
  </tr>
  <tr>
    <td>

게시글 작성 API

</td>
<td>

```json
POST /api/posts
Authorization: Bearer {AT}

{
  "content": "오늘의 코디",
  "imageObjectKeys": [
    "post/1/1-uuid.png"
  ],
  "tags": [
    "미니멀",
    "데일리"
  ]
}
```

</td>
<td>

```json
{
  "success": true,
  "code": "POST-S-001",
  "message": "게시글이 등록되었습니다.",
  "data": {
    "id": 1,
    "content": "오늘의 코디",
    "imageObjectKeys": [
      {
        "sortOrder": 1,
        "imageObjectKey": "post/1/1-uuid.png"
      }
    ],
    "tags": [
      "미니멀",
      "데일리"
    ],
    "createdAt": "2026-01-01T00:00:00"
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      AUTH-E-010 <br/>
      AUTH-E-011 <br/>
      POST-E-000 <br/>
      POST-E-001 <br/>
      POST-E-010 <br/>
      POST-E-020 <br/>
      POST-E-021 <br/>
    </td>
  </tr>
  <tr>
    <td>

게시글 목록 조회 API

</td>
<td>

```json
GET /api/posts?size=20&after=2026-01-01T00:00:00|1
Authorization: Bearer {AT}
```

</td>
<td>

```json
{
  "success": true,
  "code": "POST-S-002",
  "message": "게시글 목록 조회를 완료되었습니다.",
  "data": {
    "posts": [
      {
        "id": 1,
        "imageObjectKey": "post/1/1-uuid.png",
        "createdAt": "2026-01-01T00:00:00"
      }
    ],
    "nextCursor": "2026-01-01T00:00:00|1"
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      COMMON-E-003 <br/>
      COMMON-E-002 <br/>
    </td>
  </tr>
  <tr>
    <td>

게시글 상세 조회 API

</td>
<td>

```json
GET /api/posts/1
```

</td>
<td>

```json
{
  "success": true,
  "code": "POST-S-003",
  "message": "게시글 상세 조회를 완료되었습니다.",
  "data": {
    "imageObjectKeys": [
      {
        "sortOrder": 1,
        "imageObjectKey": "post/1/1-uuid.png"
      }
    ],
    "content": "오늘의 코디",
    "tags": [
      "미니멀",
      "데일리"
    ],
    "isLiked": false,
    "author": {
      "id": 1,
      "nickname": "dev_user",
      "profileImageObjectKey": "profile/1/1-uuid.png",
      "gender": "M",
      "height": 175,
      "weight": 70
    },
    "aggregate": {
      "likeCount": 10,
      "commentCount": 2
    },
    "createdAt": "2026-01-01T00:00:00"
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      POST-E-002 <br/>
      COMMON-E-002 <br/>
    </td>
  </tr>
  <tr>
    <td>

게시글 수정 API

</td>
<td>

```json
PATCH /api/posts/1
Authorization: Bearer {AT}

{
  "content": "수정된 본문",
  "tags": [
    "미니멀"
  ]
}
```

</td>
<td>

```json
{
  "success": true,
  "code": "POST-S-004",
  "message": "게시글이 수정되었습니다.",
  "data": {
    "content": "수정된 본문",
    "updatedAt": "2026-01-01T00:00:00"
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      AUTH-E-010 <br/>
      AUTH-E-011 <br/>
      AUTH-E-014 <br/>
      POST-E-000 <br/>
      POST-E-001 <br/>
      POST-E-020 <br/>
      POST-E-021 <br/>
      POST-E-002 <br/>
    </td>
  </tr>
  <tr>
    <td>

게시글 삭제 API

</td>
<td>

```json
DELETE /api/posts/1
Authorization: Bearer {AT}
```

</td>
<td>

```json
204 No Content
```

</td>
<td>
      AUTH-E-010 <br/>
      AUTH-E-011 <br/>
      AUTH-E-014 <br/>
      POST-E-002 <br/>
    </td>
  </tr>
  <tr>
    <td>

게시글 좋아요 API

</td>
<td>

```json
POST /api/posts/1/likes
Authorization: Bearer {AT}
```

</td>
<td>

```json
{
  "success": true,
  "code": "POST-LIKE-S-001",
  "message": "해당 게시글에 좋아요를 남겼습니다.",
  "data": {
    "postId": 1,
    "likeCount": 10
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      AUTH-E-010 <br/>
      AUTH-E-011 <br/>
      COMMON-E-002 <br/>
      POST-E-002 <br/>
      POST-LIKE-E-001 <br/>
    </td>
  </tr>
  <tr>
    <td>

게시글 좋아요 해제 API

</td>
<td>

```json
DELETE /api/posts/1/likes
Authorization: Bearer {AT}
```

</td>
<td>

```json
204 No Content
```

</td>
<td>
      AUTH-E-010 <br/>
      AUTH-E-011 <br/>
      COMMON-E-002 <br/>
      POST-E-002 <br/>
      POST-LIKE-E-002 <br/>
    </td>
  </tr>
</table>

## 댓글

<table>
  <tr>
    <td align="center">설명</td>
    <td align="center">요청</td>
    <td align="center">응답</td>
    <td align="center">오류</td>
  </tr>
  <tr>
    <td>

댓글 작성 API

</td>
<td>

```json
POST /api/posts/1/comments
Authorization: Bearer {AT}

{
  "content": "댓글입니다"
}
```

</td>
<td>

```json
{
  "success": true,
  "code": "COMMENT-S-001",
  "message": "댓글이 등록되었습니다.",
  "data": {
    "id": 1,
    "content": "댓글입니다",
    "createdAt": "2026-01-01T00:00:00",
    "updatedAt": "2026-01-01T00:00:00"
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      AUTH-E-010 <br/>
      AUTH-E-011 <br/>
      COMMENT-E-001 <br/>
      COMMENT-E-002 <br/>
      POST-E-002 <br/>
    </td>
  </tr>
  <tr>
    <td>

댓글 목록 조회 API

</td>
<td>

```json
GET /api/posts/1/comments?size=20&after=2026-01-01T00:00:00|1
```

</td>
<td>

```json
{
  "success": true,
  "code": "COMMENT-S-002",
  "message": "댓글 목록 조회를 완료되었습니다.",
  "data": {
    "comments": [
      {
        "id": 1,
        "author": {
          "id": 1,
          "nickname": "dev_user",
          "profileImageObjectKey": "profile/1/1-uuid.png"
        },
        "content": "댓글입니다",
        "createdAt": "2026-01-01T00:00:00"
      }
    ],
    "nextCursor": "2026-01-01T00:00:00|1"
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      COMMON-E-003 <br/>
      COMMON-E-002 <br/>
      POST-E-002 <br/>
    </td>
  </tr>
  <tr>
    <td>

댓글 수정 API

</td>
<td>

```json
PATCH /api/posts/1/comments/1
Authorization: Bearer {AT}

{
  "content": "수정된 댓글"
}
```

</td>
<td>

```json
{
  "success": true,
  "code": "COMMENT-S-003",
  "message": "댓글이 수정되었습니다.",
  "data": {
    "id": 1,
    "content": "수정된 댓글",
    "createdAt": "2026-01-01T00:00:00",
    "updatedAt": "2026-01-01T00:00:00"
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      AUTH-E-010 <br/>
      AUTH-E-011 <br/>
      AUTH-E-014 <br/>
      COMMENT-E-001 <br/>
      COMMENT-E-002 <br/>
      COMMENT-E-003 <br/>
      POST-E-002 <br/>
    </td>
  </tr>
  <tr>
    <td>

댓글 삭제 API

</td>
<td>

```json
DELETE /api/posts/1/comments/1
Authorization: Bearer {AT}
```

</td>
<td>

```json
204 No Content
```

</td>
<td>
      AUTH-E-010 <br/>
      AUTH-E-011 <br/>
      AUTH-E-014 <br/>
      COMMENT-E-003 <br/>
      POST-E-002 <br/>
    </td>
  </tr>
</table>

## 검색

<table>
  <tr>
    <td align="center">설명</td>
    <td align="center">요청</td>
    <td align="center">응답</td>
    <td align="center">오류</td>
  </tr>
  <tr>
    <td>

계정 검색 API

</td>
<td>

```json
GET /api/search/users?query=dev&size=20&after=2026-01-01T00:00:00|1
Authorization: Bearer {AT}
```

</td>
<td>

```json
{
  "success": true,
  "code": "COMMON-S-001",
  "message": "검색을 완료했습니다.",
  "data": {
    "members": [
      {
        "id": 1,
        "nickname": "dev_user",
        "profileImageObjectKey": "profile/1/1-uuid.png"
      }
    ],
    "nextCursor": "2026-01-01T00:00:00|1"
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      COMMON-E-001 <br/>
      COMMON-E-003 <br/>
      COMMON-E-004 <br/>
      COMMON-E-002 <br/>
      AUTH-E-010 <br/>
    </td>
  </tr>
  <tr>
    <td>

게시글 검색 API

</td>
<td>

```json
GET /api/search/posts?query=코디&size=20&after=2026-01-01T00:00:00|1
Authorization: Bearer {AT}
```

</td>
<td>

```json
{
  "success": true,
  "code": "COMMON-S-001",
  "message": "검색을 완료했습니다.",
  "data": {
    "posts": [
      {
        "id": 1,
        "imageObjectKey": "post/1/1-uuid.png",
        "createdAt": "2026-01-01T00:00:00"
      }
    ],
    "nextCursor": null
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      COMMON-E-001 <br/>
      COMMON-E-003 <br/>
      COMMON-E-004 <br/>
      COMMON-E-002 <br/>
      AUTH-E-010 <br/>
    </td>
  </tr>
</table>

## 업로드

<table>
  <tr>
    <td align="center">설명</td>
    <td align="center">요청</td>
    <td align="center">응답</td>
    <td align="center">오류</td>
  </tr>
  <tr>
    <td>

Presign 발급 API

</td>
<td>

```json
POST /api/uploads/presign
Authorization: Bearer {AT}

{
  "category": "POST",
  "extensions": [
    "jpg",
    "png"
  ]
}
```

</td>
<td>

```json
{
  "success": true,
  "code": "COMMON-S-002",
  "message": "업로드 URL 발급을 완료했습니다.",
  "data": {
    "files": [
      {
        "uploadUrl": "https://s3.amazonaws.com/...",
        "imageObjectKey": "post/1/1-uuid.jpg"
      }
    ]
  },
  "timestamp": "2026-01-01T00:00:00Z"
}
```

</td>
<td>
      AUTH-E-010 <br/>
      COMMON-E-007 <br/>
    </td>
  </tr>
</table>

## 개발 전용

<table>
  <tr>
    <td align="center">설명</td>
    <td align="center">요청</td>
    <td align="center">응답</td>
    <td align="center">오류</td>
  </tr>
  <tr>
    <td>

개발용 회원 하드 삭제 API

</td>
<td>

```json
DELETE /api/dev/members/1
```

</td>
<td>

```json
204 No Content
```

</td>
<td>
      MEMBER-E-050 <br/>
    </td>
  </tr>
</table>

## 문서/정적 경로

<table>
  <tr>
    <td align="center">설명</td>
    <td align="center">요청</td>
    <td align="center">응답</td>
    <td align="center">오류</td>
  </tr>
  <tr>
    <td>

Swagger UI

</td>
<td>

```json
GET /api/swagger-ui/index.html
```

</td>
<td>

```json
Swagger UI HTML
```

</td>
<td>
      COMMON-E-005 <br/>
    </td>
  </tr>
  <tr>
    <td>

OpenAPI Docs

</td>
<td>

```json
GET /api/v3/api-docs
```

</td>
<td>

```json
{
  "openapi": "3.0.1",
  "info": {
    "title": "fit-check",
    "version": "v1"
  }
}
```

</td>
<td>
      COMMON-E-005 <br/>
    </td>
  </tr>
  <tr>
    <td>

Bookmarks

</td>
<td>

```json
GET /bookmarks.html
```

</td>
<td>

```json
HTML
```

</td>
<td>
      COMMON-E-005 <br/>
    </td>
  </tr>
  <tr>
    <td>

JaCoCo

</td>
<td>

```json
GET /jacoco.html
```

</td>
<td>

```json
HTML
```

</td>
<td>
      COMMON-E-005 <br/>
    </td>
  </tr>
  <tr>
    <td>

OAuth2 문서용 카카오 로그인

</td>
<td>

```json
GET /oauth2/authorization/kakao
```

</td>
<td>

```json
501 Swagger 문서용 엔드포인트입니다. 브라우저에서 호출해주세요.
```

</td>
<td>
      COMMON-E-999 <br/>
    </td>
  </tr>
</table>

## Actuator

<table>
  <tr>
    <td align="center">설명</td>
    <td align="center">요청</td>
    <td align="center">응답</td>
    <td align="center">오류</td>
  </tr>
  <tr>
    <td>

Health

</td>
<td>

```json
GET /api/actuator/health
```

</td>
<td>

```json
{
  "status": "UP"
}
```

</td>
<td>
      COMMON-E-999 <br/>
    </td>
  </tr>
  <tr>
    <td>

Metrics

</td>
<td>

```json
GET /api/actuator/metrics
```

</td>
<td>

```json
{
  "names": [
    "jvm.memory.used",
    "http.server.requests"
  ]
}
```

</td>
<td>
      AUTH-E-010 <br/>
      AUTH-E-014 <br/>
    </td>
  </tr>
  <tr>
    <td>

Prometheus

</td>
<td>

```json
GET /api/actuator/prometheus
```

</td>
<td>

```json
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
```

</td>
<td>
      AUTH-E-010 <br/>
      AUTH-E-014 <br/>
    </td>
  </tr>
</table>
