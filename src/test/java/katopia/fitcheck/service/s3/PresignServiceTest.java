package katopia.fitcheck.service.s3;

import katopia.fitcheck.dto.s3.PresignRequest;
import katopia.fitcheck.dto.s3.PresignResponse;
import katopia.fitcheck.global.exception.BusinessException;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PresignServiceTest {

    private S3Presigner presigner;
    private S3PresignProperties props;
    private PresignService presignService;

    @BeforeEach
    void setUp() throws Exception {
        presigner = mock(S3Presigner.class);
        props = new S3PresignProperties(
                "ap-northeast-2",
                new S3PresignProperties.S3("bucket"),
                new S3PresignProperties.Credentials("ak", "sk"),
                new S3PresignProperties.Presign(null, 600),
                "https://cdn.example.com",
                30L * 1024 * 1024,
                null
        );
        presignService = new PresignService(presigner, props);

        PresignedPutObjectRequest mocked = mock(PresignedPutObjectRequest.class);
        URL uploadUrl = URI.create("https://s3.example.com/upload").toURL();
        when(mocked.url()).thenReturn(uploadUrl);
        when(presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(mocked);
    }

    @Test
    @DisplayName("TC-PRESIGN-S-01 확장자 정규화")
    void tcPresignS01_normalizesExtension() {
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of(".PNG"));

        PresignResponse response = presignService.createPresignedUrls(1L, request);

        assertThat(response.files()).hasSize(1);
        assertThat(response.files().getFirst().uploadUrl()).contains("https://s3.example.com/upload");
    }

    @Test
    @DisplayName("TC-PRESIGN-S-02 업로드 URL/오브젝트 키 생성")
    void tcPresignS02_buildsUrls() {
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of("png"));

        PresignResponse response = presignService.createPresignedUrls(1L, request);

        assertThat(response.files()).hasSize(1);
        assertThat(response.files().getFirst().uploadUrl()).isEqualTo("https://s3.example.com/upload");
        assertThat(response.files().getFirst().imageObjectKey()).startsWith("profiles/1/");
        assertThat(response.files().getFirst().imageObjectKey()).endsWith(".png");
    }

    @Test
    @DisplayName("TC-PRESIGN-S-03 contentType 매핑")
    void tcPresignS03_contentTypeMapping() {
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of(".PNG"));

        presignService.createPresignedUrls(1L, request);

        ArgumentCaptor<PutObjectPresignRequest> captor = ArgumentCaptor.forClass(PutObjectPresignRequest.class);
        verify(presigner).presignPutObject(captor.capture());
        PutObjectRequest put = captor.getValue().putObjectRequest();
        assertThat(put.contentType()).isEqualTo("image/png");
    }

    @Test
    @DisplayName("TC-PRESIGN-S-04 만료 분 설정 사용")
    void tcPresignS04_expireMinutes_usedWhenSecondsMissing() {
        props = new S3PresignProperties(
                "ap-northeast-2",
                new S3PresignProperties.S3("bucket"),
                new S3PresignProperties.Credentials("ak", "sk"),
                new S3PresignProperties.Presign(5, null),
                "https://cdn.example.com",
                30L * 1024 * 1024,
                null
        );
        presignService = new PresignService(presigner, props);

        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of("png"));

        presignService.createPresignedUrls(1L, request);

        ArgumentCaptor<PutObjectPresignRequest> captor = ArgumentCaptor.forClass(PutObjectPresignRequest.class);
        verify(presigner).presignPutObject(captor.capture());
        assertThat(captor.getValue().signatureDuration()).isEqualTo(java.time.Duration.ofSeconds(300));
    }

    @Test
    @DisplayName("TC-PRESIGN-S-05 만료 기본값 사용")
    void tcPresignS05_defaultExpireSeconds() {
        props = new S3PresignProperties(
                "ap-northeast-2",
                new S3PresignProperties.S3("bucket"),
                new S3PresignProperties.Credentials("ak", "sk"),
                null,
                "https://cdn.example.com",
                30L * 1024 * 1024,
                null
        );
        presignService = new PresignService(presigner, props);

        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of("png"));

        presignService.createPresignedUrls(1L, request);

        ArgumentCaptor<PutObjectPresignRequest> captor = ArgumentCaptor.forClass(PutObjectPresignRequest.class);
        verify(presigner).presignPutObject(captor.capture());
        assertThat(captor.getValue().signatureDuration()).isEqualTo(java.time.Duration.ofSeconds(600));
    }

    @Test
    @DisplayName("TC-PRESIGN-S-06 contentType 기본값 적용")
    void tcPresignS06_contentTypeDefault() {
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of("bmp"));

        presignService.createPresignedUrls(1L, request);

        ArgumentCaptor<PutObjectPresignRequest> captor = ArgumentCaptor.forClass(PutObjectPresignRequest.class);
        verify(presigner).presignPutObject(captor.capture());
        PutObjectRequest put = captor.getValue().putObjectRequest();
        assertThat(put.contentType()).isEqualTo("application/octet-stream");
    }

    @Test
    @DisplayName("TC-PRESIGN-S-07 contentType jpeg 매핑")
    void tcPresignS07_contentTypeJpeg() {
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of("jpg"));

        presignService.createPresignedUrls(1L, request);

        ArgumentCaptor<PutObjectPresignRequest> captor = ArgumentCaptor.forClass(PutObjectPresignRequest.class);
        verify(presigner).presignPutObject(captor.capture());
        PutObjectRequest put = captor.getValue().putObjectRequest();
        assertThat(put.contentType()).isEqualTo("image/jpeg");
    }

    @Test
    @DisplayName("TC-PRESIGN-S-08 contentType webp 매핑")
    void tcPresignS08_contentTypeWebp() {
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of("webp"));

        presignService.createPresignedUrls(1L, request);

        ArgumentCaptor<PutObjectPresignRequest> captor = ArgumentCaptor.forClass(PutObjectPresignRequest.class);
        verify(presigner).presignPutObject(captor.capture());
        PutObjectRequest put = captor.getValue().putObjectRequest();
        assertThat(put.contentType()).isEqualTo("image/webp");
    }

    @Test
    @DisplayName("TC-PRESIGN-S-09 contentType heic 매핑")
    void tcPresignS09_contentTypeHeic() {
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of("heic"));

        presignService.createPresignedUrls(1L, request);

        ArgumentCaptor<PutObjectPresignRequest> captor = ArgumentCaptor.forClass(PutObjectPresignRequest.class);
        verify(presigner).presignPutObject(captor.capture());
        PutObjectRequest put = captor.getValue().putObjectRequest();
        assertThat(put.contentType()).isEqualTo("image/heic");
    }

    @Test
    @DisplayName("TC-PRESIGN-F-01 확장자 누락 실패")
    void tcPresignF01_missingExtension_throws() {
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, java.util.Arrays.asList((String) null));

        assertThatThrownBy(() -> presignService.createPresignedUrls(1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(CommonErrorCode.INVALID_INPUT_VALUE);
    }

    @Test
    @DisplayName("TC-PRESIGN-F-02 버킷 누락 실패")
    void tcPresignF02_missingBucket_throws() {
        props = new S3PresignProperties(
                "ap-northeast-2",
                new S3PresignProperties.S3(null),
                new S3PresignProperties.Credentials("ak", "sk"),
                new S3PresignProperties.Presign(null, 600),
                "https://cdn.example.com",
                30L * 1024 * 1024,
                null
        );
        presignService = new PresignService(presigner, props);
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of("png"));

        assertThatThrownBy(() -> presignService.createPresignedUrls(1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(CommonErrorCode.INVALID_INPUT_VALUE);
    }

    @Test
    @DisplayName("TC-PRESIGN-F-03 maxSize 초과 실패")
    void tcPresignF03_exceedMaxSize_throws() {
        props = new S3PresignProperties(
                "ap-northeast-2",
                new S3PresignProperties.S3("bucket"),
                new S3PresignProperties.Credentials("ak", "sk"),
                new S3PresignProperties.Presign(null, 600),
                "https://cdn.example.com",
                31L * 1024 * 1024,
                null
        );
        presignService = new PresignService(presigner, props);
        PresignRequest request = new PresignRequest(UploadCategory.PROFILE, List.of("png"));

        assertThatThrownBy(() -> presignService.createPresignedUrls(1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(CommonErrorCode.INVALID_INPUT_VALUE);
    }
}
