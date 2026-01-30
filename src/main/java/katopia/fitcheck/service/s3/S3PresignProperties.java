package katopia.fitcheck.service.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud.aws")
public record S3PresignProperties(
        String region,
        S3 s3,
        Credentials credentials,
        Presign presign,
        String cloudfrontBaseUrl,
        Long maxSizeBytes,
        String sessionToken
) {

    public record S3(
            String bucket
    ) {
    }

    public record Credentials(
            String accessKeyId,
            String secretAccessKey
    ) {
    }

    public record Presign(
            Integer expireMinutes,
            Integer expireSeconds
    ) {
    }
}
