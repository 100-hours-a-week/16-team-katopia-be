package katopia.fitcheck.global.exception.code;

import org.springframework.http.HttpStatus;

public interface ResponseCode {
    HttpStatus getStatus();
    String getMessage();
    String getCode();
}
