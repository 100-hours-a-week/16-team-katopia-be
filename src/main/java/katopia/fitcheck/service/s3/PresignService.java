package katopia.fitcheck.service.s3;

import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.dto.s3.PresignRequest;
import katopia.fitcheck.dto.s3.PresignResponse;
import katopia.fitcheck.global.policy.Policy;
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
    private final S3Presigner s3Presigner;
    private final S3PresignProperties props;

    @Transactional(readOnly = true)
    public PresignResponse createPresignedUrls(Long memberId, PresignRequest request) {
        validateConfig();

        List<PresignResponse.PresignUrl> files = request.extensions().stream()
                .map(this::normalizeExtension)
                .map(ext -> buildPresign(memberId, request.category(), ext))
                .collect(Collectors.toList());

        return PresignResponse.of(files);
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
                objectKey
        );
    }

    private String buildObjectKey(Long memberId, UploadCategory category, String extension) {
        String uuid = UUID.randomUUID().toString();
        long epoch = Instant.now().toEpochMilli();
        return String.format("%s/%d/%d-%s.%s", category.getFolder(), memberId, epoch, uuid, extension);
    }

    private void validateConfig() {
        if (props.s3() == null || !StringUtils.hasText(props.s3().bucket())) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        long maxSize = props.maxSizeBytes() != null ? props.maxSizeBytes() : Policy.PRESIGN_MAX_SIZE_BYTES;
        if (maxSize > Policy.PRESIGN_MAX_SIZE_BYTES) {
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
