package katopia.fitcheck.s3;

import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.s3.dto.PresignRequest;
import katopia.fitcheck.s3.dto.PresignResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PresignService {

    private static final long MAX_SIZE_BYTES = 30L * 1024 * 1024;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "heic", "webp");

    private final S3Presigner s3Presigner;
    private final S3PresignProperties props;

    @Transactional(readOnly = true)
    public PresignResponse createPresignedUrls(Long memberId, PresignRequest request) {
        validateRequest(request);

        List<PresignResponse.PresignUrl> files = request.extensions().stream()
                .map(ext -> normalizeExtension(ext))
                .map(ext -> buildPresign(memberId, request.category(), ext))
                .collect(Collectors.toList());

        return new PresignResponse(files);
    }

    private PresignResponse.PresignUrl buildPresign(Long memberId, UploadCategory category, String extension) {
        String objectKey = buildObjectKey(memberId, category, extension);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(props.s3().bucket())
                .key(objectKey)
                .contentType(resolveContentType(extension))
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(resolveExpirySeconds()))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
        return new PresignResponse.PresignUrl(
                presigned.url().toString(),
                buildAccessUrl(objectKey)
        );
    }

    private String buildObjectKey(Long memberId, UploadCategory category, String extension) {
        String uuid = UUID.randomUUID().toString();
        long epoch = Instant.now().toEpochMilli();
        return String.format("%s/%d/%d-%s.%s", category.getFolder(), memberId, epoch, uuid, extension);
    }

    private String buildAccessUrl(String objectKey) {
        if (!StringUtils.hasText(props.cloudfrontBaseUrl())) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        return String.format("%s/%s", trimTrailingSlash(props.cloudfrontBaseUrl()), objectKey);
    }

    private String trimTrailingSlash(String base) {
        if (base.endsWith("/")) {
            return base.substring(0, base.length() - 1);
        }
        return base;
    }

    private void validateRequest(PresignRequest request) {
        if (request == null || request.category() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        if (request.extensions() == null || request.extensions().isEmpty()) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        if (props.s3() == null || !StringUtils.hasText(props.s3().bucket())) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        if (request.extensions().size() > request.category().getMaxCount()) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        for (String ext : request.extensions()) {
            String normalized = normalizeExtension(ext);
            if (!ALLOWED_EXTENSIONS.contains(normalized)) {
                throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
            }
        }
        long maxSize = props.maxSizeBytes() != null ? props.maxSizeBytes() : MAX_SIZE_BYTES;
        if (maxSize > MAX_SIZE_BYTES) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private String normalizeExtension(String extension) {
        if (!StringUtils.hasText(extension)) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        String trimmed = extension.trim().toLowerCase(Locale.ROOT);
        if (trimmed.startsWith(".")) {
            trimmed = trimmed.substring(1);
        }
        return trimmed;
    }

    private String resolveContentType(String extension) {
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            case "heic" -> "image/heic";
            default -> "application/octet-stream";
        };
    }

    private long resolveExpirySeconds() {
        if (props.presign() != null) {
            Integer seconds = props.presign().expireSeconds();
            if (seconds != null && seconds > 0) {
                return seconds;
            }
            Integer minutes = props.presign().expireMinutes();
            if (minutes != null && minutes > 0) {
                return minutes * 60L;
            }
        }
        return 600;
    }
}
