package katopia.fitcheck.service.s3;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@EnableConfigurationProperties(S3PresignProperties.class)
public class S3PresignConfig {

    @Bean
    public S3Presigner s3Presigner(S3PresignProperties props) {
        AwsCredentialsProvider credentialsProvider = resolveCredentials(props);
        return S3Presigner.builder()
                .region(Region.of(props.region()))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    AwsCredentialsProvider resolveCredentials(S3PresignProperties props) {
        if (props.credentials() == null
                || props.credentials().accessKeyId() == null
                || props.credentials().secretAccessKey() == null) {
            return DefaultCredentialsProvider.create();
        }
        if (props.sessionToken() != null && !props.sessionToken().isBlank()) {
            return StaticCredentialsProvider.create(
                    AwsSessionCredentials.create(
                            props.credentials().accessKeyId().trim(),
                            props.credentials().secretAccessKey().trim(),
                            props.sessionToken().trim()
                    )
            );
        }
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                        props.credentials().accessKeyId().trim(),
                        props.credentials().secretAccessKey().trim()
                )
        );
    }
}
