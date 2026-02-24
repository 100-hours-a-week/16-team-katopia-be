# Presign

## PresignService(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-PRESIGN-S-01 | 확장자 정규화 | extension=".PNG" | createPresignedUrls | contentType=image/png |
| TC-PRESIGN-S-02 | 업로드 URL/오브젝트 키 생성 | 유효 설정 | createPresignedUrls | uploadUrl/imageObjectKey 반환 |
| TC-PRESIGN-S-03 | contentType 매핑(png) | extension=".PNG" | createPresignedUrls | contentType=image/png |
| TC-PRESIGN-S-04 | 만료 분 설정 사용 | presign.minutes 설정 | createPresignedUrls | signatureDuration=분 기준 |
| TC-PRESIGN-S-05 | 만료 기본값 사용 | presign null | createPresignedUrls | signatureDuration=기본값 |
| TC-PRESIGN-S-06 | contentType 기본값 적용 | 확장자 미지원 | createPresignedUrls | application/octet-stream |
| TC-PRESIGN-S-07 | contentType jpeg 매핑 | jpg/jpeg | createPresignedUrls | image/jpeg |
| TC-PRESIGN-S-08 | contentType webp 매핑 | webp | createPresignedUrls | image/webp |
| TC-PRESIGN-S-09 | contentType heic 매핑 | heic | createPresignedUrls | image/heic |
| TC-PRESIGN-F-01 | 확장자 누락 실패 | extension=null | createPresignedUrls | COMMON-E-007 반환 |
| TC-PRESIGN-F-02 | 버킷 누락 실패 | bucket=null | createPresignedUrls | COMMON-E-007 반환 |
| TC-PRESIGN-F-03 | maxSize 초과 실패 | maxSizeBytes>30MB | createPresignedUrls | COMMON-E-007 반환 |

## Presign 요청 유효성(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-PRESIGN-VAL-S-01 | 확장자 정규화 허용 | extensions=[".PNG"] | validate | 오류 없음 |
| TC-PRESIGN-VAL-S-02 | 최대 개수 허용 | category=POST, size=max | validate | 오류 없음 |
| TC-PRESIGN-VAL-F-01 | 요청 null | request=null | validate | COMMON-E-007 반환 |
| TC-PRESIGN-VAL-F-02 | category 누락 | category=null | validate | COMMON-E-007 반환 |
| TC-PRESIGN-VAL-F-03 | extensions 누락 | extensions=null | validate | COMMON-E-007 반환 |
| TC-PRESIGN-VAL-F-04 | extensions 빈 리스트 | extensions=[] | validate | COMMON-E-007 반환 |
| TC-PRESIGN-VAL-F-05 | 개수 초과 | size>maxCount | validate | COMMON-E-007 반환 |
| TC-PRESIGN-VAL-F-06 | 허용되지 않는 확장자 | extensions=["exe"] | validate | COMMON-E-007 반환 |

## S3PresignConfig(Unit)
| TC ID | 설명 | GIVEN | WHEN | THEN |
| --- | --- | --- | --- | --- |
| TC-PRESIGN-CONFIG-S-01 | credentials 기본 키 사용 | accessKey/secretKey 존재 | resolveCredentials | StaticCredentialsProvider 반환 |
| TC-PRESIGN-CONFIG-S-02 | session token 포함 | sessionToken 존재 | resolveCredentials | SessionCredentials 반환 |
| TC-PRESIGN-CONFIG-S-03 | session token 미사용 | sessionToken null | resolveCredentials | BasicCredentials 반환 |
