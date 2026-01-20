package katopia.fitcheck.global;

import katopia.fitcheck.global.exception.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Getter
@AllArgsConstructor
public class APIResponse<T> {
    private String message;
    private String code;
    private T Data;

    public static <T> ResponseEntity<APIResponse<T>> ok(ResponseCode code, T data) {
        return ResponseEntity
                .status(code.getStatus())
                .body(new APIResponse<>(code.getMessage(), code.getCode(), data));
    }

    public static <T> ResponseEntity<APIResponse<T>> ok(ResponseCode code) {
        return ok(code, null);
    }

    public static ResponseEntity<APIResponse<?>> error(ResponseCode code) {
        return ResponseEntity
                .status(code.getStatus())
                .body(new APIResponse<>(code.getMessage(), code.getCode(), Map.of()));
    }

    public static ResponseEntity<APIResponse<?>> error(String message) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new APIResponse<>(message, HttpStatus.BAD_REQUEST.getReasonPhrase(), Map.of()));
    }

    public static ResponseEntity<APIResponse<?>> error(HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(new APIResponse<>(status.getReasonPhrase(), status.getReasonPhrase(), Map.of()));
    }
}
