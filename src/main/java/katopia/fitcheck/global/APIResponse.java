package katopia.fitcheck.global;

import katopia.fitcheck.global.exception.code.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import katopia.fitcheck.global.docs.Docs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;

@Getter
@AllArgsConstructor
public class APIResponse<T> {
    @Schema(description = "성공 여부", example = "true")
    private final boolean success;
    @Schema(description = "응답 메시지", example = "요청이 성공했습니다.")
    private String message;
    @Schema(description = "응답 코드", example = "COMMON-S-000")
    private String code;
    @Schema(description = "응답 데이터", example = "{...}")
    private T data;
    @Schema(description = "응답 시각(UTC)", example = Docs.TIMESTAMP)
    private Instant timestamp;

    private static Instant now() {
        return Instant.now(Clock.systemUTC());
    }

    private static boolean successStatus(HttpStatus status) {
        return status.is2xxSuccessful();
    }

    private static <T> APIResponse<T> body(ResponseCode code, T data) {
        return new APIResponse<>(
                successStatus(code.getStatus()),
                code.getMessage(),
                code.getCode(),
                data,
                now()
        );
    }

    public static <T> ResponseEntity<APIResponse<T>> ok(ResponseCode code, T data) {
        return ResponseEntity
                .status(code.getStatus())
                .body(body(code, data));
    }

    public static <T> ResponseEntity<APIResponse<T>> ok(ResponseCode code) {
        return ok(code, null);
    }

    public static ResponseEntity<Void> noContent(ResponseCode code) {
        return ResponseEntity
                .status(code.getStatus())
                .build();
    }

    public static ResponseEntity<APIResponse<?>> error(ResponseCode code) {
        return ResponseEntity
                .status(code.getStatus())
                .body(new APIResponse<>(false, code.getMessage(), code.getCode(), Map.of(), now()));
    }

    public static ResponseEntity<APIResponse<?>> error(ResponseCode code, String message) {
        return ResponseEntity
                .status(code.getStatus())
                .body(new APIResponse<>(false, message, code.getCode(), Map.of(), now()));
    }

    public static ResponseEntity<APIResponse<?>> error(String message) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(status)
                .body(new APIResponse<>(false, message, status.getReasonPhrase(), Map.of(), now()));
    }

    public static ResponseEntity<APIResponse<?>> error(HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(new APIResponse<>(false, status.getReasonPhrase(), status.getReasonPhrase(), Map.of(), now()));
    }
}
