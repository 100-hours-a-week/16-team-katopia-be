package katopia.fitcheck.service.s3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import static org.assertj.core.api.Assertions.assertThat;

class S3PresignConfigTest {

    private final S3PresignConfig config = new S3PresignConfig();

    @Test
    @DisplayName("TC-PRESIGN-CONFIG-S-01 credentials 누락 시 기본 Provider 사용")
    void tcPresignConfigS01_missingCredentials_usesDefaultProvider() {
        S3PresignProperties props = new S3PresignProperties(
                "ap-northeast-2",
                new S3PresignProperties.S3("bucket"),
                null,
                null,
                null,
                null,
                null
        );

        AwsCredentialsProvider provider = config.resolveCredentials(props);

        assertThat(provider).isInstanceOf(DefaultCredentialsProvider.class);
    }

    @Test
    @DisplayName("TC-PRESIGN-CONFIG-S-02 sessionToken 존재 시 SessionCredentials 사용")
    void tcPresignConfigS02_sessionToken_usesSessionCredentials() {
        S3PresignProperties props = new S3PresignProperties(
                "ap-northeast-2",
                new S3PresignProperties.S3("bucket"),
                new S3PresignProperties.Credentials("ak", "sk"),
                null,
                null,
                null,
                "st"
        );

        AwsCredentialsProvider provider = config.resolveCredentials(props);

        assertThat(provider).isInstanceOf(StaticCredentialsProvider.class);
        AwsCredentials credentials = provider.resolveCredentials();
        assertThat(credentials).isInstanceOf(AwsSessionCredentials.class);
        AwsSessionCredentials session = (AwsSessionCredentials) credentials;
        assertThat(session.accessKeyId()).isEqualTo("ak");
        assertThat(session.secretAccessKey()).isEqualTo("sk");
        assertThat(session.sessionToken()).isEqualTo("st");
    }

    @Test
    @DisplayName("TC-PRESIGN-CONFIG-S-03 기본 자격증명 사용")
    void tcPresignConfigS03_basicCredentials() {
        S3PresignProperties props = new S3PresignProperties(
                "ap-northeast-2",
                new S3PresignProperties.S3("bucket"),
                new S3PresignProperties.Credentials("ak", "sk"),
                null,
                null,
                null,
                null
        );

        AwsCredentialsProvider provider = config.resolveCredentials(props);

        assertThat(provider).isInstanceOf(StaticCredentialsProvider.class);
        AwsCredentials credentials = provider.resolveCredentials();
        assertThat(credentials).isInstanceOf(AwsBasicCredentials.class);
        AwsBasicCredentials basic = (AwsBasicCredentials) credentials;
        assertThat(basic.accessKeyId()).isEqualTo("ak");
        assertThat(basic.secretAccessKey()).isEqualTo("sk");
    }
}
